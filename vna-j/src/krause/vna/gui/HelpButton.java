/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *	This file: HelpButton.java
 *  Part of:   krause-common
 */

package krause.vna.gui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.help.VNAHelpDialog;
import krause.vna.resources.VNAMessages;

/**
 * @author de049434
 * 
 */
public class HelpButton extends JButton {
	private String helpID;

	/**
	 * 
	 * @param pHelpID
	 * @param pOwner
	 */
	public HelpButton(final Frame owner, String pHelpID) {
		super(VNAMessages.getString("Button.Help"));

		setToolTipText(VNAMessages.getString("Button.Help.Tooltip"));
		setActionCommand(VNAMessages.getString("Button.Help.Command"));
		setMnemonic(VNAMessages.getString("Button.Help.Key").charAt(0));
		setHelpID(pHelpID);

		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				new VNAHelpDialog(owner, getHelpID());
				TraceHelper.exit(this, "actionPerformed");
			}
		});
	}

	/**
	 * 
	 * @param pHelpID
	 * @param pOwner
	 */
	public HelpButton(final Dialog owner, String pHelpID) {
		super(VNAMessages.getString("Button.Help"));
		setHelpID(pHelpID);

		setToolTipText(VNAMessages.getString("Button.Help.Tooltip"));
		setActionCommand(VNAMessages.getString("Button.Help.Command"));
		setMnemonic(VNAMessages.getString("Button.Help.Key").charAt(0));

		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				new VNAHelpDialog(owner, getHelpID());
				TraceHelper.exit(this, "actionPerformed");
			}
		});
	}

	public String getHelpID() {
		return helpID;
	}

	public void setHelpID(String helpID) {
		this.helpID = helpID;
	}

}
