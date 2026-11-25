/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.model;
public class Feedback
{
    // Owner: Role C (存档由 Role A 的 FileManager 负责). 用户对一个候选句的决定 + 原因
    private String feedbackId; //例如 FB001
    private String profileId;
    private String partnerId;
    private String contextCode;
    private String keywords;
    private String candidateText; //当时显示的候选句
    private String decisionCode; //APPROVE / EDIT / REJECT
    private String reasonCode; //NONE / WRONG_MEANING / ...
    private String confirmedText; //EDIT 时必须有;REJECT 可以空
    private boolean allowAIReuse;
    private long createdAt;
    public Feedback(String feedbackId, String profileId, String partnerId, String contextCode, String keywords, String candidateText, String decisionCode, String reasonCode, String confirmedText, boolean allowAIReuse, long createdAt)
    {
        this.feedbackId = feedbackId;
        this.profileId = profileId;
        this.partnerId = partnerId;
        this.contextCode = contextCode;
        this.keywords = keywords;
        this.candidateText = candidateText;
        this.decisionCode = decisionCode;
        this.reasonCode = reasonCode;
        this.confirmedText = confirmedText;
        this.allowAIReuse = allowAIReuse;
        this.createdAt = createdAt;
    }
    public String getFeedbackId()
    {
        return feedbackId;
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
    public String getCandidateText()
    {
        return candidateText;
    }
    public String getDecisionCode()
    {
        return decisionCode;
    }
    public String getReasonCode()
    {
        return reasonCode;
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
        this.allowAIReuse = allowAIReuse;
    }
}
