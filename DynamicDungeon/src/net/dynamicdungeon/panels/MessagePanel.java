package net.dynamicdungeon.panels;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * 
 * @author Eric Ahnell
 */
public class MessagePanel extends JPanel {
    private static final long serialVersionUID = 2L;
    private final JTextArea message = new JTextArea();
    private final JLabel stats = new JLabel();
    private final int full = 20;

    /**
     * Class constructor.
     */
    public MessagePanel() {
	super();
	this.message.setEditable(false);
	this.message.setFocusable(false);
	this.message.setBackground(this.getBackground());
	this.add(this.message);
    }

    public MessagePanel clear() {
	this.message.setText("");
	return this;
    }

    /**
     * Write a string to the message panel, appending to what's already there.
     * 
     * @param character
     *            the character to write
     * @return this for convenient chaining of method calls
     */
    public MessagePanel write(final String string) {
	if (this.isFull()) {
	    this.message.setText("");
	}
	String currText = this.message.getText();
	if (currText.isEmpty()) {
	    this.message.setText(string);
	} else {
	    this.message.setText(currText + "\n" + string);
	}
	return this;
    }

    public JLabel getStatsLabel() {
	return this.stats;
    }

    public void clearStats() {
	this.stats.setText("");
    }

    public void setStats(final String stats) {
	this.stats.setText(stats);
    }

    private boolean isFull() {
	return this.message.getText().split("\\n").length >= this.full;
    }
}