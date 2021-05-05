package org.AVPlayer;


import java.io.*;
import java.util.ArrayList;

public class WriteVideo {

    ArrayList<Integer> breaks;
    String inputPath;
    String outputPath;
    static int outputFramesNum = 0;



    public WriteVideo (ArrayList<Integer> breaks, String inputPath, String outputPath) {
        this.breaks = breaks;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    public void deleteVideo() {
        File file = new File(outputPath);
        System.out.println("File deleting......");

        if (file == null || !file.exists()){
            System.out.println("File deleting failed.");
            return;
        }
        File[] files = file.listFiles();
        for (File f: files){
            f.delete();
        }
        System.out.println("File deleting completed!!!");

//        file.delete();
    }


    public void saveVideo() {
        int frameCnt = 0;
        for (int i = 0; i < breaks.size()-1; i+=2) {
            this.outputFramesNum += (breaks.get(i+1) - breaks.get(i));
        }
        System.out.println("Starting video generation!!!");
        for (int brk = 0; brk < breaks.size() - 1; brk += 2) {
            for (int frame = breaks.get(brk); frame <= breaks.get(brk + 1); frame++) {
                try {
                    File f = new File(inputPath + "frame" + frame + ".rgb");
                    byte b[] = new byte[(int) f.length()];
                    FileInputStream fis = new FileInputStream(f);
                    fis.read(b);
                    File fout = new File(outputPath + "frame" + frameCnt + ".rgb");
                    FileOutputStream fos = new FileOutputStream(fout);
                    fos.write(b);
                    frameCnt++;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Video generating......" + 100*frameCnt/outputFramesNum + "%");
        }
        System.out.println("Finishing video generation!!!");

    }

    public static void main(String[] args) {
        String rootpath = args[0];
        String videoPath = rootpath + args[1];
        String outputPath = args[2];
        ArrayList<Integer> breaks = new ArrayList<>();
        breaks.add(0);
        breaks.add(100);
        breaks.add(1200);
        breaks.add(1400);
        breaks.add(5500);
        breaks.add(6000);
        breaks.add(7000);
        breaks.add(7500);

        WriteVideo wa = new WriteVideo(breaks, videoPath, outputPath);
        wa.deleteVideo();
        wa.saveVideo();
    }
}
