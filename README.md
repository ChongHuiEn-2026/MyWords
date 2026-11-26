# MyWords AI Context Pack

Version: 0.2 | Prepared: 25 November 2026 | Timezone: Asia/Kuala_Lumpur (MYT, UTC+8)

> **👉 Start here: [docs/TEAM_HANDBOOK.md](docs/TEAM_HANDBOOK.md)**: what is built now, the real Java API, rules, each role's next tasks and how to run the app and checks. AI assistants: read [AGENTS.md](AGENTS.md) first.
>
> **Status (26 Nov 2026):** working Java code in `src/` (A storage/controller, C logic, D Swing GUI, plus a Scanner text menu). Role B's AI integration is in PR #4. Run the GUI with `mywords.app.Main` or the text menu with `mywords.app.ConsoleMain`. The rest of this README describes the original context pack.

This pack describes **MyWords — Let Me Finish My Sentence**, a WIX1002 Fundamentals of Programming (FOP) Hackathon project at Universiti Malaya, Track 3: Healthcare. The app scope remains the same; the revised development roles in `ROLE_A.md`–`ROLE_D.md` redistribute its programming work. Role names are development assignments, distinct from the application's Communicator and Supporter users.

## Balanced programming ownership

| Role | Main coding module | Comparable contribution |
| --- | --- | --- |
| A | Startup/controller, user-role classes, core arrays and file persistence | Builds and integrates the app backbone and CRUD/storage logic |
| B | AI configuration, prompt construction, HTTP gateway, response parser and AI exceptions | Implements and explains the external-service boundary and robust fallback errors |
| C | Suggestion workflow, feedback decisions, scoped memory search and communication rules | Implements the app's core personalization and meaning-preservation logic |
| D | Swing screens, input validation, confirmed-phrase report generation and text export | Implements the full user journey and user-controlled conversation export |

The work is balanced by implementation complexity and end-to-end ownership, not by counting Java files. Each member also contributes to integration, documentation and presenting their own code. Review this estimate together before coding and redistribute a module if the team's experience or time differs.

## Use this pack

1. Extract the ZIP and share the same version with everyone. Keep the lecturer PDF in `sources/` available for checking.
2. In your shared project, place `AGENTS.md` at the repository root and the `docs/` folder beside it. If an `AGENTS.md` already exists, reconcile its contents; do not overwrite unrelated instructions.
3. Give your AI the files in the reading order below. Some tools read `AGENTS.md` automatically; for others, upload the Markdown files or paste their text. Do not assume automatic loading.
4. State your assigned role A, B, C or D. Use the starter prompt in [AI_USAGE.md](docs/AI_USAGE.md).
5. Hold a short team review of [INTEGRATION.md](docs/INTEGRATION.md) and [OPEN_QUESTIONS.md](docs/OPEN_QUESTIONS.md) before separate implementation. These are proposed contracts, not previously agreed APIs. Confirm that programming effort feels comparable to each teammate.
6. Record accepted changes in [DECISIONS.md](docs/DECISIONS.md), update the canonical files, and tell every teammate to refresh their AI context. Chat-only agreements cause drift.

For a chat that accepts only one document, use the companion `MyWords_AI_Context_All_In_One.md`. It is a generated snapshot, not a second authority. Edit the individual files and regenerate it when the pack changes.

## Reading order and file map

| File | Purpose |
| --- | --- |
| [AGENTS.md](AGENTS.md) | Canonical shared instructions and status rules |
| [SOURCES.md](docs/SOURCES.md) | Source provenance, confidence and limitations |
| [REQUIREMENTS.md](docs/REQUIREMENTS.md) | PDF and original-announcement requirements, with evidence locations |
| [PROJECT_CONTEXT.md](docs/PROJECT_CONTEXT.md) | Purpose, scope, application roles, flow and innovation hypothesis |
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | Proposed Java classes, screens and error boundaries |
| [INTEGRATION.md](docs/INTEGRATION.md) | Proposed signatures, models, response handling and text-file schema |
| [PRIVACY.md](docs/PRIVACY.md) | Consent, local storage, logging, AI disclosure and deletion |
| [ROADMAP.md](docs/ROADMAP.md) | Dated plan and unchecked acceptance checklist |
| [ROLE_A.md](docs/ROLE_A.md) | Group leader, system architect and core data manager |
| [ROLE_B.md](docs/ROLE_B.md) | AI integration developer |
| [ROLE_C.md](docs/ROLE_C.md) | Clinical features and logic developer |
| [ROLE_D.md](docs/ROLE_D.md) | UI and report generator developer |
| [SUBMISSION.md](docs/SUBMISSION.md) | Report mapping, video outline and submission checks |
| [OPEN_QUESTIONS.md](docs/OPEN_QUESTIONS.md) | Discrepancies and missing decisions |
| [DECISIONS.md](docs/DECISIONS.md) | Status, change process and evidence ledger |
| [AI_USAGE.md](docs/AI_USAGE.md) | Copyable prompts and handoff format |
| [Lecturer PDF](sources/FOP_Project_Report_Template.pdf) | Original 8-page source document |
| [Original announcement](sources/ORIGINAL_ANNOUNCEMENT.md) | User-provided announcement transcribed from the referenced chat |

## Start here today

- [ ] Fill in member names, group number, OCC and lecturer.
- [ ] Agree on minimum features, GUI tool, Java version and proposed contracts.
- [ ] Ask the lecturer about the fourth submission item and whether Scanner is separately required alongside the GUI.
- [ ] Plan a working offline GUI milestone for 29 November; target development completion on 7 December.

The original announcement gives **10 December 2026, 11:00 PM** as the deadline. This pack uses Malaysia time from the conversation; reconfirm the portal's timezone and submission details. Do not infer a different deadline from the report template's blank date field.
