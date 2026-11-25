# Role C (Role 3): Clinical Features & Logic Developer

For MyWords, “clinical” means communication-support logic appropriate to an aphasia-focused prototype. This role does **not** build diagnosis, treatment, medication advice or clinical triage. The app supports a person's expression; it does not decide what the person means.

## Main programming ownership

- `SuggestionService.java`: coordinate the non-network candidate workflow, validate the candidate set returned by B, and provide a useful manual/saved-phrase route when AI is unavailable. Never auto-select a sentence.
- `FeedbackManager.java`: implement approve/edit/reject/none-of-these decisions, explicit reasons (wrong meaning, wrong tone, wrong person/context, other/unsure), and the rule that edits require fresh confirmation.
- `MemoryManager.java`: search and rank eligible saved phrases/corrections using arrays and loops. Enforce active profile, selected partner/context, relevance and explicit reuse consent before supplying examples to an AI request.
- `CommunicationRules.java`: implement the domain rules that prevent rejected guesses or unconfirmed information from becoming approved intent; keep partner-specific preferences scoped and apply clear limits to retrieved examples.

Coordinate model fields and persistence calls with A and B's AI request schema with B. Keep the code explainable and deterministic for the demo; personalization is retrieval, not model training.

## Programming scope and boundaries

- Use plain arrays and loops for search/ranking; no Collections storage.
- Build algorithms over fictional test scenarios and record expected results for integration.
- Do not make medical claims, infer symptoms, add facts, or claim clinical effectiveness. No diagnosis/triage feature is part of this MyWords scope.
- Present AI suggestions as possibilities and leave confirmation and storage permission to the communicator.

## Acceptance evidence

Demonstrate keyword/context search, relevant memory ranking, exclusion of wrong-profile/context or non-consented records, explicit correction handling, and manual/saved phrase fallback. Show that rejected text alone is not treated as approved meaning. Record only evidence actually collected.
