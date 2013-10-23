/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.controller;

/**
 * Spider跳转控制参数类
 * @author wcss
 */
public class SpiderJumpParamEntry 
{
    public int jumpChannelCount;
    public int jumpPagingCount;
    public int currentPagingIndex;
    public SpiderJumpParamEntry(int chCount,int paCount)
    {
        this.jumpChannelCount = chCount;
        this.jumpPagingCount = paCount;
        this.currentPagingIndex = 0;
    }
}
