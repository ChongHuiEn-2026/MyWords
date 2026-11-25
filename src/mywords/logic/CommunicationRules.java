/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.logic;
public class CommunicationRules
{
    // Owner: Role C. 意思保留 + context/同意 的规则
    public boolean isEligibleForReuse(String activeContext, String recordContext, boolean allowAIReuse)
    {
        // TODO Role C: 例如 HEALTHCARE 的私人记录不能出现在 FAMILY 的建议里
        return false;
    }
}
