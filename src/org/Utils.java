package org;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Utils{
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

    public static double findMax(double[] array) {
        return Arrays.stream(array).max().getAsDouble();
    }
}