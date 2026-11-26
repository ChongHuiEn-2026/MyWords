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
    private static String origin;
    private static String cookie = "";

    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("mywords-browser-checks");
        FileManager files = new FileManager(directory.toString());
        DemoData.createIfEmpty(files);
        AIHelper mock = new AIHelper(files) {
            @Override public String ask(String prompt) {
                return "{\"choices\":[{\"message\":{\"content\":\"[\\\"Can you tapau kopi for me later?\\\",\\\"Can tapau my kopi later?\\\",\\\"Could you get me takeaway kopi later?\\\"]\"}}]}";
            }
        };
        try (BrowserMain server = BrowserMain.start(Path.of("MyWords.html"), files, mock, 0)) {
            origin = "http://127.0.0.1:" + server.port();
            require(call("demo", "").contains("Ah Ma"), "demo includes the Java conversation partners");
            String offline = call("suggest", "partner=CP001&keywords=kopi");
            require(offline.contains("\"source\":\"SAVED\"") && !Files.exists(directory.resolve("ai_log.txt")),
                    "sharing starts off and uses saved phrases without logging AI");
            call("consent", "enabled=true");
            String online = call("suggest", "partner=CP001&keywords=kopi+tapau+later");
            require(online.contains("\"source\":\"AI\"") && online.contains("tapau"),
                    "consented data passes through the Role B helper");
            require(Files.readAllLines(directory.resolve("ai_log.txt")).size() == 4,
                    "three AI results were written to the log");
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
