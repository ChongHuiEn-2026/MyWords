package mywords.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import mywords.ai.AIHelper;
import mywords.app.DemoData;
import mywords.storage.FileManager;

public class BrowserChecks {
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final String INVITE =
            "{\"intent\":\"INVITE\",\"text\":\"Jom yumcha with Ah Ma tomorrow?\"}";
    private static final String CHECK_PLAN =
            "{\"intent\":\"CHECK_PLAN\",\"text\":\"Are we having yumcha with Ah Ma tomorrow?\"}";
    private static final String WISH =
            "{\"intent\":\"EXPRESS_WISH\",\"text\":\"I feel like yumcha with Ah Ma tomorrow.\"}";
    private static final String DUPLICATE =
            "{\"intent\":\"INVITE\",\"text\":\"How about yumcha with Ah Ma tomorrow?\"}";
    private static String origin;
    private static String cookie = "";

    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("mywords-browser-checks");
        FileManager files = new FileManager(directory.toString());
        DemoData.createIfEmpty(files);
        AIHelper mock = new AIHelper(files) {
            @Override public String ask(String prompt) {
                if (prompt.contains("short check")) {
                    return reply(INVITE, CHECK_PLAN);
                }
                if (prompt.contains("duplicate check")) {
                    return reply(INVITE, DUPLICATE, WISH);
                }
                return reply(INVITE, CHECK_PLAN, WISH);
            }
        };
        try (BrowserMain server = BrowserMain.start(Path.of("MyWords.html"), files, mock, 0)) {
            origin = "http://127.0.0.1:" + server.port();
            require(call("demo", "").contains("Ah Ma"), "demo includes the Java conversation partners");
            String offline = call("suggest", "partner=CP001&keywords=kopi");
            require(offline.contains("\"source\":\"SAVED\"") && !Files.exists(directory.resolve("ai_log.txt")),
                    "sharing starts off and uses saved phrases without logging AI");
            call("consent", "enabled=true");
            String online = call("suggest", "partner=CP001&keywords=Ah+Ma+yumcha+tomorrow");
            require(online.contains("Jom yumcha with Ah Ma tomorrow?")
                    && online.contains("Are we having yumcha with Ah Ma tomorrow?")
                    && online.contains("I feel like yumcha with Ah Ma tomorrow."),
                    "the browser receives three distinct labeled meanings");
            Path log = directory.resolve("ai_log.txt");
            require(Files.readAllLines(log).size() == 4, "three AI results were written to the log");
            for (String bad : new String[] {"short", "duplicate"}) {
                String fallback = call("suggest", "partner=CP001&keywords=kopi+" + bad + "+check");
                require(fallback.contains("Add more keywords or try again.")
                        && fallback.contains("\"source\":\"SAVED\"") && !fallback.contains("\"source\":\"AI\""),
                        bad + " reply offers guidance and saved phrases without partial AI output");
                require(Files.readAllLines(log).size() == 4, bad + " reply does not write AI log rows");
            }
            call("profile", "style=Formal+English&tone=Polite&gender=Female&language=en&theme=dark&font=large");
            call("need-add", "topic=Others&label=Pasar+malam");
            call("manual", "partner=CP001&keywords=pasar+malam&text=Can+we+go+to+pasar+malam%3F&reuse=false");
            require(call("phrases", "partner=CP001").contains("Can we go to pasar malam?"), "shows Java saved phrases");
            call("logout", "");
            require(call("session", "").contains("\"user\":null"), "logout removes the active session");
            String signup = call("signup", "email=fictional%40example.test&password=fictional-test-password&name=Fictional+Speaker&gender=Female");
            require(signup.contains("Fictional Speaker") && !signup.contains("fictional-test-password"),
                    "signup returns a Java profile without exposing passwords");
            require(!Files.readString(directory.resolve("browser.txt")).contains("fictional-test-password"),
                    "password is hashed on disk");
            expectFailure("phrases", "partner=CP001", 400);
            call("partner-add", "category=Others&name=Fictional+Helper&role=Helper&tone=Warm");
            require(call("session", "").contains("Fictional Helper"), "the fourth Role D category has Java partners");
            call("logout", "");
        }
        FileManager reloaded = new FileManager(directory.toString());
        reloaded.loadAll();
        try (BrowserMain server = BrowserMain.start(Path.of("MyWords.html"), reloaded, mock, 0)) {
            origin = "http://127.0.0.1:" + server.port();
            String user = call("demo", "");
            require(user.contains("Formal English") && user.contains("Pasar malam"), "preferences and saved needs reload");
            require(call("phrases", "partner=CP001").contains("Can we go to pasar malam?"), "phrase reloads");
            call("logout", "");
            expectFailure("login", "email=fictional%40example.test&password=wrong-password", 400);
            require(call("login", "email=fictional%40example.test&password=fictional-test-password").contains("Fictional Helper"),
                    "credentials and their partners reload together");
        }
        System.out.println("MyWords browser integration checks passed.");
    }

    private static String reply(String... options) {
        String content = ("[" + String.join(",", options) + "]").replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"choices\":[{\"message\":{\"content\":\"" + content + "\"}}]}";
    }

    private static String call(String action, String body) throws Exception {
        return request(action, body, 200);
    }

    private static void expectFailure(String action, String body, int status) throws Exception {
        request(action, body, status);
    }

    private static String request(String action, String body, int status) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(origin + "/api/" + action))
                .header("Origin", origin)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (!cookie.isEmpty()) request.header("Cookie", cookie);
        HttpResponse<String> response = HTTP.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != status) throw new AssertionError(action + " returned HTTP " + response.statusCode());
        String setCookie = response.headers().firstValue("Set-Cookie").orElse("");
        if (!setCookie.isEmpty()) cookie = setCookie.split(";", 2)[0];
        return response.body();
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
