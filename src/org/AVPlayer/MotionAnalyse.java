package org.AVPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @ClassName MotionAnalyse
 * @Description: Get motion weight
 * @Author Group
 * @Date 2021-04-26
 * @Version 1.0
 **/
public class MotionAnalyse {
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
    ArrayList<Integer> coreFrame = new ArrayList<>();

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

    public double[] getMotionArray(ArrayList<Integer> breakPoints, String path) {
        double[] breakScores = new double[breakPoints.size()-1];
        ArrayList<Integer> resultPoints = new ArrayList<>();

        for (int i = 0; i < breakPoints.size()-1; i++) {
            System.out.println("Processing break "+i+" out of "+breakPoints.size());
            int a = breakPoints.get(i)+1;
            double breakScore = 0;
            int breakBlock = 0;
            double frameScoreBefore = 0;
            for (int j = a; j <= breakPoints.get(i+1); j++) {
                if (j != breakPoints.get(i) && (j-breakPoints.get(i))%5==0) {
                    double frameScore = 0;
                    int frameBlock = 0;
                    generateMat(path+"frame"+a+".rgb", path+"frame"+j+".rgb");
                    for (int ii = 0; ii < height/10; ii++) {
                        for (int jj = 0; jj < width/10; jj++) {
                            breakScore += Math.abs(preAvg[ii][jj] - postAvg[ii][jj]);
                            frameScore += Math.abs(preAvg[ii][jj] - postAvg[ii][jj]);
                            breakBlock++;
                            frameBlock++;
                        }
                    }
                    if (frameScoreBefore != 0 && (1.0*frameScore/frameBlock-frameScoreBefore) > 20) {
                        System.out.println(a+"-"+j+": "+(1.0*frameScore/frameBlock-frameScoreBefore));
//                        if (havePair(resultPoints, breakPoints.get(i), breakPoints.get(i+1)))  {
//                            resultPoints.add(breakPoints.get(i));
//                            resultPoints.add(breakPoints.get(i+1));
//                        }
                        coreFrame.add(a);
                        coreFrame.add(j);
                    }
                    frameScoreBefore = 1.0*frameScore/frameBlock;
                    a = j;
                }
            }
            breakScores[i] = 1.0*breakScore/breakBlock;
        }
        double max = findMax(breakScores);
        for (int i = 0; i < breakScores.length; i++) {
//            if (breakScores[i] > 5 && havePair(resultPoints, breakPoints.get(i), breakPoints.get(i+1)))  {
//                resultPoints.add(breakPoints.get(i));
//                resultPoints.add(breakPoints.get(i+1));
//                System.out.println(breakPoints.get(i)+"-"+breakPoints.get(i+1)+": "+breakScores[i]);
//            }
            breakScores[i] = breakScores[i] / max;
        }
        resultPoints.sort(Comparator.naturalOrder());
        return breakScores;
    }

    public static double findMax(double[] array) {
        double max = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }


    public static boolean havePair(ArrayList<Integer> array, int value1, int value2) {
        if (array.contains(value1) && array.contains(value2)) {
            for (int i = 0; i < array.size(); i+=2) {
                if (array.get(i) == value1 && array.get(i+1) == value2) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
//        String rootPath = "D:\\MyMainFolder\\MSUSC\\CSCI576\\project\\dateset\\";
//        String pathRGB = rootPath + "frames_rgb\\meridian\\";
//        MotionAnalyse motionAnalyse = new MotionAnalyse();
//        ArrayList<Integer> breakPoints = new ArrayList<>();
//        //        videoSegmentation.getBreakPoints(path);
//        for (int i = 0; i < breaks.length; i++) {
//            breakPoints.add(breaks[i]);
//        }
//        motionAnalyse.getMotionMat(breakPoints, pathRGB);
    }

}