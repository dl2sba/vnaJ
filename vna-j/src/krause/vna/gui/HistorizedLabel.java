/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: VNAHistorizedStatusLabel.java
 *  Part of:   vna-j
 */

package krause.vna.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JLabel;

import krause.util.ras.logging.TraceHelper;

/**
 * @author Dietmar
 * 
 */
public class HistorizedLabel extends JLabel implements MouseListener {

	private int maxHistorySize = 100;
	private transient LinkedList<HistorizedLabelEntry> history = null;

	/**
	 * @param string
	 */
	public HistorizedLabel(String string) {
		super(string);
		TraceHelper.entry(this, "VNAHistorizedStatusLabel");
		addMouseListener(this);
		TraceHelper.exit(this, "VNAHistorizedStatusLabel");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		TraceHelper.entry(this, "mouseClicked");
		new HistorizedLabelDialog(null, history);
		TraceHelper.exit(this, "mouseClicked");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		TraceHelper.entry(this, "mouseEntered");
		TraceHelper.exit(this, "mouseEntered");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		TraceHelper.entry(this, "mouseExited");
		TraceHelper.exit(this, "mouseExited");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		TraceHelper.entry(this, "mousePressed");
		TraceHelper.exit(this, "mousePressed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		TraceHelper.entry(this, "mouseReleased");
		TraceHelper.exit(this, "mouseReleased");
	}

	@Override
	public void setText(String text) {
		if (text != null) {
			text = text.trim();
			if (text.length() > 0) {
				super.setText(text);
				HistorizedLabelEntry newEntry = new HistorizedLabelEntry(text, System.currentTimeMillis());
				if (history == null) {
					history = new LinkedList<>();
				}
				history.addFirst(newEntry);
				if (history.size() > maxHistorySize) {
					history.removeLast();
				}
			}
		}
	}
}
