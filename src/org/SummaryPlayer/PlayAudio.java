package org.SummaryPlayer;


import org.AVPlayer.PlayWaveException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlayAudio implements Runnable{
    String filePath;
    GeneratedPlayer player;
    public PlayAudio(String filePath, GeneratedPlayer player) {
        this.filePath = filePath;
        this.player = player;
    }

    @Override
    public void run() {
        // opens the inputStream
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(this.filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // initializes the playSound Object
        PlaySound playSound = new PlaySound(inputStream, player);

        // plays the sound
        try {
            playSound.play();
        } catch (PlayWaveException e) {
            e.printStackTrace();
            return;
        }
    }
}