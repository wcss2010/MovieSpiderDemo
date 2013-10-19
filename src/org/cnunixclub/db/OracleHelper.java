/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.db;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wcss
 */
public class OracleHelper 
{
    public static String oracleUrl = "";
    public static String users = "";
    public static String pwds = "";

    /**
     * 设置连接字符串
     */
    public static void setConnections(String type,String hostAndPort,String dbName, String dbUser, String dbPass) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        oracleUrl = "jdbc:oracle:thin:@" + hostAndPort + ":" + dbName;
        users = dbUser;
        pwds = dbPass;
    }

    /**
     * 返回数据库连接对象，连接失败则返回null
     *
     * @return Connection
     */
    public static Connection getConnections() {
        try {
            return DriverManager.getConnection(oracleUrl, users, pwds);
        } catch (SQLException ex) {
            return null;
        } catch (Exception ex) {
            return null;
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
    public static int ExecuteNoneQuerys(String cmdtext, Object[] parms)
            throws SQLException {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = getConnections();
            pstmt = conn.prepareStatement(cmdtext);
            prepareCommand(pstmt, parms);
            return pstmt.executeUpdate();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (pstmt != null) {
                pstmt.clearParameters();
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }

        }
        return 0;
    }

    /**
     * 返回查询结果集
     *
     * @param SQL语句
     * @param 附带参数
     * @return 返回结果，用ArrayList包装Object数组
     * @throws SQLException
     *
     * @example ArrayList list = mysqlhelper.ExecuteReader("Select * from
     * Documents",null); <br/>
     * for(int i = 0;i&lt;list.size();i++) { <br/> &nbsp;&nbsp;Object[] obs =
     * (Object[])list.get(i); <br/> &nbsp;&nbsp;for(int j =
     * 0;j&lt;obs.length;j++) { <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;out.print(obs[j].toString()); <br/>
     * &nbsp;&nbsp;} <br/> &nbsp;&nbsp;out.print("&lt;br/&gt;"); <br/>
     * } <br/>
     *
     */
    public static ArrayList ExecuteReaders(String cmdtext, Object[] parms)
            throws SQLException {
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getConnections();
            pstmt = conn.prepareStatement(cmdtext);

            prepareCommand(pstmt, parms);
            rs = pstmt.executeQuery();

            ArrayList al = new ArrayList();
            ResultSetMetaData rsmd = rs.getMetaData();
            int column = rsmd.getColumnCount();

            while (rs.next()) {
                Object[] ob = new Object[column];
                for (int i = 1; i <= column; i++) {
                    ob[i - 1] = rs.getObject(i);
                }
                al.add(ob);
            }
            return al;

        } catch (Exception ex) {
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return null;
    }

    /**
     * 返回第1行第1列数据，一般用来查询count值
     *
     * @param SQL语句
     * @param 带参数
     * @return 值
     * @throws SQLException
     */
    public static Object ExecuteScalars(String cmdtext, Object[] parms)
            throws SQLException {
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getConnections();

            pstmt = conn.prepareStatement(cmdtext);
            prepareCommand(pstmt, parms);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getObject(1);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return null;
    }

    private static void prepareCommand(PreparedStatement pstmt, Object[] parms)
            throws SQLException, UnsupportedEncodingException {
        if (parms != null && parms.length > 0) {
            for (int i = 1; i < parms.length + 1; i++) {
                Object item = parms[i - 1];
                String typeName = item.getClass().getSimpleName();
                if (typeName.equals("String")) {
                    pstmt.setString(i, item.toString());
                } else if (typeName.equals("Integer")) {
                    pstmt.setInt(i, Integer.parseInt(item.toString()));
                } else if (typeName.equals("Date")) {
                    pstmt.setDate(i, Date.valueOf(item.toString()));
                } else {
                    pstmt.setObject(i, item);
                }
            }
        }
    }
}