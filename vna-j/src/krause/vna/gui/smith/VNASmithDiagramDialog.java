package krause.vna.gui.smith;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import org.apache.commons.math3.complex.Complex;
import org.jfree.ui.ExtensionFileFilter;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.input.ComplexInputFieldValueChangeListener;
import krause.vna.gui.panels.marker.VNAMarkerTextField;
import krause.vna.gui.smith.data.SmithDataCurve;
import krause.vna.gui.smith.data.SmithDiagramCurve;
import krause.vna.gui.util.SwingUtil;
import krause.vna.gui.util.VNAGridBagConstraints;
import krause.vna.resources.VNAMessages;

public class VNASmithDiagramDialog extends KrauseDialog implements ActionListener, AdjustmentListener, MouseWheelListener, SmithPanelDataSupplier, ComplexInputFieldValueChangeListener {

	private static final String RAW_EXTENSION = "gif";
	private static final String RAW_DESCRIPTION = "GIF images (*.gif)";
	private List<SmithDiagramCurve> realCurves = new ArrayList<SmithDiagramCurve>();
	private List<SmithDiagramCurve> imaginaryCurves = new ArrayList<SmithDiagramCurve>();
	private SmithDataCurve dataCurve = null;
	private SmithPanel smithDiagram;
	private VNACalibratedSampleBlock dataBlock = null;
	private JScrollBar sbMarker;
	private VNAMarkerTextField txtFRQ;
	private VNAMarkerTextField txtLOSS;
	private VNAMarkerTextField txtPHASE;
	private VNAMarkerTextField txtZ;
	private VNAMarkerTextField txtR;
	private VNAMarkerTextField txtX;
	private VNAMarkerTextField txtSWR;
	private ComplexInputField referenceResistance;
	private VNAConfig config = VNAConfig.getSingleton();
	private int selectedSampleIndex;

