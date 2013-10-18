/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.ui;

import Manager.DownloaderManager;
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
import org.cnunixclub.spider.helper.HTMLDownloader;

/**
 *
 * @author wcss
 */
public class ConsoleSpider implements Runnable, IVideoSiteResolveStatus {

    public static SpiderController spiderController = null;
    public static IVideoSiteResolveAdapter resolveAdapter = new CncvodResolve();
    public static Thread tt = null;
    public static Boolean isRunSpider = true;
    public static HttpServer hs = null;
    public static ConsoleSpider localObj = new ConsoleSpider();
    public static String dbNameFinal = "";
    public static String dbUserFinal = "";
    public static String dbPwdFinal = "";
    public static int maxPageCountFinal = 0;
    private int textrowcount = 0;
    private static String logText = "";
    public static SpiderTaskContent currentSpiderTaskContent;

    /**
     * 程序入口
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 4) {
            try 
            {
                maxPageCountFinal = Integer.parseInt(args[0]);
                dbNameFinal = args[1];
                dbUserFinal = args[2];
                dbPwdFinal =  args[3];
                
                System.out.println("最大页号：" + maxPageCountFinal + ",数据库名：" + dbNameFinal + ",用户名：" + dbUserFinal + ",密码：" + dbPwdFinal);

                //设置Mysql参数
                MySqlHelper.setConnection(dbNameFinal, dbUserFinal, dbPwdFinal);

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

    /**
     * 启动抓取控制器
     *
     * @param maxCount
     * @param jumpCount
     * @throws Exception
     */
    public static void startSpiderWorker(int maxCount, int jumpCount) throws Exception {
        spiderController = new SpiderController();
        currentSpiderTaskContent = new SpiderTaskContent();
        spiderController.maxPageCount = maxCount;
        spiderController.resolveStatusEvent = localObj;
        spiderController.start(resolveAdapter, "www.cncvod.com", jumpCount, true);

        System.out.println("最大页号：" + spiderController.maxPageCount + ",最大跳过的频道数：" + spiderController.jumpChannelCount);
        spiderController.printLogText("最大页号：" + spiderController.maxPageCount + ",最大跳过的频道数：" + spiderController.jumpChannelCount);
    }

    /**
     * 启动HTTP服务器
     */
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

    /**
     * 继续当前任务
     * @throws Exception 
     */
    public static void continueCurrentTask() throws Exception
    {
        if (spiderController != null && currentSpiderTaskContent != null && currentSpiderTaskContent.taskType != null && currentSpiderTaskContent.urlList != null && currentSpiderTaskContent.urlList.length > 0)
        {
            spiderController.downloadTask(currentSpiderTaskContent.taskType, currentSpiderTaskContent.urlList);
        }
    }
    
    /**
     * 停止抓取任务
     */
    public static void stopSpiderTask()
    {
        try {
            DownloaderManager.manager.stopAllDownloader();
        } catch (Exception ex) {
            Logger.getLogger(ConsoleSpider.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            JAppToolKit.JRunHelper.runSysCmd("rm -rf " + HTMLDownloader.getBufferDir());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 返回控制器状态与工作日志
     *
     * @return
     */
    public static String getSpiderWorkStatus() {
        if (spiderController != null) {
            return spiderController.getWorkeStatus() + "\n当前日志:\n" + logText;
        } else {
            return null;
        }
    }

    /**
     * 退出本程序
     */
    public static void quitSpiderService() {
        isRunSpider = false;
        if (hs != null) {
            hs.stop(0);
        }
    }

    @Override
    public void processResolveStatus(IVideoSiteResolveAdapter ivsra, int i, Object o) {
        if (spiderController.printLogStatusValue == i) {
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
        } else if (i == spiderController.finishSpiderTaskStatusValue) {
            logText += "任务完成！";
        }
    }
}