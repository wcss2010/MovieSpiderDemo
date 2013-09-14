/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.helper;

import java.io.File;
import java.util.ArrayList;

/**
 * 影片内容分析类
 *
 * @author xyuser
 */
public class MovieContentHelper
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
            if (s.contains("/play")) {
                returns.add(s.replace("href=", "").replace("'", ""));
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
    public static String getMovieDetailText(String content) {
        ArrayList<String> result = RegularHelper.getDataWithRegular("<div class=\"detail-desc-cnt\">(.)*/div>", content, "");
        String resultStr = "";
        for (String s : result) {
            if (s.contains("detail-desc-cnt")) {
                resultStr = s;
                break;
            }
        }
        if (resultStr != null && !resultStr.isEmpty()) {
            return resultStr.replace("<div class=\"detail-desc-cnt\">", "").replace("</div>", "");
        } else {
            return resultStr;
        }
    }
}