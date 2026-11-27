/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.app;
import java.io.IOException;
import mywords.ai.AIHelper;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.logic.MemoryManager;
import mywords.logic.SuggestionService;
import mywords.storage.FileManager;
import mywords.web.BrowserMain;
public class Main
{
    // The Scanner menu uses this factory for the shared backend.
    public static AppController createController() throws IOException, InvalidInputException, StorageFullException
    {
        FileManager fileManager = new FileManager("data"); //资料放在 project 里的 data/ 资料夹
        fileManager.loadAll(); //读 data/*.txt;没有档案 = 第一次运行
        DemoData.createIfEmpty(fileManager); //完全没资料时建虚构示范资料
        AIHelper aiHelper = new AIHelper(fileManager);
        return new AppController(fileManager, aiHelper, new MemoryManager(), new SuggestionService()); //GUI 和文字菜单共用同一套后端
    }
    public static void main(String[] args)
    {
        try
        {
            BrowserMain.main(args);
        }
        catch (IOException | InvalidInputException | StorageFullException e)
        {
            System.err.println("Could not start MyWords: " + e.getMessage());
        }
    }
}
