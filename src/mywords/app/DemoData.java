/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mywords.app;
import java.io.IOException;
import mywords.exception.InvalidInputException;
import mywords.exception.StorageFullException;
import mywords.model.CommunicationProfile;
import mywords.model.Communicator;
import mywords.model.ConversationPartner;
import mywords.model.Phrase;
import mywords.model.Supporter;
import mywords.storage.FileManager;
public class DemoData
{
    // Owner: Role A. 第一次运行 (完全没有资料) 时建一组"虚构"示范资料,方便组员测试和录 demo
    // 全部是假的人和句子,不能放真实病人资料
    public static boolean createIfEmpty(FileManager fm) throws InvalidInputException, StorageFullException, IOException
    {
        if (fm.getUsers().length > 0 || fm.getProfiles().length > 0) //已经有资料 -> 什么都不做
        {
            return false;
        }
        long now = System.currentTimeMillis();
        fm.saveUser(new Communicator("U001", "Mei (demo)", "P001")); //说话的人
        fm.saveUser(new Supporter("U002", "Ah Hui (demo supporter)", "P001", false)); //帮忙的人,默认没授权
        fm.saveProfile(new CommunicationProfile("P001", "U001", "Mei (demo)", "Manglish, short sentences", "casual", false)); //AI 分享默认关闭,要用户自己开
        fm.savePartner(new ConversationPartner("CP001", "P001", "FAMILY", "Ah Ma", "warm"));
        fm.savePartner(new ConversationPartner("CP002", "P001", "HEALTHCARE", "Dr Lim (clinic)", "polite"));
        fm.savePhrase(new Phrase("PH001", "P001", "CP001", "FAMILY", "kopi tomorrow", "Tomorrow kopi with Ah Ma lah.", true, now));
        fm.savePhrase(new Phrase("PH002", "P001", "CP001", "FAMILY", "dinner together", "Tonight I want to makan dinner together.", true, now));
        fm.savePhrase(new Phrase("PH003", "P001", "CP002", "HEALTHCARE", "knee pain", "My knee is painful today.", false, now)); //健康资料: 默认不给 AI 重用
        return true;
    }
}
