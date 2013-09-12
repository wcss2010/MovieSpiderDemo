/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.helper;

import Interface.IDownloadProgressEvent;
import Manager.DownloaderManager;
import java.io.File;
import java.util.Date;

/**
 *
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
     * @param url
     * @param component
     * @return
     * @throws Exception 
     */
    public static String downloadHTMLFile(String url,IDownloadProgressEvent component) throws Exception
    {
        return downloadFile("htmlTasks",url,component);
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
     * @param file
     * @return
     * @throws Exception 
     */
    public static String readAllTextFromFile(String file) throws Exception {
        if (new File(file).exists()) {
            String[] team = JAppToolKit.JDataHelper.readAllLines(file);
            String result = "";
            for (String s : team) {
                result += s;
            }

            return result;
        } else {
            return "";
        }
    }
}