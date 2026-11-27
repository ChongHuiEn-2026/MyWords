# MyWords Team Handbook (v0.3, HTML submission draft)

Last updated: **27 November 2026 (MYT)** for draft PR #4.

**This is the single source of truth for the code that exists now.** For the report, video and ZIP, use [`REPORT_GUIDE.md`](REPORT_GUIDE.md). Give this file to your AI assistant together with `AGENTS.md` and your `docs/ROLE_X.md`. When this handbook and an older doc disagree, this handbook wins. `docs/INTEGRATION.md` v0.1 was the *proposal*. Section 4 below lists the *actual* signatures in `src/`.

---

## 0. How to use this with an AI assistant

1. Run `git pull` first. Your AI must see the latest code, not an old copy.
2. Give it these files, in this order: `AGENTS.md`, `docs/TEAM_HANDBOOK.md` (this file), `docs/ROLE_X.md` (yours), and the Java files you will edit.
3. Paste this starter prompt, replacing X:

> I am Role X in the MyWords FOP project. Read AGENTS.md and docs/TEAM_HANDBOOK.md first. Section 4 is the real API. Do not rename, remove or change the parameters of any public method listed there. Only edit files in my package (section 3). If I need something from another role's class, tell me what to ask them instead of editing their file. Use plain arrays plus int counters (no ArrayList/HashMap/List/Map/Set/streams), no JSON libraries, and Java 17. Explain the code line by line for a beginner. Before finishing, make sure the whole project compiles.

