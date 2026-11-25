/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.ui;
import mywords.exception.InvalidInputException;
public class InputValidator
{
    // Owner: Role D. 画面输入检查,错误信息要清楚,并且保留用户已经打的字
    public static final int MAX_KEYWORDS = 300; //提议值
    public static final int MAX_SENTENCE = 500;
    public String validateKeywords(String raw) throws InvalidInputException
    {
        // TODO Role D: trim 后不能空、不能超过 MAX_KEYWORDS
        throw new InvalidInputException("validateKeywords not implemented yet (Role D)");
    }
}
