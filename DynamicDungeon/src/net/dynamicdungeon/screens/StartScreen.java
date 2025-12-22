package net.dynamicdungeon.screens;

//import java.awt.FileDialog;
//import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
//import java.io.File;
//import java.io.IOException;

//import net.dynamicdungeon.constants.Constants;
//import net.dynamicdungeon.fileio.FilenameChecker;
//import net.dynamicdungeon.fileio.XMLFileReader;
import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;
//import net.dynamicdungeon.world.World;

public class StartScreen implements Screen {
    @Override
    public void displayOutput(final GuiPanel terminal, final MessagePanel messages) {
	messages.clear();
	messages.write("Dynamic Dungeon");
	// messages.write("-- press [l] to load a saved game --");
	messages.write("-- press [enter] to start --");
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key, final MouseEvent mouse) {
	if (key != null) {
	    final var keyCode = key.getKeyCode();
	    if (keyCode == KeyEvent.VK_ENTER) {
		return new PlayScreen(true, null);
	    }
//	    if (keyCode == KeyEvent.VK_L) {
//		final FileDialog fd = new FileDialog((Frame) null, "Load Game", FileDialog.LOAD);
//		String filename, extension, file, dir;
//		fd.setVisible(true);
//		file = fd.getFile();
//		dir = fd.getDirectory();
//		if (file != null && dir != null) {
//		    filename = dir + file;
//		    extension = StartScreen.getExtension(filename);
//		    if (extension.equals(Constants.SAVE_FILE_EXTENSION)) {
//			if (FilenameChecker.isFilenameOK(
//				StartScreen.getNameWithoutExtension(StartScreen.getFileNameOnly(filename)))) {
//			    try {
//				World w = new World();
//				XMLFileReader reader = new XMLFileReader(filename, Constants.SAVE_FILE_DOC_TAG);
//				w.loadWorld(reader);
//				return new PlayScreen(true, w);
//			    } catch (IOException ioe) {
//				// Failed
//			    }
//			}
//		    }
//		}
//	    }
	}
	return this;
    }
//    private static String getExtension(final String s) {
//	String ext = null;
//	final int i = s.lastIndexOf('.');
//	if ((i > 0) && (i < s.length() - 1)) {
//	    ext = s.substring(i + 1).toLowerCase();
//	}
//	return ext;
//    }
//
//    private static String getNameWithoutExtension(final String s) {
//	String ext = null;
//	final int i = s.lastIndexOf('.');
//	if ((i > 0) && (i < s.length() - 1)) {
//	    ext = s.substring(0, i);
//	} else {
//	    ext = s;
//	}
//	return ext;
//    }
//
//    private static String getFileNameOnly(final String s) {
//	String fno = null;
//	final int i = s.lastIndexOf(File.separatorChar);
//	if ((i > 0) && (i < s.length() - 1)) {
//	    fno = s.substring(i + 1);
//	} else {
//	    fno = s;
//	}
//	return fno;
//    }
}
