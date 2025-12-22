package net.dynamicdungeon.fileio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class XMLFileWriter implements AutoCloseable {
    private static final String END_OF_LINE = "\r\n";

    private static String replaceSpecialCharacters(final String s) {
	var r = s;
	r = r.replace("&", "&amp;");
	r = r.replace("<", "&lt;");
	r = r.replace(">", "&gt;");
	r = r.replace("\"", "&quot;");
	r = r.replace("\'", "&apos;");
	r = r.replace("\r", "");
	return r.replace("\n", "&#xA;");
    }

    // Fields
    private final BufferedWriter bw;
    private final String docTag;

    // Constructors
    public XMLFileWriter(final String filename, final String newDocTag) throws IOException {
	this.bw = new BufferedWriter(new FileWriter(filename));
	this.docTag = newDocTag;
	this.writeXHeader();
	this.writeOpeningDocTag();
    }

    // Methods
    @Override
    public void close() throws IOException {
	this.writeClosingDocTag();
	this.bw.close();
    }

    public void writeBoolean(final boolean b) throws IOException {
	this.bw.write("<" + XMLFileConstants.BOOLEAN_TAG + ">" + Boolean.toString(b) + "</"
		+ XMLFileConstants.BOOLEAN_TAG + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeByte(final byte b) throws IOException {
	this.bw.write("<" + XMLFileConstants.BYTE_TAG + ">" + Byte.toString(b) + "</" + XMLFileConstants.BYTE_TAG + ">"
		+ XMLFileWriter.END_OF_LINE);
    }

    private void writeClosingDocTag() throws IOException {
	this.bw.write("</" + this.docTag + ">");
    }

    public void writeClosingGroup(final String groupName) throws IOException {
	this.bw.write("</" + XMLFileWriter.replaceSpecialCharacters(groupName) + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeCustomBoolean(final boolean b, final String tag) throws IOException {
	this.bw.write("<" + tag + ">" + Boolean.toString(b) + "</" + tag + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeCustomByte(final byte b, final String tag) throws IOException {
	this.bw.write("<" + tag + ">" + Byte.toString(b) + "</" + tag + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeCustomDouble(final double d, final String tag) throws IOException {
	this.bw.write("<" + tag + ">" + Double.toString(d) + "</" + tag + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeCustomInt(final int i, final String tag) throws IOException {
	this.bw.write("<" + tag + ">" + Integer.toString(i) + "</" + tag + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeCustomLong(final long l, final String tag) throws IOException {
	this.bw.write("<" + tag + ">" + Long.toString(l) + "</" + tag + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeCustomString(final String s, final String tag) throws IOException {
	this.bw.write("<" + tag + ">" + XMLFileWriter.replaceSpecialCharacters(s) + "</" + tag + ">"
		+ XMLFileWriter.END_OF_LINE);
    }

    public void writeDouble(final double d) throws IOException {
	this.bw.write("<" + XMLFileConstants.DOUBLE_TAG + ">" + Double.toString(d) + "</" + XMLFileConstants.DOUBLE_TAG
		+ ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeInt(final int i) throws IOException {
	this.bw.write("<" + XMLFileConstants.INT_TAG + ">" + Integer.toString(i) + "</" + XMLFileConstants.INT_TAG + ">"
		+ XMLFileWriter.END_OF_LINE);
    }

    public void writeLong(final long l) throws IOException {
	this.bw.write("<" + XMLFileConstants.LONG_TAG + ">" + Long.toString(l) + "</" + XMLFileConstants.LONG_TAG + ">"
		+ XMLFileWriter.END_OF_LINE);
    }

    private void writeOpeningDocTag() throws IOException {
	this.bw.write("<" + this.docTag + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeOpeningGroup(final String groupName) throws IOException {
	this.bw.write("<" + XMLFileWriter.replaceSpecialCharacters(groupName) + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeString(final String s) throws IOException {
	this.bw.write("<" + XMLFileConstants.STRING_TAG + ">" + XMLFileWriter.replaceSpecialCharacters(s) + "</"
		+ XMLFileConstants.STRING_TAG + ">" + XMLFileWriter.END_OF_LINE);
    }

    private void writeXHeader() throws IOException {
	this.bw.write(XMLFileConstants.X_HEADER + XMLFileWriter.END_OF_LINE);
    }
}
