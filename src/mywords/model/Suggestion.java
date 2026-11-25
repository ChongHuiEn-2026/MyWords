/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.model;
public class Suggestion
{
    // Owner: Role C. 暂时的候选句 (不存档),来源是 AI 或 SAVED
    private String suggestionId;
    private String requestId; //属于哪一次请求,用来挡掉迟到的旧回复
    private String text;
    private String sourceCode; //Codes.SOURCE_AI 或 Codes.SOURCE_SAVED
    public Suggestion(String suggestionId, String requestId, String text, String sourceCode)
    {
        this.suggestionId = suggestionId;
        this.requestId = requestId;
        this.text = text;
        this.sourceCode = sourceCode;
    }
    public String getSuggestionId()
    {
        return suggestionId;
    }
    public String getRequestId()
    {
        return requestId;
    }
    public String getText()
    {
        return text;
    }
    public String getSourceCode()
    {
        return sourceCode;
    }
}
