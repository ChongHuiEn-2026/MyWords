# Report draft – Role A sections (1.1–1.3, 2.1–2.4, 3.1, 3.3, 3.4, 5)

Draft by Role A, 26 Nov 2026, following `docs/REPORT_GUIDE.md`. Code snippets are copied from `main`; the original mixed-language end-of-line comments were removed to save space, and the code itself is unchanged. Items marked `[TO CHECK]` need confirmation before submission. In tables, `\|` is only Markdown escaping; type a normal `|` in Word. Figure numbers follow REPORT_GUIDE section 5.

---

## 1. Introduction

### 1.1 Project Overview
MyWords – Let Me Finish My Sentence is a Java communication aid for Malaysians with expressive aphasia, who often know what they want to say but cannot form the full sentence. The Communicator types a few keywords, including Malaysian slang or mixed-language (rojak) words, and MyWords offers up to three candidate sentences, either from an AI service or from the Communicator's own saved phrases. The Communicator chooses, edits or rejects each sentence; nothing is sent or saved automatically. Confirmed sentences can be saved and reused offline. Each conversation partner (family, friends or healthcare) has its own context, and records from one context never appear in another. The HTML interface uses a local Java server. The Scanner text menu shares the Java backend and core `.txt` files. MyWords is a student prototype and has not been clinically validated.

### 1.2 Objectives
- Turn a few keywords, including Malaysian slang and rojak, into up to three candidate sentences.
- Keep the Communicator in control: approve, edit or reject every sentence; nothing is chosen, sent or saved automatically.
- Personalise suggestions with the Communicator's own confirmed phrases, separated by conversation context, without model training.
- Protect privacy with separate consents (AI sharing, saving, AI reuse) and keep working offline with saved phrases.
- Store all data in `.txt` files that reload correctly after restart, using plain arrays and custom exceptions.

### 1.3 Scope and User Roles
MyWords has two types of user, both subclasses of the abstract class `User`.

- **Communicator**: the person with aphasia. A Communicator owns one communication profile, composes messages, decides what is saved and is the only person who can give or withdraw consent.
- **Supporter**: a family member or carer linked to the Communicator's profile. A Supporter can help compose messages. A Supporter can change profile settings or add conversation partners only if the Communicator has authorised them.

| Action | Communicator | Supporter (authorised) | Supporter (not authorised) |
| --- | --- | --- | --- |
| Get suggestions, approve / edit / reject, write own message | Yes | Yes | Yes |
| Change language style / tone, add conversation partner | Yes | Yes | No |
| Turn AI sharing on/off | Yes | No | No |
| Add a Supporter / change a Supporter's permission | Yes | No | No |

**Main business rules**
1. A new profile starts with AI sharing **off**; only the Communicator can turn it on.
2. At most three candidate sentences are shown, labelled `[AI suggestion]` or `[Saved phrase]`, and none is pre-selected.
3. EDIT requires the edited sentence; REJECT requires a reason (wrong meaning, wrong tone, wrong person/context, other).
4. Approving a sentence, saving it, and allowing it to be reused as an AI example are three separate choices.
5. A saved phrase or feedback record must belong to the same profile, conversation partner and context. HEALTHCARE records never appear in FAMILY or FRIENDS suggestions.
6. An AI request is only sent for the conversation, keywords and consent that existed when the user clicked; if any of them changed before sending, nothing is sent.
7. Each kind of record has a fixed capacity (for example 20 profiles, 1,000 phrases). When it is full, new records are refused with a message.
8. If the AI service is unavailable, the system shows matching saved phrases instead and keeps working.

---

## 2. System Design

### 2.1 Class Diagram
**Figure: UML class diagram** – `report/figures/uml.png` (source `uml.dot`, rendered with Graphviz). Insert it on a landscape page.

The diagram groups the 34 top-level Java classes by package, including `web` for the HTML connection and `ui` for report export. Hollow triangles show that `Communicator` and `Supporter` extend `User` and the three custom exceptions extend `Exception`. Dashed arrows show which class calls which. Hollow diamonds show that `FileManager` holds arrays of the model objects.

