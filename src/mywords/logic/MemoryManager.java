/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.logic;
import mywords.model.Feedback;
import mywords.model.Phrase;
public class MemoryManager
{
    // Owner: Role C. 从"已允许"的记录里挑出最相关的几条 (不用 Collections,用 loop 排序)
    public static final int MAX_PHRASES = 3; //提议: 3 phrases + 2 feedback = 最多 5 条
    public static final int MAX_FEEDBACK = 2;
    public Phrase[] selectPhrases(String keywords, String contextCode, Phrase[] candidates, int limit)
    {
        // TODO Role C: 关键词重叠分数排序,同分看比较新的;排除 allowAIReuse=false 和其他 context
        return new Phrase[0];
    }
    public Feedback[] selectFeedback(String keywords, String contextCode, Feedback[] candidates, int limit)
    {
        // TODO Role C
        return new Feedback[0];
    }
}
