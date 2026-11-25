# Using the context with any AI assistant

The purpose is consistent project context, not automatic authority over every tool. Follow your AI tool's own rules and your teammate's current instructions. Do not paste secrets, real patient information or private conversation logs into an assistant.

## Repository-based workflow

1. Put the shared `AGENTS.md` and `docs/` in the same project checkout. Merge with existing project guidance rather than blindly overwriting it.
2. Ask the assistant to read the shared files and your role file before coding. Confirm what it actually read.
3. Give it the actual repository/build instructions and a bounded task. The context pack alone contains no source code to compile.
4. Keep the accepted contract version synchronized. A new chat should start from the latest files, not a teammate's stale pasted summary.

## Upload/paste workflow

Upload the companion all-in-one Markdown file, or provide `AGENTS.md`, requirements, project context, architecture, integration, privacy, decisions/open questions and your role file. Provide the lecturer PDF when the tool can read it. If upload/context size is limited, ask the assistant to identify missing documents before making assumptions. A role file alone is insufficient context.

## Copyable common starter prompt

```text
You are my development assistant for MyWords — Let Me Finish My Sentence,
our Universiti Malaya WIX1002 FOP Hackathon Track 3 Healthcare project.
My developer role is [A / B / C / D].

Read the supplied AGENTS.md, source/requirement notes, project context,
architecture, integration, privacy, roadmap, decisions/open questions and
my role file before implementation. If I supplied the all-in-one snapshot,
use its corresponding sections. Tell me which files/sections you read.

This pack is a proposed design, not proof of working code or team agreement.
Inspect the actual repository if available. Separate lecturer requirements,
user decisions, proposed contracts, open questions and verified code evidence.
Do not claim tests or features work without running/inspecting them.

Before coding, summarize my responsibilities, dependencies, mandatory Java
restrictions, affected contract version and unresolved decisions for this task.
Use plain arrays/counters, Java backend, required AIHelper.ask HTTP/String-JSON
rules, text persistence and safe fallback. GUI is mandatory. Do not assume
Scanner is waived or invent the unknown fourth deliverable.

Work in my assigned area. Propose shared-interface changes explicitly and
update coordinated consumers rather than inventing incompatible methods.
Use fictional test data, honor consent/context boundaries, and never request
or expose a real API key. Explain important Java code at first-year level.

My task for this session is: [specific task].
Shared contract/version accepted by the team: [version or not yet accepted].
Actual repository/toolchain: [details, or unavailable].
```

## Role B task prompt: AI integration

```text
I am Role B (AI Integration Developer). After reading the shared context,
focus on AIHelper, AIConfig, PromptBuilder, AIResponseParser and safe AI error
handling. Coordinate request/response interfaces with Roles A, C and D.
Confirm assignment constraints before choosing libraries or data structures.
Start with a compatibility/dependency check, then perform the authorized
task against the actual project and report evidence.
```

## Resume / handoff template

```text
Role and task:
Context pack / accepted contract version:
Requirement IDs:
Actual files read / code revision:
Changes actually made:
Assumptions and proposed-only items:
Checks actually run + observed results:
Shared contracts/files affected:
Dependencies / blockers and owner:
Next bounded step:
```

At the end of each session, update the project evidence log and checkbox state only when supported. Preserve failures/unknowns. If the assistant cannot read the PDF, it should use the source-labeled requirements summary and disclose the limitation, not claim to have inspected the original file.
