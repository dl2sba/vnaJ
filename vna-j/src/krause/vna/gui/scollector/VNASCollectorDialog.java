/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNASCollectorDialog.java
 *  Part of:   vna-j
 */

package krause.vna.gui.scollector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.importers.SnPRecord;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * @author Dietmar
 * 
 */
public class VNASCollectorDialog extends KrauseDialog implements ActionListener {
	public final static int S11 = 0;
	public final static int S21 = 1;
	public final static int S12 = 2;
	public final static int S22 = 3;
	private JTextField txtStart;
	private JTextField txtStop;
	private JTextField txtSteps;
	private JButton buttonAddS11;
	private JButton buttonAddS12;
	private JButton buttonAddS21;
	private JButton buttonAddS22;
	private JButton buttonDeleteS11;
	private JButton buttonDeleteS12;
	private JButton buttonDeleteS21;
	private JButton buttonDeleteS22;
	private JButton buttonSave;
	private JButton buttonClose;

	private VNACalibratedSampleBlock s11;
	private VNACalibratedSampleBlock s21;
	private VNACalibratedSampleBlock s12;
	private VNACalibratedSampleBlock s22;

	private long startFreq = -1;
	private long stopFreq = -1;
	private int numSamples = -1;

	private VNADataPool datapool = VNADataPool.getSingleton();

	/**
	 * @param aFrame
	 * @param modal
	 */
	public VNASCollectorDialog() {
		super(null, false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		TraceHelper.entry(this, "VNASCollectorDialog");
		setProperties(VNAConfig.getSingleton());
		setConfigurationPrefix(getClass().getSimpleName());

		setTitle(VNAMessages.getString("VNASCollectorDialog.title"));

		JPanel panel = new JPanel();
		panel.setBorder(null);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][grow,center][]", ""));

		// s parameters
		JPanel pnlContent = new JPanel();
		pnlContent.setBorder(new TitledBorder(null, VNAMessages.getString("VNASCollectorDialog.lblScanParameters"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlContent.setLayout(new MigLayout("", "[][grow]", "[][][]"));
		panel.add(pnlContent, "span 3, grow, wrap");

		JLabel lblStartFrequency = new JLabel(VNAMessages.getString("VNASCollectorDialog.lblStartFrequency.text")); //$NON-NLS-1$
		pnlContent.add(lblStartFrequency, "cell 0 0,alignx trailing,aligny top");

		txtStart = new JTextField();
		txtStart.setHorizontalAlignment(SwingConstants.RIGHT);
		txtStart.setEditable(false);
		pnlContent.add(txtStart, "cell 1 0,growx");
		txtStart.setColumns(10);

		JLabel lblStopFrequency = new JLabel(VNAMessages.getString("VNASCollectorDialog.lblStopFrequency.text")); //$NON-NLS-1$
		pnlContent.add(lblStopFrequency, "cell 0 1,alignx trailing");

		txtStop = new JTextField();
		txtStop.setHorizontalAlignment(SwingConstants.RIGHT);
		txtStop.setEditable(false);
		pnlContent.add(txtStop, "cell 1 1,growx");
		txtStop.setColumns(10);

		JLabel lblOfSteps = new JLabel(VNAMessages.getString("VNASCollectorDialog.lblOfSteps.text")); //$NON-NLS-1$
		pnlContent.add(lblOfSteps, "cell 0 2,alignx trailing");

		txtSteps = new JTextField();
		txtSteps.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSteps.setEditable(false);
		pnlContent.add(txtSteps, "cell 1 2,growx");
		txtSteps.setColumns(10);

		// add/remove
		panel.add(new JLabel(), "");
		JPanel panel_3 = new JPanel();
		panel_3.setLayout(new MigLayout("", "[]", "[]"));
		panel_3.add(buttonAddS21 = SwingUtil.createToolbarButton("Button.IconSParm.Add", this), "");
		panel_3.add(buttonDeleteS21 = SwingUtil.createToolbarButton("Button.IconSParm.Delete", this), "");
		panel.add(panel_3, "");
		panel.add(new JLabel(), "wrap");

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new MigLayout("", "[]", "[]"));
		panel_1.add(buttonAddS11 = SwingUtil.createToolbarButton("Button.IconSParm.Add", this), "wrap");
		panel_1.add(buttonDeleteS11 = SwingUtil.createToolbarButton("Button.IconSParm.Delete", this), "");
		panel.add(panel_1, "");

		URL url = getClass().getResource("/images/s-parameters.jpg");
		panel.add(new JLabel(new ImageIcon(url)), "");

		JPanel panel_4 = new JPanel();
		panel_4.setLayout(new MigLayout("", "[]", "[]"));
		panel_4.add(buttonAddS22 = SwingUtil.createToolbarButton("Button.IconSParm.Add", this), "wrap");
		panel_4.add(buttonDeleteS22 = SwingUtil.createToolbarButton("Button.IconSParm.Delete", this), "");
		panel.add(panel_4, "wrap");

		panel.add(new JLabel(), "");
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new MigLayout("", "[]", "[]"));
		panel_2.add(buttonAddS12 = SwingUtil.createToolbarButton("Button.IconSParm.Add", this), "");
		panel_2.add(buttonDeleteS12 = SwingUtil.createToolbarButton("Button.IconSParm.Delete", this), "");
		panel.add(panel_2, "");
		panel.add(new JLabel(), "wrap");

		// Buttons
		JPanel pnlButtons = new JPanel();
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);

		buttonSave = SwingUtil.createJButton("Button.Save", this);
		pnlButtons.add(buttonSave);

		pnlButtons.add(new HelpButton(this, getClass().getSimpleName()));

		buttonClose = SwingUtil.createJButton("Button.Close", this);
		pnlButtons.add(buttonClose);

		doDialogInit();

		TraceHelper.exit(this, "VNASCollectorDialog");
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
		setPreferredSize(new Dimension(400, 400));

		updateInfo();

		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		Object o = e.getSource();

		if (o == buttonClose) {
			doDialogCancel();
		} else if (o == buttonSave) {
			doSave();
		} else if (o == buttonAddS11) {
			doAddS11();
		} else if (o == buttonAddS12) {
			doAddS12();
		} else if (o == buttonAddS21) {
			doAddS21();
		} else if (o == buttonAddS22) {
			doAddS22();
		} else if (o == buttonDeleteS11) {
			doDeleteS11();
		} else if (o == buttonDeleteS12) {
			doDeleteS12();
		} else if (o == buttonDeleteS21) {
			doDeleteS21();
		} else if (o == buttonDeleteS22) {
			doDeleteS22();
		}
		TraceHelper.exit(this, "actionPerformed");

	}

