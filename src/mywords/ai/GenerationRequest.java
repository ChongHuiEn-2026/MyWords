/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.ai;
import mywords.model.Feedback;
import mywords.model.Phrase;
public class GenerationRequest
{
    // Owner: Role B (和 A/C 协调). 一次 AI 请求的"快照",送出后就不再改
    private final String requestId; //例如 REQ001,由 Role B 分配
    private final String profileId;
    private final String partnerId;
    private final String contextCode;
    private final String keywords;
    private final String languageStyle;
    private final String preferredTone;
    private final boolean aiSharingEnabled;
    private final Phrase[] allowedPhrases; //已经过滤好、允许送去 AI 的例子
    private final Feedback[] allowedFeedback;
    public GenerationRequest(String requestId, String profileId, String partnerId, String contextCode, String keywords, String languageStyle, String preferredTone, boolean aiSharingEnabled, Phrase[] allowedPhrases, Feedback[] allowedFeedback)
    {
        this.requestId = requestId;
        this.profileId = profileId;
        this.partnerId = partnerId;
        this.contextCode = contextCode;
        this.keywords = keywords;
        this.languageStyle = languageStyle;
        this.preferredTone = preferredTone;
        this.aiSharingEnabled = aiSharingEnabled;
        this.allowedPhrases = copyPhrases(allowedPhrases); //复制一份,外面改了也不影响这次请求
        this.allowedFeedback = copyFeedback(allowedFeedback);
    }
    private static Phrase[] copyPhrases(Phrase[] src)
    {
        Phrase[] out = new Phrase[src.length];
        for (int i = 0; i < src.length; i++)
        {
            out[i] = src[i];
        }
        return out;
    }
    private static Feedback[] copyFeedback(Feedback[] src)
    {
        Feedback[] out = new Feedback[src.length];
        for (int i = 0; i < src.length; i++)
        {
            out[i] = src[i];
        }
        return out;
    }
    public String getRequestId()
    {
        return requestId;
    }
    public String getProfileId()
    {
        return profileId;
    }
    public String getPartnerId()
    {
        return partnerId;
    }
    public String getContextCode()
    {
        return contextCode;
    }
    public String getKeywords()
    {
        return keywords;
    }
    public String getLanguageStyle()
    {
        return languageStyle;
    }
    public String getPreferredTone()
    {
        return preferredTone;
    }
    public boolean isAiSharingEnabled()
    {
        return aiSharingEnabled;
    }
    public Phrase[] getAllowedPhrases()
    {
        return copyPhrases(allowedPhrases);
    }
    public Feedback[] getAllowedFeedback()
    {
        return copyFeedback(allowedFeedback);
    }
}
