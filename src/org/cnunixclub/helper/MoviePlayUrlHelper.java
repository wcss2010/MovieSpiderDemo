/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.helper;

import java.util.ArrayList;
import org.cnunixclub.spider.helper.RegularHelper;

/**
 * 影片播放地址分析(快播或百度影音)
 *
 * @author wcss
 */
public class MoviePlayUrlHelper {

    /**
     * 查询快播地址
     *
     * @param content
     * @return
     */
    public static ArrayList<String> getQvodUrlList(String content) {
        ArrayList<String> list = RegularHelper.getDataWithRegular("qvod:\\/\\/\\d{1,12}\\|[A-Za-z0-9]{40}\\|.*\\.[A-Za-z0-9]{1,10}\\|", content, "','");
        ArrayList<String> result = new ArrayList<String>();

        ArrayList<String> filteredUrl = new ArrayList<String>();
        for (String s : list) {
            if (s.contains("'qvod'")) {
                String[] cnts = s.split("'qvod'");
                for (String t : cnts) {
                    filteredUrl.add(t);
                }
            } else {
                filteredUrl.add(s);
            }
        }

        for (String v : filteredUrl) {
            try {
                if (v.length() > 40) {
                    int index = v.indexOf("qvod://");

                    v = v.replace("|$qvod", "|").replace("]]", "").replace("',[", "");
                    if (index == 0) {
                        result.add(v);
                    } else {
                        v = v.substring(index);
                        result.add(v);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}