package org.AVPlayer;

import java.util.ArrayList;
import java.util.Comparator;

public class WeightAssignment {
    static int breaks[] = {0,31,156,277,394,470,528,605,693,892,1083,1357,1460,1650,1757,2161,2282,2411,2583,2621,2689,2997,3152,3259,3320,3573,3638,4002,4120,4236,4369,4970,5051,5268,5579,5735,5943,6139,7155,7214,7803,8164,8842,10096,10505,10835,10918,11219,11872,12041,12289,12459,13137,16199};


    public static void displayArray(ArrayList<Integer> array) {
        for (int i = 0; i < array.size(); i+=2) {
            System.out.println(array.get(i)+"-"+array.get(i+1));
        }
    }
    public static void displayList(double[] list) {
        for (int i = 0; i < list.length; i++) {
            System.out.println("break"+i+":"+list[i]);
        }
    }

    public static double Nth(double[] list, int n) {
        ArrayList<Double> array = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            array.add(list[i]);
        }
        array.sort(Comparator.reverseOrder());
        return array.get(n-1);
    }

    public ArrayList<Integer> getResults(String videoPath) {
        VideoSegmentation videoSegmentation = new VideoSegmentation();
        videoSegmentation.getBreakPoints(videoPath);

        MotionAnalyse motionAnalyse = new MotionAnalyse();
        ArrayList<Integer> breakPoints = videoSegmentation.breakPoints;
        ArrayList<Integer> resultPoints = new ArrayList<>();
//        for (int i = 0; i < breaks.length; i++) {
//            breakPoints.add(breaks[i]);
//        }
        double[] motionResults = motionAnalyse.getMotionArray(breakPoints, videoPath);
        displayList(motionResults);
        for (int i = 0; i < motionResults.length; i++) {
            if (motionResults[i] > Nth(motionResults, 10)) {
                resultPoints.add(breakPoints.get(i));
                resultPoints.add(breakPoints.get(i+1));
            }
        }
        displayArray(resultPoints);
        return resultPoints;
    }

    public static void main(String[] args) {
        WeightAssignment wa = new WeightAssignment();
        wa.getResults("D:\\MyMainFolder\\MSUSC\\CSCI576\\project\\dateset\\frames_rgb\\\\meridian\\");

    }
}
