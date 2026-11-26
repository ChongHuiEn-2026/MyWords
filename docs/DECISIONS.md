# Decisions, changes and implementation evidence

## Current status

Pack v0.1 prepared 24 November 2026. **Design proposed; no implementation inspected.**

| ID | Status | Statement | Basis |
| --- | --- | --- | --- |
| D01 | User-confirmed | Project idea is MyWords — Let Me Finish My Sentence | S3 selected final idea |
| D02 | Historical | An earlier context pack assigned the requesting student to Role C | Prior context; current request does not say which role the requester holds |
| D03 | Source requirement | Connected GUI and Java backend; inheritance/polymorphism/custom exceptions required | S1 pp.1–2 |
| D04 | Proposed | Swing, class design, method/data contracts, privacy controls and dated plan in this pack | New synthesis; no team acceptance supplied |
| D05 | Open | Fourth deliverable and Scanner-with-GUI discrepancy | S1/S2 conflict; Q01/Q02 |
| D06 | User-confirmed | Keep the same MyWords app and assign: A group leader/system architect/core data manager; B AI integration; C clinical features/logic; D UI/report generator. Balance the programming workload across all four roles. | Current direct user request and attached role/task screenshots |
| D07 | Proposed; needs D's review | A rejected candidate must never be reused as an example. If the communicator provides a correction after rejecting it, only that confirmed correction may be reused, and only with separate explicit AI-reuse consent. Rejection without a correction is not shared. | Role C code review on 25 November 2026; confirm with Role D before treating this as the team decision |
| D08 | Role B implementation; standalone live check passed | Role B uses the HTTPS Chat Completions request and response format. The endpoint, model and credential are deployment settings read from local `config.txt`. | [Role B handoff](ROLE_B.md) |

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
