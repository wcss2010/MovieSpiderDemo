/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.helper;

import java.io.File;
import java.util.ArrayList;
import org.cnunixclub.spider.helper.RegularHelper;

/**
 * 影片内容分析类
 *
 * @author xyuser
 */
public class MovieContentHelper {

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

        return resultStr.replace("<title>", "").replace("</title>", "").replace("高清", "").replace("笑笑影院", "").replace("-", "").replace("在线播放", "");
    }

    /**
     * 获取图片链接
     *
     * @param content
     * @return
     */
    public static String getMovieImageUrl(String content) {
        ArrayList<String> result = RegularHelper.getDataWithRegular("<img width=\"[0-9]{1,5}\" height=\"[0-9]{1,5}\" src=\".*\" alt=\".*\" />", content, "");
        String resultStr = "";
        for (String s : result) {
            if (s.contains("img")) {
                resultStr = s;
                break;
            }
        }

        ArrayList<String> subResult = RegularHelper.getDataWithRegular("src=[\"|'].*[\"|']", resultStr, "");
        for (String s : subResult) {
            if (s.contains("src")) {
                resultStr = s;
                break;
            }
        }
        String[] temp = resultStr.split(" ");
        if (temp.length >= 1) {
            resultStr = temp[0];
            return resultStr.replace("src=", "").replace("\"", "").replace("'", "");

        } else {
            return resultStr;
        }
    }

    /**
     * 获取剧集分类页面中的剧集链接
     *
     * @param content
     * @return
     */
    public static ArrayList<String> getMovieContentPageUrlList(String content) {
        ArrayList<String> temp = RegularHelper.getAllURLInLink(content);
        ArrayList<String> resultList = new ArrayList<String>();
        for (String s : temp) {
            if (s.contains("content/?")) {
                s = s.replace("href=", "").replace("\"", "").replace("'", "");
                if (!resultList.contains(s)) {
                    resultList.add(s);
                }
            }
        }
        return resultList;
    }

    /**
     * 获取剧集分类页面中下一页按钮的链接
     *
     * @param content
     * @return
     */
    public static String getMovieNextPageUrl(String content) {
        ArrayList<String> result = RegularHelper.getAllURLAndTitleInLink(content);
        String resultStr = "";
        for (String s : result) {
            if (s.contains("下一页")) {
                resultStr = s;
                break;
            }
        }
        return resultStr.replace("\"", "").replace("'", "").replace("<a class=", "").replace("next", "").replace("href=", "").replace(">下一页</a>", "");
    }

    /**
     * 获取频道链接列表
     *
     * @return
     */
    public static ArrayList<String> getMovieChannelPageUrlList(String content) {
        ArrayList<String> team = RegularHelper.getAllURLAndTitleInLink(content);
        ArrayList<String> resultList = new ArrayList<String>();
        for (String s : team) {
            s = s.replace("\"", "").replace("'", "").replace("<a href=", "").replace("><span>", "").replace("</span></a>", "").replace("target=_blank", "").replace("</a>", "").replace(">", "").replace(" ", "").replace("html", "html,").trim();
            if (s.contains("channel/?") && !s.contains("更多") && !s.contains("em")) {
                if (!resultList.contains(s)) {
                    resultList.add(s);
                }
            }
        }
        return resultList;
    }

    /**
     * 获取演员信息
     *
     * @return
     */
    public static String getMoviePlayActor(String content, ArrayList<String> filterList) {
        ArrayList<String> result = RegularHelper.getAllURLAndTitleInLink(content);
        ArrayList<String> playactorList = new ArrayList<String>();
        String resultStr = "";
        for (String s : result) {
            if (s.contains("search.asp?searchword=") && !s.contains("><")) {
                if (!playactorList.contains(s)) {
                    playactorList.add(s);
                }
            }
        }

        for (String s : playactorList) {
            s = s.replace("'", "").replace("\"", "").replace("</a>", "").replace("<a href=", "").replace("search.asp?searchword=", "").replace("/", "");
            s = s.substring(s.indexOf(">") + 1);
            if (filterList != null) {
                if (!filterList.contains(s)) {
                    resultStr += s + ",";
                }
            } else {
                resultStr += s + ",";
            }
        }
        return resultStr;
    }
}