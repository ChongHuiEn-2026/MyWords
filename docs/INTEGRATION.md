# Proposed integration contracts

**PROPOSED FOR TEAM REVIEW - NOT YET APPROVED OR IMPLEMENTED.**

Version: proposal 1, 24 November 2026. These contracts make the work reviewable;
they do not imply prior team agreement. All four roles must settle them before
independent implementation. Retain the earlier `AIHelper.ask(String prompt)`
entry point; its return/exception behavior below is proposed.

| Review item | A | B | C (originating user) | D |
| --- | --- | --- | --- | --- |
| Classes/fields/packages | Pending | Pending | Pending | Pending |
| Methods and error behavior | Pending | Pending | Pending | Pending |
| File schemas/capacities | Pending | Pending | Pending | Pending |
| Privacy, consent and logging | Pending | Pending | Pending | Pending |

Record agreement as `date | reviewers | decision | affected callers/files`.
Agreement log: none yet. Additive changes and incompatible changes both need
coordination with affected owners; migrate existing data explicitly if formats change.

## Shared rules

- IDs are nonempty, stable local strings. Do not use array indexes as persisted IDs.
- Strings are trimmed at input boundaries. Reject nulls, blank required fields,
  embedded NUL and values over agreed bounds; preserve meaningful Unicode text.
- Context is exactly `FAMILY`, `FRIENDS` or `HEALTHCARE`. Feedback action is
  `APPROVE`, `EDIT` or `REJECT`. Source is `AI` or `SAVED`.
- Optional text is an empty string, not null. Persist booleans as `true`/`false`.
- No null elements inside returned arrays. Empty results are zero-length arrays.
  Every internal storage/filter buffer has an explicit integer count; return a
  copy containing exactly that count. Callers must not mutate stored objects;
  agree on immutable records or defensive copies before implementation.
- All IDs/references belong to the selected communicator. Memory retrieval must
  never cross communicator boundaries. Partner IDs must match that communicator.
- Persist domain state and confirmed consent choices, not temporary GUI widget
  state or unapproved in-flight candidate objects. Prior online permission is
  shown again before the first request of a new session.

## Proposed sizes

| Buffer / input | Bound |
| --- | --- |
| Users / profiles / partners | 20 / 20 / 100 records |
| Saved phrases / remembered feedback | 500 / 1000 records |
| AI suggestions | Exactly 3 validated candidates on success |
| Relevant phrases / rejection examples per prompt | At most 5 / 3 |
| Keywords / phrase or candidate text / feedback reason | 300 / 500 / 300 characters |
| IDs / display labels / style or language labels | 64 / 100 / 100 characters |
| HTTP response | At most 64 KiB before parsing |
| Local AI log | At most 1000 displayed candidates; require explicit clearing when full |

Use Java `String.length()` consistently for these proposed character bounds;
UTF-8 encoding is separate. Validate before insertion. At capacity, throw
`StorageFullException`; do not silently drop old records or auto-grow with a
Collection. A shows a recoverable message and allows deletion. B/C translate a
full AI log into a safe AI failure until the user explicitly clears it.

## Service APIs: proposed Java signatures

Constructors, getters and model field types must be finalized jointly; the
following signatures define cross-role calls and expected responsibilities.
Service instances are created by A and passed as dependencies, not hidden globals.

