package org;

import java.io.File;

public class Consts
{
    public static File opencvFile = new File("C:\\Users\\wgz\\Desktop\\USC\\CSCI576\\finalproject\\b34c662f720236ba\\opencv\\x64\\opencv_java451.dll");

    public static String opencvFileName = Consts.opencvFile.getPath();

    public static File datasetRootFile = new File("C:\\Users\\wgz\\Desktop\\USC\\CSCI576\\finalproject\\b34c662f720236ba\\dataset");

    public static String datasetRootName = Consts.datasetRootFile.getPath();

    public static File audioRootFile = new File(datasetRootFile, "audio");

    public static String audioRootName = Consts.audioRootFile.getPath();

    public static File soccerFrameFiles = new File(datasetRootFile, "soccer");

    public static String soccerFrames = Consts.soccerFrameFiles.getPath();

    public static File meridianFrameFiles = new File(datasetRootFile, "meridian");

    public static String meridianFrames = Consts.meridianFrameFiles.getPath();

    public static File concertFrameFiles = new File(datasetRootFile, "concert");

    public static String concertFrames = Consts.concertFrameFiles.getPath();

    public static File outRootFile = new File("C:\\Users\\wgz\\Desktop\\USC\\CSCI576\\finalproject\\b34c662f720236ba\\out\\data");

    public static String outRootName = Consts.outRootFile.getPath(); 
}