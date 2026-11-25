/** Validates AI candidates and provides saved-phrase/manual fallback choices. */
public class SuggestionService {
    /** AI boundary implemented by Role B; this class performs no network calls. */
    public interface CandidateProvider {
        Suggestion[] generate(String requestId, String keywords, String contextCode)
                throws AIServiceException;
    }

    /**
     * Returns validated candidates, saved-phrase fallback, or a manual draft.
     * The caller must display the options for the communicator to choose;
     * this method deliberately never selects one.
     */
    public Suggestion[] getChoices(String requestId, String profileId,
                                   String partnerId, String contextCode,
                                   String keywords, boolean aiSharingEnabled,
                                   CandidateProvider provider, Phrase[] savedPhrases,
                                   String manualDraft) {
        if (!CommunicationRules.hasText(requestId)
                || !CommunicationRules.hasText(profileId)
                || !CommunicationRules.hasText(partnerId)
                || !CommunicationRules.isKnownContext(contextCode)
                || !CommunicationRules.isValidKeywords(keywords)) {
            return manualChoice(requestId, manualDraft);
        }

        if (aiSharingEnabled && provider != null) {
            try {
                Suggestion[] candidates = provider.generate(requestId, keywords, contextCode);
                if (isValidCandidateSet(candidates)) return copy(candidates);
            } catch (AIServiceException unavailable) {
                // Continue to local saved phrases/manual composition.
            }
        }
        return fallback(requestId, profileId, partnerId, contextCode,
                keywords, savedPhrases, manualDraft);
    }

    /** Requires three nonempty, distinct candidate sentences. */
    public boolean isValidCandidateSet(Suggestion[] candidates) {
        if (candidates == null || candidates.length != 3) return false;
        for (int i = 0; i < candidates.length; i++) {
            if (candidates[i] == null
                    || !CommunicationRules.isValidSentence(candidates[i].text)) return false;
            for (int j = 0; j < i; j++) {
                if (candidates[i].text.trim().equalsIgnoreCase(candidates[j].text.trim()))
                    return false;
            }
        }
        return true;
    }

    private Suggestion[] fallback(String requestId, String profileId,
                                  String partnerId, String contextCode,
                                  String keywords, Phrase[] savedPhrases,
                                  String manualDraft) {
        Suggestion[] localMatches = savedPhraseChoices(requestId, profileId,
                partnerId, contextCode, keywords, savedPhrases, 3);
        if (localMatches.length > 0) return localMatches;
        return manualChoice(requestId, manualDraft);
    }

    /** Offline saved phrases are local choices; AI consent is not required. */
    public Suggestion[] savedPhraseChoices(String requestId, String profileId,
                                           String partnerId, String contextCode,
                                           String keywords, Phrase[] savedPhrases,
                                           int limit) {
        if (savedPhrases == null || limit <= 0) return new Suggestion[0];
        int max = Math.min(limit, 3);
        Suggestion[] result = new Suggestion[max];
        int count = 0;
        for (int i = 0; i < savedPhrases.length && count < max; i++) {
            Phrase phrase = savedPhrases[i];
            if (phrase == null || !CommunicationRules.isValidSentence(phrase.confirmedText)
                    || !CommunicationRules.isInScope(phrase.profileId, phrase.partnerId,
                    phrase.contextCode, profileId, partnerId, contextCode)
                    || !hasKeywordMatch(keywords, phrase.keywords)) continue;
            result[count] = new Suggestion(phrase.phraseId, requestId,
                    phrase.confirmedText, "SAVED");
            count++;
        }
        Suggestion[] compact = new Suggestion[count];
        for (int i = 0; i < count; i++) compact[i] = result[i];
        return compact;
    }

    private boolean hasKeywordMatch(String request, String saved) {
        if (!CommunicationRules.hasText(request) || !CommunicationRules.hasText(saved)) return false;
        String[] requestParts = request.toLowerCase().split("[^\\p{L}\\p{N}]+");
        String normalizedSaved = " " + saved.toLowerCase().replaceAll("[^\\p{L}\\p{N}]+", " ") + " ";
        for (int i = 0; i < requestParts.length; i++) {
            if (!requestParts[i].isEmpty() && normalizedSaved.contains(" " + requestParts[i] + " "))
                return true;
        }
        return false;
    }

    private Suggestion[] manualChoice(String requestId, String manualDraft) {
        if (!CommunicationRules.isValidSentence(manualDraft)) return new Suggestion[0];
        return new Suggestion[] { new Suggestion("MANUAL", requestId,
                manualDraft.trim(), "MANUAL") };
    }

    private Suggestion[] copy(Suggestion[] source) {
        Suggestion[] result = new Suggestion[source.length];
        for (int i = 0; i < source.length; i++) {
            result[i] = new Suggestion(source[i].suggestionId,
                    source[i].requestId, source[i].text.trim(), source[i].sourceCode);
        }
        return result;
    }
}
