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

    public int addResult(int start, int end, ArrayList<Integer> breaks, double[] motion) {
        double maxMotion = 0;
        int addI = 0;
        for (int i = 0; i < breaks.size()-1; i++) {
            if (breaks.get(i) > start && breaks.get(i+1) < end) {
                if (motion[i] > maxMotion) {
                    addI = i;
                }
            }
        }
        return addI;
    }

    public ArrayList<Integer> getResults(String dataName) {
        MotionAnalyse motionAnalyse = new MotionAnalyse();
        AudioAnalysis audioAnalysis = new AudioAnalysis(cp.GetFilePath(dataName, "Audio"));
        FrameColorAnalyse frameColorAnalyse = new FrameColorAnalyse();

        VideoSegmentation videoSegmentation = new VideoSegmentation();
        videoSegmentation.getBreakPoints(cp.GetFilePath(dataName, "Video"));
        ArrayList<Integer> breakPoints = videoSegmentation.breakPoints;
        ArrayList<Integer> resultPoints = new ArrayList<>();

        double[] motionResults = motionAnalyse.getMotionArray(breakPoints, cp.GetFilePath(dataName, "Video"));
        double[] audioResults = audioAnalysis.weightAudio(breakPoints);
        double[] finalResults = new double[motionResults.length];
        for (int i = 0; i < motionResults.length; i++) {
            finalResults[i] = motionResults[i]*0.8 + audioResults[i]*0.2;
        }
        Utils.displayArray(breakPoints);
        Utils.displayList(finalResults);

//        int frameHold = 100;
        int frameHold = 40;
//        int n = (breakPoints.size()-1)/10;
        int n = (breakPoints.size()-1)/7;
        double threshold = Nth(finalResults, n, breakPoints, frameHold);

        for (int i = 0; i < finalResults.length; i++) {
            if (finalResults[i] > threshold && breakPoints.get(i+1)-breakPoints.get(i) > frameHold) {
                if (i != 0 && !resultPoints.contains(breakPoints.get(i)) && breakPoints.get(i)-breakPoints.get(i-1) > frameHold) {
                    resultPoints.add(breakPoints.get(i-1));
                    resultPoints.add(breakPoints.get(i));
                }
                resultPoints.add(breakPoints.get(i));
                resultPoints.add(breakPoints.get(i+1));

            }
        }

        // Ending supplement
        for (int i = 5; i > 0; i--) {
            if (finalResults[finalResults.length-i] > threshold
                    && !resultPoints.contains(breakPoints.get(finalResults.length-i+1))) {
                resultPoints.add(breakPoints.get(finalResults.length-i));
                resultPoints.add(breakPoints.get(finalResults.length-i+1));
            }
        }

        // Vacant supplement
        int cut = 0;
        for (int i = 0; i < resultPoints.size(); i+=2) {
            if (resultPoints.get(i) - cut > 3000) {
                int addI = addResult(cut, resultPoints.get(i), breakPoints, motionResults);
                if (addI != 0) {
                    resultPoints.add(breakPoints.get(addI));
                    resultPoints.add(breakPoints.get(addI+1));
                }
            }
            cut = resultPoints.get(i);
        }

        resultPoints.sort(Comparator.naturalOrder());
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
