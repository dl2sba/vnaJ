/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNAPadCalculator.java
 *  Part of:   vna-j
 */

package krause.vna.gui.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * @author Dietmar
 * 
 */
public class VNAConfigLanguageDialog extends KrauseDialog {
	private VNAConfig config = VNAConfig.getSingleton();

	private JRadioButton rbCS;
	private JRadioButton rbDE;
	private JRadioButton rbEN;
	private JRadioButton rbES;
	private JRadioButton rbFR;
	private JRadioButton rbHU;
	private JRadioButton rbIT;
	private JRadioButton rbJP;
	private JRadioButton rbNL;
	private JRadioButton rbPL;
	private JRadioButton rbSE;
	private JRadioButton rbRU;
	private JRadioButton rbSYS;

	/**
	 * @param aFrame
	 * @param modal
	 */
	public VNAConfigLanguageDialog(Frame aFrame) {
		super(aFrame, true);
		TraceHelper.entry(this, "VNAConfigLanguageDialog");

		setTitle(VNAMessages.getString("VNAConfigLanguageDialog.title"));
		setProperties(config);
		setConfigurationPrefix("VNAConfigLanguageDialog");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		setPreferredSize(new Dimension(300, 220));

		JPanel pnlButtons = new JPanel();
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);

		pnlButtons.add(new HelpButton(this, "VNAConfigLanguageDialog"));

		JButton btCancel = SwingUtil.createJButton("Button.Cancel", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDialogCancel();
			}
		});
		pnlButtons.add(btCancel);

		JButton btOK = SwingUtil.createJButton("Button.OK", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doOK();
			}
		});
		pnlButtons.add(btOK);

		JPanel pnlCenter = new JPanel();
		getContentPane().add(pnlCenter, BorderLayout.CENTER);
		pnlCenter.setLayout(new MigLayout("", "", ""));

		rbSYS = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbSYS")); //$NON-NLS-1$
		pnlCenter.add(rbSYS, "wrap");

		rbCS = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbCS")); //$NON-NLS-1$
		pnlCenter.add(rbCS, "");

		rbDE = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbDE")); //$NON-NLS-1$
		pnlCenter.add(rbDE, "");

		rbEN = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbEN")); //$NON-NLS-1$
		pnlCenter.add(rbEN, "wrap");

		rbHU = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbHU")); //$NON-NLS-1$
		pnlCenter.add(rbHU, "");

		rbPL = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbPL")); //$NON-NLS-1$
		pnlCenter.add(rbPL, "");

		rbSE = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbSV")); //$NON-NLS-1$
		pnlCenter.add(rbSE, "wrap");

		rbIT = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbIT")); //$NON-NLS-1$
		pnlCenter.add(rbIT, "");

		rbES = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbES")); //$NON-NLS-1$
		pnlCenter.add(rbES, "");

		rbNL = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbNL")); //$NON-NLS-1$
		pnlCenter.add(rbNL, "wrap");

		rbFR = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbFR")); //$NON-NLS-1$
		pnlCenter.add(rbFR, "");

		rbJP = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbJP")); //$NON-NLS-1$
		pnlCenter.add(rbJP, "");

		rbRU = new JRadioButton(VNAMessages.getString("VNAConfigLanguageDialog.rbRU")); //$NON-NLS-1$
		pnlCenter.add(rbRU, "");

		ButtonGroup bg = new ButtonGroup();
		bg.add(rbDE);
		bg.add(rbEN);
		bg.add(rbHU);
		bg.add(rbPL);
		bg.add(rbSE);
		bg.add(rbSYS);
		bg.add(rbCS);
		bg.add(rbIT);
		bg.add(rbES);
		bg.add(rbNL);
		bg.add(rbFR);
		bg.add(rbJP);
		bg.add(rbRU);

		//
		doDialogInit();
		TraceHelper.exit(this, "VNAConfigLanguageDialog");
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

		Locale loc = config.getLocale();
		rbSYS.setSelected(loc == null);
		rbDE.setSelected(new Locale("de", "DE").equals(loc));
		rbEN.setSelected(new Locale("en", "US").equals(loc));
		rbHU.setSelected(new Locale("hu", "HU").equals(loc));
		rbIT.setSelected(new Locale("it", "IT").equals(loc));
		rbPL.setSelected(new Locale("pl", "PL").equals(loc));
		rbSE.setSelected(new Locale("sv", "SE").equals(loc));
		rbNL.setSelected(new Locale("nl", "NL").equals(loc));
		rbES.setSelected(new Locale("es", "ES").equals(loc));
		rbCS.setSelected(new Locale("cs", "CZ").equals(loc));
		rbFR.setSelected(new Locale("fr", "FR").equals(loc));
		rbJP.setSelected(new Locale("ja", "JP").equals(loc));
		rbRU.setSelected(new Locale("ru", "RUS").equals(loc));

		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	/**
	 * 
	 */
	private void doOK() {
		TraceHelper.entry(this, "doOK");
		Locale loc = null;

		if (rbDE.isSelected())
			loc = new Locale("de", "DE");
		else if (rbEN.isSelected())
			loc = new Locale("en", "US");
		else if (rbHU.isSelected())
			loc = new Locale("hu", "HU");
		else if (rbIT.isSelected())
			loc = new Locale("it", "IT");
		else if (rbPL.isSelected())
			loc = new Locale("pl", "PL");
		else if (rbSE.isSelected())
			loc = new Locale("sv", "SE");
		else if (rbNL.isSelected())
			loc = new Locale("nl", "NL");
		else if (rbES.isSelected())
			loc = new Locale("es", "ES");
		else if (rbCS.isSelected())
			loc = new Locale("cs", "CZ");
		else if (rbFR.isSelected())
			loc = new Locale("fr", "FR");
		else if (rbJP.isSelected())
			loc = new Locale("ja", "JP");
		else if (rbRU.isSelected())
			loc = new Locale("ru", "RUS");

		JOptionPane.showMessageDialog(this, VNAMessages.getString("VNAConfigLanguageDialog.msg.1"), getTitle(), JOptionPane.INFORMATION_MESSAGE);

		config.setLocale(loc);
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doOK");
	}
}
