/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.model;
public class Phrase
{
    // Owner: Role A. 用户确认过的句子 (approved / edited),可以离线重用
    private String phraseId; //例如 PH001
    private String profileId;
    private String partnerId;
    private String contextCode;
    private String keywords; //当初输入的关键词
    private String confirmedText; //用户确认的最终句子,原样保存不翻译
    private boolean allowAIReuse; //是否允许以后送去 AI 当例子 (和"保存"是两个不同的同意)
    private long createdAt; //Unix epoch milliseconds
    public Phrase(String phraseId, String profileId, String partnerId, String contextCode, String keywords, String confirmedText, boolean allowAIReuse, long createdAt)
    {
        this.phraseId = phraseId;
        this.profileId = profileId;
        this.partnerId = partnerId;
        this.contextCode = contextCode;
        this.keywords = keywords;
        this.confirmedText = confirmedText;
        this.allowAIReuse = allowAIReuse;
        this.createdAt = createdAt;
    }
    public String getPhraseId()
    {
        return phraseId;
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
    public String getConfirmedText()
    {
        return confirmedText;
    }
    public boolean isAllowAIReuse()
    {
        return allowAIReuse;
    }
    public long getCreatedAt()
    {
        return createdAt;
    }
    public void setAllowAIReuse(boolean allowAIReuse)
    {
        this.allowAIReuse = allowAIReuse; //用户可以随时撤回 AI 重用的同意
    }
}
