/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.ui;

import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
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

    static SpiderController sc = null;
    static IVideoSiteResolveAdapter vsra = new CncvodResolve();
    static Thread tt = null;
    static Boolean isRunSpider = true;
    static HttpServer hs = null;
    static ConsoleSpider localObj = new ConsoleSpider();
    public static int maxPageCountFinal = 0; 

    public static void main(String[] args) {
        if (args.length >= 4) {
            try {
                System.out.println("最大页号：" + args[0] + ",数据库名：" + args[1] + ",用户名：" + args[2] + ",密码：" + args[3]);

                //设置Mysql参数
                MySqlHelper.setConnection(args[1], args[2], args[3]);

                maxPageCountFinal = Integer.parseInt(args[0]);
                
                startSpiderWorker(Integer.parseInt(args[0]), 0);

                tt = new Thread(localObj);
                tt.start();
            } catch (Exception ex) {
                Logger.getLogger(ConsoleSpider.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("参数错误！ex:" + ex.toString());
            }
        } else {
            System.out.println("参数错误！");
        }
    }

    public static void startSpiderWorker(int maxCount, int jumpCount) throws Exception {
        sc = new SpiderController();
        sc.maxPageCount = maxCount;
        sc.resolveStatusEvent = localObj;
        sc.start(vsra, "www.cncvod.com", jumpCount, true);

        System.out.println("最大页号：" + sc.maxPageCount + ",最大跳过的频道数：" + sc.jumpChannelCount);
        sc.printLogText("最大页号：" + sc.maxPageCount + ",最大跳过的频道数：" + sc.jumpChannelCount);
    }

    public static void startHTTPServers() {
        try {
            hs = HttpServer.create(new InetSocketAddress(43922), 0);//设置HttpServer的端口为8888
            hs.createContext("/test", new MyHandler());//用MyHandler类内处理到/chinajash的请求
            hs.setExecutor(null); // creates a default executor
            hs.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startHTTPServers();

        while (isRunSpider) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConsoleSpider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    int textrowcount = 0;
    static String logText = "";

    public static String getSpiderWorkStatus() {
        return sc.getWorkeStatus() + "\n当前日志:\n" + logText;
    }

    @Override
    public void processResolveStatus(IVideoSiteResolveAdapter ivsra, int i, Object o) {
        if (sc.printLogStatusValue == i) {
            textrowcount++;
            if (textrowcount >= 20) {
                try {
                    JAppToolKit.JDataHelper.writeAllLines(JAppToolKit.JRunHelper.getUserHomeDirPath() + "/spider.log", new String[]{logText});
                } catch (Exception ex) {
                    Logger.getLogger(ConsoleSpider.class.getName()).log(Level.SEVERE, null, ex);
                }

                textrowcount = 0;
                logText = "";
            }

            logText += o + "\n";

            //System.out.println(getSpiderWorkStatus());
        } else if (i == sc.finishSpiderTaskStatusValue) {
            isRunSpider = false;
            if (hs != null) {
                hs.stop(0);
            }
        }
    }
}