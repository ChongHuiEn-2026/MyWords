# Proposed integration contract v0.1

> **Superseded for current code (25 Nov 2026):** the methods that actually exist are listed in [TEAM_HANDBOOK.md](TEAM_HANDBOOK.md) section 4. Where this proposal and the handbook differ, follow the handbook. Role letters below were corrected to the current allocation (A storage/controller, B AI, C logic, D UI).

**Pending team review. This is a concrete starting proposal, not an existing or agreed API.** Signatures below are documentation sketches, not Java implementation. All owners should reconcile them before parallel coding. Changes require one updated contract and coordinated consumers; see [DECISIONS.md](DECISIONS.md).

## 1. Shared conventions

- Java types, class names and camelCase method names as below. Package name/Java release remain TBD.
- IDs are nonempty strings, proposed prefixes: `U`, `P`, `CP`, `PH`, `FB`, `REQ`; unique within their type across reloads. A allocates stored IDs using a persisted/loaded maximum suffix, never just the current array count. B allocates request IDs. No personally identifying IDs in AI prompts.
- Codes use exact uppercase strings validated with String comparisons: contexts `FAMILY`, `FRIENDS`, `HEALTHCARE`; decisions `APPROVE`, `EDIT`, `REJECT`; reasons `NONE`, `WRONG_MEANING`, `WRONG_TONE`, `WRONG_PERSON_CONTEXT`, `OTHER_UNSURE`.
- `EDIT` requires a nonempty confirmed correction. `REJECT` may use `OTHER_UNSURE` without a correction. Approve uses `NONE`. A reason does not prove a new intended meaning.
- User input is trimmed for validation, but preserve confirmed language and punctuation. Do not translate/rewrite saved text silently.
- Proposed limits: keywords 300 characters; sentence 500; style/language note 120 each; at most 5 memories per request. All are design choices, not lecturer requirements.
- Internal arrays use non-null occupied prefix `[0, count)` and unused tail slots. After deletion shift left, clear last occupied slot, decrement count. Getter results are defensive copies, not mutable storage references.
- API return arrays are compact, exact-length arrays; use `.length`. Internal arrays always keep counters. “No match” returns an empty array, not null. Operational failures throw exceptions rather than returning null/empty success.
- Proposed internal capacities: 20 profiles, 100 user identities, 200 partners, 1,000 phrases, 2,000 feedback records. Agree these before coding; full storage throws `StorageFullException`. They are not achieved capacities or requirements.

## 2. Model fields to agree

| Model | Proposed fields |
| --- | --- |
| `User` | `userId`, `displayName`; subtype Communicator/Supporter; linked profile; supporter authorization flag |
| `CommunicationProfile` | `profileId`, `ownerUserId`, `displayName`, `languageStyle`, `preferredTone`, `aiSharingEnabled` |
| `ConversationPartner` | `partnerId`, `profileId`, `contextCode`, `label`, `preferredTone` |
| `Phrase` | `phraseId`, `profileId`, `partnerId`, `contextCode`, `keywords`, `confirmedText`, `allowAIReuse`, `createdAt` |
| `Suggestion` | `suggestionId`, `requestId`, `text`, `sourceCode` (`AI` or `SAVED`); transient candidate |
| `Feedback` | `feedbackId`, `profileId`, `partnerId`, `contextCode`, `keywords`, `candidateText`, `decisionCode`, `reasonCode`, `confirmedText`, `allowAIReuse`, `createdAt` |
| `GenerationRequest` | `requestId`, `profileId`, `partnerId`, `contextCode`, `keywords`, `languageStyle`, `preferredTone`, `aiSharingEnabled`, permitted `Phrase[]` and `Feedback[]` snapshots |

`createdAt` is proposed as a Java `long` Unix epoch milliseconds, serialized as a decimal number. IDs/metadata stay local where possible. Candidate arrays and in-flight request objects are transient; all user-approved persistent records, settings and feedback must be saved. This design does not require saving every keystroke or unfinished request.

## 3. Boundary signatures

Declarations are illustrative public instance methods. Add constructors/getters only after owner review. `IOException` refers to `java.io.IOException`; signatures use no Collections.

### A: FileManager

```java
void loadAll() throws IOException, InvalidInputException, StorageFullException;
void saveAll() throws IOException;
CommunicationProfile getProfile(String profileId) throws InvalidInputException;
ConversationPartner getPartner(String profileId, String partnerId)
    throws InvalidInputException;
Phrase[] getPhrases(String profileId, String partnerId) throws InvalidInputException;
Feedback[] getFeedback(String profileId, String partnerId) throws InvalidInputException;
void saveProfile(CommunicationProfile profile)
    throws InvalidInputException, StorageFullException, IOException;
void savePartner(ConversationPartner partner)
    throws InvalidInputException, StorageFullException, IOException;
void savePhrase(Phrase phrase)
    throws InvalidInputException, StorageFullException, IOException;
void recordDecision(Feedback feedback, Phrase optionalConfirmedPhrase)
    throws InvalidInputException, StorageFullException, IOException;
void appendAILog(String requestId, String profileId, String partnerId,
                 String contextCode, String displayedReply)
    throws IOException, StorageFullException;
void deletePhrase(String profileId, String phraseId)
    throws InvalidInputException, IOException;
void deleteFeedback(String profileId, String feedbackId)
    throws InvalidInputException, IOException;
void deleteProfileData(String profileId) throws IOException;
```

