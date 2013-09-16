/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.controller;

import Interface.AVideoDownloader;
import Interface.IDownloadProgressEvent;
import Manager.DownloaderManager;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cnunixclub.db.MovieDBHelper;
import org.cnunixclub.spider.helper.HTMLDownloader;
import org.cnunixclub.spider.Interface.IVideoSiteResolveAdapter;
import org.cnunixclub.spider.Interface.IVideoSiteResolveStatus;
import org.cnunixclub.spider.Interface.model.VideoChannelInfo;
import org.cnunixclub.spider.Interface.model.VideoInfo;

/**
 * 数据抓取控制器
 *
 * @author wcss
 */
public class SpiderController implements IDownloadProgressEvent {

    public long maxPageCount = 1000000;
    
    /**
     * 当前解析类
     */
    public IVideoSiteResolveAdapter currentResolveAdapter = null;
    /**
     * 频道列表
     */
    public ArrayList<VideoChannelInfo> channelList = new ArrayList<VideoChannelInfo>();
    /**
     * 当前频道对象
     */
    public VideoChannelInfo currentVideoChannel = null;
    
    /**
     * 当前频道记录ID
     */
    public int currentVideoChannelID = 0;
    
    /**
     * 当前视频说明
     */
    public VideoInfo currentVideoInfo = null;
    /**
     * 当前频道分页列表
     */
    public ArrayList<String> currentChannelPagingBufferList = new ArrayList<String>();
    /**
     * 频道队列
     */
    public Queue<VideoChannelInfo> queueChannelList = new LinkedList<VideoChannelInfo>();
    /*
     * 内容队列 
     */
    public Queue<String> queueContentList = new LinkedList<String>();
    
    /**
     * 解析状态事件
     */
    public IVideoSiteResolveStatus resolveStatusEvent = null;
    
    /**
     * 投递解析状态事件
     * @param stateCode
     * @param txt 
     */
    public void processResolveStatus(int stateCode,Object obj)
    {
        if (this.resolveStatusEvent != null)
        {
            this.resolveStatusEvent.processResolveStatus(this.currentResolveAdapter, stateCode, obj);
        }
    }
    
    /**
     * 打印日志
     * @param txt 
     */
    public void printLogText(String txt)
    {
       processResolveStatus(-1,txt);
    }

    /**
     * 开启
     *
     * @param adapter
     * @param url
     */
    public void start(IVideoSiteResolveAdapter adapter, String url, Boolean isReloadAll) throws Exception {
        if (adapter != null && url != null && !url.isEmpty()) {
            this.currentResolveAdapter = adapter;
            String[] support = this.currentResolveAdapter.getSupportVideoSiteUrlList();
            this.currentResolveAdapter.currentSiteUrl = url;

            Boolean exists = false;
            for (String s : support) {
                if (s.equals(url)) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                if (!url.startsWith("http://")) {
                    url = "http://" + url;
                }
                HTMLDownloader.downloadFile("homepage", url, this);
                
                printLogText("任务开始,地址：" + url);
            }
        }
    }

    @Override
    public void onReportProgress(AVideoDownloader avd, long l, long l1) {
    }

    @Override
    public void onReportError(AVideoDownloader avd, String cmd, String error)
    {
        printLogText("错误：" + error);
    }

    /**
     * 开始一个下载任务
     *
     * @param type
     * @param subUrl
     * @throws Exception
     */
    protected void downloadTask(String type, String subUrl) throws Exception {
        if (this.currentResolveAdapter != null && this.currentResolveAdapter.currentSiteUrl != null) {
            printLogText("正在下载文件，地址：" + subUrl);
//            DownloaderManager.manager.stopAllDownloader();
//            DownloaderManager.manager.clearAllDownloader();
            subUrl = subUrl.trim();
            if (!subUrl.startsWith("/"))
            {
               subUrl = "/" + subUrl;
            }
            
            HTMLDownloader.downloadFile(type, "http://" + this.currentResolveAdapter.currentSiteUrl + subUrl, this);
        } else {
            throw new Exception("Adapter is Empty!");
        }
    }

    protected void downloadNextContentPage() throws Exception {
        //获取下一页的内容
        if (this.queueContentList.size() > 0) {
            String taskUrl = this.queueContentList.poll();
            downloadTask("content", taskUrl);
        }
    }

    protected void downloadNextChannelPage() throws Exception {
        if (this.queueChannelList.size() > 0) {
            currentVideoChannel = this.queueChannelList.poll();
            this.currentChannelPagingBufferList.add(currentVideoChannel.url);
            currentVideoChannelID = MovieDBHelper.getClassId(this.currentVideoChannel.name);
            downloadTask("channel", currentVideoChannel.url);
        }
    }

