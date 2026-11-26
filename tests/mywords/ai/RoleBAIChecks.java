package mywords.ai;

import java.io.RandomAccessFile;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import mywords.app.AppController;
import mywords.app.DemoData;
import mywords.app.Main;
import mywords.app.RequestSnapshot;
import mywords.exception.AIServiceException;
import mywords.exception.InvalidInputException;
import mywords.logic.MemoryManager;
import mywords.logic.SuggestionService;
import mywords.model.Codes;
import mywords.model.CommunicationProfile;
import mywords.model.Feedback;
import mywords.model.Phrase;
import mywords.model.Suggestion;
import mywords.model.User;
import mywords.storage.FileManager;

/** Offline checks and opt-in live cases using fictional text. */
public class RoleBAIChecks
{
    private static final String CONTENT =
            "[\"Boleh tapau nasi lemak, please?\",\"Can you say \\\"yum cha\\\" to Ah Ma?\","
            + "\"I want kopi with 阿嬷.\"]";
    private static final String ENVELOPE =
            "{\"usage\":{\"total_tokens\":42},\"choices\":[{\"finish_reason\":\"stop\","
            + "\"message\":{\"role\":\"assistant\",\"content\":"
            + quoteFixture(CONTENT) + "}}]}";
    private static final String CONFIG = """
            # Local test fixture
            AI_API_KEY=fictional-key=
            AI_ENDPOINT=https://api.example.test/v1/chat/completions
            AI_MODEL=fictional-model
            CONNECT_TIMEOUT_MS=10000
            READ_TIMEOUT_MS=20000
            MAX_RESPONSE_BYTES=65536
            DATA_DIRECTORY=data
            """;
    private static final LanguageCase FAMILY_CASE = new LanguageCase("family", "FAMILY",
            "tomorrow, Ah Ma, yum cha", "Casual rojak", "Warm tone");
    private static final LanguageCase[] LANGUAGE_CASES = {
        new LanguageCase("friends-casual", "FRIENDS", "later, tapau, nasi lemak, for me",
                "Casual rojak", "Friendly"),
        new LanguageCase("friends-formal", "FRIENDS", "later, tapau, nasi lemak, for me",
                "Formal English", "Polite"),
        FAMILY_CASE,
        new LanguageCase("healthcare", "HEALTHCARE", "nurse, speak, now", "Formal English", "Polite")
    };
    private static int assertions;

    public static void main(String[] args) throws Exception
    {
        if (args.length > 0)
        {
            checkLiveProvider(selectLiveCase(args));
            return;
        }

        Path config = Files.createTempFile("mywords-role-b-", ".txt");
        try
        {
            checkConfig(config);
            checkParser();
            checkPrompt();
            checkLanguagePrompts();
            checkGeneration();
            checkFallback();
            checkConsentSnapshot();
            System.out.println("Role B AI checks passed: " + assertions);
        }
        finally
        {
            Files.deleteIfExists(config);
        }
    }