	/**
	 * 
	 */
	private void doSave() {
		TraceHelper.entry(this, "doSave");
		SnPRecord sRecords[] = new SnPRecord[numSamples];

		// init records
		for (int i = 0; i < numSamples; ++i) {
			sRecords[i] = new SnPRecord();
		}

		// s11 data set?
		if (s11 != null) {
			VNACalibratedSample[] samples = s11.getCalibratedSamples();
			for (int i = 0; i < numSamples; ++i) {
				SnPRecord rec = sRecords[i];
				VNACalibratedSample sample = samples[i];
				rec.setFrequency(sample.getFrequency());
				rec.setLoss(S11, sample.getReflectionLoss());
				rec.setPhase(S11, sample.getReflectionPhase());
			}
		}

		if (s21 != null) {
			VNACalibratedSample[] samples = s21.getCalibratedSamples();
			for (int i = 0; i < numSamples; ++i) {
				SnPRecord rec = sRecords[i];
				VNACalibratedSample sample = samples[i];
				rec.setFrequency(sample.getFrequency());
				rec.setLoss(S21, sample.getTransmissionLoss());
				rec.setPhase(S21, sample.getTransmissionPhase());
			}
		}

		if (s12 != null) {
			VNACalibratedSample[] samples = s12.getCalibratedSamples();
			for (int i = 0; i < numSamples; ++i) {
				SnPRecord rec = sRecords[i];
				VNACalibratedSample sample = samples[i];
				rec.setFrequency(sample.getFrequency());
				rec.setLoss(S12, sample.getTransmissionLoss());
				rec.setPhase(S12, sample.getTransmissionPhase());
			}
		}

		if (s22 != null) {
			VNACalibratedSample[] samples = s22.getCalibratedSamples();
			for (int i = 0; i < numSamples; ++i) {
				SnPRecord rec = sRecords[i];
				VNACalibratedSample sample = samples[i];
				rec.setFrequency(sample.getFrequency());
				rec.setLoss(S22, sample.getReflectionLoss());
				rec.setPhase(S22, sample.getReflectionPhase());
			}
		}

		// all data put into record
		// now export to file
		new VNASnPExportDialog(this, sRecords);

		TraceHelper.exit(this, "doSave");
	}

	/**
	 * 
	 */
	private void doDeleteS22() {
		TraceHelper.entry(this, "doDeleteS22");
		s22 = null;
		updateInfo();
		TraceHelper.exit(this, "doDeleteS22");
	}

