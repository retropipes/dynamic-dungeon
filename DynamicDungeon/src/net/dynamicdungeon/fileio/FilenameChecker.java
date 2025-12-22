package net.dynamicdungeon.fileio;

public class FilenameChecker {
    public static boolean isFilenameOK(final String filename) {
	if (filename.contains("/") || filename.contains("?") || filename.contains("<") || filename.contains(">")) {
	    return false;
	}
	if (filename.contains("\\") || filename.contains(":") || filename.contains("*") || filename.contains("|")) {
	    return false;
	}
	if (filename.contains("\"")) {
	    return false;
	}
	switch (filename) {
	case "con":
	    return false;
	case "nul":
	    return false;
	case "prn":
	    return false;
	default:
	    break;
	}
	if (filename.length() == 4 && filename.matches("com[1-9]")) {
	    return false;
	}
	if (filename.length() == 4 && filename.matches("lpt[1-9]")) {
	    return false;
	}
	return true;
    }

    // Private constructor
    private FilenameChecker() {
	// Do nothing
    }
}
