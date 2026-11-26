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
2. `PromptBuilder.build` writes the selected context, language style, partner tone and keywords as quoted data. It adds up to three permitted saved phrases and two permitted feedback examples from that conversation. It asks for different phrasings of the same action, recipient and timing, and for local expressions like `tapau` and `yum cha` in casual wording.
3. `AIHelper.ask` sends the HTTPS POST with `HttpURLConnection` and returns the provider's JSON response. It uses the API key in the authorization header and requests a low sampling temperature of `0.1` to reduce wording variability. The configured timeouts and byte limit bound the read, and messages for failed requests contain only safe summaries. [ILMU Chat Completions parameters](https://docs.ilmu.ai/docs/api/chat-completions#parameters)
4. `AIResponseParser.extractContent` finds `choices[0].message.content`. The cursor is an integer that advances through the JSON one character at a time. That lets `parseCandidates` recognize a comma inside quotes as part of a sentence, decode escapes, and reject replies without three distinct, usable candidates.
5. `AIHelper.generateSuggestions` checks the request's consent flag and asks `FileManager.appendAILogBatch` to log all three validated suggestions before returning them. If the batch does not fit, the log keeps its existing rows and adds none from that request. If config, networking, parsing, or logging fails, `AppController` can show saved phrases.

## Evaluator setup

Swing captures the keywords, profile, partner and AI-sharing setting together when **Get suggestions** is clicked. The controller checks the captured conversation and consent again before calling the AI helper. The remaining Swing task is for Role D to explain provider transmission and local suggestion logging together in the enable dialog. The console already shows both disclosures.

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

The checks use fictional data and a stub provider reply. They verify that all four handbook language cases pass their keywords, context, style and partner tone into the prompt. They also cover a stale click, a change to consent before dispatch, and a log with room for only one of the three suggestions. A request blocked by a conversation switch before dispatch generates no mock-provider call or log. If the user changes conversations while the AI is already answering, Swing may discard the result after the validated suggestions have been logged locally.

## Check the API with fictional input

From the repository root, run the compile commands above and choose one of the commands below.

```sh
java -cp "$classes" mywords.ai.RoleBAIChecks --live friends-casual
java -cp "$classes" mywords.ai.RoleBAIChecks --live friends-formal
java -cp "$classes" mywords.ai.RoleBAIChecks --live family
java -cp "$classes" mywords.ai.RoleBAIChecks --live healthcare
```

`--live` on its own runs the Family case. Each command sends one fictional request to the configured provider using the same Role B code as the app. It sends no saved memories and uses a temporary directory for the log. It checks the reply format and logging, prints the suggestions, then deletes the temporary log.

Review all three replies against [handbook section 7](TEAM_HANDBOOK.md#7-malaysian-slang-and-rojak-core-mvp-feature-restored). The casual Friends case should keep `tapau`. The formal Friends case should keep the takeaway request and `nasi lemak` in a formal register. The Family case should preserve `tomorrow`, `Ah Ma` and `yum cha`. The Healthcare case should ask to speak with a nurse now. In every case, look for new details the keywords did not supply. This review is a separate check from passing the parser.

### Live wording review, 26 November 2026

The final run used ILMU Mini v3.3 (`ilmu-mini-v3.3`) with one fictional request per case and no memories. All four runs returned three distinct, parsed and logged suggestions. Earlier runs also passed the format check but introduced extra people, meetings or unrelated requests. The team still needs to agree on the demo model.

| Case | What the final replies did | Wording review |
| --- | --- | --- |
| Casual Friends | All three kept `tapau`, `nasi lemak` and `later`. One changed the request to the speaker getting the food. | Two of three preserved the request. |
| Formal Friends | Two used English takeaway wording, and one kept `tapau`. All three changed the request into the speaker's plan. | None clearly preserved both the intended request and the selected register. |
| Family | All three kept `tomorrow`, `Ah Ma` and `yum cha` without adding a separate plan. | Three of three fit the case. |
| Healthcare | All three asked for immediate speech and added no symptoms or advice. They addressed the nurse directly, with one using the more forceful wording “I require”. | Preferred framing and tone still need review. |

The integrated Swing flow has passed with a local HTTPS mock and offline saved phrases. The live Swing test with fictional data is pending Role D's enable-dialog update.
