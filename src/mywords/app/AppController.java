/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.app;
import java.io.IOException;
import mywords.ai.AIHelper;
import mywords.ai.GenerationRequest;
import mywords.model.Codes;
import mywords.exception.AIServiceException;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.logic.MemoryManager;
import mywords.logic.SuggestionService;
import mywords.model.CommunicationProfile;
import mywords.model.Communicator;
import mywords.model.ConversationPartner;
import mywords.model.Feedback;
import mywords.model.Phrase;
import mywords.model.Supporter;
import mywords.model.Suggestion;
import mywords.model.User;
import mywords.storage.FileManager;
public class AppController
{
    // Owner: Role A. 画面 (D) 只跟这个 class 讲话;它再去叫 FileManager / AI / Memory
    private final FileManager fileManager;
    private final AIHelper aiHelper;
    private final MemoryManager memoryManager;
    private final SuggestionService suggestionService;
    private User activeUser; //现在是谁在用 (注意: 选 profile 不是安全登录)
    private String activeProfileId;
    private String activePartnerId;
    private String lastFallbackMessage = ""; //AI 失败时给画面显示的原因
    private int sessionVersion = 0; //每次换 session 就 +1,用来挡掉迟到的旧 AI 回复
    public AppController(FileManager fileManager, AIHelper aiHelper, MemoryManager memoryManager, SuggestionService suggestionService)
    {
        this.fileManager = fileManager;
        this.aiHelper = aiHelper;
        this.memoryManager = memoryManager;
        this.suggestionService = suggestionService;
    }
    public FileManager getFileManager()
    {
        return fileManager;
    }
    public void selectSession(User user, String profileId, String partnerId) throws InvalidInputException
    {
        if (user == null) //一定要先选 user
        {
            throw new InvalidInputException("Please choose a user first.");
        }
        fileManager.getPartner(profileId, partnerId); //顺便检查 profile + partner 存在而且配对 (不配对会 throw)
        activeUser = user;
        activeProfileId = profileId;
        activePartnerId = partnerId;
        sessionVersion++; //session 换了
    }
    public String getActiveProfileId()
    {
        return activeProfileId;
    }
    public String getActivePartnerId()
    {
        return activePartnerId;
    }
    public User getActiveUser()
    {
        return activeUser;
    }
    public int getSessionVersion()
    {
        return sessionVersion; //画面在送出请求前记下来,回来时比较
    }
    public boolean isCurrentSession(int version, String profileId, String partnerId)
    {
        return version == sessionVersion && profileId != null && profileId.equals(activeProfileId) && partnerId != null && partnerId.equals(activePartnerId); //版本 + profile + partner 都一样才算同一个 session
    }
    public boolean canActiveUserChangeConsent()
    {
        // 同意 (AI 分享) 只能由 communicator 本人改,supporter 就算有授权也不行 (PRIVACY.md)
        return activeUser instanceof Communicator && activeProfileId != null && activeProfileId.equals(activeUser.getProfileId());
    }
    public boolean canActiveUserEditProfile()
    {
        return canEdit(activeUser, activeProfileId); //交给下面共用的检查
    }
    private boolean canEdit(User actingUser, String profileId)
    {
        if (actingUser == null || profileId == null) //没有人或没有 profile -> 不能改
        {
            return false;
        }
        boolean sameProfile = profileId.equals(actingUser.getProfileId()); //只能改自己关联的 profile
        return sameProfile && actingUser.canEditProfile(); //用 User 父类 reference 叫 override 的方法 = polymorphism
    }
    // ===================== 新增 / 修改 profile、partner、supporter (给 Role D 的画面用) =====================
    public static final int MAX_NAME_LENGTH = 60; //名字长度上限 (提议值)
    public static final int MAX_STYLE_LENGTH = 120; //语言风格 / 语气 长度上限 (INTEGRATION.md 提议 120)
    private static String requireName(String text, String fieldName) throws InvalidInputException
    {
        if (text == null || text.trim().isEmpty()) //没填
        {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
        String clean = text.trim(); //去掉前后空格
        if (clean.length() > MAX_NAME_LENGTH) //太长
        {
            throw new InvalidInputException(fieldName + " must be " + MAX_NAME_LENGTH + " characters or fewer.");
        }
        return clean;
    }
    private static String optionalText(String text, String fieldName) throws InvalidInputException
    {
        if (text == null) //可以不填 -> 当作空字串
        {
            return "";
        }
        String clean = text.trim();
        if (clean.length() > MAX_STYLE_LENGTH)
        {
            throw new InvalidInputException(fieldName + " must be " + MAX_STYLE_LENGTH + " characters or fewer.");
        }
        return clean; //原样保存,不翻译、不"纠正" slang
    }
    public CommunicationProfile createProfile(String communicatorName, String languageStyle, String preferredTone) throws InvalidInputException, StorageFullException, IOException
    {
        // 新的说话的人: 一次建 Communicator + 他的 profile (AI 分享默认关闭,要本人自己开)
        String name = requireName(communicatorName, "Communicator name"); //1. 先检查输入
        String style = optionalText(languageStyle, "Language style");
        String tone = optionalText(preferredTone, "Preferred tone");
        if (fileManager.getUsers().length >= FileManager.MAX_USERS) //2. 先检查两边容量,避免只存一半
        {
            throw new StorageFullException("User storage is full (" + FileManager.MAX_USERS + ").");
        }
        if (fileManager.getProfiles().length >= FileManager.MAX_PROFILES)
        {
            throw new StorageFullException("Profile storage is full (" + FileManager.MAX_PROFILES + ").");
        }
        String userId = fileManager.nextId("U"); //3. 拿新的 ID,例如 U003
        String profileId = fileManager.nextId("P"); //例如 P002
        CommunicationProfile profile = new CommunicationProfile(profileId, userId, name, style, tone, false); //false = AI 分享先关
        fileManager.saveProfile(profile); //4. 存 profile
        fileManager.saveUser(new Communicator(userId, name, profileId)); //再存 user (子类 Communicator)
        return profile; //回传给画面,方便马上选它
    }
    public ConversationPartner createPartner(User actingUser, String profileId, String contextCode, String label, String preferredTone) throws InvalidInputException, StorageFullException, IOException
    {
        if (!canEdit(actingUser, profileId)) //没权限 (例如没授权的 Supporter) -> 拒绝
        {
            throw new InvalidInputException("This user is not allowed to add conversation partners to this profile.");
        }
        if (!Codes.isValid(contextCode, Codes.CONTEXTS)) //只能 FAMILY / FRIENDS / HEALTHCARE
        {
            throw new InvalidInputException("Choose FAMILY, FRIENDS or HEALTHCARE.");
        }
        String name = requireName(label, "Partner name");
        String tone = optionalText(preferredTone, "Preferred tone");
        ConversationPartner partner = new ConversationPartner(fileManager.nextId("CP"), profileId, contextCode, name, tone);
        fileManager.savePartner(partner); //FileManager 会再检查 profile 存在 + 容量
        return partner;
    }
    public Supporter addSupporter(User actingUser, String profileId, String supporterName, boolean authorized) throws InvalidInputException, StorageFullException, IOException
    {
        // 加帮忙的人 + 决定给不给授权: 这是一种同意,所以只有 Communicator 本人可以做
        if (!(actingUser instanceof Communicator) || !profileId.equals(actingUser.getProfileId()))
        {
            throw new InvalidInputException("Only the communicator can add a supporter to their own profile.");
        }
        fileManager.getProfile(profileId); //确认 profile 存在 (不存在会 throw)
        Supporter supporter = new Supporter(fileManager.nextId("U"), requireName(supporterName, "Supporter name"), profileId, authorized);
        fileManager.saveUser(supporter);
        return supporter;
    }
    public void setSupporterAuthorization(User actingUser, Supporter supporter, boolean authorized) throws InvalidInputException, StorageFullException, IOException
    {
        if (!(actingUser instanceof Communicator) || supporter == null || !actingUser.getProfileId().equals(supporter.getProfileId())) //只有同一个 profile 的 Communicator
        {
            throw new InvalidInputException("Only the communicator can change this supporter's permission.");
        }
        boolean oldValue = supporter.isSupporterAuthorized(); //记住旧的值
        supporter.setSupporterAuthorized(authorized); //改授权
        try
        {
            fileManager.saveUser(supporter); //saveUser 是 upsert -> 同 ID 会更新
        }
        catch (IOException | StorageFullException | InvalidInputException e) //存不到 -> 物件也要改回去
        {
            supporter.setSupporterAuthorized(oldValue);
            throw e; //再丢给画面显示
        }
    }
    public void updateProfileSettings(User actingUser, String profileId, String languageStyle, String preferredTone) throws InvalidInputException, StorageFullException, IOException
    {
        if (!canEdit(actingUser, profileId)) //Communicator: 可以 / 有授权 Supporter: 可以 / 没授权 Supporter: 不行
        {
            throw new InvalidInputException("This user is not allowed to change this profile's settings.");
        }
        String style = optionalText(languageStyle, "Language style"); //先检查完
        String tone = optionalText(preferredTone, "Preferred tone");
        CommunicationProfile profile = fileManager.getProfile(profileId);
        String oldStyle = profile.getLanguageStyle(); //记住旧的值,存档失败要改回去
        String oldTone = profile.getPreferredTone();
        profile.setLanguageStyle(style); //再改
        profile.setPreferredTone(tone);
        try
        {
            fileManager.saveProfile(profile); //存档
        }
        catch (IOException | StorageFullException | InvalidInputException e) //FileManager 只能还原数组,物件里的字段要我们自己改回去
        {
            profile.setLanguageStyle(oldStyle);
            profile.setPreferredTone(oldTone);
            throw e;
        }
    }
    public Suggestion[] requestSuggestions(String keywords) throws InvalidInputException, IOException, StorageFullException
    {
        // TODO Role A: 之后要放进 SwingWorker (背景执行),并检查回来时 session 有没有换
        if (activeProfileId == null || activePartnerId == null) //没选 session
        {
            throw new InvalidInputException("Please choose a profile and conversation partner first.");
        }
        if (keywords == null || keywords.trim().isEmpty()) //关键词不能空 (Role D 的 InputValidator 也会先检查)
        {
            throw new InvalidInputException("Please enter at least one keyword.");
        }
        CommunicationProfile profile = fileManager.getProfile(activeProfileId);
        ConversationPartner partner = fileManager.getPartner(activeProfileId, activePartnerId);
        Phrase[] saved = fileManager.getPhrases(activeProfileId, activePartnerId); //这个 profile + partner 的已保存句子
        String requestId = "REQ" + System.currentTimeMillis(); //暂时用时间当 ID,之后 Role B 决定格式
        if (!profile.isAiSharingEnabled()) //没同意分享给 AI -> 直接用 saved phrase
        {
            lastFallbackMessage = "AI sharing is off. Showing saved phrases.";
            return suggestionService.savedPhraseFallback(requestId, keywords, saved);
        }
        Feedback[] feedback = fileManager.getFeedback(activeProfileId, activePartnerId);
        Phrase[] memPhrases = memoryManager.selectPhrases(keywords, partner.getContextCode(), saved, MemoryManager.MAX_PHRASES); //Role C 挑相关的
        Feedback[] memFeedback = memoryManager.selectFeedback(keywords, partner.getContextCode(), feedback, MemoryManager.MAX_FEEDBACK);
        GenerationRequest request = new GenerationRequest(requestId, activeProfileId, activePartnerId, partner.getContextCode(), keywords.trim(), profile.getLanguageStyle(), partner.getPreferredTone(), true, memPhrases, memFeedback);
        try
        {
            lastFallbackMessage = "";
            return aiHelper.generateSuggestions(request); //Role B: 真正问 AI
        }
        catch (AIServiceException e) //没 key / 没网络 / 格式错 -> 不让 app 死,改用 saved phrase
        {
            lastFallbackMessage = "AI unavailable: " + e.getMessage() + " Showing saved phrases.";
            return suggestionService.savedPhraseFallback(requestId, keywords, saved);
        }
    }
    public String getLastFallbackMessage()
    {
        return lastFallbackMessage;
    }
}
