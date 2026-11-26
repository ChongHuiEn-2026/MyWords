# MyWords Report, Video and Submission Standard (v1.0)

Last updated: **26 November 2026 (MYT)** by Role A. This file **replaces the planning tables in `SUBMISSION.md`**, whose owner letters are out of date.

**Purpose:** all four members, and the AI assistants they use, write the report, record the video and package the submission in **one consistent way**. Every fact below was checked against the code on `main` (plus Role B's PR #4 for the AI parts). When the code changes, update this file in the same commit.

Read together with: `AGENTS.md` (rules), `docs/TEAM_HANDBOOK.md` (real API and status), lecturer template `sources/FOP_Project_Report_Template.pdf`.

---

## 0. Starter prompt for your AI (copy, replace X and the sections)

> I am Role X of the MyWords FOP project. Read `AGENTS.md`, `docs/TEAM_HANDBOOK.md` and `docs/REPORT_GUIDE.md`. Write **report sections [numbers]** that the guide assigns to my role. Follow the guide exactly: its section order and headings, its terminology (section 2), its figure numbers (section 5), its tables and its code-snippet rules. Use only facts and code that exist in the repository; quote code from the current files, never invent code, results or numbers. Write in clear academic English at a first-year level. Mark anything you could not verify as `[TO CHECK: ...]`. Output Markdown that I can paste into the Word template.

Each member writes **their own sections** in a separate file `report/drafts/section-<numbers>-<role>.md` and pushes it. Role A merges the drafts into the template (`.docx`) at the end.

---

## 1. Who writes what (owner = writes the first draft; reviewer = checks it)

| Template section | Owner | Reviewer | Content (source of truth) |
| --- | --- | --- | --- |
| Cover page | A | all | Title **MyWords – Let Me Finish My Sentence**; Track **3: Healthcare**; frontend **Java Swing**; group/lecturer/matric `[TO CHECK]` |
| 1.1 Project Overview | A | C | One paragraph. Use the overview text in section 3.1 below |
| 1.2 Objectives | A | all | Use the 5 objectives in section 3.2 |
| 1.3 Scope and User Roles | A | D | Communicator and Supporter permissions + business rules (section 3.3) |
| 2.1 Class Diagram | A | all | UML image `report/figures/uml.png` (section 3.4) |
| 2.2 Class Descriptions | A (+ each role checks its rows) | all | Table in section 3.4 |
| 2.3 Inheritance and Polymorphism | A | C | `User` → `Communicator` / `Supporter` (section 3.5) |
| 2.4 Data Files | A | B | Table in section 3.6 |
| 3.1 Use of Arrays | A | C | `FileManager` add / delete-with-shift snippets |
| 3.2 Key Methods and Business Rules | each role, 2–3 methods each | A merges | Method list in section 3.7 |
| 3.3 Custom Exception Handling | A | all | Table in section 3.8 |
| 3.4 File Input and Output | A | B | Save (temp file + replace), load (validate, then swap), rollback, AI log |
| 3.5 Frontend and Connection | D | A | Screens and navigation; screen → `AppController` → backend; `SwingWorker` + `RequestSnapshot`; Scanner text menu shares the same backend |
| 3.6 AI Feature | B | C, D | What is sent, `AIHelper.ask`, manual JSON, 3 suggestions, log, fallback, consent; one fictional input → output example |
| 4. Screenshots | each role takes the figures assigned in section 5 | A | Fixed figure list, section 5 |
| 5. Requirements Checklist | A | all | Table in section 3.9 |
| 6. Challenges Faced | each role adds 1 real challenge | A merges | Real list in section 3.10 |
| 7. Conclusion | C | all | Achievements, learning, future work, honest limitations (handbook section 11) |
| 8. Individual Contribution | each member writes own row | A | Must match git history (section 3.11) |
| 9. References | each role adds what they actually used | A merges | APA 7; real sources only |

---

## 2. Writing standard (everyone, every AI)

**Terminology: use exactly these words.**

| Use | Meaning | Do not write |
| --- | --- | --- |
| Communicator | the person with aphasia who uses MyWords | patient, user (alone) |
| Supporter | family member or carer linked to a profile | admin, caregiver account |
| communication profile | a Communicator's settings (language style, tone, AI sharing) | account |
| conversation partner | who they are talking to, with a context: FAMILY, FRIENDS or HEALTHCARE | contact |
| candidate sentence / suggestion | one of up to 3 possible sentences shown | answer, prediction |
| saved phrase | a sentence the Communicator confirmed and chose to save | memory (alone) |
| feedback | an APPROVE / EDIT / REJECT decision with a reason | rating |
| AI sharing | profile-level consent to send data to the AI service | AI mode |
| AI reuse | per-record consent to use a phrase/feedback as an AI example | training |
| fallback | showing saved phrases when AI is off or unavailable | offline AI |

**Claims.** MyWords is a **student prototype**, not a medical device. Do not claim clinical benefit, diagnosis, accuracy improvement or that the AI "learns"; it uses **retrieval of user-confirmed examples, not model training**. Only say a feature works if there is a screenshot or a passed check.

**Style.** English, third person ("The system…", "The Communicator…"). Past tense for what we built and tested, present tense for how the system works. One idea per paragraph.

**Code snippets.** At most **15 lines**, copied from the current file, Allman braces as in the source, with a caption line under them: `Code 3: FileManager.removePhraseAt() – shifts elements left after deletion (src/mywords/storage/FileManager.java)`. Number code snippets `Code 1, 2, …` in report order.

**Tables.** Use the template's own table columns. Class, method, file and code names in `monospace`.

**Data.** Fictional demo data only (Mei, Ah Hui, Ah Ma, Dr Lim, Ali). Never show a real API key, `config.txt` contents, or a real person's data.

---

## 3. Shared facts (copy from here, do not re-invent)

### 3.1 Overview (for 1.1)
MyWords – Let Me Finish My Sentence is a Java communication aid for Malaysians with expressive aphasia, who often know what they want to say but cannot form the full sentence. The Communicator types a few keywords, including Malaysian slang or mixed-language (rojak) words, and MyWords offers up to three candidate sentences, either from an AI service or from the Communicator's own saved phrases. The Communicator chooses, edits or rejects each sentence; nothing is sent or saved automatically. Confirmed sentences can be saved and reused offline. Each conversation partner (family, friends or healthcare) has its own context, and records from one context never appear in another. The system has a Java Swing GUI and a Scanner text menu, which share the same Java backend and `.txt` data files.

### 3.2 Objectives (for 1.2)
1. Turn a few keywords, including Malaysian slang and rojak, into up to three candidate sentences.
2. Keep the Communicator in control: approve, edit or reject every sentence; nothing is chosen, sent or saved automatically.
3. Personalise suggestions with the Communicator's own confirmed phrases, separated by conversation context, without model training.
4. Protect privacy with separate consents (AI sharing, saving, AI reuse) and keep working offline with saved phrases.
5. Store all data in `.txt` files that reload correctly after restart, using plain arrays and custom exceptions.

### 3.3 User roles and business rules (for 1.3)
| Action | Communicator | Supporter (authorised) | Supporter (not authorised) |
| --- | --- | --- | --- |
| Get suggestions, approve / edit / reject, write own message | Yes | Yes | Yes |
| Change language style / tone, add conversation partner | Yes | Yes | **No** |
| Turn AI sharing on/off | Yes | **No** | **No** |
| Add a Supporter / change a Supporter's permission | Yes | **No** | **No** |

Business rules: AI sharing is OFF for a new profile; at most 3 suggestions, none pre-selected; EDIT needs the edited text; REJECT needs a reason; a saved phrase must use its partner's context; HEALTHCARE records never appear in FAMILY or FRIENDS suggestions; saving and AI reuse are separate choices; a request is only sent for the conversation and consent that existed when the user clicked; IDs come from `FileManager.nextId`; arrays have fixed capacities and refuse new records when full.

### 3.4 Classes (for 2.1 UML and 2.2) – 32 classes
| Class | Type | Owner | Purpose |
| --- | --- | --- | --- |
| `User` | Abstract class | A | Parent of all app users; abstract `getTypeCode()`, `canEditProfile()` |
| `Communicator` | Subclass of `User` | A | Person with aphasia; can always edit own profile |
| `Supporter` | Subclass of `User` | A | Helper; can edit only when authorised |
| `CommunicationProfile` | Model | A | Language style, preferred tone, AI sharing consent |
| `ConversationPartner` | Model | A | Partner name, context (FAMILY/FRIENDS/HEALTHCARE), tone |
| `Phrase` | Model | A | Confirmed sentence with keywords and AI-reuse consent |
| `Codes` | Constants | A | Allowed context, decision and reason codes; `isValid()` |
| `Feedback` | Model | C | APPROVE/EDIT/REJECT decision with reason |
| `Suggestion` | Model | C | One candidate sentence, source AI or SAVED |
| `FileManager` | Storage | A | Arrays + counters, CRUD, `.txt` save/load, rollback, AI log |
| `RecordCodec` | Storage helper | A | Escapes and splits `|`-separated records |
| `AppController` | Controller | A | Session, permissions, request snapshot, AI→fallback flow |
| `RequestSnapshot` | Controller helper | A | Keywords + profile + partner + consent captured at the click |
| `DemoData` | Setup | A | Creates fictional demo data on first run |
| `Main` | Entry point | A | Starts the Swing GUI |
| `ConsoleMain` | Entry point | A | Starts the Scanner text menu |
| `ConsoleMenu` | Text frontend | A | Scanner menu using the same backend |
| `InvalidInputException` | Custom exception | A | Invalid input or not allowed |
| `StorageFullException` | Custom exception | A | Array or AI log is full |
| `AIServiceException` | Custom exception | B | Any AI failure (key, network, HTTP, format) |
| `AIHelper` | AI | B | `ask(prompt)` via `HttpURLConnection`; `generateSuggestions` |
| `AIConfig` | AI | B | Reads `config.txt` |
| `PromptBuilder` | AI | B | Builds the prompt from a `GenerationRequest` |
| `AIResponseParser` | AI | B | Hand-written JSON scanning; exactly 3 sentences |
| `GenerationRequest` | AI model | B | Immutable data sent to the AI |
| `SuggestionService` | Logic | C | Saved-phrase fallback (max 3) |
| `MemoryManager` | Logic | C | Picks up to 3 phrases + 2 feedback by keyword overlap |
| `CommunicationRules` | Logic | C | Validation and sharing rules |
| `FeedbackManager` | Logic | C | Builds and validates `Feedback` |
| `MainFrame` | GUI (extends `JFrame`) | D | Main Swing window |
| `InputValidator` | GUI helper | D | Keyword/sentence validation |
| `ReportGenerator` | GUI helper | D | Builds and writes the `.txt` report |

UML rules: show the `User` hierarchy with inheritance arrows, the 3 exceptions extending `Exception`, and for each class only the **key** fields/methods (≤ 5). Draw it with draw.io or PlantUML. Save the source and `report/figures/uml.png`.

### 3.5 Inheritance and polymorphism (for 2.3)
`User` is abstract with `getTypeCode()` and `canEditProfile()`. `Communicator.canEditProfile()` returns `true`; `Supporter.canEditProfile()` returns its `supporterAuthorized` flag. `AppController.canEdit(User actingUser, String profileId)` calls `actingUser.canEditProfile()` through a **`User` reference**, so Java runs the subclass version (polymorphism). It is used by `createPartner` and `updateProfileSettings`. `FileManager.loadAll()` reads `users.txt` and creates a `Communicator` or a `Supporter` by type, storing both in one `User[]` array; `saveAll()` uses `instanceof Supporter` to save the authorisation flag. Consent actions use `instanceof Communicator` because only the Communicator may grant consent.

### 3.6 Data files (for 2.4) – folder `data/`, UTF-8, first line is a header, `|` separated, `\` escapes `| \ newline tab`
| File | Format | Example line |
| --- | --- | --- |
| `users.txt` | `userId\|type\|displayName\|profileId\|supporterAuthorized` | `U002\|SUPPORTER\|Ah Hui (demo supporter)\|P001\|false` |
| `profiles.txt` | `profileId\|ownerUserId\|displayName\|languageStyle\|preferredTone\|aiSharingEnabled` | `P001\|U001\|Mei (demo)\|Manglish, short sentences\|casual\|false` |
| `partners.txt` | `partnerId\|profileId\|contextCode\|label\|preferredTone` | `CP001\|P001\|FAMILY\|Ah Ma\|warm` |
| `phrases.txt` | `phraseId\|profileId\|partnerId\|contextCode\|keywords\|confirmedText\|allowAIReuse\|createdAt` | `PH001\|P001\|CP001\|FAMILY\|kopi tomorrow\|Tomorrow kopi with Ah Ma lah.\|true\|1795596982988` |
| `feedback.txt` | `feedbackId\|profileId\|partnerId\|contextCode\|keywords\|candidateText\|decisionCode\|reasonCode\|confirmedText\|allowAIReuse\|createdAt` | `FB001\|P001\|CP001\|FAMILY\|kopi\|Please buy me coffee.\|REJECT\|WRONG_MEANING\|\|true\|1795596990000` |
| `ai_log.txt` | `createdAt\|requestId\|profileId\|partnerId\|contextCode\|displayedReply` | `1795600000000\|REQ1795600000000\|P001\|CP001\|FAMILY\|Tomorrow can kopi with Ah Ma?` |
| Report export (GUI: file chosen by the user; text menu: `data/report-<time>.txt`) | Plain text from `ReportGenerator` | `1. Tomorrow kopi with Ah Ma lah.` |

Header lines look like `#MyWords|1|PHRASES`. (In the Word table, write `|` normally; the `\|` above is only Markdown escaping.)

### 3.7 Key methods (for 3.2) – each owner explains theirs
- **A:** `FileManager.savePhrase` (validate → capacity → upsert → commit with rollback), `FileManager.loadAll` (read into temporary arrays, validate, then swap), `AppController.requestSuggestions(RequestSnapshot)` (session + consent re-check → AI or fallback).
- **B:** `AIHelper.ask` (HTTP POST, timeouts, status codes), `AIResponseParser.parseCandidates` (exactly 3 distinct strings), `AIHelper.generateSuggestions` (log before return).
- **C:** `MemoryManager.selectPhrases` (keyword overlap + context isolation), `SuggestionService.savedPhraseFallback`, `FeedbackManager.createDecision` (EDIT/REJECT rules).
- **D:** `MainFrame.requestSuggestions` (`SwingWorker` + snapshot + stale check), `MainFrame.saveDecision` (separate consents), `ReportGenerator.writeReport`.

### 3.8 Custom exceptions (for 3.3)
| Exception | When it is thrown | Where it is caught / handled |
| --- | --- | --- |
| `InvalidInputException` | Empty or too-long input; unknown context/decision/reason code; EDIT without text; REJECT without reason; partner not in profile; Supporter not allowed; conversation changed before sending; corrupt line in a data file | `MainFrame.showError` / `ConsoleMenu.showError` show the message; `FileManager.loadAll` restores the previous arrays first; `AppController` restores changed fields first |
| `StorageFullException` | An array reaches capacity (e.g. 20 profiles, 1000 phrases) or `ai_log.txt` would exceed 5 MB | `MainFrame` / `ConsoleMenu` show "… storage is full"; `AIHelper` converts a full log into `AIServiceException` |
| `AIServiceException` | Missing/placeholder key, no network, timeout, HTTP 401/402/429/5xx, invalid or oversized reply, not exactly 3 sentences, AI sharing off | `AppController.requestSuggestions` catches it and shows saved phrases with the reason (fallback); the app keeps working |

Demo for the video: `InvalidInputException` = press Get suggestions with empty keywords, or Ah Hui (unauthorised Supporter) tries to change style/tone; `StorageFullException` = section 6 script; `AIServiceException` = remove or rename `config.txt` and turn AI sharing on, then Get suggestions shows "AI unavailable: … Showing saved phrases."

### 3.9 Requirements checklist (for 5)
| # | Requirement | Evidence |
| --- | --- | --- |
| 1 | Backend written in Java | §2.2, all of `src/mywords` |
| 2 | GUI frontend connected to the backend | §3.5, Fig 1–8 |
| 3 | Plain arrays with counters (no Collections) | §3.1, Code 1–2 |
| 4 | Inheritance and polymorphism | §2.3, Fig 12 |
| 5 | Custom exceptions thrown and caught | §3.3, Fig 11–14 |
| 6 | Data saved to and reloaded from files | §2.4, §3.4, Fig 9–10 |
| 7 | AI feature | §3.6, Fig 4, Fig 14 |
| 8 | All planned features working | §4 (all figures) |
Extra (original announcement): Scanner text menu, Fig 15; at least 10 classes (32).

### 3.10 Real challenges (for 6) – pick yours, add details you remember
| Challenge | How it was solved | Role |
| --- | --- | --- |
| A missing `package` line in `FeedbackManager` stopped the whole app from opening | Integration review; added the line and a handbook rule that every file starts with `package` | A, C |
| UI and logic used different method names (`createFeedback` vs `createDecision`) | Adapter method; the real API is recorded in `TEAM_HANDBOOK.md` section 4 | A, C, D |
| A quick conversation switch could send old keywords under another profile's AI consent | `RequestSnapshot` captured at the click plus a re-check before sending; 10 race checks | B found it, A + D fixed it |
| A sentence containing `\|` or a newline broke the `.txt` format | `RecordCodec` escaping, with a state machine when reading | A |
| A failed save left memory and files different | Backup + rollback in `FileManager.commit()` | A |
| The AI sometimes added details absent from the keywords, or wrapped JSON in Markdown | Stricter prompt, low temperature, parser accepts fenced JSON | B |
| The PDF requires a GUI but the announcement asks for a Scanner menu | Both: Swing GUI plus `ConsoleMenu` on the same backend; lecturer asked (Q02) | A |
| Git: conflicting pushes, line endings, account transfer | `git pull` before push; add only own files; handbook section 9 | all |

### 3.11 Individual contribution (for 8) – must match `git log`
| Member | Role | Main classes / work |
| --- | --- | --- |
| Hui En `[matric]` | A – core data, storage, controller, integration | `User` hierarchy, models, `FileManager`, `RecordCodec`, `AppController`, `RequestSnapshot`, `DemoData`, `Main`, `ConsoleMain`/`ConsoleMenu`, handbook |
| xxianen `[name, matric]` | B – AI integration | `AIHelper`, `AIConfig`, `PromptBuilder`, `AIResponseParser`, `RoleBAIChecks` |
| weinisi `[name, matric]` | C – communication logic | `SuggestionService`, `MemoryManager`, `CommunicationRules`, `FeedbackManager`, `RoleCLogicChecks` |
| shaniece `[name, matric]` | D – GUI and report | `MainFrame`, `InputValidator`, `ReportGenerator` |

---

## 4. Screenshot rules
- Fictional data only. Window at a readable size, no other apps visible, no `config.txt`/API key on screen.
- File name `report/figures/fig-NN-short-name.png` (two digits). Caption format: `Figure NN: <what it shows>`.
- Take the screenshot **after** the feature really worked; if it fails, report it to the owner instead.

## 5. Fixed figure list (numbers never change; owner takes it)
| Fig | Shows | Owner |
| --- | --- | --- |
| 01 | Main window after start | D |
| 02 | Conversation selected (Mei / Ah Ma) | D |
| 03 | `[Saved phrase]` suggestions for `kopi` with AI sharing off | C |
| 04 | `[AI suggestion]` ×3 for a section-7 slang case (AI sharing on) | B |
| 05 | Approve / Edit / Reject with reason and consent questions | C |
| 06 | Manual message saved | D |
| 07 | Manage saved phrases (AI reuse on/off, delete) | D |
| 08 | Exported report file opened | D |
| 09 | `data/` folder and `phrases.txt` contents | A |
| 10 | After restart: the same phrases still there | A |
| 11 | `InvalidInputException`: empty keywords error | D |
| 12 | `InvalidInputException`: unauthorised Supporter refused when changing style/tone (polymorphism via `canEditProfile()`). Use text-menu option 7 until D's settings screen exists | A |
| 13 | `StorageFullException`: "Profile storage is full (20)" | A |
| 14 | `AIServiceException`: "AI unavailable … Showing saved phrases." | B |
| 15 | Scanner text menu (`ConsoleMain`) | A |
| 16 | `ai_log.txt` rows for one request | B |
| 17 | HEALTHCARE phrase not shown in a FAMILY conversation | C |

## 6. Video standard (5–7 min, every member speaks about their own part)
| Time | Speaker | Must show (template section 6) |
| --- | --- | --- |
| 0:00–0:40 | A | Members, Track 3, purpose (overview 3.1) |
| 0:40–1:40 | D | GUI walk-through; how screens call `AppController` (frontend ↔ backend) |
| 1:40–2:50 | A | Class structure, `User` hierarchy, arrays + counters, file save, **restart reload**, rollback idea |
| 2:50–4:00 | B | AI live: slang keywords → 3 suggestions; `AIHelper.ask`, manual JSON, log file, consent |
| 4:00–5:10 | C | Approve/edit/reject + reasons, saved-phrase fallback, context isolation (Fig 17) |
| 5:10–6:20 | A, B, C, D | **Error handling, each own exception:** D empty keywords; A Supporter refused + storage full (script below); B missing key → fallback; C EDIT without text |
| 6:20–6:50 | all | Contribution recap (one sentence each) and limitations |

Storage-full demo (A). Run it on a copy, then reset:
```
cp -r data data_backup              # keep the real demo data
java -cp out mywords.app.ConsoleMain < tests/demo/storage_full_input.txt
# shows: [!] Profile storage is full (20).
rm -rf data && mv data_backup data  # restore
```
Recording: 1080p, readable font size, microphone check; upload as `[TO CHECK: MP4 or unlisted link, lecturer Q05]`.

## 7. Submission package (one ZIP, name `G[NN]_T3_MyWords.zip`, group number `[TO CHECK]`)
Include: `src/`, `tests/`, `docs/`, `README.md` (with run steps for `mywords.app.Main` and `ConsoleMain`), `config.example.txt`, and a **`data/` folder with fresh fictional demo data** (delete `data`, run once, close; the template requires the `.txt` data files).
Exclude: `config.txt`, real keys, `out/`, `bin/`, `.vscode/`, `.git/`, `ai_log.txt` from real tests, `latest_roles/` (archive).
Final check: unzip into an empty folder → compile → run GUI and text menu → restart → data reloads. Report `GroupNo_TrackNo_ProjectName.docx/.pdf`; both upload destinations `[TO CHECK: Q04]`.

## 8. Deadlines
| Date | What |
| --- | --- |
| 29 Nov | Offline milestone + figures 01–03, 05–13, 15, 17 |
| 3 Dec | Each owner pushes report drafts (`report/drafts/`) |
| 5 Dec | AI milestone (PR #4 merged) + figures 04, 14, 16 |
| 7 Dec | A merges the report; everyone reviews |
| 8 Dec | Code freeze; record the video |
| 9 Dec | Final package check (section 7) |
| 10 Dec, 11:00 PM | Submit |
