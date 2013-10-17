/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.ui;

import Manager.DownloaderManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.cnunixclub.spider.helper.HTMLDownloader;

/**
 *
 * @author wcss
 */
public class MyHandler implements HttpHandler {

    public String makeResponseHtml(String responseMsg) {
        String response = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf8\" /><title>数据抓取机器人HTTP反馈信息</title></head>";
        response += responseMsg.replace("\n", "<br>");
        response += "</html>";
        return response;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String cmds = "";
        String response = "";
        String cmdResponse = "";
        try {
            cmds = t.getRequestURI().getRawQuery();
            if (cmds != null && cmds.startsWith("restart-"))
            {
                cmds = cmds.trim();
                
                int jumpCount = 0;
                try
                {
                    jumpCount = Integer.parseInt(cmds.replace("restart-", ""));
                }catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
                cmdResponse = "正在重新开启抓取模块！需要跳过" + jumpCount + "个频道！最大分页数：" + ConsoleSpider.maxPageCountFinal;
                
                try
                {
                    JAppToolKit.JRunHelper.runSysCmd("rm -rf " + HTMLDownloader.getBufferDir());
                }catch(Exception ex)
                {
                   ex.printStackTrace();
                }
                
                DownloaderManager.manager.stopAllDownloader();
                
                ConsoleSpider.startSpiderWorker(ConsoleSpider.maxPageCountFinal, jumpCount);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (cmds == null || (cmds != null && cmds.length() <= 2)) {
                InputStream is = t.getRequestBody();
                response = makeResponseHtml(ConsoleSpider.getSpiderWorkStatus());
                byte[] cnts = response.getBytes();
                t.sendResponseHeaders(200, cnts.length);
                OutputStream os = t.getResponseBody();
                os.write(cnts);
                os.flush();
                os.close();
            } else {
                InputStream is = t.getRequestBody();
                response = makeResponseHtml(cmdResponse);
                byte[] cnts = response.getBytes();
                t.sendResponseHeaders(200, cnts.length);
                OutputStream os = t.getResponseBody();
                os.write(cnts);
                os.flush();
                os.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}