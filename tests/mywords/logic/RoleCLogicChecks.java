package mywords.logic;

import mywords.model.Phrase;
import mywords.model.Suggestion;

/** Small dependency-free checks for Role C's fallback and memory selection. */
public class RoleCLogicChecks
{
    private static int passed;

    public static void main(String[] args)
    {
        checkSectionSevenFallback(
                "later, tapau, nasi lemak, for me", "FRIENDS",
                "Later can tapau nasi lemak for me?");
        checkSectionSevenFallback(
                "later, tapau, nasi lemak, for me", "FRIENDS",
                "Could you get me some nasi lemak to take away later?");
        checkSectionSevenFallback(
                "tomorrow, Ah Ma, yum cha", "FAMILY",
                "Tomorrow I want to yum cha with Ah Ma.");
        checkSectionSevenFallback(
                "nurse, speak, now", "HEALTHCARE",
                "Could I speak with a nurse now?");
        checkMemoryRankingAndConsent();
        checkFamilyHealthcareIsolation();
        checkFallbackUsesWholeWordsAndKeepsText();
        System.out.println("Role C logic checks passed: " + passed);
    }

    private static void checkSectionSevenFallback(String keywords, String context,
                                                   String confirmedText)
    {
        Phrase saved = phrase("PH-" + passed, context, keywords,
                confirmedText, false, 100L);
        Suggestion[] result = new SuggestionService().savedPhraseFallback(
                "REQ-" + passed, keywords, new Phrase[] { saved });

        require(result.length == 1, "section 7 fallback returns its matching phrase");
        require(result[0].getText().equals(confirmedText),
                "section 7 fallback preserves the confirmed wording");
        require("SAVED".equals(result[0].getSourceCode()),
                "fallback labels the choice SAVED");
        passed++;
    }

    private static void checkMemoryRankingAndConsent()
    {
        MemoryManager manager = new MemoryManager();
        Phrase best = phrase("PH-best", "FRIENDS", "later tapau nasi lemak for me",
                "Later can tapau nasi lemak for me?", true, 100L);
        Phrase second = phrase("PH-second", "FRIENDS", "nasi lemak me",
                "Please get nasi lemak for me.", true, 50L);
        Phrase third = phrase("PH-third", "FRIENDS", "later tapau",
                "Can tapau later?", true, 300L);
        Phrase noConsent = phrase("PH-no-consent", "FRIENDS",
                "later tapau nasi lemak for me", "Private phrase.", false, 500L);
        Phrase[] selected = manager.selectPhrases(
                "later, tapau, nasi lemak, for me",
                "FRIENDS", new Phrase[] { third, noConsent, second, best }, 3);

        require(selected.length == 3, "memory selection respects the limit");
        require("PH-best".equals(selected[0].getPhraseId()),
                "memory ranks the greatest keyword overlap first");
        require("PH-second".equals(selected[1].getPhraseId()),
                "memory ranks the next greatest keyword overlap second");
        require("PH-third".equals(selected[2].getPhraseId()),
                "memory ranks the remaining eligible phrase third");
        for (int i = 0; i < selected.length; i++)
        {
            require(!"PH-no-consent".equals(selected[i].getPhraseId()),
                    "memory excludes a phrase without AI reuse consent");
        }
        passed++;
    }

    private static void checkFamilyHealthcareIsolation()
    {
        MemoryManager manager = new MemoryManager();
        Phrase family = phrase("PH-family", "FAMILY", "nurse speak now",
                "I want to speak with my family now.", true, 1L);
        Phrase healthcare = phrase("PH-healthcare", "HEALTHCARE", "nurse speak now",
                "Could I speak with a nurse now?", true, 2L);

        Phrase[] familyResults = manager.selectPhrases("nurse speak now", "FAMILY",
                new Phrase[] { healthcare, family }, 3);
        require(familyResults.length == 1,
                "FAMILY request excludes the otherwise matching HEALTHCARE phrase");
        require("PH-family".equals(familyResults[0].getPhraseId()),
                "FAMILY request returns only the FAMILY phrase");

        Phrase[] healthcareResults = manager.selectPhrases("nurse speak now", "HEALTHCARE",
                new Phrase[] { family, healthcare }, 3);
        require(healthcareResults.length == 1
                        && "PH-healthcare".equals(healthcareResults[0].getPhraseId()),
                "HEALTHCARE request returns its own matching phrase");
        passed++;
    }

    private static void checkFallbackUsesWholeWordsAndKeepsText()
    {
        String originalText = "Later can tapau nasi lemak for me?  Lah.";
        Phrase saved = phrase("PH-rojak", "FRIENDS", "tapau, nasi lemak",
                originalText, false, 1L);
        SuggestionService service = new SuggestionService();
        Suggestion[] matched = service.savedPhraseFallback("REQ-match", "TAPAU!",
                new Phrase[] { saved });
        Suggestion[] partialWord = service.savedPhraseFallback("REQ-partial", "tap",
                new Phrase[] { saved });

        require(matched.length == 1, "fallback keyword matching ignores case and punctuation");
        require(originalText.equals(matched[0].getText()),
                "fallback preserves slang and spacing in saved text");
        require(partialWord.length == 0,
                "fallback does not match a partial word such as tap inside tapau");
        passed++;
    }

    private static Phrase phrase(String id, String context, String keywords,
                                 String confirmedText, boolean allowAIReuse,
                                 long createdAt)
    {
        return new Phrase(id, "P001", "CP001", context, keywords,
                confirmedText, allowAIReuse, createdAt);
    }

    private static void require(boolean condition, String description)
    {
        if (!condition)
        {
            throw new AssertionError("FAILED: " + description);
        }
        System.out.println("PASS: " + description);
    }
}
