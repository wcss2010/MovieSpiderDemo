/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.db;

import java.sql.SQLException;

/**
 *
 * @author wcss
 */
public class MovieDBWithOracleHelper
{
    /**
     * 是否存在
     *
     * @param name
     * @return
     * @throws SQLException
     */
    public static Boolean existsClassName(String name) throws SQLException {
        Object obj = DBHelper.ExecuteScalar("select count(*) from DataDictionary where ParentNode = 2 and DicKey = '" + name + "'", null);
        if (obj != null) {
            if (Integer.parseInt(obj.toString()) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
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
    public static int getClassId(String name) throws SQLException {
        Object obj = DBHelper.ExecuteScalar("select DID from DataDictionary where ParentNode = 2 and DicKey = '" + name + "'", null);
        if (obj != null) {
            return Integer.parseInt(obj.toString());
        } else {
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
    public static int getMovieId(String name) throws SQLException {
        Object obj = DBHelper.ExecuteScalar("select MID from Movies where MovieName = '" + name + "'", null);
        if (obj != null) {
            return Integer.parseInt(obj.toString());
        } else {
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
    public static Boolean addMovieInfo(String movieName, String actor, String storyLine, String stagePhoto, int classNameID, String status) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into Movies(MID,MovieName,actor,StoryLine,StagePhoto,ClassNameID,Status) values (seq_movies.nextval,'" + movieName + "','" + actor + "','" + storyLine + "','" + stagePhoto + "'," + classNameID + ",'" + status + "')");
        int count = DBHelper.ExecuteNoneQuery(sb.toString(), null);
        return count > 0 ? true : false;
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
    public static Boolean addMovieUrl(int mid, String addrType, String url) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into MovieUrl(PID,MID,AddrType,Url) values (seq_movieurl.nextval," + mid + ",'" + addrType + "','" + url + "')");
        int count = DBHelper.ExecuteNoneQuery(sb.toString(), null);
        return count > 0 ? true : false;
    }

    /**
     * 移出已添加的URL
     *
     * @param mid
     * @param addrType
     * @return
     * @throws SQLException
     */
    public static int removeMovieUrl(int mid, String addrType) throws SQLException {
        int result = DBHelper.ExecuteNoneQuery("delete from MovieUrl where MID = " + mid + " and AddrType = '" + addrType + "'", null);
        return result;
    }

    /**
     * 查询链接数量
     * @param mid
     * @param addrType
     * @return 
     */
    public static int getMovieurlCount(int mid, String addrType) {
        try {
            Object result = DBHelper.ExecuteScalar("select count(*) from MovieUrl where MID = " + mid + " and AddrType = '" + addrType + "'", null);
            return Integer.parseInt(result + "");
        } catch (Exception ex) {
            return 0;
        }
    }
}