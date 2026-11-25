package mywords.logic;

import java.util.Locale;
import mywords.model.Phrase;
import mywords.model.Suggestion;

/** Validates AI candidates and offers relevant saved phrases as fallback. */
public class SuggestionService
{
    public static final int MAX_FALLBACK_CHOICES = 3;

    /**
     * Returns matching saved phrases as local choices. This does not require
     * AI-reuse consent because these choices stay in the app.
     */
    public Suggestion[] savedPhraseFallback(String requestId, String keywords,
                                            Phrase[] savedPhrases)
    {
        if (!CommunicationRules.hasText(requestId)
                || !CommunicationRules.isValidKeywords(keywords)
                || savedPhrases == null)
        {
            return new Suggestion[0];
        }

        int capacity = Math.min(MAX_FALLBACK_CHOICES, savedPhrases.length);
        Suggestion[] matches = new Suggestion[capacity];
        int count = 0;

        for (int i = 0; i < savedPhrases.length && count < capacity; i++)
        {
            Phrase phrase = savedPhrases[i];
            if (phrase == null
                    || !CommunicationRules.hasText(phrase.getPhraseId())
                    || !CommunicationRules.isValidSentence(phrase.getConfirmedText())
                    || !hasKeywordMatch(keywords, phrase.getKeywords()))
            {
                continue;
            }

            matches[count] = new Suggestion(
                    phrase.getPhraseId(),
                    requestId.trim(),
                    phrase.getConfirmedText().trim(),
                    "SAVED");
            count++;
        }

        Suggestion[] result = new Suggestion[count];
        for (int i = 0; i < count; i++)
        {
            result[i] = matches[i];
        }
        return result;
    }

    /** Requires exactly three distinct, nonempty candidate sentences. */
    public boolean isValidCandidateSet(Suggestion[] candidates)
    {
        if (candidates == null || candidates.length != 3)
        {
            return false;
        }

        for (int i = 0; i < candidates.length; i++)
        {
            if (candidates[i] == null
                    || !CommunicationRules.isValidSentence(candidates[i].getText()))
            {
                return false;
            }

            for (int j = 0; j < i; j++)
            {
                if (candidates[i].getText().trim()
                        .equalsIgnoreCase(candidates[j].getText().trim()))
                {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasKeywordMatch(String request, String saved)
    {
        if (!CommunicationRules.hasText(saved))
        {
            return false;
        }

        String[] requestWords = words(request);
        String[] savedWords = words(saved);

        for (int i = 0; i < requestWords.length; i++)
        {
            for (int j = 0; j < savedWords.length; j++)
            {
                if (requestWords[i].equals(savedWords[j]))
                {
                    return true;
                }
            }
        }
        return false;
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
}