    @Override
    public void onReportFinish(AVideoDownloader avd) {
        if (avd != null && this.currentResolveAdapter != null)
        {
            printLogText("数据下载完成！地址：" + avd.videoUrl + ",任务号：" + avd.downloaderID);
            
            String content = null;
            try {
                content = HTMLDownloader.readAllTextFromFile(new File(avd.getVideoBufferUrl()), this.currentResolveAdapter.getEncoding());
            } catch (Exception ex) {
                Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (content != null && avd.downloaderID.startsWith("homepage")) {
                try {
                    VideoChannelInfo[] channels = this.currentResolveAdapter.getChannelList(content);
                    for (VideoChannelInfo vci : channels) {
                        this.channelList.add(vci);
                        this.queueChannelList.offer(vci);
                    }                    
                    this.saveChannel(channels);
                    this.downloadNextChannelPage();
                } catch (Exception ex) {
                    Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (content != null && avd.downloaderID.startsWith("channel")) {
                String nextPage = this.currentResolveAdapter.getNextPageUrl(content);
                printLogText("找到下一页地址：" + nextPage);
                if (this.currentChannelPagingBufferList.contains(nextPage) || this.currentChannelPagingBufferList.size() > maxPageCount) {
                    //搜索完成                    
                    this.currentChannelPagingBufferList.clear();
                    
                    try {
                        this.downloadNextContentPage();
                    } catch (Exception ex) {
                        Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    //继续搜索
                    this.currentChannelPagingBufferList.add(nextPage);

                    String[] list = this.currentResolveAdapter.getChannelContentURLList(content);
                    for (String s : list)
                    {
                        if (!this.queueContentList.contains(s))
                        {
                           printLogText("内容页入队！地址：" + s + "\n");
                           this.queueContentList.offer(s);
                        }
                    }

                    try {
                        downloadTask("channel", nextPage);
                    } catch (Exception ex) {
                        Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (content != null && avd.downloaderID.startsWith("content")) {
                //获取当前页内容
                currentVideoInfo = this.currentResolveAdapter.getVideoInfoObj(content);
                if (currentVideoInfo != null) {
                    //视频信息获取完成
                    printLogText("找到片名：" + currentVideoInfo.name);
                    String[] ss = this.currentResolveAdapter.getVideoUrlList(content, "page");
                    if (ss != null && ss.length > 0) {
                        try {
                            this.downloadTask("play", ss[0]);
                        } catch (Exception ex) {
                            Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            this.downloadNextContentPage();
                        } catch (Exception ex) {
                            Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    try {
                        this.downloadNextContentPage();
                    } catch (Exception ex) {
                        Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }else if (content != null && avd.downloaderID.startsWith("play"))
            {
                String[] qvods = this.currentResolveAdapter.getVideoUrlList(content, "qvod");
                
                printLogText("qvod链接数：" + qvods.length);
                
                //保存视频信息
                this.saveVideo(this.currentVideoInfo,qvods);
                
                if (this.queueContentList.size() > 0)
                {
                    try {
                        this.downloadNextContentPage();
                    } catch (Exception ex) {
                        Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else
                {
                    try {
                        this.downloadNextChannelPage();
                    } catch (Exception ex) {
                        Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            try {
                DownloaderManager.manager.stopDownloader(avd.downloaderID);
            } catch (Exception ex) {
                Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                DownloaderManager.manager.clearDownloader(avd.downloaderID);
            } catch (Exception ex) {
                Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void onReportStatus(AVideoDownloader avd, String string) {
    }

    private void saveVideo(VideoInfo videoInfo, String[] qvods)
    {
        if (videoInfo != null && qvods != null)
        {
            int mid = 0;
            try {
                mid = MovieDBHelper.getMovieId(videoInfo.name);
            } catch (SQLException ex) {
                Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (mid <= 0)
            {
                printLogText("保存影片！片名：" + videoInfo.name + ",qvod链接数：" + qvods.length);
                try 
                {                    
                    MovieDBHelper.addMovieInfo(videoInfo.name, videoInfo.playactor, videoInfo.summary,videoInfo.logo, currentVideoChannelID, "1");
                    mid = MovieDBHelper.getMovieId(videoInfo.name);
                    for(String s : qvods)
                    {
                        MovieDBHelper.addMovieUrl(mid, "qvod", s);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void saveChannel(VideoChannelInfo[] channels)
    {
        
    }
}