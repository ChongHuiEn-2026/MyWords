package mywords.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Locale;
import mywords.ai.AIHelper;
import mywords.app.AppController;
import mywords.app.RequestSnapshot;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.logic.CommunicationRules;
import mywords.logic.FeedbackManager;
import mywords.logic.MemoryManager;
import mywords.logic.SuggestionService;
import mywords.model.Codes;
import mywords.model.CommunicationProfile;
import mywords.model.ConversationPartner;
import mywords.model.Feedback;
import mywords.model.Phrase;
import mywords.model.Suggestion;
import mywords.model.User;
import mywords.storage.FileManager;
import mywords.ui.ReportGenerator;
import static mywords.web.BrowserForm.quote;

/** Browser actions reuse the existing controller and Java domain objects. */
final class BrowserApp {
    private static final String DEMO_PROFILE = "P001";
    private static final String DEMO_EMAIL = "demo@mywords.app";
    private static final String[] TOPICS = {"Toilet", "Eat or Drink", "Healthcare", "Comfort", "Others"};
    private static final String[] CATEGORIES = {"Family", "Friends", "Healthcare", "Others"};
    private static final String[] REJECT_REASONS = {"WRONG_MEANING", "WRONG_TONE", "WRONG_PERSON_CONTEXT", "OTHER_UNSURE"};
    private final AppController controller;
    private final FileManager files;
    private final BrowserRecords records;
    private final Session[] sessions = new Session[20];
    private int sessionCount;

    record Reply(String json, String cookie) {
        Reply(String json) { this(json, null); }
    }

    private static final class Session {
        final String token = BrowserRecords.token();
        final String profile;
        String requestId = "";
        String partnerId = "";
        String keywords = "";
        Suggestion[] choices = new Suggestion[0];
        boolean[] rejected = new boolean[0]; // a rejected suggestion cannot be chosen again, the others stay available

        Session(String profile) {
            this.profile = profile;
        }
    }

    BrowserApp(FileManager files, AIHelper ai) throws IOException, InvalidInputException, StorageFullException {
        this.files = files;
        records = new BrowserRecords(files);
        controller = new AppController(files, ai, new MemoryManager(), new SuggestionService());
    }

    synchronized Reply run(String action, BrowserForm form, String token)
            throws InvalidInputException, StorageFullException, IOException, GeneralSecurityException {
        Session session = findSession(token);
        switch (action) {
            case "session": return new Reply(session == null ? "{\"user\":null}" : userReply(session.profile));
            case "demo": return demo(session);
            case "login": return signIn(form, session, false);
            case "signup": return signIn(form, session, true);
            case "logout":
                revoke(session);
                return new Reply("{\"user\":null}", "");
            default:
                if (session == null) throw new InvalidInputException("Sign in to MyWords first.");
                return new Reply(perform(action, form, session));
        }
    }

    private Session findSession(String token) {
        for (int i = 0; i < sessionCount; i++) {
            if (sessions[i].token.equals(token)) return sessions[i];
        }
        return null;
    }

    private void revoke(Session session) {
        if (session == null) return;
        for (int i = 0; i < sessionCount; i++) {
            if (sessions[i] == session) {
                sessions[i] = sessions[--sessionCount];
                sessions[sessionCount] = null;
                return;
            }
        }
    }

    private Reply enter(String profile, Session previous) throws InvalidInputException {
        String user = userReply(profile);
        revoke(previous);
        if (sessionCount == sessions.length) {
            throw new InvalidInputException("Too many sessions are open. Sign out of another session first.");
        }
        Session session = new Session(profile);
        sessions[sessionCount++] = session;
        return new Reply(user, session.token);
    }

