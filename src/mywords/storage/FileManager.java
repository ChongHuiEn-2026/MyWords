/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.storage;
import java.io.IOException;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.model.CommunicationProfile;
import mywords.model.ConversationPartner;
import mywords.model.Feedback;
import mywords.model.Phrase;
import mywords.model.User;
public class FileManager
{
    // Owner: Role A. 所有资料的数组 + counter + .txt 存读 (INTEGRATION.md §3 + §5)
    // 规则: 只用 array + int counter,不用 ArrayList/HashMap;占用的格子是 [0, count)
    public static final int MAX_USERS = 100; //容量是提议值,全组同意后再定
    public static final int MAX_PROFILES = 20;
    public static final int MAX_PARTNERS = 200;
    public static final int MAX_PHRASES = 1000;
    public static final int MAX_FEEDBACK = 2000;
    private final String dataFolder; //例如 "data",放 users.txt / profiles.txt ... (已被 .gitignore 忽略)
    private User[] users = new User[MAX_USERS];
    private int userCount = 0;
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
        // TODO Role A: 读 6 个 .txt;文件不存在 = 第一次运行 (空资料);文件坏了要报错,不能当成空的
    }
    public void saveAll() throws IOException
    {
        // TODO Role A: 先写 .tmp 再替换,写失败要保留原本的文件
    }
    public User[] getUsers()
    {
        // TODO Role A: 回传 exact-length 的副本 (不是内部数组本身)
        return new User[0];
    }
    public CommunicationProfile[] getProfiles()
    {
        // TODO Role A
        return new CommunicationProfile[0];
    }
    public ConversationPartner[] getPartners(String profileId) throws InvalidInputException
    {
        // TODO Role A: 只回传这个 profile 的 partner
        return new ConversationPartner[0];
    }
    public CommunicationProfile getProfile(String profileId) throws InvalidInputException
    {
        // TODO Role A: loop 找;找不到 throw InvalidInputException
        throw new InvalidInputException("getProfile not implemented yet (Role A)");
    }
    public ConversationPartner getPartner(String profileId, String partnerId) throws InvalidInputException
    {
        // TODO Role A: 要同时检查 partner 是不是属于这个 profile
        throw new InvalidInputException("getPartner not implemented yet (Role A)");
    }
    public Phrase[] getPhrases(String profileId, String partnerId) throws InvalidInputException
    {
        // TODO Role A: 找不到就回传 new Phrase[0],不要回传 null
        return new Phrase[0];
    }
    public Feedback[] getFeedback(String profileId, String partnerId) throws InvalidInputException
    {
        // TODO Role A
        return new Feedback[0];
    }
    public void saveUser(User user) throws InvalidInputException, StorageFullException, IOException
    {
        // TODO Role A: upsert (同 ID 就更新,新 ID 才加)
    }
    public void saveProfile(CommunicationProfile profile) throws InvalidInputException, StorageFullException, IOException
    {
        // TODO Role A: upsert
    }
    public void savePartner(ConversationPartner partner) throws InvalidInputException, StorageFullException, IOException
    {
        // TODO Role A: upsert + 检查 profile 存在
    }
    public void savePhrase(Phrase phrase) throws InvalidInputException, StorageFullException, IOException
    {
        // TODO Role A: 满了 throw StorageFullException,不能偷偷丢掉
    }
    public void recordDecision(Feedback feedback, Phrase optionalConfirmedPhrase) throws InvalidInputException, StorageFullException, IOException
    {
        // TODO Role A: feedback + (可选) phrase 一起存;两个都要先检查完才改数组
    }
    public void appendAILog(String requestId, String profileId, String partnerId, String contextCode, String displayedReply) throws IOException, StorageFullException
    {
        // TODO Role A: 只记录显示给用户的 AI 回复,不能记 key / prompt;5 MB 上限
    }
    public void deletePhrase(String profileId, String phraseId) throws InvalidInputException, IOException
    {
        // TODO Role A: 删除后往左 shift,清掉最后一格,count--
    }
    public void deleteFeedback(String profileId, String feedbackId) throws InvalidInputException, IOException
    {
        // TODO Role A
    }
    public void deleteProfileData(String profileId) throws IOException
    {
        // TODO Role A
    }
    public String nextId(String prefix)
    {
        // TODO Role A: 用读回来的最大编号 +1,不是用 count (删过东西 count 会变小 -> ID 重复)
        return prefix + "000";
    }
}
