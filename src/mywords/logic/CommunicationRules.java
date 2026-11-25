/** Shared communication-safety and input rules for Role C. */
public final class CommunicationRules {
    public static final int MAX_KEYWORDS_LENGTH = 300;
    public static final int MAX_SENTENCE_LENGTH = 500;
    public static final int MAX_MEMORIES_PER_REQUEST = 5;

    private CommunicationRules() { }

    public static boolean isKnownContext(String contextCode) {
        return "FAMILY".equals(contextCode) || "FRIENDS".equals(contextCode)
                || "HEALTHCARE".equals(contextCode);
    }

    public static boolean isKnownReason(String reasonCode) {
        return "NONE".equals(reasonCode) || "WRONG_MEANING".equals(reasonCode)
                || "WRONG_TONE".equals(reasonCode)
                || "WRONG_PERSON_CONTEXT".equals(reasonCode)
                || "OTHER_UNSURE".equals(reasonCode);
    }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidKeywords(String keywords) {
        return hasText(keywords) && keywords.trim().length() <= MAX_KEYWORDS_LENGTH;
    }

    public static boolean isValidSentence(String sentence) {
        return hasText(sentence) && sentence.trim().length() <= MAX_SENTENCE_LENGTH;
    }

    /**
     * True only when a record belongs to the selected profile, partner and
     * context. Consent is checked separately so local saved-phrase search can
     * still offer phrases that the person has not allowed AI to reuse.
     */
    public static boolean isInScope(String recordProfileId, String recordPartnerId,
                                    String recordContextCode, String activeProfileId,
                                    String selectedPartnerId, String selectedContextCode) {
        return same(recordProfileId, activeProfileId)
                && same(recordPartnerId, selectedPartnerId)
                && same(recordContextCode, selectedContextCode);
    }

    public static boolean same(String first, String second) {
        return first != null && second != null && first.equals(second);
    }

    /** Rejected candidate text is never promoted as confirmed meaning. */
    public static boolean mayBecomeConfirmedPhrase(Feedback feedback,
                                                   boolean freshConfirmation) {
        if (feedback == null || !freshConfirmation) return false;
        if ("APPROVE".equals(feedback.decisionCode)) {
            return isValidSentence(feedback.candidateText);
        }
        if ("EDIT".equals(feedback.decisionCode)) {
            return isValidSentence(feedback.confirmedText);
        }
        return false;
    }

    /** Only explicitly consented and relevant records may enter an AI prompt. */
    public static boolean maySharePhrase(Phrase phrase) {
        return phrase != null && phrase.allowAIReuse && isValidSentence(phrase.confirmedText);
    }

    public static boolean mayShareFeedback(Feedback feedback) {
        if (feedback == null || !feedback.allowAIReuse) return false;
        if ("EDIT".equals(feedback.decisionCode))
            return isValidSentence(feedback.confirmedText);
        if ("APPROVE".equals(feedback.decisionCode))
            return isValidSentence(feedback.candidateText);
        // A rejection can be useful as explicitly labelled feedback only
        // when the communicator also supplied a confirmed correction.
        return "REJECT".equals(feedback.decisionCode)
                && isValidSentence(feedback.confirmedText);
    }
}
