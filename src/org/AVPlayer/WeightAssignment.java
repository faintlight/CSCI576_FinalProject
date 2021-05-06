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
            if (breakPoints.get(i+1)-breakPoints.get(i) > 100){
                array.add(list[i]);
            }
        }
        array.sort(Comparator.reverseOrder());
        return array.get(n-1);
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

    public ArrayList<Integer> getResults(String dataName) {
        ConfigurationProperty cp = new ConfigurationProperty();
        VideoSegmentation videoSegmentation = new VideoSegmentation();
        videoSegmentation.getBreakPoints(cp.GetFilePath(dataName, "Video"));

        MotionAnalyse motionAnalyse = new MotionAnalyse();
        FrameColorAnalyse frameColorAnalyse = new FrameColorAnalyse();
        ArrayList<Integer> breakPoints = videoSegmentation.breakPoints;
        ArrayList<Integer> resultPoints = new ArrayList<>();
        double[] motionResults = motionAnalyse.getMotionArray(breakPoints, cp.GetFilePath(dataName, "Video"));
        Utils.displayArray(breakPoints);
        Utils.displayList(motionResults);

        int n = (breakPoints.size()-1)/8;
        for (int i = 0; i < motionResults.length; i++) {
            if (motionResults[i] > Nth(motionResults, n, breakPoints) && breakPoints.get(i+1)-breakPoints.get(i) > 100) {
                if (i != 0 && !resultPoints.contains(breakPoints.get(i)) && breakPoints.get(i)-breakPoints.get(i-1) > 100) {
                    resultPoints.add(breakPoints.get(i-1));
                    resultPoints.add(breakPoints.get(i));
                }
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
        wa.getResults(args[0]);

    }
}