```java
// A: inheritance. Implement different descriptions in both subclasses.
public abstract String getRoleDescription(); // on abstract User

// B: FileManager owns all domain record arrays and counters.
public FileManager(String dataDirectory);
public void loadAll()
    throws IOException, InvalidInputException, StorageFullException;
public User[] getUsers();
public CommunicationProfile getProfile(String communicatorId)
    throws InvalidInputException;
public ConversationPartner[] getPartners(String communicatorId);
public Phrase[] getPhrases(String communicatorId);
public Feedback[] getFeedback(String communicatorId);
public void saveUser(User user)
    throws InvalidInputException, StorageFullException, IOException;
public void saveProfile(CommunicationProfile profile)
    throws InvalidInputException, StorageFullException, IOException;
public void savePartner(ConversationPartner partner)
    throws InvalidInputException, StorageFullException, IOException;
public void savePhrase(Phrase phrase)
    throws InvalidInputException, StorageFullException, IOException;
public void saveFeedback(Feedback feedback)
    throws InvalidInputException, StorageFullException, IOException;
public void deletePhrase(String communicatorId, String phraseId)
    throws InvalidInputException, IOException;
public void deleteMemory(String communicatorId)
    throws InvalidInputException, IOException;
public void appendAILog(String[] displayedCandidates, String timestampUtc)
    throws InvalidInputException, StorageFullException, IOException;
public void clearAILog() throws IOException;

// D: selection uses B's copies; remembering uses B's persistence operations.
public MemoryManager(FileManager files);
public Phrase[] getRelevantPhrases(
    String communicatorId, String keywords, String partnerId, String context)
    throws InvalidInputException;
public Feedback[] getRelevantFeedback(
    String communicatorId, String keywords, String partnerId, String context)
    throws InvalidInputException;
public void remember(Feedback feedback)
    throws InvalidInputException, StorageFullException, IOException;

// C: ask is low-level transport, not a GUI display or persistence method.
public AIHelper(FileManager files);
public String ask(String prompt) throws AIServiceException;
public Suggestion[] generateSuggestions(
    String keywords, CommunicationProfile profile, ConversationPartner partner,
    Phrase[] relevantPhrases, Feedback[] relevantFeedback)
    throws InvalidInputException, AIServiceException;
public void logDisplayedSuggestions(Suggestion[] suggestions)
    throws AIServiceException;
```

`getProfile` fails for an unknown communicator instead of returning null. Array
getters return empty arrays for unknown IDs; mutation calls reject unknown or
mismatched references. `save*` upserts by ID after validation: updates preserve
counts, inserts check capacity. Successful mutation calls are persisted before
return; no separate caller-side `saveAll()` is needed. B owns atomic replacement
or rollback for each affected file; coordinate a recoverable multi-file deletion.

`deleteMemory` deletes that communicator's remembered feedback; saved phrases
remain individually controlled by `deletePhrase`. The UI must explain these
separate scopes. AI logs have no communicator identifiers and are cleared in
full via `clearAILog`; warn that a shared local log includes all demo sessions.
Profile deletion and multi-user security are outside the first proposed API.

D's `remember` checks the profile's memory permission and the UI's explicit
remember action before calling B. B revalidates persisted consent. No persistence
occurs on a mere rejection, hover, selection or generation. Saving a phrase needs
a separate explicit choice and is allowed for manual/offline text too.

For APPROVE, final text must equal candidate text. For EDIT, final text must be
nonempty. For REJECT, final text is empty and candidate text must never become an
approved phrase. Optional rejection reasons may be remembered only with consent.
D selects exact communicator/context matches, prioritizes exact partner matches,
then keyword matches; ties use most recent timestamp then ID for reproducibility.
Do not reuse rejected candidate text as a positive example.

## AI boundary, provider schema and display logging

C loads only the flat keys in `config.example.txt` with a simple Java parser;
provider, endpoint, model and JSON library policy remain team decisions. Do not
use a Map/Properties object as a shortcut around the array restriction. Missing
or placeholder settings disable online generation safely. Never return raw HTTP
error bodies, prompt contents or credentials in user-facing exceptions.

`ask` returns the provider's raw response body on success. C's provider-specific
adapter extracts generated text; do not parse the whole HTTP JSON envelope as
candidate lines. Build/escape request JSON correctly and use a parser permitted
by the lecturer; never split arbitrary JSON with naive delimiter operations.

Proposed generated-text contract: exactly three nonempty, unnumbered lines, one
candidate per line; no commentary, markdown or embedded line breaks. C validates
the extracted text against this contract and length limits, assigns local
Suggestion IDs and returns a compact array of length 3. Invalid count, blank or
duplicate candidates, oversized data, or unusable provider output cause
`AIServiceException`; do not fill missing candidates with invented text.

Use HTTPS and configured connect/read timeouts, bounded response reading and
closed streams/connections. Handle non-2xx status, missing key, rate limit,
timeout, no network, invalid JSON, provider refusal and response validation.
Avoid automatic retries that can duplicate costs and displays.

