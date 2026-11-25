/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.app;
import java.io.IOException;
import mywords.ai.AIHelper;
import mywords.ai.GenerationRequest;
import mywords.exception.AIServiceException;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.logic.MemoryManager;
import mywords.logic.SuggestionService;
import mywords.model.CommunicationProfile;
import mywords.model.ConversationPartner;
import mywords.model.Feedback;
import mywords.model.Phrase;
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
    }
    public boolean canActiveUserEditProfile()
    {
        if (activeUser == null) //还没选 user 就不能改
        {
            return false;
        }
        boolean sameProfile = activeProfileId != null && activeProfileId.equals(activeUser.getProfileId()); //只能改自己关联的 profile
        return sameProfile && activeUser.canEditProfile(); //用 User 父类 reference 叫 override 的方法 = polymorphism
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
