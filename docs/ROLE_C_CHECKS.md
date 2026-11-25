# Role C logic check record

Date: 25 November 2026 (MYT)

This record covers deterministic checks of `SuggestionService.savedPhraseFallback`
and `MemoryManager` using fictional phrases. It does not test AI generation or
claim clinical effectiveness.

## Scenarios and expected results

| Check | Expected result |
| --- | --- |
| Section 7 casual rojak keywords | Matching saved phrase is returned with its original local wording unchanged. |
| Section 7 formal Friends keywords | Matching saved phrase is returned unchanged. |
| Section 7 casual mixed Family keywords | Matching saved phrase is returned with `Ah Ma` and `yum cha` unchanged. |
| Section 7 formal Healthcare keywords | Matching saved phrase is returned without adding symptoms or advice. |
| Memory ranking | Higher distinct keyword overlap ranks first; lower overlap follows; result count respects the limit. |
| AI reuse consent | A matching phrase with `allowAIReuse=false` is excluded from AI memory selection. |
| Context isolation | A FAMILY request never returns a matching HEALTHCARE phrase, and vice versa. |
| Fallback matching and text | Matching is case-insensitive and word-based; saved slang and spacing are preserved; partial words do not match. |

## Run

From the project root in PowerShell:

```powershell
$classes = Join-Path $env:TEMP 'mywords-rolec-checks'
New-Item -ItemType Directory -Force -Path $classes | Out-Null
$sources = @(Get-ChildItem src -Filter *.java -Recurse | ForEach-Object FullName)
$sources += (Resolve-Path 'tests/mywords/logic/RoleCLogicChecks.java').Path
javac -encoding UTF-8 -d $classes $sources
if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
java -cp $classes mywords.logic.RoleCLogicChecks
if ($LASTEXITCODE -ne 0) { throw 'Role C checks failed' }
```

## Observed result

Passed on 25 November 2026. All project source files and the check harness
compiled, and all 7 scenario groups (25 assertions) passed. The available JDK
was 16.0.2, so this run checks compilation and behavior on Java 16; the
project's required Java 17 runtime still needs a separate check. The normal
`javac` launcher on this machine did not return, so the successful run used
`C:\Program Files\Java\jdk-16.0.2\bin\javac.exe` and the matching `java.exe`.

Scope note: `MemoryManager` filters by context and consent. It receives
profile/partner-scoped arrays from `FileManager` through `AppController`; this
harness checks context isolation, not profile/partner filtering inside
`MemoryManager` itself. These fictional checks do not establish clinical
effectiveness.

## Generic-versus-memory comparison plan (draft for D's review)

**Status:** Scenarios are prepared for review with D. No AI comparison has been
run and no output or improvement is claimed. B's AI generation must be working
before results can be collected.

### Fair comparison setup

- Use the same model, system instructions, keywords, profile style, partner
  tone, and context in both conditions. Change only whether eligible memory
  examples are included.
- **Generic condition:** send no saved phrase or feedback examples.
- **Memory condition:** include only examples returned by `MemoryManager`,
  with AI reuse consent, from the active profile and partner, and from the
  same context. Include no more than 3 phrases and 2 feedback records.
- Use fictional records only. Do not include a rejected candidate as approved
  intent. A confirmed correction can be included only if the team agrees the
  proposed D07 rule and the record has separate AI reuse consent.
- Capture both outputs for each scenario. Do not choose or save an output as
  the communicator's intent during this comparison.

### Matched scenarios

| ID | Keywords | Style / context | Permitted memory fixture for the memory condition | Expected review focus |
| --- | --- | --- | --- | --- |
| C1 | `later, tapau, nasi lemak, for me` | Casual rojak / FRIENDS | Consented FRIENDS phrase using unrelated local-food wording, such as `Can tapau curry puff for me later?`; no exact target sentence. | Does the memory output keep `tapau` naturally and retain the request for nasi lemak? |
| C2 | `later, tapau, nasi lemak, for me` | Formal English / FRIENDS | Consented FRIENDS phrase with a different request and formal tone, such as `Could you bring kaya toast later?` | Does the memory output keep the intent while using formal wording? |
| C3 | `tomorrow, Ah Ma, yum cha` | Casual mixed / FAMILY | Consented FAMILY phrase mentioning a different family activity, such as `I want to visit Ah Ma tomorrow.` | Does the output keep `Ah Ma` and `yum cha` without adding details? |
| C4 | `nurse, speak, now` | Formal English / HEALTHCARE | Consented HEALTHCARE phrase asking to speak with a doctor, such as `Could I speak with the doctor now?`; include no FAMILY examples. | Does the output ask to speak with a nurse without inventing symptoms or advice, and does the memory stay HEALTHCARE-scoped? |

Before running C1–C4, D should review the fixture wording and report capture
plan. For each scenario, run the generic and memory conditions with the same
input and settings. If the model is nondeterministic, collect three paired
runs per condition and retain every output; otherwise record the single paired
run and note that only one run was collected.

### Recording sheet (fill only after runs)

| ID | Generic output / evidence | Memory output / evidence | Intent preserved? | Style and local wording fit? | Context safe; no invented facts? | Notes / reviewer |
| --- | --- | --- | --- | --- | --- | --- |
| C1 | Pending | Pending | Pending | Pending | Pending | Awaiting B's AI integration and D's review |
| C2 | Pending | Pending | Pending | Pending | Pending | Awaiting B's AI integration and D's review |
| C3 | Pending | Pending | Pending | Pending | Pending | Awaiting B's AI integration and D's review |
| C4 | Pending | Pending | Pending | Pending | Pending | Awaiting B's AI integration and D's review |

These scenarios support a descriptive comparison only. Do not claim improved
accuracy from them without an agreed scoring method and enough collected
evidence.
