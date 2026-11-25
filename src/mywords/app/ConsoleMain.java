/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.app;
import java.util.Scanner;
public class ConsoleMain
{
    // Owner: Role A. 文字菜单版起点 (Scanner text menu),和 GUI 共用同一套后端
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in); //读键盘输入
        AppController controller;
        try
        {
            controller = Main.createController(); //和 GUI 一样: 读资料 + 建 service
        }
        catch (Exception e) //读档失败就结束,不要盖掉原本的档案
        {
            System.out.println("Could not load saved data: " + e.getMessage());
            return;
        }
        new ConsoleMenu(controller, input).run(); //进入菜单
    }
}
