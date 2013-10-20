/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static org.cnunixclub.db.OracleHelper.getConnections;

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
    public static Boolean existsClassName(String name) throws Exception {
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
    public static int getClassId(String name) throws Exception {
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
    public static int getMovieId(String name) throws Exception {
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
    private static Boolean addMovieInfo_old(String movieName, String actor, String storyLine, String stagePhoto, int classNameID, String status) throws Exception {
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
    public static Boolean addMovieUrl(int mid, String addrType, String url) throws Exception {
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
    public static int removeMovieUrl(int mid, String addrType) throws Exception {
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
    
    /**
     * 数据影片信息函数(使用存储过程)
     *
     * @param SQL语句
     * @param 语句带的参数
     * @return 操作影响行数
     * @throws SQLException
     *
     * @example Object[] parms = new Object[2];<br/> parms[0] = "标题"; <br/>
     * parms[1] = "内容";<br/> int val = mysqlhelper.ExecuteNoneQuery( "insert
     * into Documents(Title,Content) values (?,?)", parms);
     */
    public static Boolean addMovieInfo(String movieName, String actor, String storyLine, String stagePhoto, int classNameID, String status) throws Exception
    {
        CallableStatement pstmt = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            pstmt = conn.prepareCall("{call MOVIE_PACKAGE.PROC_ADD_MOVIE(?,?,?,?,?,?,?)}"); 
            pstmt.setString(1, movieName);
            pstmt.setString(2, actor);
            pstmt.setString(3, storyLine);
            pstmt.setString(4, stagePhoto);
            pstmt.setInt(5, classNameID);
            pstmt.setString(6, status);
            pstmt.registerOutParameter(7, java.sql.Types.INTEGER); 
            return pstmt.execute();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new Exception("\n error:" + ex.toString());
        } finally {
            if (pstmt != null) {
                pstmt.clearParameters();
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }

        }        
    }
}