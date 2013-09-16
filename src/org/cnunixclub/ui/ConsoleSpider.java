/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.ui;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cnunixclub.controller.SpiderController;
import org.cnunixclub.db.MySqlHelper;
import org.cnunixclub.plugin.CncvodResolve;
import org.cnunixclub.spider.Interface.IVideoSiteResolveAdapter;
import org.cnunixclub.spider.Interface.IVideoSiteResolveStatus;

/**
 *
 * @author wcss
 */
public class ConsoleSpider implements Runnable, IVideoSiteResolveStatus {

    static SpiderController sc = new SpiderController();
    static IVideoSiteResolveAdapter vsra = new CncvodResolve();

    public static void main(String[] args) {
        if (args.length >= 4) {
            try {
                ConsoleSpider obj = new ConsoleSpider();
                sc.maxPageCount = Integer.parseInt(args[0]);
                sc.resolveStatusEvent = obj;
                MySqlHelper.setConnection(args[1], args[2], args[3]);
                System.out.println("最大页号：" + sc.maxPageCount + ",数据库名：" + args[1] + ",用户名：" + args[2] + ",密码：" + args[3]);
                sc.start(vsra, "www.cncvod.com", true);
                new Thread(obj).start();
            } catch (Exception ex) {
                Logger.getLogger(ConsoleSpider.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("参数错误！ex:" + ex.toString());
            }
        } else {
            System.out.println("参数错误！");
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConsoleSpider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    int textrowcount = 0;
    String logText = "";

    @Override
    public void processResolveStatus(IVideoSiteResolveAdapter ivsra, int i, Object o) {
        textrowcount++;
        if (textrowcount >= 40) {
            if (new File(JAppToolKit.JRunHelper.getUserHomeDirPath() + "/spider.log").exists()) {
                try {
                    JAppToolKit.JDataHelper.appendLineToFileEnd(JAppToolKit.JRunHelper.getUserHomeDirPath() + "/spider.log", logText);
                } catch (Exception ex) {
                    Logger.getLogger(MovieSpiderDemoFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    JAppToolKit.JDataHelper.writeAllLines(JAppToolKit.JRunHelper.getUserHomeDirPath() + "/spider.log", new String[]{logText});
                } catch (Exception ex) {
                    Logger.getLogger(ConsoleSpider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            textrowcount = 0;
            logText = "";
        }

        logText += o + "\n";
        System.out.println(o + "");
    }
}
