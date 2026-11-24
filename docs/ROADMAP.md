# Development roadmap and evidence checklist

Planning baseline: **24 November 2026, Malaysia time (UTC+08:00)**.
Working final deadline from the earlier discussion: **10 December 2026, 11:00 PM**,
to be verified in the official portal. Intermediate dates below are proposed team
targets. Finish features by 7 December; reserve 8-9 December for submission work.

## Milestones

| Dates | Outcome | Owners / dependencies |
| --- | --- | --- |
| 24-25 Nov | Review context pack, settle scope and source questions, approve contracts | All; A coordinates |
| 26-27 Nov | Common Java project, UML, screen sketches, arrays and service stubs | A/B; C/D review record models |
| 28-29 Nov | Usable GUI without online AI: manual phrases, errors and save/restart | A/B; D verifies |
| 30 Nov-2 Dec | Java API integration, response validation, logs and offline fallback | C; depends on A/B stubs |
| 3-4 Dec | Explicit feedback memory, partner styles and mixed-language scenarios | D/C/B with A's consent UI |
| 5-7 Dec | Integrated acceptance tests, generic/personalized comparison, feature freeze | All; D coordinates evidence |
| 8-9 Dec | Final report, screenshots, source ZIP, 5-7 minute all-member video | All; A coordinates |
| 10 Dec | Fresh-machine/offline checks, upload early, verify receipt before 23:00 | All; final portal owner to be assigned |

## Context-pack setup

- [x] Inspect the existing repository and branches before preparing changes.
- [x] Recover project discussion and directly inspect the lecturer PDF.
- [x] Populate shared context, requirements, role guidance and proposed contracts.
- [x] Prepare a placeholder configuration and additive privacy ignore rules.
- [ ] Team reviews and merges the context-pack PR.

These completed items describe documentation setup only. All application tasks
below remain unchecked; this pack does not assert implementation progress.

## Phase 1: settle the plan (24-25 Nov)

- [ ] A: confirm names/roles, group number, same-OCC eligibility and report details.
- [ ] A: verify deadline/timezone and three-versus-four submission requirements.
- [ ] A: clarify the Scanner-menu requirement and any earlier extra constraints.
- [ ] All: approve MVP scope; keep conversation repair optional.
- [ ] All: review class fields, method signatures, capacities, formats and errors.
- [ ] A: settle JDK, NetBeans/build format, packages and branch workflow.
- [ ] A/C: settle consent UI, provider transmission, local logging and deletion.

## Phase 2: working offline application (26-29 Nov)

- [ ] A: draw UML, screens and backend connection; implement meaningful polymorphism.
- [ ] B: implement bounded arrays/counters and custom storage/input exceptions.
- [ ] B: implement UTF-8 text save/reload and recoverable failure behavior.
- [ ] A/B: profile/context selection, manual entry and saved-phrase GUI work.
- [ ] A: integrate stubs without blocking GUI navigation.
- [ ] D: verify empty state, Unicode, invalid input, full arrays and restart.
- [ ] All: demonstrate usable offline GUI by 29 November.

## Phase 3: online AI and feedback (30 Nov-4 Dec)

- [ ] C: safe local config loader and provider-approved HTTP request/response path.
- [ ] C: preserve `ask(String prompt)`; validate exactly three candidates.
- [ ] C/A: handle timeout, missing key, rate limit, malformed data and stale requests.
- [ ] C/B: log displayed AI candidates locally, with safe failure and clear behavior.
- [ ] A: saved phrases/manual entry work when AI is unavailable or declined.
- [ ] D/B: feedback and approved phrases remain distinct and persist correctly.
- [ ] D: only permitted memory influences later suggestions; deletion takes effect.
- [ ] C/D: test family/friends/healthcare context and English/BM/mixed-language inputs.

## Phase 4: verify and freeze (5-7 Dec)

- [ ] D: record generic versus personalized results using fictional matched scenarios.
- [ ] All: every promised main GUI feature works end to end.
- [ ] All: custom exceptions are thrown and caught with recoverable messages.
- [ ] B/D: test corrupt files, overflow, escaped text, restart and failed disk writes.
- [ ] A/C: test no consent, cancellation, rapid clicks and offline recovery.
- [ ] All: inspect tracked files and screenshots for secrets or personal data.
- [ ] A: count meaningful Java classes and reconcile final code with UML/contracts.
- [ ] All: feature freeze on 7 December; record known limitations honestly.

## Phase 5: submission (8-10 Dec)

- [ ] All: complete the lecturer report's pp.4-8, APA references and contribution rows.
- [ ] A/D: collect screenshots of each feature, each error and restart persistence.
- [ ] All: record and time a 5-7 minute video; every member explains their own work.
- [ ] B: package fictional `.txt` sample data; exclude keys, personal data and runtime logs.
- [ ] A: add verified build/run/JDK and offline-demo instructions to README.
- [ ] All: run the source ZIP in a clean location with documented setup.
- [ ] A: use actual group/track naming and confirm any fourth submission item.
- [ ] Assigned uploader: submit early on 10 December and verify uploaded files/link access.
- [ ] All: retain portal receipt and final source/report/video versions.

## Recording evidence

For each completed implementation task, attach a commit/PR, test result, screenshot
or report section. Use `date | owner | scenario | expected | observed | evidence`.
No implementation evidence is recorded yet. Keep private data out of evidence.
If dates slip, replan scope with the whole team; never quietly drop a mandatory
GUI, persistence, exception, array or submission requirement.
