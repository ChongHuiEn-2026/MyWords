/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.app;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import mywords.ai.AIHelper;
import mywords.logic.MemoryManager;
import mywords.logic.SuggestionService;
import mywords.storage.FileManager;
import mywords.ui.MainFrame;
public class Main
{
    // Owner: Role A. 程式起点: 读资料 -> 建 service (只建一次) -> 开 GUI
    public static void main(String[] args)
    {
        FileManager fileManager = new FileManager("data"); //资料放在 project 里的 data/ 资料夹
        try
        {
            fileManager.loadAll(); //读 .txt (现在是空的 TODO)
        }
        catch (Exception e) //读档失败: 告诉用户,不要假装成功
        {
            JOptionPane.showMessageDialog(null, "Could not load saved data: " + e.getMessage(), "MyWords", JOptionPane.ERROR_MESSAGE);
            return; //资料坏了就不继续,避免盖掉原本的档案
        }
        AIHelper aiHelper = new AIHelper(fileManager);
        AppController controller = new AppController(fileManager, aiHelper, new MemoryManager(), new SuggestionService());
        SwingUtilities.invokeLater(() -> new MainFrame(controller).setVisible(true)); //GUI 要在 Swing 的 thread 开
    }
}
