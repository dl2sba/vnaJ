/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNASnPImportDialog.java
 *  Part of:   vna-j
 */

package krause.vna.gui.importers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.util.SwingUtil;
import krause.vna.importers.SnPImporter;
import krause.vna.importers.SnPInfoBlock;
import krause.vna.importers.SnPInfoBlock.FORMAT;
import krause.vna.importers.SnPInfoBlock.PARAMETER;
import krause.vna.importers.SnPRecord;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * @author Dietmar
 * 
 */
public class VNASnPImportDialog extends KrauseDialog {

	private JButton btOK;
	private JTextField txtFilename;
	private String filename;
	private VNASnPDataTable lstData;
	private SnPImporter importer;
	private SnPInfoBlock infoBlock;
	private JComboBox cbRL;
	private JComboBox cbTL;
	private JComboBox cbRP;
	private VNACalibratedSampleBlock csb = null;
	private JTextField txtFormat;
	private JTextField txtParameter;
	private JTextField txtReference;
	private JComboBox cbTP;

	/**
	 * 
	 * @param aFrame
	 */
	@SuppressWarnings("unchecked")
	public VNASnPImportDialog(Window aFrame, String pFilename) {
		super(aFrame, true);
		TraceHelper.entry(this, "VNASnPImportDialog");

		filename = pFilename;

		importer = new SnPImporter();

		setProperties(VNAConfig.getSingleton());
		setConfigurationPrefix("VNASnPImportDialog");

		setTitle(VNAMessages.getString("VNASnPImportDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(900, 600));
		setLayout(new MigLayout("", "[][][][][][grow,fill]", ""));

		//
		add(new JLabel(VNAMessages.getString("VNASnPImportDialog.headline")), "span 6,grow,wrap");
		add(new JLabel(VNAMessages.getString("VNASnPImportDialog.lblFN")), "");
		txtFilename = new JTextField(filename);
		txtFilename.setEditable(false);
		add(txtFilename, "span 5,grow,wrap");

		//
		add(new JLabel(VNAMessages.getString("VNASnPImportDialog.lblFormat")), "");
		txtFormat = new JTextField();
		txtFormat.setColumns(10);
		txtFormat.setEditable(false);
		add(txtFormat, "");

		//
		add(new JLabel(VNAMessages.getString("VNASnPImportDialog.lblReference")), "");
		txtReference = new JTextField();
		txtReference.setColumns(10);
		txtReference.setEditable(false);
		add(txtReference, "");

		//
		add(new JLabel(VNAMessages.getString("VNASnPImportDialog.lblParameter")), "");
		txtParameter = new JTextField();
		txtParameter.setEditable(false);
		add(txtParameter, "grow,wrap");

		//
		lstData = new VNASnPDataTable();
		JScrollPane scrollPane = new JScrollPane(lstData);
		scrollPane.setViewportBorder(null);
		add(scrollPane, "span 6,grow,wrap");

		//
		JPanel pnl1 = new JPanel(new MigLayout("", "[][10%][][10%][][10%][][10%][grow,fill]", ""));
		pnl1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNASnPImportDialog.lblAssign"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		pnl1.add(new JLabel(VNAMessages.getString("VNASnPImportDialog.lblAssign2")), "span 8, wrap");
		pnl1.add(new JLabel(VNAMessages.getString("Marker.RL") + " - "), "");
		cbRL = new JComboBox(new String[] {
				"",
				"S11",
				"S21",
				"S12",
				"S22"
		});
		pnl1.add(cbRL, "left");

		pnl1.add(new JLabel(VNAMessages.getString("Marker.PhaseRL") + " - "), "");
		cbRP = new JComboBox(new String[] {
				"",
				"S11",
				"S21",
				"S12",
				"S22"
		});
		pnl1.add(cbRP, "left");

		pnl1.add(new JLabel(VNAMessages.getString("Marker.TL") + " - "), "");
		cbTL = new JComboBox(new String[] {
				"",
				"S11",
				"S21",
				"S12",
				"S22"
		});
		pnl1.add(cbTL, "left");

		pnl1.add(new JLabel(VNAMessages.getString("Marker.PhaseTL") + " - "), "");
		cbTP = new JComboBox(new String[] {
				"",
				"S11",
				"S21",
				"S12",
				"S22"
		});
		pnl1.add(cbTP, "left");

		add(pnl1, "grow, wrap, span 6");

		//
		add(SwingUtil.createJButton("Button.Cancel", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDialogCancel();
			}
		}), "center,span 2");

		//
		add(new HelpButton(this, "VNASnPImportDialog"), "");
		//
		btOK = SwingUtil.createJButton("Button.Load", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doLoad();
			}
		});
		add(btOK, "right, span 3");

		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();

		TraceHelper.exit(this, "VNASnPImportDialog");
	}

	/**
	 * 
	 */
	protected void doReadFile() {
		TraceHelper.entry(this, "doReadFile");
		try {
			infoBlock = importer.readFile(filename, "US-ASCII");
			VNASnPDataTableModel model = lstData.getModel();
			model.getData().clear();
			model.getData().addAll(infoBlock.getRecords());
			model.fireTableDataChanged();

			//
			analyseData();
		} catch (ProcessingException e) {
			ErrorLogHelper.exception(this, "doReadFile", e);
		}
		TraceHelper.exit(this, "doReadFile");

	}

	/**
	 * 
	 */
	private void analyseData() {
		TraceHelper.entry(this, "analyseData");
		List<SnPRecord> records = infoBlock.getRecords();

		if (records.size() > 0) {
			boolean hasLossData[] = {
					false,
					false,
					false,
					false
			};
			boolean hasPhaseData[] = {
					false,
					false,
					false,
					false
			};

			for (SnPRecord record : records) {
				for (int i = 0; i < 4; ++i) {
					double loss = record.getLoss()[i];
					if (!Double.isNaN(loss) && loss != 0.0) {
						hasLossData[i] = true;
					}
					double phase = record.getPhase()[i];
					if (!Double.isNaN(phase) && phase != 0.0) {
						hasPhaseData[i] = true;
					}
				}

			}

			if (hasLossData[0] && hasPhaseData[0] && !hasLossData[1] && !hasPhaseData[1] && !(hasLossData[2] && !hasPhaseData[2]) && !hasLossData[3] && !hasPhaseData[3]) {
				cbRL.setSelectedIndex(1);
				cbTL.setSelectedIndex(0);
				cbRP.setSelectedIndex(1);
			} else if (!hasLossData[0] && !hasPhaseData[0] && hasLossData[1] && hasPhaseData[1] && !(hasLossData[2] && !hasPhaseData[2]) && !hasLossData[3] && !hasPhaseData[3]) {
				cbRL.setSelectedIndex(0);
				cbTL.setSelectedIndex(2);
				cbRP.setSelectedIndex(2);
			} else if (hasLossData[0] && hasPhaseData[0] && hasLossData[1] && hasPhaseData[1] && !(hasLossData[2] && !hasPhaseData[2]) && !hasLossData[3] && !hasPhaseData[3]) {
				cbRL.setSelectedIndex(1);
				cbTL.setSelectedIndex(2);
				cbRP.setSelectedIndex(1);
			}

			if (infoBlock.getFormat() != FORMAT.DB || infoBlock.getParameter() != PARAMETER.S) {
				JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASnPImportDialog.notSupportedFormat"), getTitle(), JOptionPane.ERROR_MESSAGE);
				btOK.setEnabled(false);
			}
		} else {
			JOptionPane.showMessageDialog(this, VNAMessages.getString("VNASnPImportDialog.notDataFound"), getTitle(), JOptionPane.ERROR_MESSAGE);
			btOK.setEnabled(false);
		}
		TraceHelper.exit(this, "analyseData");
	}

	/**
	 * 
	 */
	protected void doLoad() {
		TraceHelper.entry(this, "doOK");
		//
		List<SnPRecord> records = infoBlock.getRecords();

		csb = new VNACalibratedSampleBlock(records.size());
		csb.setFile(new File(filename));

		int rpIndex = cbRP.getSelectedIndex();
		int tpIndex = cbTP.getSelectedIndex();
		int rlIndex = cbRL.getSelectedIndex();
		int tlIndex = cbTL.getSelectedIndex();

		int index = 0;
		for (SnPRecord record : records) {
			VNACalibratedSample cs = new VNACalibratedSample();
			cs.setFrequency(record.getFrequency());

			if (rpIndex != 0) {
				cs.setReflectionPhase(record.getPhase()[rpIndex - 1]);
			}

			if (tpIndex != 0) {
				cs.setTransmissionPhase(record.getPhase()[tpIndex - 1]);
			}

			if (rlIndex != 0) {
				cs.setReflectionLoss(record.getLoss()[rlIndex - 1]);
			}

			if (tlIndex != 0) {
				cs.setTransmissionLoss(record.getLoss()[tlIndex - 1]);
			}

			calculateDerivedValues(cs);

			csb.consumeCalibratedSample(cs, index++);
		}

		setVisible(false);
		TraceHelper.exit(this, "doOK");
	}

	/**
	 * @param cs
	 */
	private void calculateDerivedValues(VNACalibratedSample cs) {
		TraceHelper.entry(this, "calculateDerivedValues");

		final double RAD2DEG = 180.0 / Math.PI;
		double referenceRes = infoBlock.getReference().getReal();
		double mag = Math.pow(10, cs.getReflectionLoss() / 20.0);
		double swr = Math.abs((1.0 + mag) / (1.0 - mag));

		// f = Cos((angle(i) * 0.1758) / 57.324)
		double f = Math.cos(cs.getReflectionPhase() / RAD2DEG);
		// g = Sin((angle(i) * 0.1758) / 57.324)
		double g = Math.sin(cs.getReflectionPhase() / RAD2DEG);
		// rr = f * mag
		double rr = f * mag;
		// ss = g * mag
		double ss = g * mag;
		// '******************************************* X calc
		// *************************************
		// x_imp = Abs(((2 * ss) / (((1 - rr) ^ 2) + (ss ^ 2))) * 50)
		double x_imp = ((2 * ss) / (((1 - rr) * (1 - rr)) + (ss * ss))) * referenceRes;

		// '******************************************* R calc
		// *************************************
		// r_imp = Abs(((1 - (rr ^ 2) - (ss ^ 2)) / (((1 - rr) ^ 2) + (ss ^ 2)))
		// * 50)
		double r_imp = ((1 - (rr * rr) - (ss * ss)) / (((1 - rr) * (1 - rr)) + (ss * ss))) * referenceRes;

		// '******************************************* Z calc
		// *************************************
		// z_imp = Sqr(((r_imp) ^ 2 + (x_imp) ^ 2))
		double z_imp = Math.sqrt(((r_imp * r_imp) + (x_imp * x_imp)));

		cs.setMag(mag);
		cs.setSWR(swr);

		cs.setX(x_imp);
		cs.setR(r_imp);
		cs.setZ(z_imp);

		TraceHelper.exit(this, "calculateDerivedValues");
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

		doReadFile();

		txtFormat.setText("" + infoBlock.getFormat());
		txtParameter.setText("" + infoBlock.getParameter());
		txtReference.setText("" + infoBlock.getReference().getReal());

		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	public VNACalibratedSampleBlock getData() {
		return csb;
	}
}
