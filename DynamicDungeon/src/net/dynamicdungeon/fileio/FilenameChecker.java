package net.dynamicdungeon.fileio;

public class FilenameChecker {
    // Private constructor
    private FilenameChecker() {
	// Do nothing
    }

    public static boolean isFilenameOK(final String filename) {
	if (filename.contains("/")) {
	    return false;
	}
	if (filename.contains("?")) {
	    return false;
	}
	if (filename.contains("<")) {
	    return false;
	}
	if (filename.contains(">")) {
	    return false;
	}
	if (filename.contains("\\")) {
	    return false;
	}
	if (filename.contains(":")) {
	    return false;
	}
	if (filename.contains("*")) {
	    return false;
	}
	if (filename.contains("|")) {
	    return false;
	}
	if (filename.contains("\"")) {
	    return false;
	}
	if (filename.equals("con")) {
	    return false;
	}
	if (filename.equals("nul")) {
	    return false;
	}
	if (filename.equals("prn")) {
	    return false;
	}
	if (filename.length() == 4 && filename.matches("com[1-9]")) {
	    return false;
	}
	if (filename.length() == 4 && filename.matches("lpt[1-9]")) {
	    return false;
	}
	return true;
    }
}