    private static LanguageCase selectLiveCase(String[] args)
    {
        if ("--live".equals(args[0]))
        {
            if (args.length == 1)
            {
                return FAMILY_CASE;
            }
            if (args.length == 2)
            {
                for (LanguageCase example : LANGUAGE_CASES)
                {
                    if (example.name().equals(args[1]))
                    {
                        return example;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Run without arguments for offline checks or use "
                + "--live [friends-casual|friends-formal|family|healthcare].");
    }

    private static void checkLiveProvider(LanguageCase example) throws Exception
    {
        System.out.println("Live check sends fictional keywords, context, style and tone to the configured AI provider.");
        System.out.println(example.name() + " | " + example.context() + " | " + example.style()
                + " | " + example.tone() + " | " + example.keywords());
        System.out.println("The three replies are logged in a temporary directory and then printed.");
        Path folder = Files.createTempDirectory("mywords-role-b-live-");
        Path log = folder.resolve("ai_log.txt");
        try
        {
            AIHelper ai = new AIHelper(new FileManager(folder.toString()));
            Suggestion[] results = ai.generateSuggestions(example.request(true, new Phrase[0], new Feedback[0]));
            check(results.length == 3 && Files.readString(log).split("\\R").length == 4,
                    "three suggestions logged before display");
            for (Suggestion suggestion : results)
            {
                check(Codes.SOURCE_AI.equals(suggestion.getSourceCode()), "live result source is AI");
                System.out.println(suggestion.getText());
            }
            System.out.println("Live Role B check passed: three logged AI suggestions.");
            System.out.println("Review each suggestion for meaning, register and details absent from the keywords.");
        }
        finally
        {
            Files.deleteIfExists(log);
            Files.delete(folder);
        }
    }

    private static void checkConfig(Path config) throws Exception
    {
        Files.writeString(config, CONFIG);
        AIConfig settings = AIConfig.load(config.toString());
        check("fictional-key=".equals(settings.getApiKey()), "config splits only at the first equals");
        check(settings.getEndpoint().startsWith("https://"), "config reads endpoint");
        check("fictional-model".equals(settings.getModel()), "config reads model");
        Files.writeString(config, CONFIG.replace("fictional-key=", "REPLACE_WITH_LOCAL_KEY"));
        expectAI(() -> AIConfig.load(config.toString()), "configuration placeholder");
        Files.writeString(config, CONFIG.replace("fictional-key=", " "));
        expectAI(() -> AIConfig.load(config.toString()), "blank key");
        Files.writeString(config, CONFIG.replace("https://", "http://"));
        expectAI(() -> AIConfig.load(config.toString()), "insecure endpoint");
        Files.writeString(config, CONFIG + "AI_MODEL=duplicate\n");
        expectAI(() -> AIConfig.load(config.toString()), "duplicate setting");
        Files.writeString(config, CONFIG.replace("10000", "0"));
        expectAI(() -> AIConfig.load(config.toString()), "nonpositive timeout");
        Files.delete(config);
        expectAI(() -> AIConfig.load(config.toString()), "missing config file");
    }

    private static void checkParser() throws Exception
    {
        AIResponseParser parser = new AIResponseParser();
        String[] candidates = parser.parseCandidates(parser.extractContent(ENVELOPE));
        check(candidates.length == 3, "extracts three candidates");
        check("Boleh tapau nasi lemak, please?".equals(candidates[0]), "keeps commas and rojak");
        check("Can you say \"yum cha\" to Ah Ma?".equals(candidates[1]), "decodes escaped quotes");
        check(candidates[2].contains("阿嬷"), "preserves Unicode");
        String fenced = "  ```json\r\n" + CONTENT + "\r\n```\n";
        String[] wrapped = parser.parseCandidates(fenced);
        check(candidates[0].equals(wrapped[0]) && candidates[1].equals(wrapped[1])
                && candidates[2].equals(wrapped[2]), "one JSON code fence preserves all three sentences");
        check(parser.parseCandidates("```\n" + CONTENT + "\n```").length == 3, "bare code fence is accepted");
        expectAI(() -> parser.parseCandidates(fenced + "extra prose"), "prose after the fence");
        expectAI(() -> parser.parseCandidates("extra prose\n" + fenced), "prose before the fence");
        expectAI(() -> parser.parseCandidates("```html\n" + CONTENT + "\n```"), "non-JSON code fence");
        expectAI(() -> parser.parseCandidates("```json\n[\"a\",\"b\",\"c\",\"d\"]\n```"),
                "four candidates inside a fence");
        String[] escaped = parser.parseCandidates("[ \"C:\\\\kopi\", \"Ah Ma\\u0021\", \"tab\\there\" ]");
        check("C:\\kopi".equals(escaped[0]), "decodes backslashes");
        check("Ah Ma!".equals(escaped[1]), "decodes Unicode escapes");
        check("tab\there".equals(escaped[2]), "decodes tabs");
        check(parser.extractContent("{\"choices\":[{\"message\":{\"content\":\"one\\ntwo\"}}]}")
                .equals("one\ntwo"), "decodes newline in envelope content");
        expectAI(() -> parser.extractContent(ENVELOPE + "fake tail"), "trailing envelope data");
        expectAI(() -> parser.extractContent(ENVELOPE.replace("\"stop\"", "\"length\"")), "truncated provider finish");
        expectAI(() -> parser.extractContent("{\"choices\":[{\"message\":{\"content\":null}}]}"), "missing content");
        expectAI(() -> parser.extractContent("{\"meta\":{\"choices\":[]} }"), "nested choices cannot stand in for top level");
        for (String invalid : new String[] {"[\"a\",\"b\"]", "[\"a\",\"b\",\"c\",\"d\"]",
                "[\"a\",\" A \",\"c\"]", "[\"a\",\" \",\"c\"]", "[\"a\",12,\"c\"]",
                "[\"a\",\"b\",\"c\",]", "[\"a\",\"b\\q\",\"c\"]", "[\"a\",\"b\\nc\",\"d\"]",
                "[\"a\",\"b\",\"" + "z".repeat(501) + "\"]"})
        {
            expectAI(() -> parser.parseCandidates(invalid), "invalid candidate array");
        }
    }

    private static void checkPrompt()
    {
        Phrase[] phrases = {
            new Phrase("PH001", "P-test", "CP-test", "FAMILY", "kopi", "Jom kopi lah.", true, 1),
            new Phrase("PH002", "P-test", "CP-test", "FAMILY", "kopi", "PRIVATE PHRASE", false, 2),
            new Phrase("PH003", "P-other", "CP-test", "FAMILY", "kopi", "OTHER PROFILE", true, 3)
        };
        Feedback[] feedback = {
            new Feedback("FB001", "P-test", "CP-test", "FAMILY", "kopi", "Wrong kopi request.",
                    "EDIT", "WRONG_MEANING", "Can tapau kopi for me?", true, 1),
            new Feedback("FB002", "P-test", "CP-test", "FAMILY", "kopi", "UNCONFIRMED REJECTION",
                    "REJECT", "WRONG_TONE", "", true, 2),
            new Feedback("FB003", "P-test", "CP-test", "FAMILY", "kopi", "REJECTED ORIGINAL",
                    "REJECT", "WRONG_PERSON_CONTEXT", "Jom kopi with Ah Ma.", true, 3)
        };
        String prompt = new PromptBuilder().build(request(true, phrases, feedback));
        check(prompt.contains("tomorrow, Ah Ma, yum cha") && prompt.contains("Casual rojak")
                && prompt.contains("Warm tone") && prompt.contains("FAMILY"), "prompt includes input, style, tone and context");
        check(prompt.contains("Jom kopi lah.") && prompt.contains("Can tapau kopi for me?"), "prompt includes eligible examples");
        check(prompt.contains("Wrong kopi request.") && prompt.toLowerCase().contains("rejected"), "edited candidate labelled as negative");
        check(!prompt.contains("P-test") && !prompt.contains("CP-test"), "prompt omits local IDs");
        check(!prompt.contains("PRIVATE PHRASE") && !prompt.contains("OTHER PROFILE")
                && !prompt.contains("UNCONFIRMED REJECTION"), "prompt omits disallowed examples");
        check(prompt.contains("Jom kopi with Ah Ma.") && !prompt.contains("REJECTED ORIGINAL")
                && !prompt.contains("WRONG_PERSON_CONTEXT"), "only the confirmed wording from a rejection is reused");
    }

    private static void checkLanguagePrompts()
    {
        PromptBuilder builder = new PromptBuilder();
        for (LanguageCase example : LANGUAGE_CASES)
        {
            String prompt = builder.build(example.request(true, new Phrase[0], new Feedback[0]));
            check(prompt.contains("Current keywords: " + quoteFixture(example.keywords()) + "\n"),
                    example.name() + " keeps the keywords as entered");
            check(prompt.contains("Context: " + quoteFixture(example.context()) + "\n")
                    && prompt.contains("Language style: " + quoteFixture(example.style()) + "\n")
                    && prompt.contains("Partner tone: " + quoteFixture(example.tone()) + "\n"),
                    example.name() + " sends the selected context, style and tone");
        }
    }

    private static void checkGeneration() throws Exception
    {
        Path folder = Files.createTempDirectory("mywords-role-b-log-");
        Path log = folder.resolve("ai_log.txt");
        try
        {
            StubAI ai = new StubAI(new FileManager(folder.toString()), ENVELOPE);
            expectAI(() -> ai.generateSuggestions(request(false, new Phrase[0], new Feedback[0])), "consent off");
            check(ai.calls == 0 && Files.notExists(log), "no provider call or log without consent");
            StubAI malformed = new StubAI(new FileManager(folder.toString()), "{}");
            expectAI(() -> malformed.generateSuggestions(request(true, new Phrase[0], new Feedback[0])), "bad provider reply");
            check(Files.notExists(log), "malformed reply is never logged");
            Suggestion[] suggestions = ai.generateSuggestions(request(true, new Phrase[0], new Feedback[0]));
            check(suggestions.length == 3 && Codes.SOURCE_AI.equals(suggestions[0].getSourceCode()), "three AI suggestions");
            String logText = Files.readString(log);
            check(logText.split("\\R").length == 4 && logText.contains(suggestions[2].getText()), "all three logged before return");
            check(!logText.contains("tomorrow, Ah Ma, yum cha"), "raw prompt stays out of log");
            GenerationRequest defaultStyle = new GenerationRequest("REQ-test", "P-test", "CP-test",
                    "FAMILY", "kopi", "", "", true, new Phrase[0], new Feedback[0]);
            check(ai.generateSuggestions(defaultStyle).length == 3, "blank optional preferences work");

            // Leave room for one reply row but too little for all three.
            long nearCapacity = FileManager.MAX_LOG_BYTES - 120;
            try (RandomAccessFile file = new RandomAccessFile(log.toFile(), "rw"))
            {
                file.setLength(nearCapacity);
            }
            AIServiceException fullLog = expectAI(
                    () -> ai.generateSuggestions(request(true, new Phrase[0], new Feedback[0])), "full log batch");
            check(fullLog.getMessage().contains("local AI log"), "a full log reports the logging failure");
            check(Files.size(log) == nearCapacity, "a rejected batch leaves no partial log rows");

            Files.delete(log);
            Files.createDirectory(log);
            expectAI(() -> ai.generateSuggestions(request(true, new Phrase[0], new Feedback[0])), "unwritable log");
        }
        finally
        {
            Files.deleteIfExists(log);
            Files.delete(folder);
        }
    }

    private static void checkFallback() throws Exception
    {
        check(Files.notExists(Path.of("config.txt")), "run from an isolated working directory");
        AppController app = Main.createController();
        app.selectSession(app.getFileManager().getUsers()[0], "P001", "CP001");
        Suggestion[] offline = app.requestSuggestions("kopi");
        check(offline.length > 0 && Codes.SOURCE_SAVED.equals(offline[0].getSourceCode()), "sharing off shows saved phrase");
        app.getFileManager().getProfile("P001").setAiSharingEnabled(true);
        Suggestion[] missingKey = app.requestSuggestions("kopi");
        check(missingKey.length > 0 && Codes.SOURCE_SAVED.equals(missingKey[0].getSourceCode())
                && app.getLastFallbackMessage().contains("config.txt"), "missing config shows saved phrase and a safe reason");
    }

    private static void checkConsentSnapshot() throws Exception
    {
        Path folder = Files.createTempDirectory("mywords-role-b-consent-");
        try
        {
            checkConsentSnapshot(folder);
        }
        finally
        {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder))
            {
                for (Path entry : entries)
                {
                    Files.delete(entry);
                }
            }
            Files.delete(folder);
        }
    }

    private static void checkConsentSnapshot(Path folder) throws Exception
    {
        FileManager files = new FileManager(folder.toString());
        DemoData.createIfEmpty(files);
        StubAI ai = new StubAI(files, ENVELOPE);
        AppController app = new AppController(files, ai, new MemoryManager(), new SuggestionService());
        User communicator = files.getUsers()[0];
        CommunicationProfile profile = files.getProfile("P001");
        app.selectSession(communicator, "P001", "CP001");

        RequestSnapshot sharingOff = app.captureRequest("kopi");
        profile.setAiSharingEnabled(true);
        check(Codes.SOURCE_SAVED.equals(app.requestSuggestions(sharingOff)[0].getSourceCode()) && ai.calls == 0,
                "turning on sharing after the click sends no request");

        RequestSnapshot sharingRevoked = app.captureRequest("kopi");
        profile.setAiSharingEnabled(false);
        check(Codes.SOURCE_SAVED.equals(app.requestSuggestions(sharingRevoked)[0].getSourceCode()) && ai.calls == 0,
                "turning off sharing before dispatch sends no request");

        profile.setAiSharingEnabled(true);
        RequestSnapshot oldClick = app.captureRequest("kopi");
        app.selectSession(communicator, "P001", "CP002");
        try
        {
            app.requestSuggestions(oldClick);
            throw new AssertionError("a click from the previous conversation should be rejected");
        }
        catch (InvalidInputException expected)
        {
            check(ai.calls == 0 && Files.notExists(folder.resolve("ai_log.txt")),
                    "a stale click sends zero mock requests and writes no log");
        }

        Suggestion[] current = app.requestSuggestions(app.captureRequest("speak now"));
        check(current.length == 3 && Codes.SOURCE_AI.equals(current[0].getSourceCode()) && ai.calls == 1,
                "a current consented click reaches the mock provider once");
    }

    private static GenerationRequest request(boolean consent, Phrase[] phrases, Feedback[] feedback)
    {
        return FAMILY_CASE.request(consent, phrases, feedback);
    }

    private record LanguageCase(String name, String context, String keywords, String style, String tone)
    {
        GenerationRequest request(boolean consent, Phrase[] phrases, Feedback[] feedback)
        {
            return new GenerationRequest("REQ-test", "P-test", "CP-test", context, keywords,
                    style, tone, consent, phrases, feedback);
        }
    }

    private static String quoteFixture(String text)
    {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static AIServiceException expectAI(Check action, String scenario) throws Exception
    {
        try
        {
            action.run();
            throw new AssertionError(scenario + " should fail");
        }
        catch (AIServiceException expected)
        {
            check(!expected.getMessage().contains("fictional-key"), scenario + " does not report the key");
            return expected;
        }
    }

    private static void check(boolean condition, String scenario)
    {
        if (!condition)
        {
            throw new AssertionError(scenario);
        }
        assertions++;
    }

    private interface Check
    {
        void run() throws Exception;
    }

    private static class StubAI extends AIHelper
    {
        private final String reply;
        private int calls;

        StubAI(FileManager files, String reply)
        {
            super(files);
            this.reply = reply;
        }

        @Override
        public String ask(String prompt)
        {
            calls++;
            return reply;
        }
    }
}
