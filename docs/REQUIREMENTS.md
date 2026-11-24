# Requirements and source status

Reviewed 24 November 2026. This file separates direct lecturer requirements from
earlier planning context and proposed implementation choices.

## Sources and precedence

1. **Directly inspected source:** the attached eight-page
   `FOP_Project_Report_Template.pdf` from the referenced “idea and role” chat.
   Pages 1-2 were also visually checked. Page numbers below refer to that PDF.
   The original PDF has not been added to this public repository.
2. **Earlier planning context:** the recovered conversation “idea and role”,
   specifically its lecturer review and complete project summary. The original
   announcement itself was not available for direct inspection. Treat its extra
   constraints as retained working requirements, not independently verified quotes.
3. **Team proposals:** this pack's class design, capacities, Swing choice,
   interfaces, formats and dates before the final deadline are review proposals.

For direct conflicts, follow the verified PDF baseline and seek written lecturer
clarification. Do not silently discard earlier requirements that can coexist.
Record the clarification source/date and update this file and all affected docs.

## Verified lecturer requirements

| ID | Mandatory requirement | PDF location | Planned evidence / owner |
| --- | --- | --- | --- |
| L1 | Choose one track; an alternative project must define users, menus and business rules | p.1 sec.1 | Track 3 scope, screens and rules; A |
| L2 | Include an AI feature relevant to the track | p.1 sec.1; p.8 checklist | Reviewed sentence suggestions and AI demo; C |
| L3 | Java backend for all classes, logic, storage, file I/O and exceptions | p.1 sec.2 | Java source and walkthrough; all |
| L4 | GUI required; console-only programs are not accepted | p.1 sec.3 | Working connected frontend; A |
| L5 | Frontend may use any tool, but must use the Java backend for processing; explain connection | p.1 sec.2; p.7 sec.3.5 | Proposed Swing-to-Java method calls; A |
| L6 | No Java Collections Framework classes to store objects; use plain arrays and separate integer counters | p.2 sec.4; p.8 checklist | Array declarations, add/remove/full behavior; B/D |
| L7 | At least one inheritance hierarchy and polymorphism with overridden methods | p.2 sec.4; p.6 sec.2.3 | `User` subclasses and runtime dispatch; A |
| L8 | Own custom exceptions, thrown and caught correctly | p.2 sec.4; p.7 sec.3.3 | Invalid input, capacity and AI errors; all |
| L9 | Save all application data to text files and restore it on restart | p.2 sec.4; p.7 sec.3.4 | Sanitized files plus restart demonstration; B |
| L10 | Define users, screens and business rules; every planned feature must work | p.2 sec.4; p.8 checklist | Scope and complete acceptance walkthrough; A/D |

The PDF mentions `java.util.*` when describing Collections. It prohibits those
classes **for object storage**; it does not independently establish a ban on every
utility class in that package. Do not use this distinction to introduce collection
storage indirectly through helpers, parsing libraries or stream operations.

## Team MVP requirement: Malaysian language support

The MyWords MVP includes Malaysian slang and rojak phrasing. This is a team
product requirement. The [shared examples](PROJECT_CONTEXT.md#malaysian-slang-and-rojak-phrasing)
define the target behavior across GUI input, AI suggestions, saved phrases and
testing. The selected style should guide suggestions without changing what the
communicator is trying to say. A, B, C and D each own a checkpoint in that flow.

## Earlier constraints retained pending confirmation

| Working constraint | Implementation intention | Verification status |
| --- | --- | --- |
| At least 10 meaningful Java classes | Architecture proposes 13 core classes plus custom exceptions | Earlier conversation; not specified in inspected PDF |
| `AIHelper.ask(String prompt)` | Preserve public entry point; proposed `String` return and checked exception in INTEGRATION | Earlier conversation; exact API not specified in PDF |
| API integration through `HttpURLConnection` | C implements provider-specific Java adapter with timeouts | Earlier conversation; PDF requires an AI feature, not this HTTP class |
| Offline fallback | Existing approved phrases remain usable if AI fails | Earlier conversation and project plan |
| Log every displayed AI response in `ai_log.txt` | C writes local, secret-free display log via B's file API; obtain consent | Earlier conversation; PDF does not name this file |
| Scanner text menu | Clarify if a separate console interface is still needed alongside compulsory GUI | Open; GUI cannot be replaced by a console menu |
| Four submissions in original announcement | Ask lecturer/portal to identify any additional item | Original announcement not directly inspected; PDF lists three |
| Four students in same OCC | Confirm eligibility and member details with team | Earlier conversation; cover template has four member rows |
| Deadline 10 December 2026, 11:00 PM Malaysia time | Plan to submit before 23:00 UTC+08:00 | Earlier conversation; deadline absent from inspected PDF |

## Submission package: three items in the PDF

The table on p.2 sec.5 lists:

1. **Source ZIP:** complete Java backend and frontend, source files, `.txt` data
   files and short run instructions. Use fictional/sanitized sample data and a
   placeholder configuration, never real credentials or personal communication data.
2. **Report:** complete the lecturer template and submit `.docx` or `.pdf`.
3. **Video presentation:** recorded walkthrough, **5-7 minutes** (p.2 sec.6).

Filename pattern: `GroupNo_TrackNo_ProjectName`, for example
`G05_T3_MyWords.zip` **only if** the real group number is G05. Group number,
lecturer name, matric numbers and names are not known and must not be invented.
Page 3 shows example video platform settings rather than one settled delivery
platform; check portal format/link access requirements.

**Open discrepancy:** the original announcement reportedly listed four items.
Do not assume a slide deck is the fourth. A must obtain the actual current list.

## Report and video evidence

The report (pp.6-8) needs introduction/objectives/scope and roles; UML and class
descriptions; inheritance/polymorphism; each text-file format with an example;
arrays and counters; key methods/business rules; custom exceptions and handlers;
save/reload; frontend connection; AI; screenshots; requirements checklist;
challenges; conclusion; individual contributions; and APA references. Complete
the cover details on p.4 and update the table of contents on p.5.

The video (pp.2-3) must introduce the group, track and purpose; explain Java
classes, arrays/counters, inheritance, exceptions, I/O, AI and frontend connection;
show every main GUI feature; show created output files and restart/reload; trigger
and catch each custom exception; and let every member explain their contribution.

## Questions to close and record

- [ ] A: confirm three versus four submissions and identify any fourth item.
- [ ] A: confirm Scanner menu in addition to GUI.
- [ ] A: verify deadline/timezone, group/OCC eligibility and portal naming rules.
- [ ] A/C: confirm earlier class count, `ask` signature, HTTP and logging constraints.
- [ ] A: confirm accepted AI use, third-party dependency policy, JDK and video format.
- [ ] All: record team approvals for contracts, privacy flow and test evidence.

Clarification log: **none recorded yet**. Use `date | question | lecturer/portal
source | answer | affected files` when an answer is obtained.
