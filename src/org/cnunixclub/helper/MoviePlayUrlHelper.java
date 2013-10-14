/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.helper;

import java.util.ArrayList;
import org.cnunixclub.spider.helper.RegularHelper;

/**
 * 影片播放地址分析(快播或百度影音)
 * @author wcss
 */
public class MoviePlayUrlHelper
{
    /**
     * 查询快播地址
     *
     * @param content
     * @return
     */
    public static ArrayList<String> getQvodUrlList(String content) {
        ArrayList<String> list = RegularHelper.getDataWithRegular("qvod:\\/\\/\\d{1,12}\\|[A-Za-z0-9]{40}\\|.*\\.[A-Za-z0-9]{1,10}\\|", content, "','");
        ArrayList<String> result = new ArrayList<String>();

        for (String v : list) {
            int index = v.indexOf("qvod://");
            v = v.replace("|$qvod", "|");
            if (index == 0) {
                result.add(v);
            } else {
                v = v.substring(index);
                result.add(v);
            }
        }
        return result;
    }
    
}