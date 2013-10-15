/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.ui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author wcss
 */
public class MyHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {
        try {
            InputStream is = t.getRequestBody();
            String response = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf8\" /><title>最新大陆剧  第1页  在线观看和下载 笑笑影院</title></head>";
            response += ConsoleSpider.getSpiderWorkStatus().replace("\n", "<br>");
            response += "</html>";
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
}