    private Reply demo(Session previous) throws InvalidInputException, IOException, StorageFullException {
        String[] existing = records.byEmail(DEMO_EMAIL);
        if (existing != null) return enter(existing[1], previous);

        CommunicationProfile profile = null;
        for (CommunicationProfile candidate : files.getProfiles()) {
            if (DEMO_PROFILE.equals(candidate.getProfileId()) && "Mei (demo)".equals(candidate.getDisplayName())) {
                profile = candidate;
                break;
            }
        }
        if (profile == null) {
            profile = controller.createProfile("Mei (demo)", "Casual rojak", "Friendly");
            controller.createPartner(owner(profile), profile.getProfileId(), "FAMILY", "Ah Ma", "Warm");
        }
        User speaker = owner(profile);
        String id = profile.getProfileId();
        boolean hasFriend = false;
        boolean hasOther = false;
        for (ConversationPartner partner : files.getPartners(id)) {
            hasFriend |= "FRIENDS".equals(partner.getContextCode());
            hasOther |= "OTHERS".equals(partner.getContextCode());
        }
        if (!hasFriend) controller.createPartner(speaker, id, "FRIENDS", "Fictional friend", "Friendly");
        if (!hasOther) controller.createPartner(speaker, id, "OTHERS", "Kak Siti (demo)", "Polite");
        records.put("ACCOUNT", id, id, DEMO_EMAIL, "", "Male", "en", "light", "normal");
        return enter(id, previous);
    }

