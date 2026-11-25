/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.model;
public abstract class User
{
    // Owner: Role A. 父类 User,示范 inheritance + polymorphism (子类 override canEditProfile)
    private String userId; //例如 U001
    private String displayName; //显示的名字
    private String profileId; //这个 user 关联的 CommunicationProfile ID
    public User(String userId, String displayName, String profileId)
    {
        this.userId = userId;
        this.displayName = displayName;
        this.profileId = profileId;
    }
    public String getUserId()
    {
        return userId;
    }
    public String getDisplayName()
    {
        return displayName;
    }
    public String getProfileId()
    {
        return profileId;
    }
    public abstract String getTypeCode(); //COMMUNICATOR 或 SUPPORTER,存进 users.txt 用
    public abstract boolean canEditProfile(); //子类各自 override:能不能改 profile
}
