package org.AVPlayer;

import org.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.util.ArrayList;

public class WriteAudio {

    ArrayList<Integer> breaks;
    String inputPath;
    String outputPath;
    static int FPS = 30;
    static int FULL_TIME = 540;
    static int width = 320;
    static int height = 180;
    static int EXTERNAL_BUFFER_SIZE = 16384;
    static int outputFramesNum = 0;
    static long outputBytesNum = 0;


    public WriteAudio (ArrayList<Integer> breaks, String inputPath, String outputPath) {
        this.breaks = breaks;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    public static byte[] int4byte(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);
        targets[1] = (byte) ((res >> 8) & 0xff);
        targets[2] = (byte) ((res >> 16) & 0xff);
        targets[3] = (byte) (res >>> 24);
        return targets;
    }
    public void getHead(OutputStream os, AudioFormat audioFormat) throws IOException {
        int fr = (int) audioFormat.getFrameRate();
        int fs = audioFormat.getFrameSize();
        int c = audioFormat.getChannels();
//            1 - 4       "RIFF"               Marks the file as a riff file. Characters are each 1. byte long.
        os.write(0x52);
        os.write(0x49);
        os.write(0x46);
        os.write(0x46);
//            5 - 8       File size (integer)  Size of the overall file - 8 bytes, in bytes (32-bit integer). Typically, you'd fill this in after creation.
        os.write(int4byte((int) (outputBytesNum-8))[0]);
        os.write(int4byte((int) (outputBytesNum-8))[1]);
        os.write(int4byte((int) (outputBytesNum-8))[2]);
        os.write(int4byte((int) (outputBytesNum-8))[3]);
//            9 -12       "WAVE"               File Type Header. For our purposes, it always equals "WAVE".
        os.write(0x57);
        os.write(0x41);
        os.write(0x56);
        os.write(0x45);
//            13-16       "fmt "               Format chunk marker. Includes trailing null
        os.write(0x66);
        os.write(0x6d);
        os.write(0x74);
        os.write(0x20);
//            17-20       18                   Length of format data as listed above
        os.write(0x12);
        os.write(0x00);
        os.write(0x00);
        os.write(0x00);
//            21-22       1                    Type of format (1 is PCM) - 2 byte integer
        os.write(0x01);
        os.write(0x00);
//            23-24       2                    Number of Channels - 2 byte integer
        byte[] writing = new byte[4];
        for(int i = 0; i < 2; i++) {
            if (Math.floor(c/Math.pow(16, 2-(2*i)))!=0) {
                writing[i] = (byte)Math.floor(c/Math.pow(16, 2-(2*i)));
                c -= Math.pow(16, 2-(2*i));
            }
            else {
                writing[i] = 0x00;
            }
        }
        os.write(writing[1]);
        os.write(writing[0]);
//            25-28       44100                Sample Rate - 32 bit integer. Common values are 44100 (CD), 48000 (DAT). Sample Rate = Number of Samples per second, or Hertz.
        os.write(int4byte(fr)[0]);
        os.write(int4byte(fr)[1]);
        os.write(int4byte(fr)[2]);
        os.write(int4byte(fr)[3]);
//            29-32       176400               (Sample Rate * BitsPerSample * Channels) / 8.
        os.write(int4byte(fr*fs*c)[0]);
        os.write(int4byte(fr*fs*c)[1]);
        os.write(int4byte(fr*fs*c)[2]);
        os.write(int4byte(fr*fs*c)[3]);
//            33-34       4                    (BitsPerSample * Channels) / 8.1 - 8 bit mono2 - 8 bit stereo/16 bit mono4 - 16 bit stereo
            int bytesPerFrame = audioFormat.getFrameSize();
            for(int i = 0;i < 2;i++) {
                if (Math.floor(bytesPerFrame/Math.pow(16, 2-(2*i)))!=0) {
                    writing[i] = (byte)Math.floor(bytesPerFrame/Math.pow(16, 2-(2*i)));
                    bytesPerFrame -= Math.pow(16, 2-(2*i));
                }
                else {
                    writing[i] = 0x00;
                }
            }
        os.write(writing[1]);
        os.write(writing[0]);
//            35-38       16                   Bits per sample
        os.write(0x10);
        os.write(0x00);
        os.write(0x00);
        os.write(0x00);
//            39-42       "data"               "data" chunk header. Marks the beginning of the data section.
        os.write(0x64);
        os.write(0x61);
        os.write(0x74);
        os.write(0x61);
//            42-46       File size (data)     Size of the data section, i.e. file size - 44 bytes header.
        os.write(int4byte((int)(outputBytesNum - 46))[0]);
        os.write(int4byte((int)(outputBytesNum - 46))[1]);
        os.write(int4byte((int)(outputBytesNum - 46))[2]);
        os.write(int4byte((int)(outputBytesNum - 46))[3]);
    }

    public void saveAudio() {
        try {
            System.out.println("Strating audio generation!!!");
            InputStream is = new FileInputStream(new File(inputPath));
            OutputStream os = new FileOutputStream(outputPath);
            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            AudioFormat af = audioInputStream.getFormat();

            int breakFrames;
            long frameBytes = (long) (af.getFrameRate() * af.getFrameSize() / FPS);
            ArrayList<Long> byteBreaks = new ArrayList<Long>();
            for (int i = 0; i < breaks.size()-1; i+=2) {
                breakFrames = breaks.get(i+1) - breaks.get(i);
                this.outputFramesNum += breakFrames;
                this.outputBytesNum += breakFrames * frameBytes;
                byteBreaks.add(frameBytes*breaks.get(i));
                byteBreaks.add(frameBytes*breaks.get(i+1));
            }

            getHead(os, af);

            int byteBreaksCount = 0;
            long startByte = byteBreaks.get(byteBreaksCount++);
            long endByte = byteBreaks.get(byteBreaksCount++);
            long currentByte = 0;
            int readBytes = 0;
            long writeBytes = 0;
            byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];
            while (readBytes != -1) {
                readBytes = audioInputStream.read(audioBuffer, 0,
                        audioBuffer.length);
                for (int i = 1; i < readBytes; i+=2, currentByte+=2) {
                    if (byteBreaksCount >= breaks.size() && currentByte > endByte) {
                        readBytes = -1;
                        break;
                    }
                    if (currentByte>endByte) {
                        startByte= byteBreaks.get(byteBreaksCount++);
                        endByte = byteBreaks.get(byteBreaksCount++);
                    }
                    if (currentByte >= startByte && currentByte <= endByte) {
                        os.write(audioBuffer[i-1]);
                        os.write(audioBuffer[i]);
                        writeBytes += 2;
                    }
                }
                System.out.println("Audio generating......" + 100*writeBytes/outputBytesNum + "%");
            }
            System.out.println("Finishing audio generation!!!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ConfigurationProperty cp = new ConfigurationProperty();
        String audioPath = cp.GetFilePath(args[0], "Audio");
        String outputPath = cp.GetFilePath(args[0], "OutAudio");

        ArrayList<Integer> breaks = new ArrayList<>();
        breaks.add(0);
        breaks.add(100);
        breaks.add(1200);
        breaks.add(1400);
        breaks.add(5500);
        breaks.add(6000);
        breaks.add(7000);
        breaks.add(7500);

        WriteAudio wa = new WriteAudio(breaks, audioPath, outputPath);
        wa.saveAudio();
    }
}
