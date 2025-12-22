package net.dynamicdungeon.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

class SoundFile extends SoundFactory {
    private final String filename;
    private int number;

    public SoundFile(final ThreadGroup group, final String wavfile, final int taskNum) {
	super(group);
	this.filename = wavfile;
	this.number = taskNum;
    }

    @Override
    int getNumber() {
	return this.number;
    }

    @Override
    public void run() {
	if (this.filename != null) {
	    final var soundFile = new File(this.filename);
	    if (!soundFile.exists()) {
		SoundFactory.taskCompleted(this.number);
		return;
	    }
	    try (var audioInputStream = AudioSystem.getAudioInputStream(soundFile)) {
		final var format = audioInputStream.getFormat();
		final var info = new DataLine.Info(SourceDataLine.class, format);
		try (var auline = (SourceDataLine) AudioSystem.getLine(info)) {
		    auline.open(format);
		    auline.start();
		    var nBytesRead = 0;
		    final var abData = new byte[SoundFactory.EXTERNAL_BUFFER_SIZE];
		    try {
			while (nBytesRead != -1) {
			    nBytesRead = audioInputStream.read(abData, 0, abData.length);
			    if (nBytesRead >= 0) {
				auline.write(abData, 0, nBytesRead);
			    }
			}
		    } catch (final IOException e) {
			SoundFactory.taskCompleted(this.number);
			return;
		    } finally {
			auline.drain();
			auline.close();
			try {
			    audioInputStream.close();
			} catch (final IOException e2) {
			    // Ignore
			}
		    }
		} catch (final LineUnavailableException e) {
		    try {
			audioInputStream.close();
		    } catch (final IOException e2) {
			// Ignore
		    }
		    SoundFactory.taskCompleted(this.number);
		    return;
		}
	    } catch (final UnsupportedAudioFileException | IOException e1) {
	    }
	}
	SoundFactory.taskCompleted(this.number);
    }

    @Override
    public void stopLoop() {
	// Do nothing
    }

    @Override
    protected void updateNumber(final int newNumber) {
	this.number = newNumber;
    }
}