### 2.2 Class Descriptions
| Class Name | Type | Purpose / Responsibility |
| --- | --- | --- |
| `User` | Abstract class | Parent of all app users; declares abstract `getTypeCode()` and `canEditProfile()` |
| `Communicator` | Subclass of `User` | Person with aphasia; can always edit their own profile |
| `Supporter` | Subclass of `User` | Helper linked to a profile; can edit only when authorised |
| `CommunicationProfile` | Model | Language style, preferred tone and AI-sharing consent |
| `ConversationPartner` | Model | Partner name, context (FAMILY/FRIENDS/HEALTHCARE) and tone |
| `Phrase` | Model | A confirmed sentence with its keywords and AI-reuse consent |
| `Feedback` | Model | An APPROVE/EDIT/REJECT decision with a reason |
| `Suggestion` | Model | One candidate sentence and its source (AI or SAVED) |
| `Codes` | Constants | Allowed context, decision and reason codes; `isValid()` |
| `FileManager` | Storage | Arrays with counters, add/search/update/delete, `.txt` save and load, rollback, AI log |
| `RecordCodec` | Storage helper | Joins, escapes and splits `\|`-separated records |
| `AppController` | Controller | Current session, permission checks, request snapshot, AI-or-fallback flow |
| `RequestSnapshot` | Controller helper | Keywords, profile, partner and consent captured at the moment of the click |
| `DemoData` | Setup | Creates fictional demo data on the first run |
| `Main` | Entry point | Starts the HTML server through BrowserMain |
| `ConsoleMain` | Entry point | Loads data and starts the Scanner text menu |
| `ConsoleMenu` | Text frontend | Scanner menu that calls the same backend as the GUI |
| `InvalidInputException` | Custom exception | Invalid input or an action that is not allowed |
| `StorageFullException` | Custom exception | An array or the AI log is full |
| `AIServiceException` | Custom exception | Any AI failure (key, network, HTTP status, reply format) |
| `AIHelper` | AI | Sends the request with `HttpURLConnection` and turns the reply into suggestions |
| `AIConfig` | AI | Reads the endpoint, model and key from the local `config.txt` |
| `PromptBuilder` | AI | Builds the prompt from a `GenerationRequest` |
| `AIResponseParser` | AI | Hand-written JSON scanning; requires exactly three sentences |
| `GenerationRequest` | AI model | Immutable data allowed to be sent to the AI |
| `SuggestionService` | Logic | Saved-phrase fallback (up to three matches) |
| `MemoryManager` | Logic | Selects up to 3 phrases and 2 feedback records by keyword overlap |
| `CommunicationRules` | Logic | Validation and sharing rules |
| `FeedbackManager` | Logic | Builds and validates `Feedback` records |
| `BrowserMain` | Entry point | Serves HTML assets and handles local HTTP |
| `BrowserApp` | Browser adapter | Routes browser actions to Java services |
| `BrowserForm` | HTTP form helper | Parses and validates request fields |
| `BrowserRecords` | Storage adapter | Browser-specific records through FileManager |
| `ReportGenerator` | GUI helper | Builds and writes the plain-text report |

`[TO CHECK: B, C and D confirm their rows.]`

### 2.3 Inheritance and Polymorphism
The abstract class `User` holds the fields shared by every user (`userId`, `displayName`, `profileId`) and declares two abstract methods, `getTypeCode()` and `canEditProfile()`. `Communicator` and `Supporter` override both methods with different behaviour (Code 1).

```java
// Communicator
public boolean canEditProfile()
{
    return true;
}
// Supporter
public boolean canEditProfile()
{
    return supporterAuthorized;
}
```
*Code 1: `Communicator.canEditProfile()` and `Supporter.canEditProfile()` (src/mywords/model)*

`AppController` checks permissions through a **`User` reference** (Code 2). At run time Java calls the version of the actual object, so the same line allows a Communicator, allows an authorised Supporter and refuses an unauthorised Supporter. This check protects `createPartner()` and `updateProfileSettings()` (Figure 12).

```java
private boolean canEdit(User actingUser, String profileId)
{
    if (actingUser == null || profileId == null)
    {
        return false;
    }
    boolean sameProfile = profileId.equals(actingUser.getProfileId());
    return sameProfile && actingUser.canEditProfile();
}
```
*Code 2: `AppController.canEdit()` – polymorphic call through the parent type (src/mywords/app/AppController.java)*

Polymorphism is also used in file I/O. `FileManager.loadAll()` reads the type column of `users.txt` and creates either a `Communicator` or a `Supporter`, storing both in the same `User[]` array. `getTypeCode()` writes the type back when saving. Consent actions (turning AI sharing on, adding a Supporter) deliberately use `instanceof Communicator`, because consent must come from the Communicator even when a Supporter is authorised to edit settings.

