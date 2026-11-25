# Proposed timeline and development checklist

Prepared 24 November 2026. Dates are MYT (Asia/Kuala_Lumpur). **All boxes are unchecked because no implementation evidence was supplied.** This is a new working checklist, not recovered progress from the earlier chat's interactive widget.

The source announcement deadline is 10 December 2026, 11:00 PM. There are 17 calendar dates from 24 November through 10 December inclusive; that is not a guarantee of 17 full working days. Reconfirm portal timing.

## Dated plan

| Dates | Proposed goal | Main owners | Exit evidence |
| --- | --- | --- | --- |
| 24 Nov | Confirm scope, roles, lecturer questions and shared context | All; A coordinates | Named owners, questions sent by a human, decision log |
| 25–26 Nov | Screens, UML, file schema, API contract, mock fixtures | A/B/C/D | Team-reviewed design and compile-ready shells |
| 27–29 Nov | Offline GUI and persistence | A/B; C/D integrate fixtures | **29 Nov milestone:** create/select profile, enter keywords, confirm manual/saved phrase, save and reload through GUI |
| 30 Nov–2 Dec | Real AI gateway, response handling, logging and fallback | B with A/D | Live synthetic request plus missing-key/offline/parse/log-error cases |
| 3–5 Dec | Feedback memory, scoped retrieval and controlled comparison | C with B/D | **5 Dec milestone:** explicit correction can affect an eligible later prompt; raw comparison records collected |
| 6–7 Dec | Integrate, fix defects, verify restrictions and restart behavior | All | **7 Dec development target:** end-to-end core scope demonstrated |
| 8 Dec | Feature freeze; report evidence and video rehearsal | All | Stable candidate build; known issues and deferred features recorded |
| 9 Dec | Finish report/video and rehearse clean evaluator setup | All | Reviewed report; 5–7 minute video; clean ZIP and access checks |
| 10 Dec | Early upload, verification and buffer | A; all available | Required portal/course submissions and receipts before 11 PM |

The earlier chat mentioned both finishing development by 7 December and freezing on 8 December. This plan keeps those as consecutive milestones. If a milestone slips, reduce stretch scope openly rather than relabeling incomplete work as done.

## Phase 1: decisions and compliance

- [ ] A: confirm four members from the same OCC and registration (O01).
- [ ] All: record names, group/track, lecturer, Java/IDE and roles; confirm each member accepts a comparable programming scope.
- [ ] All: review S1/S2 requirements and the GUI/inheritance corrections.
- [ ] A: obtain written clarification on fourth deliverable and Scanner menu; record answer and source.
- [ ] All: agree minimum features, deferred features, data consent and accessibility assumptions.
- [ ] All: review and accept one integration contract version before splitting files.
- [ ] All: map Weeks 1–10 topics to planned features without adding arbitrary classes.

## Phase 2: design and shared interfaces

- [ ] A: draw GUI navigation and UML, including real overridden behavior.
- [ ] B: agree field lists, IDs, array capacities, escaping, schema version and recovery behavior.
- [ ] B: confirm provider/endpoint/model/credits and permitted data use; define String-based envelope parsing.
- [ ] B/C: agree candidate format, memory budget and rejection-reason codes.
- [ ] B/D: create fictional fixtures with expected retrieval and permissions.
- [ ] A: create a compilable integrated skeleton and document actual run steps once available.

## Phase 3: 29 November offline milestone

- [ ] A: arrays and counters support add/search/update/delete and detect capacity limits; files survive restart.
- [ ] D/A: GUI selects/creates a profile and partner/context; invalid fields preserve user input.
- [ ] C/D: manual or saved phrases can be selected and confirmed without AI.
- [ ] D/C/A: saving and future-AI reuse have separate consent controls.
- [ ] A: missing initial files, corrupt existing files and save failures are handled visibly.
- [ ] All: demonstrate offline GUI flow and retain evidence; no mock is called live AI.

## Phase 4: AI and fallback

- [ ] B: local `config.txt` key loading with safe errors and evaluator setup notes.
- [ ] B: every AI request routes through `AIHelper.ask`, using `HttpURLConnection` and manual String JSON work.
- [ ] B: timeouts, HTTP failure, response size, quotes/escapes/Unicode and malformed bodies handled.
- [ ] B/C/D: exactly three validated candidate sentences reach UI; the user can edit, reject or choose none.
- [ ] A/B: exact displayed AI replies are logged without prompts/keys; log-write failure has a clear fallback.
- [ ] A/B/D: no blocked GUI; stale/cancelled response cannot target the wrong session/context.
- [ ] A/B/D: missing key/network triggers caught `AIServiceException`; saved phrases still work.

## Phase 5: personalization and evidence

- [ ] C/A: explicit permitted corrections and reasons are saved/reloaded accurately.
- [ ] C: deterministic array-based retrieval filters by profile, partner, context, consent and relevance.
- [ ] B/C: retrieved rejection is labeled as a rejection; unconfirmed meaning never becomes approved memory.
- [ ] D: prepare fixed fictional scenarios and intended meanings before running comparisons.
- [ ] D with B/C: run generic, memory-only and correction/context conditions with the same provider/model/settings and record inputs, outputs and latency.
- [ ] D: compute top-three intended-meaning match and edit/selection effort, record unsupported details/privacy leaks and disclose limitations. No invented improvements.
- [ ] All: test user-controlled memory withdrawal/deletion and next-request exclusion.

## Phase 6: integration, report and submission

- [ ] All: inspect actual code for Collections storage, external JSON libraries, missing comments and duplicate backend logic.
- [ ] A: exercise inheritance through parent references and demonstrate each application role's real rules.
- [ ] All: trigger and catch every custom exception for the report/video.
- [ ] B: verify files and reload after restart; confirm no silent data loss or ID collisions.
- [ ] A: compile/run cleanly in the chosen IDE with only documented steps.
- [ ] All: reconcile final promised features, UML, method names and data formats with actual code.
- [ ] All: complete lecturer report template with screenshots and references; update its table of contents.
- [ ] All: record a 5–7 minute video; every member explains their code contribution.
- [ ] C/B: remove real keys/private data; include fictional data and placeholder configuration instructions.
- [ ] A: verify actual group filename, source ZIP, report, video access and any confirmed fourth deliverable.
- [ ] A: submit to both required destinations, reopen uploaded artifacts and save receipts.

Completion needs evidence: use [DECISIONS.md](DECISIONS.md) to record commit/build, check performed, result and artifact location. Documentation-only readiness is not application completion.
