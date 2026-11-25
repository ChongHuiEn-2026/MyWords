/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.model;
public class Supporter extends User
{
    // Owner: Role A. 帮忙的人 (家人/看护),只有被授权时才能改 profile
    private boolean supporterAuthorized; //communicator 有没有授权他帮忙 (不是密码,不是安全登录)
    public Supporter(String userId, String displayName, String profileId, boolean supporterAuthorized)
    {
        super(userId, displayName, profileId);
        this.supporterAuthorized = supporterAuthorized;
    }
    public boolean isSupporterAuthorized()
    {
        return supporterAuthorized;
    }
    public void setSupporterAuthorized(boolean supporterAuthorized)
    {
        this.supporterAuthorized = supporterAuthorized;
    }
    @Override
    public String getTypeCode()
    {
        return "SUPPORTER";
    }
    @Override
    public boolean canEditProfile()
    {
        return supporterAuthorized; //有授权才 true,和 Communicator 的版本不一样 = polymorphism
    }
}
