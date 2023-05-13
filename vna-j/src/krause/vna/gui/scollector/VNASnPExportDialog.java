/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNASnPImportDialog.java
 *  Part of:   vna-j
 */

package krause.vna.gui.scollector;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.jfree.ui.ExtensionFileFilter;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.GlobalSymbols;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.gui.HelpButton;
import krause.vna.gui.importers.VNASnPDataTable;
import krause.vna.gui.importers.VNASnPDataTableModel;
import krause.vna.gui.util.SwingUtil;
import krause.vna.importers.SnPRecord;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * @author Dietmar
 * 
 */
public class VNASnPExportDialog extends KrauseDialog {

	private static final String S2P_EXTENSION = ".s2p";
	private JButton btOK;
	private VNASnPDataTable lstData;
	private SnPRecord sRecords[];
	private VNAConfig config = VNAConfig.getSingleton();

	/**
	 * 
	 * @param aFrame
	 */
	public VNASnPExportDialog(Window aFrame, SnPRecord pRecords[]) {
		super(aFrame, true);
		TraceHelper.entry(this, "VNASnPExportDialog");

		sRecords = pRecords;

		setProperties(VNAConfig.getSingleton());
		setConfigurationPrefix("VNASnPExportDialog");

		setTitle(VNAMessages.getString("VNASnPExportDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(800, 600));
		getContentPane().setLayout(new MigLayout("", "[][grow,fill][][]", "[][grow,fill][]"));

		//
		getContentPane().add(new JLabel(VNAMessages.getString("VNASnPExportDialog.headline")), "span 4,grow,wrap");

		//
		lstData = new VNASnPDataTable();
		JScrollPane scrollPane = new JScrollPane(lstData);
		scrollPane.setViewportBorder(null);
		getContentPane().add(scrollPane, "span 4,grow,wrap");

		//
		getContentPane().add(SwingUtil.createJButton("Button.Cancel", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDialogCancel();
			}
		}), "left");

		getContentPane().add(new JLabel(), "");
		//
		getContentPane().add(new HelpButton(this, "VNASnPExportDialog"), "");
		//
		btOK = SwingUtil.createJButton("Button.Save", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doSave();
			}
		});
		getContentPane().add(btOK, "right");

		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();

		TraceHelper.exit(this, "VNASnPImportDialog");
	}

	/**
	 * 
	 */
	protected void doSave() {
		TraceHelper.entry(this, "doSave");

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new ExtensionFileFilter("S2P files", S2P_EXTENSION));
		fc.setSelectedFile(new File(config.getReferenceDirectory() + "/."));
		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			config.setReferenceDirectory(file.getParent());

			if (!file.getName().endsWith(S2P_EXTENSION)) {
				file = new File(file.getAbsolutePath() + S2P_EXTENSION);
			}
			if (file.exists()) {
				String msg = MessageFormat.format(VNAMessages.getString("Message.Export.1"), file.getName());
				int response = JOptionPane.showOptionDialog(this, msg, VNAMessages.getString("Message.Export.2"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (response == JOptionPane.CANCEL_OPTION)
					return;
			}

			try {
				exportS2P(file.getAbsolutePath());
			} catch (ProcessingException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), VNAMessages.getString("Message.Export.2"), JOptionPane.ERROR_MESSAGE);
				ErrorLogHelper.exception(this, "doExport", e);
			}
			setVisible(false);
			dispose();
		}
		TraceHelper.exit(this, "doSave");
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

		VNASnPDataTableModel model = lstData.getModel();
		List<SnPRecord> recList = new ArrayList<SnPRecord>();
		for (SnPRecord record : sRecords) {
			recList.add(record);
		}
		model.getData().clear();
		model.getData().addAll(recList);
		model.fireTableDataChanged();

		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	private void exportS2P(String fnp) throws ProcessingException {
		TraceHelper.entry(this, "exportS2P");

		if (fnp != null) {
			//
			DecimalFormatSymbols dfs = getDecimalFormatSymbols();
			DecimalFormat fmtFrequency = new DecimalFormat("000000000", dfs);
			DecimalFormat fmtLoss = new DecimalFormat("0.00000000", dfs);
			DecimalFormat fmtPhase = new DecimalFormat("0.00000000", dfs);
			DecimalFormat fmtReference = new DecimalFormat("0.0", dfs);
			FileOutputStream fos = null;
			Writer w = null;
			try {
				fos = new FileOutputStream(fnp);
				w = new BufferedWriter(new OutputStreamWriter(fos, "ISO-8859-1"));
				// write header
				w.write("! created by ");
				w.write(System.getProperty("user.name"));
				w.write(" at ");
				w.write(new Date().toString());
				w.write(GlobalSymbols.LINE_SEPARATOR);
				w.write("! generated using vna/J Version ");
				w.write(VNAMessages.getString("Application.version"));
				w.write(GlobalSymbols.LINE_SEPARATOR);
				w.write("# Hz S DB R ");

				double real = VNADataPool.getSingleton().getDriver().getDeviceInfoBlock().getReferenceResistance().getReal();
				w.write(fmtReference.format(real));

				w.write(GlobalSymbols.LINE_SEPARATOR);

				// write data
				int numSamples = sRecords.length;
				for (int i = 0; i < numSamples; ++i) {
					SnPRecord data = sRecords[i];
					//
					w.write(fmtFrequency.format(data.getFrequency()));
					w.write(" ");
					//
					for (int j = 0; j < 4; ++j) {
						w.write(fmtLoss.format(data.getLoss()[j]));
						w.write(" ");
						w.write(fmtPhase.format(data.getPhase()[j]));
						w.write(" ");
					}
					w.write(GlobalSymbols.LINE_SEPARATOR);
				}
			} catch (IOException e) {
				ErrorLogHelper.exception(this, "exportS2P", e);
				throw new ProcessingException(e);
			} finally {
				if (w != null) {
					try {
						w.flush();
						w.close();
						w = null;
					} catch (IOException e) {
						ErrorLogHelper.exception(this, "exportS2P", e);
					}
				}
				if (fos != null) {
					try {
						fos.flush();
						fos.close();
						fos = null;
					} catch (IOException e) {
						ErrorLogHelper.exception(this, "exportS2P", e);
					}
				}
			}
		}
		TraceHelper.exit(this, "exportS2P");
	}

	/**
	 * @return
	 */
	private DecimalFormatSymbols getDecimalFormatSymbols() {
		if (".".equals(config.getExportDecimalSeparator())) {
			return new DecimalFormatSymbols(Locale.ENGLISH);
		} else {
			return new DecimalFormatSymbols(Locale.GERMAN);
		}
	}
}
