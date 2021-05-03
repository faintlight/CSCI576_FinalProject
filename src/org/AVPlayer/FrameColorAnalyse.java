package org.AVPlayer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FrameColorAnalyse 
{
    int Width = 320;
    int Height = 180;
    int FrameLength = 320 * 180;

    public FrameColorAnalyse(int width=320, int height=180)
    {
        this.Width = width;
        this.Height = height;
        this.FrameLength = this.width * this.height;
    }

    private void CheckFrameLengthValidness(int frameLength)
    {
        System.out.println("The frame length is " + frameLength + " and it should be " + this.FrameLength);
        if (frameLength != this.FrameLength)
        {
            throw new Exception("Wrong frame image array length! The length should be ");
        }
    }

    /// <summary>
    /// calculate the number of neighbour pixels.
    /// </summary>
    /// <remarks>
    /// -------------
    /// |   | x |   |
    /// -------------
    /// | x | o | x |
    /// -------------
    /// |   | x |   |
    /// -------------
    /// Used for calculate the contrast, x are the neighbours for o
    /// </remarks>
    /// <returns>number of neighbours</remarks>
    private int GetNumberOfClosePixels()
    {
        int twoNeighbour = 8;
        int threeNeighbour = (this.Width - 2) * 2 + (this.Height - 2) * 2;
        int fourNeighbour = (this.Width - 2) * (this.Height -2);
        return twoNeighbour * 2 + threeNeighbour * 3 + fourNeighbour * 4;
    }

    /// <summary>
    /// Calculate the variance of each of RGB of the frame
    /// </summary>
    /// <params name="frameImage">image frame based on 3 channels, RGB</parmas>
    /// <returns>variance of each color in each channel</returns>
    public double[] CalculateFrameColorVariance(double[][] frameImage)
    {
        this.CheckFrameLengthValidness(frameImage[0].length);
        double[] variance = new Double[3];
        for (int c = 0; c < 3; c++)
        {
            
        }
    }

    public double CalculateFrameLuminanceAvg(double[][] frameImage)
    {
        this.CheckFrameLengthValidness(frameImage[0].length);
        
        double lumTotal = 0;
        for(int ind = 0; ind < this.FrameLength; ind ++)
        {
            lumTotal += frameImage[0][ind] * 0.27 + frameImage[1][ind] * 0.67 + frameImage[2][ind] * 0.06;
        }
        return lumTotal / this.FrameLength;
    }


    public double CalculateFrameLuminanceVar(double[][] frameImage)
    {
        lumAvg = this.CalculateFrameLuminanceAvg(frameImage);
        double varTotal = 0;
        for(int ind = 0; ind < this.FrameLength; ind ++)
        {
            lumInd = frameImage[0][ind] * 0.27 + frameImage[1][ind] * 0.67 + frameImage[2][ind] * 0.06;
            varTotal += (lumInd - lumAvg) ** 2;
        }
        return varTotal / this.FrameLength;
    }

    public CalculateFrameContrast(double[][] frameImage)
    {
        this.CheckFrameLengthValidness(frameImage[0].length);

    }
    
}