package mywords.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import mywords.app.AppController;
import mywords.app.RequestSnapshot;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.logic.FeedbackManager;
import mywords.model.Codes;
import mywords.model.CommunicationProfile;
import mywords.model.ConversationPartner;
import mywords.model.Feedback;
import mywords.model.Phrase;
import mywords.model.Suggestion;
import mywords.model.User;
import mywords.storage.FileManager;

/** Role D's Swing screen, wired to the supplied Role A controller and model APIs. */
public class MainFrame extends JFrame {
    private final AppController controller;
    private final FileManager fileManager;
    private final InputValidator validator = new InputValidator();
    private final ReportGenerator reportGenerator = new ReportGenerator();
    private final FeedbackManager feedbackManager = new FeedbackManager();

    private final JComboBox<CommunicationProfile> profiles = new JComboBox<CommunicationProfile>();
    private final JComboBox<User> users = new JComboBox<User>();
    private final JComboBox<ConversationPartner> partners = new JComboBox<ConversationPartner>();
    private final JTextField keywords = new JTextField();
    private final JTextArea manualMessage = new JTextArea(3, 40);
    private final DefaultListModel<Suggestion> candidateModel = new DefaultListModel<Suggestion>();
    private final JList<Suggestion> candidateList = new JList<Suggestion>(candidateModel);
    private final JLabel status = new JLabel("Choose a profile, user, and conversation partner.");
    private final Phrase[] exportPhrases = new Phrase[FileManager.MAX_PHRASES];
    private int exportPhraseCount;
    private String lastKeywords = "";

    public MainFrame(AppController controller) {
        super("MyWords - Let Me Finish My Sentence");
        this.controller = controller;
        this.fileManager = controller.getFileManager();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 720);
        setLocationRelativeTo(null);
        setJMenuBar(createMenu());
        setLayout(new BorderLayout(10, 10));
        add(createSessionPanel(), BorderLayout.NORTH);
        add(createWorkPanel(), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        configureRenderers();
        loadProfiles();
    }

