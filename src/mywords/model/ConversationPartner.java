/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.model;
public class ConversationPartner
{
    // Owner: Role A. 对话对象 + 场景,例如 "Ah Ma" / FAMILY
    private String partnerId; //例如 CP001
    private String profileId; //属于哪个 profile (不能跨 profile 用)
    private String contextCode; //FAMILY / FRIENDS / HEALTHCARE
    private String label; //显示名字,例如 "Ah Ma"
    private String preferredTone; //对这个人用的语气
    public ConversationPartner(String partnerId, String profileId, String contextCode, String label, String preferredTone)
    {
        this.partnerId = partnerId;
        this.profileId = profileId;
        this.contextCode = contextCode;
        this.label = label;
        this.preferredTone = preferredTone;
    }
    public String getPartnerId()
    {
        return partnerId;
    }
    public String getProfileId()
    {
        return profileId;
    }
    public String getContextCode()
    {
        return contextCode;
    }
    public String getLabel()
    {
        return label;
    }
    public String getPreferredTone()
    {
        return preferredTone;
    }
    public void setLabel(String label)
    {
        this.label = label;
    }
    public void setPreferredTone(String preferredTone)
    {
        this.preferredTone = preferredTone;
    }
}
