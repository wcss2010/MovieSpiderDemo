/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.helper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则处理工具类
 * @author xyuser
 */
public class RegularHelper
{
    /**
     * 通过正则分析一个字符串并返回分析结果
     *
     * @param regular
     * @param content
     * @param splitStr
     * @return
     */
    public static ArrayList<String> getDataWithRegular(String regular, String content, String splitStr) {
        Pattern p = Pattern.compile(regular);
        Matcher m = p.matcher(content);
        ArrayList<String> list = new ArrayList<String>();
        while (m.find()) {
            if (splitStr == null || (splitStr != null && splitStr.isEmpty())) {
                list.add(m.group());
            } else {
                String[] team = m.group().split(splitStr);
                for (String v : team) {
                    list.add(v);
                }
            }
        }
        return list;
    }
    
    /**
     * 获取所有以http://开头的url
     * @param contents
     * @return 
     */
    public static ArrayList<String> getAllHttpUrlList(String contents)
    {
        return getDataWithRegular("[a-zA-z]+://[^\\s]*",contents,"");
    }
    
    /**
     * 获取所有链接地址
     * @param contents
     * @return 
     */
    public static ArrayList<String> getAllURLInLink(String contents)
    {
        return getDataWithRegular("href *= *['\"]*(\\S+)[\"']",contents,"");
    }

    /**
     * 获取所有链接地址及标题
     * @param contents
     * @return 
     */
    public static ArrayList<String> getAllURLAndTitleInLink(String contents)
    {
        return getDataWithRegular("\\<a href *= *['\"]*(\\S+)[\"'].*\\>(.[^\\<]*)?\\</a>",contents,"");
    }

    /**
     * 获取所有HTML标签
     * @param contents
     * @return 
     */
    public static ArrayList<String> getAllHtmlFlag(String contents)
    {
        return getDataWithRegular("<(\\S*?)[^>]*>.*?</\\1>|<.*? /><a href *= *['\"]*(\\S+)[\"'].*\\>(.[^\\<]*)?\\</a>",contents,"");
    }
}