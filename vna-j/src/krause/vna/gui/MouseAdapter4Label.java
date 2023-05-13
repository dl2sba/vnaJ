/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: MouseAdapter4Label.java
 *  Part of:   vna-j
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
public class MouseAdapter4Label extends MouseAdapter implements ClipboardOwner {
	private JLabel label = null;

	public MouseAdapter4Label(JLabel pLabel) {
		super();
		label = pLabel;
		label.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		String txt = label.getText();
		if (txt != null) {
			txt = txt.trim();
			if (txt.length() > 0) {
				if (e.isShiftDown()) {
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection str = new StringSelection(txt);
					cb.setContents(str, this);
				} else {
					JOptionPane.showMessageDialog(label, txt, VNAMessages.getString("VNAMainFrame.StatusPanel.status.title"), JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		} else {
			Toolkit.getDefaultToolkit().beep();
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
