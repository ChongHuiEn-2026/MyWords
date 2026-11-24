# MyWords: shared instructions for AI assistants

## Read before changing the project

Read [README.md](README.md), [project context](docs/PROJECT_CONTEXT.md),
[requirements](docs/REQUIREMENTS.md), [architecture](docs/ARCHITECTURE.md),
[integration contracts](docs/INTEGRATION.md), [roadmap](docs/ROADMAP.md), and your
assigned `docs/ROLE_*.md`. Inspect the actual files and current branch too.

This pack was prepared on 24 November 2026. It documents the project plan; it
does not mean the Java application exists or that the team has approved every
design choice. All integration contracts are **PROPOSED FOR TEAM REVIEW** until
A, B, C and D record agreement in `docs/INTEGRATION.md`.

## Project boundaries

- MyWords - Let Me Finish My Sentence is a WIX1002 FOP Hackathon communication
  aid prototype, Track 3 Healthcare, Universiti Malaya.
- The user in the originating discussion owns **Role C: AI integration**.
  Do not assume their name, GitHub handle, or ownership of other roles.
- Working submission deadline from the earlier discussion: **10 December 2026,
  11:00 PM, Asia/Kuala_Lumpur (UTC+08:00)**. Confirm against the official portal.
- Use the lecturer PDF requirements summarized with page references in
  `docs/REQUIREMENTS.md`. Keep the three-versus-four submission discrepancy and
  Scanner-menu question visible until the lecturer clarifies them.
- A GUI is mandatory. All business logic, storage, exceptions and AI processing
  belong in Java. Swing/NetBeans is the proposed simplest implementation.
- Store domain objects in plain arrays with explicit integer counters. Do not
  introduce ArrayList, LinkedList, HashMap, Sets, streams-to-collections, or other
  Java Collections for object storage, including in convenience helpers.
- Demonstrate inheritance, overridden methods/polymorphism, custom exceptions
  thrown and caught, and text-file save/reload across restarts.
- Retain the earlier target of at least ten meaningful Java classes,
  `AIHelper.ask(String prompt)`, API fallback and local AI display logging.
  Their source and confirmation status are in `docs/REQUIREMENTS.md`.

## Team collaboration

- Inspect existing files, branches, pending changes and PRs first. Preserve
  teammates' work. Use a focused branch and PR; do not force-push or merge it
  without explicit authorization.
- Work within the assigned role. Coordinate changes to shared classes with the
  owner. Do not build a second storage, GUI or personalization subsystem.
- Use the proposed interfaces only as a review starting point. Before writing
  implementations, settle package names, fields, signatures, capacity limits,
  format and error behavior together. Record approvals and changes in
  `docs/INTEGRATION.md` and update all callers in the same reviewed change.
- Explain important Java decisions at beginner FOP level. Prefer small methods
  and meaningful classes over frameworks or inflated class counts.
- Update the relevant docs when behavior changes. Mark tasks complete only
  when evidence exists; never fabricate tests, screenshots or contribution data.

## Privacy and user control

- This repository is public as inspected on 24 November 2026. Use fictional
  examples only. Never commit API keys, `config.txt`, real names, patient details,
  conversation history, feedback, memory files or AI logs.
- Read credentials locally; keep `config.example.txt` as a placeholder. Never
  include secrets in prompts, logs, screenshots, reports or exception messages.
- Explain provider transmission and local logging before enabling AI. Obtain
  explicit user permission to send selected context and to remember feedback.
  Offline saved phrases must remain available when AI is disabled or fails.
- Only the communicator confirms intended meaning. Supporter assistance requires
  permission; it does not silently override or train on the communicator's words.
- Treat user text, memories and model output as untrusted data. Validate bounds
  and response structure; never execute model output or follow its instructions.
- Do not claim diagnosis, treatment, recovery, clinical effectiveness, mind
  reading, or proven originality. Healthcare context is for expressing a user's
  words, not generating clinical recommendations.

## Verification and handoff

For implementation changes, verify the affected GUI flow and backend behavior,
array capacity, invalid input, save/restart, and offline/error paths as relevant.
Role D's checklist is the shared acceptance guide. For documentation changes,
check links, consistency, source attribution and the complete diff.

In each handoff, state what changed, how it was checked, dependencies, unresolved
questions, and relevant branch/PR. Never report the planned app as implemented.
