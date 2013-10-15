/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.controller;

import Interface.DownloadStatus;
import Interface.IDownloaderEvent;
import Interface.IDownloaderPlugin;
import Manager.DownloaderManager;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cnunixclub.db.MovieDBHelper;
import org.cnunixclub.spider.Interface.IVideoSiteResolveAdapter;
import org.cnunixclub.spider.Interface.IVideoSiteResolveStatus;
import org.cnunixclub.spider.Interface.model.VideoChannelInfo;
import org.cnunixclub.spider.Interface.model.VideoInfo;
import org.cnunixclub.spider.helper.HTMLDownloader;

/**
 * 数据抓取控制器
 *
 * @author wcss
 */
public class SpiderController implements IDownloaderEvent {

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
     * 内容缓冲队列
     */
    public Queue<String> queueContentBufferList = new LinkedList<String>();
    /**
     * 解析状态事件
     */
    public IVideoSiteResolveStatus resolveStatusEvent = null;
    /**
     * 当前解析出来的内容
     */
    public String resolveContent = "";
    public int printLogStatusValue = 10;
    public int finishSpiderTaskStatusValue = 100;
    public Boolean onlyResoveFirstPlayPage = true;
    public int downloadedMovieCount = 0;

    /**
     * 投递解析状态事件
     *
     * @param stateCode
     * @param txt
     */
    public void processResolveStatus(int stateCode, Object obj) {
        if (this.resolveStatusEvent != null) {
            this.resolveStatusEvent.processResolveStatus(this.currentResolveAdapter, stateCode, obj);
        }
    }