4. Before you push, **[run the HTML app](../README.md#run-the-html-gui)** and click through your feature. If it does not open, do not push.

---

## 1. Current status (what works today)

| Area | Owner | Status |
| --- | --- | --- |
| Models, `User` → `Communicator` / `Supporter` inheritance | A | Done |
| `FileManager`: arrays and counters, CRUD, `.txt` save/load, AI log, `nextId` | A | Done, 39 automated checks passed |
| `RecordCodec` (escape `\|`, newline, `\`; strict parsing) | A | Done |
| `DemoData` (fictional Mei / Ah Ma / Dr Lim on first run) | A | Done |
| `AppController` (session, consent check, stale-result check, AI→fallback) | A | Used by the HTML and Scanner flows |
| `SuggestionService.savedPhraseFallback`, `MemoryManager`, `CommunicationRules`, `FeedbackManager` | C | Done (v1) |
| HTML screens, `BrowserApp` and `ReportGenerator` | D / integration | HTML is the selected GUI in draft PR #4 |
| `AIConfig`, `AIHelper.ask`, `PromptBuilder`, `AIResponseParser` | B (xxianen) | **In draft PR #4**: prompts for three plausible interpretations, validates and logs the batch, and falls back to saved phrases. Live wording review of this response format is pending |
| Automated checks (`tests/`) | B, C (+ A's checks kept by A) | `mywords.ai.RoleBAIChecks`, `mywords.logic.RoleCLogicChecks` and `mywords.web.BrowserChecks`. How to run: section 9b |
| Request snapshot (consent race fix, 26 Nov) | A + D | Done: keywords, profile, partner and consent are captured at the click and re-checked before sending (section 6) |

For a fictional offline demo, open the HTML GUI, choose **Quick Demo Login**, then **Family → Ah Ma → Eat or Drink → My Own Idea (Type Myself)**. Enter `kopi` and look for a **Saved phrase** suggestion.

---

## 2. Golden rules (every role, every AI)

1. **Arrays + counters only** for storing objects. No `ArrayList`, `LinkedList`, `HashMap`, `List`, `Map`, `Set` or streams. `StringBuilder` and `java.util.Locale` are fine.
2. **Do not change another role's public method** (name, parameters, return type, exceptions). Need a change? Agree it in the group chat, then update section 4 in the same commit.
3. **Every file starts with a `package mywords.xxx;` line.** A missing package line broke the build on 25 Nov.
4. **Throw our own exceptions**: `InvalidInputException` (bad input), `StorageFullException` (array or log full), `AIServiceException` (anything AI). Catch them in the screen and show a message. Never swallow an exception and pretend it worked.
5. **Never auto-select, send or save an AI sentence.** The communicator chooses. Approving a sentence and allowing it to be saved or reused are **separate** questions.
6. **Consent:** only the **Communicator** can turn AI sharing on or off (`controller.canActiveUserChangeConsent()`). A Supporter cannot, even when authorised.
7. **Privacy:** no real patient data. Fictional demo data only. Never commit `config.txt`, API keys or the `data/` folder.
8. **No JSON library and no AI SDK.** Use `HttpURLConnection` and String parsing (B).
9. **Malaysian slang and rojak are a core feature** (section 7). Never "correct" `tapau`, `lah`, `yum cha` and similar into standard English unless the style is formal.

---

## 3. Project structure and ownership

```
src/mywords/
  app/        A  Main, ConsoleMain, ConsoleMenu, AppController, RequestSnapshot, DemoData
  model/      A  User, Communicator, Supporter, CommunicationProfile, ConversationPartner, Phrase, Codes
              C  Feedback, Suggestion (shared models; ask A and C before changing fields)
  storage/    A  FileManager, RecordCodec
  exception/  A  InvalidInputException, StorageFullException   B  AIServiceException
  ai/         B  AIHelper, AIConfig, PromptBuilder, AIResponseParser, GenerationRequest
  logic/      C  SuggestionService, MemoryManager, CommunicationRules, FeedbackManager
  web/           BrowserMain, BrowserApp, BrowserForm, BrowserRecords
  ui/         D  ReportGenerator
MyWords.html, browser.js, mywords.css  D's browser GUI
data/         runtime .txt files (git-ignored, created automatically)
docs/         shared docs; this handbook is the current contract
latest_roles/ ARCHIVE copy of older role docs. Do not give it to your AI.
```

Run the GUI with **`mywords.web.BrowserMain`** from the repository root and open the address printed in the terminal. See the [README](../README.md#run-the-html-gui). Run the Scanner menu with **`mywords.app.ConsoleMain`**.

---

## 4. The real API (call these exactly as written)

### A: `AppController` (the screen talks to this)
```java
void selectSession(User user, String profileId, String partnerId) throws InvalidInputException
String getActiveProfileId();  String getActivePartnerId();  User getActiveUser()
int getSessionVersion()                                   // read before starting a background request
boolean isCurrentSession(int version, String profileId, String partnerId) // check when the result arrives
boolean canActiveUserChangeConsent()                      // Communicator of this profile only
boolean canActiveUserEditProfile()                        // polymorphism: User.canEditProfile() + same profile
RequestSnapshot captureRequest(String keywords) throws InvalidInputException   // call ON THE UI THREAD at the moment of the click
Suggestion[] requestSuggestions(RequestSnapshot snapshot) throws InvalidInputException, IOException, StorageFullException
boolean isCurrentSession(RequestSnapshot snapshot)       // use in done() to discard stale results
Suggestion[] requestSuggestions(String keywords)          // convenience = capture + request at once; ONLY for single-threaded code (console menu, tests)
String getLastFallbackMessage()                           // prefer snapshot.getResultMessage() (per request)
FileManager getFileManager()
// Create / edit (for D's screens). All validate input and throw InvalidInputException with a user-friendly message.
CommunicationProfile createProfile(String communicatorName, String languageStyle, String preferredTone)   // new Communicator + profile, AI sharing OFF
ConversationPartner  createPartner(User actingUser, String profileId, String contextCode, String label, String preferredTone)
Supporter addSupporter(User actingUser, String profileId, String supporterName, boolean authorized)        // Communicator only
void setSupporterAuthorization(User actingUser, Supporter supporter, boolean authorized)                   // Communicator only
void updateProfileSettings(User actingUser, String profileId, String languageStyle, String preferredTone)
// (all of the above also throw StorageFullException, IOException)
```
Permission rule (polymorphism demo): `createPartner` and `updateProfileSettings` are allowed when `actingUser.canEditProfile()` is true **and** the user belongs to that profile. That means a Communicator is allowed, an authorised Supporter is allowed, and an unauthorised Supporter is refused. Adding or authorising supporters and changing AI sharing are allowed for the **Communicator only**. Limits: names up to 60 characters, style and tone up to 120 characters, text kept exactly as typed (slang is not corrected).
`RequestSnapshot` (A, `mywords.app`) getters: `getRequestId, getSessionVersion, getProfileId, getPartnerId, getKeywords, isAiSharingAtClick, getResultMessage`.

`requestSuggestions(snapshot)` flow: if the session changed since the click → `InvalidInputException` ("Nothing was sent"). If AI sharing was off **at the click or now** → → C's `savedPhraseFallback`. Otherwise → C's `MemoryManager` selects up to 3 phrases + 2 feedback → B's `AIHelper.generateSuggestions` → if it throws `AIServiceException` → `savedPhraseFallback`. It never returns null.

### A: `FileManager`
```java
void loadAll() throws IOException, InvalidInputException, StorageFullException
void saveAll() throws IOException
User[] getUsers();  CommunicationProfile[] getProfiles()
CommunicationProfile getProfile(String profileId) throws InvalidInputException
ConversationPartner[] getPartners(String profileId) throws InvalidInputException
ConversationPartner getPartner(String profileId, String partnerId) throws InvalidInputException
Phrase[]   getPhrases(String profileId, String partnerId) throws InvalidInputException
Feedback[] getFeedback(String profileId, String partnerId) throws InvalidInputException
void saveUser / saveProfile / savePartner / savePhrase(...) throws InvalidInputException, StorageFullException, IOException  // upsert by ID
void recordDecision(Feedback feedback, Phrase optionalPhraseOrNull) throws InvalidInputException, StorageFullException, IOException
void appendAILogBatch(String requestId, String profileId, String partnerId, String contextCode, String[] displayedReplies) throws IOException, StorageFullException // all rows or none
void appendAILog(String requestId, String profileId, String partnerId, String contextCode, String displayedReply) throws IOException, StorageFullException     // = a batch of one
void deletePhrase(String profileId, String phraseId) / deleteFeedback(String profileId, String feedbackId) throws InvalidInputException, IOException
void deleteProfileData(String profileId) throws IOException
String nextId(String prefix)   // "U","P","CP","PH","FB" -> e.g. "PH004". ALWAYS use this for new IDs
```
- Getters return **copies with exact length**. Use `.length`. "No match" gives an empty array, never null.
- Every save writes the `.txt` files immediately. If writing fails, the arrays are restored and an `IOException` saying "Nothing was changed" is thrown, so show that message to the user.
- A phrase or feedback must use the **same context as its partner**, otherwise `InvalidInputException`.

### B: `AIHelper` and friends (to implement)
```java
String ask(String prompt) throws AIServiceException                    // ONLY place with HttpURLConnection
Suggestion[] generateSuggestions(GenerationRequest r) throws AIServiceException, InvalidInputException, IOException, StorageFullException
static AIConfig load(String path) throws AIServiceException            // reads config.txt
String PromptBuilder.build(GenerationRequest r)
String AIResponseParser.extractContent(String providerJson) throws AIServiceException
String[] AIResponseParser.parseCandidates(String content) throws AIServiceException // exactly 3 labeled meanings
```
`GenerationRequest` getters: `getRequestId, getProfileId, getPartnerId, getContextCode, getKeywords, getLanguageStyle, getPreferredTone, isAiSharingEnabled, getAllowedPhrases, getAllowedFeedback`.

### C: logic
```java
Suggestion[] SuggestionService.savedPhraseFallback(String requestId, String keywords, Phrase[] saved) // max 3, source "SAVED"
boolean      SuggestionService.isValidCandidateSet(Suggestion[] c)    // exactly 3 distinct valid sentences
Phrase[]   MemoryManager.selectPhrases(String keywords, String contextCode, Phrase[] candidates, int limit)    // max 3
Feedback[] MemoryManager.selectFeedback(String keywords, String contextCode, Feedback[] candidates, int limit) // max 2
Feedback FeedbackManager.createFeedback(id, profileId, partnerId, contextCode, keywords, candidateText,
                                        decisionCode, reasonCode, confirmedText, boolean allowAIReuse) throws InvalidInputException // used by UI
Feedback FeedbackManager.createDecision(... , boolean freshConfirmation, boolean allowAIReuse, long createdAt) // C's full version, throws IllegalArgumentException
static boolean CommunicationRules.hasText / isValidKeywords / isValidSentence / isKnownContext / isKnownReason / maySharePhrase / mayShareFeedback
```

### D: report helper
```java
String ReportGenerator.buildReport(Phrase[] selected);  void writeReport(String text, String path) throws IOException
```

### Models (A; Feedback and Suggestion: C)
Constructors (field order = file column order):
```java
new Communicator(userId, displayName, profileId)
new Supporter(userId, displayName, profileId, boolean supporterAuthorized)
new CommunicationProfile(profileId, ownerUserId, displayName, languageStyle, preferredTone, boolean aiSharingEnabled)
new ConversationPartner(partnerId, profileId, contextCode, label, preferredTone)
new Phrase(phraseId, profileId, partnerId, contextCode, keywords, confirmedText, boolean allowAIReuse, long createdAt)
new Feedback(feedbackId, profileId, partnerId, contextCode, keywords, candidateText, decisionCode, reasonCode, confirmedText, boolean allowAIReuse, long createdAt)
new Suggestion(suggestionId, requestId, text, sourceCode)   // sourceCode = Codes.SOURCE_AI ("AI") or Codes.SOURCE_SAVED ("SAVED")
```

---

## 5. Codes, IDs, files and config

| Item | Allowed values |
| --- | --- |
| Context | `FAMILY`, `FRIENDS`, `HEALTHCARE`, `OTHERS` |
| Decision (stored) | `APPROVE`, `EDIT`, `REJECT`. `NONE_OF_THESE` exists in C's FeedbackManager but is **not stored**; the screen just clears the selection |
| Reason | `NONE` (approve only), `WRONG_MEANING`, `WRONG_TONE`, `WRONG_PERSON_CONTEXT`, `OTHER_UNSURE` |
| EDIT rule | Must have a non-empty confirmed (edited) text |
| REJECT rule | Reason must not be `NONE` |
| IDs | `U001`, `P001`, `CP001`, `PH001`, `FB001` from `fileManager.nextId(prefix)`; `REQ...` for requests (controller / B) |

**Data files** (UTF-8, `data/`, first line is the header). Fields are separated by `|`, and `\ | newline tab` are escaped by `RecordCodec`. **Only `FileManager` reads or writes these files.**
```
users.txt     #MyWords|1|USERS     userId|COMMUNICATOR or SUPPORTER|displayName|profileId|supporterAuthorized
profiles.txt  #MyWords|1|PROFILES  profileId|ownerUserId|displayName|languageStyle|preferredTone|aiSharingEnabled
partners.txt  #MyWords|1|PARTNERS  partnerId|profileId|contextCode|label|preferredTone
phrases.txt   #MyWords|1|PHRASES   phraseId|profileId|partnerId|contextCode|keywords|confirmedText|allowAIReuse|createdAt
feedback.txt  #MyWords|1|FEEDBACK  feedbackId|profileId|partnerId|contextCode|keywords|candidateText|decisionCode|reasonCode|confirmedText|allowAIReuse|createdAt
ai_log.txt    #MyWords|1|AI_LOG    createdAt|requestId|profileId|partnerId|contextCode|displayedReply   (5 MB cap)
```
The browser also stores account preferences and other browser records in [`browser.txt`](INTEGRATION.md#html-gui-for-submission). For a clean demo, run the app from a fresh project checkout.

**`config.txt`** (B; copy from `config.example.txt` and never commit it). These keys are the contract. B's `AIConfig` must read exactly these:
`AI_API_KEY`, `AI_ENDPOINT`, `AI_MODEL`, `CONNECT_TIMEOUT_MS` (10000), `READ_TIMEOUT_MS` (20000), `MAX_RESPONSE_BYTES` (65536). A blank value or one starting with `REPLACE_` means "AI off" and must throw `AIServiceException`.

---

## 6. The main flows (who does what)

**Get suggestions** (D → A → C/B).
1. The HTML sends the selected partner and keywords to `BrowserApp` via a local POST. Java resolves the signed-in communicator, validates the input and selects the conversation.
2. `BrowserApp` calls `controller.captureRequest(keywords)` then `controller.requestSuggestions(ticket)`. Java checks the snapshot and the current consent before contacting the provider.
3. The browser checks the current conversation when the response returns. It displays eligible cards labeled **AI suggestion** or **Saved phrase** and leaves the choice to the communicator.

**B's `generateSuggestions`** (the contract D and A rely on):
build prompt → `ask` → `extractContent` → `parseCandidates` (three objects with different recognized intent codes and distinct wording) → **one** `fileManager.appendAILogBatch(..., texts)` call **before** returning → return three `Suggestion`s with source `"AI"`. A short, repeated or malformed reply or failed log write throws `AIServiceException` so the controller can use fallback.

**Approve / Edit / Reject** (D → C → A): `BrowserApp` checks the request ID associated with the current browser session. C's `createFeedback` builds and validates the record, then A's `recordDecision(feedback, phraseOrNull)` stores both together. Saving the phrase, saving the feedback and allowing AI reuse are each asked separately.

Approving a saved phrase with the same wording skips the duplicate save in `BrowserApp.decision`.

**Manual message**: saved as a `Phrase` only (no Feedback, because there is no candidate), using the keywords typed in the Keywords box so it can be found again.

---

## 6b. Scanner text menu (requirement O04, lecturer question Q02)

The original announcement asks for "a text menu that reads input with Scanner". The lecturer PDF makes a GUI compulsory. Whether both are needed is still **open** (Q02). Until the lecturer answers, we provide **both**:
- `mywords.web.BrowserMain`: HTML GUI for submission. `mywords.app.Main` also starts the browser server.
- `mywords.app.ConsoleMain` → `ConsoleMenu` (Role A): a Scanner menu covering choose conversation, create profile, get suggestions (approve/edit/reject), write own message, view/delete phrases, export report, add partner, change style/tone, AI sharing on/off and add supporter.
- Both call the **same** `AppController` / `FileManager` / `FeedbackManager` / `ReportGenerator`, so **no business logic is duplicated** and they read and write the same `data/` files. If you change a public method, the console menu must still compile.
- In VS Code: open `src/mywords/app/ConsoleMain.java` and press Run; the menu appears in the Terminal.

## 7. Malaysian slang and rojak (core MVP feature, restored)

MyWords is for everyday Malaysian speech. Accept local terms, informal spelling and mixed-language keywords. When the communicator's style is casual or rojak, keep the local expressions that fit the meaning. When the style is formal, change the register but keep the same intent. Always combine the **profile's `languageStyle`** with the **partner's `preferredTone` and context**. The communicator can review, edit or reject every suggestion.

Role B preview correction requested 27 November. Each successful AI batch offers exactly three different plausible interpretations and preserves explicit details. If the AI cannot provide all three, the app asks for more keywords or a retry. The communicator chooses which meaning fits.

Shared fictional test cases. Every role tests these:

| Keywords | Style and context | Acceptable example / expected behaviour |
| --- | --- | --- |
| `later, tapau, nasi lemak, for me` | Casual rojak, Friends | `Later can tapau nasi lemak for me?` (keep the local term) |
| `later, tapau, nasi lemak, for me` | Formal English, Friends | `Could you get me some nasi lemak to take away later?` |
| `tomorrow, Ah Ma, yum cha` | Casual mixed, Family | `Tomorrow I want to yum cha with Ah Ma.` |
| `nurse, speak, now` | Formal English, Healthcare | `Could I speak with a nurse now?` (no invented symptoms or advice) |

- **A:** stores and reloads the exact wording (verified: `Ah Ma|kopi`, Chinese, BM, emoji and newlines survive a restart).
- **B:** the prompt must include `languageStyle`, `preferredTone`, context and keywords as given. It must say to keep the supplied local words in casual style and must not invent facts.
- **C:** do not normalise or translate saved text. Keyword matching is case-insensitive, word-based.
- **D:** never auto-correct input. Run the 4 cases above through the GUI and record the results for the report.

---

## 8. What each role must do next (deadline: offline milestone 29 Nov, AI milestone 5 Dec, code freeze 8 Dec)

### Role A: core data and integration
- [x] Profile, partner and supporter **creation** and settings methods in `AppController` (section 4). Done 25 Nov, 14 checks passed.
- [x] Rollback: if writing a file fails, `FileManager` restores its arrays and throws `IOException` ("Nothing was changed"). Done 25 Nov, 7 failure checks passed.
- [x] Scanner text menu `ConsoleMain` / `ConsoleMenu` (section 6b). Done 25 Nov, tested with scripted input.
- [ ] Keep this handbook and `docs/DECISIONS.md` updated; final clean build and run instructions in README. Record the lecturer's Q02 answer.
- [ ] Evidence: inheritance through a `User` reference, restart persistence, `StorageFullException` demo.

### Role B: AI (in PR #4, almost done)
- [x] Implement `AIConfig`, `AIHelper.ask`, `PromptBuilder`, `AIResponseParser`, and the saved-phrase fallback. Include only the placeholder `config.example.txt`.
- [x] Bring the click-time consent fix and `appendAILogBatch` from `main` into `feature/role-b-ai`.
- [x] In `generateSuggestions`, write all three validated replies with one `appendAILogBatch` call before building the `Suggestion` array. Update [the Role B handoff](ROLE_B.md).
- [x] Compile for Java 17 and run the integrated checks. All 69 Role B assertions and seven Role C groups passed. A stale click produced zero mock calls, a near-full log gained no partial rows, and Swing showed mock AI suggestions and saved-phrase fallback. The four standalone live ILMU checks verified reply format and logging. See [the evidence ledger](DECISIONS.md) and [the wording review](ROLE_B.md#live-wording-review-26-november-2026).
- [x] Push the verified AI integration commits to draft PR #4.
- [ ] Review live AI wording in the HTML flow with fictional examples and AI sharing enabled. Confirm the demo model with the team and record the results in `DECISIONS.md`.
- [ ] Push the Role B report draft by 3 Dec. Capture figures 04, 14 and 16 for the 5 Dec AI milestone, and prepare the AI walkthrough and missing-key fallback for the video. See [the report guide](REPORT_GUIDE.md).

### Role C: communication logic
- [ ] D07 is proposed. Get D's answer and record the final decision.
- [ ] Unit-check `savedPhraseFallback` and `MemoryManager` with the section 7 cases and with HEALTHCARE isolation (a FAMILY request never returns HEALTHCARE records).
- [ ] Decide with D whether REJECT feedback **without** a correction may be shared with AI (currently `mayShareFeedback` returns false for it) and record the decision.
- [ ] Keep `createFeedback` (the UI depends on it). If you change `createDecision`, keep `createFeedback` working.
- [ ] Prepare the comparison scenarios (generic vs memory) together with D for the report.

### Role D: screens and report
- [ ] Review the browser’s **AI sharing** disclosure for provider transmission and local logging in each supported language.
- [ ] Review C's proposal **D07** (a rejected AI sentence is never reused; only an explicitly confirmed correction may be, with separate consent) and reply.
- [ ] Walk through browser account, profile settings and contact management. Browser accounts are Communicator accounts. Use the Scanner menu for Supporter authorization and the polymorphism demonstration.
- [ ] Run the section 7 test cases and the bad-input cases; take screenshots for the report.
- [ ] Keep using `controller` methods for the session. Do not add file reading or writing in the UI.

### Team decisions still open
- [ ] **Demo AI model**: B tested ILMU Mini v3.3 (`ilmu-mini-v3.3`). Agree on it (or another) and record it in `DECISIONS.md`.
- [ ] **Review draft PR #4** with Roles A and D. Verify the HTML run instructions, shared backend and submission evidence, then record the review outcome.

### Everyone
- [ ] Report, video and figures: follow `docs/REPORT_GUIDE.md` (your sections in its section 1, your figures in its section 5). Drafts go in `report/drafts/` by 3 Dec.
- [ ] Each member explains their own code in the 5–7 minute video.
- [ ] Lecturer questions (`docs/OPEN_QUESTIONS.md` Q01–Q05) must be answered and recorded.

---

## 9. Git workflow (avoid breaking each other)

```
git pull --no-rebase --no-edit       # 1. always start with the latest code
# ... code, then RUN the app ...
git status                           # 2. check: only your files should be listed
git add src/mywords/<your package>   # 3. add only your own files
git commit -m "Role X: what you did"
git pull --no-rebase --no-edit       # 4. get anything new before pushing
git push
```
- A file shows as "modified" but you never touched it? That is usually only line endings. Do not add it. Run `git restore <file>`.
- Do not commit `.vscode/`, `nbproject/private/`, `out/`, `bin/`, `data/` or `config.txt` (all git-ignored).
- Changing a shared public method? Message the group **before** pushing, and update section 4 in the same commit.

---

## 9b. Running the automated checks (before you push)

From the repo root in **Git Bash / MSYS2** (not PowerShell):
```
javac --release 17 -encoding UTF-8 -d out $(find src tests -name "*.java")
java -cp out mywords.logic.RoleCLogicChecks
java -cp out mywords.ai.RoleBAIChecks
java -cp out mywords.web.BrowserChecks
```
- Everything must **compile**, including other roles' tests. If your change breaks someone's test, fix it with them before pushing.
- `out/` is git-ignored. The checks use fictional data and a mock AI. They never need `config.txt` or a real key (`RoleBAIChecks --live ...` is optional and sends fictional text to the real provider).
- Put new checks in `tests/mywords/<your package>/` with a `main` method, as B and C did.

## 10. Integration fixes made on 25 Nov (so nobody is surprised)

| File (owner) | Change | Why |
| --- | --- | --- |
| `logic/FeedbackManager.java` (C) | Added the missing `package mywords.logic;` and imports. Added `createFeedback(...)`, which calls C's `createDecision` and converts `IllegalArgumentException` → `InvalidInputException` | The class did not compile; the UI calls `createFeedback` |
| `ui/MainFrame.java` (D) | `GridLayout(2,3)` → `(3,3)` | Dropdowns were misaligned |
| `ui/MainFrame.java` (D) | A manual message is saved as a Phrase only, with the typed keywords | C's rules need a candidate for Feedback; empty keywords made the phrase impossible to find |
| `ui/MainFrame.java` (D) | Saving uses the confirmed session; stale AI results are discarded; only the Communicator can change AI sharing | Prevents saving to the wrong partner and a Supporter granting consent |
| `app/AppController.java` (A) | Added session getters, `getSessionVersion`, `isCurrentSession`, `canActiveUserChangeConsent` | Used by the fixes above |
| 26 Nov: `app/RequestSnapshot.java` (new), `AppController`, `FileManager` (A); `ui/MainFrame.java` (D); `ConsoleMenu` (A) | Request snapshot captured at the click, re-checked before sending; FileManager methods synchronized | B found that a quick conversation switch could send the previous keywords under the new profile's consent. Verified fixed: 10 race checks (including 200 switch-while-sending rounds), B's 54 and C's 7 checks pass together with PR #4 |
| `AGENTS.md`, `docs/PROJECT_CONTEXT.md`, `docs/INTEGRATION.md`, `config.example.txt`, `.gitignore` | Role letters corrected; Malaysian slang section restored; `.vscode/` and `bin/` ignored | The old AGENTS.md used outdated role letters; the slang section was lost when docs were replaced |

| 26 Nov (afternoon): `FileManager.appendAILogBatch` (A), console consent text (A), handbook, README | AI log rows for one request are written all-or-nothing; the console explains sending + local logging before AI sharing is turned on; the README no longer says the repo has no code | Requested in B's `ROLE_B.md` (partial log writes, consent wording) |

## 11. Known limitations (be honest in the report)
- If the user changes conversation while the AI is answering, the reply is discarded on screen but its rows are already in `ai_log.txt` (each row has its `requestId`). The log records what the AI *returned for display*, not proof that the user read it.
- The 5 data files are written one after another. A crash in the middle can leave them partly updated (no multi-file atomicity).
- `FileManager` rolls back its **arrays** when a save fails. It cannot undo a **field you changed on an object** before calling save (for example `profile.setAiSharingEnabled(...)` or `phrase.setAllowAIReuse(...)`). Whoever calls the setter must set it back in the `catch`, as `AppController.updateProfileSettings` does. **D: please apply this in `toggleProfileAISharing`, `manageMemories` and `manageFeedbackMemory`.**
- Choosing a profile is not secure login. The Supporter authorisation is a demo of rules, not security.
- Retrieval is keyword overlap, not model training. Any accuracy improvement must be measured, never assumed.