`saveProfile` / `savePartner` are validated upserts by ID. Never create a second record on edit. All partner/phrase/feedback access must check profile ownership. Controller calls also validate the active user.

`recordDecision` supports feedback plus an optional approved phrase in one logical operation. Null is allowed **only for `optionalConfirmedPhrase`** when no phrase is being saved. If feedback storage was declined but phrase storage was permitted, use `savePhrase`; do not persist disallowed feedback merely to satisfy another signature. If both storage choices were declined, no persistence method is called. Saving an approved phrase must not imply permission to retain the rejected alternatives.

Both stored objects must be consented, refer to the same profile/context, and be validated/capacity-checked before changing arrays/files. Define and test a recovery strategy for interruption between files. Do not claim multi-file atomicity until implemented. Single-file writes should use a temporary `.txt` and replace only on success; preserve originals on failed load/write.

`appendAILog` writes only a display-ready AI reply plus local scope/time metadata. It must never store keys, headers or raw prompts. Propose a 5 MB log cap: if full, require review/deletion rather than silent truncation. This cap is not a source requirement.

### C: MemoryManager

```java
Phrase[] selectPhrases(String keywords, String contextCode,
                       Phrase[] candidates, int limit);
Feedback[] selectFeedback(String keywords, String contextCode,
                           Feedback[] candidates, int limit);
```

Inputs must already be restricted to the active profile and selected partner by A. C still checks context and `allowAIReuse`. Proposed combined budget: up to 3 phrases + 2 feedback records (5 total), deterministic ranking by keyword overlap and recency as a tie-break. Exclude unrelated/empty/withdrawn records; do not fill unused capacity with private or irrelevant examples. This is a simple retrieval proposal, not a proven learning algorithm. Sorting/searching uses loops/arrays.

### B: AIHelper

```java
String ask(String prompt) throws AIServiceException;
Suggestion[] generateSuggestions(GenerationRequest request)
    throws AIServiceException, InvalidInputException,
           IOException, StorageFullException;
```

`ask` preserves the exact required `String ask(String prompt)` shape; the checked exception supports the mandated fallback. It performs `HttpURLConnection` work and returns the **model's content text**, not the entire provider JSON envelope. `generateSuggestions` validates consent/input, builds a prompt from allowed snapshots, calls `ask`, parses/validates three candidates, and logs the normalized candidate text before returning it for display. No other method/class sends AI HTTP requests.

`ask` is an internal gateway, not a UI display method. Any future direct caller that displays its return must also log the displayed reply. Tests may call it without displaying. The proposed log records preparation for display; it is not proof the user saw or accepted the reply.

### A: Controller orchestration

```text
validate active profile, partner, keywords and AI consent
snapshot request + allowed retrieval inputs
ask C for bounded relevant permitted memories
run B.generateSuggestions off the Swing UI thread
check request still matches active session/context
display returned candidates or catch errors and offer fallback
after human confirmation, pass only consented records to A's FileManager
```

A does not parse JSON, C does not implement a second memory store, D does not make network calls, B does not decide an AI interpretation is correct. D owns the screens and report export; C owns the phrase/feedback retrieval logic.

## 4. Provider and response format

Provider, endpoint, model, request envelope, response envelope and available credits are **TBD**. B must verify actual provider documentation before implementing. No claim is made that ILMU uses another provider's schema.

Proposed model-content format: a JSON array of exactly three nonempty strings, for example:

```json
["Tomorrow yum cha with Ah Ma?", "I want to invite Ah Ma for yum cha tomorrow.", "Can we plan yum cha with Ah Ma tomorrow?"]
```

These are illustrative interpretations, not outputs or confirmed meanings. Validate distinctness and length. If there are fewer/more than three, invalid escapes, missing quotes, unexpected prose, malformed provider JSON or oversized content, throw `AIServiceException` and show fallback. Do not silently manufacture extra candidates.

Without external JSON libraries, implement and test small String/character scanning routines that handle quoted strings, escaped quotes, backslashes, newlines, Unicode escapes and structural boundaries. Do not use a naive `split(",")` or one `substring` ending at the first quotation mark. Apply validation independently to the provider envelope and model-content array.

