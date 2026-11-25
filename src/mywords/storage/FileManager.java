/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.storage;
import java.io.IOException;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.model.Codes;
import mywords.model.CommunicationProfile;
import mywords.model.ConversationPartner;
import mywords.model.Feedback;
import mywords.model.Phrase;
import mywords.model.User;
public class FileManager
{
    // Owner: Role A. 所有资料的数组 + counter + .txt 存读 (INTEGRATION.md §3 + §5)
    // 规则: 只用 array + int counter,不用 ArrayList/HashMap;占用的格子是 [0, count)
    // Step 1 (done): 数组的 find / get / save / delete. Step 2-3 (TODO): escape + 读写 .txt
    public static final int MAX_USERS = 100; //容量是提议值,全组同意后再定
    public static final int MAX_PROFILES = 20;
    public static final int MAX_PARTNERS = 200;
    public static final int MAX_PHRASES = 1000;
    public static final int MAX_FEEDBACK = 2000;
    private final String dataFolder; //例如 "data",放 users.txt / profiles.txt ... (已被 .gitignore 忽略)
    private User[] users = new User[MAX_USERS];
    private int userCount = 0; //现在用了几格
    private CommunicationProfile[] profiles = new CommunicationProfile[MAX_PROFILES];
    private int profileCount = 0;
    private ConversationPartner[] partners = new ConversationPartner[MAX_PARTNERS];
    private int partnerCount = 0;
    private Phrase[] phrases = new Phrase[MAX_PHRASES];
    private int phraseCount = 0;
    private Feedback[] feedbacks = new Feedback[MAX_FEEDBACK];
    private int feedbackCount = 0;
    public FileManager(String dataFolder)
    {
        this.dataFolder = dataFolder;
    }
    public String getDataFolder()
    {
        return dataFolder;
    }
    public void loadAll() throws IOException, InvalidInputException, StorageFullException
    {
        // TODO Step 3: 读 6 个 .txt;文件不存在 = 第一次运行 (空资料);文件坏了要报错,不能当成空的
    }
    public void saveAll() throws IOException
    {
        // TODO Step 3: 先写 .tmp 再替换,写失败要保留原本的文件
    }
    // ===================== 小工具 =====================
    private static void requireText(String value, String fieldName) throws InvalidInputException
    {
        if (value == null || value.trim().isEmpty()) //null 或者只有空格 = 没填
        {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }
    // ===================== find: 用 loop 找位置 (index),找不到回传 -1 =====================
    private int findUserIndex(String userId)
    {
        for (int i = 0; i < userCount; i++) //只看用过的格子 [0, userCount),后面都是 null
        {
            if (users[i].getUserId().equals(userId)) //String 要用 equals 比,不能用 ==
            {
                return i; //找到就马上回传位置
            }
        }
        return -1; //整个 loop 跑完都没有 = 找不到
    }
    private int findProfileIndex(String profileId)
    {
        for (int i = 0; i < profileCount; i++)
        {
            if (profiles[i].getProfileId().equals(profileId))
            {
                return i;
            }
        }
        return -1;
    }
    private int findPartnerIndex(String partnerId)
    {
        for (int i = 0; i < partnerCount; i++)
        {
            if (partners[i].getPartnerId().equals(partnerId))
            {
                return i;
            }
        }
        return -1;
    }
    private int findPhraseIndex(String phraseId)
    {
        for (int i = 0; i < phraseCount; i++)
        {
            if (phrases[i].getPhraseId().equals(phraseId))
            {
                return i;
            }
        }
        return -1;
    }
    private int findFeedbackIndex(String feedbackId)
    {
        for (int i = 0; i < feedbackCount; i++)
        {
            if (feedbacks[i].getFeedbackId().equals(feedbackId))
            {
                return i;
            }
        }
        return -1;
    }
    // ===================== get: 回传资料 (副本,不是内部数组本身) =====================
    public User[] getUsers()
    {
        User[] copy = new User[userCount]; //刚好 userCount 格,不多不少,外面直接用 .length
        for (int i = 0; i < userCount; i++)
        {
            copy[i] = users[i]; //一格一格抄过去
        }
        return copy; //外面改这个副本,不会弄坏我们的数组
    }
    public CommunicationProfile[] getProfiles()
    {
        CommunicationProfile[] copy = new CommunicationProfile[profileCount];
        for (int i = 0; i < profileCount; i++)
        {
            copy[i] = profiles[i];
        }
        return copy;
    }
    public CommunicationProfile getProfile(String profileId) throws InvalidInputException
    {
        int index = findProfileIndex(profileId); //先找位置
        if (index == -1) //找不到就 throw,不回传 null
        {
            throw new InvalidInputException("Profile not found: " + profileId);
        }
        return profiles[index];
    }
    public ConversationPartner getPartner(String profileId, String partnerId) throws InvalidInputException
    {
        int index = findPartnerIndex(partnerId);
        if (index == -1 || !partners[index].getProfileId().equals(profileId)) //找不到,或者是别人 profile 的 partner,都不给
        {
            throw new InvalidInputException("Conversation partner not found for this profile: " + partnerId);
        }
        return partners[index];
    }
    public ConversationPartner[] getPartners(String profileId) throws InvalidInputException
    {
        getProfile(profileId); //先确定 profile 存在 (不存在会 throw)
        int matches = 0;
        for (int i = 0; i < partnerCount; i++) //第 1 圈: 先数有几个符合
        {
            if (partners[i].getProfileId().equals(profileId))
            {
                matches++;
            }
        }
        ConversationPartner[] result = new ConversationPartner[matches]; //知道数量了才开刚好大小的数组
        int k = 0; //result 下一格要放的位置
        for (int i = 0; i < partnerCount; i++) //第 2 圈: 把符合的放进去
        {
            if (partners[i].getProfileId().equals(profileId))
            {
                result[k] = partners[i];
                k++;
            }
        }
        return result; //没有符合的话就是长度 0 的数组,不是 null
    }
    public Phrase[] getPhrases(String profileId, String partnerId) throws InvalidInputException
    {
        getPartner(profileId, partnerId); //检查 profile + partner 存在而且配对
        int matches = 0;
        for (int i = 0; i < phraseCount; i++) //第 1 圈: 数
        {
            if (phrases[i].getProfileId().equals(profileId) && phrases[i].getPartnerId().equals(partnerId))
            {
                matches++;
            }
        }
        Phrase[] result = new Phrase[matches];
        int k = 0;
        for (int i = 0; i < phraseCount; i++) //第 2 圈: 放
        {
            if (phrases[i].getProfileId().equals(profileId) && phrases[i].getPartnerId().equals(partnerId))
            {
                result[k] = phrases[i];
                k++;
            }
        }
        return result;
    }
    public Feedback[] getFeedback(String profileId, String partnerId) throws InvalidInputException
    {
        getPartner(profileId, partnerId);
        int matches = 0;
        for (int i = 0; i < feedbackCount; i++)
        {
            if (feedbacks[i].getProfileId().equals(profileId) && feedbacks[i].getPartnerId().equals(partnerId))
            {
                matches++;
            }
        }
        Feedback[] result = new Feedback[matches];
        int k = 0;
        for (int i = 0; i < feedbackCount; i++)
        {
            if (feedbacks[i].getProfileId().equals(profileId) && feedbacks[i].getPartnerId().equals(partnerId))
            {
                result[k] = feedbacks[i];
                k++;
            }
        }
        return result;
    }
    // ===================== save: upsert (同 ID = 更新,新 ID = 加在最后) =====================
    public void saveUser(User user) throws InvalidInputException, StorageFullException, IOException
    {
        if (user == null)
        {
            throw new InvalidInputException("User is missing.");
        }
        requireText(user.getUserId(), "User ID");
        requireText(user.getDisplayName(), "Display name");
        int index = findUserIndex(user.getUserId());
        if (index != -1) //已经有这个 ID -> 盖过去 (更新)
        {
            users[index] = user;
        }
        else //新的 -> 放在第一个空格 users[userCount]
        {
            if (userCount >= MAX_USERS) //满了: throw,不能偷偷丢掉
            {
                throw new StorageFullException("User storage is full (" + MAX_USERS + ").");
            }
            users[userCount] = user;
            userCount++; //用了一格,counter +1
        }
        saveAll(); //Step 3 之后才会真的写进 .txt
    }
    public void saveProfile(CommunicationProfile profile) throws InvalidInputException, StorageFullException, IOException
    {
        if (profile == null)
        {
            throw new InvalidInputException("Profile is missing.");
        }
        requireText(profile.getProfileId(), "Profile ID");
        requireText(profile.getOwnerUserId(), "Profile owner");
        requireText(profile.getDisplayName(), "Profile name");
        int index = findProfileIndex(profile.getProfileId());
        if (index != -1)
        {
            profiles[index] = profile;
        }
        else
        {
            if (profileCount >= MAX_PROFILES)
            {
                throw new StorageFullException("Profile storage is full (" + MAX_PROFILES + ").");
            }
            profiles[profileCount] = profile;
            profileCount++;
        }
        saveAll();
    }
    public void savePartner(ConversationPartner partner) throws InvalidInputException, StorageFullException, IOException
    {
        if (partner == null)
        {
            throw new InvalidInputException("Conversation partner is missing.");
        }
        requireText(partner.getPartnerId(), "Partner ID");
        requireText(partner.getLabel(), "Partner name");
        getProfile(partner.getProfileId()); //profile 一定要先存在
        if (!Codes.isValid(partner.getContextCode(), Codes.CONTEXTS)) //只能是 FAMILY / FRIENDS / HEALTHCARE
        {
            throw new InvalidInputException("Unknown context: " + partner.getContextCode());
        }
        int index = findPartnerIndex(partner.getPartnerId());
        if (index != -1)
        {
            if (!partners[index].getProfileId().equals(partner.getProfileId())) //不能把 partner 搬去别人的 profile
            {
                throw new InvalidInputException("Partner " + partner.getPartnerId() + " belongs to another profile.");
            }
            partners[index] = partner;
        }
        else
        {
            if (partnerCount >= MAX_PARTNERS)
            {
                throw new StorageFullException("Partner storage is full (" + MAX_PARTNERS + ").");
            }
            partners[partnerCount] = partner;
            partnerCount++;
        }
        saveAll();
    }
    private void validatePhrase(Phrase phrase) throws InvalidInputException
    {
        if (phrase == null)
        {
            throw new InvalidInputException("Phrase is missing.");
        }
        requireText(phrase.getPhraseId(), "Phrase ID");
        requireText(phrase.getConfirmedText(), "Confirmed sentence");
        ConversationPartner partner = getPartner(phrase.getProfileId(), phrase.getPartnerId()); //profile + partner 要配对
        if (!partner.getContextCode().equals(phrase.getContextCode())) //phrase 的场景要跟 partner 一样
        {
            throw new InvalidInputException("Phrase context does not match the conversation partner.");
        }
    }
    private void putPhrase(Phrase phrase)
    {
        int index = findPhraseIndex(phrase.getPhraseId());
        if (index != -1)
        {
            phrases[index] = phrase; //更新
        }
        else
        {
            phrases[phraseCount] = phrase; //新增 (调用前已经检查过容量)
            phraseCount++;
        }
    }
    public void savePhrase(Phrase phrase) throws InvalidInputException, StorageFullException, IOException
    {
        validatePhrase(phrase); //1. 先检查,不合法就 throw,数组完全没被改
        if (findPhraseIndex(phrase.getPhraseId()) == -1 && phraseCount >= MAX_PHRASES) //2. 新的而且满了
        {
            throw new StorageFullException("Phrase storage is full (" + MAX_PHRASES + ").");
        }
        putPhrase(phrase); //3. 检查都过了才真的改数组
        saveAll();
    }
    private void validateFeedback(Feedback feedback) throws InvalidInputException
    {
        if (feedback == null)
        {
            throw new InvalidInputException("Feedback is missing.");
        }
        requireText(feedback.getFeedbackId(), "Feedback ID");
        if (!Codes.isValid(feedback.getDecisionCode(), Codes.DECISIONS))
        {
            throw new InvalidInputException("Unknown decision: " + feedback.getDecisionCode());
        }
        if (!Codes.isValid(feedback.getReasonCode(), Codes.REASONS))
        {
            throw new InvalidInputException("Unknown reason: " + feedback.getReasonCode());
        }
        if (feedback.getDecisionCode().equals("EDIT")) //EDIT 一定要有用户改好的句子
        {
            requireText(feedback.getConfirmedText(), "Edited sentence");
        }
        ConversationPartner partner = getPartner(feedback.getProfileId(), feedback.getPartnerId());
        if (!partner.getContextCode().equals(feedback.getContextCode()))
        {
            throw new InvalidInputException("Feedback context does not match the conversation partner.");
        }
    }
    public void recordDecision(Feedback feedback, Phrase optionalConfirmedPhrase) throws InvalidInputException, StorageFullException, IOException
    {
        validateFeedback(feedback); //1. 两样都先检查完
        boolean newFeedback = findFeedbackIndex(feedback.getFeedbackId()) == -1;
        boolean newPhrase = false;
        if (optionalConfirmedPhrase != null) //phrase 是可选的 (用户可能不想保存句子)
        {
            validatePhrase(optionalConfirmedPhrase);
            if (!optionalConfirmedPhrase.getProfileId().equals(feedback.getProfileId()) || !optionalConfirmedPhrase.getPartnerId().equals(feedback.getPartnerId()))
            {
                throw new InvalidInputException("Feedback and phrase must belong to the same profile and partner.");
            }
            newPhrase = findPhraseIndex(optionalConfirmedPhrase.getPhraseId()) == -1;
        }
        if (newFeedback && feedbackCount >= MAX_FEEDBACK) //2. 两样的容量都先检查
        {
            throw new StorageFullException("Feedback storage is full (" + MAX_FEEDBACK + ").");
        }
        if (newPhrase && phraseCount >= MAX_PHRASES)
        {
            throw new StorageFullException("Phrase storage is full (" + MAX_PHRASES + ").");
        }
        int index = findFeedbackIndex(feedback.getFeedbackId()); //3. 全部过关才一起改,不会只存一半
        if (index != -1)
        {
            feedbacks[index] = feedback;
        }
        else
        {
            feedbacks[feedbackCount] = feedback;
            feedbackCount++;
        }
        if (optionalConfirmedPhrase != null)
        {
            putPhrase(optionalConfirmedPhrase);
        }
        saveAll();
    }
    public void appendAILog(String requestId, String profileId, String partnerId, String contextCode, String displayedReply) throws IOException, StorageFullException
    {
        // TODO Step 3: 只记录显示给用户的 AI 回复,不能记 key / prompt;5 MB 上限
    }
    // ===================== delete: 删掉后,后面的往左移一格 =====================
    private void removePhraseAt(int index)
    {
        for (int i = index; i < phraseCount - 1; i++) //从被删的位置开始,把右边那格抄过来
        {
            phrases[i] = phrases[i + 1];
        }
        phrases[phraseCount - 1] = null; //最后一格现在是重复的,清空
        phraseCount--; //少了一格
    }
    private void removeFeedbackAt(int index)
    {
        for (int i = index; i < feedbackCount - 1; i++)
        {
            feedbacks[i] = feedbacks[i + 1];
        }
        feedbacks[feedbackCount - 1] = null;
        feedbackCount--;
    }
    public void deletePhrase(String profileId, String phraseId) throws InvalidInputException, IOException
    {
        int index = findPhraseIndex(phraseId);
        if (index == -1 || !phrases[index].getProfileId().equals(profileId)) //不能删别人 profile 的句子
        {
            throw new InvalidInputException("Phrase not found for this profile: " + phraseId);
        }
        removePhraseAt(index);
        saveAll();
    }
    public void deleteFeedback(String profileId, String feedbackId) throws InvalidInputException, IOException
    {
        int index = findFeedbackIndex(feedbackId);
        if (index == -1 || !feedbacks[index].getProfileId().equals(profileId))
        {
            throw new InvalidInputException("Feedback not found for this profile: " + feedbackId);
        }
        removeFeedbackAt(index);
        saveAll();
    }
    public void deleteProfileData(String profileId) throws IOException
    {
        // 删掉这个 profile 的所有句子和 feedback (profile 和 partner 本身保留)
        for (int i = phraseCount - 1; i >= 0; i--) //倒着删: 删掉后左移不会跳过下一个
        {
            if (phrases[i].getProfileId().equals(profileId))
            {
                removePhraseAt(i);
            }
        }
        for (int i = feedbackCount - 1; i >= 0; i--)
        {
            if (feedbacks[i].getProfileId().equals(profileId))
            {
                removeFeedbackAt(i);
            }
        }
        saveAll();
    }
    // ===================== ID: 用现有最大编号 +1,不是用 count =====================
    private static int idNumber(String id, String prefix)
    {
        if (id == null || !id.startsWith(prefix) || id.length() == prefix.length()) //不是这个 prefix 的 ID
        {
            return -1;
        }
        String rest = id.substring(prefix.length()); //例如 "PH012" 去掉 "PH" -> "012"
        for (int i = 0; i < rest.length(); i++) //剩下的每个字都要是数字
        {
            if (!Character.isDigit(rest.charAt(i))) //例如 prefix "P" 遇到 "PH012" -> "H012" 不是数字,跳过
            {
                return -1;
            }
        }
        return Integer.parseInt(rest);
    }
    public String nextId(String prefix)
    {
        int max = 0;
        for (int i = 0; i < userCount; i++) //每个数组都扫一遍,找这个 prefix 最大的编号
        {
            max = Math.max(max, idNumber(users[i].getUserId(), prefix));
        }
        for (int i = 0; i < profileCount; i++)
        {
            max = Math.max(max, idNumber(profiles[i].getProfileId(), prefix));
        }
        for (int i = 0; i < partnerCount; i++)
        {
            max = Math.max(max, idNumber(partners[i].getPartnerId(), prefix));
        }
        for (int i = 0; i < phraseCount; i++)
        {
            max = Math.max(max, idNumber(phrases[i].getPhraseId(), prefix));
        }
        for (int i = 0; i < feedbackCount; i++)
        {
            max = Math.max(max, idNumber(feedbacks[i].getFeedbackId(), prefix));
        }
        String number = String.valueOf(max + 1); //下一个编号
        while (number.length() < 3) //补 0: 7 -> "007"
        {
            number = "0" + number;
        }
        return prefix + number; //例如 "PH" + "008" = "PH008"
    }
}
