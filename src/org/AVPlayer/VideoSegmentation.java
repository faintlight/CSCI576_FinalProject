package org.AVPlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
/**
 * @ClassName VideoSegmentation
 * @Description: Make segment breaks
 * @Author Group
 * @Date 2021-04-26
 * @Version 1.0
 **/
public class VideoSegmentation {
    double preImgR[] = new double[height*width];
    double preImgG[] = new double[height*width];
    double preImgB[] = new double[height*width];
    double postImgR[] = new double[height*width];
    double postImgG[] = new double[height*width];
    double postImgB[] = new double[height*width];
    double preHisto[][][] = new double[16][16][16];
    double postHisto[][][] = new double[16][16][16];
    static int FPS = 30;
    static int FULL_TIME = 540;
    static int width = 320;
    static int height = 180;
    static double THRESHOLD1 = 140;
    static double THRESHOLD2 = 1.001;
    static double diffOrigin[] = new double[FPS*FULL_TIME-1];
    static double diffHisto[] = new double[FPS*FULL_TIME-1];
    ArrayList<Integer> breakPoints = new ArrayList<>();


    public VideoSegmentation() {
        System.load("D:\\MyMainFolder\\MSUSC\\CSCI576\\project\\code\\opencv\\x64\\opencv_java451.dll");
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
            clearHisto();
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
                    preHisto[((int)preImgR[ind])/16][((int)preImgG[ind])/16][((int)preImgB[ind])/16]++;
                    postHisto[((int)postImgR[ind])/16][((int)postImgG[ind])/16][((int)postImgB[ind])/16]++;
                    ind++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void generateCannyMat(String imgPath1, String imgPath2){
        File file1 = new File(imgPath1);
        File file2 = new File(imgPath2);
        BufferedImage bi1 = null;
        BufferedImage bi2 = null;
        try {
            bi1 = ImageIO.read(file1);
            bi2 = ImageIO.read(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int ind = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel1 = bi1.getRGB(j, i);
                int pixel2 = bi2.getRGB(j, i);
                preImgR[ind] = (pixel1 & 0xff0000) >> 16;
                preImgG[ind] = (pixel1 & 0xff00) >> 8;
                preImgB[ind] = (pixel1 & 0xff);
                postImgR[ind] = (pixel2 & 0xff0000) >> 16;
                postImgG[ind] = (pixel2 & 0xff00) >> 8;
                postImgB[ind] = (pixel2 & 0xff);
                ind++;
            }
        }
    }

    public double analyseFrames() {
        double diffR = 0;
        double diffG = 0;
        double diffB = 0;
        int pixels = width * height;
        for (int i = 0; i < pixels-1; i++) {
            diffR += Math.abs(preImgR[i] - postImgR[i]);
            diffG += Math.abs(preImgG[i] - postImgG[i]);
            diffB += Math.abs(preImgB[i] - postImgB[i]);
        }
        diffR = 1.0 * diffR / pixels;
        diffG = 1.0 * diffG / pixels;
        diffB = 1.0 * diffB / pixels;
        return diffR + diffG + diffB;
    }

    public double analyseHisto() {
        double diffHisto = 0;
        int pixels = width * height;
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    diffHisto += Math.abs(preHisto[x][y][z] - postHisto[x][y][z]);
                }
            }
        }
        return 1.0*diffHisto/pixels;
    }

    public void getFramesDiff(String originPath) {
        for (int i = 0; i < FPS * FULL_TIME-1; i++) {
            String imgPath1 = originPath + "frame" + i + ".rgb";
            String imgPath2 = originPath + "frame" + (i+1) + ".rgb";
            generateMat(imgPath1, imgPath2);
            diffOrigin[i] = analyseFrames();
            diffHisto[i] = analyseHisto();
            if (i % 1000 == 0) {
                System.out.println("Segment finished: " + i);
            }
//            imgPath1 = cannyPath + "frame" + i + ".jpg";
//            imgPath2 = cannyPath + "frame" + (i+1) + ".jpg";
//            generateCannyMat(imgPath1, imgPath2);
//            diffCanny[i] = analyseFrames();
        }
    }

//    public void cannyEdgeDetection(String inputPath, String outputPath) {
//        for (int i = 0; i < FPS * FULL_TIME; i++) {
//            if (i % 1000 == 0) {
//                System.out.println("Canny finished: " + i);
//            }
//            Mat m1 = Imgcodecs.imread(inputPath+"frame"+i+".jpg");
//            Mat s1 = new Mat();
//            Imgproc.Canny(m1,s1,50,150,3,true);
//            Imgcodecs.imwrite(outputPath+"frame"+i+".jpg", s1);
//        }
//    }

    public void getBreakPoints(String path) {
        getFramesDiff(path);
        breakPoints.add(0);
        for (int i = 0; i < FPS * FULL_TIME-1; i++) {
            if (diffOrigin[i] > THRESHOLD1 || diffHisto[i] > THRESHOLD2) {
                breakPoints.add(i);
            }
//            System.out.println("FRAME:"+i+" ORIGIN:"+diffOrigin[i]+" HISTO:"+diffHisto[i]);
        }
        breakPoints.add(16199);
    }
    public void clearHisto() {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    preHisto[x][y][z] = 0;
                    postHisto[x][y][z] = 0;
                }
            }
        }

    }
//    public static void main(String[] args) {
//        String rootPath = "D:\\MyMainFolder\\MSUSC\\CSCI576\\project\\dateset\\";
//        String pathRGB = rootPath + "frames_rgb\\meridian\\";
//        String pathJPG = rootPath + "frames\\meridian\\";
//        String pathCanny = rootPath + "canny\\meridian\\";
//
//        VideoSegmentation videoSegmentation = new VideoSegmentation();
////        System.out.println("Canny starts......");
////        videoSegmentation.cannyEdgeDetection(pathJPG, pathCanny);
////        System.out.println("Canny finishes......");
//
//        videoSegmentation.getFramesDiff(pathRGB, pathCanny);
//        for (int i = 0; i < FPS * FULL_TIME-1; i++) {
//            if (diffOrigin[i] > THRESHOLD) {
//
//            }
//        }
//
//
//    }
}
