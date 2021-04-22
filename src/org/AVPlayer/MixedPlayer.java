package org.AVPlayer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MixedPlayer {
    JFrame frame;
    JLabel lbIm1;
    JButton terminateBtn = new JButton("TERMINATE");
    JButton pauseBtn = new JButton("PAUSE");
    static int width = 320;
    static int height = 180;
    int statusId = 1; //1: play, 0: pause, -1: cease
    float videoProgress = 0;
    int audioProgress = 0;
    static MixedPlayer player;


    class ButtonAction implements ActionListener {
        private int actionId;

        public ButtonAction(int id) {
            actionId = id;
        }
        @Override
        public void actionPerformed(ActionEvent event) {
            if (actionId == 1) {
                statusId = statusId==1?0:statusId==0?1:null;
                System.out.println(statusId);
            } else if (actionId == 2) {
                statusId = -1;
                System.out.println(statusId);
            }
        }
    }


    public void init(String[] inputs){
        BufferedImage img = showIms(inputs[0]);
        // Use labels to display the images
        frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);

        lbIm1 = new JLabel(new ImageIcon(img));


        ButtonAction pause = new ButtonAction(1);
        ButtonAction terminate = new ButtonAction(2);
        pauseBtn.addActionListener(pause);
        terminateBtn.addActionListener(terminate);
        pauseBtn.setFocusPainted(false);
        pauseBtn.setForeground(new Color(255,255,255));
        pauseBtn.setBackground(new Color(59, 89, 182));
        pauseBtn.setFont(new Font("Courier", Font.BOLD, 20));
        pauseBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        terminateBtn.setFocusPainted(false);
        terminateBtn.setForeground(new Color(255,255,255));
        terminateBtn.setBackground(new Color(59, 89, 182));
        terminateBtn.setFont(new Font("Courier", Font.BOLD, 20));
        terminateBtn.setBorder(BorderFactory.createRaisedBevelBorder());

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        frame.getContentPane().add(lbIm1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
//        frame.getContentPane().add(pauseBtn, c);
        c.gridx = 1;
        c.gridy = 1;
        frame.getContentPane().add(terminateBtn, c);

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public BufferedImage showIms(String imgPath){
            // Read in the specified image
            BufferedImage imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            try
            {
                int frameLength = width*height*3;

                File file = new File(imgPath);
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(0);

                long len = frameLength;
                byte[] bytes = new byte[(int) len];
                raf.read(bytes);

                int ind = 0;
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        byte r = bytes[ind];
                        byte g = bytes[ind+height*width];
                        byte b = bytes[ind+height*width*2];

                        int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                        imgOne.setRGB(x,y,pix);
                        ind++;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return imgOne;
    }

    public static void main(String[] args) throws InterruptedException {
        player = new MixedPlayer();

        String rootpath = "D:\\MyMainFolder\\MSUSC\\CSCI576\\project\\dateset\\";
        String audioPath = rootpath + "audio\\meridian.wav";
        String videoPath = rootpath + "frames_rgb\\meridian\\";
        String[] input = {videoPath+"frame0.rgb", videoPath};

        player.init(input);
        Thread audio= new Thread(new PlayAudio(audioPath, player));
        Thread video = new Thread(new PlayVideo(videoPath, player));
        audio.start();
        video.start();
    }
}
