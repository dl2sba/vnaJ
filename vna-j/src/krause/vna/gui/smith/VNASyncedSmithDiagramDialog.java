package krause.vna.gui.smith;

import java.awt.BorderLayout;
import java.awt.Component;
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
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.math3.complex.Complex;
import org.jfree.ui.ExtensionFileFilter;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.input.ComplexInputFieldValueChangeListener;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.smith.data.SmithDataCurve;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNASyncedSmithDiagramDialog extends KrauseDialog implements WindowListener, ActionListener, SmithPanelDataSupplier, ComplexInputFieldValueChangeListener {
	private VNAConfig config = VNAConfig.getSingleton();

	private static final String RAW_EXTENSION = "gif";
	private static final String RAW_DESCRIPTION = "GIF images (*.gif)";

	private SmithDataCurve dataCurve = null;
	private SmithPanel smithDiagram;
	private VNAMainFrame mainFrame = null;
	private VNACalibratedSampleBlock lastDataReceived = null;

	public VNASyncedSmithDiagramDialog(VNAMainFrame pMainFrame) {
		super(false);
		final String methodName = "VNASyncedSmithDiagramDialog";
		TraceHelper.entry(this, methodName);

		setConfigurationPrefix("VNASyncedSmithDiagramDialog");
		setProperties(VNAConfig.getSingleton());

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		this.mainFrame = pMainFrame;
		setTitle(VNAMessages.getString("Dlg.SyncedSmith.Title"));
		setResizable(true);
		setPreferredSize(new Dimension(600, 600));

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

	public SelectedSampleTuple[] getSelectedTuples() {
		TraceHelper.entry(this, "getSelectedTuples");

		ArrayList<SelectedSampleTuple> tuples = new ArrayList<SelectedSampleTuple>();

		VNAMarkerPanel mp = mainFrame.getMarkerPanel();
		VNAMarker[] markers = mp.getMarkers();
		for (VNAMarker marker : markers) {
			if (marker.isVisible()) {
				SelectedSampleTuple t = new SelectedSampleTuple(marker.getSample().getDiagramX(), config.getColorMarker(1), marker.getShortName());
				tuples.add(t);
			}
		}
		TraceHelper.exit(this, "getSelectedTuples");
		return tuples.toArray(new SelectedSampleTuple[tuples.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.gui.input.ComplexInputFieldValueChangeListener#valueChanged (org.apache.commons.math.complex.Complex,
	 * org.apache.commons.math.complex.Complex)
	 */
	public void valueChanged(Complex oldValue, Complex newValue) {
		TraceHelper.entry(this, "valueChanged");
		consumeCalibratedData(lastDataReceived);
		TraceHelper.exit(this, "valueChanged");
	}

	@Override
	public void windowClosing(WindowEvent e) {
		TraceHelper.entry(this, "windowClosing");
		mainFrame.getDiagramPanel().getScaleSelectPanel().doHandleSmithDiagram();
		// JOptionPane.showMessageDialog(this,
		// VNAMessages.getString("VNASyncedSmithDiagramDialog.infoClose.text"),
		// VNAMessages.getString("VNASyncedSmithDiagramDialog.infoClose.title"),
		// JOptionPane.INFORMATION_MESSAGE);
		TraceHelper.exit(this, "windowClosing");
	}
}
