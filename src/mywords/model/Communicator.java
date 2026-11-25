/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.model;
public class Communicator extends User
{
    // Owner: Role A. 真正用 app 说话的人 (aphasia user),可以改自己的 profile
    public Communicator(String userId, String displayName, String profileId)
    {
        super(userId, displayName, profileId); //交给父类 User 的 constructor
    }
    @Override
    public String getTypeCode()
    {
        return "COMMUNICATOR";
    }
    @Override
    public boolean canEditProfile()
    {
        return true; //自己的 profile 永远可以改 (controller 还会再检查是不是自己的 profile)
    }
}
