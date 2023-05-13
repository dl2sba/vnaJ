package krause.vna.gui.raw;

import java.awt.Window;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.importers.VNASnPImportDialog;
import krause.vna.resources.VNAMessages;

public class VNARawHandler {
	protected Window owner;
	protected VNAConfig config = VNAConfig.getSingleton();
	protected VNADataPool datapool = VNADataPool.getSingleton();
	public final static String S1P_EXTENSION = "S1P";
	public final static String S2P_EXTENSION = "S2P";
	public final static String RAW_DESCRIPTION = "vna/J Import/Export files";

	// first new file format with magic at start
	public final static String RAW_EXTENSION_V2 = "XML";

	public VNARawHandler(Window pMainFrame) {
		TraceHelper.entry(this, "VNARawHandler");
		owner = pMainFrame;
		TraceHelper.exit(this, "VNARawHandler");

	}

	/**
	 * 
	 * @param blk
	 * @return
	 */
	public String doExport(VNACalibratedSampleBlock blk) {
		TraceHelper.entry(this, "doExport");
		String rc = null;

		VNARawCommentField ac = new VNARawCommentField(owner, true);
		ac.setText(config.getLastRawComment());

		JFileChooser fc = new JFileChooser();
		fc.setAccessory(ac);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileNameExtensionFilter(RAW_DESCRIPTION, RAW_EXTENSION_V2));
		fc.setSelectedFile(new File(config.getReferenceDirectory() + "/."));
		int returnVal = fc.showSaveDialog(owner);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			config.setReferenceDirectory(file.getParent());

			if (!file.getName().endsWith("." + RAW_EXTENSION_V2)) {
				file = new File(file.getAbsolutePath() + "." + RAW_EXTENSION_V2);
			}
			if (file.exists()) {
				String msg = MessageFormat.format(VNAMessages.getString("Message.Export.1"), file.getName());
				int response = JOptionPane.showOptionDialog(owner, msg, VNAMessages.getString("Message.Export.2"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (response == JOptionPane.CANCEL_OPTION)
					return rc;
			}

			// set the entered comment in the global config
			config.setLastRawComment(ac.getText());

			// set the entered comment in the passed block
			blk.setComment(ac.getText());

			// write to file
			try {
				new VNARawXMLHandler().writeXMLFile(blk, file);
			} catch (ProcessingException e) {
				String msg = MessageFormat.format(VNAMessages.getString("Message.Export.6"), e.getMessage());
				JOptionPane.showMessageDialog(owner, msg, VNAMessages.getString("Message.Export.5"), JOptionPane.ERROR_MESSAGE);
			}

			rc = file.getAbsolutePath();
		}
		TraceHelper.exitWithRC(this, "doExport", rc);
		return rc;
	}

	public VNACalibratedSampleBlock doImport() {
		TraceHelper.entry(this, "doImport");
		VNACalibratedSampleBlock rc = null;

		VNARawCommentField ac = new VNARawCommentField(owner, false);
		ac.setEnabled(false);

		JFileChooser fc = new JFileChooser();
		fc.setAccessory(ac);
		fc.addPropertyChangeListener(ac);
		fc.addActionListener(ac);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setSelectedFile(new File(config.getReferenceDirectory() + "/."));
		fc.setFileFilter(new FileNameExtensionFilter(RAW_DESCRIPTION, RAW_EXTENSION_V2, S1P_EXTENSION, S2P_EXTENSION));
		int returnVal = fc.showOpenDialog(owner);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			config.setReferenceDirectory(file.getParent());
			try {
				rc = readFile(file);
			} catch (ProcessingException e) {
				String m = MessageFormat.format(VNAMessages.getString("Message.Import.1"), e.getMessage());
				JOptionPane.showMessageDialog(owner, m, VNAMessages.getString("Message.Import.2"), JOptionPane.ERROR_MESSAGE);
			}
		}
		TraceHelper.exit(this, "doImport");
		return rc;
	}

	public void exportMainDiagram() {
		TraceHelper.entry(this, "exportMainDiagram");
		VNACalibratedSampleBlock blk = datapool.getCalibratedData();
		doExport(blk);
		TraceHelper.exit(this, "exportMainDiagram");
	}

	public String readComment(File file) throws ProcessingException {
		TraceHelper.entry(this, "readComment");
		String rc = null;
		if (file != null) {
			if (file.getAbsolutePath().toUpperCase().endsWith(RAW_EXTENSION_V2)) {
				TraceHelper.text(this, "readComment", "found " + RAW_EXTENSION_V2 + " in name");
				rc = new VNARawXMLHandler().readXMLCommentFromFile(file);
			}
		}
		TraceHelper.exitWithRC(this, "readComment", rc);
		return rc;
	}

	/**
	 * Try to load the data based on file extension
	 * 
	 * @param file
	 * @return
	 * @throws ProcessingException
	 */
	public VNACalibratedSampleBlock readFile(File file) throws ProcessingException {
		TraceHelper.entry(this, "readFile");
		VNACalibratedSampleBlock rc = null;
		if (file.getAbsolutePath().toUpperCase().endsWith(RAW_EXTENSION_V2)) {
			rc = new VNARawXMLHandler().readXMLFromFile(file);
		} else if (file.getAbsolutePath().toUpperCase().endsWith(".S1P")) {
			rc = readSParameterFile(file);
		} else if (file.getAbsolutePath().toUpperCase().endsWith(".S2P")) {
			rc = readSParameterFile(file);
		} else {
			throw new ProcessingException(VNAMessages.getString("Message.Import.3"));
		}
		TraceHelper.exit(this, "readFile");
		return rc;
	}

	/**
	 * @param file
	 * @return
	 */
	private VNACalibratedSampleBlock readSParameterFile(File file) {
		VNACalibratedSampleBlock rc = null;
		TraceHelper.entry(this, "readSParameterFile");
		VNASnPImportDialog dlg = new VNASnPImportDialog(owner, file.getAbsolutePath());

		rc = dlg.getData();
		dlg.dispose();

		TraceHelper.exit(this, "readSParameterFile");
		return rc;
	}
}