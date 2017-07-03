package net.dynamicdungeon;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
    public static void play(final String soundName) {
	Thread player = new Thread() {
	    @Override
	    public void run() {
		try {
		    AudioInputStream in = AudioSystem.getAudioInputStream(
			    Sound.class.getResourceAsStream("/assets/sounds/" + soundName + ".wav"));
		    if (in != null) {
			AudioFormat baseFormat = in.getFormat();
			AudioFormat targetFormat = new AudioFormat(baseFormat.getEncoding(), baseFormat.getSampleRate(),
				baseFormat.getSampleSizeInBits(), baseFormat.getChannels(),
				baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			AudioInputStream dataIn = AudioSystem.getAudioInputStream(targetFormat, in);
			byte[] buffer = new byte[4096];
			// get a line from a mixer in the system with the wanted
			// format
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, targetFormat);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			if (line != null) {
			    line.open();
			    line.start();
			    int nBytesRead = 0;
			    while (nBytesRead != -1) {
				nBytesRead = dataIn.read(buffer, 0, buffer.length);
				if (nBytesRead != -1) {
				    line.write(buffer, 0, nBytesRead);
				}
			    }
			    line.drain();
			    line.stop();
			    line.close();
			    dataIn.close();
			}
			in.close();
			// playback finished
		    }
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
		    // failed
		}
	    }
	};
	player.setName("Sound Effect Player");
	player.setDaemon(true);
	player.start();
    }
}