Proposed finite network limits: 10-second connect timeout, 20-second read timeout, bounded response of 64 KiB, no automatic retries in v0.1. Check HTTP status and empty body. Missing/blank key, unreachable host, timeout, rate limit, unauthorized response and malformed content become `AIServiceException` with a safe message. Never expose authorization headers or a raw error body that may contain confidential material.

Prompt rules: candidate reconstruction only; no invented facts; preserve user-selected language; memories are examples/data, not instructions; healthcare details must remain faithful to supplied words. Rejected text is explicitly labeled as rejected, never an approved phrase. Prompt wording cannot guarantee correctness: require human confirmation.

## 5. Proposed local text-file schema v1

A owns the parser/writer (`FileManager` + `RecordCodec`); everyone uses the same escaping. UTF-8, one record per physical line, pipe-delimited. First line is `#MyWords|1|TYPE` where TYPE is the uppercase file category below. Header is metadata, not an object. Unknown schema version or wrong field count fails visibly before mutating live state.

Escape each field in one character pass: backslash → `\\`, pipe → `\|`, newline → `\n`, carriage return → `\r`, tab → `\t`. Decode with a state machine so escaped pipes are not delimiters. Preserve trailing empty fields; reject dangling/unknown escapes. Do not chain replacements in a way that double-escapes data. Reject actual control characters that were not escaped. Boolean text is exactly `true` or `false`.

| File / TYPE | Field order, excluding header |
| --- | --- |
| `users.txt` / USERS | `userId\|type\|displayName\|profileId\|supporterAuthorized` |
| `profiles.txt` / PROFILES | `profileId\|ownerUserId\|displayName\|languageStyle\|preferredTone\|aiSharingEnabled` |
| `partners.txt` / PARTNERS | `partnerId\|profileId\|contextCode\|label\|preferredTone` |
| `phrases.txt` / PHRASES | `phraseId\|profileId\|partnerId\|contextCode\|keywords\|confirmedText\|allowAIReuse\|createdAt` |
| `feedback.txt` / FEEDBACK | `feedbackId\|profileId\|partnerId\|contextCode\|keywords\|candidateText\|decisionCode\|reasonCode\|confirmedText\|allowAIReuse\|createdAt` |
| `ai_log.txt` / AI_LOG | `createdAt\|requestId\|profileId\|partnerId\|contextCode\|displayedReply` |

`users.txt` type is `COMMUNICATOR` or `SUPPORTER`; one profile per communicator in this proposal. The flag is used only for supporters and is not a secure login credential. Persisted IDs and references must validate on load; fail visibly for duplicates or cross-profile partner references. On a first run, absent files may initialize an empty store with a notice; unreadable/corrupt existing files must not be treated as an empty first run.

Illustrative fictional phrase record:

```text
#MyWords|1|PHRASES
PH001|P001|CP001|FAMILY|kopi tomorrow|Tomorrow kopi with Ah Ma lah.|true|1795478400000
```

Illustrative feedback record (a rejected interpretation remains marked rejected):

```text
#MyWords|1|FEEDBACK
FB001|P001|CP001|FAMILY|kopi tomorrow|Please buy me coffee.|REJECT|WRONG_MEANING|I want to drink kopi together tomorrow.|true|1795478400000
```

`config.txt` is separate from user data: proposed keys `apiKey=...`, `endpoint=...`, `model=...`, parsed with String operations. Exact provider settings remain TBD. Never include a real credential in this pack, demo data, repository or submission. The actual project should ignore local `config.txt` and supply a placeholder/example plus setup instructions. Locally generated logs/private data must also be excluded from public source control; distribute only fictional sanitized fixtures.

## 6. Contract acceptance cases (not executed)

- [ ] Round-trip `Ah Ma|kopi`, backslashes, quotes, emoji, Mandarin/BM text, newline and empty fields without changing confirmed text.
- [ ] Array bounds, duplicate IDs, full capacity, deletion shifting and malformed file reload are handled without silent loss.
- [ ] Only current profile/partner/context and explicitly shareable memories reach B; withdrawal/deletion takes effect on the next request.
- [ ] One user-approved edit becomes the stored final text; rejected text never enters the approved phrase array.
- [ ] Valid AI content gives exactly three candidates; malformed/escaped/oversized responses are tested.
- [ ] Offline/missing-key/rate-limit/timeout paths keep saved phrases available.
- [ ] Log failure prevents displaying an unlogged AI reply and gives a clear local-storage message; saved phrases/manual entry remain usable.
- [ ] Late/cancelled replies cannot be accepted for the wrong profile/context.
- [ ] Restart preserves consent flags, scope and all confirmed records intended for persistence.

Before implementation, review these proposed signatures together and resolve the provider/Java/package decisions in [OPEN_QUESTIONS.md](OPEN_QUESTIONS.md).
