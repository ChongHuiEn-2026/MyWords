/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.app;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import mywords.ai.AIHelper;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.logic.MemoryManager;
import mywords.logic.SuggestionService;
import mywords.storage.FileManager;
import mywords.ui.MainFrame;
public class Main
{
    // Owner: Role A. GUI 版起点: 读资料 -> 建 service (只建一次) -> 开 Swing 视窗
    // 文字菜单版的起点是 ConsoleMain,两个都用下面同一个 createController()
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
        AppController controller;
        try
        {
            controller = createController();
        }
        catch (Exception e) //读档失败: 告诉用户,不要假装成功
        {
            JOptionPane.showMessageDialog(null, "Could not load saved data: " + e.getMessage(), "MyWords", JOptionPane.ERROR_MESSAGE);
            return; //资料坏了就不继续,避免盖掉原本的档案
        }
        SwingUtilities.invokeLater(() -> new MainFrame(controller).setVisible(true)); //GUI 要在 Swing 的 thread 开
    }
}
