/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.db;

import java.sql.SQLException;
import org.cnunixclub.ui.ConsoleSpider;

/**
 *
 * @author wcss
 */
public class MovieDBHelper {

    /**
     * 是否存在
     *
     * @param name
     * @return
     * @throws SQLException
     */
    public static Boolean existsClassName(String names) {
        try {
            if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql"))) {
                return MovieDBWithMySqlHelper.existsClassName(names);
            } else {
                return MovieDBWithOracleHelper.existsClassName(names);
            }
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 获取分类ID
     *
     * @param name
     * @return
     * @throws SQLException
     */
    public static int getClassId(String name1) {
        try {
            if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql"))) {
                return MovieDBWithMySqlHelper.getClassId(name1);
            } else {
                return MovieDBWithOracleHelper.getClassId(name1);
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * 获取影片ID
     *
     * @param name
     * @return
     * @throws SQLException
     */
    public static int getMovieId(String name) {
        try {
            if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql"))) {
                return MovieDBWithMySqlHelper.getMovieId(name);
            } else {
                return MovieDBWithOracleHelper.getMovieId(name);
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * 增加影片信息
     *
     * @param movieName
     * @param actor
     * @param storyLine
     * @param stagePhoto
     * @param classNameID
     * @param status
     * @return
     */
    public static Boolean addMovieInfo(String movieName, String actor, String storyLine, String stagePhoto, int classNameID, String status) throws Exception {
        if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql"))) {
            return MovieDBWithMySqlHelper.addMovieInfo(movieName, actor, storyLine, stagePhoto, classNameID, status);
        } else {
            return MovieDBWithOracleHelper.addMovieInfo(movieName, actor, storyLine, stagePhoto, classNameID, status);
        }
    }

    /**
     * 增加影片链接
     *
     * @param mid
     * @param addrType
     * @param url
     * @return
     * @throws SQLException
     */
    public static Boolean addMovieUrl(int mid, String addrType, String url) throws Exception {
        if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql"))) {
            return MovieDBWithMySqlHelper.addMovieUrl(mid, addrType, url);
        } else {
            return MovieDBWithOracleHelper.addMovieUrl(mid, addrType, url);
        }
    }

    /**
     * 移出已添加的URL
     *
     * @param mid
     * @param addrType
     * @return
     * @throws SQLException
     */
    public static int removeMovieUrl(int mid, String addrType) throws Exception {
        if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql"))) {
            return MovieDBWithMySqlHelper.removeMovieUrl(mid, addrType);
        } else {
            return MovieDBWithOracleHelper.removeMovieUrl(mid, addrType);
        }
    }

    /**
     * 查询链接数量
     *
     * @param mid
     * @param addrType
     * @return
     */
    public static int getMovieurlCount(int mid, String addrType) {
        if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql"))) {
            return MovieDBWithMySqlHelper.getMovieurlCount(mid, addrType);
        } else {
            return MovieDBWithOracleHelper.getMovieurlCount(mid, addrType);
        }
    }
}