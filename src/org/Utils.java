package org;

import java.util.ArrayList;
import java.util.Arrays;

public class Utils{
    public static void displayArray(ArrayList<Integer> array) {
        for (int i = 0; i < array.size(); i++) {
            System.out.println(array.get(i));
        }
    }
    public static void displayList(double[] list) {
        for (int i = 0; i < list.length; i++) {
            System.out.println("break"+i+":"+list[i]);
        }
    }

    public static double findMax(double[] array) {
        return Arrays.stream(array).max().getAsDouble();
    }

    public static void clearNaN(double[] array) {
        for (int i = 0; i < array.length; i++) {
            if (Double.isNaN(array[i])) {
                array[i] = 0;
            }
        }
    }
}