	public VNASmithDiagramDialog(VNACalibratedSampleBlock blk, String titleInsert) {
		super(false);
		TraceHelper.entry(this, "VNASmithDiagramDialog");

		String msg = VNAMessages.getString("Dlg.Smith.Title");

		setConfigurationPrefix("VNASmithDiagramDialog");
		setProperties(VNAConfig.getSingleton());

		setTitle(MessageFormat.format(msg, titleInsert));
		setResizable(true);
		dataBlock = blk;
		setPreferredSize(new Dimension(720, 600));

		getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
		getContentPane().add(createSmithPanel(), BorderLayout.CENTER);

		calculateSmithChart();
		valueChanged(null, null);

		doDialogInit();
		TraceHelper.exit(this, "VNASmithDiagramDialog");
	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");

		config.setSmithReference(referenceResistance.getComplexValue());

		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.gui.input.ComplexInputFieldValueChangeListener#valueChanged
	 * (org.apache.commons.math.complex.Complex,
	 * org.apache.commons.math.complex.Complex)
	 */
	public void valueChanged(Complex oldValue, Complex newValue) {
		TraceHelper.entry(this, "valueChanged");

		VNACalibratedSample[] samples = dataBlock.getCalibratedSamples();
		dataCurve = smithDiagram.createDataCurve(samples);

		sbMarker.setValue(0);
		updateMarker(dataBlock.getCalibratedSamples()[0]);
		smithDiagram.repaint();
		TraceHelper.exit(this, "valueChanged");
	}

	private Component createButtonPanel() {
		TraceHelper.entry(this, "createButtonPanel");
		JPanel pnlButton = new JPanel();

		referenceResistance = new ComplexInputField(config.getSmithReference());
		referenceResistance.setMaximum(new Complex(5000, 5000));
		referenceResistance.setMinimum(new Complex(-5000, -5000));
		referenceResistance.setListener(this);

		pnlButton.add(referenceResistance);
		pnlButton.add(createMarkerPanel());
		pnlButton.add(SwingUtil.createJButton("Button.Save.GIF", this));
		pnlButton.add(SwingUtil.createJButton("Button.Close", this));
		TraceHelper.exit(this, "createButtonPanel");
		return pnlButton;
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		TraceHelper.entry(this, "actionPerformed", cmd);
		if (VNAMessages.getString("Button.Close.Command").equals(cmd)) {
			doDialogCancel();
		} else if (VNAMessages.getString("Button.Save.GIF.Command").equals(cmd)) {
			doExportToImage();
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		selectedSampleIndex = e.getValue();
		updateMarker(dataBlock.getCalibratedSamples()[e.getValue()]);
		smithDiagram.repaint();
	}

	private void calculateSmithChart() {
		TraceHelper.entry(this, "calculateSmithChart");
		realCurves.add(createCircle4Real(0));
		realCurves.add(createCircle4Real(0.2));
		realCurves.add(createCircle4Real(0.5));
		realCurves.add(createCircle4Real(1));
		realCurves.add(createCircle4Real(2));
		realCurves.add(createCircle4Real(5));

		imaginaryCurves.add(createCircle4Imaginary(-5));
		imaginaryCurves.add(createCircle4Imaginary(-2));
		imaginaryCurves.add(createCircle4Imaginary(-1));
		imaginaryCurves.add(createCircle4Imaginary(-0.5));
		imaginaryCurves.add(createCircle4Imaginary(-0.2));
		imaginaryCurves.add(createCircle4Imaginary(0));
		imaginaryCurves.add(createCircle4Imaginary(0.2));
		imaginaryCurves.add(createCircle4Imaginary(0.5));
		imaginaryCurves.add(createCircle4Imaginary(1));
		imaginaryCurves.add(createCircle4Imaginary(2));
		imaginaryCurves.add(createCircle4Imaginary(5));

		TraceHelper.exit(this, "calculateSmithChart");
	}

	private SmithDiagramCurve createCircle4Imaginary(double imaginary) {
		SmithDiagramCurve rc = new SmithDiagramCurve();
		rc.setLabel(NumberFormat.getNumberInstance().format(imaginary));
		rc.setRealCurve(false);

		for (double x = 0; x < 10; x += 0.1) {
			Complex comp = new Complex(x, imaginary);
			Complex gamma = mGamma(comp);
			int px = (int) (gamma.getReal() * getFactor());
			int py = (int) (gamma.getImaginary() * getFactor());
			rc.addPoint(px, -py);
		}
		for (double x = 10; x < 100; x += 0.5) {
			Complex comp = new Complex(x, imaginary);
			Complex gamma = mGamma(comp);
			int px = (int) (gamma.getReal() * getFactor());
			int py = (int) (gamma.getImaginary() * getFactor());
			rc.addPoint(px, -py);
		}
		return rc;
	}

	private SmithDiagramCurve createCircle4Real(double real) {
		SmithDiagramCurve rc = new SmithDiagramCurve();
		rc.setLabel(NumberFormat.getNumberInstance().format(real));
		rc.setRealCurve(false);

		for (double x = -100.0; x < -10.0; x += 0.5) {
			Complex comp = new Complex(real, x);
			Complex gamma = mGamma(comp);
			int px = (int) (gamma.getReal() * getFactor());
			int py = (int) (gamma.getImaginary() * getFactor());
			rc.addPoint(px, -py);
		}
		for (double x = -10.0; x < 10.0; x += 0.1) {
			Complex comp = new Complex(real, x);
			Complex gamma = mGamma(comp);
			int px = (int) (gamma.getReal() * getFactor());
			int py = (int) (gamma.getImaginary() * getFactor());
			rc.addPoint(px, -py);
		}
		for (double x = 10.0; x < 100.0; x += 0.5) {
			Complex comp = new Complex(real, x);
			Complex gamma = mGamma(comp);
			int px = (int) (gamma.getReal() * getFactor());
			int py = (int) (gamma.getImaginary() * getFactor());
			rc.addPoint(px, -py);
		}

		return rc;
	}

	private JPanel createMarkerPanel() {
		JPanel rc = new JPanel();
		rc.setLayout(new GridBagLayout());
		rc.setBorder(null);
		// headline
		int x = 0;
		int line = 0;
		rc.add(new JLabel(VNAMessages.getString("Marker.Frequency")), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(new JLabel(VNAMessages.getString("Marker.RL")), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(new JLabel(VNAMessages.getString("Marker.PhaseRL")), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(new JLabel(VNAMessages.getString("Marker.Z")), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(new JLabel(VNAMessages.getString("Marker.R")), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(new JLabel(VNAMessages.getString("Marker.X")), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(new JLabel(VNAMessages.getString("Marker.SWR")), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		//
		line = 1;
		x = 0;
		rc.add(txtFRQ = new VNAMarkerTextField(9, false), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(txtLOSS = new VNAMarkerTextField(4, false), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(txtPHASE = new VNAMarkerTextField(4, false), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(txtZ = new VNAMarkerTextField(5, false), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(txtR = new VNAMarkerTextField(4, false), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(txtX = new VNAMarkerTextField(4, false), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));
		rc.add(txtSWR = new VNAMarkerTextField(4, false), new VNAGridBagConstraints(x++, line, 1, 1, 0, 0));

		return rc;
	}

	private JPanel createSmithPanel() {
		TraceHelper.entry(this, "createSmithPanel");
		JPanel rc = new JPanel();
		rc.setLayout(new BorderLayout());

		rc.add(smithDiagram = new SmithPanel(this), BorderLayout.CENTER);

		sbMarker = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, dataBlock.getCalibratedSamples().length - 1);
		sbMarker.addAdjustmentListener(this);
		sbMarker.addMouseWheelListener(this);
		sbMarker.setToolTipText(VNAMessages.getString("Dlg.Smith.Scrollbar"));
		rc.add(sbMarker, BorderLayout.SOUTH);
		TraceHelper.exit(this, "createSmithPanel");
		return rc;
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");

	}

	private void doExportToImage() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new ExtensionFileFilter(RAW_DESCRIPTION, RAW_EXTENSION));
		fc.setSelectedFile(new File(VNAConfig.getSingleton().getExportDirectory() + "/."));
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
			} catch (IOException e) {
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
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	/**
	 * @return the dataCurve
	 */
	public SmithDataCurve getDataCurve() {
		return dataCurve;
	}

	public int getFactor() {
		return 1000;
	}

	public List<SmithDiagramCurve> getImaginaryCurves() {
		return imaginaryCurves;
	}

	public List<SmithDiagramCurve> getRealCurves() {
		return realCurves;
	}

	public int getSelectedSampleIndex() {
		return selectedSampleIndex;
	}

	private static Complex mGamma(Complex z) {
		Complex rc = null;

		rc = z.subtract(SmithPanel.PLUS_1).divide(z.add(SmithPanel.PLUS_1));
		return rc;
	}

	private void updateMarker(VNACalibratedSample s) {
		if (s != null) {
			txtFRQ.setText(VNAFormatFactory.getFrequencyFormat().format(s.getFrequency()));
			txtSWR.setText(VNAFormatFactory.getSwrFormat().format(s.getSWR()));
			txtLOSS.setText(VNAFormatFactory.getReflectionLossFormat().format(s.getReflectionLoss()));
			txtPHASE.setText(VNAFormatFactory.getPhaseFormat().format(s.getReflectionPhase()));
			txtZ.setText(VNAFormatFactory.getZFormat().format(s.getZ()));
			txtR.setText(VNAFormatFactory.getRsFormat().format(s.getR()));
			txtX.setText(VNAFormatFactory.getXsFormat().format(s.getX()));
		} else {
			txtFRQ.setText("");
			txtSWR.setText("");
			txtZ.setText("");
			txtLOSS.setText("");
			txtPHASE.setText("");
			txtR.setText("");
			txtX.setText("");
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		TraceHelper.entry(this, "mouseWheelMoved");
		JScrollBar source = (JScrollBar) e.getSource();
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			int totalScrollAmount = e.getUnitsToScroll() * source.getUnitIncrement();
			source.setValue(source.getValue() + totalScrollAmount);
		}
		TraceHelper.exit(this, "mouseWheelMoved");
	}

	public SelectedSampleTuple[] getSelectedTuples() {
		return new SelectedSampleTuple[] {
				new SelectedSampleTuple(selectedSampleIndex, Color.red, "M")
		};
	}
}
