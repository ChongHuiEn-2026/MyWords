# Open questions and unresolved discrepancies

Status as of 24 November 2026: **none of the answers below has been supplied.** This pack does not silently resolve them. A human teammate should contact the lecturer/portal; this pack has not sent any message.

## Lecturer / portal questions

| ID | Question | Source / significance | Owner / timing |
| --- | --- | --- | --- |
| Q01 | The original announcement says four submission items, but the report-template PDF lists source ZIP, report and video. What is the fourth item, if any? | S2 vs S1 p.2; no basis to assume slides | A; ask immediately, resolve before final package |
| Q02 | Is a separate Scanner text menu still required in addition to the mandatory GUI? | S2 specifies Scanner; S1 p.1 requires GUI and rejects console-only | A; ask before UI/adapter scope locks |
| Q03 | Do the original 10-class, `AIHelper.ask`, HttpURLConnection, manual JSON, config-key, logging and fallback rules still apply unchanged? | Not repeated in S1; retain them pending answer | A/B; early clarification |
| Q04 | Confirm exact deadline/timezone, both upload destinations, and latest submission instructions | S2 gives 10 Dec 2026, 11 PM; S1 has no deadline | A; early check and recheck before submission |
| Q05 | Accepted video format/platform, size limit and evaluator access settings? | S1 p.3 contains example placeholder text | A; before recording/upload |
| Q06 | Any required log retention/evidence constraints when a user deletes local communication data? | O10 logs displayed AI replies; proposed privacy deletion affects logs | A/B; use fictional evidence while awaiting answer |

Working position for Q01: prepare the three explicitly named deliverables and leave the fourth unknown. Working position for Q02: the submission GUI is HTML, and the Scanner menu is retained while the requirement is open. No lecturer answer may be invented or inferred from silence.

## Team decisions

| ID | Needed decision | Proposed default / dependency |
| --- | --- | --- |
| T01 | Four names/matric numbers, group number/name, same OCC, lecturer and registration status | Unknown |
| T02 | Confirm each member accepts the proposed A–D programming allocation; shared repository and change reviewer | Proposed role files; no repository URL supplied |
| T03 | Confirm the evaluator IDE and clean run steps | HTML selected on 27 Nov. JDK 17+, package `mywords`, browser entry point `mywords.web.BrowserMain`. |
| T04 | Provider, endpoint, model, credits, retention/data-use terms and API schema | Unknown; B verifies primary provider docs before coding |
| T05 | Contract v0.1: exact models, signatures, counters/capacities, schemas, response format and logging behavior | Review `INTEGRATION.md` together; no accepted contract yet |
| T06 | Minimum deliverable scope and deferred features | Typed/selected keywords, permitted memory, offline saved phrases proposed; repair/audio deferred |
| T07 | Consent wording, supporter authorization, context isolation and retention/deletion behavior | Defaults in `PRIVACY.md`; do not call them implemented controls |
| T08 | File-write recovery for phrase+feedback changes and deletion across files/logs | B proposes and demonstrates failure behavior; no atomicity claim |
| T09 | Evaluation scenarios, comparison conditions, repetitions, scoring and roles | D's synthetic protocol proposed; no data/results yet |
| T10 | Real-user or specialist feedback, if feasible | Unknown; synthetic testing first; real participation needs an appropriate agreed process |

Independent planning, fictional fixtures and GUI sketches can proceed while questions are open. Do not make real external disclosures, conflicting API implementations or final compliance claims that depend on unanswered questions.

## Suggested clarification text for a human to send

> Could you clarify two differences between the hackathon announcement and the report-template PDF? The announcement says four submission items, while the PDF lists source ZIP, report and a 5–7 minute video. Is a fourth item required? Also, must we provide a Scanner text menu in addition to the mandatory GUI? We plan to retain the original 10-class and AIHelper/HTTP/manual-JSON/config/logging/fallback rules unless told otherwise. Please also confirm the deadline/timezone, upload platforms and accepted video format/access settings.

Record the actual answer, date, source and changed requirements in [DECISIONS.md](DECISIONS.md), then notify all teammates to refresh their AI context.
