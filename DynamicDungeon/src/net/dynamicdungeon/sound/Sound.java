package net.dynamicdungeon.sound;

public class Sound {
    public static void play(final String soundName) {
	SoundFactory.getNonLoopingResource(Sound.class.getResource("/assets/sounds/" + soundName + ".wav")).start();
    }
}
