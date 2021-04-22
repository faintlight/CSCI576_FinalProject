package org.AVPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class MotionAnalyse {
//    ArrayList<Integer> breakMat = new ArrayList<>();
    ArrayList<Double> motionMat = new ArrayList<>();
    static int FPS = 30;
    static int FULL_TIME = 540;
    static int width = 320;
    static int height = 180;
    static int BLOCK_SIZE = 10;
    double preImgR[] = new double[height*width];
    double preImgG[] = new double[height*width];
    double preImgB[] = new double[height*width];
    double postImgR[] = new double[height*width];
    double postImgG[] = new double[height*width];
    double postImgB[] = new double[height*width];
    double preAvg[][] = new double[(height/10)][(width/10)];
    double postAvg[][] = new double[(height/10)][(width/10)];
    static int breaks[] = {0,31,156,277,394,470,528,605,693,892,1083,1357,1460,1650,1757,2161,2282,2411,2583,2621,2689,2997,3152,3259,3320,3573,3638,4002,4120,4236,4369,4970,5051,5268,5579,5735,5943,6139,7155,7214,7803,8164,8842,10096,10505,10835,10918,11219,11872,12041,12289,12459,13137,16199};

    private void getBlockAvg(int indexI, int indexJ){
        double preR = 0.0;
        double preG = 0.0;
        double preB = 0.0;
        double postR = 0.0;
        double postG = 0.0;
        double postB = 0.0;
        for (int i = 0; i < BLOCK_SIZE; i++) {
            for (int j = 0; j < BLOCK_SIZE; j++) {
                preR += preImgR[(indexI*BLOCK_SIZE+i)*width+(indexJ*BLOCK_SIZE+j)];
                preG += preImgG[(indexI*BLOCK_SIZE+i)*width+(indexJ*BLOCK_SIZE+j)];
                preB += preImgB[(indexI*BLOCK_SIZE+i)*width+(indexJ*BLOCK_SIZE+j)];
                postR += postImgR[(indexI*BLOCK_SIZE+i)*width+(indexJ*BLOCK_SIZE+j)];
                postG += postImgG[(indexI*BLOCK_SIZE+i)*width+(indexJ*BLOCK_SIZE+j)];
                postB += postImgB[(indexI*BLOCK_SIZE+i)*width+(indexJ*BLOCK_SIZE+j)];
            }
        }
        preAvg[indexI][indexJ] = (1.0*(preR/(BLOCK_SIZE*BLOCK_SIZE)) + 1.0*(preG/(BLOCK_SIZE*BLOCK_SIZE)) + 1.0*(preB/(BLOCK_SIZE*BLOCK_SIZE)))/3;
        postAvg[indexI][indexJ] = (1.0*(postR/(BLOCK_SIZE*BLOCK_SIZE)) + 1.0*(postG/(BLOCK_SIZE*BLOCK_SIZE)) + 1.0*(postB/(BLOCK_SIZE*BLOCK_SIZE)))/3;
    }

    public void generateAvgMat() {
        for (int i = 0; i < height/10; i++) {
            for (int j = 0; j < width/10; j++) {
                getBlockAvg(i, j);
            }
        }
    }

    public void generateMat(String imgPath1, String imgPath2){
        try
        {
            int frameLength = width*height*3;
            File file1 = new File(imgPath1);
            File file2 = new File(imgPath2);
            RandomAccessFile raf1 = new RandomAccessFile(file1, "r");
            RandomAccessFile raf2 = new RandomAccessFile(file2, "r");
            raf1.seek(0);
            raf2.seek(0);
            long len = frameLength;
            byte[] bytes1 = new byte[(int) len];
            byte[] bytes2 = new byte[(int) len];
            raf1.read(bytes1);
            raf2.read(bytes2);
            int ind = 0;
            for(int y = 0; y < height; y++){
                for(int x = 0; x < width; x++){
                    byte a = 0;
                    byte r1 = bytes1[ind];
                    byte g1 = bytes1[ind+height*width];
                    byte b1 = bytes1[ind+height*width*2];
                    byte r2 = bytes2[ind];
                    byte g2 = bytes2[ind+height*width];
                    byte b2 = bytes2[ind+height*width*2];
                    preImgR[ind] = r1<0?(r1+256):r1;
                    preImgG[ind] = g1<0?(g1+256):g1;
                    preImgB[ind] = b1<0?(b1+256):b1;
                    postImgR[ind] = r2<0?(r2+256):r2;
                    postImgG[ind] = g2<0?(g2+256):g2;
                    postImgB[ind] = b2<0?(b2+256):b2;
                    ind++;
                }
            }
            generateAvgMat();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMotionMat(VideoSegmentation vs, String path) {
        double[] breakScores = new double[vs.breakPoints.size()-1];
        for (int i = 0; i < vs.breakPoints.size()-1; i++) {
            int a = vs.breakPoints.get(i);
            double breakScore = 0;
            for (int j = a; j < vs.breakPoints.get(i+1); j++) {
                if (j != vs.breakPoints.get(i) && (j-vs.breakPoints.get(i))%5==0) {
                    generateMat(path+"frame"+a+".rgb", path+"frame"+i+".rgb");
                    for (int ii = 0; ii < height/10; ii++) {
                        for (int jj = 0; jj < width/10; jj++) {
                            breakScore += Math.abs(preAvg[ii][jj] - postAvg[ii][jj]);
                        }
                    }
                    a = i;
                }
            }
            breakScores[i] = 1.0*breakScore/(vs.breakPoints.get(i+1)-vs.breakPoints.get(i));
        }
        for (int i = 0; i < breakScores.length; i++) {
            System.out.println(breakScores[i]);
        }
    }

    public static void main(String[] args) {
        String rootPath = "D:\\MyMainFolder\\MSUSC\\CSCI576\\project\\dateset\\";
        String pathRGB = rootPath + "frames_rgb\\meridian\\";
        MotionAnalyse motionAnalyse = new MotionAnalyse();
        VideoSegmentation videoSegmentation = new VideoSegmentation();
        //        videoSegmentation.getBreakPoints(path);
        for (int i = 0; i < breaks.length; i++) {
            videoSegmentation.breakPoints.add(breaks[i]);
        }
        motionAnalyse.getMotionMat(videoSegmentation, pathRGB);
    }

}