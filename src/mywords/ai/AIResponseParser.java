/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.ai;
import mywords.exception.AIServiceException;
public class AIResponseParser
{
    // Owner: Role B. 不用 JSON library,自己用 String/char 扫描
    public String extractContent(String providerJson) throws AIServiceException
    {
        // TODO Role B: 从 provider 的 JSON envelope 拿出 model content
        throw new AIServiceException("extractContent not implemented yet (Role B)");
    }
    public String[] parseCandidates(String content) throws AIServiceException
    {
        // TODO Role B: content 必须刚好是 3 个不同的非空字串的 JSON array,否则 throw
        throw new AIServiceException("parseCandidates not implemented yet (Role B)");
    }
}
