/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: StatusBarLabel.java
 *  Part of:   krause-common
 */

package krause.vna.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import krause.util.ras.logging.TraceHelper;
import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar
 * 
 */
public class StatusBarLabel extends JLabel implements ClipboardOwner {
	private String fullText;
	private int maxLength;

	/**
	 * @param string
	 */
	public StatusBarLabel(String string, int cutLength) {
		maxLength = cutLength;
		setText(string);
		setOpaque(true);

		addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent
			 * )
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				TraceHelper.entry(this, "mouseClicked");
				handleMouseClicked(e);
				TraceHelper.exit(this, "mouseClicked");
			}
		});
	}

	/**
	 * @param e
	 */
	protected void handleMouseClicked(MouseEvent e) {
		TraceHelper.entry(this, "handleMouseClicked");
		String txt = getFullText();
		if (txt != null) {
			txt = txt.trim();
			if (txt.length() > 0) {
				if (e.isShiftDown()) {
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection str = new StringSelection(txt);
					cb.setContents(str, this);
				} else {
					JOptionPane.showMessageDialog(this, txt, VNAMessages.getString("VNAMainFrame.StatusPanel.status.title"), JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
		TraceHelper.exit(this, "handleMouseClicked");

	}

	public String getFullText() {
		return fullText;
	}

	@Override
	public void setText(String text) {
		fullText = text;
		if (text != null) {
			text = text.replace('\r', ' ');
			text = text.replace('\n', ' ');
			text = text.replace('\t', ' ');
			text = text.replace("<br/>", " ");
			if (text.length() > maxLength) {
				super.setText(text.substring(0, maxLength) + " ...");
			} else {
				super.setText(text);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer
	 * .Clipboard, java.awt.datatransfer.Transferable)
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		TraceHelper.entry(this, "lostOwnership");
		TraceHelper.exit(this, "lostOwnership");
	}
}
