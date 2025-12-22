package net.dynamicdungeon.fileio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class XMLFileReader implements AutoCloseable {
    private static String replaceSpecialCharacters(final String s) {
	var r = s;
	r = r.replace("&amp;", "&");
	r = r.replace("&lt;", "<");
	r = r.replace("&gt;", ">");
	r = r.replace("&quot;", "\"");
	r = r.replace("&apos;", "\'");
	return r.replace("&#xA;", "\n");
    }

    private static String[] splitLine(final String line) {
	final var split = new String[3];
	final var loc0 = line.indexOf('>') + 1;
	final var loc2 = line.indexOf('<', loc0);
	split[0] = line.substring(0, loc0);
	split[1] = line.substring(loc0, loc2);
	split[2] = line.substring(loc2);
	return split;
    }

    private static void validateClosingTag(final String tag, final String tagType) throws IOException {
	if (!("</" + tagType + ">").equals(tag)) {
	    throw new UnexpectedTagException("Expected closing tag of </" + tagType + ">, found " + tag + "!");
	}
    }

    private static void validateOpeningTag(final String tag, final String tagType) throws IOException {
	if (!("<" + tagType + ">").equals(tag)) {
	    throw new UnexpectedTagException("Expected opening tag of <" + tagType + ">, found " + tag + "!");
	}
    }

    // Fields
    private final BufferedReader br;
    private final String docTag;

    public XMLFileReader(final InputStream stream, final String newDocTag) throws IOException {
	this.br = new BufferedReader(new InputStreamReader(stream));
	this.docTag = newDocTag;
	this.readXHeader();
	this.readOpeningDocTag();
    }

    // Constructors
    public XMLFileReader(final String filename, final String newDocTag) throws IOException {
	this.br = new BufferedReader(new FileReader(filename));
	this.docTag = newDocTag;
	this.readXHeader();
	this.readOpeningDocTag();
    }

    // Methods
    @Override
    public void close() throws IOException {
	this.readClosingDocTag();
	this.br.close();
    }

    public boolean readBoolean() throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], XMLFileConstants.BOOLEAN_TAG);
	    XMLFileReader.validateClosingTag(split[2], XMLFileConstants.BOOLEAN_TAG);
	    return Boolean.parseBoolean(split[1]);
	}
	throw new IOException("End of file!");
    }

    public byte readByte() throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], XMLFileConstants.BYTE_TAG);
	    XMLFileReader.validateClosingTag(split[2], XMLFileConstants.BYTE_TAG);
	    return Byte.parseByte(split[1]);
	}
	throw new IOException("End of file!");
    }

    private void readClosingDocTag() throws IOException {
	final var line = this.br.readLine();
	if (line != null && !("</" + this.docTag + ">").equals(line)) {
	    throw new UnexpectedTagException(
		    "Closing doc tag does not match: expected </" + this.docTag + ">, found " + line + "!");
	}
    }

    public void readClosingGroup(final String groupName) throws IOException {
	final var line = this.br.readLine();
	if (line == null) {
	    throw new IOException("End of file!");
	}
	XMLFileReader.validateClosingTag(XMLFileReader.replaceSpecialCharacters(line), groupName);
    }

    public boolean readCustomBoolean(final String tag) throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], tag);
	    XMLFileReader.validateClosingTag(split[2], tag);
	    return Boolean.parseBoolean(split[1]);
	}
	throw new IOException("End of file!");
    }

    public byte readCustomByte(final String tag) throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], tag);
	    XMLFileReader.validateClosingTag(split[2], tag);
	    return Byte.parseByte(split[1]);
	}
	throw new IOException("End of file!");
    }

    public double readCustomDouble(final String tag) throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], tag);
	    XMLFileReader.validateClosingTag(split[2], tag);
	    return Double.parseDouble(split[1]);
	}
	throw new IOException("End of file!");
    }

    public int readCustomInt(final String tag) throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], tag);
	    XMLFileReader.validateClosingTag(split[2], tag);
	    return Integer.parseInt(split[1]);
	}
	throw new IOException("End of file!");
    }

    public long readCustomLong(final String tag) throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], tag);
	    XMLFileReader.validateClosingTag(split[2], tag);
	    return Long.parseLong(split[1]);
	}
	throw new IOException("End of file!");
    }

    public String readCustomString(final String tag) throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], tag);
	    XMLFileReader.validateClosingTag(split[2], tag);
	    return XMLFileReader.replaceSpecialCharacters(split[1]);
	}
	throw new IOException("End of file!");
    }

    public double readDouble() throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], XMLFileConstants.DOUBLE_TAG);
	    XMLFileReader.validateClosingTag(split[2], XMLFileConstants.DOUBLE_TAG);
	    return Double.parseDouble(split[1]);
	}
	throw new IOException("End of file!");
    }

    public int readInt() throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], XMLFileConstants.INT_TAG);
	    XMLFileReader.validateClosingTag(split[2], XMLFileConstants.INT_TAG);
	    return Integer.parseInt(split[1]);
	}
	throw new IOException("End of file!");
    }

    public long readLong() throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], XMLFileConstants.LONG_TAG);
	    XMLFileReader.validateClosingTag(split[2], XMLFileConstants.LONG_TAG);
	    return Long.parseLong(split[1]);
	}
	throw new IOException("End of file!");
    }

    private void readOpeningDocTag() throws IOException {
	final var line = this.br.readLine();
	if (line != null && !("<" + this.docTag + ">").equals(line)) {
	    throw new UnexpectedTagException(
		    "Opening doc tag does not match: expected <" + this.docTag + ">, found " + line + "!");
	}
    }

    public void readOpeningGroup(final String groupName) throws IOException {
	final var line = this.br.readLine();
	if (line == null) {
	    throw new IOException("End of file!");
	}
	XMLFileReader.validateOpeningTag(XMLFileReader.replaceSpecialCharacters(line), groupName);
    }

    public String readString() throws IOException {
	final var line = this.br.readLine();
	if (line != null) {
	    final var split = XMLFileReader.splitLine(line);
	    XMLFileReader.validateOpeningTag(split[0], XMLFileConstants.STRING_TAG);
	    XMLFileReader.validateClosingTag(split[2], XMLFileConstants.STRING_TAG);
	    return XMLFileReader.replaceSpecialCharacters(split[1]);
	}
	throw new IOException("End of file!");
    }

    private void readXHeader() throws IOException {
	final var header = this.br.readLine();
	if (header == null || !XMLFileConstants.X_HEADER.equals(header)) {
	    throw new UnexpectedTagException("Corrupt or invalid header!");
	}
    }
}
