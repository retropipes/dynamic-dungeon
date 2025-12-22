package net.dynamicdungeon.sound;

import java.net.URL;

public abstract class SoundFactory extends Thread {
    // Constants
    protected static final int EXTERNAL_BUFFER_SIZE = 4096; // 4Kb
    private static int ACTIVE_MEDIA_COUNT = 0;
    private static int MAX_MEDIA_ACTIVE = 5;
    private static SoundFactory[] ACTIVE_MEDIA = new SoundFactory[SoundFactory.MAX_MEDIA_ACTIVE];
    private static ThreadGroup MEDIA_GROUP = new ThreadGroup("Media Players");
    private static SoundExceptionHandler meh = new SoundExceptionHandler();

    // Factories
    public static SoundFactory getNonLoopingFile(final String file) {
	return SoundFactory
		.provisionMedia(new SoundFile(SoundFactory.MEDIA_GROUP, file, SoundFactory.ACTIVE_MEDIA_COUNT));
    }

    public static SoundFactory getNonLoopingResource(final URL resource) {
	return SoundFactory
		.provisionMedia(new SoundResource(SoundFactory.MEDIA_GROUP, resource, SoundFactory.ACTIVE_MEDIA_COUNT));
    }

    private static void killAllMediaPlayers() {
	SoundFactory.MEDIA_GROUP.interrupt();
    }

    private static SoundFactory provisionMedia(final SoundFactory src) {
	if (SoundFactory.ACTIVE_MEDIA_COUNT >= SoundFactory.MAX_MEDIA_ACTIVE) {
	    SoundFactory.killAllMediaPlayers();
	}
	try {
	    if (src != null) {
		src.setUncaughtExceptionHandler(SoundFactory.meh);
		SoundFactory.ACTIVE_MEDIA[SoundFactory.ACTIVE_MEDIA_COUNT] = src;
		SoundFactory.ACTIVE_MEDIA_COUNT++;
	    }
	} catch (final ArrayIndexOutOfBoundsException aioob) {
	    // Do nothing
	}
	return src;
    }

    static synchronized void taskCompleted(final int taskNum) {
	SoundFactory.ACTIVE_MEDIA[taskNum] = null;
	for (var z = taskNum + 1; z < SoundFactory.ACTIVE_MEDIA.length; z++) {
	    if (SoundFactory.ACTIVE_MEDIA[z] != null) {
		SoundFactory.ACTIVE_MEDIA[z - 1] = SoundFactory.ACTIVE_MEDIA[z];
		if (SoundFactory.ACTIVE_MEDIA[z - 1].isAlive()) {
		    SoundFactory.ACTIVE_MEDIA[z - 1].updateNumber(z - 1);
		}
	    }
	}
	SoundFactory.ACTIVE_MEDIA_COUNT--;
    }

    // Constructor
    protected SoundFactory(final ThreadGroup group) {
	super(group, "Media Player " + SoundFactory.ACTIVE_MEDIA_COUNT);
    }

    abstract int getNumber();

    // Methods
    public abstract void stopLoop();

    protected abstract void updateNumber(int newNumber);
}
