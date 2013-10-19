/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wcss
 */
public class QvodUrlFilterHelper {

    /**
     *
     * @param qvodurl
     * @return
     */
    public static String getMovieStr(String qvodurl) {
        String url = qvodurl.replace("qvod://", "");
        return url.split("\\|")[2];
    }

    public static String filterHTTPURLFromMovieName(String sourceName) {
        if (sourceName.contains("[") && sourceName.contains("]")) {
            int startindex = sourceName.indexOf("[");
            int endindex = sourceName.indexOf("]") + 1;
            try {
                return sourceName.replace(sourceName.substring(startindex, endindex), "");
            } catch (Exception ex) {
                return sourceName;
            }
        } else {
            return sourceName;
        }
    }

    public static boolean isASCII(String str) {
        char tch;
        int tint;
        for (int i = 0; i < str.length(); i++) {
            tch = str.charAt(i);
            tint = (int) tch;
            if (tint > 255) {
                return false;
            }
        }
        return true;
    }

    public static String getMediaExtension(String mediafile) throws Exception {
        if (mediafile != null && mediafile.contains(".")) {
            String[] team = mediafile.split("\\.");
            if (team.length > 0) {
                return team[team.length - 1];
            } else {
                throw new Exception("media filename error!");
            }
        } else {
            throw new Exception("media filename error!");
        }
    }

    // GENERAL_PUNCTUATION 判断中文的“号  
    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号  
    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号  
    public static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static final boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    public static String filterErrorString(String text) throws Exception {
        char dot = '.';
        char downline = '_';
        char upline = '-';
        String source = text.toLowerCase();
        char[] checks = source.toCharArray();
        String movieExt = getMediaExtension(text);
        String destName = "";
        for (char c : checks) {
            Boolean ableuse = false;
            if (isASCII(new String(new char[]{c}))) {
                //asicc字符处理
                if ((int) c == (int) dot || (int) c == (int) downline || (int) c == (int) upline) {
                    ableuse = true;
                } else {
                    if ((int) c >= 48 && (int) c <= 57) {
                        ableuse = true;
                    } else if ((int) c >= 97 && (int) c <= 122) {
                        ableuse = true;
                    }
                }

            } else {
                //中文日文韩文字符处理
                if (isChinese(c)) {
                    ableuse = true;
                }
            }
            if (ableuse) {
                destName += c;
            }
        }
        String[] teamss = destName.split("\\.");
        if (teamss != null && (teamss.length <= 1 || (teamss.length >= 2 && teamss[0].length() <= 2))) {
            SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
            sdf.applyPattern("yyyy年MM月dd日_HH时mm分ss秒");
            String timeStr = sdf.format(new Date());
            destName = timeStr + "." + movieExt;
        }
        return destName;
    }

    public static String decodeMovieName(String sourceMovieName) throws Exception {
        String result = "";
        if (CharHelper.helper.isUtf8Url(sourceMovieName)) {
            result = CharHelper.helper.Utf8URLdecode(sourceMovieName);
        } else {
            try {
                result = URLDecoder.decode(sourceMovieName, "gb2312");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
                result = sourceMovieName;
            }
        }
        result = filterErrorString(filterHTTPURLFromMovieName(result));
        return result;
    }
}