    /**
     * 打印日志
     *
     * @param txt
     */
    public void printLogText(String txt) {
        processResolveStatus(printLogStatusValue, txt);
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
                HTMLDownloader.downloadFile("homepage", new String[]{url}, this);

                printLogText("任务开始,地址：" + url);
            }
        }
    }

    /**
     * 开始一个下载任务
     *
     * @param type
     * @param subUrl
     * @throws Exception
     */
    protected void downloadTask(String type, String[] subUrls) throws Exception {
        if (this.currentResolveAdapter != null && this.currentResolveAdapter.currentSiteUrl != null) {
            String[] destUrls = new String[subUrls.length];
            int fileIndex = 0;
            for (String s : subUrls) {
                s = s.trim();
                if (!s.startsWith("/")) {
                    s = "/" + s;
                }
                destUrls[fileIndex] = "http://" + this.currentResolveAdapter.currentSiteUrl + s;
                printLogText("正在下载文件，地址：" + destUrls[fileIndex]);
                fileIndex++;
            }

            HTMLDownloader.downloadFile(type, destUrls, this);
        } else {
            throw new Exception("Adapter is Empty!");
        }
    }

    protected void downloadAllContentPage() throws Exception {
        //获取下一页的内容
        if (this.queueContentList.size() > 0) {
            String[] taskUrls = JAppToolKit.JDataHelper.convertTo(this.queueContentList.toArray());
            this.queueContentList.clear();
            downloadTask("content", taskUrls);
        }
    }

    protected void downloadNextChannelPage() throws Exception {
        if (this.queueChannelList.size() > 0) {
            currentVideoChannel = this.queueChannelList.poll();
            this.currentChannelPagingBufferList.add(currentVideoChannel.url);
            currentVideoChannelID = MovieDBHelper.getClassId(this.currentVideoChannel.name);
            downloadTask("channel", new String[]{currentVideoChannel.url});
        }
    }

    @Override
    public void onReportStatus(IDownloaderPlugin idp, int code, String msg) {
        if (code == DownloadStatus.finishDownload) {
            onReportFinish(idp);
        } else if (code == DownloadStatus.downloadError) {
            printLogText(msg);
        }
    }

    /**
     * 读取缓冲区中的内容
     *
     * @param plugin
     * @param fileIndex
     * @return
     */
    public String getHtmlContent(IDownloaderPlugin plugin, int fileIndex) {
        String content = null;
        try {
            content = HTMLDownloader.readAllTextFromFile(new File(plugin.getBufferFileUrl(fileIndex)), this.currentResolveAdapter.getEncoding());
        } catch (Exception ex) {
            Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return content;
    }

    /**
     * 获得影片内容队列数据
     *
     * @return
     * @throws Exception
     */
    public String getNextMovieContentQueueTxt() throws Exception {
        return HTMLDownloader.readAllTextFromFile(new File(this.queueContentBufferList.poll()), this.currentResolveAdapter.getEncoding());
    }

    public void onReportFinish(IDownloaderPlugin sender) {
        if (sender != null && this.currentResolveAdapter != null) {
            printLogText("数据下载完成！地址：" + sender.urlList.get(sender.currentUrlIndex) + ",任务号：" + sender.downloaderID);

            if (sender.downloaderID.startsWith("homepage")) {
                try {
                    VideoChannelInfo[] channels = this.currentResolveAdapter.getChannelList(getHtmlContent(sender, 0));
                    for (VideoChannelInfo vci : channels) {
                        this.channelList.add(vci);
                        this.queueChannelList.offer(vci);
                    }
                    this.saveChannel(channels);
                    this.downloadNextChannelPage();
                } catch (Exception ex) {
                    Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (sender.downloaderID.startsWith("channel")) {
                String nextPage = this.currentResolveAdapter.getNextPageUrl(getHtmlContent(sender, 0));
                printLogText("找到下一页地址：" + nextPage);
                if (this.currentChannelPagingBufferList.contains(nextPage) || this.currentChannelPagingBufferList.size() > maxPageCount) {
                    //搜索完成                    
                    this.currentChannelPagingBufferList.clear();

                    try {
                        System.out.println("搜索分页完毕！开始下载内容页，总数：" + this.queueContentList.size());
                        this.downloadAllContentPage();
                    } catch (Exception ex) {
                        Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    //继续搜索
                    this.currentChannelPagingBufferList.add(nextPage);

                    String[] list = this.currentResolveAdapter.getChannelContentURLList(getHtmlContent(sender, 0));
                    for (String s : list) {
                        if (!this.queueContentList.contains(s)) {
                            printLogText("内容页入队！地址：" + s + "\n");
                            this.queueContentList.offer(s);
                        }
                    }

                    try {
                        downloadTask("channel", new String[]{nextPage});
                    } catch (Exception ex) {
                        Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (sender.downloaderID.startsWith("content")) {

                //将下载完成的缓冲文件路径加载到队列中
                for (int k = 0; k < sender.urlList.size(); k++) {
                    this.queueContentBufferList.offer(sender.getBufferFileUrl(k));
                }

                System.out.println("分类" + this.currentVideoChannel.name + "内的影片内容页面下载完成！总数：" + sender.urlList.size());

                this.getNextMovieContent();

            } else if (sender.downloaderID.startsWith("play")) {
                ArrayList<String> qvodUrlLists = new ArrayList<String>();
                String[] qvods = new String[0];
                try {
                    this.printLogText("正在分析影片" + this.currentVideoInfo.name + "的播放页面，数量：" + sender.urlList.size());

                    for (int k = 0; k < sender.urlList.size(); k++) {
                        qvods = this.currentResolveAdapter.getVideoUrlList(HTMLDownloader.readAllTextFromFile(new File(sender.getBufferFileUrl(k)), this.currentResolveAdapter.getEncoding()), "qvod");

                        if (qvods != null && qvods.length > 0) {
                            for (String s : qvods) {
                                if (!qvodUrlLists.contains(s)) {
                                    qvodUrlLists.add(s);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                }

                printLogText("qvod链接数：" + qvodUrlLists.size());

                //保存视频信息
                this.saveVideo(this.currentVideoInfo, JAppToolKit.JDataHelper.convertTo(qvodUrlLists.toArray()));

                if (this.queueContentBufferList.size() > 0) {
                    getNextMovieContent();
                } else {
                    if (queueChannelList.size() > 0) {
                        try {
                            this.downloadNextChannelPage();
                        } catch (Exception ex) {
                            Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        this.finishSpiderTask();
                    }
                }
            }

            try {
                DownloaderManager.manager.stopDownloader(sender.downloaderID);
            } catch (Exception ex) {
                Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                DownloaderManager.manager.clearDownloader(sender.downloaderID);
            } catch (Exception ex) {
                Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveVideo(VideoInfo videoInfo, String[] qvods)
    {
        downloadedMovieCount++;
        
        if (videoInfo != null && qvods != null) {
            int mid = 0;
            try {
                mid = MovieDBHelper.getMovieId(videoInfo.name);
            } catch (SQLException ex) {
                Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (mid <= 0) {
                printLogText("保存影片！片名：" + videoInfo.name + ",qvod链接数：" + qvods.length);
                try {
                    MovieDBHelper.addMovieInfo(videoInfo.name, videoInfo.playactor, videoInfo.summary, videoInfo.logo, currentVideoChannelID, "1");
                    mid = MovieDBHelper.getMovieId(videoInfo.name);
                    for (String s : qvods) {
                        MovieDBHelper.addMovieUrl(mid, "qvod", s);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                printLogText("该影片已存在！片名：" + videoInfo.name);
            }
        }
    }

    private void saveChannel(VideoChannelInfo[] channels) {
    }

    @Override
    public void onReportProgress(IDownloaderPlugin idp, int fileIndex, long currentLength, long totalLength) {
        this.printLogText("任务名：" + idp.downloaderID + "，序号：" + fileIndex + ",已下载：" + currentLength + "字节");
    }

    /**
     * 检查当前工作状态
     * @return 
     */
    public String getWorkeStatus()
    {
        String result = "";
        if (this.currentVideoChannel != null)
        {
           result += "当前正在下载的频道：" + this.currentVideoChannel.name + "\n";
        }
        if (this.queueChannelList != null)
        {
           result += "当前剩余频道数：" + this.queueChannelList.size() + "\n";
        }
        
        if (this.currentVideoInfo != null)
        {
           result += "当前正在下载的影片：" + this.currentVideoInfo.name + "\n";
        }
        
        result += "已搜索到的影片数：" + this.queueContentList.size() + "\n";
        result += "已下载的影片内容页数量：" + this.queueContentBufferList.size() + "\n";
        result += "已下载影片数：" + downloadedMovieCount + "\n";
        return result;
    }
    
    /**
     * 下载影片数据
     */
    private void getNextMovieContent() {
        try {
            //获取当前页内容
            resolveContent = getNextMovieContentQueueTxt();
            currentVideoInfo = this.currentResolveAdapter.getVideoInfoObj(resolveContent);
        } catch (Exception ex) {
            Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (currentVideoInfo != null) {
            //视频信息获取完成
            printLogText("找到片名：" + currentVideoInfo.name);
            String[] ss = this.currentResolveAdapter.getVideoUrlList(resolveContent, "page");
            if (ss != null && ss.length > 0) {
                if (onlyResoveFirstPlayPage) {
                    try {
                        this.downloadTask("play", new String[]{ss[0]});
                    } catch (Exception ex) {
                        Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        this.downloadTask("play", ss);
                    } catch (Exception ex) {
                        Logger.getLogger(SpiderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private void finishSpiderTask() {
        this.processResolveStatus(finishSpiderTaskStatusValue, null);
    }
}