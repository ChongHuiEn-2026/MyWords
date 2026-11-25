/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.model;
public class CommunicationProfile
{
    // Owner: Role A (fields 和 Role D 协调). 一个 communicator 的偏好设定
    private String profileId; //例如 P001
    private String ownerUserId; //这个 profile 属于哪个 Communicator
    private String displayName;
    private String languageStyle; //例如 "Manglish, short sentences" (用户自己选,不靠猜)
    private String preferredTone;
    private boolean aiSharingEnabled; //是否同意把资料送去 AI
    public CommunicationProfile(String profileId, String ownerUserId, String displayName, String languageStyle, String preferredTone, boolean aiSharingEnabled)
    {
        this.profileId = profileId;
        this.ownerUserId = ownerUserId;
        this.displayName = displayName;
        this.languageStyle = languageStyle;
        this.preferredTone = preferredTone;
        this.aiSharingEnabled = aiSharingEnabled;
    }
    public String getProfileId()
    {
        return profileId;
    }
    public String getOwnerUserId()
    {
        return ownerUserId;
    }
    public String getDisplayName()
    {
        return displayName;
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
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    public void setLanguageStyle(String languageStyle)
    {
        this.languageStyle = languageStyle;
    }
    public void setPreferredTone(String preferredTone)
    {
        this.preferredTone = preferredTone;
    }
    public void setAiSharingEnabled(boolean aiSharingEnabled)
    {
        this.aiSharingEnabled = aiSharingEnabled;
    }
}
