/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.logic;
import mywords.exception.InvalidInputException;
import mywords.model.Feedback;
public class FeedbackManager
{
    // Owner: Role C. 检查并建立 Feedback (APPROVE / EDIT / REJECT + reason)
    public Feedback createFeedback(String feedbackId, String profileId, String partnerId, String contextCode, String keywords, String candidateText, String decisionCode, String reasonCode, String confirmedText, boolean allowAIReuse) throws InvalidInputException
    {
        // TODO Role C: EDIT 必须有 confirmedText;APPROVE 的 reason = NONE;code 要合法
        throw new InvalidInputException("createFeedback not implemented yet (Role C)");
    }
}
