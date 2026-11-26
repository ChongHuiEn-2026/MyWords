/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.app;
public class RequestSnapshot
{
    // Owner: Role A. 按下 "Get suggestions" 那一刻的快照: 关键词 + profile + partner + AI 同意 + session 版本
    // 背景程序只用这张快照,不再去读"现在"的 session -> 换对话也不会把旧关键词用新 profile 的同意送出去
    private final String requestId; //例如 REQ1795...
    private final int sessionVersion; //按下时的 session 版本
    private final String profileId;
    private final String partnerId;
    private final String keywords; //已经 trim 过
    private final boolean aiSharingAtClick; //按下时 profile 有没有同意 AI 分享
    private String resultMessage = ""; //结果说明 (例如为什么没用 AI),只有 controller 会写
    public RequestSnapshot(String requestId, int sessionVersion, String profileId, String partnerId, String keywords, boolean aiSharingAtClick)
    {
        this.requestId = requestId;
        this.sessionVersion = sessionVersion;
        this.profileId = profileId;
        this.partnerId = partnerId;
        this.keywords = keywords;
        this.aiSharingAtClick = aiSharingAtClick;
    }
    public String getRequestId()
    {
        return requestId;
    }
    public int getSessionVersion()
    {
        return sessionVersion;
    }
    public String getProfileId()
    {
        return profileId;
    }
    public String getPartnerId()
    {
        return partnerId;
    }
    public String getKeywords()
    {
        return keywords;
    }
    public boolean isAiSharingAtClick()
    {
        return aiSharingAtClick;
    }
    public synchronized String getResultMessage()
    {
        return resultMessage;
    }
    synchronized void setResultMessage(String message) //没写 public = 只有同一个 package (mywords.app) 的 AppController 能改
    {
        resultMessage = message;
    }
}
