# Role B (Role 2): AI Integration Developer

Owns the complete AI boundary: safe configuration, request construction, HTTP communication, response parsing, validation, and AI error reporting. All AI requests must follow the lecturer's Java constraints.

## Main programming ownership

- `AIHelper.java`: implement the required `String ask(String prompt)` gateway using `java.net.HttpURLConnection`; set finite timeouts, inspect status codes, bound response size, and never expose keys or raw sensitive error bodies.
- `AIConfig.java`: read the key and agreed endpoint/model settings from a local `config.txt`; document evaluator setup and keep real credentials out of source control.
- `PromptBuilder.java`: construct candidate-generation prompts from the approved request snapshot. Label saved phrases and rejected/corrected examples as data, preserve selected language, and prohibit invented intent.
- `AIResponseParser.java`: construct and parse the agreed JSON using Java String/character operations only, validate exactly three distinct nonempty candidates, and reject malformed or oversized replies.
- `AIServiceException.java`: represent missing configuration, offline/HTTP/timeout and invalid-response failures with safe messages. Coordinate the catch/fallback behavior with A and D.
- Coordinate the AI display log boundary with A/FileManager: each AI reply shown to a user is logged in `.txt` before it is displayed; no keys or prompts are logged.

## Contract and safety boundaries

- Use `HttpURLConnection`, `AIHelper.ask(String prompt)` and no external JSON library or SDK that hides the required work.
- Receive only the consented, profile/context-scoped request assembled by the controller and retrieval logic.
- Do not decide whether a candidate is the user's intended meaning. Return candidates for the user to confirm, edit or reject.
- Keep an offline path usable through saved phrases/manual composition when AI fails; do not label a mock as a live response.

## Acceptance evidence

Show a clearly labeled live or mock response and exercise missing-key, offline, HTTP error and malformed-response handling. Explain the parser and demonstrate that the GUI remains usable through fallback. Record only checks actually performed.

## How the Java flow works

1. `AIConfig.load` reads the six settings from the local file in the working directory. Each setting has a place in a plain array. The code checks that the endpoint uses HTTPS and that the timeout and response limits are positive and bounded.
2. `PromptBuilder.build` writes the selected context, language style, partner tone and keywords as quoted data. It adds up to three permitted saved phrases and two permitted feedback examples from that conversation. Casual wording can keep expressions like `tapau` and `yum cha`.
3. `AIHelper.ask` sends the HTTPS POST with `HttpURLConnection` and returns the provider's JSON response. It uses the API key in the authorization header. The configured timeouts and byte limit bound the read, and messages for failed requests contain only safe summaries.
4. `AIResponseParser.extractContent` finds `choices[0].message.content`. The cursor is an integer that advances through the JSON one character at a time. That lets `parseCandidates` recognize a comma inside quotes as part of a sentence, decode escapes, and reject replies without three distinct, usable candidates.
5. `AIHelper.generateSuggestions` checks consent before sending and asks `FileManager` to append each validated suggestion before returning it. If config, networking, parsing, or logging fails, `AppController` can show saved phrases.

## Evaluator setup

Ask Roles A and D to bind the Swing request to a single session and its consent before enabling a live provider. Currently the worker reads mutable keywords and the controller reads the active conversation separately. Changing conversations at that moment can pair old input with a different profile's consent. The enable screen should explain provider transmission and local AI-output logging together before asking for consent.

Copy `config.example.txt` to `config.txt` in the working directory used by Java. Set the full HTTPS Chat Completions URL, model ID and key locally. The service must accept a Bearer API key and a JSON POST with `model` and `messages`, then return one choice with the generated text in `choices[0].message.content`.

The default values in the template are 10,000 ms to connect, 20,000 ms to read and 65,536 response bytes. The application uses saved phrases while the local file is missing or contains placeholders. Only the communicator should enable AI sharing after reading what will be sent. Use fictional demo data. Keep `config.txt` and runtime `data/` local.

For the saved-phrase demo, run `mywords.app.Main`, choose Mei and Ah Ma, press **Use this conversation**, type `kopi`, and press **Get suggestions**.

## Run the Role B checks

From the repository root on macOS or Linux with JDK 17 or later:

```sh
classes=$(mktemp -d)
javac --release 17 -encoding UTF-8 -d "$classes" $(find src tests -name '*.java')
(cd "$classes" && java -cp . mywords.ai.RoleBAIChecks)
```

The checks use a temporary working directory for fictional data and a stub provider reply. Log rows are written through the existing `FileManager` API one at a time. If a later write fails or Swing discards a stale response, the log can contain rows the user did not see. Role A can address partial writes with a batch append, coordinated with Roles B and D.

## Check the API with fictional input

From the repository root, run the compile commands above and then run:

```sh
java -cp "$classes" mywords.ai.RoleBAIChecks --live
```

This sends one fictional request to the local configuration's AI endpoint using the same Role B code as the app. It sends no saved memories and uses a temporary directory for the log. It checks and displays three replies before deleting the temporary log. Use this to verify the endpoint and model independently of Swing. For a full Swing live demo, use the consent flow once the controller and screen bind requests to the same conversation.
