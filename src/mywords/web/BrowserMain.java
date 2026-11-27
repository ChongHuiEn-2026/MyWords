package mywords.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Locale;
import mywords.ai.AIHelper;
import mywords.app.DemoData;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.storage.FileManager;

/** Runs Role D's HTML and the Java backend together on the local computer. */
public final class BrowserMain implements AutoCloseable {
    private final HttpServer server;

    private BrowserMain(HttpServer server) {
        this.server = server;
    }

    public static void main(String[] args) throws IOException, InvalidInputException, StorageFullException {
        int port = args.length == 0 ? 8080 : Integer.parseInt(args[0]);
        FileManager files = new FileManager("data");
        files.loadAll();
        DemoData.createIfEmpty(files);
        BrowserMain browser = start(Path.of("MyWords.html"), files, new AIHelper(files), port);
        Runtime.getRuntime().addShutdownHook(new Thread(browser::close));
        System.out.println("MyWords is ready at http://127.0.0.1:" + browser.port() + "/");
    }

    static BrowserMain start(Path html, FileManager files, AIHelper ai, int port)
            throws IOException, InvalidInputException, StorageFullException {
        byte[] page = Files.readAllBytes(html);
        BrowserApp app = new BrowserApp(files, ai);
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 16);
        String host = "127.0.0.1:" + server.getAddress().getPort();
        server.createContext("/", exchange -> serve(exchange, host, page, html, app));
        server.start();
        return new BrowserMain(server);
    }

    int port() {
        return server.getAddress().getPort();
    }

    @Override public void close() {
        server.stop(0);
    }

    private static void serve(HttpExchange exchange, String host, byte[] page, Path html, BrowserApp app)
            throws IOException {
        try (exchange) {
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
            exchange.getResponseHeaders().set("Referrer-Policy", "no-referrer");
            exchange.getResponseHeaders().set("Content-Security-Policy",
                    "default-src 'self'; script-src 'self' 'unsafe-inline'; "
                    + "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; "
                    + "font-src 'self' https://fonts.gstatic.com; connect-src 'self'; "
                    + "img-src 'self' data:; frame-ancestors 'none'; form-action 'self'");
            if (!host.equals(exchange.getRequestHeaders().getFirst("Host"))) {
                error(exchange, 403, "Open the local MyWords address.");
                return;
            }
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if ("GET".equals(method) && ("/".equals(path) || "/MyWords.html".equals(path))) {
                send(exchange, 200, "text/html; charset=UTF-8", page);
            } else if ("GET".equals(method) && "/mywords.css".equals(path)) {
                send(exchange, 200, "text/css; charset=UTF-8", Files.readAllBytes(html.resolveSibling("mywords.css")));
            } else if ("GET".equals(method) && "/browser.js".equals(path)) {
                send(exchange, 200, "text/javascript; charset=UTF-8", Files.readAllBytes(html.resolveSibling("browser.js")));
            } else if (path.startsWith("/api/") && "POST".equals(method)) {
                api(exchange, host, path.substring(5), app);
            } else {
                error(exchange, 404, "Page not found.");
            }
        }
    }

    private static void api(HttpExchange exchange, String host, String action, BrowserApp app)
            throws IOException {
        String type = exchange.getRequestHeaders().getFirst("Content-Type");
        if (!("http://" + host).equals(exchange.getRequestHeaders().getFirst("Origin"))) {
            error(exchange, 403, "Open MyWords from its local server.");
            return;
        }
        if (type == null || !type.toLowerCase(Locale.ROOT).startsWith("application/x-www-form-urlencoded")) {
            error(exchange, 415, "Expected a form submission.");
            return;
        }
        byte[] body = exchange.getRequestBody().readNBytes(8193);
        if (body.length > 8192) {
            error(exchange, 413, "The request is too large.");
            return;
        }
        try {
            BrowserForm form = new BrowserForm(new String(body, StandardCharsets.UTF_8));
            BrowserApp.Reply result = app.run(action, form, cookie(exchange));
            if (result.cookie() != null) {
                exchange.getResponseHeaders().set("Set-Cookie", "mywords=" + result.cookie()
                        + "; Path=/; HttpOnly; SameSite=Strict" + (result.cookie().isEmpty() ? "; Max-Age=0" : ""));
            }
            send(exchange, 200, "application/json; charset=UTF-8", result.json().getBytes(StandardCharsets.UTF_8));
        } catch (InvalidInputException | StorageFullException e) {
            error(exchange, 400, e.getMessage());
        } catch (IllegalArgumentException e) {
            error(exchange, 400, "Check the form fields.");
        } catch (IOException | GeneralSecurityException e) {
            error(exchange, 500, "MyWords could not complete the request. Check local storage and try again.");
        }
    }

    private static String cookie(HttpExchange exchange) {
        String value = exchange.getRequestHeaders().getFirst("Cookie");
        if (value != null) {
            for (String part : value.split(";")) {
                String field = part.strip();
                if (field.startsWith("mywords=")) return field.substring("mywords=".length());
            }
        }
        return "";
    }

    private static void error(HttpExchange exchange, int status, String message) throws IOException {
        String json = "{\"error\":" + BrowserForm.quote(message) + "}";
        send(exchange, status, "application/json; charset=UTF-8", json.getBytes(StandardCharsets.UTF_8));
    }

    private static void send(HttpExchange exchange, int status, String type, byte[] bytes) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }
}
