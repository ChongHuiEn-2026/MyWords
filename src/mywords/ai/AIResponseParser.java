package mywords.ai;

import mywords.exception.AIServiceException;
import mywords.logic.CommunicationRules;

public class AIResponseParser
{
    public String extractContent(String providerJson) throws AIServiceException
    {
        // Follow the documented path: choices[0].message.content.
        Cursor choices = new Cursor(new Cursor(providerJson).member("choices"));
        choices.expect('[');
        String firstChoice = choices.readValue();
        choices.expect(']');
        choices.requireEnd();

        String finishReason = new Cursor(firstChoice).member("finish_reason");
        if (finishReason != null && !"\"stop\"".equals(finishReason))
        {
            throw badResponse();
        }

        String message = new Cursor(firstChoice).member("message");
        String refusal = new Cursor(message).member("refusal");
        if (refusal != null && !"null".equals(refusal))
        {
            throw badResponse();
        }

        Cursor content = new Cursor(new Cursor(message).member("content"));
        String text = content.readString();
        content.requireEnd();
        return text;
    }

    public String[] parseCandidates(String content) throws AIServiceException
    {
        Cursor array = new Cursor(content);
        array.expect('[');
        String[] candidates = new String[3];
        for (int i = 0; i < candidates.length; i++)
        {
            if (i > 0)
            {
                array.expect(',');
            }
            String rawText = array.readString();
            String text = rawText.strip();
            if (!CommunicationRules.isValidSentence(text) || !isSingleLine(rawText))
            {
                throw badResponse();
            }
            for (int j = 0; j < i; j++)
            {
                if (text.equalsIgnoreCase(candidates[j]))
                {
                    throw badResponse();
                }
            }
            candidates[i] = text;
        }
        array.expect(']');
        array.requireEnd();
        return candidates;
    }

    private static boolean isSingleLine(String text)
    {
        for (int i = 0; i < text.length(); i++)
        {
            char ch = text.charAt(i);
            if ((Character.isISOControl(ch) && ch != '\t') || ch == '\u2028' || ch == '\u2029')
            {
                return false;
            }
        }
        return true;
    }

    static String quote(String text)
    {
        if (text == null)
        {
            return "null";
        }
        StringBuilder json = new StringBuilder("\"");
        for (int i = 0; i < text.length(); i++)
        {
            char ch = text.charAt(i);
            switch (ch)
            {
                case '"' -> json.append("\\\"");
                case '\\' -> json.append("\\\\");
                case '\b' -> json.append("\\b");
                case '\f' -> json.append("\\f");
                case '\n' -> json.append("\\n");
                case '\r' -> json.append("\\r");
                case '\t' -> json.append("\\t");
                default ->
                {
                    if (ch < ' ')
                    {
                        json.append("\\u00");
                        json.append(Character.forDigit(ch >> 4, 16));
                        json.append(Character.forDigit(ch & 15, 16));
                    }
                    else
                    {
                        json.append(ch);
                    }
                }
            }
        }
        return json.append('"').toString();
    }

    private static AIServiceException badResponse()
    {
        return new AIServiceException("The AI reply did not contain three usable suggestions.");
    }

    private static class Cursor
    {
        private static final int MAX_DEPTH = 32;
        private final String json;
        private int index;

        Cursor(String json) throws AIServiceException
        {
            if (json == null)
            {
                throw badResponse();
            }
            this.json = json;
        }

        // Read one whole object, remembering only the requested field.
        String member(String name) throws AIServiceException
        {
            expect('{');
            String result = null;
            if (!take('}'))
            {
                do
                {
                    String key = readString();
                    expect(':');
                    if (name.equals(key))
                    {
                        if (result != null)
                        {
                            throw badResponse();
                        }
                        result = readValue();
                    }
                    else
                    {
                        skipValue(0);
                    }
                }
                while (take(','));
                expect('}');
            }
            requireEnd();
            return result;
        }