### 2.4 Data Files
All files are in the `data/` folder, use UTF-8, start with a header line such as `#MyWords|1|PHRASES`, and separate fields with `|`. If a value contains `|`, `\`, a newline or a tab, it is escaped (`\|`, `\\`, `\n`, `\t`) by `RecordCodec`.

| File Name | Format | Example Line |
| --- | --- | --- |
| `users.txt` | `userId\|type\|displayName\|profileId\|supporterAuthorized` | `U002\|SUPPORTER\|Ah Hui (demo supporter)\|P001\|false` |
| `profiles.txt` | `profileId\|ownerUserId\|displayName\|languageStyle\|preferredTone\|aiSharingEnabled` | `P001\|U001\|Mei (demo)\|Manglish, short sentences\|casual\|false` |
| `partners.txt` | `partnerId\|profileId\|contextCode\|label\|preferredTone` | `CP001\|P001\|FAMILY\|Ah Ma\|warm` |
| `phrases.txt` | `phraseId\|profileId\|partnerId\|contextCode\|keywords\|confirmedText\|allowAIReuse\|createdAt` | `PH001\|P001\|CP001\|FAMILY\|kopi tomorrow\|Tomorrow kopi with Ah Ma lah.\|true\|1795596982988` |
| `feedback.txt` | `feedbackId\|profileId\|partnerId\|contextCode\|keywords\|candidateText\|decisionCode\|reasonCode\|confirmedText\|allowAIReuse\|createdAt` | `FB001\|P001\|CP001\|FAMILY\|kopi\|Please buy me coffee.\|REJECT\|WRONG_MEANING\|\|true\|1795596990000` |
| `ai_log.txt` | `createdAt\|requestId\|profileId\|partnerId\|contextCode\|displayedReply` | `1795600000000\|REQ1795600000000\|P001\|CP001\|FAMILY\|Tomorrow can kopi with Ah Ma?` |
| Exported report | Plain text written by `ReportGenerator` | `1. Tomorrow kopi with Ah Ma lah.` |

`createdAt` is the time in milliseconds since 1 January 1970. `[TO CHECK: replace the ai_log example with a real fictional row after B's live test.]`

---

## 3. Implementation Details

### 3.1 Use of Arrays (No Collections)
No class from the Java Collections Framework is used to store objects. `FileManager` keeps one fixed-size array per record type with a separate counter, for example `Phrase[] phrases = new Phrase[MAX_PHRASES]` and `int phraseCount`. The occupied elements are always `phrases[0]` to `phrases[phraseCount - 1]`, so every loop runs `for (int i = 0; i < phraseCount; i++)`. Searching is a linear loop that returns the index, or −1 if nothing is found. Getters return a new array of exactly the matching length, so callers can use `.length` and never receive `null`.

Adding (Code 3): an existing ID is updated in place; a new record goes into the first free element `phrases[phraseCount]`, and the counter increases. The capacity is checked before this method is called.

```java
private void putPhrase(Phrase phrase)
{
    int index = findPhraseIndex(phrase.getPhraseId());
    if (index != -1)
    {
        phrases[index] = phrase;
    }
    else
    {
        phrases[phraseCount] = phrase;
        phraseCount++;
    }
}
```
*Code 3: `FileManager.putPhrase()` – update or add (src/mywords/storage/FileManager.java)*

Removing (Code 4): every element after the removed one moves one place to the left, the last occupied element is cleared, and the counter decreases. When several records are removed, the loop runs backwards so that no element is skipped.

```java
private void removePhraseAt(int index)
{
    for (int i = index; i < phraseCount - 1; i++)
    {
        phrases[i] = phrases[i + 1];
    }
    phrases[phraseCount - 1] = null;
    phraseCount--;
}
```
*Code 4: `FileManager.removePhraseAt()` – shift left after deletion (src/mywords/storage/FileManager.java)*

New IDs come from `nextId(prefix)`, which uses the largest existing number plus one, not the counter, so deleting a record can never produce a duplicate ID.

### 3.3 Custom Exception Handling
| Exception | When It Is Thrown | Where It Is Caught / Handled |
| --- | --- | --- |
| `InvalidInputException` | Empty or too-long input; unknown context, decision or reason code; EDIT without text; REJECT without a reason; partner not in the profile; Supporter not allowed; conversation changed before sending; corrupt line in a data file | The browser shows the Java error response and the Scanner menu prints the error and the program continues; `FileManager.loadAll()` restores the previous arrays first; `AppController` restores changed fields first |
| `StorageFullException` | An array reaches its capacity (e.g. 20 profiles, 1,000 phrases) or `ai_log.txt` would exceed 5 MB | The browser and Scanner menu show the storage capacity error; `AIHelper` turns a full log into `AIServiceException` |
| `AIServiceException` | Missing or placeholder key, no network, timeout, HTTP error status, invalid or oversized reply, not exactly three sentences, AI sharing off | `AppController.requestSuggestions()` catches it and shows saved phrases with the reason (fallback); the app keeps working |

Code 5 shows the usual pattern. All checks run before the array is changed, so a refused record leaves the data untouched.

```java
public synchronized void savePhrase(Phrase phrase) throws InvalidInputException, StorageFullException, IOException
{
    backup();
    validatePhrase(phrase);
    if (findPhraseIndex(phrase.getPhraseId()) == -1 && phraseCount >= MAX_PHRASES)
    {
        throw new StorageFullException("Phrase storage is full (" + MAX_PHRASES + ").");
    }
    putPhrase(phrase);
    commit();
}
```
*Code 5: `FileManager.savePhrase()` – validate, check capacity, then change (src/mywords/storage/FileManager.java)*

Figures 11–14 show each exception being caught in the running program.

### 3.4 File Input and Output
**Saving.** Every change (save or delete) writes the `.txt` files immediately. Each file is first written to a temporary file (for example `phrases.txt.tmp`) with a `BufferedWriter` in UTF-8, and only after the write succeeds does the temporary file replace the real file. If writing fails, the old file is still complete.

**Rollback.** Before each change, `FileManager` copies its arrays and counters (`backup()`). If saving fails, `commit()` puts the copies back and reports that nothing was changed (Code 6), so the screen never shows data that is not on disk.

```java
private void commit() throws IOException
{
    try
    {
        saveAll();
    }
    catch (IOException e)
    {
        restore();
        throw new IOException("Could not save to " + dataFolder + ". Nothing was changed. (" + e.getMessage() + ")", e);
    }
}
```
*Code 6: `FileManager.commit()` – save or roll back (src/mywords/storage/FileManager.java)*

**Loading and restart.** On start, `Main` and `ConsoleMain` call `FileManager.loadAll()`. A missing file means a first run (no records). Each file must start with the correct header. Each line is split by `RecordCodec` into the exact number of fields. All records are first read into **temporary arrays** and then checked (duplicate IDs, links between profile, partner and records, valid codes). Only if every check passes do they replace the real arrays; otherwise the error names the file and line, and the existing data is kept. On the very first run, `DemoData` creates fictional records. Figures 9 and 10 show the files and the same data after a restart.

**AI log.** Each AI reply shown to the user is appended to `ai_log.txt` with its time, request ID, profile, partner and context; the prompt and the API key are never logged. The replies of one request are written in one operation (`appendAILogBatch`), so either all of them are logged or none.

**Exported report.** `ReportGenerator` writes the phrases chosen by the user to a plain-text file (Figure 8).

---

## 5. Requirements Checklist
| # | Requirement | Done? | Evidence (Figure / section) |
| --- | --- | --- | --- |
| 1 | Backend written in Java | Yes | §2.2; all classes in `src/mywords` |
| 2 | GUI frontend connected to the backend | Yes | §3.5; Figures 1–8 |
| 3 | Plain arrays with counters (no Collections) | Yes | §3.1; Code 3–4 |
| 4 | Inheritance and polymorphism | Yes | §2.3; Code 1–2; Figure 12 |
| 5 | Custom exceptions thrown and caught | Yes | §3.3; Figures 11–14 |
| 6 | Data saved to and reloaded from files | Yes | §2.4, §3.4; Figures 9–10 |
| 7 | AI feature | `[TO CHECK: Yes after PR #4 is merged]` | §3.6; Figures 4, 14, 16 |
| 8 | All planned features working | `[TO CHECK at code freeze]` | §4 (all figures) |

Additional requirements from the original announcement: Scanner text menu (`ConsoleMain`, Figure 15) and at least 10 classes (34 top-level Java classes, §2.2).
