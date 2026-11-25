# Project context and proposed scope

## Identity and purpose

**MyWords — Let Me Finish My Sentence** is the team's selected idea for UM WIX1002 FOP Hackathon, Track 3: Healthcare. The proposed prototype helps people with expressive aphasia communicate everyday intentions from typed or selected keywords. It should preserve the person's wording choices, rather than equating fluent AI text with their actual intention.

This is a student communication-aid prototype. It is not a diagnostic tool, treatment, emergency decision system, verified interpreter or clinically validated product. Different people have different language, reading and motor needs; target-user suitability remains to be validated.

Proposed SDG alignment: **SDG 3, Good Health and Well-being**, through accessible communication support; **SDG 10, Reduced Inequalities**, through reducing communication barriers. These are intended contributions, not measured health outcomes.

## Application roles versus development roles

Application users are **Communicator** (person composing and confirming messages) and **Supporter** (person helping with explicitly authorized setup). These are distinct from developer Roles A–D in [ROLE_A.md](ROLE_A.md)–[ROLE_D.md). A supporter must not silently approve a message, grant AI-sharing consent or rewrite the communicator's preferences on their behalf. Role C's “clinical” scope means communication-support logic for this aphasia-focused app; it does not add diagnosis, treatment, medication advice or triage.

Proposed screens: home/profile selection, profile/preferences, context and keyword entry, candidate review, feedback, saved phrases and memory/privacy controls. Context labels begin with Family, Friends and Healthcare; partner-specific records can refine these.

## Proposed end-to-end flow

1. Select the active communicator profile and conversation partner/context.
2. Enter or choose keywords; allow a message to be composed manually without AI.
3. Review what may be sent to the AI service. If AI sharing is off, use saved phrases or manual composition.
4. Retrieve a bounded set of relevant, explicitly permitted memories for this profile and context.
5. Request three distinct candidate sentences. A timeout, unavailable key, malformed response or other AI failure leads to saved-phrase/manual fallback.
6. Present candidates as possibilities. The user may approve one, edit then confirm, reject with an optional reason, or choose “none of these.” Never auto-select the first candidate.
7. Treat message confirmation separately from permission to save and reuse it. “Use once” must remain possible.
8. If permitted, save the confirmed expression and/or explicit feedback; reload permitted records after restart. An edited sentence requires a fresh confirmation.
9. On later requests, use relevant permitted records as prompt context, not as unquestionable truth or a clinical record.

Illustrative keywords: `Tomorrow, Ah Ma, yum cha`. Any candidate generated from them is only an interpretation until confirmed.

## Minimum proposed features

| Feature | Priority | Boundary / acceptance intent |
| --- | --- | --- |
| Profile and partner/context selection | Core | Keep each person's memory and each context scoped |
| Typed/selected keywords and manual composition | Core | Useful path without a microphone or network |
| Three AI candidate sentences | Core | Clearly labeled candidates; support none/edited choice |
| Approve/edit/reject | Core | No automatic message confirmation |
| Saved phrase browsing and restart persistence | Core | Works offline; local-only records supported |
| Consent-based memory retrieval | Core | Saved does not automatically mean shareable with AI |
| Explicit rejection reasons | Differentiator to test | Distinguish wrong meaning, tone, person/context, other/unsure |
| Partner-specific style and Malaysian mixed-language expressions | Differentiator to test | User-selected preferences; no assumptions from ethnicity or age |
| Review/delete memory and AI disclosure | Core | Includes local log implications |
| Comparison with a generic baseline | Evaluation target | Real measurements required before improvement claims |
| Conversation repair | Stretch | Clarification choices still need user confirmation |
| Speech input/output, automatic recording, model fine-tuning | Deferred | Not part of the proposed first-version commitment |

## Business rules

- An approved interpretation is not a permission grant to store, share or reuse it.
- A rejection alone does not identify the intended meaning. If the user gives no reason/correction, do not invent one or infer a permanent preference.
- Healthcare wording must not add unconfirmed symptoms, severity, diagnosis, medicine or treatment details. Keep suggestions close to supplied intent and allow rejection.
- A private healthcare memory cannot leak into family/friend suggestions. No cross-profile retrieval.
- Saved phrases are an offline fallback; offline operation does not mean the AI can generate new personalized sentences without its service.
- Capacity, corrupt files and permission failures need understandable handling; no silent dropping or overwriting of records.
- Final committed scope must be reconciled with the report, because S1 requires planned features to work. Mark deferred ideas explicitly instead of presenting unfinished features as delivered.

## Malaysian slang and rojak phrasing

**Core MVP feature** (restored 25 Nov 2026 from the team's earlier docs; role letters updated to the current allocation).

MyWords is designed for everyday Malaysian speech. The first release should accept local terms, informal spelling and mixed-language keywords. When the communicator prefers casual or rojak phrasing, suggestions should preserve local expressions that fit the meaning. For a formal preference, offer suitable wording while preserving the same intent. Use the selected style and conversation context together. The communicator can review, edit or reject every suggestion.

These fictional examples are shared build and acceptance scenarios for A, B, C and D. The candidate in each row shows one acceptable possibility.

| Keywords | Selected style and context | Example candidate or expected behavior |
| --- | --- | --- |
| `later, tapau, nasi lemak, for me` | Casual rojak, Friends | `Later can tapau nasi lemak for me?` Keep the supplied local term. |
| `later, tapau, nasi lemak, for me` | Formal English, Friends | `Could you get me some nasi lemak to take away later?` Keep the request and food name while changing the register. |
| `tomorrow, Ah Ma, yum cha` | Casual mixed language, Family | `Tomorrow I want to yum cha with Ah Ma.` Keep the familiar terms where they fit. |
| `nurse, speak, now` | Formal English, Healthcare | `Could I speak with a nurse now?` Follow the chosen style and avoid inventing symptoms or adding advice. |

A checks that saved wording survives restart unchanged and is available offline. B implements the matching prompt behavior (profile `languageStyle`, partner `preferredTone`, context, keywords) and keeps the three-suggestion response contract. C keeps retrieval and fallback free of normalization or translation. D passes the entered words unchanged, lets the user edit before approval, runs the same cases across the GUI, generation and save/reload flow, and records what happened.

## Innovation hypothesis and evidence limits

The proposed mechanism is **retrieval-based personalization**: Java stores permitted feedback and supplies relevant examples to an existing LLM. No model weights are retrained. The question is whether this mechanism improves meaning preservation and reduces interaction effort in fictional Malaysian communication scenarios compared with a defined generic baseline.

Sentence completion, personalization and AAC feedback have prior work. The earlier chat identified overlap with existing products and research; see [SOURCES.md](SOURCES.md). Do not say “first,” “unique,” “competitors cannot do this,” “clinically effective” or “gets more accurate over time” without appropriate evidence. Malaysian language support and correction memory are proposed differentiators to investigate, not proven novelty.
