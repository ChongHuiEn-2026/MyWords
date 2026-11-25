/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.exception;
public class AIServiceException extends Exception
{
    // Owner: Role B. 没 key、没网络、HTTP 错误、timeout、AI 回复格式错 时 throw
    public AIServiceException(String message)
    {
        super(message); //把错误信息交给父类 Exception 保存
    }
    public AIServiceException(String message, Throwable cause)
    {
        super(message, cause); //同时保存原本的错误 (cause),方便 debug
    }
}