    private Reply signIn(BrowserForm form, Session previous, boolean create)
            throws InvalidInputException, StorageFullException, IOException, GeneralSecurityException {
        String email = form.text("email", 120, true).toLowerCase(Locale.ROOT);
        String password = form.get("password");
        if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+") || password.length() < 6 || password.length() > 120) {
            throw new InvalidInputException("Check your email and enter a password of 6 to 120 characters.");
        }
        String[] account = records.byEmail(email);
        if (create) {
            if (account != null || DEMO_EMAIL.equals(email)) throw new InvalidInputException("This email is already registered.");
            String gender = option(form.get("gender"), "voice preference", "Male", "Female");
            String hash = BrowserRecords.passwordHash(password);
            CommunicationProfile profile = controller.createProfile(form.text("name", 60, true), "Casual rojak", "Friendly");
            String id = profile.getProfileId();
            records.put("ACCOUNT", id, id, email, hash, gender, "en", "light", "normal");
            return enter(id, previous);
        }
        if (account == null || !BrowserRecords.passwordMatches(password, account[4])) {
            throw new InvalidInputException("The email or password is incorrect.");
        }
        return enter(account[1], previous);
    }

    private String perform(String action, BrowserForm form, Session session)
            throws InvalidInputException, StorageFullException, IOException, GeneralSecurityException {
        CommunicationProfile profile = files.getProfile(session.profile);
        return switch (action) {
            case "consent" -> setConsent(profile, form.checked("enabled"));
            case "profile" -> profileSettings(profile, form);
            case "name" -> rename(profile, form);
            case "password" -> password(profile, form);
            case "partner-add", "partner-edit", "partner-delete" -> partnerAction(action, profile, form);
            case "need-add", "need-edit", "need-delete" -> needAction(action, profile, form);
            case "suggest" -> suggestions(profile, form, session);
            case "decision" -> decision(profile, form, session);
            case "manual" -> manual(profile, form);
            case "phrases" -> phrases(profile, form);
            case "phrase-delete", "phrase-reuse" -> phraseAction(action, profile, form);
            case "feedback" -> feedback(profile, form);
            case "feedback-delete", "feedback-reuse" -> feedbackAction(action, profile, form);
            case "report" -> report(profile, form);
            default -> throw new InvalidInputException("Unknown MyWords action.");
        };
    }

    private String setConsent(CommunicationProfile profile, boolean enabled)
            throws IOException, InvalidInputException, StorageFullException {
        owner(profile); // Browser sessions are issued to communicator accounts only.
        saveProfile(profile, profile.getDisplayName(), enabled);
        return userReply(profile.getProfileId());
    }

    private String profileSettings(CommunicationProfile profile, BrowserForm form)
            throws IOException, InvalidInputException, StorageFullException {
        String[] account = account(profile.getProfileId());
        String gender = option(form.get("gender"), "voice preference", "Male", "Female");
        String language = option(form.get("language"), "language", "en", "ms", "zh");
        String theme = option(form.get("theme"), "theme", "dark", "light");
        String font = option(form.get("font"), "text size", "normal", "large", "xlarge");
        String style = form.text("style", 120, true);
        String tone = form.text("tone", 120, true);
        controller.updateProfileSettings(owner(profile), profile.getProfileId(), style, tone);
        records.put("ACCOUNT", account[1], account[2], account[3], account[4], gender, language, theme, font);
        return userReply(profile.getProfileId());
    }

    private String rename(CommunicationProfile profile, BrowserForm form)
            throws IOException, InvalidInputException, StorageFullException {
        saveProfile(profile, form.text("name", 60, true), profile.isAiSharingEnabled());
        return userReply(profile.getProfileId());
    }

    private void saveProfile(CommunicationProfile profile, String name, boolean consent)
            throws IOException, InvalidInputException, StorageFullException {
        files.saveProfile(new CommunicationProfile(profile.getProfileId(), profile.getOwnerUserId(),
                name, profile.getLanguageStyle(), profile.getPreferredTone(), consent));
    }

    private String password(CommunicationProfile profile, BrowserForm form)
            throws InvalidInputException, GeneralSecurityException, StorageFullException, IOException {
        String[] account = account(profile.getProfileId());
        String replacement = form.get("replacement");
        if (!BrowserRecords.passwordMatches(form.get("current"), account[4])) {
            throw new InvalidInputException("The current password is incorrect.");
        }
        if (replacement.length() < 6 || replacement.length() > 120) {
            throw new InvalidInputException("Enter a new password of 6 to 120 characters.");
        }
        account[4] = BrowserRecords.passwordHash(replacement);
        records.put(account);
        return "{\"ok\":true}";
    }

    private String partnerAction(String action, CommunicationProfile profile, BrowserForm form)
            throws IOException, InvalidInputException, StorageFullException {
        String id = profile.getProfileId();
        String partnerId = form.text("partner", 32, false);
        if ("partner-delete".equals(action)) {
            files.deletePartner(id, partnerId);
            records.remove("PARTNER", id, partnerId);
            return userReply(id);
        }
        String name = form.text("name", 60, true);
        String role = form.text("role", 60, true);
        String phrase = form.text("callPhrase", 300, false);
        String category = option(form.get("category"), "conversation category", CATEGORIES);
        String context = category.toUpperCase(Locale.ROOT);
        String tone = form.text("tone", 120, false);
        if (tone.isEmpty()) tone = "HEALTHCARE".equals(context) ? "Polite" : "Friendly";
        ConversationPartner partner;
        if ("partner-add".equals(action)) {
            partner = controller.createPartner(owner(profile), id, context, name, tone);
        } else {
            ConversationPartner original = files.getPartner(id, partnerId);
            if (!context.equals(original.getContextCode())) throw new InvalidInputException("Keep the person's conversation category.");
            partner = new ConversationPartner(partnerId, id, context, name, tone);
            files.savePartner(partner);
        }
        records.put("PARTNER", id, partner.getPartnerId(), role, phrase);
        return userReply(id, ",\"partnerId\":" + quote(partner.getPartnerId()));
    }

    private String needAction(String action, CommunicationProfile profile, BrowserForm form)
            throws IOException, InvalidInputException, StorageFullException {
        String id = profile.getProfileId();
        String needId = form.text("need", 32, false);
        String topic = option(form.get("topic"), "topic", TOPICS);
        if (!"need-add".equals(action)) {
            String[] existing = records.get("NEED", id, needId);
            if (existing == null || !topic.equals(existing[3])) throw new InvalidInputException("Saved need not found.");
        }
        if ("need-delete".equals(action)) {
            records.remove("NEED", id, needId);
        } else {
            if ("need-add".equals(action)) {
                if (records.needs(id).length >= 20) throw new StorageFullException("This profile can save up to 20 needs.");
                needId = BrowserRecords.token();
            }
            records.put("NEED", id, needId, topic, form.text("label", 100, true));
        }
        return userReply(id);
    }

    private User owner(CommunicationProfile profile) throws InvalidInputException {
        for (User user : files.getUsers()) {
            if (user.getUserId().equals(profile.getOwnerUserId()) && "COMMUNICATOR".equals(user.getTypeCode())) {
                return user;
            }
        }
        throw new InvalidInputException("Communicator profile is unavailable.");
    }

    private String suggestions(CommunicationProfile profile, BrowserForm form, Session session)
            throws InvalidInputException, StorageFullException, IOException {
        session.requestId = "";
        session.choices = new Suggestion[0];
        ConversationPartner partner = partner(profile, form);
        String keywords = form.text("keywords", CommunicationRules.MAX_KEYWORDS_LENGTH, true);
        controller.selectSession(owner(profile), profile.getProfileId(), partner.getPartnerId());
        RequestSnapshot ticket = controller.captureRequest(keywords);
        Suggestion[] results = controller.requestSuggestions(ticket);
        session.requestId = BrowserRecords.token();
        session.choices = results;
        session.rejected = new boolean[results.length];
        session.partnerId = partner.getPartnerId();
        session.keywords = keywords;
        StringBuilder json = new StringBuilder("{\"request\":").append(quote(session.requestId))
                .append(",\"message\":").append(quote(ticket.getResultMessage()))
                .append(",\"suggestions\":[");
        for (int i = 0; i < results.length; i++) {
            if (i > 0) json.append(',');
            json.append("{\"text\":").append(quote(results[i].getText()))
                    .append(",\"source\":").append(quote(results[i].getSourceCode())).append('}');
        }
        return json.append("]}").toString();
    }

    private String decision(CommunicationProfile profile, BrowserForm form, Session session)
            throws IOException, InvalidInputException, StorageFullException {
        if (session.requestId.isEmpty() || !session.requestId.equals(form.get("request"))) {
            throw new InvalidInputException("These suggestions have changed. Request them again.");
        }
        int index = Integer.parseInt(form.get("index"));
        if (index < 0 || index >= session.choices.length) throw new InvalidInputException("Choose a suggestion.");
        if (session.rejected[index]) throw new InvalidInputException("You already said this suggestion was not right.");
        Suggestion choice = session.choices[index];
        String type = option(form.get("decision"), "decision", "APPROVE", "EDIT", "REJECT");
        boolean reject = "REJECT".equals(type);
        ConversationPartner partner = files.getPartner(profile.getProfileId(), session.partnerId);
        String correction = "EDIT".equals(type) ? form.text("text", 500, true)
                : reject ? form.text("text", 500, false) : choice.getText();
        Feedback feedback = new FeedbackManager().createFeedback(files.nextId("FB"), profile.getProfileId(),
                partner.getPartnerId(), partner.getContextCode(), session.keywords, choice.getText(), type,
                reject ? option(form.get("reason"), "reason", REJECT_REASONS) : "NONE",
                correction, form.checked("saveFeedback") && form.checked("reuseFeedback") && (!reject || !correction.isEmpty()));
        boolean duplicate = Codes.SOURCE_SAVED.equals(choice.getSourceCode()) && choice.getText().equals(correction);
        Phrase saved = !reject && !duplicate && form.checked("savePhrase")
                ? newPhrase(partner, session.keywords, correction, form.checked("reusePhrase")) : null;
        String detail = reject ? form.text("detail", 200, false) : "";
        if (form.checked("saveFeedback")) {
            files.recordDecision(feedback, saved);
            if (!detail.isEmpty()) records.put("REASON", profile.getProfileId(), feedback.getFeedbackId(), detail);
        } else if (saved != null) files.savePhrase(saved);
        if (reject) {
            session.rejected[index] = true; // keep the other suggestions so the communicator can still pick one
        } else {
            session.requestId = "";
            session.choices = new Suggestion[0];
            session.rejected = new boolean[0];
        }
        return "{\"ok\":true}";
    }

    private String manual(CommunicationProfile profile, BrowserForm form)
            throws IOException, InvalidInputException, StorageFullException {
        ConversationPartner partner = partner(profile, form);
        String text = form.text("text", CommunicationRules.MAX_SENTENCE_LENGTH, true);
        String keywords = form.text("keywords", CommunicationRules.MAX_KEYWORDS_LENGTH, true);
        files.savePhrase(newPhrase(partner, keywords, text, form.checked("reuse")));
        return "{\"ok\":true}";
    }

    private Phrase newPhrase(ConversationPartner partner, String keywords, String text, boolean reuse) {
        return new Phrase(files.nextId("PH"), partner.getProfileId(), partner.getPartnerId(),
                partner.getContextCode(), keywords, text, reuse, System.currentTimeMillis());
    }

    private String phrases(CommunicationProfile profile, BrowserForm form) throws InvalidInputException {
        ConversationPartner partner = partner(profile, form);
        StringBuilder json = new StringBuilder("{\"phrases\":[");
        Phrase[] list = files.getPhrases(profile.getProfileId(), partner.getPartnerId());
        for (int i = 0; i < list.length; i++) {
            if (i > 0) json.append(',');
            Phrase phrase = list[i];
            json.append("{\"id\":").append(quote(phrase.getPhraseId())).append(",\"text\":")
                    .append(quote(phrase.getConfirmedText())).append(",\"keywords\":").append(quote(phrase.getKeywords()))
                    .append(",\"createdAt\":").append(phrase.getCreatedAt())
                    .append(",\"reuse\":").append(phrase.isAllowAIReuse()).append('}');
        }
        return json.append("]}").toString();
    }

    private String phraseAction(String action, CommunicationProfile profile, BrowserForm form)
            throws InvalidInputException, IOException, StorageFullException {
        Phrase phrase = findPhrase(profile, form);
        if ("phrase-delete".equals(action)) files.deletePhrase(profile.getProfileId(), phrase.getPhraseId());
        else files.savePhrase(new Phrase(phrase.getPhraseId(), phrase.getProfileId(), phrase.getPartnerId(),
                phrase.getContextCode(), phrase.getKeywords(), phrase.getConfirmedText(), form.checked("reuse"), phrase.getCreatedAt()));
        return phrases(profile, form);
    }

    private String feedback(CommunicationProfile profile, BrowserForm form) throws InvalidInputException {
        ConversationPartner selected = partner(profile, form);
        Feedback[] list = files.getFeedback(profile.getProfileId(), selected.getPartnerId());
        StringBuilder json = new StringBuilder("{\"feedback\":[");
        for (int i = 0; i < list.length; i++) {
            if (i > 0) json.append(',');
            Feedback item = list[i];
            String[] detail = records.get("REASON", profile.getProfileId(), item.getFeedbackId());
            json.append("{\"id\":").append(quote(item.getFeedbackId()))
                    .append(",\"reason\":").append(quote(item.getReasonCode()))
                    .append(",\"detail\":").append(quote(detail == null ? "" : detail[3]))
                    .append(",\"decision\":").append(quote(item.getDecisionCode()))
                    .append(",\"text\":").append(quote(item.getCandidateText()))
                    .append(",\"confirmed\":").append(quote(item.getConfirmedText()))
                    .append(",\"reuse\":").append(item.isAllowAIReuse()).append('}');
        }
        return json.append("]}").toString();
    }

    private String feedbackAction(String action, CommunicationProfile profile, BrowserForm form)
            throws InvalidInputException, IOException, StorageFullException {
        ConversationPartner selected = partner(profile, form);
        for (Feedback item : files.getFeedback(profile.getProfileId(), selected.getPartnerId())) {
            if (!item.getFeedbackId().equals(form.get("feedback"))) continue;
            if ("feedback-delete".equals(action)) {
                files.deleteFeedback(profile.getProfileId(), item.getFeedbackId());
                records.remove("REASON", profile.getProfileId(), item.getFeedbackId());
            } else {
                boolean reuse = form.checked("reuse");
                Feedback updated = new Feedback(item.getFeedbackId(), item.getProfileId(), item.getPartnerId(),
                        item.getContextCode(), item.getKeywords(), item.getCandidateText(), item.getDecisionCode(),
                        item.getReasonCode(), item.getConfirmedText(), reuse, item.getCreatedAt());
                if (reuse && !CommunicationRules.mayShareFeedback(updated)) {
                    throw new InvalidInputException("This feedback needs a confirmed correction before it can be reused.");
                }
                files.recordDecision(updated, null);
            }
            return feedback(profile, form);
        }
        throw new InvalidInputException("Saved feedback not found.");
    }

    private String report(CommunicationProfile profile, BrowserForm form) throws InvalidInputException {
        ConversationPartner partner = partner(profile, form);
        String selection = form.text("selected", 6000, true);
        String[] ids = selection.split(",");
        Phrase[] selected = new Phrase[ids.length];
        int count = 0;
        for (Phrase phrase : files.getPhrases(profile.getProfileId(), partner.getPartnerId())) {
            if (Codes.isValid(phrase.getPhraseId(), ids)) selected[count++] = phrase;
        }
        if (count == 0) throw new InvalidInputException("Select confirmed phrases to export.");
        return "{\"report\":" + quote(new ReportGenerator().buildReport(Arrays.copyOf(selected, count))) + "}";
    }

    private Phrase findPhrase(CommunicationProfile profile, BrowserForm form) throws InvalidInputException {
        ConversationPartner partner = partner(profile, form);
        for (Phrase phrase : files.getPhrases(profile.getProfileId(), partner.getPartnerId())) {
            if (phrase.getPhraseId().equals(form.get("phrase"))) return phrase;
        }
        throw new InvalidInputException("Saved phrase not found.");
    }

    private ConversationPartner partner(CommunicationProfile profile, BrowserForm form) throws InvalidInputException {
        return files.getPartner(profile.getProfileId(), form.text("partner", 32, true));
    }

    private String[] account(String profileId) throws InvalidInputException {
        String[] account = records.get("ACCOUNT", profileId, profileId);
        if (account == null) throw new InvalidInputException("This browser profile is unavailable.");
        return account;
    }

    private String userReply(String profileId) throws InvalidInputException {
        return userReply(profileId, "");
    }

    private String userReply(String profileId, String extra) throws InvalidInputException {
        CommunicationProfile profile = files.getProfile(profileId);
        String[] account = account(profileId);
        StringBuilder user = new StringBuilder("{\"user\":{");
        user.append("\"email\":").append(quote(account[3])).append(",\"name\":").append(quote(profile.getDisplayName()))
                .append(",\"style\":").append(quote(profile.getLanguageStyle()))
                .append(",\"tone\":").append(quote(profile.getPreferredTone()))
                .append(",\"aiSharing\":").append(profile.isAiSharingEnabled())
                .append(",\"gender\":").append(quote(account[5])).append(",\"language\":").append(quote(account[6]))
                .append(",\"theme\":").append(quote(account[7])).append(",\"fontSize\":").append(quote(account[8]))
                .append(",\"members\":");
        appendMembers(user, profileId);
        user.append(",\"customNeeds\":");
        appendNeeds(user, profileId);
        return user.append('}').append(extra).append('}').toString();
    }

    private void appendMembers(StringBuilder json, String profileId) throws InvalidInputException {
        ConversationPartner[] partners = files.getPartners(profileId);
        json.append('{');
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (i > 0) json.append(',');
            json.append(quote(CATEGORIES[i])).append(":[");
            boolean comma = false;
            for (ConversationPartner partner : partners) {
                if (!partner.getContextCode().equalsIgnoreCase(CATEGORIES[i])) continue;
                if (comma) json.append(',');
                comma = true;
                String[] record = records.get("PARTNER", profileId, partner.getPartnerId());
                json.append("{\"id\":").append(quote(partner.getPartnerId()))
                        .append(",\"name\":").append(quote(partner.getLabel()))
                        .append(",\"tone\":").append(quote(partner.getPreferredTone()))
                        .append(",\"role\":").append(quote(record == null ? CATEGORIES[i] : record[3]))
                        .append(",\"customPhrase\":").append(quote(record == null ? "" : record[4])).append('}');
            }
            json.append(']');
        }
        json.append('}');
    }

    private void appendNeeds(StringBuilder json, String profileId) {
        String[][] needs = records.needs(profileId);
        json.append('{');
        for (int i = 0; i < TOPICS.length; i++) {
            if (i > 0) json.append(',');
            json.append(quote(TOPICS[i])).append(":[");
            boolean comma = false;
            for (String[] need : needs) {
                if (!TOPICS[i].equals(need[3])) continue;
                if (comma) json.append(',');
                comma = true;
                json.append("{\"id\":").append(quote(need[2])).append(",\"label\":").append(quote(need[4])).append('}');
            }
            json.append(']');
        }
        json.append('}');
    }

    private static String option(String value, String field, String... choices) throws InvalidInputException {
        if (!Codes.isValid(value, choices)) throw new InvalidInputException("Choose a valid " + field + ".");
        return value;
    }
}
