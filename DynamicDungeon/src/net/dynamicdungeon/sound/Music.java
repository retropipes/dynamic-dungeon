package net.dynamicdungeon.sound;

import org.retropipes.diane.asset.ogg.DianeOggPlayer;

public class Music {
    public static void play() {
	DianeOggPlayer.loadLoopedResource(Music.class.getResource("/assets/music/dungeon.ogg")).play();
    }

    public void stopPlaying() {
	DianeOggPlayer.stopPlaying();
    }
}