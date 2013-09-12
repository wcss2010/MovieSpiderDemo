/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.helper;

import java.util.ArrayList;

/**
 * 网址：http://www.dd13.tv/gc/
 * @author wcss
 */
public class MovieConentHelperWithDD13 
{
    /**
     * 获取影片播放页面地址
     *
     * @param content
     * @return
     */
    public static ArrayList<String> getPlayPageUrlList(String content) {
        ArrayList<String> result = RegularHelper.getAllURLInLink(content);
        ArrayList<String> returns = new ArrayList<String>();
        for (String s : result) {
            if (s.contains("play_")) {
                returns.add(s.replace("href=", "").replace("'", "").replace("\"", ""));
            }
        }
        result.clear();
        return returns;
    }    
}
