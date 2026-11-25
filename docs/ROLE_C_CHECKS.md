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
