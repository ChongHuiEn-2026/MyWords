# Proposed architecture

**Proposal v0.1, not agreed or implemented.** Swing is recommended to keep the GUI and backend in one Java project. The PDF permits other frontend tools; no frontend choice has been confirmed. Java version, IDE/project layout and package name need agreement before coding.

Role ownership below is updated for the current MyWords communication-aid scope. This is a proposed allocation pending team agreement. Each role owns a substantial programming module; shared integration and group deliverables are additional common work.

## Boundaries

```text
Swing screens (D) -> AppController (A) -> models / FileManager (A)
                                    -> SuggestionService / MemoryManager (C)
                                    -> AIHelper (B) -> external AI provider
AIHelper / controller -> FileManager (A) -> local .txt files
```

Swing action handlers call Java backend methods; they must not contain duplicate storage, prompt or business logic. `AIHelper.ask` is the only network gateway for AI requests. `MemoryManager` selects permitted examples; `AIHelper` formats them into a prompt. File access remains centralized in `FileManager`.

Use a background worker for the network operation so the GUI remains responsive. `SwingWorker` is a proposed standard-library option, not an external framework. Read/update Swing components on the event-dispatch thread. Copy request data before the background call; serialize state changes and file writes. Reject a late response if profile/context changed or the user cancelled; never save it as feedback automatically. A simple “one AI request at a time” rule is sufficient for the prototype.

## Proposed classes and ownership

| Class | Owner | Responsibility |
| --- | --- | --- |
| `Main` | A | Startup, file loading and launching GUI |
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
| Swing screen classes | D | User-facing navigation, composition, review, feedback and memory controls |
| `InputValidator` | D | UI input validation and useful error details |
| `ReportGenerator` / `ReportWriter` | D | User-selected, confirmed-phrase summary and text export |
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
