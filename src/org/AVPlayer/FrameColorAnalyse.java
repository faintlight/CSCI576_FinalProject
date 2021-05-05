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

    public FrameColorAnalyse(int width, int height)
    {
        this.Width = width;
        this.Height = height;
        this.FrameLength = this.Width * this.Height;
    }

    public FrameColorAnalyse()
    {
    }

    /// <summary>
    /// check whether the frame statisfies the given width and height
    /// </summary>
    /// <params name="frameLength">length of the target frame<params>
    private Boolean CheckFrameLengthValidness(int frameLength)
    {
        System.out.println("The frame length is " + frameLength + " and it should be " + this.FrameLength);
        
        return true;
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
    /// Calculate the average of each of RGB of the frame
    /// </summary>
    /// <params name="frameImage">image frame based on 3 channels, RGB</parmas>
    /// <returns>average of each color in each channel</returns>
    public double[] CalculateFrameColorAverage(double[][] frameImage)
    {
        this.CheckFrameLengthValidness(frameImage[0].length);
        double[] averages = new double[3];
        for (int c = 0; c < 3; c++)
        {
            for(int ind = 0; ind < this.FrameLength; ind++)
            {
                averages[c] += frameImage[c][ind] / this.FrameLength;
            }
        }
        return averages;
    }


    /// <summary>
    /// Calculate the variances of each of RGB of the frame
    /// </summary>
    /// <params name="frameImage">image frame based on 3 channels, RGB</parmas>
    /// <returns>variance of each color in each channel</returns>
    public double[] CalculateFrameColorVariances(double[][] frameImage)
    {
        this.CheckFrameLengthValidness(frameImage[0].length);
        double[] averages = this.CalculateFrameColorAverage(frameImage);
        double[] variances = new double[3];
        for (int c = 0; c < 3; c++)
        {
            for(int ind = 0; ind < this.FrameLength; ind++)
            {
                averages[c] += Math.pow(frameImage[c][ind] - averages[c], 2) / this.FrameLength;
            }
        }
        return variances;
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
        double lumAvg = this.CalculateFrameLuminanceAvg(frameImage);
        double varTotal = 0;
        for(int ind = 0; ind < this.FrameLength; ind ++)
        {
            double lumInd = frameImage[0][ind] * 0.27 + frameImage[1][ind] * 0.67 + frameImage[2][ind] * 0.06;
            varTotal += Math.pow(lumInd - lumAvg, 2);
        }
        return varTotal / this.FrameLength;
    }


    public double[] CalculateFrameLuminanceMatrix(double[][] frameImage)
    {
        this.CheckFrameLengthValidness(frameImage.length);
        double[] lumMatrix = new double[this.FrameLength];
        for(int ind = 0; ind < this.FrameLength; ind ++)
        {
            lumMatrix[ind] = frameImage[0][ind] * 0.27 + frameImage[1][ind] * 0.67 + frameImage[2][ind] * 0.06;
        }
        return lumMatrix;
    }


    public double CalculateFrameContrast(double[][] frameImage)
    {
        this.CheckFrameLengthValidness(frameImage[0].length);
        double contrast = 0;
        double[] lumMatrix = this.CalculateFrameLuminanceMatrix(frameImage);
        int contrastCalPair = this.GetNumberOfClosePixels();
        for (int i = 0; i < this.Width; i ++)
        {
            for (int j = 0; j < this.Height; j ++) 
            {
                if (i != 0) 
                {
                    contrast += Math.pow(lumMatrix[j*this.Width+i] - lumMatrix[j*this.Width+(i-1)], 2) / contrastCalPair;
                }
                if (i != this.Width-1)
                {
                    contrast += Math.pow(lumMatrix[j*this.Width+i] - lumMatrix[j*this.Width+(i+1)], 2) / contrastCalPair;
                }
                if (j != 0)
                {
                    contrast += Math.pow(lumMatrix[(j-1)*this.Width+i] - lumMatrix[j*this.Width+i], 2) / contrastCalPair;
                }
                if(j != this.Height - 1)
                {
                    contrast += Math.pow(lumMatrix[(j+1)*this.Width+i] - lumMatrix[j*this.Width+i], 2) / contrastCalPair;
                }
            }
        }
        return contrast;
    }

    public static void main(String[] args){
        System.out.println("!23");
    }
}