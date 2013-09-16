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
public class MovieDBHelper
{
    /**
     * 是否存在
     * @param name
     * @return
     * @throws SQLException 
     */
    public static Boolean existsClassName(String name) throws SQLException
    {
        Object obj = MySqlHelper.ExecuteScalar("select count(*) from DataDictionary where ParentNode = 2 and DicKey = '" + name + "'", null);
        if (obj != null)
        {
            if (Integer.parseInt(obj.toString()) > 0)
            {
                return true;    
            }else
            {
                return false;
            }
        }else
        {
            return false;
        }
    }
    
    /**
     * 获取分类ID
     * @param name
     * @return
     * @throws SQLException 
     */
    public static int getClassId(String name) throws SQLException
    {
        Object obj = MySqlHelper.ExecuteScalar("select DID from DataDictionary where ParentNode = 2 and DicKey = '" + name + "'", null);
        if (obj != null)
        {
            return Integer.parseInt(obj.toString());
        }else
        {
            return 0;
        }
    }
    
    /**
     * 获取影片ID
     * @param name
     * @return
     * @throws SQLException 
     */
    public static int getMovieId(String name) throws SQLException
    {
        Object obj = MySqlHelper.ExecuteScalar("select MID from Movies where MovieName = '" + name + "'", null);
        if (obj != null)
        {
            return Integer.parseInt(obj.toString());
        }else
        {
            return 0;
        }
    }
    
    
    /**
     * 增加影片信息
     * @param movieName
     * @param actor
     * @param storyLine
     * @param stagePhoto
     * @param classNameID
     * @param status
     * @return 
     */
    public static Boolean addMovieInfo(String movieName,String actor,String storyLine,String stagePhoto,int classNameID,String status) throws SQLException
    {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into Movies(MovieName,actor,StoryLine,StagePhoto,ClassNameID,Status) values ('" + movieName + "','" + actor + "','" + storyLine + "','" + stagePhoto + "'," + classNameID + ",'" + status +"')");        
        int count = MySqlHelper.ExecuteNoneQuery(sb.toString(), null);
        return count > 0?true:false;
    }

    /**
     * 增加影片链接
     * @param mid
     * @param addrType
     * @param url
     * @return
     * @throws SQLException 
     */
    public static Boolean addMovieUrl(int mid,String addrType,String url) throws SQLException
    {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into MovieUrl(MID,AddrType,Url) values (" + mid + ",'" + addrType + "','" + url + "')");        
        int count = MySqlHelper.ExecuteNoneQuery(sb.toString(), null);
        return count > 0?true:false;
    }
}