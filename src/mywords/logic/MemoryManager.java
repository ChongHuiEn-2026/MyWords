/** Searches and deterministically ranks scoped, consented memory records. */
public class MemoryManager {
    public Phrase[] selectPhrases(String activeProfileId, String partnerId,
                                  String contextCode, String keywords,
                                  Phrase[] candidates, int limit) {
        if (!CommunicationRules.isValidKeywords(keywords)
                || candidates == null || limit <= 0) return new Phrase[0];
        Phrase[] ranked = new Phrase[Math.min(limit, CommunicationRules.MAX_MEMORIES_PER_REQUEST)];
        int[] scores = new int[ranked.length];
        int count = 0;

        for (int i = 0; i < candidates.length; i++) {
            Phrase item = candidates[i];
            if (item == null || !CommunicationRules.maySharePhrase(item)
                    || !CommunicationRules.isInScope(item.profileId, item.partnerId,
                    item.contextCode, activeProfileId, partnerId, contextCode)) continue;
            int score = keywordOverlap(keywords, item.keywords);
            if (score == 0) continue;
            count = insertPhrase(ranked, scores, count, item, score);
        }
        return copyPhrases(ranked, count);
    }

    public Feedback[] selectFeedback(String activeProfileId, String partnerId,
                                     String contextCode, String keywords,
                                     Feedback[] candidates, int limit) {
        if (!CommunicationRules.isValidKeywords(keywords)
                || candidates == null || limit <= 0) return new Feedback[0];
        Feedback[] ranked = new Feedback[Math.min(limit, CommunicationRules.MAX_MEMORIES_PER_REQUEST)];
        int[] scores = new int[ranked.length];
        int count = 0;

        for (int i = 0; i < candidates.length; i++) {
            Feedback item = candidates[i];
            if (item == null || !CommunicationRules.mayShareFeedback(item)
                    || !CommunicationRules.isInScope(item.profileId, item.partnerId,
                    item.contextCode, activeProfileId, partnerId, contextCode)) continue;
            int score = keywordOverlap(keywords, item.keywords);
            if (score == 0) continue;
            count = insertFeedback(ranked, scores, count, item, score);
        }
        return copyFeedback(ranked, count);
    }

    private int keywordOverlap(String requestKeywords, String savedKeywords) {
        if (!CommunicationRules.hasText(savedKeywords)) return 0;
        String[] requestWords = words(requestKeywords);
        String[] savedWords = words(savedKeywords);
        int matches = 0;
        for (int i = 0; i < requestWords.length; i++) {
            if (requestWords[i].length() == 0) continue;
            boolean found = false;
            for (int j = 0; j < savedWords.length; j++) {
                if (requestWords[i].equals(savedWords[j])) { found = true; break; }
            }
            if (found) matches++;
        }
        return matches;
    }

    private String[] words(String text) {
        String normalized = text.toLowerCase().trim();
        String[] result = new String[normalized.length() + 1];
        int count = 0, start = -1;
        for (int i = 0; i <= normalized.length(); i++) {
            boolean wordChar = i < normalized.length()
                    && Character.isLetterOrDigit(normalized.charAt(i));
            if (wordChar && start < 0) start = i;
            if (!wordChar && start >= 0) {
                result[count++] = normalized.substring(start, i);
                start = -1;
            }
        }
        String[] compact = new String[count];
        for (int i = 0; i < count; i++) compact[i] = result[i];
        return compact;
    }

    private int insertPhrase(Phrase[] items, int[] scores, int count,
                             Phrase item, int score) {
        int position = positionFor(score, item.createdAt, scores, count,
                items.length, phraseTimes(items));
        if (position >= items.length) return count;
        int newCount = count < items.length ? count + 1 : count;
        for (int i = newCount - 1; i > position; i--) {
            items[i] = items[i - 1]; scores[i] = scores[i - 1];
        }
        items[position] = item; scores[position] = score;
        return newCount;
    }

    private int insertFeedback(Feedback[] items, int[] scores, int count,
                               Feedback item, int score) {
        int position = positionFor(score, item.createdAt, scores, count,
                items.length, feedbackTimes(items));
        if (position >= items.length) return count;
        int newCount = count < items.length ? count + 1 : count;
        for (int i = newCount - 1; i > position; i--) {
            items[i] = items[i - 1]; scores[i] = scores[i - 1];
        }
        items[position] = item; scores[position] = score;
        return newCount;
    }

    private int positionFor(int score, long createdAt, int[] scores, int count,
                            int capacity, long[] times) {
        int i = 0;
        while (i < count && (scores[i] > score
                || (scores[i] == score && times[i] >= createdAt))) i++;
        return i < capacity ? i : capacity;
    }

    private long[] phraseTimes(Phrase[] items) {
        long[] times = new long[items.length];
        for (int i = 0; i < items.length; i++) if (items[i] != null) times[i] = items[i].createdAt;
        return times;
    }

    private long[] feedbackTimes(Feedback[] items) {
        long[] times = new long[items.length];
        for (int i = 0; i < items.length; i++) if (items[i] != null) times[i] = items[i].createdAt;
        return times;
    }

    private Phrase[] copyPhrases(Phrase[] items, int count) {
        Phrase[] result = new Phrase[count];
        for (int i = 0; i < count; i++) result[i] = items[i];
        return result;
    }

    private Feedback[] copyFeedback(Feedback[] items, int count) {
        Feedback[] result = new Feedback[count];
        for (int i = 0; i < count; i++) result[i] = items[i];
        return result;
    }
}
