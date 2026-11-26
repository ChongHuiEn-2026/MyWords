/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.app;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.logic.FeedbackManager;
import mywords.model.Codes;
import mywords.model.CommunicationProfile;
import mywords.model.Communicator;
import mywords.model.ConversationPartner;
import mywords.model.Feedback;
import mywords.model.Phrase;
import mywords.model.Suggestion;
import mywords.model.User;
import mywords.storage.FileManager;
import mywords.ui.ReportGenerator;
public class ConsoleMenu
{
    // Owner: Role A. Scanner 文字菜单: 只负责"问问题 + 显示结果",所有规则都交给 AppController / FileManager / FeedbackManager
    // 和 Swing 画面用同一套后端,所以两边存的资料完全一样
    private final AppController controller;
    private final FileManager fileManager;
    private final Scanner input;
    private final FeedbackManager feedbackManager = new FeedbackManager(); //Role C 的规则
    private final ReportGenerator reportGenerator = new ReportGenerator(); //Role D 的报告格式
    public ConsoleMenu(AppController controller, Scanner input)
    {
        this.controller = controller;
        this.fileManager = controller.getFileManager();
        this.input = input;
    }
    // ===================== 读输入的小工具 =====================
    private String readLine(String prompt)
    {
        System.out.print(prompt);
        if (!input.hasNextLine()) //输入结束了 (例如 Ctrl+D) -> 直接离开,不要一直问
        {
            System.out.println();
            System.out.println("Input ended. Goodbye.");
            System.exit(0);
        }
        return input.nextLine().trim(); //用 nextLine 读整行,不会有 nextInt 留下换行的问题
    }
    private int readInt(String prompt, int min, int max)
    {
        while (true) //一直问到输入正确为止
        {
            String line = readLine(prompt);
            try
            {
                int value = Integer.parseInt(line); //"3" -> 3
                if (value >= min && value <= max) //在范围内才接受
                {
                    return value;
                }
            }
            catch (NumberFormatException e) //打了字母或空白
            {
                // 不做什么,下面统一显示提示
            }
            System.out.println("Please enter a number from " + min + " to " + max + ".");
        }
    }
    private boolean readYesNo(String prompt)
    {
        while (true)
        {
            String answer = readLine(prompt + " (y/n): ").toLowerCase(); //Y 和 y 都可以
            if (answer.equals("y") || answer.equals("yes"))
            {
                return true;
            }
            if (answer.equals("n") || answer.equals("no"))
            {
                return false;
            }
            System.out.println("Please type y or n.");
        }
    }
    private void showError(Exception e)
    {
        System.out.println("[!] " + e.getMessage()); //只显示讯息,程序继续,不会当掉
    }
    // ===================== 主菜单 =====================
    public void run()
    {
        System.out.println("=====================================");
        System.out.println(" MyWords - Let Me Finish My Sentence");
        System.out.println("   (text menu, same data as the GUI)");
        System.out.println("=====================================");
        boolean running = true;
        while (running) //一直显示菜单,直到选 0
        {
            System.out.println();
            System.out.println("--- Main menu ---");
            System.out.println("1. Choose a conversation");
            System.out.println("2. Create a new communicator profile");
            System.out.println("0. Exit");
            int choice = readInt("Choose: ", 0, 2);
            if (choice == 1)
            {
                if (chooseConversation()) //选好了才进入对话菜单
                {
                    running = conversationMenu(); //对话菜单回传 false = 用户要离开整个程序
                }
            }
            else if (choice == 2)
            {
                createProfile();
            }
            else
            {
                running = false;
            }
        }
        System.out.println("Goodbye! All changes were saved to the data folder.");
    }
    private boolean chooseConversation()
    {
        User[] users = fileManager.getUsers();
        if (users.length == 0) //一个人都没有
        {
            System.out.println("No users yet. Create a profile first.");
            return false;
        }
        System.out.println("Who is using MyWords now?");
        for (int i = 0; i < users.length; i++) //列出全部 user,编号从 1 开始比较好懂
        {
            System.out.println((i + 1) + ". " + users[i].getDisplayName() + " [" + users[i].getTypeCode() + "]");
        }
        User user = users[readInt("Choose user: ", 1, users.length) - 1]; //用户打 1 -> 数组 index 0
        try
        {
            String profileId = user.getProfileId(); //一个 user 只属于一个 profile
            ConversationPartner[] partners = fileManager.getPartners(profileId);
            if (partners.length == 0) //这个 profile 还没有对话对象
            {
                System.out.println("This profile has no conversation partners yet.");
                if (!readYesNo("Add one now?"))
                {
                    return false;
                }
                ConversationPartner created = askNewPartner(user, profileId);
                if (created == null)
                {
                    return false;
                }
                partners = fileManager.getPartners(profileId); //重新拿一次
            }
            System.out.println("Who are you talking to?");
            for (int i = 0; i < partners.length; i++)
            {
                System.out.println((i + 1) + ". " + partners[i].getLabel() + " (" + partners[i].getContextCode() + ")");
            }
            ConversationPartner partner = partners[readInt("Choose partner: ", 1, partners.length) - 1];
            controller.selectSession(user, profileId, partner.getPartnerId()); //和 GUI 的 "Use this conversation" 一样
            System.out.println("Now: " + user.getDisplayName() + " talking to " + partner.getLabel() + ".");
            return true;
        }
        catch (InvalidInputException e)
        {
            showError(e);
            return false;
        }
    }
    private void createProfile()
    {
        String name = readLine("Communicator name: ");
        String style = readLine("Language style (e.g. Manglish, short sentences) [Enter to skip]: ");
        String tone = readLine("Preferred tone (e.g. casual) [Enter to skip]: ");
        try
        {
            CommunicationProfile profile = controller.createProfile(name, style, tone);
            System.out.println("Created profile " + profile.getProfileId() + " for " + profile.getDisplayName() + ". AI sharing is OFF until the communicator turns it on.");
        }
        catch (InvalidInputException | StorageFullException | IOException e) //输入错 / 满了 / 写档失败
        {
            showError(e);
        }
    }
    // ===================== 对话菜单 =====================
    private boolean conversationMenu()
    {
        while (true)
        {
            System.out.println();
            System.out.println("--- Conversation menu (" + controller.getActiveUser().getDisplayName() + ") ---");
            System.out.println("1. Get suggestions from keywords");
            System.out.println("2. Write my own message");
            System.out.println("3. View saved phrases");
            System.out.println("4. Delete a saved phrase");
            System.out.println("5. Export a report");
            System.out.println("6. Add a conversation partner");
            System.out.println("7. Change language style / tone");
            System.out.println("8. Turn AI sharing on/off");
            System.out.println("9. Add a supporter");
            System.out.println("10. Back to main menu");
            System.out.println("0. Exit");
            int choice = readInt("Choose: ", 0, 10);
            if (choice == 0)
            {
                return false; //离开整个程序
            }
            if (choice == 10)
            {
                return true; //回主菜单
            }
            try
            {
                if (choice == 1)
                {
                    getSuggestions();
                }
                else if (choice == 2)
                {
                    writeOwnMessage();
                }
                else if (choice == 3)
                {
                    listPhrases();
                }
                else if (choice == 4)
                {
                    deletePhrase();
                }
                else if (choice == 5)
                {
                    exportReport();
                }
                else if (choice == 6)
                {
                    askNewPartner(controller.getActiveUser(), controller.getActiveProfileId());
                }
                else if (choice == 7)
                {
                    changeStyle();
                }
                else if (choice == 8)
                {
                    toggleAISharing();
                }
                else
                {
                    addSupporter();
                }
            }
            catch (InvalidInputException | StorageFullException | IOException e) //任何一个功能出错,都显示讯息然后回到菜单
            {
                showError(e);
            }
        }
    }
    private ConversationPartner currentPartner() throws InvalidInputException
    {
        return fileManager.getPartner(controller.getActiveProfileId(), controller.getActivePartnerId());
    }
    private void getSuggestions() throws InvalidInputException, StorageFullException, IOException
    {
        String keywords = readLine("Keywords (e.g. kopi tomorrow): ");
        RequestSnapshot ticket = controller.captureRequest(keywords); //按下 Enter 的那一刻拍快照 (和 GUI 一样)
        Suggestion[] suggestions = controller.requestSuggestions(ticket); //AI -> 失败就用 saved phrase
        keywords = ticket.getKeywords(); //用快照里整理过的关键词存档
        if (!ticket.getResultMessage().isEmpty())
        {
            System.out.println("(" + ticket.getResultMessage() + ")");
        }
        if (suggestions.length == 0)
        {
            System.out.println("No saved phrases match these keywords. Try other words or write your own message (option 2).");
            return;
        }
        for (int i = 0; i < suggestions.length; i++) //列出候选句,标明是 AI 还是 saved phrase,不预先选任何一个
        {
            String label = Codes.SOURCE_AI.equals(suggestions[i].getSourceCode()) ? "[AI suggestion]" : "[Saved phrase]";
            System.out.println((i + 1) + ". " + label + " " + suggestions[i].getText());
        }
        System.out.println("0. None of these");
        int pick = readInt("Choose a sentence: ", 0, suggestions.length);
        if (pick == 0) //都不要 -> 什么都不存
        {
            System.out.println("Nothing was saved.");
            return;
        }
        Suggestion chosen = suggestions[pick - 1];
        System.out.println("1. Approve  2. Edit then confirm  3. Reject");
        int action = readInt("What do you want to do? ", 1, 3);
        if (action == 1)
        {
            saveDecision(chosen, "APPROVE", "NONE", chosen.getText(), keywords);
        }
        else if (action == 2)
        {
            String edited = readLine("Type your corrected sentence: ");
            saveDecision(chosen, "EDIT", readReason(), edited, keywords);
        }
        else
        {
            saveDecision(chosen, "REJECT", readReason(), "", keywords);
        }
    }
    private String readReason()
    {
        System.out.println("Why? 1. Wrong meaning  2. Wrong tone  3. Wrong person/context  4. Other/unsure");
        int r = readInt("Reason: ", 1, 4);
        return Codes.REASONS[r]; //REASONS[0] 是 NONE,所以 1-4 刚好对应后面四个
    }
    private void writeOwnMessage() throws InvalidInputException, StorageFullException, IOException
    {
        String keywords = readLine("Keywords for finding it later (e.g. makan): ");
        String message = readLine("Your message: ");
        saveDecision(null, "APPROVE", "NONE", message, keywords); //null = 没有候选句,只会存成 phrase
    }
    private void saveDecision(Suggestion candidate, String decision, String reason, String confirmedText, String keywords) throws InvalidInputException, StorageFullException, IOException
    {
        // 和 GUI 一样: "确认句子"、"存档"、"给 AI 重用" 是三个分开的同意
        boolean hasText = confirmedText != null && !confirmedText.trim().isEmpty();
        if (candidate == null && !hasText) //自己写的句子不能空
        {
            throw new InvalidInputException("Your message cannot be empty.");
        }
        boolean alreadySaved = candidate != null && Codes.SOURCE_SAVED.equals(candidate.getSourceCode()) && hasText && confirmedText.trim().equals(candidate.getText()); //原本就是已保存的句子,而且没改字
        boolean saveFeedback = candidate != null && readYesNo("Save this decision as feedback?");
        boolean savePhrase = hasText && !alreadySaved && readYesNo("Save the confirmed sentence for later?"); //已经存过就不再存一次,避免重复
        boolean feedbackReuse = saveFeedback && readYesNo("Allow this feedback to be used in future AI requests?");
        boolean phraseReuse = savePhrase && readYesNo("Allow this sentence to be used in future AI requests?");
        ConversationPartner partner = currentPartner();
        String profileId = controller.getActiveProfileId();
        Feedback feedback = null;
        if (saveFeedback)
        {
            feedback = feedbackManager.createFeedback(fileManager.nextId("FB"), profileId, partner.getPartnerId(), partner.getContextCode(), keywords, candidate.getText(), decision, reason, hasText ? confirmedText.trim() : "", feedbackReuse); //Role C 检查规则
        }
        Phrase phrase = null;
        if (savePhrase)
        {
            phrase = new Phrase(fileManager.nextId("PH"), profileId, partner.getPartnerId(), partner.getContextCode(), keywords, confirmedText.trim(), phraseReuse, System.currentTimeMillis());
        }
        if (feedback != null) //有 feedback -> feedback + phrase 一起存
        {
            fileManager.recordDecision(feedback, phrase);
        }
        else if (phrase != null) //只有 phrase
        {
            fileManager.savePhrase(phrase);
        }
        if (feedback == null && phrase == null)
        {
            System.out.println("Nothing was saved (your choice).");
        }
        else
        {
            System.out.println("Saved according to your choices. No message was sent.");
        }
    }
    private Phrase[] listPhrases() throws InvalidInputException
    {
        Phrase[] phrases = fileManager.getPhrases(controller.getActiveProfileId(), controller.getActivePartnerId());
        if (phrases.length == 0)
        {
            System.out.println("No saved phrases for this conversation yet.");
            return phrases;
        }
        for (int i = 0; i < phrases.length; i++)
        {
            String reuse = phrases[i].isAllowAIReuse() ? "AI reuse: yes" : "AI reuse: no";
            System.out.println((i + 1) + ". " + phrases[i].getConfirmedText() + "   [" + phrases[i].getKeywords() + "] (" + reuse + ")");
        }
        return phrases;
    }
    private void deletePhrase() throws InvalidInputException, IOException
    {
        Phrase[] phrases = listPhrases();
        if (phrases.length == 0)
        {
            return;
        }
        int n = readInt("Delete which number? (0 = cancel): ", 0, phrases.length);
        if (n == 0)
        {
            return;
        }
        if (readYesNo("Delete \"" + phrases[n - 1].getConfirmedText() + "\"?")) //删除前一定要确认
        {
            fileManager.deletePhrase(controller.getActiveProfileId(), phrases[n - 1].getPhraseId());
            System.out.println("Deleted.");
        }
    }
    private void exportReport() throws InvalidInputException, IOException
    {
        Phrase[] phrases = listPhrases();
        if (phrases.length == 0)
        {
            return;
        }
        String answer = readLine("Type the numbers to include separated by spaces (e.g. 1 3), or A for all: ");
        Phrase[] selected;
        if (answer.equalsIgnoreCase("A"))
        {
            selected = phrases;
        }
        else
        {
            String[] parts = answer.split("\\s+"); //用空白切开 "1 3" -> ["1","3"]
            Phrase[] temp = new Phrase[parts.length];
            int count = 0;
            for (int i = 0; i < parts.length; i++)
            {
                try
                {
                    int n = Integer.parseInt(parts[i]);
                    if (n >= 1 && n <= phrases.length)
                    {
                        temp[count] = phrases[n - 1];
                        count++;
                    }
                }
                catch (NumberFormatException e) //不是数字就跳过
                {
                    System.out.println("Skipped \"" + parts[i] + "\" (not a number).");
                }
            }
            selected = new Phrase[count]; //缩成刚好大小
            for (int i = 0; i < count; i++)
            {
                selected[i] = temp[i];
            }
        }
        if (selected.length == 0)
        {
            System.out.println("Nothing selected, no report written.");
            return;
        }
        String report = reportGenerator.buildReport(selected); //Role D 的报告格式
        File file = new File(fileManager.getDataFolder(), "report-" + System.currentTimeMillis() + ".txt");
        reportGenerator.writeReport(report, file.getAbsolutePath());
        System.out.println(report);
        System.out.println("Report saved to " + file.getPath());
    }
    private ConversationPartner askNewPartner(User actingUser, String profileId)
    {
        System.out.println("Context: 1. FAMILY  2. FRIENDS  3. HEALTHCARE");
        String context = Codes.CONTEXTS[readInt("Choose context: ", 1, 3) - 1];
        String label = readLine("Partner name (e.g. Ah Ma): ");
        String tone = readLine("Preferred tone with this person [Enter to skip]: ");
        try
        {
            ConversationPartner partner = controller.createPartner(actingUser, profileId, context, label, tone); //没授权的 supporter 会被拒绝
            System.out.println("Added " + partner.getLabel() + " (" + partner.getContextCode() + ").");
            return partner;
        }
        catch (InvalidInputException | StorageFullException | IOException e)
        {
            showError(e);
            return null;
        }
    }
    private void changeStyle() throws InvalidInputException, StorageFullException, IOException
    {
        CommunicationProfile profile = fileManager.getProfile(controller.getActiveProfileId());
        System.out.println("Current style: " + profile.getLanguageStyle());
        System.out.println("Current tone : " + profile.getPreferredTone());
        String style = readLine("New style [Enter to keep]: ");
        String tone = readLine("New tone  [Enter to keep]: ");
        if (style.isEmpty())
        {
            style = profile.getLanguageStyle(); //没打字 = 保持原本的
        }
        if (tone.isEmpty())
        {
            tone = profile.getPreferredTone();
        }
        controller.updateProfileSettings(controller.getActiveUser(), profile.getProfileId(), style, tone); //polymorphism: 没授权的 Supporter 会在这里被拒绝
        System.out.println("Profile settings updated.");
    }
    private void toggleAISharing() throws InvalidInputException, StorageFullException, IOException
    {
        if (!controller.canActiveUserChangeConsent()) //只有 Communicator 本人可以
        {
            throw new InvalidInputException("Only the communicator can change AI sharing.");
        }
        CommunicationProfile profile = fileManager.getProfile(controller.getActiveProfileId());
        boolean oldValue = profile.isAiSharingEnabled();
        System.out.println("AI sharing is now " + (oldValue ? "ON" : "OFF") + ".");
        if (!oldValue) //要开启之前,先清楚说明会送出什么、会存什么
        {
            System.out.println("If you turn it ON, each time you ask for suggestions MyWords will:");
            System.out.println(" - SEND to the external AI service in config.txt: your keywords, this profile's language style,");
            System.out.println("   the partner's tone and context, and up to 3 saved phrases + 2 feedback records you allowed for AI reuse.");
            System.out.println(" - SAVE the AI replies shown to you in a local log file (data/ai_log.txt) on this computer.");
            System.out.println("Nothing is sent while AI sharing is OFF. You can turn it off at any time.");
        }
        else
        {
            System.out.println("If you turn it OFF, nothing will be sent to the AI. Saved phrases and manual messages still work.");
        }
        if (!readYesNo("Turn it " + (oldValue ? "OFF" : "ON") + "?"))
        {
            return;
        }
        profile.setAiSharingEnabled(!oldValue);
        try
        {
            fileManager.saveProfile(profile);
        }
        catch (InvalidInputException | StorageFullException | IOException e) //存不到 -> 把字段改回去 (FileManager 只能还原数组)
        {
            profile.setAiSharingEnabled(oldValue);
            throw e;
        }
        System.out.println("AI sharing is now " + (profile.isAiSharingEnabled() ? "ON" : "OFF") + ".");
    }
    private void addSupporter() throws InvalidInputException, StorageFullException, IOException
    {
        User acting = controller.getActiveUser();
        if (!(acting instanceof Communicator)) //先给清楚的讯息 (controller 也会再检查)
        {
            throw new InvalidInputException("Only the communicator can add a supporter.");
        }
        String name = readLine("Supporter name: ");
        boolean authorized = readYesNo("Allow this supporter to edit profile settings and partners?");
        controller.addSupporter(acting, controller.getActiveProfileId(), name, authorized);
        System.out.println("Supporter added.");
    }
}
