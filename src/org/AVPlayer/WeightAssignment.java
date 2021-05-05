package org.AVPlayer;

import org.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class WeightAssignment {
    public Boolean saveTmpFile = true;    

    public static double Nth(double[] list, int n, ArrayList<Integer> breakPoints) {
        ArrayList<Double> array = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            if (!Double.isNaN(list[i]) && breakPoints.get(i+1)-breakPoints.get(i) > 100){
                array.add(list[i]);
            }
        }
        array.sort(Comparator.reverseOrder());
        return array.get(n-1);
    }

    public static int[] GetBigestN(double[] list, int n)
    {
        int[] bigestN = new int[n];
        for (int k = 0; k < n; k ++)
        {
            int largestIndex = -1;
            double largestScore = -1; // please set all scores as positive values
            for (int i = 0; i < list.length; i ++)
            {
                if ()
            }
        }
    }

    private void saveBreaksToFile(String fileName, ArrayList<Integer> listToWrite) 
    {
        try
        {
            File writeFile = new File(fileName);
            writeFile.createNewFile();
            BufferedWriter outBuffer = new BufferedWriter(new FileWriter(writeFile));
            for (int breakPoint : listToWrite)
            {
                outBuffer.write(String.format("%d\n", breakPoint));
            }
            outBuffer.flush();
            outBuffer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getResults(String videoPath) {
        VideoSegmentation videoSegmentation = new VideoSegmentation();
        videoSegmentation.getBreakPoints(videoPath);

        MotionAnalyse motionAnalyse = new MotionAnalyse();
        FrameColorAnalyse frameColorAnalyse = new FrameColorAnalyse();
        ArrayList<Integer> breakPoints = videoSegmentation.breakPoints;
        ArrayList<Integer> resultPoints = new ArrayList<>();
<<<<<<< HEAD
//        for (int i = 0; i < breaks.length; i++) {
//            breakPoints.add(breaks[i]);
//        }
        displayArray(breakPoints);

=======
>>>>>>> 2306516... configure property
        double[] motionResults = motionAnalyse.getMotionArray(breakPoints, videoPath);
        Utils.displayList(motionResults);
        for (int i = 0; i < motionResults.length; i++) {
            if (motionResults[i] > Nth(motionResults, 10, breakPoints) && breakPoints.get(i+1)-breakPoints.get(i) > 100) {
                resultPoints.add(breakPoints.get(i));
                resultPoints.add(breakPoints.get(i+1));
            }
        }
        Utils.displayArray(resultPoints);
        if (this.saveTmpFile) {
            this.saveBreaksToFile("1.txt", resultPoints);
        }
        return resultPoints;
    }

    public static void main(String[] args) {
        WeightAssignment wa = new WeightAssignment();
<<<<<<< HEAD
//        wa.getResults("D:\\MyMainFolder\\MSUSC\\CSCI576\\project\\dateset\\frames_rgb\\concert\\");
        wa.getResults(Consts.);
=======
        wa.getResults(Consts.meridianFrames);
>>>>>>> 2306516... configure property

    }
}
