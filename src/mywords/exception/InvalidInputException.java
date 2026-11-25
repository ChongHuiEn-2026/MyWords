/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.exception;
public class InvalidInputException extends Exception
{
    // Owner: Role A. 输入不合法 (空关键词、错 ID、错 code、关系不对) 时 throw
    public InvalidInputException(String message)
    {
        super(message); //把错误信息交给父类 Exception 保存
    }
    public InvalidInputException(String message, Throwable cause)
    {
        super(message, cause); //同时保存原本的错误 (cause),方便 debug
    }
}
