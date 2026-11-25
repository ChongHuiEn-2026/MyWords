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
