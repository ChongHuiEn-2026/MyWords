# Decisions, changes and implementation evidence

## Current status

The original planning baseline dates to 24 November 2026. The [team handbook](TEAM_HANDBOOK.md) describes the current draft build.

| ID | Status | Statement | Basis |
| --- | --- | --- | --- |
| D01 | User-confirmed | Project idea is MyWords — Let Me Finish My Sentence | S3 selected final idea |
| D02 | Historical | An earlier context pack assigned the requesting student to Role C | Prior context; current request does not say which role the requester holds |
| D03 | Source requirement | Connected GUI and Java backend; inheritance/polymorphism/custom exceptions required | S1 pp.1–2 |
| D04 | Historical proposal | Initial GUI and integration design | Superseded for the GUI by D09 |
| D05 | Open | Fourth deliverable and Scanner-with-GUI discrepancy | S1/S2 conflict; Q01/Q02 |
| D06 | User-confirmed | Keep the same MyWords app and assign: A group leader/system architect/core data manager; B AI integration; C clinical features/logic; D UI/report generator. Balance the programming workload across all four roles. | Current direct user request and attached role/task screenshots |
| D07 | Proposed; needs D's review | A rejected candidate must never be reused as an example. If the communicator provides a correction after rejecting it, only that confirmed correction may be reused, and only with separate explicit AI-reuse consent. Rejection without a correction is not shared. | Role C code review on 25 November 2026; confirm with Role D before treating this as the team decision |
| D08 | Role B implementation; standalone live check passed | Role B uses the HTTPS Chat Completions request and response format. The endpoint, model and credential are deployment settings read from local `config.txt`. | [Role B handoff](ROLE_B.md) |

| D09 | Requester-confirmed, draft PR #4 | Use the Java-backed HTML interface as the submission GUI. Retain the Scanner menu while Q02 is open. | 27 Nov 2026 project direction. Team review and merge are pending. |
| D10 | Requester-confirmed (Role A), PR into `feature/role-b-ai`; team review pending | Redesign the HTML GUI for people with aphasia after stroke: no login wall (demo opens straight away), "Tap to speak" at the top, choose who you are talking to on the home screen, picture buttons, at most 2–3 taps to speak a sentence, Yes / No / Call someone always visible, a sidebar for everything else, back and next buttons. **Speech is in scope:** voice input uses the browser's Web Speech API (Chrome and Edge may send audio to their own speech service; only the text reaches Java, and a notice is shown before the first use); read-aloud uses the browser's speech voices. No AI request is made from the browser, so `AIHelper.ask` stays the only AI route. | 28 Nov 2026 direct request: "语音要做，跟谁说需要". Design sources: Stroke Association accessible information guidelines and aphasia-friendly formatting studies (large sans-serif text, bold key words, white space, one idea per screen, pictures with words). |

## Accepting or changing a design

1. State the problem, proposed change and affected owners/classes/files.
2. Separate a requirement correction from a design preference; cite the source if requirements change.
3. Get affected teammates to review shared signatures, file fields or behavior before incompatible implementation spreads.
4. Update canonical documents in one coordinated version; add migration notes if existing data/code will break.
5. Record the decision below and tell every teammate to reload context. If there is actual code, update producers/consumers and run appropriate checks.

No team acceptance is implied merely by receiving this pack. An accepted contract is still not proof it has been implemented.

## Decision log template

| Date MYT | ID / version | Decision and status | Source or approving teammates | Affected documents/code | Migration / next action |
| --- | --- | --- | --- | --- | --- |
| 25 Nov 2026 | D07 | Proposed: share only an explicitly confirmed correction from a rejected suggestion, with separate AI-reuse consent; never share the rejected candidate itself. Awaiting Role D review. | Role C proposal; no team approval recorded yet | `src/mywords/logic/CommunicationRules.java`, `docs/TEAM_HANDBOOK.md` | Ask D to review; then record the agreed rule and align the handbook/code if needed |

## Implementation evidence ledger

Use this only after inspecting actual work. Record failed checks as well as passes. Never fill a row from the planned architecture alone.