        String readValue() throws AIServiceException
        {
            skipSpace();
            int start = index;
            skipValue(0);
            return json.substring(start, index);
        }

        private void skipValue(int depth) throws AIServiceException
        {
            skipSpace();
            if (index == json.length() || depth >= MAX_DEPTH)
            {
                throw badResponse();
            }
            switch (json.charAt(index))
            {
                case '"' -> readString();
                case '{' -> skipGroup('{', '}', depth);
                case '[' -> skipGroup('[', ']', depth);
                default -> skipLiteral();
            }
        }

        private void skipGroup(char open, char close, int depth) throws AIServiceException
        {
            expect(open);
            if (take(close))
            {
                return;
            }
            do
            {
                if (open == '{')
                {
                    readString();
                    expect(':');
                }
                skipValue(depth + 1);
            }
            while (take(','));
            expect(close);
        }

        private void skipLiteral() throws AIServiceException
        {
            int start = index;
            while (index < json.length() && " \t\r\n,]}".indexOf(json.charAt(index)) < 0)
            {
                index++;
            }
            String token = json.substring(start, index);
            if (!"null".equals(token) && !"true".equals(token) && !"false".equals(token)
                    && !token.matches("-?(0|[1-9][0-9]*)(\\.[0-9]+)?([eE][+-]?[0-9]+)?"))
            {
                throw badResponse();
            }
        }

        String readString() throws AIServiceException
        {
            expect('"');
            StringBuilder text = new StringBuilder();
            while (index < json.length())
            {
                char ch = json.charAt(index++);
                if (ch == '"')
                {
                    if (text.length() > 0 && Character.isHighSurrogate(text.charAt(text.length() - 1)))
                    {
                        throw badResponse();
                    }
                    return text.toString();
                }
                if (ch < ' ')
                {
                    throw badResponse(); // JSON control characters must be escaped.
                }
                if (ch == '\\')
                {
                    ch = readEscape();
                }
                // Java represents characters like emoji as two matching UTF-16 units.
                if (Character.isLowSurrogate(ch))
                {
                    if (text.length() == 0 || !Character.isHighSurrogate(text.charAt(text.length() - 1)))
                    {
                        throw badResponse();
                    }
                }
                else if (text.length() > 0 && Character.isHighSurrogate(text.charAt(text.length() - 1)))
                {
                    throw badResponse();
                }
                text.append(ch);
            }
            throw badResponse();
        }

        private char readEscape() throws AIServiceException
        {
            if (index == json.length())
            {
                throw badResponse();
            }
            return switch (json.charAt(index++))
            {
                case '"' -> '"';
                case '\\' -> '\\';
                case '/' -> '/';
                case 'b' -> '\b';
                case 'f' -> '\f';
                case 'n' -> '\n';
                case 'r' -> '\r';
                case 't' -> '\t';
                case 'u' -> readUnicode();
                default -> throw badResponse();
            };
        }

        private char readUnicode() throws AIServiceException
        {
            if (json.length() - index < 4)
            {
                throw badResponse();
            }
            int code = 0;
            for (int i = 0; i < 4; i++)
            {
                char ch = json.charAt(index++);
                int digit = "0123456789abcdef".indexOf(Character.toLowerCase(ch));
                if (digit < 0 || ch > 127)
                {
                    throw badResponse();
                }
                code = code * 16 + digit;
            }
            return (char) code;
        }

        private void skipSpace()
        {
            while (index < json.length() && " \t\r\n".indexOf(json.charAt(index)) >= 0)
            {
                index++;
            }
        }

        boolean take(char expected)
        {
            skipSpace();
            if (index < json.length() && json.charAt(index) == expected)
            {
                index++;
                return true;
            }
            return false;
        }

        void expect(char expected) throws AIServiceException
        {
            if (!take(expected))
            {
                throw badResponse();
            }
        }

        void requireEnd() throws AIServiceException
        {
            skipSpace();
            if (index != json.length())
            {
                throw badResponse();
            }
        }
    }
}
