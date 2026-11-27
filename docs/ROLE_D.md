# Role D (Role 4): User Interface (UI) & Report Generator Developer

Owns the complete user-facing flow and an in-app report/export feature. Build against the agreed Java interfaces; screens call backend services and do not duplicate their storage, AI or communication logic.

## Main programming ownership

- `MyWords.html` and `browser.js` for the submission screens: home/profile and partner/context selection, keyword/manual composition, candidate review, feedback, saved phrases/memory controls, and help/about/privacy information. Use readable text, clear navigation and large actionable controls.
- `mywords.web`: connect same-origin browser requests to the shared Java backend. Use the Java form and communication rules for validation, and preserve entered text when showing a correction.
- `ReportGenerator.java`: create a readable conversation summary from only the phrases the communicator explicitly selected for export and already confirmed. Keep AI suggestions marked as suggestions; do not include unconfirmed or rejected content.
- Preview the Java-generated report and download the reviewed text as a `.txt` file.
- Connect loading, empty, invalid-input, offline/fallback, save-success and file-error states to the correct backend calls. AI replies are displayed only after the required logging step succeeds.

## Accessibility and consent

- Allow manual composition, edit, reject, “none of these,” back and cancel paths; no automatic selection or sending.
- Keep message approval separate from consent to save and reuse it.
- Make AI-generated candidates and saved phrases distinguishable and communicate when AI is unavailable.
- Do not assume the prototype has been clinically validated or is accessible to every person with aphasia.

## Acceptance evidence

Demonstrate the complete GUI flow with fictional data, validation/error states, manual and offline fallback, memory consent controls, and an export containing only the user-selected, confirmed phrases. Record only checks actually performed.