| Date MYT | Requirement/task | Actual code revision/files | Check performed | Observed result | Evidence artifact | Owner |
| --- | --- | --- | --- | --- | --- | --- |
| 26 Nov 2026 | AI config, prompt, parser and fallback | `src/mywords/ai/` | Compiled for Java 17 and ran Role B and Role C checks | 48 Role B assertions and all seven Role C groups passed. During development, failing checks reproduced a parser cursor error and rejection of valid blank preferences before fixes. | [Role B checks](../tests/mywords/ai/RoleBAIChecks.java) | B |
| 26 Nov 2026 | Swing and AI transport | `mywords.app.Main`, `AIHelper` | Exercised the Swing buttons with fictional data and a local HTTPS mock | Three unselected AI suggestions and three log rows on success. Saved phrases remained available with sharing off and after HTTP 401/429/503, a redirect, malformed JSON, oversized or invalid UTF-8 replies, timeout and connection failure. Live provider test pending. | [Role B handoff](ROLE_B.md) | B |
| 26 Nov 2026 | Standalone Role B API check | [Role B checks](../tests/mywords/ai/RoleBAIChecks.java) | Used a local HTTPS mock to test the opt-in check independently of Swing | One fictional request reached the mock and returned three logged suggestions. A placeholder key stopped the check before any request. Live endpoint verification is pending. | [Role B handoff](ROLE_B.md) | B |
| 26 Nov 2026 | Live service and AI reply formatting | [Role B checks](../tests/mywords/ai/RoleBAIChecks.java) | One fictional live request, followed by tests of a fenced JSON reply using a local HTTPS mock | The live service returned a response that failed array validation. No suggestions were returned. After the prompt and parser update, the mock returned three validated and logged suggestions. 54 Role B assertions and all seven Role C groups passed. See the standalone live retest below. | [Role B handoff](ROLE_B.md) | B |
| 26 Nov 2026 | Standalone live AI retest | [AIHelper](../src/mywords/ai/AIHelper.java) and [AIResponseParser](../src/mywords/ai/AIResponseParser.java) | Sent one fictional Family request with rojak wording and no memory examples | The service returned three distinct suggestions that Java parsed and logged before returning. The suggestions kept familiar terms but included details absent from the keywords. The live Swing flow remains pending the session and consent fix. | [Role B handoff](ROLE_B.md) | B |
| 26 Nov 2026 | Integrated consent and AI log batch | [AIHelper](../src/mywords/ai/AIHelper.java) and [Role B checks](../tests/mywords/ai/RoleBAIChecks.java) with the current `main` integrated | Compiled for Java 17, ran the Role B and Role C suites, and opened Swing with fictional data and a local HTTPS mock | 69 Role B assertions and all seven Role C groups passed. A stale click sent zero mock calls. The near-capacity check reproduced a partial row before the batch change and confirmed zero rows added after it. Swing showed three unselected mock suggestions with three log rows, and showed saved phrases with sharing off and after HTTP 429. Live Swing remains pending the enable-dialog disclosure. | [Role B handoff](ROLE_B.md) | B |
| 26 Nov 2026 | Role C logic checks on required Java version | `214fa1a`; all `src/` and `tests/` Java files; `RoleCLogicChecks` | JDK 17.0.12: compile with `javac --release 17 -encoding UTF-8`, then run `mywords.logic.RoleCLogicChecks` | Compilation succeeded; all 7 scenario groups (25 assertions) passed | `docs/ROLE_C_CHECKS.md` | C |

| 27 Nov 2026 | Role D HTML connected to Java | `mywords.web.BrowserMain`, `MyWords.html` | Compiled for Java 17, ran Role B/C and browser backend checks, and drove Chrome at desktop and phone widths with the mock provider | 69 Role B assertions and 7 Role C groups passed. Browser checks covered Java accounts and restart, scope, sharing off, saved fallback, approved mock replies, editing, saving, feedback and exporting selected phrases. Live-provider browser test remains to be performed by the communicator. | [Browser backend checks](../tests/mywords/web/BrowserChecks.java) | Integration branch |
| 27 Nov 2026 | HTML submission draft | `mywords.web.BrowserMain`, `mywords.app.Main`, `ConsoleMain` | Compiled for Java 17. Ran Role B and C checks, the browser integration suite, the Scanner capacity script and the fictional `kopi` flow in the browser. | 82 Role B assertions and 7 Role C groups passed. Browser checks passed for three mock options, short/repeated replies, saved-phrase fallback, logging and restart. The browser displayed the saved Ah Ma phrase with AI sharing off. The Scanner menu reported full profile storage. Live provider wording remains open. | [Browser checks](../tests/mywords/web/BrowserChecks.java) | Draft PR #4 |
| 28 Nov 2026 | D10 redesign and speech | `MyWords.html`, `browser.js`, `mywords.css`, `BrowserMain`, `BrowserApp`, `BrowserChecks` | Compiled for Java 17; ran Role B, Role C and browser checks; drove Chrome (Playwright) at 390 px and 1366 px with a mock AI provider in English and Chinese, light and dark | 82 Role B assertions, 7 Role C groups and the browser suite passed, including new checks: a rejected suggestion cannot be chosen again while the other two stay available, `localhost` and `127.0.0.1` are accepted but other host names are refused, and phrases carry their save time. Picture → Use this → spoken took 2 taps; consent, reject reasons, save choices, saved-phrase list, add person, write-my-own and voice notice all worked with no console errors. Voice input could not be tested in the headless browser (microphone blocked there); it needs a manual check in Chrome or Edge with a real microphone. | [Browser checks](../tests/mywords/web/BrowserChecks.java) | A (requested), affects D |

## Shared-contract change request template

```text
Change ID / proposed version:
Reason and requirement source (if any):
Current contract:
Proposed contract:
Affected owners/callers/files:
Data migration or backward compatibility:
Checks to run:
Team decision and date:
Canonical files updated:
```
