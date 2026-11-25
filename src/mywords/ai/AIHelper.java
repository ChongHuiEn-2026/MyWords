/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.ai;
import java.io.IOException;
import mywords.exception.AIServiceException;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.model.Suggestion;
import mywords.storage.FileManager;
public class AIHelper
{
    // Owner: Role B. 唯一可以连网的地方,所有 AI 请求都要经过 ask(String prompt)
    private final FileManager fileManager; //显示前要用它写 ai_log.txt
    private final PromptBuilder promptBuilder = new PromptBuilder();
    private final AIResponseParser parser = new AIResponseParser();
    public AIHelper(FileManager fileManager)
    {
        this.fileManager = fileManager;
    }
    public String ask(String prompt) throws AIServiceException
    {
        // TODO Role B: HttpURLConnection, 10s connect / 20s read timeout, 64 KiB 上限, 检查 HTTP status
        throw new AIServiceException("AI not connected yet (Role B TODO)"); //现在先一律失败 -> 会走 saved phrase fallback
    }
    public Suggestion[] generateSuggestions(GenerationRequest request) throws AIServiceException, InvalidInputException, IOException, StorageFullException
    {
        // TODO Role B: 检查同意 -> build prompt -> ask -> parse 3 句 -> 每句 appendAILog -> 回传
        String prompt = promptBuilder.build(request); //1. 组 prompt
        String content = ask(prompt); //2. 连网 (现在一定 throw)
        String[] texts = parser.parseCandidates(content); //3. 解析 3 句
        Suggestion[] result = new Suggestion[texts.length];
        for (int i = 0; i < texts.length; i++)
        {
            fileManager.appendAILog(request.getRequestId(), request.getProfileId(), request.getPartnerId(), request.getContextCode(), texts[i]); //4. 显示前先记 log
            result[i] = new Suggestion(request.getRequestId() + "-" + (i + 1), request.getRequestId(), texts[i], "AI");
        }
        return result;
    }
}
