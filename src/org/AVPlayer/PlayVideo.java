package org.AVPlayer;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class PlayVideo implements Runnable{
    String folderPath;
    MixedPlayer player;
    static int FPS = 30;
    static int FULL_TIME = 540;
    static int width = 320;
    static int height = 180;
    public static volatile boolean playListener;


    public PlayVideo(String folderPath, MixedPlayer player) {
        this.folderPath = folderPath;
        this.player = player;
        this.playListener = true;
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
            for (int i = 0; i < FPS * FULL_TIME; i++) {
                    if (player.statusId == 1) {
                        startTime = i == 0 ? (System.nanoTime()) : startTime;
                        String imgPath = folderPath + "frame" + i + ".rgb";
                        BufferedImage nextImg = player.showIms(imgPath);
                        player.lbIm1.setIcon(new ImageIcon(nextImg));
                        player.videoProgress = (float) 1.0 * i / (FPS * FULL_TIME);
                        TimeUnit.MICROSECONDS.sleep(((i + 1) * 33333) - (System.nanoTime() / 1000 - startTime / 1000));
                    } else if (player.statusId == 0) {
                        i = i - 1;
                        Thread.sleep(1);
                    } else if (player.statusId == -1) {
                        System.exit(0);
                    }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
