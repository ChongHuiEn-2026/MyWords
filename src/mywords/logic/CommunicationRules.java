package mywords.logic;

import mywords.model.Feedback;
import mywords.model.Phrase;

/** Shared consent, scope, and confirmation rules for communication features. */
public class CommunicationRules
{
    public static final int MAX_KEYWORDS_LENGTH = 300;
    public static final int MAX_SENTENCE_LENGTH = 500;
    public static final int MAX_MEMORIES_PER_REQUEST = 5;

    public boolean isEligibleForReuse(String activeContext,
                                      String recordContext,
                                      boolean allowAIReuse)
    {
        return allowAIReuse && same(activeContext, recordContext);
    }

    public static boolean isKnownContext(String contextCode)
    {
        return "FAMILY".equals(contextCode)
                || "FRIENDS".equals(contextCode)
                || "HEALTHCARE".equals(contextCode);
    }

    public static boolean isKnownReason(String reasonCode)
    {
        return "NONE".equals(reasonCode)
                || "WRONG_MEANING".equals(reasonCode)
                || "WRONG_TONE".equals(reasonCode)
                || "WRONG_PERSON_CONTEXT".equals(reasonCode)
                || "OTHER_UNSURE".equals(reasonCode);
    }

    public static boolean hasText(String value)
    {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidKeywords(String keywords)
    {
        return hasText(keywords)
                && keywords.trim().length() <= MAX_KEYWORDS_LENGTH;
    }

    public static boolean isValidSentence(String sentence)
    {
        return hasText(sentence)
                && sentence.trim().length() <= MAX_SENTENCE_LENGTH;
    }

    public static boolean isInScope(String recordProfileId,
                                    String recordPartnerId,
                                    String recordContextCode,
                                    String activeProfileId,
                                    String selectedPartnerId,
                                    String selectedContextCode)
    {
        return same(recordProfileId, activeProfileId)
                && same(recordPartnerId, selectedPartnerId)
                && same(recordContextCode, selectedContextCode);
    }

    public static boolean mayBecomeConfirmedPhrase(Feedback feedback,
                                                   boolean freshConfirmation)
    {
        if (feedback == null || !freshConfirmation)
        {
            return false;
        }
        if ("APPROVE".equals(feedback.getDecisionCode()))
        {
            return isValidSentence(feedback.getCandidateText());
        }
        if ("EDIT".equals(feedback.getDecisionCode()))
        {
            return isValidSentence(feedback.getConfirmedText());
        }
        return false;
    }

    public static boolean maySharePhrase(Phrase phrase)
    {
        return phrase != null
                && phrase.isAllowAIReuse()
                && isValidSentence(phrase.getConfirmedText());
    }

    public static boolean mayShareFeedback(Feedback feedback)
    {
        if (feedback == null || !feedback.isAllowAIReuse())
        {
            return false;
        }
        if ("EDIT".equals(feedback.getDecisionCode()))
        {
            return isValidSentence(feedback.getConfirmedText());
        }
        if ("APPROVE".equals(feedback.getDecisionCode()))
        {
            return isValidSentence(feedback.getCandidateText());
        }
        return "REJECT".equals(feedback.getDecisionCode())
                && isValidSentence(feedback.getConfirmedText());
    }

    private static boolean same(String first, String second)
    {
        return first != null && second != null && first.equals(second);
    }
}
