# Proposed Java architecture

**PROPOSED FOR TEAM REVIEW. No application classes are implemented by this pack.**
Confirm the JDK, project format, packages and [contracts](INTEGRATION.md) before
splitting implementation. Lecturer constraints are in [REQUIREMENTS](REQUIREMENTS.md).

## Shape of the application

Proposed frontend: Java Swing in NetBeans, sharing one Java process with the
backend. A owns screen navigation and a small coordination layer. Screens call
Java services; they do not duplicate validation, persistence or prompt logic.

```text
Swing screens / coordinator (A)
  |-- FileManager and domain records (B): arrays + counters + .txt persistence
  |-- MemoryManager (D): select relevant, permitted phrases and feedback
  `-- AIHelper (C): prompt -> HTTP -> validate candidates -> local display log
          `-- Suggestion[] (D's shared record) -> GUI for explicit user choice
```

The request flow is: A validates UI state, D retrieves allowed relevant context,
C generates/validates/logs candidates, and A displays them. A sends explicit user
feedback to D, which validates it and asks B to persist it. Saving an approved
phrase is a separate explicit action through B. Offline phrase retrieval uses B
without C or a network connection.

Long-running AI and file operations run off Swing's event dispatch thread. Use a
plain `Thread` and `SwingUtilities.invokeLater` if appropriate; avoid convenience
APIs that introduce collection storage. A disables duplicate requests and ignores
stale results after profile/context changes, cancellation or consent withdrawal.
Only the current permitted request may reach the display/log step.

## Proposed classes and owners

These 13 core classes exceed the earlier ten-class target without relying on
exception classes to reach it. Additional GUI/controller classes may be useful;
do not add empty classes just to increase the count.

| Class | Owner | Key data / responsibility |
| --- | --- | --- |
| `Main` | A | Load state, initialize services, launch GUI, handle startup failures |
| `User` (abstract) | A | Stable ID, display name; abstract `getRoleDescription()` |
| `Communicator extends User` | A | Override role description: chooses and confirms own words |
| `Supporter extends User` | A | Override role description: authorized setup assistance |
| `CommunicationProfile` | B | Communicator ID, preferred language/style, separate online and memory consent flags |
| `ConversationPartner` | B | ID, communicator ID, label, context and style preference |
| `Phrase` | B | ID, communicator/partner IDs, approved text, context, creation time |
| `Suggestion` | D | ID, candidate text, source (`AI`/`SAVED`), context; not approved memory |
| `Feedback` | D | ID, communicator/partner IDs, keywords, candidate, action, final text, optional reason, time |
| `MemoryManager` | D | Relevant context selection, feedback validation, permission-aware remembering/deletion |
| `FileManager` | B | Own bounded record arrays/counters and all text-file reads/writes |
| `AIHelper` | C | Local config, prompt construction, HTTP, parsing, AI display logging and error translation |
| `AIServiceException` | C | Checked AI/config/network/response/log failure with safe user message |

Also propose `InvalidInputException` (A coordinates, B/D use) and
`StorageFullException` (B owns). Standard `IOException` still represents disk I/O
failures. All custom exceptions must be thrown and caught in real flows.

`User[]` should contain both subclasses. Calling `getRoleDescription()` through
a `User` reference demonstrates dynamic dispatch. This is a teaching example of
polymorphism, not an access-control mechanism. Supporter permission checks are
explicit application rules and must not be inferred from that description string.

## Screens and backend responsibilities

| Screen | User action | Backend connection |
| --- | --- | --- |
| Home/profile | Choose communicator or permitted supporter assistance | B loads records; A controls role flow |
| Profile/privacy | Edit language/style and review transmission/memory permissions | B validates/persists profile; A explains choices |
| Context/keywords | Choose partner/context; enter short keywords | A validates; D retrieves permitted context |
| Suggestions | Review three candidates; approve/edit/reject or return | C supplies AI candidates; A keeps final user control |
| Feedback | Optional rejection reason and explicit remember choice | D validates; B persists only permitted feedback |
| Saved phrases | Read/select/save/delete approved expressions offline | B arrays and text I/O |
| Memory/settings | Inspect/delete memory; clear local AI log | D/B delete records; C/B clear log; A confirms destructive choice |

## Storage, errors and failure recovery

B uses `User[]`, `CommunicationProfile[]`, `ConversationPartner[]`, `Phrase[]`
and `Feedback[]`, each with its own integer count. D similarly uses bounded
arrays/counters for filtering and ranking. Boundary results are compact arrays;
do not expose mutable backing arrays to other roles. File formats and limits are
specified as proposals in [INTEGRATION](INTEGRATION.md).

On initial startup, missing data files mean empty state. Malformed existing files
produce a visible load error; do not overwrite them with an empty state. Validate
records and capacity before replacing loaded state. Persist mutations before
reporting success; disk errors must leave previous state recoverable.

An AI failure is caught by A, which offers existing saved phrases or manual entry.
An empty saved library shows a clear empty state; it never creates invented
offline sentences. Missing config, timeout, rate limit, malformed candidates and
log-write failure must not crash the application or expose credentials.

## Planned folder layout

```text
src/                   Java packages/classes (team decides exact layout)
docs/                  This shared context pack
examples/data/         Fictional .txt fixtures to add during implementation
data/                  Local runtime records and ai_log.txt; ignored by Git
config.example.txt     Safe, proposed configuration keys
config.txt             Local configuration/credentials; ignored by Git
```

Only `docs/` and the context/configuration documents exist in this setup change.
Do not assume `src/`, fixtures, a build file or a working GUI already exists.

## Decisions before implementation

- [ ] A: common JDK, package names, project/build format and screen sketches.
- [ ] A/B: meaningful inheritance fields, role checks and shared exception locations.
- [ ] B/D: fields/constructors, capacities and file version/escaping rules.
- [ ] C/D: exact suggestion and feedback construction/parsing contract.
- [ ] All: request cancellation, consent, deletion and failure behavior.
- [ ] All: approve INTEGRATION before treating its proposals as stable APIs.
