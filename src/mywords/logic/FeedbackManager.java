package mywords.logic;

import mywords.exception.InvalidInputException;
import mywords.model.Feedback;

/** Validates a communicator's choice about a candidate sentence. */
public class FeedbackManager {
    public static final String APPROVE = "APPROVE";
    public static final String EDIT = "EDIT";
    public static final String REJECT = "REJECT";
    public static final String NONE_OF_THESE = "NONE_OF_THESE";

    /**
     * Creates the feedback record. A null correctedText is allowed when the
     * communicator has no correction; it is never inferred from a rejection.
     * For EDIT, correctedText must be freshly confirmed by the communicator.
     */
    public Feedback createDecision(String feedbackId, String profileId,
                                   String partnerId, String contextCode,
                                   String keywords, String candidateText,
                                   String decisionCode, String reasonCode,
                                   String correctedText, boolean freshConfirmation,
                                   boolean allowAIReuse, long createdAt)
            throws IllegalArgumentException {
        require(CommunicationRules.hasText(feedbackId), "Feedback ID is required.");
        require(CommunicationRules.hasText(profileId), "Profile ID is required.");
        require(CommunicationRules.hasText(partnerId), "Partner ID is required.");
        require(CommunicationRules.isKnownContext(contextCode), "Choose a valid context.");
        require(CommunicationRules.isValidKeywords(keywords), "Enter valid keywords.");
        require(CommunicationRules.hasText(decisionCode), "Choose a decision.");
        require(CommunicationRules.isKnownReason(reasonCode), "Choose a valid reason.");

        String normalizedDecision = decisionCode.trim().toUpperCase();
        String normalizedReason = reasonCode.trim().toUpperCase();
        String safeCandidate = candidateText == null ? "" : candidateText.trim();
        String safeCorrection = correctedText == null ? "" : correctedText.trim();

        if (APPROVE.equals(normalizedDecision)) {
            require(CommunicationRules.isValidSentence(safeCandidate),
                    "There is no usable candidate to approve.");
            require("NONE".equals(normalizedReason), "Approval must use reason NONE.");
        } else if (EDIT.equals(normalizedDecision)) {
            require(CommunicationRules.isValidSentence(safeCandidate),
                    "Choose a candidate before editing it.");
            require(freshConfirmation && CommunicationRules.isValidSentence(safeCorrection),
                    "The edited wording needs a fresh confirmation.");
        } else if (REJECT.equals(normalizedDecision)) {
            require(CommunicationRules.isValidSentence(safeCandidate),
                    "Choose a candidate before rejecting it.");
            require(!"NONE".equals(normalizedReason),
                    "Choose a rejection reason, or use OTHER_UNSURE.");
            // A correction after rejection is optional and not required to
            // explain the rejected candidate.
        } else if (NONE_OF_THESE.equals(normalizedDecision)) {
            safeCandidate = "";
            safeCorrection = "";
            require("NONE".equals(normalizedReason),
                    "None of these does not record a rejection reason.");
        } else {
            throw new IllegalArgumentException("Choose approve, edit, reject, or none of these.");
        }

        return new Feedback(feedbackId.trim(), profileId.trim(), partnerId.trim(),
                contextCode.trim(), keywords.trim(), safeCandidate,
                normalizedDecision, normalizedReason, safeCorrection,
                allowAIReuse, createdAt);
    }

    /**
     * Integration adapter (added by Role A): keeps the skeleton/INTEGRATION
     * name createFeedback used by the UI, and delegates to createDecision.
     * The UI only calls this after the communicator confirmed the text.
     */
    public Feedback createFeedback(String feedbackId, String profileId, String partnerId,
                                   String contextCode, String keywords, String candidateText,
                                   String decisionCode, String reasonCode, String confirmedText,
                                   boolean allowAIReuse) throws InvalidInputException {
        try {
            return createDecision(feedbackId, profileId, partnerId, contextCode, keywords,
                    candidateText, decisionCode, reasonCode, confirmedText, true,
                    allowAIReuse, System.currentTimeMillis());
        } catch (IllegalArgumentException e) { // turn C's validation error into our custom exception
            throw new InvalidInputException(e.getMessage());
        }
    }

    /** Storage permission is separate from confirming a message. */
    public boolean maySaveConfirmedPhrase(Feedback feedback, boolean freshConfirmation,
                                          boolean allowPhraseStorage) {
        return allowPhraseStorage
                && CommunicationRules.mayBecomeConfirmedPhrase(feedback, freshConfirmation);
    }

    /** Feedback sharing also requires explicit consent and a usable record. */
    public boolean mayReuseFeedback(Feedback feedback) {
        return CommunicationRules.mayShareFeedback(feedback);
    }

    private void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
}
