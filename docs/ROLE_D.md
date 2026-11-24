# Role D: personalization, feedback and testing

Assigned person: **to be confirmed**. Read [AGENTS](../AGENTS.md) and the shared
docs first. All model/method contracts remain **proposed for team review**.

## Your ownership

Own `Suggestion`, `Feedback`, `MemoryManager`, relevant-memory retrieval,
correction/rejection semantics, comparison design and integration testing. B owns
record storage and text I/O; C owns AI transport/prompts; A owns the user flow.
Do not build a second file system or AI API wrapper.

## Personalization behavior

Separate generated candidates from approved phrases and remembered feedback.
APPROVE records a confirmed candidate, EDIT records a confirmed final expression,
and REJECT is negative evidence only. A rejection without a reason is still a
valid user choice. Nothing is remembered without explicit permission and a current
remember action. Saving an approved phrase is a distinct user action.

Retrieve context only for the current communicator and permitted profile, using
bounded arrays with counters. Prefer the selected context/partner and relevant
keywords, returning at most the reviewed prompt limits. Use deterministic tie
breaking so the team can explain results. A rejected candidate must never become
a positive preference. Inspect/delete memory must affect both retrieval and disk
state; B performs actual persistence operations.

## Dependencies and collaboration

- A provides approve/edit/reject, remember/privacy choices and inspect/delete UI.
- B provides copies of stored records and validated persistence/deletion calls.
- C consumes bounded relevant phrases/feedback and produces three candidates.
- Confirm Suggestion/Feedback fields and constructors with C/B before coding.

## Acceptance scenarios

Record `expected`, `observed` and evidence using fictional data. These scenarios
are a verification plan, not claims that tests have passed.

| Area | Minimum meaningful scenarios |
| --- | --- |
| Core GUI | Profile/context -> keywords -> candidates -> approve/edit/reject -> return |
| Arrays | Empty, one record, exact capacity, one beyond capacity, delete then insert |
| Persistence | Save, close, restart; updates keep count; deletion survives restart |
| Text format | Unicode/mixed languages, tabs/backslashes/newlines, empty optional fields |
| Malaysian slang and rojak | Run the [shared examples](PROJECT_CONTEXT.md#malaysian-slang-and-rojak-phrasing). Check casual local wording, formal style, user edits and unchanged saved text after offline restart. |
| Bad files | Wrong version/columns, duplicate IDs, dangling references, invalid enum/time |
| Disk failures | Load error preserves files; failed save does not report success |
| Inheritance | Both User subclasses dispatched through a User reference |
| Exceptions | Each custom exception thrown and caught in a real GUI flow |
| AI failures | Missing key, timeout, rate limit, refusal, malformed/oversized response |
| Offline | Saved phrases/manual entry work with no network and no saved phrases |
| Privacy | No online consent, no remember consent, no cross-profile context, clear/delete |
| GUI races | Rapid clicks, profile/context changes, cancelled requests and withdrawn consent |
| Logging | Every displayed AI candidate logged; stale results skipped; log failure/full/clear |

## Comparison without overclaiming

Use the same fictional keywords, contexts and provider/model settings for generic
and personalized runs. Record the memory supplied, candidates, intended reference
expression, selected candidate, edits and rejection reasons. Repeat enough to
show variation and record failures too. Report the number of scenarios/runs,
limitations and observed behavior; do not claim clinical effectiveness from a
student demonstration or guarantee the AI learned the person's intent.

Suggested scenario: a fictional communicator explicitly prefers mixed English/BM
phrasing for family. Compare a generic result with a later result using a saved
correction, then delete the memory and verify it is no longer supplied to C.

## Delivery checklist

- [ ] Agree Suggestion/Feedback models and memory rules with A/B/C.
- [ ] Implement bounded retrieval, validation, consent-aware remembering and deletion.
- [ ] Verify approval/edit/rejection semantics and no cross-communicator leakage.
- [ ] Run the acceptance scenarios and retain honest evidence/known defects.
- [ ] Run matched generic/personalized comparisons with C.
- [ ] Record expected and observed slang and rojak results with A, B and C.
- [ ] Map results to the lecturer checklist, screenshots and report sections.
- [ ] Explain your individual contribution in the all-member video.

## Starting prompt for your coding assistant

> Read AGENTS.md, README.md, all shared docs and docs/ROLE_D.md. I am Role D.
> Inspect existing code before editing. Settle shared Suggestion/Feedback models
> and memory interfaces with A/B/C. Implement my approved personalization task
> using plain arrays/counters, explicit user permission and B's persistence API.
> Verify rejection, deletion, privacy and integration behavior with fictional data.
> Report actual results and limitations without clinical or novelty claims.
