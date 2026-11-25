package mywords.ui;

import mywords.exception.InvalidInputException;

/** Validates UI fields without changing the text held in the Swing component. */
public class InputValidator {
    public static final int MAX_KEYWORDS = 300;
    public static final int MAX_SENTENCE = 500;

    /** Returns the original value after checking its trimmed form. */
    public String validateKeywords(String raw) throws InvalidInputException {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidInputException("Please enter at least one keyword.");
        }
        if (raw.trim().length() > MAX_KEYWORDS) {
            throw new InvalidInputException("Keywords must be 300 characters or fewer.");
        }
        return raw;
    }

    /** Returns the original sentence; the caller should preserve it in the input field on error. */
    public String validateSentence(String raw) throws InvalidInputException {
        if (raw == null || raw.trim().isEmpty()) {
            throw new InvalidInputException("Please enter a message.");
        }
        if (raw.length() > MAX_SENTENCE) {
            throw new InvalidInputException("The message must be 500 characters or fewer.");
        }
        return raw;
    }

    public String validateDecision(String code) throws InvalidInputException {
        if ("APPROVE".equals(code) || "EDIT".equals(code) || "REJECT".equals(code)) return code;
        throw new InvalidInputException("Choose approve, edit, or reject.");
    }

    public String validateReason(String code) throws InvalidInputException {
        if ("NONE".equals(code) || "WRONG_MEANING".equals(code) || "WRONG_TONE".equals(code)
                || "WRONG_PERSON_CONTEXT".equals(code) || "OTHER_UNSURE".equals(code)) return code;
        throw new InvalidInputException("Choose a valid feedback reason.");
    }
}
