# MyWords source skeleton v0.1

**Status: compilable shells only.** Classes, fields and method signatures follow the *proposed* `docs/INTEGRATION.md` v0.1. Most methods are `TODO` stubs. Nothing here means a feature works yet.

## Assumptions (team, please confirm or change)

- Java 17+, Swing GUI, package root `mywords`
- Plain `src/` folder (open in NetBeans as an existing-sources project)
- Runtime data folder `data/` (already ignored by `.gitignore`)

## Who owns which package

| Package | Owner | Classes |
| --- | --- | --- |
| `mywords.app` | A | `Main`, `AppController` |
| `mywords.model` | A (Feedback, Suggestion: C) | `User`, `Communicator`, `Supporter`, `CommunicationProfile`, `ConversationPartner`, `Phrase`, `Codes`, `Feedback`, `Suggestion` |
| `mywords.storage` | A | `FileManager` |
| `mywords.exception` | A (AIServiceException: B) | `InvalidInputException`, `StorageFullException`, `AIServiceException` |
| `mywords.ai` | B | `AIHelper`, `AIConfig`, `PromptBuilder`, `AIResponseParser`, `GenerationRequest` |
| `mywords.logic` | C | `SuggestionService`, `FeedbackManager`, `MemoryManager`, `CommunicationRules` |
| `mywords.ui` | D | `MainFrame`, `InputValidator`, `ReportGenerator` (+ new screen classes) |

Search for `TODO Role X` to find your work. Edit only your own package. If you need to change another owner's method signature, agree it with them and update `docs/INTEGRATION.md` first.

## Run

**NetBeans:** File → New Project → Java with Ant → *Java Project with Existing Sources* → add this `src` folder → main class `mywords.app.Main`.

**Command line** (from the repo root):

```
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out mywords.app.Main
```

Currently it opens a placeholder window. AI always fails on purpose (`AIHelper.ask` throws `AIServiceException`) so the saved-phrase fallback path can be developed first.
