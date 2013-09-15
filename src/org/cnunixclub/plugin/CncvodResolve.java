/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.plugin;

import java.util.ArrayList;
import org.cnunixclub.helper.MovieContentHelper;
import org.cnunixclub.helper.MoviePlayUrlHelper;
import org.cnunixclub.spider.Interface.model.VideoChannelInfo;
import org.cnunixclub.spider.Interface.model.VideoInfo;

/**
 * 笑笑影院影片资源解析类
 * @author wcss
 */
public class CncvodResolve extends org.cnunixclub.spider.Interface.IVideoSiteResolveAdapter {

    protected ArrayList<String> hotVideos = new ArrayList<String>();

    @Override
    public String getEncoding() {
        return "gbk";
    }

    @Override
    public String[] getSupportVideoSiteUrlList() {
        return new String[]{"www.cncvod.com"};
    }

    @Override
    public VideoChannelInfo[] getChannelList(String content) {
        int indexx = 0;
        ArrayList<String> team = MovieContentHelper.getMovieChannelPageUrlList(content);
        searchHotVideos(content);
        VideoChannelInfo[] result = new VideoChannelInfo[team.size()];

        for (String obj : team) {
            String[] splitTeam = obj.split(",");
            result[indexx] = new VideoChannelInfo(splitTeam[0], splitTeam[1]);
            indexx++;
        }
        return result;
    }

    @Override
    public String[] getChannelContentURLList(String content) {
        ArrayList<String> team = MovieContentHelper.getMovieContentPageUrlList(content);
        return JAppToolKit.JDataHelper.convertTo(team.toArray());
    }

    @Override
    public VideoInfo getVideoInfoObj(String content) {
        VideoInfo vi = new VideoInfo();
        vi.name = MovieContentHelper.getMovieName(content);
        vi.logo = MovieContentHelper.getMovieImageUrl(content);
        vi.summary = MovieContentHelper.getMovieDetailText(content);
        vi.playactor = MovieContentHelper.getMoviePlayActor(content, hotVideos);
        return vi;
    }

    @Override
    public String[] getVideoUrlList(String content, String type) {
        if (type == null || (type != null && type.contains("qvod"))) {
            return JAppToolKit.JDataHelper.convertTo(MoviePlayUrlHelper.getQvodUrlList(content).toArray());
        } else if (type != null && type.contains("page")) {
            return JAppToolKit.JDataHelper.convertTo(MovieContentHelper.getPlayPageUrlList(content).toArray());
        } else {
            return null;
        }
    }

    @Override
    public void setDBUrl(String url, String use, String pwd) {
    }

    @Override
    public void init() {
    }

    /**
     * 查找热门搜索视频
     *
     * @param data
     */
    public void searchHotVideos(String data) {
        String temp = MovieContentHelper.getMoviePlayActor(data, hotVideos);
        if (temp != null) {
            String[] team = temp.split(",");
            for (String ss : team) {
                if (ss != null && !ss.isEmpty()) {
                    hotVideos.add(ss);
                }
            }
        }
    }
   
    @Override
    public String getNextPageUrl(String content)
    {
        return MovieContentHelper.getMovieNextPageUrl(content);
    }

    @Override
    public Object resolve(String cmd, Object obj) {
        return null;
    }
}