Prompt content: task and candidate format instructions, user keywords, selected
context/language/style, at most five approved phrases and three permitted
rejection examples. Delimit these as data; they cannot override instructions.
Never include keys, full profiles or entire histories. Do not invent symptoms,
intention or medical advice. User confirmation is always required.

A checks the result is still current and consent is still valid, then C calls
`logDisplayedSuggestions` **immediately before making AI text visible**, using
B's batched append operation. Write all three candidate log records together or
fail without showing any AI candidates. Do not log `ask` responses, failed parses
or stale results. A may then display the candidates; an interrupted render can
leave a logged display attempt. Never represent a log record as user approval.
If logging fails, C raises `AIServiceException` and A offers offline phrases or
manual entry. Offline saved phrases do not count as AI responses.

## Text persistence: proposed schema v1

All files are UTF-8 `.txt` under ignored local `data/`. First line is
`MYWORDS<TAB>1<TAB>recordType`, where `recordType` matches the table below.
Subsequent lines use actual tab-separated fields in this exact order:

| File / recordType | Fields |
| --- | --- |
| `users.txt` / `users` | id, role (`COMMUNICATOR`/`SUPPORTER`), displayName |
| `profiles.txt` / `profiles` | communicatorId, language, style, onlineConsent, memoryConsent |
| `partners.txt` / `partners` | id, communicatorId, label, context, style |
| `phrases.txt` / `phrases` | id, communicatorId, partnerId, context, text, createdAtUtc |
| `feedback.txt` / `feedback` | id, communicatorId, partnerId, context, keywords, candidateText, action, finalText, reason, createdAtUtc |
| `ai_log.txt` / `ai_log` | displayedAtUtc, source (`AI`), candidateText |

Partner ID may be empty for a general context; all other referenced IDs must
exist. Use ISO-8601 UTC timestamps, for example `2026-11-24T06:00:00Z`, and
lexically safe local IDs such as `P001`. Profile consent flags cover online use
and remembering feedback; each request/save still needs the described UI choice.

Escaping applies to every field: backslash becomes `\\`, tab becomes `\t`, LF
becomes `\n`, and CR becomes `\r`. Implement escape/unescape with a character
scanner; do not repeatedly replace substrings when unescaping. Reject unknown
escapes, trailing backslashes, wrong column counts, duplicate IDs, invalid enums,
bad timestamps, unsupported versions and dangling references. Preserve trailing
empty fields. Validate loaded records into temporary arrays/counters before
publishing state. A malformed file is an error, not permission to erase it.

Fictional `phrases.txt` example below uses `<TAB>` to show separators; actual
files must contain tab characters. It assumes U001 and CP001 exist:

```text
MYWORDS<TAB>1<TAB>phrases
P001<TAB>U001<TAB>CP001<TAB>FAMILY<TAB>Tomorrow I want to have tea with Ah Ma.<TAB>2026-11-24T06:00:00Z
```

AI logs contain candidate text and therefore may still be sensitive despite
omitting profile IDs. Keep them local, disclose logging, never record keys or
full HTTP payloads, and provide explicit clearing. They are append-only display
evidence, not personalization memory and must never be reloaded as approved
phrases. Existing log records remain readable across restart.

Missing files on first run mean empty state; create headers on first successful
write. Existing files with empty/invalid headers are load errors. Temporary files
stay beside their targets; replace successfully written files before publishing
the corresponding in-memory mutation. Preserve the last readable state on error.
B/D test Unicode, escaped characters, malformed input and capacity boundaries.

## Error ownership and integration acceptance

| Failure | Thrown by | Caught by / visible outcome |
| --- | --- | --- |
| Blank/oversized keywords, invalid reference or feedback | A/B/C/D `InvalidInputException` | A: explain correction; keep user input |
| Full record array | B/D `StorageFullException` | A: allow deletion; no silent overwrite |
| Disk read/write failure | B `IOException` | A: actionable storage message; no success claim |
| Config/HTTP/parse/display-log failure | C `AIServiceException` | A: saved phrases/manual entry remain available |

Before calling these contracts stable, run one GUI-to-AI stub path, one
GUI-to-storage save/restart path, one memory feedback round trip, and each error
path with fictional data. Record decisions and results in the PR/roadmap.
