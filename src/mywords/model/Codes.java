/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.model;
public class Codes
{
    // Owner: Role A. 全组共用的 code 常量 + 检查方法 (INTEGRATION.md §1)
    public static final String[] CONTEXTS = {"FAMILY", "FRIENDS", "HEALTHCARE", "OTHERS"}; //对话场景
    public static final String[] DECISIONS = {"APPROVE", "EDIT", "REJECT"}; //用户对候选句的决定
    public static final String[] REASONS = {"NONE", "WRONG_MEANING", "WRONG_TONE", "WRONG_PERSON_CONTEXT", "OTHER_UNSURE"}; //拒绝原因
    public static final String SOURCE_AI = "AI"; //候选句来自 AI
    public static final String SOURCE_SAVED = "SAVED"; //候选句来自已保存的 phrase
    public static boolean isValid(String code, String[] allowed)
    {
        if (code == null) //null 直接当作不合法
        {
            return false;
        }
        for (int i = 0; i < allowed.length; i++) //用 loop 逐个比较,不用 Collections
        {
            if (allowed[i].equals(code)) //完全一样 (大小写也要一样) 才算
            {
                return true;
            }
        }
        return false; //找完都没有 = 不合法
    }
}
