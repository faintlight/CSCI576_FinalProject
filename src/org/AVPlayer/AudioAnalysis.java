package org.AVPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.ArrayList;

public class AudioAnalysis {
    private static final int EXTERNAL_BUFFER_SIZE = 16384;
    private String audioFileName;
    private final double FPS = 30;
    private final int HEADER_LEN = 46;

    public AudioAnalysis(String audioPath) {
        audioFileName = audioPath;
    }

    public double[] calculateAudio(ArrayList<Integer> breaks) {
        ArrayList<Double> audioWeights = new ArrayList<>();

        try {
            File sourceFile = new File(audioFileName);
            InputStream inputstream = new FileInputStream(audioFileName);
            InputStream waveInStream = inputstream;

            InputStream bufferedIn = new BufferedInputStream(waveInStream);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            AudioFormat audioFormat = audioStream.getFormat();


            long totFileBytes = sourceFile.length();
            int numChan = audioFormat.getChannels();
            double bytesPerSample = audioFormat.getFrameSize();
            double sampleRate = audioFormat.getFrameRate();
            //double bytesPerSecond = sampleRate*bytesPerSample;
            long actualNumSamples = (long) ((totFileBytes - HEADER_LEN) / (bytesPerSample * numChan));
            long actualNumBytes = (long) (actualNumSamples * numChan);

            double bytesPerVidFrame = bytesPerSample*sampleRate*(1/FPS);
            double vidFramesPerByte = 1/bytesPerVidFrame;

            int readBytes = 0;
            byte[] audioBuffer = new byte[EXTERNAL_BUFFER_SIZE];
            long byteCount = 0;
            int index = 1;
            double wgtCount = 0;
            double wgtTotal = 0;

            double max = Double.NEGATIVE_INFINITY;
            long breakPoint = framesToBytes(breaks.get(index), bytesPerVidFrame);

            index++;
            while (readBytes != -1) {
                readBytes = audioStream.read(audioBuffer, 0, audioBuffer.length);

                if (readBytes >= 0) {
                    for (int i = 1; i < readBytes; i += 2) {
                        if (byteCount < breakPoint) {
                            wgtTotal += audioBuffer[i];
                            wgtCount++;
                        } else {
                            audioWeights.add(wgtTotal / wgtCount);
                            wgtTotal = audioBuffer[i];
                            wgtCount = 1;
                            if (index < breaks.size()) {
                                breakPoint = this.framesToBytes(breaks.get(index), bytesPerVidFrame);
                                index++;
                            } else {
                                readBytes = -1;
                            }
                        }
                        byteCount += 2;
                    }
                }
            }

            while (audioWeights.size() < breaks.size() - 1) {
                audioWeights.add(wgtTotal / wgtCount);
            }

            for(int i=0;i<audioWeights.size();i++) {
                if(audioWeights.get(i)>max) {
                    max = audioWeights.get(i);
                }
            }

            for(int i=0;i<audioWeights.size();i++) {
                double val = audioWeights.get(i);
                audioWeights.set(i, max/val);
            }
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioWeights.stream().mapToDouble(i->i).toArray();
    }
    public long framesToBytes(double frames, double bytesPerVidFrame) {
        return (long) (frames * bytesPerVidFrame);
    }
}
