# MyWords source code

**Status and the real API: see [`docs/TEAM_HANDBOOK.md`](../docs/TEAM_HANDBOOK.md).** A, C and D have working v1 code; B (AI) is still a stub, so the app always falls back to saved phrases.

## Assumptions (team, please confirm or change)

- Java 17+, Swing GUI, package root `mywords`
- Plain `src/` folder (open in NetBeans as an existing-sources project)
- Runtime data folder `data/` (already ignored by `.gitignore`)

## Who owns which package

| Package | Owner | Classes |
| --- | --- | --- |
| `mywords.app` | A | `Main`, `AppController`, `DemoData` |
| `mywords.model` | A (Feedback, Suggestion: C) | `User`, `Communicator`, `Supporter`, `CommunicationProfile`, `ConversationPartner`, `Phrase`, `Codes`, `Feedback`, `Suggestion` |
| `mywords.storage` | A | `FileManager`, `RecordCodec` |
| `mywords.exception` | A (AIServiceException: B) | `InvalidInputException`, `StorageFullException`, `AIServiceException` |
| `mywords.ai` | B | `AIHelper`, `AIConfig`, `PromptBuilder`, `AIResponseParser`, `GenerationRequest` |
| `mywords.logic` | C | `SuggestionService`, `FeedbackManager`, `MemoryManager`, `CommunicationRules` |
| `mywords.ui` | D | `MainFrame`, `InputValidator`, `ReportGenerator` (+ new screen classes) |

Search for `TODO Role X` to find your work. Edit only your own package. If you need to change another owner's method signature, agree it with them and update `docs/TEAM_HANDBOOK.md` section 4 in the same commit.

## Run

**NetBeans:** File → New Project → Java with Ant → *Java Project with Existing Sources* → add this `src` folder → main class `mywords.app.Main`.

**Command line** (from the repo root):

```
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out mywords.app.Main
```

On first run it creates fictional demo data in `data/` (delete that folder to reset). Try: Mei + Ah Ma, keyword `kopi`, Get suggestions.
