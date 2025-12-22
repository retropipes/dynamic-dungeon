package net.dynamicdungeon.sound;

import java.lang.Thread.UncaughtExceptionHandler;

public class SoundExceptionHandler implements UncaughtExceptionHandler {
    @Override
    public void uncaughtException(final Thread thr, final Throwable exc) {
	try {
	    if (thr instanceof final SoundFactory media) {
		SoundFactory.taskCompleted(media.getNumber());
	    }
	} catch (final Throwable t) {
	    // Ignore
	}
    }
}
