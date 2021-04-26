package org.AVPlayer;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName PlaySound
 * @Description: Sound player
 * @Author Group
 * @Date 2021-04-26
 * @Version 1.0
 **/
public class PlaySound {

	MixedPlayer player;
    private InputStream waveStream;


//    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
	private final int EXTERNAL_BUFFER_SIZE = 16384;

    /**
     * CONSTRUCTOR
     */
    public PlaySound(InputStream waveStream, MixedPlayer player) {
	this.waveStream = waveStream;
	this.player = player;
    }

    public void play() throws PlayWaveException {
	AudioInputStream audioInputStream = null;
	try {
	    //audioInputStream = AudioSystem.getAudioInputStream(this.waveStream);
		
		//add buffer for mark/reset support, modified by Jian
		InputStream bufferedIn = new BufferedInputStream(this.waveStream);
	    audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
		System.out.println("Audio total frames: " + audioInputStream.getFrameLength());
	} catch (UnsupportedAudioFileException e1) {
	    throw new PlayWaveException(e1);
	} catch (IOException e1) {
	    throw new PlayWaveException(e1);
	}

	// Obtain the information about the AudioInputStream
	AudioFormat audioFormat = audioInputStream.getFormat();
	Info info = new Info(SourceDataLine.class, audioFormat);

	// opens the audio channel
	SourceDataLine dataLine = null;
	try {
	    dataLine = (SourceDataLine) AudioSystem.getLine(info);
	    dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
	} catch (LineUnavailableException e1) {
	    throw new PlayWaveException(e1);
	}

	// Starts the music :P
	dataLine.start();

	int readBytes = 0;
	byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

	try {
	    while (readBytes != -1) {
	    	if (player.statusId == 1) {
				readBytes = audioInputStream.read(audioBuffer, 0,
						audioBuffer.length);

				if (player.audioProgress == 0) {
					player.audioProgress = 1;
//					Thread.sleep(1);
				}
				if (readBytes >= 0){
					dataLine.write(audioBuffer, 0, readBytes);
				}

			} else if (player.statusId == 0) {
				Thread.sleep(1);
			} else if (player.statusId == -1) {
				System.exit(0);
			}
	    }
	} catch (IOException e1) {
	    throw new PlayWaveException(e1);
	} catch (InterruptedException e) {
		e.printStackTrace();
	} finally {
	    // plays what's left and and closes the audioChannel
	    dataLine.drain();
	    dataLine.close();
	}

    }
}
