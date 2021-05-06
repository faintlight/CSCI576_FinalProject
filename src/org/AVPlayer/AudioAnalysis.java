package org.AVPlayer;

import org.Consts;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.ArrayList;

public class AudioAnalysis {
    private String audioFileName;

    public AudioAnalysis(String audioPath) {
        audioFileName = audioPath;
    }

    public double[] weightAudio(ArrayList<Integer> breaks) {
        ArrayList<Double> audioWeights = new ArrayList<>();
        try {
            // Initialize Stream
            InputStream waveInStream = new FileInputStream(audioFileName);
            InputStream bufferedIn = new BufferedInputStream(waveInStream);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            AudioFormat audioFormat = audioStream.getFormat();

            int bytes = 0;
            byte[] audioBuffer = new byte[Consts.EXTERNAL_BUFFER_SIZE];
            long byteCount = 0;
            int index = 1;
            double slotByteCount = 0;
            double slotByteRes = 0;

            double bytesPerSample = audioFormat.getFrameSize();
            double sampleRate = audioFormat.getFrameRate();
            double bytesPerVidFrame = bytesPerSample*sampleRate*(1.0/Consts.VideoFps);

            double maxByteValue = Double.NEGATIVE_INFINITY;
            long breakPoint = transformation(breaks.get(index), bytesPerVidFrame);

            index++;
            while (bytes != -1) {
                bytes = audioStream.read(audioBuffer, 0, audioBuffer.length);

                if (bytes >= 0) {
                    for (int i = 1; i < bytes; i += 2) {
                        if (byteCount < breakPoint) {
                            slotByteRes += audioBuffer[i];
                            slotByteCount++;
                        } else {
                            audioWeights.add(slotByteRes / slotByteCount);
                            slotByteRes = audioBuffer[i];
                            slotByteCount = 1;
                            if (index < breaks.size()) {
                                breakPoint = this.transformation(breaks.get(index), bytesPerVidFrame);
                                index++;
                            } else {
                                bytes = -1;
                            }
                        }
                        byteCount += 2;
                    }
                }
            }

            while (audioWeights.size() < breaks.size() - 1) {
                audioWeights.add(slotByteRes / slotByteCount);
            }

            for(int i=0; i<audioWeights.size(); i++) {
                if(audioWeights.get(i)>maxByteValue) {
                    maxByteValue = audioWeights.get(i);
                }
            }

            for(int i=0;i<audioWeights.size();i++) {
                double val = audioWeights.get(i);
                audioWeights.set(i, maxByteValue/val);
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

    public long transformation(double frames, double bytesPerVidFrame) {
        return (long) (frames * bytesPerVidFrame);
    }
}