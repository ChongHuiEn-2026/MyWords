# MyWords

**Let Me Finish My Sentence** - a personalized communication aid prototype for
the Universiti Malaya WIX1002 Fundamentals of Programming Hackathon, Track 3:
Healthcare. A person enters keywords, reviews possible sentences, and chooses,
edits or rejects them. Only explicitly approved feedback becomes personal memory.

MyWords is designed for Malaysian everyday speech, including local slang and
rojak phrasing that mixes languages. Suggestions follow the communicator's chosen
style and can keep local expressions such as `tapau` or `Ah Ma` when appropriate.
See the [shared language behavior and examples](docs/PROJECT_CONTEXT.md#malaysian-slang-and-rojak-phrasing).

MyWords is an educational prototype, with no clinical validation or treatment
claims. See [project scope and privacy](docs/PROJECT_CONTEXT.md).

## Current status

As inspected on 24 November 2026, the repository contained only this project's
title and a standard Java `.gitignore` on `main`. This context pack adds the
shared development plan; **there is no runnable Java application yet**.
Architecture, method signatures and data formats are proposals awaiting team
review, not descriptions of implemented behavior.

Working deadline: **10 December 2026 at 11:00 PM Malaysia time
(Asia/Kuala_Lumpur, UTC+08:00)**, carried from the earlier planning conversation.
The lecturer PDF does not state this deadline; confirm it in the official portal.

## Start here

| File | Purpose |
| --- | --- |
| [AGENTS.md](AGENTS.md) | Shared instructions for every coding assistant |
| [PROJECT_CONTEXT](docs/PROJECT_CONTEXT.md) | Users, features, scope and project rationale |
| [REQUIREMENTS](docs/REQUIREMENTS.md) | Verified PDF requirements, earlier constraints and open questions |
| [ARCHITECTURE](docs/ARCHITECTURE.md) | Proposed Java classes, Swing flow and ownership |
| [INTEGRATION](docs/INTEGRATION.md) | Proposed signatures, arrays, persistence and error contracts |
| [ROADMAP](docs/ROADMAP.md) | Dates, checkboxes and delivery evidence |
| [ROLE_A](docs/ROLE_A.md) | Project coordination, GUI and inheritance |
| [ROLE_B](docs/ROLE_B.md) | Domain records, arrays and text persistence |
| [ROLE_C](docs/ROLE_C.md) | Originating user's AI integration responsibilities |
| [ROLE_D](docs/ROLE_D.md) | Feedback memory, personalization and testing |
| [config.example.txt](config.example.txt) | Placeholder configuration; contains no credentials |

All four teammates should read the shared files, then their role file. Give an AI
assistant access to the repository and ask it to read `AGENTS.md` and the assigned
role before coding. Other assistants may need these files attached explicitly.
No team member names have been assigned to A, B or D in this pack.

## Get the repository

```sh
git clone https://github.com/ChongHuiEn-2026/MyWords.git
cd MyWords
git switch -c feature/your-role-task
```

Start feature work from the team's approved base after this pack is reviewed.
Keep one focused PR per task and coordinate changes to shared contracts.

## Development setup: to finalize with the team

1. A confirms a common JDK version, NetBeans project/build format and package
   layout before generating project files. Swing is proposed; it is not yet set up.
2. Agree on the interfaces and data formats in `docs/INTEGRATION.md`.
3. For future AI development only, copy `config.example.txt` to `config.txt` in
   the working directory. C implements and documents its Java loader. The example
   is not executable configuration until that loader exists.
4. C confirms an approved API provider, HTTPS endpoint, model and response schema.
   Replace placeholders locally. Never commit `config.txt` or an actual key.
5. B implements local `data/` storage and checked-in fictional fixtures under
   `examples/data/`. Runtime data and logs are ignored.
6. When the app exists, A replaces this section with verified build/run commands,
   JDK version, entry class and offline-demo instructions. No guessed commands
   are presented as working setup here.

## Important requirements to settle

- GUI plus Java backend is mandatory; a console-only submission is unacceptable.
- Plain arrays with counters, inheritance/polymorphism, custom exceptions and
  text-file restart persistence are compulsory.
- The PDF lists **three** submissions: source ZIP, report and 5-7 minute video.
  The earlier announcement reportedly listed four. Confirm the fourth item;
  do not assume it is slides.
- Confirm whether the earlier Scanner text menu is required in addition to the GUI.
- Preserve the earlier ten-class target and `AIHelper.ask(String prompt)` plan
  while seeking clarification. Full source distinctions are in
  [REQUIREMENTS](docs/REQUIREMENTS.md).

## Safe demos and submissions

Use fictional data. Do not upload runtime `data/`, `ai_log.txt`, personal memory
or keys to this public repository. `.gitignore` helps prevent accidental additions
but does not remove files already tracked. The final ZIP must include sanitized
`.txt` sample data and run instructions, as required by the lecturer.

Saved phrases should work without network access. The user controls each final
sentence, memory updates and deletion. AI output is always a suggestion.
