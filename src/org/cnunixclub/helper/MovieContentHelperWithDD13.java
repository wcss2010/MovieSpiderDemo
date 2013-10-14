/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.helper;

import java.util.ArrayList;
import org.cnunixclub.spider.helper.RegularHelper;

/**
 * 网址：http://www.dd13.tv/gc/
 * @author wcss
 */
public class MovieContentHelperWithDD13 
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
    
        /**
     * 获取影片详细介绍
     *
     * @param content
     * @return
     */
    public static String getMovieName(String content) {
        ArrayList<String> result = RegularHelper.getDataWithRegular("<title>(.)*/title>", content, "");
        String resultStr = "";
        for (String s : result) {
            if (s.contains("title")) {
                resultStr = s;
                break;
            }
        }
//        String[] temp = resultStr.split("</a>");
//        if (temp.length >= 3)
//        {        
//            resultStr = temp[2];
//            return resultStr.replace("</div>", "");
//
//        }else
//        {
           return resultStr;
        //}
    }
}
