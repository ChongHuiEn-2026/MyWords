# MyWords source code

Use JDK 17 or newer. Run the HTML GUI from the project root with
`mywords.web.BrowserMain`. Follow the [README run instructions](../README.md#run-the-html-gui).
`mywords.app.Main` starts the same browser server. The Scanner text menu runs
with `mywords.app.ConsoleMain` and uses the same core Java backend.

| Package | Owner | Responsibility |
| --- | --- | --- |
| `mywords.app` | A | Launchers, Scanner menu, controller, request snapshots and demo data |
| `mywords.model` | A (Feedback and Suggestion: C) | Domain records and the user hierarchy |
| `mywords.storage` | A | FileManager and RecordCodec |
| `mywords.exception` | A (AIServiceException: B) | Custom input, capacity and AI exceptions |
| `mywords.ai` | B | AI configuration, prompts, HTTP, response parsing and request data |
| `mywords.logic` | C | Suggestions, feedback, memory and communication rules |
| `mywords.web` | D / integration | Local HTTP server, browser actions, form parsing and browser records |
| `mywords.ui` | D | ReportGenerator, shared by the browser and the Scanner menu |

See the [team handbook](../docs/TEAM_HANDBOOK.md) for the current Java API.
