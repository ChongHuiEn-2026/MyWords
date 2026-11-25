# Requirements and source reconciliation

Status: source requirements summarized below; the “planned evidence” column is proposed and not yet demonstrated. Sources are defined in [SOURCES.md](SOURCES.md).

**Current proposed role mapping:** A owns architecture/core data; B owns AI integration; C owns MyWords communication features and logic; D owns UI/report generation. Role allocations are a current user decision; exact class interfaces remain proposed until the team agrees. They do not change source requirements.

## Lecturer PDF: confirmed from S1

| ID | Requirement | Location | Planned evidence / owner |
| --- | --- | --- | --- |
| P01 | Choose one track; define users, menus and business rules before building a custom project | p.1 §1; p.2 §4 | Track 3 Healthcare scope and rules / A + all |
| P02 | Include an AI feature appropriate to the track | p.1 §1 | Keyword-to-candidate-sentence demonstration / B, integrated by A/C/D |
| P03 | Java backend for classes, rules, data storage, file I/O and exceptions; frontend uses it for all processing | p.1 §2 | Architecture and connected GUI walkthrough / A + all |
| P04 | GUI compulsory; console-only program not accepted | p.1 §3 | Working GUI and main-feature screenshots / D, integrated by A |
| P05 | No Collections Framework classes for object storage; plain arrays with separate integer counters | p.2 §4 | Array/counter declarations and add/remove loops / A + C |
| P06 | At least one inheritance hierarchy and polymorphism, with overridden behavior | p.2 §4 | `User` subclasses used through parent references / A |
| P07 | Custom exceptions designed, thrown and caught correctly | p.2 §4 | Invalid-input, capacity and AI-error demos / all |
| P08 | Save all data to text files; reload correctly at restart | p.2 §4 | File schemas, created files and restart demonstration / A |
| P09 | Clear users, screens and business rules; planned features work | p.2 §4 | Acceptance checklist; honest final scope / all |
| P10 | Complete frontend/backend source and data `.txt` files plus short run instructions in one ZIP | p.2 §5 | Clean-machine source package review / A + D |
| P11 | Fill the provided report template; submit `.docx` or `.pdf` | p.2 §5; pp.4–8 | Completed template, evidence and APA references / all |
| P12 | Recorded presentation, 5–7 minutes, covering specified material; every member presents their contribution | p.2 §6; p.3 | Video and timed coverage map / all |
| P13 | Filename stem `GroupNo_TrackNo_ProjectName` | p.2 §5 | Actual group number, e.g. `G05_T3_MyWords` only if group is G05 / A |

## Original announcement: preserve additional S2 rules

| ID | Requirement | Planned response / owner |
| --- | --- | --- |
| O01 | Exactly four students, same OCC; team registration with UM SiswaMail compulsory | Confirm registration, member details and group number / A |
| O02 | One track and at least one UN SDG, explaining the connection | Healthcare; proposed SDG 3 and optional SDG 10 / all |
| O03 | Demonstrate Java topics Weeks 1–10; at least 10 Java classes | Meaningful class design plus rubric-to-code mapping / A + all |
| O04 | Text menu reading input through `Scanner` | Unresolved alongside mandatory GUI; retain plan for thin optional menu adapter until clarified / A |
| O05 | Arrays/counters and loops; `.txt` persistence, documented formats and generated reports saved to file | Same as P05/P08 plus report export / B + D |
| O06 | Implement menus and rules for every application user role; clear naming, indentation, class/key-method comments; compile and run in NetBeans or chosen IDE | Review and clean-run instructions / all |
| O07 | Every AI request through `AIHelper` and `String ask(String prompt)`; `HttpURLConnection`; String-based JSON construction and parsing, no external JSON libraries | B implementation walkthrough with fixtures / B |
| O08 | API key from `config.txt`; no key in source/GitHub; evaluator setup instructions | Placeholder config instructions and secret exclusion / B |
| O09 | Missing key/internet: throw `AIServiceException`, catch it, show clear fallback and keep other program features working | Offline saved-phrase and error demonstration / B + A + D |
| O10 | AI reply used in a useful feature; save every reply shown to a user in text log such as `ai_log.txt` | Candidate display and sanitized demo log / B + A + D |
| O11 | Deadline: 10 December 2026, 11:00 PM; submit to hackathon portal and Spectrum or lecturer-specified course platform | Reconfirm time/locations, upload and retain receipts / A |
| O12 | Announcement says “all four items” without listing all four in the supplied text | Ask lecturer/portal; fourth item unknown / A |

ILMUCode is recommended with sponsored credits in S2; it is not identified as the only permitted development tool or an exclusive AI provider.

## Conflicts and interpretation

| Topic | Sources differ | Working position pending clarification |
| --- | --- | --- |
| GUI vs Scanner | S2 specifies a Scanner text menu; S1 requires a GUI | Build GUI. Do not say Scanner was waived. Ask whether a separate text menu remains necessary; a thin Java menu adapter can reuse backend logic if required. |
| Inheritance / polymorphism / extra custom exceptions | S2 calls them optional rubric items, except mandatory AI fallback; S1 makes them mandatory | Follow the stricter PDF: plan all of them. |
| Number of deliverables | S2 says four; S1 lists exactly three | Prepare the three explicit PDF deliverables and obtain clarification. Never invent a fourth. |
| Exact AI rules and 10-class minimum | S1 does not repeat S2's details | Preserve them; absence is not an exemption. |
| Video platform | S1 p.3 uses a bracketed example of upload/link formats | Accepted platform, access settings and file constraints still need confirmation. |

No task is marked compliant merely because this pack describes a design. Completion needs actual code and evidence. See [SUBMISSION.md](SUBMISSION.md) for report mapping and [OPEN_QUESTIONS.md](OPEN_QUESTIONS.md) for resolution owners.
