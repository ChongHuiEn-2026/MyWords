# Report, video and submission plan

**Planning guide only.** Complete the actual lecturer template after the program exists. Do not submit this context pack as a completed project report or use proposal text in the past tense without evidence.

Use `ROLE_A.md`–`ROLE_D.md` for the current proposed ownership: A architecture/data, B AI integration, C communication logic, D UI/report generation. Each member should explain their own code contribution; the request does not identify which role the requesting student holds.

## Explicit deliverables

S1 p.2 §5 lists:

1. Complete project source, frontend and Java backend, `.txt` data and short running instructions, compressed into one `.zip`.
2. Filled lecturer report template, `.docx` or `.pdf`.
3. Recorded video presentation, **5–7 minutes**.

**Unresolved:** S2 says “all four items.” The fourth item is not identified in the supplied material. Confirm with the lecturer/portal; do not assume slides are required or that the discrepancy is resolved. S1 p.3's video-platform text is a bracketed example, so accepted format/platform and access rules need confirmation too.

Use filename stem `GroupNo_TrackNo_ProjectName`, for example `G05_T3_MyWords` only if the team is actually G05. Group number is unknown. Original announcement deadline: **10 December 2026 at 11:00 PM**, interpreted as MYT in the conversation and this plan. Reconfirm portal timezone. S2 requires both hackathon portal and Spectrum, or the lecturer-specified course platform.

## Report map to S1

| Template part | What to prepare | Evidence / proposed owner |
| --- | --- | --- |
| Cover, p.4 | Project title, track, group/lecturer, Java backend, actual frontend, submission date, video link, four names/matric numbers/roles | Actual team details / A + all |
| Contents, p.5 | Update fields/page numbers after editing | Final report review / A |
| 1.1 Overview, p.6 | Domain, target users and actual built system | Honest scope / all |
| 1.2 Objectives, p.6 | 3–5 assessable objectives | Proposed: keyword candidates, user choice, permitted memory, offline access, persistent data; reconcile with actual work |
| 1.3 Scope/User Roles, p.6 | Communicator/Supporter functions and business rules; identify deferred features | GUI/rules / A + all |
| 2.1 UML, p.6 | Image showing actual classes, fields, methods and inheritance arrows | Match final code / A |
| 2.2 Classes, p.6 | Class type and responsibility table | Actual class names and ownership / all |
| 2.3 Inheritance/Polymorphism, p.6 | What is overridden, different behaviors and where parent references invoke them | Code plus behavior / A |
| 2.4 Data Files, p.6 | Every `.txt` file, final line format and one sanitized example | Actual escaped file rows / A |
| 3.1 Arrays, p.7 | Counters and short add/remove snippets; no Collections | Actual loops/invariants / B + all |
| 3.2 Methods/Rules, p.7 | Important methods and enforcement, with short code snippets | Actual method names / all |
| 3.3 Exceptions, p.7 | Each exception, throw condition and catch location | Triggered examples / all |
| 3.4 File I/O, p.7 | Saving/loading, first run, restart and failure handling | Files before/after restart / B |
| 3.5 Frontend/Connection, p.7 | Tool, navigation and Java backend connection | Actual GUI/controller calls / A |
| 3.6 AI, p.7 | Useful feature, implementation, example input/output, permitted memories, configuration and fallback | Live synthetic example, HTTP/JSON explanation and logs / B + C + D |
| 4 Screenshots, p.7 | One per feature with captions; main screen, custom errors, output files and reloading | Real screenshots with fictional data / all |
| 5 Checklist, p.8 | Mark each requirement yes/no and cite figure/section | Evidence map; no automatic “Yes” / A + all |
| 6 Challenges, p.8 | Actual problems and how resolved | Work records, not invented stories / all |
| 7 Conclusion, p.8 | Actual achievements, learning and future work; limitations | No unsupported clinical/accuracy claims / all |
| 8 Individual Contribution, p.8 | Name/matric/tasks and completed classes | Actual contributions / each member |
| 9 References, p.8 | Every used book, website, documentation and tutorial in APA style | Verify original sources; no fabricated citations / all |

## Proposed video running order: 6 minutes 30 seconds

This is an outline to record after implementation, not a script claiming features work. Capture real behavior and actual results; if a test fails, repair it or describe the limitation honestly.

| Time | Speaker | Planned coverage |
| --- | --- | --- |
| 0:00–0:35 | A | All members, Track 3, project purpose, user/SDG scope |
| 0:35–1:40 | A | Main GUI navigation, Java backend connection, main classes and actual overridden role behavior |
| 1:40–2:50 | B | Arrays/counters, add/delete logic, created text files, save and restart/reload |
| 2:50–4:10 | B | Useful AI input/output, permitted prompt context, `AIHelper.ask`, HTTP/manual JSON, text log, missing-key/offline fallback |
| 4:10–5:25 | D | Confirm/edit/reject, explicit reasons, permitted memory retrieval, comparison evidence and limits |
| 5:25–6:30 | All, each their own errors | Deliberately trigger every custom exception and show catches; cover any main feature not yet shown; individual contribution recap and limitations |

Ensure every main feature is visible somewhere in the recording, not only described in narration. Each person must explain the part they worked on (S1 p.2). Avoid showing real credentials while walking through configuration. Use fictional data in every screen and file.

If code/error coverage cannot fit, rehearse and tighten repeated narration. The final recording must still meet 5–7 minutes; this outline does not waive coverage requirements. Keep video access unlisted/open to the evaluator if that is the confirmed delivery method.

## Final package checks (not completed)

- [ ] Lecturer questions answered and final scope settled.
- [ ] Source ZIP contains all source/build/project files, frontend, fictional `.txt` fixtures and clear run/config steps.
- [ ] Actual Java version, IDE/build steps, main entry point and working-directory/file-path assumptions documented.
- [ ] Evaluator can configure a key locally without a real key being shipped; offline fallback also documented.
- [ ] Fresh extraction compiles/runs and reloads data after restart.
- [ ] No real API key, private log, patient data or personal communication history included.
- [ ] Actual report and UML match code, file formats and completed features; all evidence links/captions work.
- [ ] Video is 5–7 minutes, all four members present, every required coverage item is visible.
- [ ] Filename stem uses confirmed group/track/project values.
- [ ] Video file/link format, accessibility and any fourth item confirmed against current instructions.
- [ ] Both required submission destinations checked after upload, with receipts retained.