	/**
	 * 
	 */
	private void doDeleteS21() {
		TraceHelper.entry(this, "doDeleteS21");
		s21 = null;
		updateInfo();
		TraceHelper.exit(this, "doDeleteS21");
	}

	/**
	 * 
	 */
	private void doDeleteS12() {
		TraceHelper.entry(this, "doDeleteS12");
		s12 = null;
		updateInfo();
		TraceHelper.exit(this, "doDeleteS12");
	}

	/**
	 * 
	 */
	private void doDeleteS11() {
		TraceHelper.entry(this, "doDeleteS11");
		s11 = null;
		updateInfo();
		TraceHelper.exit(this, "doDeleteS11");
	}

	/**
	 * 
	 */
	private void doAddS22() {
		TraceHelper.entry(this, "doAddS22");
		VNACalibratedSampleBlock data = datapool.getCalibratedData();
		if (data != null) {
			VNACalibratedSample[] samples = data.getCalibratedSamples();
			if (matchesSamples(samples)) {
				s22 = data;
			} else {
				JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.NotMatching"), getTitle(), JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.Missing"), getTitle(), JOptionPane.ERROR_MESSAGE);
		}

		updateInfo();
		TraceHelper.exit(this, "doAddS22");
	}

	/**
	 * 
	 */
	private void doAddS21() {
		TraceHelper.entry(this, "doAddS21");
		VNACalibratedSampleBlock data = datapool.getCalibratedData();
		if (data != null) {
			VNACalibratedSample[] samples = data.getCalibratedSamples();
			if (matchesSamples(samples)) {
				s21 = data;
			} else {
				JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.NotMatching"), getTitle(), JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.Missing"), getTitle(), JOptionPane.ERROR_MESSAGE);
		}

		updateInfo();
		TraceHelper.exit(this, "doAddS21");
	}

	/**
	 * 
	 */
	private void doAddS12() {
		TraceHelper.entry(this, "doAddS12");
		VNACalibratedSampleBlock data = datapool.getCalibratedData();
		if (data != null) {
			VNACalibratedSample[] samples = data.getCalibratedSamples();
			if (matchesSamples(samples)) {
				s12 = data;
			} else {
				JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.NotMatching"), getTitle(), JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.Missing"), getTitle(), JOptionPane.ERROR_MESSAGE);
		}

		updateInfo();
		TraceHelper.exit(this, "doAddS12");
	}

	/**
	 * 
	 */
	private void doAddS11() {
		TraceHelper.entry(this, "doAddS11");
		VNACalibratedSampleBlock data = datapool.getCalibratedData();
		if (data != null) {
			VNACalibratedSample[] samples = data.getCalibratedSamples();
			if (matchesSamples(samples)) {
				s11 = data;
			} else {
				JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.NotMatching"), getTitle(), JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASCollectorDialog.Missing"), getTitle(), JOptionPane.ERROR_MESSAGE);
		}

		updateInfo();

		TraceHelper.exit(this, "doAddS11");
	}

	/**
	 * 
	 */
	private void updateInfo() {
		TraceHelper.entry(this, "updateInfo");
		buttonDeleteS11.setEnabled(s11 != null);
		buttonDeleteS12.setEnabled(s12 != null);
		buttonDeleteS21.setEnabled(s21 != null);
		buttonDeleteS22.setEnabled(s22 != null);

		if (s11 == null && s12 == null && s21 == null && s22 == null) {
			startFreq = -1;
			stopFreq = -1;
			numSamples = -1;
			txtStart.setText("");
			txtStop.setText("");
			txtSteps.setText("");
			buttonSave.setEnabled(false);
		} else {
			txtStart.setText(VNAFormatFactory.getFrequencyFormat().format(startFreq));
			txtStop.setText(VNAFormatFactory.getFrequencyFormat().format(stopFreq));
			txtSteps.setText(VNAFormatFactory.getFrequencyFormat().format(numSamples));
			buttonSave.setEnabled(true);
		}
		TraceHelper.exit(this, "updateInfo");
	}

	/**
	 * @param samples
	 * @return
	 */
	private boolean matchesSamples(VNACalibratedSample[] samples) {
		boolean rc = false;
		TraceHelper.entry(this, "matchesSamples");

		if (startFreq == -1 && stopFreq == -1 && numSamples == -1) {
			numSamples = samples.length;
			startFreq = samples[0].getFrequency();
			stopFreq = samples[numSamples - 1].getFrequency();

			rc = true;
		} else {
			rc = (numSamples == samples.length) && (startFreq == samples[0].getFrequency());
		}

		TraceHelper.exit(this, "matchesSamples");
		return rc;
	}
}
