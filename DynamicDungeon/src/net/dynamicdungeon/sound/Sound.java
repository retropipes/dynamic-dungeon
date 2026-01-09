package net.dynamicdungeon.sound;

import org.retropipes.diane.asset.sound.DianeSoundPlayer;

public class Sound {
    public static void play(final String soundName) {
	DianeSoundPlayer.playSource(Sound.class.getResource("/assets/sounds/" + soundName + ".wav"));
    }
}