    private JMenuBar createMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Information");
        JMenuItem help = new JMenuItem("Help");
        help.addActionListener(e -> showInfo("Help", "Choose a profile, user, and conversation partner. Enter keywords or write a message manually. AI candidates are suggestions only; select one before approving, editing, or rejecting it. You can choose None of these and keep composing. Confirming a message does not send it. Saving and AI reuse require separate choices."));
        JMenuItem about = new JMenuItem("About MyWords");
        about.addActionListener(e -> showInfo("About MyWords", "MyWords is a student communication aid prototype. It is not a diagnostic tool, treatment, verified interpreter, or clinically validated product. Accessibility and suitability for any individual have not been established."));
        JMenuItem privacy = new JMenuItem("Privacy");
        privacy.addActionListener(e -> showInfo("Privacy", "Saved phrases and feedback are stored by the local backend. Profile-level AI sharing and each record's reuse permission must both allow information to be included in an AI request. Displayed AI replies are logged by the backend before the UI receives them. Local text files may be visible to others with access to this computer. Use fictional data in demos."));
        menu.add(help); menu.add(about); menu.add(privacy); bar.add(menu);
        return bar;
    }

    private JPanel createSessionPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 8, 6));
        panel.setBorder(BorderFactory.createTitledBorder("1. Select the conversation"));
        panel.add(new JLabel("Communicator profile")); panel.add(new JLabel("Current user")); panel.add(new JLabel("Partner/context"));
        panel.add(profiles); panel.add(users); panel.add(partners);
        profiles.addActionListener(e -> loadUsers());
        JButton select = new JButton("Use this conversation");
        select.addActionListener(e -> selectSession());
        panel.add(new JLabel("")); panel.add(new JLabel("")); panel.add(select);
        return panel;
    }

    private JPanel createWorkPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel compose = new JPanel(new BorderLayout(6, 6));
        compose.setBorder(BorderFactory.createTitledBorder("2. Compose with keywords or manually"));
        JPanel keywordRow = new JPanel(new BorderLayout(6, 6));
        keywordRow.add(new JLabel("Keywords"), BorderLayout.WEST);
        keywordRow.add(keywords, BorderLayout.CENTER);
        JButton ask = new JButton("Get suggestions");
        ask.addActionListener(e -> requestSuggestions());
        keywordRow.add(ask, BorderLayout.EAST);
        compose.add(keywordRow, BorderLayout.NORTH);
        manualMessage.setLineWrap(true); manualMessage.setWrapStyleWord(true);
        compose.add(new JScrollPane(manualMessage), BorderLayout.CENTER);
        JButton useManual = new JButton("Confirm my manual message");
        useManual.addActionListener(e -> confirmManual());
        compose.add(useManual, BorderLayout.SOUTH);

        JPanel review = new JPanel(new BorderLayout(6, 6));
        review.setBorder(BorderFactory.createTitledBorder("3. Review candidates (AI and saved phrases are labeled)"));
        candidateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        candidateList.setVisibleRowCount(7);
        candidateList.setCellRenderer(new DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof Suggestion) {
                    Suggestion s = (Suggestion)value;
                    String label = Codes.SOURCE_AI.equals(s.getSourceCode()) ? "AI suggestion" : "Saved phrase";
                    setText((index + 1) + ". [" + label + "] " + s.getText());
                }
                return this;
            }
        });
        review.add(new JScrollPane(candidateList), BorderLayout.CENTER);
        JPanel actions = new JPanel(new GridLayout(2, 4, 6, 6));
        JButton approve = new JButton("Approve selected"); approve.addActionListener(e -> reviewSelected("APPROVE"));
        JButton edit = new JButton("Edit then confirm"); edit.addActionListener(e -> editSelected());
        JButton reject = new JButton("Reject selected"); reject.addActionListener(e -> reviewSelected("REJECT"));
        JButton none = new JButton("None of these"); none.addActionListener(e -> clearSelection());
        JButton memory = new JButton("Manage saved phrases"); memory.addActionListener(e -> manageMemories());
        JButton feedbackMemory = new JButton("Manage saved feedback"); feedbackMemory.addActionListener(e -> manageFeedbackMemory());
        JButton aiSharing = new JButton("Profile AI sharing"); aiSharing.addActionListener(e -> toggleProfileAISharing());
        JButton report = new JButton("Review / export report"); report.addActionListener(e -> exportReport());
        actions.add(approve); actions.add(edit); actions.add(reject); actions.add(none);
        actions.add(memory); actions.add(feedbackMemory); actions.add(aiSharing); actions.add(report);
        review.add(actions, BorderLayout.SOUTH);
        panel.add(compose, BorderLayout.NORTH); panel.add(review, BorderLayout.CENTER);
        return panel;
    }

    private void configureRenderers() {
        profiles.setRenderer(new DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof CommunicationProfile) setText(((CommunicationProfile)value).getDisplayName());
                return this;
            }
        });
        users.setRenderer(new DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof User) setText(((User)value).getDisplayName() + " (" + ((User)value).getTypeCode() + ")");
                return this;
            }
        });
        partners.setRenderer(new DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                if (value instanceof ConversationPartner) {
                    ConversationPartner p = (ConversationPartner)value;
                    setText(p.getLabel() + " (" + p.getContextCode() + ")");
                }
                return this;
            }
        });
    }

    private void loadProfiles() {
        profiles.removeAllItems();
        for (CommunicationProfile p : fileManager.getProfiles()) profiles.addItem(p);
        loadUsers();
    }
    private void loadUsers() {
        users.removeAllItems();
        partners.removeAllItems();
        CommunicationProfile p = (CommunicationProfile)profiles.getSelectedItem();
        if (p == null) return;
        for (User u : fileManager.getUsers()) if (p.getProfileId().equals(u.getProfileId())) users.addItem(u);
        try { for (ConversationPartner partner : fileManager.getPartners(p.getProfileId())) partners.addItem(partner); }
        catch (InvalidInputException ex) { showError("Could not load conversation partners", ex); }
    }
    private void selectSession() {
        CommunicationProfile p = (CommunicationProfile)profiles.getSelectedItem();
        User u = (User)users.getSelectedItem();
        ConversationPartner partner = (ConversationPartner)partners.getSelectedItem();
        if (p == null || u == null || partner == null) { showError("Choose a profile, user, and partner/context first.", null); return; }
        try {
            controller.selectSession(u, p.getProfileId(), partner.getPartnerId());
            candidateModel.clear();
            status.setText("Conversation selected. Compose manually or request suggestions.");
        } catch (InvalidInputException ex) { showError("Could not select this conversation", ex); }
    }

    private void requestSuggestions() {
        String raw = keywords.getText();
        try { validator.validateKeywords(raw); }
        catch (InvalidInputException ex) { showError("Check the keywords", ex); keywords.requestFocus(); return; }
        final RequestSnapshot ticket; // keywords + profile + partner + consent captured at the moment of the click
        try { ticket = controller.captureRequest(raw); }
        catch (InvalidInputException ex) { showError("Choose the conversation first", ex); return; }
        setBusy(true, "Loading suggestions. Your text remains available to edit.");
        new SwingWorker<Suggestion[], Void>() {
            @Override protected Suggestion[] doInBackground() throws Exception { return controller.requestSuggestions(ticket); } // uses only the snapshot
            @Override protected void done() {
                setBusy(false, "Review a possibility; nothing is selected automatically.");
                try {
                    Suggestion[] result = get();
                    if (!controller.isCurrentSession(ticket)) { // conversation changed while waiting
                        status.setText("The conversation changed, so the old suggestions were discarded.");
                        return;
                    }
                    lastKeywords = ticket.getKeywords(); // keywords that belong to the suggestions on screen
                    candidateModel.clear();
                    if (result != null) for (Suggestion s : result) candidateModel.addElement(s);
                    String fallback = ticket.getResultMessage();
                    if (fallback != null && !fallback.isEmpty()) status.setText(fallback);
                    else if (candidateModel.isEmpty()) status.setText("No suggestions found. Try a manual message.");
                    else status.setText("Suggestions are ready. Select one to review it.");
                } catch (Exception ex) {
                    candidateModel.clear();
                    status.setText("Suggestions are unavailable. You can still compose manually or use saved phrases.");
                    showError("Suggestion request failed", ex.getCause() == null ? ex : ex.getCause());
                }
            }
        }.execute();
    }

    private void reviewSelected(String decision) {
        Suggestion selected = candidateList.getSelectedValue();
        if (selected == null) { showError("Select a candidate first. No candidate is chosen automatically.", null); return; }
        if ("APPROVE".equals(decision)) saveDecision(selected, decision, "NONE", selected.getText());
        else {
            String[] reasons = {"WRONG_MEANING", "WRONG_TONE", "WRONG_PERSON_CONTEXT", "OTHER_UNSURE"};
            String reason = (String)JOptionPane.showInputDialog(this, "Choose a reason:", "Reject candidate", JOptionPane.QUESTION_MESSAGE, null, reasons, reasons[0]);
            if (reason == null) return;
            saveDecision(selected, "REJECT", reason, "");
        }
    }
    private void editSelected() {
        Suggestion selected = candidateList.getSelectedValue();
        if (selected == null) { showError("Select a candidate to edit first.", null); return; }
        JTextArea editor = new JTextArea(selected.getText(), 4, 40); editor.setLineWrap(true); editor.setWrapStyleWord(true);
        if (JOptionPane.showConfirmDialog(this, new JScrollPane(editor), "Edit, then confirm the final message", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try { validator.validateSentence(editor.getText()); }
        catch (InvalidInputException ex) { showError("Check the edited message", ex); return; }
        saveDecision(selected, "EDIT", "OTHER_UNSURE", editor.getText());
    }
    private void confirmManual() {
        try { validator.validateSentence(manualMessage.getText()); }
        catch (InvalidInputException ex) { showError("Check your message", ex); manualMessage.requestFocus(); return; }
        // Manual text is a communicator-authored, explicitly confirmed message; it is not an AI candidate.
        lastKeywords = keywords.getText().trim(); // keep the typed keywords so this phrase can be found again later
        saveDecision(null, "APPROVE", "NONE", manualMessage.getText());
    }
    private void saveDecision(Suggestion candidate, String decision, String reason, String confirmedText) {
        String textForRecord = candidate == null ? "" : candidate.getText();
        int feedbackConsent = askConsent("Save this feedback decision locally?");
        if (feedbackConsent == CANCEL) return;
        boolean hasConfirmedText = confirmedText != null && !confirmedText.trim().isEmpty();
        int phraseConsent = hasConfirmedText ? askConsent("Save the confirmed phrase locally for later browsing?") : NO;
        if (phraseConsent == CANCEL) return;
        boolean saveFeedback = feedbackConsent == YES;
        boolean savePhrase = phraseConsent == YES;
        boolean feedbackReuse = false;
        boolean phraseReuse = false;
        if (saveFeedback) {
            int answer = askConsent("Allow this feedback record to be used in future AI requests?");
            if (answer == CANCEL) return;
            feedbackReuse = answer == YES;
        }
        if (savePhrase) {
            int answer = askConsent("Allow this phrase to be used in future AI requests? Profile-level AI sharing must also be enabled.");
            if (answer == CANCEL) return;
            phraseReuse = answer == YES;
        }
        try {
            CommunicationProfile profile = (CommunicationProfile)profiles.getSelectedItem();
            ConversationPartner partner = (ConversationPartner)partners.getSelectedItem();
            if (profile == null || partner == null) throw new InvalidInputException("Select a conversation first.");
            if (!profile.getProfileId().equals(controller.getActiveProfileId()) || !partner.getPartnerId().equals(controller.getActivePartnerId())) {
                throw new InvalidInputException("Press \"Use this conversation\" for the selected profile and partner first.");
            }
            long now = System.currentTimeMillis();
            Feedback feedback = null; // manual messages have no candidate, so they are saved only as phrases
            if (saveFeedback && candidate != null) {
                feedback = feedbackManager.createFeedback(fileManager.nextId("FB"), profile.getProfileId(), partner.getPartnerId(),
                        partner.getContextCode(), lastKeywords, textForRecord, decision, reason,
                        "APPROVE".equals(decision) || "EDIT".equals(decision) ? confirmedText : "", feedbackReuse);
            }
            Phrase phrase = null;
            if (savePhrase && confirmedText != null && !confirmedText.trim().isEmpty()) {
                phrase = new Phrase(fileManager.nextId("PH"), profile.getProfileId(), partner.getPartnerId(),
                        partner.getContextCode(), lastKeywords, confirmedText, phraseReuse, now);
            }
            if (feedback != null) {
                fileManager.recordDecision(feedback, phrase);
            } else if (phrase != null) {
                fileManager.savePhrase(phrase);
            }
            if (hasConfirmedText) {
                Phrase exportPhrase = phrase != null ? phrase : new Phrase("", profile.getProfileId(), partner.getPartnerId(),
                        partner.getContextCode(), lastKeywords, confirmedText, false, now);
                if (askConsent("Add this confirmed phrase to the report export?" ) == YES && exportPhraseCount < exportPhrases.length) {
                    exportPhrases[exportPhraseCount++] = exportPhrase;
                }
            }
            candidateList.clearSelection();
            status.setText("Decision recorded according to your choices. No message was sent.");
        } catch (InvalidInputException | StorageFullException | IOException ex) {
            showError("Could not save this decision", ex);
        }
    }

    private static final int CANCEL = -1;
    private static final int NO = 0;
    private static final int YES = 1;
    private int askConsent(String question) {
        int result = JOptionPane.showConfirmDialog(this, question, "Optional consent", JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) return CANCEL;
        return result == JOptionPane.YES_OPTION ? YES : NO;
    }

    private void clearSelection() { candidateList.clearSelection(); status.setText("No candidate selected. Continue manually or cancel."); }

    private void manageMemories() {
        CommunicationProfile profile = (CommunicationProfile)profiles.getSelectedItem();
        ConversationPartner partner = (ConversationPartner)partners.getSelectedItem();
        if (profile == null || partner == null) { showError("Select a conversation first.", null); return; }
        try {
            Phrase[] phrases = fileManager.getPhrases(profile.getProfileId(), partner.getPartnerId());
            if (phrases.length == 0) { showInfo("Saved phrases", "There are no saved phrases for this conversation."); return; }
            String[] options = new String[phrases.length];
            for (int i = 0; i < phrases.length; i++) options[i] = phrases[i].getConfirmedText() + " [AI reuse " + (phrases[i].isAllowAIReuse() ? "on" : "off") + "]";
            int selected = JOptionPane.showOptionDialog(this, "Select a phrase to manage:", "Saved phrase memory", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (selected < 0) return;
            Phrase phrase = phrases[selected];
            Object[] actions = {phrase.isAllowAIReuse() ? "Turn AI reuse off" : "Turn AI reuse on", "Delete phrase", "Cancel"};
            int action = JOptionPane.showOptionDialog(this, phrase.getConfirmedText(), "Manage saved phrase", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, actions, actions[2]);
            if (action == 0) {
                phrase.setAllowAIReuse(!phrase.isAllowAIReuse());
                fileManager.savePhrase(phrase);
                status.setText("Phrase reuse permission updated.");
            } else if (action == 1 && JOptionPane.showConfirmDialog(this, "Delete this saved phrase?", "Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                fileManager.deletePhrase(profile.getProfileId(), phrase.getPhraseId());
                status.setText("Saved phrase deleted.");
            }
        } catch (InvalidInputException | StorageFullException | IOException ex) { showError("Could not manage saved phrases", ex); }
    }

    private void manageFeedbackMemory() {
        CommunicationProfile profile = (CommunicationProfile)profiles.getSelectedItem();
        ConversationPartner partner = (ConversationPartner)partners.getSelectedItem();
        if (profile == null || partner == null) { showError("Select a conversation first.", null); return; }
        try {
            Feedback[] records = fileManager.getFeedback(profile.getProfileId(), partner.getPartnerId());
            if (records.length == 0) { showInfo("Saved feedback", "There is no saved feedback for this conversation."); return; }
            String[] options = new String[records.length];
            for (int i = 0; i < records.length; i++) {
                options[i] = records[i].getDecisionCode() + " / " + records[i].getReasonCode() + " — "
                        + records[i].getCandidateText() + " [AI reuse " + (records[i].isAllowAIReuse() ? "on" : "off") + "]";
            }
            int selected = JOptionPane.showOptionDialog(this, "Select feedback to manage:", "Saved feedback memory",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (selected < 0) return;
            Feedback feedback = records[selected];
            Object[] actions = {feedback.isAllowAIReuse() ? "Turn AI reuse off" : "Turn AI reuse on", "Delete feedback", "Cancel"};
            int action = JOptionPane.showOptionDialog(this, feedback.getCandidateText(), "Manage saved feedback",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, actions, actions[2]);
            if (action == 0) {
                feedback.setAllowAIReuse(!feedback.isAllowAIReuse());
                fileManager.recordDecision(feedback, null);
                status.setText("Feedback AI reuse permission updated.");
            } else if (action == 1 && JOptionPane.showConfirmDialog(this, "Delete this saved feedback?", "Confirm deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                fileManager.deleteFeedback(profile.getProfileId(), feedback.getFeedbackId());
                status.setText("Saved feedback deleted.");
            }
        } catch (InvalidInputException | StorageFullException | IOException ex) { showError("Could not manage saved feedback", ex); }
    }

    private void toggleProfileAISharing() {
        CommunicationProfile profile = (CommunicationProfile)profiles.getSelectedItem();
        if (profile == null) { showError("Choose a profile first.", null); return; }
        if (!profile.getProfileId().equals(controller.getActiveProfileId()) || !controller.canActiveUserChangeConsent()) {
            showError("Only the communicator can change AI sharing. Select the communicator as Current user and press \"Use this conversation\".", null);
            return;
        }
        boolean enabled = !profile.isAiSharingEnabled();
        String message = enabled
                ? "Enable sending current keywords and permitted memories to the AI service for this profile?"
                : "Turn off external AI sharing for this profile? Manual composition and local saved phrases remain available.";
        if (JOptionPane.showConfirmDialog(this, message, "Profile AI sharing", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            profile.setAiSharingEnabled(enabled);
            fileManager.saveProfile(profile);
            status.setText("Profile AI sharing " + (enabled ? "enabled" : "disabled") + ".");
        } catch (InvalidInputException | StorageFullException | IOException ex) { showError("Could not update profile AI sharing", ex); }
    }

    private void exportReport() {
        if (exportPhraseCount == 0) { showInfo("Report", "No phrases have been selected for export."); return; }
        Phrase[] selected = new Phrase[exportPhraseCount];
        for (int i = 0; i < exportPhraseCount; i++) selected[i] = exportPhrases[i];
        String report = reportGenerator.buildReport(selected);
        JTextArea preview = new JTextArea(report, 18, 60); preview.setEditable(false); preview.setLineWrap(true); preview.setWrapStyleWord(true);
        if (JOptionPane.showConfirmDialog(this, new JScrollPane(preview), "Review report before saving", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        JFileChooser chooser = new JFileChooser(); chooser.setSelectedFile(new java.io.File("mywords-summary.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            reportGenerator.writeReport(preview.getText(), chooser.getSelectedFile().getAbsolutePath());
            status.setText("Reviewed report saved.");
        } catch (IOException ex) { showError("Could not save report", ex); }
    }

    private void setBusy(boolean busy, String message) {
        status.setText(message);
        setCursor(busy ? java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR) : java.awt.Cursor.getDefaultCursor());
    }
    private void showInfo(String title, String message) { JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE); }
    private void showError(String title, Throwable ex) { JOptionPane.showMessageDialog(this, title + (ex == null ? "" : ": " + ex.getMessage()), "MyWords", JOptionPane.ERROR_MESSAGE); }
}
