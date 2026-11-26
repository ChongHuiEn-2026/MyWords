package mywords.ai;

import java.nio.file.Files;
import java.nio.file.Path;
import mywords.app.AppController;
import mywords.app.Main;
import mywords.exception.AIServiceException;
import mywords.model.Codes;
import mywords.model.Feedback;
import mywords.model.Phrase;
import mywords.model.Suggestion;
import mywords.storage.FileManager;

/** Offline checks. Uses fictional text and a stubbed provider response. */
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
    private static int assertions;

    public static void main(String[] args) throws Exception
    {
        Path config = Files.createTempFile("mywords-role-b-", ".txt");
        try
        {
            checkConfig(config);
            checkParser();
            checkPrompt();
            checkGeneration();
            checkFallback();
            System.out.println("Role B AI checks passed: " + assertions);
        }
        finally
        {
            Files.deleteIfExists(config);
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
            check(logText.lines().count() == 4 && logText.contains(suggestions[2].getText()), "all three logged before return");
            check(!logText.contains("tomorrow, Ah Ma, yum cha"), "raw prompt stays out of log");
            GenerationRequest defaultStyle = new GenerationRequest("REQ-test", "P-test", "CP-test",
                    "FAMILY", "kopi", "", "", true, new Phrase[0], new Feedback[0]);
            check(ai.generateSuggestions(defaultStyle).length == 3, "blank optional preferences work");

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

    private static GenerationRequest request(boolean consent, Phrase[] phrases, Feedback[] feedback)
    {
        return new GenerationRequest("REQ-test", "P-test", "CP-test", "FAMILY", "tomorrow, Ah Ma, yum cha",
                "Casual rojak", "Warm tone", consent, phrases, feedback);
    }

    private static String quoteFixture(String text)
    {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static void expectAI(Check action, String scenario) throws Exception
    {
        try
        {
            action.run();
            throw new AssertionError(scenario + " should fail");
        }
        catch (AIServiceException expected)
        {
            check(!expected.getMessage().contains("fictional-key"), scenario + " does not report the key");
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
