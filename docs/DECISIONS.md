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
| 26 Nov 2026 | Role C logic checks on required Java version | `214fa1a`; all `src/` and `tests/` Java files; `RoleCLogicChecks` | JDK 17.0.12: compile with `javac --release 17 -encoding UTF-8`, then run `mywords.logic.RoleCLogicChecks` | Compilation succeeded; all 7 scenario groups (25 assertions) passed | `docs/ROLE_C_CHECKS.md` | C |

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
