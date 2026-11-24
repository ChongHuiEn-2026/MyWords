# MyWords project context

Status: shared planning baseline, 24 November 2026. Technical decisions remain
proposed until reviewed under [INTEGRATION.md](INTEGRATION.md).

## Purpose and audience

MyWords - Let Me Finish My Sentence is a WIX1002 FOP Hackathon project at
Universiti Malaya, proposed for Track 3 Healthcare. It explores communication
support for people who have difficulty expressing intended sentences, including
people with expressive aphasia. This is the project's intended audience, not a
claim that the prototype has been clinically assessed or is suitable for care.

The goal is to reduce the effort of constructing everyday expressions while
preserving the person's control over their own meaning. SDG 3 (Good Health and
Well-being) is the primary alignment; SDG 10 (Reduced Inequalities) is secondary.

## Users and main flow

The **Communicator** chooses words, approves the final sentence and decides what
to remember. A **Supporter** may help with settings or phrase setup only with the
communicator's permission. These are application roles, separate from developer
roles A-D. A role selector alone is not secure authentication; the MVP is proposed
as a local demonstration, not a multi-user clinical service.

1. Select a profile and context: Family, Friends or Healthcare.
2. Type or select keywords, such as fictional input `Tomorrow, Ah Ma, yum cha`.
3. If online AI consent is enabled, retrieve only relevant approved memory and
   request three candidate sentences.
4. Review suggestions; approve one, edit it, reject it with an optional reason,
   or return to manual entry. Never infer intent from silence.
5. Explicitly choose whether to remember the approved expression/correction or
   rejection feedback. Saving a phrase and learning from feedback are separate
   user choices.
6. Reuse saved phrases offline, inspect or delete memory, and reload saved data
   after restarting the application.

## Features and boundaries

| Scope | Feature and intended outcome |
| --- | --- |
| MVP | Accessible GUI with keyword entry and three reviewable AI candidates |
| MVP | Approve/edit/reject, with optional user-confirmed rejection reasons |
| MVP | Profiles, conversation contexts, saved phrases and text-file restart persistence |
| MVP | Permission-based memory retrieval and partner/context style preferences |
| MVP | Malaysian slang and rojak phrasing, including English/Bahasa Malaysia and terms supplied by the communicator |
| MVP | Offline saved phrases and clear AI/network error recovery |
| MVP | Memory view/delete and explicit consent for provider transmission and learning |
| Enhancement | Conversation repair after a misunderstanding, only after the MVP works |
| Out of scope | Speech recognition, microphone recording, model fine-tuning and cloud accounts |
| Out of scope | Diagnosis, symptom interpretation, treatment recommendations and medical record integration |

Aim for large readable text, generous controls, short labels, keyboard access and
few decisions per screen. Test with fictional scenarios first. Do not present
informal classmate feedback as evidence about patients or aphasia outcomes.

## Malaysian slang and rojak phrasing

MyWords is designed for everyday Malaysian speech. The first release should
accept local terms, informal spelling and mixed-language keywords. When the
communicator prefers casual or rojak phrasing, suggestions should preserve local
expressions that fit the meaning. For a formal preference, offer suitable wording
while preserving the same intent. Use the selected style and conversation context
together. The communicator can review, edit or reject every suggestion.

These fictional examples are shared build and acceptance scenarios for A, B, C
and D. The candidate in each row shows one acceptable possibility.

| Keywords | Selected style and context | Example candidate or expected behavior |
| --- | --- | --- |
| `later, tapau, nasi lemak, for me` | Casual rojak, Friends | `Later can tapau nasi lemak for me?` Keep the supplied local term. |
| `later, tapau, nasi lemak, for me` | Formal English, Friends | `Could you get me some nasi lemak to take away later?` Keep the request and food name while changing the register. |
| `tomorrow, Ah Ma, yum cha` | Casual mixed language, Family | `Tomorrow I want to yum cha with Ah Ma.` Keep the familiar terms where they fit. |
| `nurse, speak, now` | Formal English, Healthcare | `Could I speak with a nurse now?` Follow the chosen style and avoid inventing symptoms or adding advice. |

A checks that the GUI passes the entered wording and chosen style to Java and
displays the returned text. B checks that saved wording survives restart and is
available offline. C implements the matching prompt behavior and checks that the
three suggestions still follow the response contract. D runs the same cases
across the GUI, generation and save/reload flow and records what happened.

## Innovation hypothesis and evidence

The hypothesis is that explicit corrections and rejection reasons can improve
later suggestions for an individual's language and conversation partner. Sentence
completion and personalized communication aids already exist; do not claim this
project invented them. Any named competitor or research comparison needs current
primary sources before it is used in the report.

D should compare generic and personalized suggestions using the same fictional
keywords and provider/model settings. Record accepted choices, edits, rejection
reasons and failures. Report observed prototype behavior, sample size and limits;
do not turn a classroom comparison into a clinical efficacy claim.

## Privacy and control

The repository is public. All committed examples, screenshots, report evidence
and demo records must be fictional. Keep keys, profiles, conversations, feedback,
memory and AI logs local and excluded from Git.

Before online generation, explain what selected text will be sent to the chosen
provider and that displayed responses will be logged locally under the earlier
brief's logging requirement. Obtain permission; otherwise use offline phrases.
Local text files are not encrypted merely because they are ignored by Git.

Use stable local IDs in storage. Do not send names or whole histories by default;
send only the relevant context the user has allowed. Never save rejected text as
an approved preference. A supporter cannot grant themselves authority over the
communicator's words. Implement inspect/delete/clear behavior for saved phrases,
feedback and logs, and reflect deletion on disk and in memory.

Healthcare context may help a user express their own request, such as asking to
speak with a nurse. It must not turn keywords into invented symptoms, diagnoses,
medication instructions or claims about what the person intended.

## Team and deadline

The originating user owns C (AI). A owns coordination/GUI, B owns storage, and D
owns personalization/testing; names for A, B and D are pending. The working
deadline is **10 December 2026, 11:00 PM Malaysia time**, from the earlier planning
discussion, to be checked against the official portal. See [ROADMAP](ROADMAP.md).
