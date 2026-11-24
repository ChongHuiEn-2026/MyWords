# Role A: coordination, GUI and Java architecture

Assigned person: **to be confirmed**. The originating user is C, not automatically
the project leader. Read [AGENTS](../AGENTS.md) and the shared docs first.

## Your ownership

Coordinate scope, requirements clarifications, integration review and final
submission. Own `Main`, abstract `User`, `Communicator`, `Supporter`, Swing
screens/navigation and the coordination code connecting B, C and D. Coordinate
`InvalidInputException` placement so all roles share one definition.

Swing/NetBeans, class APIs and all layouts are **proposals for team review**.
Confirm a common JDK, build/project format and package structure before others
create competing project scaffolds. Do not put backend business rules in widgets.

## Dependencies and collaboration

- B supplies record constructors, array-backed lookups and persistent mutations.
- C supplies AI generation, display logging and safe AI failure messages.
- D supplies Suggestion/Feedback models and relevant memory/feedback validation.
- All four approve [INTEGRATION](INTEGRATION.md) before treating APIs as stable.

Build with deterministic fictional stubs first so the GUI is usable offline by
29 November. Keep network/file work off the GUI thread, suppress duplicate clicks
and discard stale responses after context, profile or consent changes.

## Rules to demonstrate

Use a `User[]` containing Communicator and Supporter and call an overridden method
through `User` references. Explain the differing behavior and keep actual supporter
permission checks explicit. Make large readable controls, keyboard navigation,
short labels and a clear route back to manual/saved phrases.

Show what leaves the device, what is logged locally and what will be remembered.
Keep online consent, explicit feedback remembering and saved-phrase actions
distinct. The communicator always confirms the final sentence. Catch custom
exceptions at the GUI boundary without dropping the user's input or exposing keys.

## Delivery checklist

- [ ] Confirm lecturer/portal questions in REQUIREMENTS and record answers.
- [ ] Agree project setup, screens, UML, fields, contracts and owners with all roles.
- [ ] Implement startup, role/profile/context flow and meaningful polymorphism.
- [ ] Connect saved phrases, keyword entry, suggestions, feedback and privacy screens.
- [ ] Handle empty state, validation, capacity, I/O and AI failures visibly.
- [ ] Keep the GUI responsive and reject stale/unauthorized results.
- [ ] Demonstrate offline GUI by 29 Nov and full integration by 7 Dec.
- [ ] Write verified README build/run instructions after the app exists.
- [ ] Coordinate report, source ZIP, video and final upload evidence.

## Starting prompt for your coding assistant

> Read AGENTS.md, README.md, all shared docs and docs/ROLE_A.md. I am Role A.
> Inspect existing Java files and branches before editing. Explain my dependencies
> on B/C/D and which contracts still need team agreement. Implement my approved
> GUI/architecture task with Java backend logic, plain arrays/counters and meaningful
> polymorphism. Preserve other owners' files and report verification and open questions.
