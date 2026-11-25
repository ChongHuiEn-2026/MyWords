# Proposed privacy and user-control rules

These are engineering requirements proposed for the prototype, not a claim of legal compliance, encryption, secure authentication or clinical readiness. Use fictional profiles and communication histories for development, evaluation, screenshots and submission.

## Separate the decisions

The UI should distinguish:

1. **Confirm this message:** the sentence expresses what the person wants to communicate now.
2. **Save this phrase locally:** optional storage for later browsing, including offline access.
3. **Save this correction/feedback locally:** optional retention of the chosen reason and correction; do not force retention of rejected alternatives.
4. **Allow this saved record in future AI requests:** a separate opt-in on each phrase/feedback record.
5. **Enable external AI for this profile:** profile-level permission for current keywords, chosen style/context and eligible memories to be sent to the selected provider.

Default proposals: AI sharing off until explained and enabled; saving/reuse boxes unchecked. A local-only saved phrase has `allowAIReuse=false`. Both profile-level permission and record-level reuse permission are required for a stored item to leave the computer. Confirmation alone grants none of these permissions.

## Data minimization and scoping

- Send only current keywords, chosen language/tone/context and a bounded set of relevant, permitted memories. Omit display names, matric numbers, contact details, medical records and unnecessary local IDs.
- Use only the active communicator and selected partner/context. No implicit cross-partner or cross-context retrieval; particularly no Healthcare-to-Family reuse. Sharing across contexts would need an explicit future design decision.
- Supporter assistance is explicitly authorized and visible. Do not equate needing communication help with lack of capacity to choose.
- Saved content can contain sensitive meaning even without names. Plain text on a shared computer is readable by people with file access. No encryption or secure multi-user isolation has been supplied or verified.
- Keep user text and memories as quoted data in prompts. An instruction inside a saved phrase must not change the app's rules, destination or allowed context.

## AI logging and the assignment

O10 requires every AI reply displayed to be saved in a text log. “Use once” therefore means **not saved as reusable memory**, while the required local AI-output log still records displayed AI text. Explain this before enabling AI; do not promise zero retention for AI use.

Proposed behavior: validate/normalize a reply, append the exact three candidate texts and local scope/time metadata to `ai_log.txt`, then allow display. Never log API keys, authorization headers, raw prompts or full provider response envelopes. If logging fails or capacity is exhausted, report the local storage issue and offer saved phrases/manual composition; do not show an unlogged reply. Rows prepared for display do not prove the user saw or approved them.

AI logs must never be used as personal memory automatically: displayed alternatives include rejected/incorrect interpretations. Synthetic development logs may be included as evidence; real private logs must not enter the submission or repository.

## Review, withdrawal and deletion

- Show saved phrases and feedback with their reuse settings; let the communicator change or remove them.
- Turning AI sharing off stops future external requests; withdrawing a record's reuse permission excludes it from subsequent prompts but can preserve it locally if desired.
- Deleting a record must remove it from the in-memory array and durable file. If cached request/memory snapshots exist, invalidate them. Cancel/suppress a pending response when consent or profile changes.
- `deleteProfileData` must remove the profile's user links, partners, phrases, feedback, scoped AI log rows and any generated local reports containing that data. Review temporary/backup files too; report incomplete deletion honestly.
- Removing local data cannot retract a completed external request or guarantee provider-side deletion. Provider retention and data-use terms remain unknown until a provider is selected and checked. Do not claim otherwise.
- Propose session-only logs for synthetic testing or deliberate user-managed deletion, subject to lecturer confirmation about evidence retention. Never silently erase the assignment's required log while claiming to retain it.

## Accessibility and meaning safeguards

Use a simple GUI with readable text, clear choices, large targets and consistent navigation. Allow “none of these,” editing, manual messages and back/cancel. Do not assume three full sentences are easy for every user to read. Any real-user evaluation requires a separately agreed, appropriate consent and accessibility process; the first evaluation should use fictional scenarios and make no clinical claims.

Do not let AI turn sparse keywords into unconfirmed symptoms, treatment instructions or emergency advice. Human confirmation remains necessary even when a sentence is fluent or resembles prior memory.

## Planned privacy checks (not executed)

- [ ] With AI disabled, no network request is sent and no record enters an AI prompt.
- [ ] A local-only phrase remains browsable but is absent from prompts.
- [ ] A Healthcare-context correction is absent from a Family request; profile A data is absent from profile B requests.
- [ ] Reject-without-reason does not become a fabricated permanent preference.
- [ ] Save and AI-reuse choices remain separate after reload.
- [ ] Deletion removes array entries, file rows and scoped log/report copies as promised.
- [ ] Missing logging permission offers offline fallback without disclosing raw errors or credentials.
