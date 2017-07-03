package net.dynamicdungeon;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;
import net.dynamicdungeon.screens.Screen;
import net.dynamicdungeon.screens.StartScreen;

public class DynamicDungeon extends JFrame implements KeyListener, MouseListener {
    private static final long serialVersionUID = 1060623638149583738L;
    private final GuiPanel terminal;
    private final MessagePanel messages;
    private Screen screen;

    public DynamicDungeon() {
	super();
	this.terminal = new GuiPanel();
	this.messages = new MessagePanel();
	this.setLayout(new BorderLayout());
	this.add(this.terminal, BorderLayout.CENTER);
	this.add(this.messages, BorderLayout.SOUTH);
	this.add(this.messages.getStatsLabel(), BorderLayout.NORTH);
	this.pack();
	this.screen = new StartScreen();
	this.addKeyListener(this);
	this.addMouseListener(this);
	this.repaint();
	Music.play();
    }

    @Override
    public void repaint() {
	this.terminal.clear();
	this.screen.displayOutput(this.terminal, this.messages);
	super.repaint();
    }

    @Override
    public void keyPressed(final KeyEvent e) {
	if (!e.isMetaDown()) {
	    this.screen = this.screen.respondToUserInput(e, null);
	    this.pack();
	    this.repaint();
	}
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    public static void main(final String[] args) {
	final DynamicDungeon app = new DynamicDungeon();
	app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	app.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	this.screen = this.screen.respondToUserInput(null, e);
	this.pack();
	this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
	// Do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
	// Do nothing
    }

    @Override
    public void mouseEntered(MouseEvent e) {
	// Do nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
	// Do nothing
    }
}
