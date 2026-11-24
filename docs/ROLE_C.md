# Role C: AI integration - the originating user's role

**Ownership confirmed by the originating user: Role C.** No name or GitHub handle
is inferred. Read [AGENTS](../AGENTS.md) and all shared docs before implementation.

## Your ownership and boundaries

Own `AIHelper`, `AIServiceException`, local API configuration loading, prompt
construction, provider HTTP integration, response parsing/validation, safe AI
errors, fallback coordination and AI display-log semantics. Preserve the earlier
`AIHelper.ask(String prompt)` entry point. Its proposed `String` return/checked
exception and higher-level APIs need review in [INTEGRATION](INTEGRATION.md).

Do not independently implement the whole app, GUI, profile storage or memory
ranking engine. A owns screens/control flow; B owns shared text I/O and data
arrays; D owns relevant memory selection and feedback semantics. You integrate
their reviewed data into prompts and return validated suggestions.

## Dependencies to settle first

| Teammate | You need | You provide |
| --- | --- | --- |
| A | Keyword/context/profile flow, consent and current-request checks | Generation API, error cases and responsive-call guidance |
| B | Profile/partner records and batched local log writer/clear | Exact display-log fields and failure requirements |
| D | Suggestion/Feedback models and bounded relevant memory arrays | Prompt context needs, validation format and candidate output |

API provider, model, endpoint, JSON parser policy and JDK are not decided. Confirm
them before writing provider-specific code. Use `HttpURLConnection` as the retained
earlier plan; do not silently switch transport or add Collections-based SDK storage.

## Implementation sequence

1. Agree signatures and models with the team. Provide deterministic fictional
   suggestion stubs for A while B/D implement storage/memory.
2. Implement a local flat configuration loader matching `config.example.txt`.
   Missing/placeholder values should disable online AI safely. Never commit or
   print `config.txt`, actual keys or authorization headers.
3. Implement `ask(String prompt)` with HTTPS, correct request escaping, bounded
   response reading, connect/read timeouts, closed resources and non-2xx handling.
4. Build prompts from current keywords, selected style/context and only permitted
   bounded memory from D. Treat all supplied text as data. Keep credentials and
   full histories out of prompts. Do not invent user intent or medical advice.
5. Parse the provider envelope properly, then validate three distinct nonempty
   candidates under the reviewed generated-text contract. Reject malformed output;
   never invent extra candidates to hide a failure.
6. Coordinate A's current-request/consent check and B's batched local display log.
   Every displayed AI candidate must have a local log record. If logging fails,
   surface a safe AI failure before display. Never log raw transport payloads.
7. On failure, let A offer B's existing saved phrases or manual entry. Do not call
   generic fallback text personalized or pretend it is an AI response.

## Malaysian slang and rojak suggestions

Implement the [shared language behavior](PROJECT_CONTEXT.md#malaysian-slang-and-rojak-phrasing)
in the prompt. Use the communicator's selected language/style and context. Keep
recognizable supplied terms such as `tapau` or `yum cha` when they fit the casual
style and preserve meaning. For formal requests, follow the chosen register.
Keep the three-candidate response contract and let the communicator choose.

## Privacy and error checklist

- [ ] Obtain online transmission permission; offline mode works when declined.
- [ ] Explain local candidate logging and offer explicit clear behavior.
- [ ] Handle missing key/config, bad endpoint, timeout, no network and rate limit.
- [ ] Handle provider refusal, invalid JSON, wrong candidate count and oversized text.
- [ ] Handle local log capacity/I/O failure with no exposed keys or HTTP bodies.
- [ ] Do not display/log stale results after profile changes or consent withdrawal.
- [ ] Use only fictional inputs in committed examples and evidence.
- [ ] Do not diagnose, prescribe or claim clinical benefit or proven originality.

## Deliverables and verification

- [ ] Reviewed Java AI APIs and a documented provider response contract.
- [ ] Safe configuration example and clear local setup instructions.
- [ ] Working Java request, prompt, parsing and display-log path.
- [ ] Check the shared casual and formal examples with D and record observed outputs.
- [ ] Meaningful failure tests and GUI fallback walkthrough with A/B.
- [ ] Generic versus personalized comparison with D using matched fictional inputs.
- [ ] Report/video explanation of your classes, API path, errors, privacy and limits.

## Starting prompt for your coding assistant

> Read AGENTS.md, README.md, all shared docs and docs/ROLE_C.md. I am Role C,
> responsible for MyWords AI integration. Inspect existing files first. Preserve
> AIHelper.ask(String prompt), follow reviewed A/B/D interfaces, and identify any
> proposed contracts that still need agreement. Implement my approved AI task in
> Java with safe local config, explicit exceptions, bounded parsing and offline
> fallback coordination. Follow the shared Malaysian slang and rojak scenarios
> in docs/PROJECT_CONTEXT.md. Keep keys/private data out of Git and logs. Explain
> the important Java code at beginner level and report meaningful verification.
