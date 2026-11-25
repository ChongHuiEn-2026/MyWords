/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.storage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.model.Codes;
import mywords.model.Communicator;
import mywords.model.CommunicationProfile;
import mywords.model.ConversationPartner;
import mywords.model.Feedback;
import mywords.model.Phrase;
import mywords.model.Supporter;
import mywords.model.User;
public class FileManager
{
    // Owner: Role A. 所有资料的数组 + counter + .txt 存读 (INTEGRATION.md §3 + §5)
    // 规则: 只用 array + int counter,不用 ArrayList/HashMap;占用的格子是 [0, count)
    // Step 1: 数组的 find / get / save / delete. Step 2: RecordCodec. Step 3: 读写 .txt
    public static final int MAX_USERS = 100; //容量是提议值,全组同意后再定
    public static final int MAX_PROFILES = 20;
    public static final int MAX_PARTNERS = 200;
    public static final int MAX_PHRASES = 1000;
    public static final int MAX_FEEDBACK = 2000;
    public static final long MAX_LOG_BYTES = 5L * 1024 * 1024; //ai_log.txt 上限 5 MB
    private static final String USERS_FILE = "users.txt"; //档名集中在这里,改一个地方就好
    private static final String PROFILES_FILE = "profiles.txt";
    private static final String PARTNERS_FILE = "partners.txt";
    private static final String PHRASES_FILE = "phrases.txt";
    private static final String FEEDBACK_FILE = "feedback.txt";
    private static final String AI_LOG_FILE = "ai_log.txt";
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
    // ===================== Step 3: 读档 =====================
    private String[] readDataLines(String fileName, String type, int maxRecords) throws IOException, InvalidInputException, StorageFullException
    {
        File file = new File(dataFolder, fileName); //data/users.txt 这样
        if (!file.exists()) //第一次运行还没有档案 -> 当作 0 笔
        {
            return new String[0];
        }
        String[] buffer = new String[maxRecords]; //先开最大容量
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) //try-with-resources: 用完自动关档
        {
            String header = reader.readLine(); //第一行是标头
            if (header != null && header.startsWith("﻿")) //用 Notepad 存过可能多了 BOM 隐形字,去掉
            {
                header = header.substring(1);
            }
            String expected = "#MyWords|1|" + type;
            if (header == null || !header.equals(expected)) //标头不对 = 不是我们的档案或版本不对
            {
                throw new InvalidInputException(fileName + ": first line must be " + expected);
            }
            String line = reader.readLine();
            while (line != null) //一直读到档案结尾 (readLine 回传 null)
            {
                if (!line.isEmpty()) //空行跳过
                {
                    if (count >= maxRecords) //资料比容量还多
                    {
                        throw new StorageFullException(fileName + " has more than " + maxRecords + " records.");
                    }
                    buffer[count] = line;
                    count++;
                }
                line = reader.readLine();
            }
        }
        String[] result = new String[count]; //缩成刚好大小
        for (int i = 0; i < count; i++)
        {
            result[i] = buffer[i];
        }
        return result;
    }
    public void loadAll() throws IOException, InvalidInputException, StorageFullException
    {
        // 重点: 先全部读进 "临时数组" 检查完,全部 OK 才换掉真正的数组 -> 档案坏了也不会只读一半
        String[] lines = readDataLines(USERS_FILE, "USERS", MAX_USERS);
        User[] newUsers = new User[MAX_USERS];
        for (int i = 0; i < lines.length; i++)
        {
            String[] f = parseLine(lines[i], 5, USERS_FILE, i); //userId|type|displayName|profileId|supporterAuthorized
            if (f[1].equals("COMMUNICATOR")) //依 type 建不同的子类 -> 都放进 User[] (polymorphism)
            {
                newUsers[i] = new Communicator(f[0], f[2], f[3]);
            }
            else if (f[1].equals("SUPPORTER"))
            {
                newUsers[i] = new Supporter(f[0], f[2], f[3], parseBool(f[4], USERS_FILE, i));
            }
            else
            {
                throw new InvalidInputException(USERS_FILE + " line " + (i + 2) + ": unknown user type " + f[1]);
            }
        }
        int newUserCount = lines.length;
        lines = readDataLines(PROFILES_FILE, "PROFILES", MAX_PROFILES);
        CommunicationProfile[] newProfiles = new CommunicationProfile[MAX_PROFILES];
        for (int i = 0; i < lines.length; i++)
        {
            String[] f = parseLine(lines[i], 6, PROFILES_FILE, i); //profileId|ownerUserId|displayName|languageStyle|preferredTone|aiSharingEnabled
            newProfiles[i] = new CommunicationProfile(f[0], f[1], f[2], f[3], f[4], parseBool(f[5], PROFILES_FILE, i));
        }
        int newProfileCount = lines.length;
        lines = readDataLines(PARTNERS_FILE, "PARTNERS", MAX_PARTNERS);
        ConversationPartner[] newPartners = new ConversationPartner[MAX_PARTNERS];
        for (int i = 0; i < lines.length; i++)
        {
            String[] f = parseLine(lines[i], 5, PARTNERS_FILE, i); //partnerId|profileId|contextCode|label|preferredTone
            newPartners[i] = new ConversationPartner(f[0], f[1], f[2], f[3], f[4]);
        }
        int newPartnerCount = lines.length;
        lines = readDataLines(PHRASES_FILE, "PHRASES", MAX_PHRASES);
        Phrase[] newPhrases = new Phrase[MAX_PHRASES];
        for (int i = 0; i < lines.length; i++)
        {
            String[] f = parseLine(lines[i], 8, PHRASES_FILE, i); //phraseId|profileId|partnerId|contextCode|keywords|confirmedText|allowAIReuse|createdAt
            newPhrases[i] = new Phrase(f[0], f[1], f[2], f[3], f[4], f[5], parseBool(f[6], PHRASES_FILE, i), parseTime(f[7], PHRASES_FILE, i));
        }
        int newPhraseCount = lines.length;
        lines = readDataLines(FEEDBACK_FILE, "FEEDBACK", MAX_FEEDBACK);
        Feedback[] newFeedbacks = new Feedback[MAX_FEEDBACK];
        for (int i = 0; i < lines.length; i++)
        {
            String[] f = parseLine(lines[i], 11, FEEDBACK_FILE, i); //feedbackId|profileId|partnerId|contextCode|keywords|candidateText|decisionCode|reasonCode|confirmedText|allowAIReuse|createdAt
            newFeedbacks[i] = new Feedback(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8], parseBool(f[9], FEEDBACK_FILE, i), parseTime(f[10], FEEDBACK_FILE, i));
        }
        int newFeedbackCount = lines.length;
        // 全部读完了 -> 先暂时换上新资料,再用 Step 1 写好的检查逐笔验证
        User[] oldUsers = users; //备份旧的,验证失败要换回来
        CommunicationProfile[] oldProfiles = profiles;
        ConversationPartner[] oldPartners = partners;
        Phrase[] oldPhrases = phrases;
        Feedback[] oldFeedbacks = feedbacks;
        int[] oldCounts = {userCount, profileCount, partnerCount, phraseCount, feedbackCount};
        users = newUsers;
        userCount = newUserCount;
        profiles = newProfiles;
        profileCount = newProfileCount;
        partners = newPartners;
        partnerCount = newPartnerCount;
        phrases = newPhrases;
        phraseCount = newPhraseCount;
        feedbacks = newFeedbacks;
        feedbackCount = newFeedbackCount;
        try
        {
            validateLoadedData(); //有问题会 throw InvalidInputException
        }
        catch (InvalidInputException e) //验证失败 -> 全部换回旧资料,再把错误丢出去
        {
            users = oldUsers;
            profiles = oldProfiles;
            partners = oldPartners;
            phrases = oldPhrases;
            feedbacks = oldFeedbacks;
            userCount = oldCounts[0];
            profileCount = oldCounts[1];
            partnerCount = oldCounts[2];
            phraseCount = oldCounts[3];
            feedbackCount = oldCounts[4];
            throw e;
        }
    }
    private static String[] parseLine(String line, int fieldCount, String fileName, int index) throws InvalidInputException
    {
        try
        {
            return RecordCodec.split(line, fieldCount);
        }
        catch (InvalidInputException e) //加上档名和第几行,方便找错 (+2 = 标头那一行 + 从 1 开始算)
        {
            throw new InvalidInputException(fileName + " line " + (index + 2) + ": " + e.getMessage());
        }
    }
    private static boolean parseBool(String text, String fileName, int index) throws InvalidInputException
    {
        try
        {
            return RecordCodec.parseBoolean(text);
        }
        catch (InvalidInputException e)
        {
            throw new InvalidInputException(fileName + " line " + (index + 2) + ": " + e.getMessage());
        }
    }
    private static long parseTime(String text, String fileName, int index) throws InvalidInputException
    {
        try
        {
            return RecordCodec.parseLong(text);
        }
        catch (InvalidInputException e)
        {
            throw new InvalidInputException(fileName + " line " + (index + 2) + ": " + e.getMessage());
        }
    }
    private void validateLoadedData() throws InvalidInputException
    {
        for (int i = 0; i < userCount; i++)
        {
            requireText(users[i].getUserId(), "User ID");
            if (findUserIndex(users[i].getUserId()) != i) //find 找到的是更前面那格 = ID 重复
            {
                throw new InvalidInputException(USERS_FILE + ": duplicate ID " + users[i].getUserId());
            }
        }
        for (int i = 0; i < profileCount; i++)
        {
            requireText(profiles[i].getProfileId(), "Profile ID");
            if (findProfileIndex(profiles[i].getProfileId()) != i)
            {
                throw new InvalidInputException(PROFILES_FILE + ": duplicate ID " + profiles[i].getProfileId());
            }
        }
        for (int i = 0; i < partnerCount; i++)
        {
            if (findPartnerIndex(partners[i].getPartnerId()) != i)
            {
                throw new InvalidInputException(PARTNERS_FILE + ": duplicate ID " + partners[i].getPartnerId());
            }
            getProfile(partners[i].getProfileId()); //partner 指向的 profile 一定要存在
            if (!Codes.isValid(partners[i].getContextCode(), Codes.CONTEXTS))
            {
                throw new InvalidInputException(PARTNERS_FILE + ": unknown context " + partners[i].getContextCode());
            }
        }
        for (int i = 0; i < phraseCount; i++)
        {
            if (findPhraseIndex(phrases[i].getPhraseId()) != i)
            {
                throw new InvalidInputException(PHRASES_FILE + ": duplicate ID " + phrases[i].getPhraseId());
            }
            validatePhrase(phrases[i]); //重用 Step 1 的检查 (profile/partner 配对、场景一致)
        }
        for (int i = 0; i < feedbackCount; i++)
        {
            if (findFeedbackIndex(feedbacks[i].getFeedbackId()) != i)
            {
                throw new InvalidInputException(FEEDBACK_FILE + ": duplicate ID " + feedbacks[i].getFeedbackId());
            }
            validateFeedback(feedbacks[i]);
        }
    }
    // ===================== Step 3: 写档 =====================
    private void writeDataFile(String fileName, String type, String[] lines) throws IOException
    {
        File folder = new File(dataFolder);
        if (!folder.exists() && !folder.mkdirs()) //资料夹不存在就建;建不了就报错
        {
            throw new IOException("Cannot create data folder: " + folder.getAbsolutePath());
        }
        File target = new File(folder, fileName);
        File temp = new File(folder, fileName + ".tmp"); //先写到临时档
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), StandardCharsets.UTF_8)))
        {
            writer.write("#MyWords|1|" + type); //第一行: 标头
            writer.newLine();
            for (int i = 0; i < lines.length; i++)
            {
                writer.write(lines[i]);
                writer.newLine();
            }
        } //离开 try 自动 close (= 真正写进硬盘)
        Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING); //写成功才把临时档换成正式档;中途失败原本的档案还在
    }
    public void saveAll() throws IOException
    {
        String[] lines = new String[userCount];
        for (int i = 0; i < userCount; i++)
        {
            boolean authorized = false;
            if (users[i] instanceof Supporter) //只有 Supporter 才有授权 flag
            {
                authorized = ((Supporter) users[i]).isSupporterAuthorized(); //cast 成 Supporter 才能叫它自己的方法
            }
            lines[i] = RecordCodec.join(new String[] {users[i].getUserId(), users[i].getTypeCode(), users[i].getDisplayName(), users[i].getProfileId(), String.valueOf(authorized)});
        }
        writeDataFile(USERS_FILE, "USERS", lines);
        lines = new String[profileCount];
        for (int i = 0; i < profileCount; i++)
        {
            CommunicationProfile p = profiles[i];
            lines[i] = RecordCodec.join(new String[] {p.getProfileId(), p.getOwnerUserId(), p.getDisplayName(), p.getLanguageStyle(), p.getPreferredTone(), String.valueOf(p.isAiSharingEnabled())});
        }
        writeDataFile(PROFILES_FILE, "PROFILES", lines);
        lines = new String[partnerCount];
        for (int i = 0; i < partnerCount; i++)
        {
            ConversationPartner c = partners[i];
            lines[i] = RecordCodec.join(new String[] {c.getPartnerId(), c.getProfileId(), c.getContextCode(), c.getLabel(), c.getPreferredTone()});
        }
        writeDataFile(PARTNERS_FILE, "PARTNERS", lines);
        lines = new String[phraseCount];
        for (int i = 0; i < phraseCount; i++)
        {
            Phrase p = phrases[i];
            lines[i] = RecordCodec.join(new String[] {p.getPhraseId(), p.getProfileId(), p.getPartnerId(), p.getContextCode(), p.getKeywords(), p.getConfirmedText(), String.valueOf(p.isAllowAIReuse()), String.valueOf(p.getCreatedAt())});
        }
        writeDataFile(PHRASES_FILE, "PHRASES", lines);
        lines = new String[feedbackCount];
        for (int i = 0; i < feedbackCount; i++)
        {
            Feedback f = feedbacks[i];
            lines[i] = RecordCodec.join(new String[] {f.getFeedbackId(), f.getProfileId(), f.getPartnerId(), f.getContextCode(), f.getKeywords(), f.getCandidateText(), f.getDecisionCode(), f.getReasonCode(), f.getConfirmedText(), String.valueOf(f.isAllowAIReuse()), String.valueOf(f.getCreatedAt())});
        }
        writeDataFile(FEEDBACK_FILE, "FEEDBACK", lines);
        // 注意: 5 个档案是一个一个写的,中途失败可能只有部分更新 (还不是 multi-file atomic)
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
        // 只记录"要显示给用户的 AI 回复" + 时间/范围;绝对不记 API key 或 prompt
        File folder = new File(dataFolder);
        if (!folder.exists() && !folder.mkdirs())
        {
            throw new IOException("Cannot create data folder: " + folder.getAbsolutePath());
        }
        File log = new File(folder, AI_LOG_FILE);
        boolean isNew = !log.exists(); //新档案要先写标头
        String line = RecordCodec.join(new String[] {String.valueOf(System.currentTimeMillis()), requestId, profileId, partnerId, contextCode, displayedReply});
        long extraBytes = line.getBytes(StandardCharsets.UTF_8).length + 2; //这一行大概多少 byte (+2 给换行)
        if (log.length() + extraBytes > MAX_LOG_BYTES) //超过 5 MB: 不偷偷删旧的,要用户先处理
        {
            throw new StorageFullException("AI log is full (5 MB). Please review or delete " + AI_LOG_FILE + ".");
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(log, true), StandardCharsets.UTF_8))) //true = append 模式,接在档案后面
        {
            if (isNew)
            {
                writer.write("#MyWords|1|AI_LOG");
                writer.newLine();
            }
            writer.write(line);
            writer.newLine();
        }
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
