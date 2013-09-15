/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.controller;

import Interface.AVideoDownloader;
import Interface.IDownloadProgressEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cnunixclub.spider.helper.HTMLDownloader;
import org.cnunixclub.spider.Interface.IVideoSiteResolveAdapter;
import org.cnunixclub.spider.Interface.model.VideoChannelInfo;

/**
 * 数据抓取控制器
 *
 * @author wcss
 */
public class SpliderController implements IDownloadProgressEvent {

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
            }
        }
    }

    @Override
    public void onReportProgress(AVideoDownloader avd, long l, long l1) {
    }

    @Override
    public void onReportError(AVideoDownloader avd, String string, String string1) {
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
            HTMLDownloader.downloadFile(type, "http://" + this.currentResolveAdapter.currentSiteUrl + "/" + subUrl, this);
        } else {
            throw new Exception("Adapter is Empty!");
        }
    }

    @Override
    public void onReportFinish(AVideoDownloader avd) {
        if (avd != null && this.currentResolveAdapter != null) {
            String content = null;
            try {
                content = HTMLDownloader.readAllTextFromFile(new File(avd.getVideoBufferUrl()), this.currentResolveAdapter.getEncoding());
            } catch (Exception ex) {
                Logger.getLogger(SpliderController.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (content != null && avd.downloaderID.startsWith("homepage")) {
                try {
                    VideoChannelInfo[] channels = this.currentResolveAdapter.getChannelList(content);
                    for (VideoChannelInfo vci : channels) {
                        this.channelList.add(vci);
                        this.queueChannelList.offer(vci);
                    }

                    if (this.queueChannelList.size() > 0) {
                        currentVideoChannel = this.queueChannelList.element();
                        this.currentChannelPagingBufferList.add(currentVideoChannel.url);
                        downloadTask("channel", currentVideoChannel.url);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SpliderController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (content != null && avd.downloaderID.startsWith("channel")) {
                String nextPage = this.currentResolveAdapter.getNextPageUrl(content);
                if (this.currentChannelPagingBufferList.contains(nextPage)) {
                    //搜索完成
                    this.currentChannelPagingBufferList.clear();
                    if (this.queueContentList.size() > 0) {
                        String taskUrl = this.queueContentList.element();
                        try {
                            downloadTask("content", taskUrl);
                        } catch (Exception ex) {
                            Logger.getLogger(SpliderController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    //继续搜索
                    this.currentChannelPagingBufferList.add(nextPage);

                    String[] list = this.currentResolveAdapter.getChannelContentURLList(content);
                    for (String s : list) {
                        this.queueContentList.offer(s);
                    }

                    try {
                        downloadTask("channel", nextPage);
                    } catch (Exception ex) {
                        Logger.getLogger(SpliderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (content != null && avd.downloaderID.startsWith("content")) {
                if (this.queueContentList.size() > 0) {
                    String taskUrl = this.queueContentList.element();
                    try {
                        downloadTask("content", taskUrl);
                    } catch (Exception ex) {
                        Logger.getLogger(SpliderController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @Override
    public void onReportStatus(AVideoDownloader avd, String string) {
    }
}
