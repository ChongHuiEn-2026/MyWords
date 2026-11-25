/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.exception;
public class StorageFullException extends Exception
{
    // Owner: Role A. 数组 / log 满了 (capacity limit) 时 throw
    public StorageFullException(String message)
    {
        super(message); //把错误信息交给父类 Exception 保存
    }
    public StorageFullException(String message, Throwable cause)
    {
        super(message, cause); //同时保存原本的错误 (cause),方便 debug
    }
}
