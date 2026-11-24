# Role B: domain records, arrays and text persistence

Assigned person: **to be confirmed**. Read [AGENTS](../AGENTS.md),
[requirements](REQUIREMENTS.md) and [proposed contracts](INTEGRATION.md) first.

## Your ownership

Own `CommunicationProfile`, `ConversationPartner`, `Phrase`, `FileManager` and
`StorageFullException`. Coordinate User records with A and Feedback fields with D.
Supply the only shared implementation of domain storage and text I/O, including
file operations used for C's AI display log. C decides AI logging semantics;
you implement safe writes and clearing.

All schemas, capacities and APIs in this pack are **proposed for team review**.
Confirm them with A/C/D before implementation or migration.

## Storage rules

Store each domain type in a plain Java array with a separate integer count.
Validate before insertion; check capacity; shift elements and clear the vacated
slot on deletion; keep count within bounds. Updates by ID do not increase count.
Return copies rather than mutable backing arrays. No Collections-based indexes,
parsing caches, Properties/Maps or stream collectors for object storage.

Use UTF-8 text files with the reviewed version, field order and escaping rules.
Round-trip tabs, backslashes, line breaks and mixed-language text. Load into
temporary arrays/counters, validate references and publish state only when valid.
Missing files on first run are empty state; malformed existing data is an error.
Do not replace unreadable data with an apparently successful empty save.

Persist a mutation before reporting success and keep prior state recoverable on
disk errors. Support memory/phrase deletion and complete log clearing. Never
commit local runtime data; use fictional fixtures under `examples/data/` for tests
and the required sanitized submission data files.

## Dependencies and handoff

- A needs startup/load, profile/partner access, saved-phrase actions and safe errors.
- C needs batched AI log appends/clear with capacity and I/O error behavior.
- D needs communicator-scoped phrases/feedback, permitted remembering and deletion.
- D owns memory ranking; do not build a second personalization engine in storage.

Deliver sample fictional `.txt` files, an exact schema explanation and meaningful
restart/error evidence. Coordinate any record-field change with all callers.

## Delivery checklist

- [ ] Agree models, ID rules, capacities and persistence schema with A/C/D.
- [ ] Implement array/count invariants, inserts, updates, deletes and safe lookups.
- [ ] Throw input/capacity exceptions and preserve state on failure.
- [ ] Implement validated text load/reload and recoverable file replacement.
- [ ] Implement phrase/feedback deletion and AI log write/clear operations.
- [ ] Verify first run, restart, Unicode, escaping, corrupt files and full arrays.
- [ ] Demonstrate disk-write failures without false success or data loss.
- [ ] Supply fictional `.txt` fixtures and report/video file I/O evidence.

## Starting prompt for your coding assistant

> Read AGENTS.md, README.md, all shared docs and docs/ROLE_B.md. I am Role B.
> Inspect the existing project first. Confirm the proposed record schemas and APIs
> with A/C/D before implementing. Use plain arrays and integer counters for domain
> storage, safe UTF-8 text persistence and explicit exceptions. Preserve teammate
> work; show save/restart, capacity, malformed-file and Unicode verification.
