/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 *
 *	This file: HistoriyedLabelDialog.java
 *  Part of:   vna-j
 */

package krause.vna.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;

/**
 * @author Dietmar
 * 
 */
public class HistorizedLabelDialog extends KrauseDialog {

	private HistorizedLabelTable tblStatus;
	private JScrollPane tablePane;
	private JPanel pnlStatus;

	/**
	 * @param aFrame
	 * @param modal
	 */
	public HistorizedLabelDialog(Frame aFrame, List<HistorizedLabelEntry> data) {
		super(aFrame, true);
		TraceHelper.entry(this, "HistorizedLabelDialog");

		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, BorderLayout.SOUTH);

		JButton btOK = new JButton("OK");
		btOK.addActionListener(e -> doDialogCancel());
		pnlButton.add(btOK);

		pnlStatus = new JPanel();
		getContentPane().add(pnlStatus, BorderLayout.CENTER);
		//
		tblStatus = new HistorizedLabelTable(data);

		tablePane = new JScrollPane(tblStatus);
		tablePane.setPreferredSize(new Dimension(500, 300));
		tablePane.setMinimumSize(tablePane.getPreferredSize());
		tablePane.setAlignmentX(LEFT_ALIGNMENT);
		pnlStatus.add(tablePane);
		//
		doDialogInit();

		TraceHelper.exit(this, "HistorizedLabelDialog");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doCANCEL()
	 */
	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		showCenteredOnScreen();
		TraceHelper.exit(this, "doInit");
	}

}
