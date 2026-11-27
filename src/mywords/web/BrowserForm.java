package mywords.web;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import mywords.exception.InvalidInputException;

/** Bounded form fields avoid a JSON dependency at the browser boundary. */
final class BrowserForm {
    private final String[][] fields;

    BrowserForm(String body) throws InvalidInputException {
        String[] pairs = body.isEmpty() ? new String[0] : body.split("&", -1);
        if (pairs.length > 20) throw new InvalidInputException("Too many form fields.");
        fields = new String[pairs.length][2];
        for (int i = 0; i < pairs.length; i++) {
            int equals = pairs[i].indexOf('=');
            if (equals < 1) throw new InvalidInputException("Invalid form data.");
            fields[i][0] = URLDecoder.decode(pairs[i].substring(0, equals), StandardCharsets.UTF_8);
            fields[i][1] = URLDecoder.decode(pairs[i].substring(equals + 1), StandardCharsets.UTF_8);
            for (int j = 0; j < i; j++) {
                if (fields[j][0].equals(fields[i][0])) throw new InvalidInputException("Duplicate form field.");
            }
        }
    }

    String get(String name) {
        for (String[] field : fields) {
            if (field[0].equals(name)) return field[1];
        }
        return "";
    }

    boolean checked(String name) {
        return "true".equals(get(name));
    }

    String text(String name, int limit, boolean required) throws InvalidInputException {
        String value = get(name).strip();
        if ((required && value.isEmpty()) || value.length() > limit
                || value.codePoints().anyMatch(Character::isISOControl)) {
            throw new InvalidInputException("Check " + name + " (" + limit + " characters maximum).");
        }
        return value;
    }

    static String quote(String text) {
        StringBuilder value = new StringBuilder("\"");
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            switch (ch) {
                case '"' -> value.append("\\\"");
                case '\\' -> value.append("\\\\");
                case '\n' -> value.append("\\n");
                case '\r' -> value.append("\\r");
                case '\t' -> value.append("\\t");
                default -> {
                    if (ch < 32) value.append(String.format("\\u%04x", (int) ch));
                    else value.append(ch);
                }
            }
        }
        return value.append('"').toString();
    }
}
