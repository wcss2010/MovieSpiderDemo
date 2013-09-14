/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.helper;

import Interface.IDownloadProgressEvent;
import Manager.DownloaderManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 *  数据下载工具类
 * @author wcss
 */
public class HTMLDownloader {

    /**
     * 获取缓冲目录
     *
     * @return
     */
    public static String getBufferDir() {
        String bufferDir = JAppToolKit.JRunHelper.getUserHomeDirPath() + "/.htmlspiderbufferdir";
        try {
            new File(bufferDir).mkdirs();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bufferDir;
    }

    /**
     * 下载网页任务
     *
     * @param url
     * @param component
     * @return
     * @throws Exception
     */
    public static String downloadHTMLFile(String url, IDownloadProgressEvent component) throws Exception {
        return downloadFile("htmlTasks", url, component);
    }

    /**
     * 下载文件任务
     *
     * @param url
     * @param form
     * @return
     * @throws Exception
     */
    public static String downloadFile(String taskType, String url, IDownloadProgressEvent component) throws Exception {
        if (taskType == null || (taskType != null && taskType.isEmpty()) || url == null || (url != null && url.isEmpty()) || component == null) {
            throw new Exception("下载参数错误!");
        } else {
            //DownloaderManager.manager.clearAllDownloader();
            String taskName = taskType + "-" + new Date().getTime();
            DownloaderManager.manager.createDownloader(taskName, url, "http", getBufferDir(), 0, 0, component);
            DownloaderManager.manager.startDownloader(taskName);
            return taskName;
        }
    }

    /**
     * 读取所有字符串从文件中
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String readAllTextFromFile(File file, String encoding) throws Exception {
        if (file != null && encoding != null && file.exists()) {
            long filelength = file.length();
            byte[] filecontent = new byte[Integer.parseInt(filelength + "")];
            try {
                FileInputStream in = new FileInputStream(file);
                in.read(filecontent);
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return new String(filecontent, encoding);
            } catch (UnsupportedEncodingException e) {
                System.err.println("The OS does not support " + encoding);
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }
    
    /**
     * 使用gbk编码读取一个文本文件
     * @param filePath
     * @return
     * @throws Exception 
     */
    public static String readAllTextFromFileWithGBK(String filePath) throws Exception
    {
        return readAllTextFromFile(new File(filePath),"gbk");
    }
    
        /**
     * 使用utf8编码读取一个文本文件
     * @param filePath
     * @return
     * @throws Exception 
     */
    public static String readAllTextFromFileWithUTF8(String filePath) throws Exception
    {
        return readAllTextFromFile(new File(filePath),"utf8");
    }
}