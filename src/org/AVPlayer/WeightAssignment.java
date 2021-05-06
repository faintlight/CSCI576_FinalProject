package org.AVPlayer;

import org.ConfigurationProperty;
import org.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class WeightAssignment {
    public Boolean saveTmpFile = true;
    static ConfigurationProperty cp = new ConfigurationProperty();

    public static double Nth(double[] list, int n, ArrayList<Integer> breakPoints, int frameHold) {
        ArrayList<Double> array = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            if (breakPoints.get(i+1)-breakPoints.get(i) > frameHold) {
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
        VideoSegmentation videoSegmentation = new VideoSegmentation();
        videoSegmentation.getBreakPoints(cp.GetFilePath(dataName, "Video"));

        MotionAnalyse motionAnalyse = new MotionAnalyse();
        FrameColorAnalyse frameColorAnalyse = new FrameColorAnalyse();
        ArrayList<Integer> breakPoints = videoSegmentation.breakPoints;
        ArrayList<Integer> resultPoints = new ArrayList<>();
        double[] motionResults = motionAnalyse.getMotionArray(breakPoints, cp.GetFilePath(dataName, "Video"));
        Utils.displayArray(breakPoints);
        Utils.displayList(motionResults);

        int frameHold = breakPoints.size()>100?100:40;
        int n = (breakPoints.size()-1)/10;
        double threshold = Nth(motionResults, n, breakPoints, frameHold);

        for (int i = 0; i < motionResults.length; i++) {
            if (motionResults[i] > threshold && breakPoints.get(i+1)-breakPoints.get(i) > frameHold) {
                if (i != 0 && !resultPoints.contains(breakPoints.get(i)) && breakPoints.get(i)-breakPoints.get(i-1) > frameHold) {
                    resultPoints.add(breakPoints.get(i-1));
                    resultPoints.add(breakPoints.get(i));
                }
//                resultPoints.add(breakPoints.get(i));
//                resultPoints.add(breakPoints.get(i+1));
//                System.out.println(breakPoints.get(i+1)-breakPoints.get(i));
                if (breakPoints.get(i+1) - breakPoints.get(i) > 500) {
                    resultPoints.add(breakPoints.get(i));
                    resultPoints.add(breakPoints.get(i)+300);
                } else {
                    resultPoints.add(breakPoints.get(i));
                    resultPoints.add(breakPoints.get(i+1));
                }

            }
        }

        if (motionResults[motionResults.length-2] > threshold
                && !resultPoints.contains(breakPoints.get(motionResults.length-1))) {
            resultPoints.add(breakPoints.get(motionResults.length-2));
            resultPoints.add(breakPoints.get(motionResults.length-1));
        }

        if (motionResults[motionResults.length-1] > threshold
                && !resultPoints.contains(breakPoints.get(motionResults.length))) {
            resultPoints.add(breakPoints.get(motionResults.length-1));
            resultPoints.add(breakPoints.get(motionResults.length));
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
