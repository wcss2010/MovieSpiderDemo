/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cnunixclub.ui;

import Interface.AVideoDownloader;
import Interface.IDownloadProgressEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cnunixclub.helper.HTMLDownloader;
import org.cnunixclub.helper.MovieContentHelperWithDD13;
import org.cnunixclub.helper.MovieContentHelper;
import org.cnunixclub.helper.MoviePlayUrlHelper;
import org.cnunixclub.helper.RegularHelper;

/**
 *
 * @author wcss
 */
public class MovieSpiderDemoFrame extends javax.swing.JFrame implements IDownloadProgressEvent {

    /**
     * Creates new form MovieSpiderDemoFrame
     */
    public MovieSpiderDemoFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtConentUrl = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPlayUrl = new javax.swing.JTextField();
        btnGetContent = new javax.swing.JButton();
        btnGetPlay = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtResult = new javax.swing.JEditorPane();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("文泉驿微米黑", 1, 12)); // NOI18N
        jLabel1.setText("剧集内容页：");

        txtConentUrl.setText("http://www.cncvod.com/content/?64063.html");

        jLabel2.setFont(new java.awt.Font("文泉驿微米黑", 1, 12)); // NOI18N
        jLabel2.setText("剧情播放页：");

        txtPlayUrl.setText("http://www.cncvod.com/play/?64063-0-0.html");

        btnGetContent.setText("分析内容页");
        btnGetContent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetContentActionPerformed(evt);
            }
        });

        btnGetPlay.setText("分析播放页");
        btnGetPlay.setEnabled(false);
        btnGetPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetPlayActionPerformed(evt);
            }
        });

        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setViewportView(txtResult);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtPlayUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGetPlay))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtConentUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGetContent)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtConentUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGetContent))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPlayUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGetPlay))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGetPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetPlayActionPerformed
        try {
            // TODO add your handling code here:
            this.lblStatus.setText("正在下载页面，请稍后......");
            HTMLDownloader.downloadFile("qvodfinder", this.txtPlayUrl.getText(), this);
            this.txtResult.setText("");
        } catch (Exception ex) {
            Logger.getLogger(MovieSpiderDemoFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGetPlayActionPerformed

    private void btnGetContentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetContentActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            this.lblStatus.setText("正在下载页面，请稍后......");
            HTMLDownloader.downloadFile("contentresolve", this.txtConentUrl.getText(), this);
            this.txtResult.setText("");
        } catch (Exception ex) {
            Logger.getLogger(MovieSpiderDemoFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGetContentActionPerformed

    private void printLogText(String cnt)
    {
        String oldContent = this.txtResult.getText();
        oldContent += cnt + "\n";
        this.txtResult.setText(oldContent);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MovieSpiderDemoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MovieSpiderDemoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MovieSpiderDemoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MovieSpiderDemoFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MovieSpiderDemoFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGetContent;
    private javax.swing.JButton btnGetPlay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextField txtConentUrl;
    private javax.swing.JTextField txtPlayUrl;
    private javax.swing.JEditorPane txtResult;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onReportProgress(AVideoDownloader avd, long l, long l1) {
    }

    @Override
    public void onReportError(AVideoDownloader avd, String string, String string1) {
        this.lblStatus.setText("页面下载出错！");
    }

    @Override
    public void onReportFinish(AVideoDownloader avd) {
        this.lblStatus.setText("页面下载完成，正在分析......");
        try {
            String content = HTMLDownloader.readAllTextFromFileWithGBK(avd.getVideoBufferUrl());
//            ArrayList<String> team = MovieContentHelper.getMovieChannelPageUrlList(content);
//            for(String s : team)
//            {
//               printLogText(s);
//            }
//            ArrayList<String> hotVideos = new ArrayList<String>();
//            hotVideos.add("大上海");
//            hotVideos.add("一路向西");
//            printLogText(MovieContentHelper.getMoviePlayActor(content,hotVideos));
            if (avd.downloaderID.startsWith("qvodfinder")) {
                ArrayList<String> team = MoviePlayUrlHelper.getQvodUrlList(content);
                for (String s : team) {
                    printLogText(s);
                }

                printLogText("页面分析完成，共找到" + team.size() + "个快播地址");
            } else if (avd.downloaderID.startsWith("contentresolve")) {
                            ArrayList<String> hotVideos = new ArrayList<String>();
            hotVideos.add("大上海");
            hotVideos.add("一路向西");
                String showStr = "";
                printLogText("片名：" + MovieContentHelper.getMovieName(content));
                printLogText("演员：" + MovieContentHelper.getMoviePlayActor(content, hotVideos));
                printLogText("图片：" + MovieContentHelper.getMovieImageUrl(content));
                printLogText("介绍：" + MovieContentHelper.getMovieDetailText(content));
                printLogText("Qvod地址：");
                ArrayList<String> team = MovieContentHelper.getPlayPageUrlList(content);
                if (team.size() > 0)
                {
                   String currentPlayPage = "http://www.cncvod.com/" + team.get(0);
                   HTMLDownloader.downloadFile("qvodfinder", currentPlayPage, this);
                }

                //this.lblStatus.setText("页面分析完成，共找到" + team.size() + "个记录");
            }
        } catch (Exception ex) {
            Logger.getLogger(MovieSpiderDemoFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onReportStatus(AVideoDownloader avd, String string) 
    {
        
    }
}