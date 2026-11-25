/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.ai;
import mywords.exception.AIServiceException;
public class AIConfig
{
    // Owner: Role B. 从本地 config.txt 读 apiKey / endpoint / model (key 绝对不能 commit)
    private String apiKey;
    private String endpoint;
    private String model;
    public static AIConfig load(String path) throws AIServiceException
    {
        // TODO Role B: 用 String 操作读 key=value;没有文件或 key 空 -> throw AIServiceException
        throw new AIServiceException("AIConfig.load not implemented yet (Role B)");
    }
    public String getApiKey()
    {
        return apiKey;
    }
    public String getEndpoint()
    {
        return endpoint;
    }
    public String getModel()
    {
        return model;
    }
}
