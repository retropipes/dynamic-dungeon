package net.dynamicdungeon.fileio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class XMLFileWriter implements AutoCloseable {
    // Fields
    private BufferedWriter bw;
    private String docTag;
    private static final String END_OF_LINE = "\r\n";

    // Constructors
    public XMLFileWriter(String filename, String newDocTag) throws IOException {
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

    public void writeDouble(double d) throws IOException {
	this.bw.write("<" + XMLFileConstants.DOUBLE_TAG + ">" + Double.toString(d) + "</" + XMLFileConstants.DOUBLE_TAG
		+ ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeChar(char c) throws IOException {
	this.bw.write("<" + XMLFileConstants.CHARACTER_TAG + ">" + Character.toString(c) + "</"
		+ XMLFileConstants.CHARACTER_TAG + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeInt(int i) throws IOException {
	this.bw.write("<" + XMLFileConstants.INT_TAG + ">" + Integer.toString(i) + "</" + XMLFileConstants.INT_TAG + ">"
		+ XMLFileWriter.END_OF_LINE);
    }

    public void writeLong(long l) throws IOException {
	this.bw.write("<" + XMLFileConstants.LONG_TAG + ">" + Long.toString(l) + "</" + XMLFileConstants.LONG_TAG + ">"
		+ XMLFileWriter.END_OF_LINE);
    }

    public void writeByte(byte b) throws IOException {
	this.bw.write("<" + XMLFileConstants.BYTE_TAG + ">" + Byte.toString(b) + "</" + XMLFileConstants.BYTE_TAG + ">"
		+ XMLFileWriter.END_OF_LINE);
    }

    public void writeBoolean(boolean b) throws IOException {
	this.bw.write("<" + XMLFileConstants.BOOLEAN_TAG + ">" + Boolean.toString(b) + "</"
		+ XMLFileConstants.BOOLEAN_TAG + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeString(String s) throws IOException {
	this.bw.write("<" + XMLFileConstants.STRING_TAG + ">" + XMLFileWriter.replaceSpecialCharacters(s) + "</"
		+ XMLFileConstants.STRING_TAG + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeOpeningGroup(String groupName) throws IOException {
	this.bw.write("<" + XMLFileWriter.replaceSpecialCharacters(groupName) + ">" + XMLFileWriter.END_OF_LINE);
    }

    public void writeClosingGroup(String groupName) throws IOException {
	this.bw.write("</" + XMLFileWriter.replaceSpecialCharacters(groupName) + ">" + XMLFileWriter.END_OF_LINE);
    }

    private void writeXHeader() throws IOException {
	this.bw.write(XMLFileConstants.X_HEADER + XMLFileWriter.END_OF_LINE);
    }

    private void writeOpeningDocTag() throws IOException {
	this.bw.write("<" + this.docTag + ">" + XMLFileWriter.END_OF_LINE);
    }

    private void writeClosingDocTag() throws IOException {
	this.bw.write("</" + this.docTag + ">");
    }

    private static String replaceSpecialCharacters(String s) {
	String r = s;
	r = r.replace("&", "&amp;");
	r = r.replace("<", "&lt;");
	r = r.replace(">", "&gt;");
	r = r.replace("\"", "&quot;");
	r = r.replace("\'", "&apos;");
	r = r.replace("\r", "");
	return r.replace("\n", "&#xA;");
    }
}
