/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.logic;
import mywords.model.Phrase;
import mywords.model.Suggestion;
public class SuggestionService
{
    // Owner: Role C. 候选句流程 + AI 失败时用 saved phrase 补位
    public Suggestion[] savedPhraseFallback(String requestId, String keywords, Phrase[] savedPhrases)
    {
        // TODO Role C: 从已保存的 phrase 挑出相关的,sourceCode = SAVED (绝对不能假装是 AI)
        return new Suggestion[0];
    }
}
