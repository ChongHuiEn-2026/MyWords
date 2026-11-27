# MyWords architecture

**Submission interface:** HTML and JavaScript served by a local JDK 17+ server. See the [browser integration contract](INTEGRATION.md#html-gui-for-submission) and [team handbook](TEAM_HANDBOOK.md) for the current draft.

Role ownership below is updated for the current MyWords communication-aid scope. This is a proposed allocation pending team agreement. Each role owns a substantial programming module; shared integration and group deliverables are additional common work.

## Boundaries

```text
MyWords.html + browser.js -> BrowserMain / BrowserApp -> AppController (A)
                                                     -> models / FileManager (A)
AppController -> SuggestionService / MemoryManager (C)
              -> AIHelper (B) -> external AI provider
AIHelper / controller -> FileManager (A) -> local .txt files
```

Browser controls submit local HTTP requests. Java handles storage, validation, prompt construction and business logic. `AIHelper.ask` is the only network gateway for AI requests. `MemoryManager` selects permitted examples; `AIHelper` formats them into a prompt. File access remains centralized in `FileManager`.

The browser awaits the request asynchronously. Java captures a request snapshot and checks session and consent before dispatching to the provider. The browser checks the active conversation before showing returned candidates. Feedback is a separate user action.

## Proposed classes and ownership

| Class | Owner | Responsibility |
| --- | --- | --- |
| `Main` | A | Compatibility launcher for the HTML server and controller factory for the Scanner menu |
| `User` (abstract) | A | Shared user identity and abstract `canEditProfile()` behavior |
| `Communicator` | A | Overrides role behavior for their own profile |
| `Supporter` | A | Overrides behavior based on explicit local authorization |
| `AppController` | A | Active session, orchestration, integration and caught errors |
| `CommunicationProfile` | A | Profile preferences and AI-sharing setting, coordinated with D |
| `ConversationPartner` | A | Profile-bound partner/context and user-selected style |
| `Phrase` | A | Confirmed expression, keywords, context and reuse permission |
| `FileManager` | A | Arrays/counters, CRUD, schema, persistence and AI display-log writes |
| `GenerationRequest` | B, coordinated with A/C | Immutable request snapshot of allowed input/context |
| `AIHelper` | B | `HttpURLConnection` gateway and `String ask(String prompt)` |
| `AIConfig` / `PromptBuilder` / `AIResponseParser` | B | Configuration, prompt construction and String-based JSON handling |
| `AIServiceException` | B | Missing config/key, service/network/response failures |
| `SuggestionService` | C | Candidate workflow and manual/saved phrase fallback |
| `Feedback` / `FeedbackManager` | C | Decisions, explicit reasons, confirmed corrections and consent |
| `MemoryManager` | C | Filter and rank permitted phrases/feedback without Collections |
| `CommunicationRules` | C | Meaning-preservation and context/consent eligibility rules |
| `BrowserMain` / `BrowserApp` | D / integration | Serve the HTML and handle requests through Java |
| `BrowserForm` / `BrowserRecords` | D / integration | Decode form data and manage browser-specific persisted records through FileManager |
| `ReportGenerator` | D | User-selected, confirmed-phrase summary and text export |
| `InvalidInputException` | A, used by all | Invalid fields, unsupported codes, invalid relationships |
| `StorageFullException` | A | Array/log/data capacity limits |

This table proposes at least 20 meaningful classes including GUI classes. Count actual classes in final code rather than treating this table as fulfillment of O03. The exact breakdown can change when interfaces are agreed, but each role should retain a similarly sized coding commitment.

## Inheritance and polymorphism demonstration

Proposed `User.canEditProfile()` returns true for a communicator editing their own profile, and for a supporter only while authorized assistance is enabled. Call the overridden method through a `User` reference in a real controller check. Pair it with an active-profile ownership check; the method alone is not authorization. Document field and method signatures in the final UML.

This is a local demonstration design. Selecting a profile is not secure authentication; do not claim patient-grade access control or security without implementing and evaluating it.

## Failure boundaries

| Failure | Proposed throw site | Proposed catch / user behavior |
| --- | --- | --- |
| Empty keywords, invalid IDs, invalid decision/reason | Controller / validation / model boundary: `InvalidInputException` | A shows clear correction prompt; retains input |
| Array/log cap reached | FileManager: `StorageFullException` | A offers review/delete or retry; no partial record silently added |
| Missing key, offline, HTTP failure, timeout, invalid AI format | AIHelper: `AIServiceException` | A offers saved phrases/manual composition; no fake AI success |
| File read/write permission or corruption | FileManager: `IOException` (invalid file structure may wrap a validation cause) | A reports affected file; preserves existing valid data; avoids overwriting corrupt source |

File I/O errors are distinct from unavailable AI. A failed save must not be described as “memory updated.” No broad catch should swallow an exception and proceed as if the operation succeeded.

## Planned compile and integration approach

First agree on class names/signatures and create compilable shells in the actual project. C supplies deterministic fictional scenarios and retrieval expectations; B supplies an explicitly labeled mock AI path for UI development; D builds the UI against the agreed interfaces; A wires the modules together. A mock must never be represented as live service output. Once real code exists, document exact compile/run commands, required Java version and configuration in the project README.
