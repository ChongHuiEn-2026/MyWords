package mywords.logic;

import java.util.Locale;
import mywords.model.Feedback;
import mywords.model.Phrase;

/** Searches consented phrase and feedback arrays for relevant AI examples. */
public class MemoryManager
{
    public static final int MAX_PHRASES = 3;
    public static final int MAX_FEEDBACK = 2;

    private final CommunicationRules rules = new CommunicationRules();

    public Phrase[] selectPhrases(String keywords, String contextCode,
                                  Phrase[] candidates, int limit)
    {
        if (!CommunicationRules.isValidKeywords(keywords)
                || candidates == null || limit <= 0)
        {
            return new Phrase[0];
        }

        int capacity = Math.min(limit, MAX_PHRASES);
        Phrase[] ranked = new Phrase[capacity];
        int[] scores = new int[capacity];
        long[] times = new long[capacity];
        int count = 0;

        for (int i = 0; i < candidates.length; i++)
        {
            Phrase item = candidates[i];
            if (item == null
                    || !rules.isEligibleForReuse(contextCode,
                            item.getContextCode(), item.isAllowAIReuse())
                    || !CommunicationRules.isValidSentence(item.getConfirmedText()))
            {
                continue;
            }

            int score = keywordOverlap(keywords, item.getKeywords());
            if (score > 0)
            {
                count = insertPhrase(ranked, scores, times, count, item, score);
            }
        }

        Phrase[] result = new Phrase[count];
        for (int i = 0; i < count; i++)
        {
            result[i] = ranked[i];
        }
        return result;
    }

    public Feedback[] selectFeedback(String keywords, String contextCode,
                                     Feedback[] candidates, int limit)
    {
        if (!CommunicationRules.isValidKeywords(keywords)
                || candidates == null || limit <= 0)
        {
            return new Feedback[0];
        }

        int capacity = Math.min(limit, MAX_FEEDBACK);
        Feedback[] ranked = new Feedback[capacity];
        int[] scores = new int[capacity];
        long[] times = new long[capacity];
        int count = 0;

        for (int i = 0; i < candidates.length; i++)
        {
            Feedback item = candidates[i];
            if (item == null
                    || !rules.isEligibleForReuse(contextCode,
                            item.getContextCode(), item.isAllowAIReuse())
                    || !CommunicationRules.mayShareFeedback(item))
            {
                continue;
            }

            int score = keywordOverlap(keywords, item.getKeywords());
            if (score > 0)
            {
                count = insertFeedback(ranked, scores, times, count, item, score);
            }
        }

        Feedback[] result = new Feedback[count];
        for (int i = 0; i < count; i++)
        {
            result[i] = ranked[i];
        }
        return result;
    }

    private int keywordOverlap(String request, String saved)
    {
        if (!CommunicationRules.hasText(saved))
        {
            return 0;
        }

        String[] requestWords = words(request);
        String[] savedWords = words(saved);
        int matches = 0;

        for (int i = 0; i < requestWords.length; i++)
        {
            boolean seenBefore = false;
            for (int p = 0; p < i; p++)
            {
                if (requestWords[i].equals(requestWords[p]))
                {
                    seenBefore = true;
                    break;
                }
            }
            if (seenBefore)
            {
                continue;
            }

            for (int j = 0; j < savedWords.length; j++)
            {
                if (requestWords[i].equals(savedWords[j]))
                {
                    matches++;
                    break;
                }
            }
        }
        return matches;
    }

    private String[] words(String text)
    {
        String normalized = text.toLowerCase(Locale.ROOT);
        String[] temporary = new String[normalized.length() + 1];
        int count = 0;
        int start = -1;

        for (int i = 0; i <= normalized.length(); i++)
        {
            boolean wordCharacter = i < normalized.length()
                    && Character.isLetterOrDigit(normalized.charAt(i));

            if (wordCharacter && start < 0)
            {
                start = i;
            }
            else if (!wordCharacter && start >= 0)
            {
                temporary[count] = normalized.substring(start, i);
                count++;
                start = -1;
            }
        }

        String[] result = new String[count];
        for (int i = 0; i < count; i++)
        {
            result[i] = temporary[i];
        }
        return result;
    }

    private int insertPhrase(Phrase[] items, int[] scores, long[] times,
                             int count, Phrase item, int score)
    {
        int position = 0;
        while (position < count
                && (scores[position] > score
                    || (scores[position] == score
                        && times[position] >= item.getCreatedAt())))
        {
            position++;
        }

        if (position >= items.length)
        {
            return count;
        }

        int newCount = Math.min(count + 1, items.length);
        for (int i = newCount - 1; i > position; i--)
        {
            items[i] = items[i - 1];
            scores[i] = scores[i - 1];
            times[i] = times[i - 1];
        }
        items[position] = item;
        scores[position] = score;
        times[position] = item.getCreatedAt();
        return newCount;
    }

    private int insertFeedback(Feedback[] items, int[] scores, long[] times,
                               int count, Feedback item, int score)
    {
        int position = 0;
        while (position < count
                && (scores[position] > score
                    || (scores[position] == score
                        && times[position] >= item.getCreatedAt())))
        {
            position++;
        }

        if (position >= items.length)
        {
            return count;
        }

        int newCount = Math.min(count + 1, items.length);
        for (int i = newCount - 1; i > position; i--)
        {
            items[i] = items[i - 1];
            scores[i] = scores[i - 1];
            times[i] = times[i - 1];
        }
        items[position] = item;
        scores[position] = score;
        times[position] = item.getCreatedAt();
        return newCount;
    }
}
