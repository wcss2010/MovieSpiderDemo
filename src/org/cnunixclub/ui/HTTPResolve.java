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
import java.net.URI;
import org.cnunixclub.controller.SpiderJumpParamEntry;
import org.cnunixclub.spider.helper.HTMLDownloader;

/**
 *
 * @author wcss
 */
public class HTTPResolve implements HttpHandler {

    /**
     * 生成反馈信息结构
     *
     * @param responseMsg
     * @return
     */
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
            cmdResponse = resolveHTTPCmdMethod(t.getRequestURI());
        } catch (Exception ex) {
            ex.printStackTrace();
            cmdResponse = ex.toString();
        }

        try {
            InputStream is = t.getRequestBody();
            response = makeResponseHtml(cmdResponse);
            byte[] cnts = response.getBytes();
            t.sendResponseHeaders(200, cnts.length);
            OutputStream os = t.getResponseBody();
            os.write(cnts);
            os.flush();
            os.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 解析HTTP指令
     *
     * @param uri
     * @return
     */
    public String resolveHTTPCmdMethod(URI uri) throws Exception {
        String result = "";
        String cmds = uri.getRawQuery();
        if (cmds != null && cmds.length() > 1) {
            if (cmds.startsWith("restart-")) {
                cmds = cmds.trim();

                int jumpChannelCount = 0;
                int jumpPagingCount = 0;
                try 
                {
                    String[] team = cmds.replace("restart-", "").split("@");
                    jumpChannelCount = Integer.parseInt(team[0]);
                    jumpPagingCount = Integer.parseInt(team[1]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                result = "正在重新开启抓取模块！从第" + (jumpChannelCount + 1) + "个频道的第" + (jumpPagingCount + 1) + "页开始！最大分页数：" + ConsoleSpider.maxPageCountFinal;
                ConsoleSpider.stopSpiderTask();
                ConsoleSpider.startSpiderWorker(ConsoleSpider.maxPageCountFinal, new SpiderJumpParamEntry(jumpChannelCount,jumpPagingCount));
            } else if (cmds.startsWith("quit")) {
                ConsoleSpider.quitSpiderService();
            } else if (cmds.startsWith("stop")) {
                ConsoleSpider.stopSpiderTask();
                ConsoleSpider.spiderController = null;
                result = "任务已停止";
            } else if (cmds.startsWith("continue")) {
                ConsoleSpider.continueCurrentTask();
                result = "任务正在继续执行";
            } else if (cmds.startsWith("setmaxpagecount-")) {
                try {
                    ConsoleSpider.maxPageCountFinal = Integer.parseInt(cmds.replace("setmaxpagecount-", "").trim());
                    result = "当前最大扫描页数为" + ConsoleSpider.maxPageCountFinal;
                    if (ConsoleSpider.spiderController != null)
                    {
                        ConsoleSpider.spiderController.maxPageCount = ConsoleSpider.maxPageCountFinal;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (cmds.startsWith("pause")) {
                DownloaderManager.manager.stopAllDownloader();
                result = "任务已暂停";
            } else if (cmds.startsWith("help")) {
                result += "服务器地址/test?restart-4@3 (表示从第五个频道的第四页重新开始抓取数据！)\n";
                result += "服务器地址/test?setmaxpagecount-500 (设置每个分类最大扫描页数为500！)\n";
                result += "服务器地址/test?quit (退出程序)\n";
                result += "服务器地址/test?stop (停止当前任务并清理缓冲区)\n";
                result += "服务器地址/test?pause （暂停当前任务）\n";
                result += "服务器地址/test?continue (重新执行因为超时而失败的任务！)\n";
                result += "服务器地址/test?help (打印帮助)\n";
            }
        } else {
            result = ConsoleSpider.getSpiderWorkStatus();
            if (result == null || result != null && result.length() == 0) {
                result = "抓取服务尚未启动";
            }
        }

        return result;
    }
}