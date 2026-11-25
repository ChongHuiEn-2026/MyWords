/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.ui;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import mywords.app.AppController;
public class MainFrame extends JFrame
{
    // Owner: Role D. 主视窗 (现在只是占位,让 app 可以跑起来)
    private final AppController controller; //画面只叫 controller,不自己存档/连网
    public MainFrame(AppController controller)
    {
        super("MyWords - Let Me Finish My Sentence"); //视窗标题
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //关视窗 = 结束程式
        setSize(800, 600);
        setLocationRelativeTo(null); //放在屏幕中间
        add(new JLabel("MyWords skeleton v0.1 - screens coming soon (Role D)", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
