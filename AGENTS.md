# MyWords: shared instructions for AI assistants

Version 0.3, 26 November 2026. Every AI tool used on this repository must follow this file. It does not override the tool's own system rules or the current user's explicit instructions.

## Read first, in this order
1. `docs/TEAM_HANDBOOK.md`: **the current, as-built contract.** Real class names, method signatures, codes, file formats, flows and each role's remaining tasks. When it disagrees with another doc, the handbook wins.
2. The user's own role file: `docs/ROLE_A.md` (core data, storage, controller), `docs/ROLE_B.md` (AI), `docs/ROLE_C.md` (communication logic) or `docs/ROLE_D.md` (UI and report).
3. Background only when needed: `docs/PROJECT_CONTEXT.md`, `docs/REQUIREMENTS.md`, `docs/PRIVACY.md`, `docs/OPEN_QUESTIONS.md`, `docs/DECISIONS.md`.
4. The actual Java files you will touch under `src/mywords/`. Inspect them before editing.

**Writing the report, planning the video or packaging the submission?** Follow `docs/REPORT_GUIDE.md` exactly (owners, terminology, figure numbers, shared facts).

`latest_roles/` is an archive. Ignore it. `docs/SUBMISSION.md` is the old plan; `REPORT_GUIDE.md` replaces it. `docs/INTEGRATION.md` v0.1 was the proposal. The handbook lists what was actually built.

## Project
MyWords: Let Me Finish My Sentence. A UM WIX1002 FOP Hackathon project, Track 3 Healthcare. It is a communication aid with Role D’s HTML frontend and a Java backend for Malaysians with expressive aphasia. Typed keywords produce up to three candidate sentences (AI or saved phrases). The communicator approves, edits or rejects them, and only explicitly permitted records are reused. **Malaysian slang and rojak phrasing are core MVP features** (handbook section 7). It is a student prototype: not diagnostic, not clinically validated, and it uses retrieval-based personalization, not model training.

Working deadline: **10 December 2026, 11:00 PM MYT** (confirm on the portal). Milestones: offline GUI 29 Nov, AI and memory 5 Dec, development target 7 Dec, freeze 8 Dec.

## Roles (development roles, not app users)
| Role | Package(s) | Owns |
| --- | --- | --- |
| A | `app`, `model`, `storage`, `exception` | Main (browser launcher), ConsoleMain/ConsoleMenu, AppController, RequestSnapshot, DemoData, User hierarchy, FileManager, RecordCodec |
| B | `ai` | AIHelper (`String ask(String prompt)`), AIConfig, PromptBuilder, AIResponseParser, GenerationRequest |
| C | `logic` (+ `Feedback`, `Suggestion` models) | SuggestionService, MemoryManager, CommunicationRules, FeedbackManager |
| D | HTML, JavaScript, `web`, `ui` | MyWords.html, browser.js, BrowserMain, BrowserApp, BrowserForm, BrowserRecords, ReportGenerator |

Ask which role the user has. Edit only that role's files. If another role's class must change, say so and let the user coordinate. Never silently rewrite a teammate's module.

## Hard coding rules
- Java 17 and a GUI are mandatory. The browser GUI is `MyWords.html`, served by the Java app on the local computer. All business logic, storage, exceptions and AI processing are in Java.
- Store objects only in **plain arrays with int counters**, using loops for add/search/update/delete. No `ArrayList`, `LinkedList`, `HashMap`, `List`, `Map`, `Set` or streams-to-collections. No wildcard imports.
- Every file starts with its `package mywords.<pkg>;` line.
- **Do not rename or change the parameters, return type or exceptions of any public method listed in handbook section 4** without a recorded team decision. Update the handbook in the same commit.
- Demonstrate a real inheritance hierarchy with polymorphism (`User` → `Communicator`/`Supporter`, `canEditProfile()`), and throw and catch the custom exceptions `InvalidInputException`, `StorageFullException` and `AIServiceException`.
- Persist all data in `.txt` files via `FileManager` only. Get new IDs from `fileManager.nextId(prefix)`.
- Every AI HTTP request goes through `AIHelper.ask` using `java.net.HttpURLConnection`. Build and parse JSON with String/char code. No JSON libraries and no AI SDKs.
- Read keys from local `config.txt` (keys listed in handbook section 5). Never hardcode, log, commit or paste real keys. A missing key or no network must throw `AIServiceException`, which the caller catches so that saved phrases still work. Log every AI reply shown to the user before it is displayed, with one `FileManager.appendAILogBatch(...)` call per request (all rows or none).

## Behaviour rules
- Never auto-select, send or save an AI interpretation. Approving a sentence, saving it and allowing AI reuse are separate choices.
- Only the Communicator can change AI-sharing consent. A Supporter may edit settings only when authorised.
- Keep profiles and contexts separate (no HEALTHCARE memory in FAMILY suggestions). Use fictional data only.
- Run the HTML frontend through the local Java server described in `docs/INTEGRATION.md`. Coordinate any remote hosting, new speech services, microphone input, model fine-tuning or unrelated features through a recorded decision.
- Never claim a feature works, a test passed or accuracy improved without evidence.

## Working style
- Explain code at beginner level. Prefer small methods students can present.
- Before finishing: the whole project compiles, `mywords.web.BrowserMain` loads the HTML in a browser, and your feature works with the demo data.
- Handoff note: role, files changed, public methods added or changed, checks actually run, open issues, which teammate is affected.
