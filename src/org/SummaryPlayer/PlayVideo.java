package org.SummaryPlayer;

import java.io.File;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName PlayVideo
 * @Description: Video player
 * @Author Group
 * @Date 2021-04-26
 * @Version 1.0
 **/
public class PlayVideo implements Runnable{
    String folderPath;
    GeneratedPlayer player;
    int totFrame = 0;
    static int FPS = 30;
    static int FULL_TIME = 540;
    static int width = 320;
    static int height = 180;
    public static volatile boolean playListener;


    public PlayVideo(String folderPath, GeneratedPlayer player, int totFrame) {
        this.folderPath = folderPath;
        this.player = player;
        this.playListener = true;
        this.totFrame = totFrame;
    }

    @Override
    public void run() {
        long startTime = 0;
        try {
            for (;;) {
                Thread.sleep(1);
                if (player.audioProgress != 0) {
                    break;
                }
            }
            int iCon = 0;
            int iConBU = 0;
            for (int i = 0; i < totFrame; i++) {
                if (player.statusId == 1) {
                    startTime = (i==0||iConBU!=iCon) ? (System.nanoTime()) : startTime;
                    String imgPath = new File(folderPath, "frame" + i + ".rgb").getPath();
                    BufferedImage nextImg = player.showIms(imgPath);
                    player.lbIm1.setIcon(new ImageIcon(nextImg));
                    player.text.setText((i)/30+"/"+(totFrame)/30);
                    player.videoProgress = (float) 1.0 * i / (FPS * FULL_TIME);
                    iConBU = iCon;
                    TimeUnit.MICROSECONDS.sleep(((i-iCon+1)*33333)-(System.nanoTime()/1000-startTime/1000));
                } else if (player.statusId == 0) {
                    iCon = i;
                    i = i - 1;
                    Thread.sleep(1);
                } else if (player.statusId == -1) {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
