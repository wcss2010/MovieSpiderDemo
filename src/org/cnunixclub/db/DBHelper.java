/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.db;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.*;
import javax.sql.*;
import org.cnunixclub.ui.ConsoleSpider;

/**
 * <strong>数据库操作控制类</strong>
 * <p>
 *    包装了常用的操作方法。类似于SqlHelper在java中的实现。
 * </p>
 *
 * @author birdshover
 */
public abstract class DBHelper {

    /**
     * 设置连接字符串
     */
    public static void setConnection(String type,String hostAndPort,String dbName, String dbUser, String dbPass) {
       if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) ||(ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql")))
       {
           MySqlHelper.setConnections(type, hostAndPort, dbName, dbUser, dbPass);
       }else
       {
           OracleHelper.setConnections(type, hostAndPort, dbName, dbUser, dbPass);
       }
    }

    /**
     * 返回数据库连接对象，连接失败则返回null
     *
     * @return Connection
     */
    public static Connection getConnection() {
       if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) ||(ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql")))
       {
           return MySqlHelper.getConnections();
       }else
       {
           return OracleHelper.getConnections();
       }
    }

    /**
     * 无结果查询，适用于更新和插入
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
    public static int ExecuteNoneQuery(String cmdtext, Object[] parms)
            throws Exception {
       if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) ||(ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql")))
       {
           return MySqlHelper.ExecuteNoneQuerys(cmdtext, parms);
       }else
       {
           return OracleHelper.ExecuteNoneQuerys(cmdtext, parms);
       }
    }

    /**
     * 返回第1行第1列数据，一般用来查询count值
     *
     * @param SQL语句
     * @param 带参数
     * @return 值
     * @throws SQLException
     */
    public static Object ExecuteScalar(String cmdtext, Object[] parms) throws Exception{
       if (ConsoleSpider.dbType == null || (ConsoleSpider.dbType != null && ConsoleSpider.dbType.isEmpty()) ||(ConsoleSpider.dbType != null && ConsoleSpider.dbType.equals("mysql")))
       {
           return MySqlHelper.ExecuteScalars(cmdtext, parms);
       }else
       {
           return OracleHelper.ExecuteScalars(cmdtext, parms);
       }
    }
}