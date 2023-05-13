package krause.vna.gui.calibrate.calibrationkit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.ui.ExtensionFileFilter;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.smith.SelectedSampleTuple;
import krause.vna.gui.smith.SmithPanel;
import krause.vna.gui.smith.SmithPanelDataSupplier;
import krause.vna.gui.smith.data.SmithDataCurve;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNACalibrationKitSmithDiagramDialog extends KrauseDialog implements WindowListener, ActionListener, SmithPanelDataSupplier {
	private VNAConfig config = VNAConfig.getSingleton();

	private static final String RAW_EXTENSION = "gif";
	private static final String RAW_DESCRIPTION = "GIF images (*.gif)";

	private SmithDataCurve dataCurve = null;
	private SmithPanel smithDiagram;
	private VNACalibratedSampleBlock lastDataReceived = null;

	public VNACalibrationKitSmithDiagramDialog(Dialog aDlg) {
		super(aDlg, false);
		final String methodName = "VNACalibrationKitSmithDiagramDialog";
		TraceHelper.entry(this, methodName);

		setConfigurationPrefix("VNACalibrationKitSmithDiagramDialog");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle(VNAMessages.getString("Dlg.SyncedSmith.Title"));
		setResizable(true);
		setPreferredSize(new Dimension(500, 500));

		getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
		getContentPane().add(createSmithPanel(), BorderLayout.CENTER);

		doDialogInit();
		TraceHelper.entry(this, methodName);
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		TraceHelper.entry(this, "actionPerformed", cmd);
		if (VNAMessages.getString("Button.Save.GIF.Command").equals(cmd)) {
			doExportToImage();
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	public void consumeCalibratedData(VNACalibratedSampleBlock currentData) {
		TraceHelper.entry(this, "consumeCalibratedData");
		lastDataReceived = currentData;

		if (lastDataReceived != null) {
			if (lastDataReceived.getCalibratedSamples().length > 0) {
				dataCurve = smithDiagram.createDataCurve(lastDataReceived.getCalibratedSamples());
				smithDiagram.repaint();
			}
		}
		TraceHelper.exit(this, "consumeCalibratedData");
	}

	private Component createButtonPanel() {
		TraceHelper.entry(this, "createButtonPanel");
		JPanel pnlButton = new JPanel();

		pnlButton.add(SwingUtil.createJButton("Button.Save.GIF", this));

		TraceHelper.exit(this, "createButtonPanel");
		return pnlButton;
	}

	private JPanel createSmithPanel() {
		TraceHelper.entry(this, "createSmithPanel");
		JPanel rc = new JPanel();
		rc.setLayout(new BorderLayout());

		rc.add(smithDiagram = new SmithPanel(this), BorderLayout.CENTER);

		TraceHelper.exit(this, "createSmithPanel");
		return rc;
	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		TraceHelper.exit(this, "doCANCEL");
	}

	private void doExportToImage() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new ExtensionFileFilter(RAW_DESCRIPTION, RAW_EXTENSION));
		fc.setSelectedFile(new File(config.getExportDirectory() + "/."));
		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!file.getName().endsWith("." + RAW_EXTENSION)) {
				file = new File(file.getAbsolutePath() + "." + RAW_EXTENSION);
			}
			if (file.exists()) {
				String msg = MessageFormat.format(VNAMessages.getString("Message.Export.1"), file.getName());
				int response = JOptionPane.showOptionDialog(this, msg, VNAMessages // $NON-NLS-1$
						.getString("Message.Export.2"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, //$NON-NLS-1$
						null);
				if (response == JOptionPane.CANCEL_OPTION)
					return;
			}
			Dimension size = smithDiagram.getSize();
			BufferedImage smithImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = smithImage.createGraphics();
			smithDiagram.paint(g2);
			OutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(file.getAbsolutePath());
				ImageIO.write(smithImage, RAW_EXTENSION, outputStream);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), VNAMessages.getString("Message.Export.2"), JOptionPane.ERROR_MESSAGE);
				ErrorLogHelper.exception(this, "doExportToJPG", e);
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						ErrorLogHelper.exception(this, "doExportToJPG", e);
					}
				}
			}
		}
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	/**
	 * @return the dataCurve
	 */
	public SmithDataCurve getDataCurve() {
		return dataCurve;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		final String methodName = "windowClosing";
		TraceHelper.entry(this, methodName);
		TraceHelper.exit(this, methodName);
	}

	@Override
	public SelectedSampleTuple[] getSelectedTuples() {
		final String methodName = "getSelectedTuples";
		TraceHelper.entry(this, methodName);
		TraceHelper.exit(this, methodName);
		return null;
	}
}
