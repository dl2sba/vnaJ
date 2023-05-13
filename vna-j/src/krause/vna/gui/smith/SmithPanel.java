package krause.vna.gui.smith;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.math3.complex.Complex;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.smith.data.SmithDataCurve;
import krause.vna.gui.smith.data.SmithDiagramCurve;
import krause.vna.resources.VNAMessages;

public class SmithPanel extends JPanel {
	public static final Complex PLUS_1 = new Complex(1, 0);
	public static final Complex PLUS_50 = new Complex(50, 0);
	public static final int SCALING_FACTOR = 1000;

	public static final String VERSION_INFO = VNAMessages.getString("Application.version");
	public static final String COPYRIGHT_INFO = VNAMessages.getString("Application.copyright");

	public static final int TEXT_TOP_OFFSET = 15;
	public static final int TEXT_LEFT_OFFSET = 15;

	private static Complex calculateReflectionCoefficient(Complex z) {
		Complex rc = null;

		rc = z.subtract(PLUS_1).divide(z.add(PLUS_1));
		return rc;
	}

	private static SmithDiagramCurve createCircleAdmittanceImaginary(double imaginary) {
		SmithDiagramCurve rc = new SmithDiagramCurve();
		rc.setLabel(NumberFormat.getNumberInstance().format(-imaginary));
		rc.setRealCurve(false);

		for (double x = 0; x < 10; x += 0.1) {
			Complex comp = new Complex(x, imaginary);
			Complex gamma = calculateReflectionCoefficient(comp);
			int px = -(int) (gamma.getReal() * SCALING_FACTOR);
			int py = (int) (gamma.getImaginary() * SCALING_FACTOR);
			rc.addPoint(px, -py);
		}
		for (double x = 10; x < 100; x += 0.5) {
			Complex comp = new Complex(x, imaginary);
			Complex gamma = calculateReflectionCoefficient(comp);
			int px = -(int) (gamma.getReal() * SCALING_FACTOR);
			int py = (int) (gamma.getImaginary() * SCALING_FACTOR);
			rc.addPoint(px, -py);
		}
		return rc;
	}

	private static SmithDiagramCurve createCircleAdmittanceReal(double real) {
		SmithDiagramCurve rc = new SmithDiagramCurve();
		rc.setLabel(NumberFormat.getNumberInstance().format(real));
		rc.setRealCurve(false);

		for (double x = -100.0; x < -10.0; x += 0.5) {
			Complex comp = new Complex(real, x);
			Complex gamma = calculateReflectionCoefficient(comp);
			int px = -(int) (gamma.getReal() * SCALING_FACTOR);
			int py = (int) (gamma.getImaginary() * SCALING_FACTOR);
			rc.addPoint(px, -py);
		}
		for (double x = -10.0; x < 10.0; x += 0.1) {
			Complex comp = new Complex(real, x);
			Complex gamma = calculateReflectionCoefficient(comp);
			int px = -(int) (gamma.getReal() * SCALING_FACTOR);
			int py = (int) (gamma.getImaginary() * SCALING_FACTOR);
			rc.addPoint(px, -py);
		}
		for (double x = 10.0; x < 100.0; x += 0.5) {
			Complex comp = new Complex(real, x);
			Complex gamma = calculateReflectionCoefficient(comp);
			int px = -(int) (gamma.getReal() * SCALING_FACTOR);
			int py = (int) (gamma.getImaginary() * SCALING_FACTOR);
			rc.addPoint(px, -py);
		}

		return rc;
	}

	private static SmithDiagramCurve createCircleImpedanceImaginary(double imaginary) {
		SmithDiagramCurve rc = new SmithDiagramCurve();
		rc.setLabel(NumberFormat.getNumberInstance().format(imaginary));
		rc.setRealCurve(false);

		for (double x = 0; x < 10; x += 0.1) {
			Complex comp = new Complex(x, imaginary);
			Complex gamma = calculateReflectionCoefficient(comp);
			int px = (int) (gamma.getReal() * SCALING_FACTOR);
			int py = (int) (gamma.getImaginary() * SCALING_FACTOR);
			rc.addPoint(px, -py);
		}
		for (double x = 10; x < 100; x += 0.5) {
			Complex comp = new Complex(x, imaginary);
			Complex gamma = calculateReflectionCoefficient(comp);
			int px = (int) (gamma.getReal() * SCALING_FACTOR);
			int py = (int) (gamma.getImaginary() * SCALING_FACTOR);
			rc.addPoint(px, -py);
		}
		return rc;
	}

	private static SmithDiagramCurve createCircleImpedanceReal(double real) {
		SmithDiagramCurve rc = new SmithDiagramCurve();
		rc.setLabel(NumberFormat.getNumberInstance().format(real));
		rc.setRealCurve(false);

		for (double x = -100.0; x < -10.0; x += 0.5) {
			Complex comp = new Complex(real, x);
			Complex gamma = calculateReflectionCoefficient(comp);
			int px = (int) (gamma.getReal() * SCALING_FACTOR);
			int py = (int) (gamma.getImaginary() * SCALING_FACTOR);
			rc.addPoint(px, -py);
		}
		for (double x = -10.0; x < 10.0; x += 0.1) {
			Complex comp = new Complex(real, x);
			Complex gamma = calculateReflectionCoefficient(comp);
			int px = (int) (gamma.getReal() * SCALING_FACTOR);
			int py = (int) (gamma.getImaginary() * SCALING_FACTOR);
			rc.addPoint(px, -py);
		}
		for (double x = 10.0; x < 100.0; x += 0.5) {
			Complex comp = new Complex(real, x);
			Complex gamma = calculateReflectionCoefficient(comp);
			int px = (int) (gamma.getReal() * SCALING_FACTOR);
			int py = (int) (gamma.getImaginary() * SCALING_FACTOR);
			rc.addPoint(px, -py);
		}

		return rc;
	}

	private static double[] readDoubleList(final String line) {
		ArrayList<Double> al = new ArrayList<>();

		String[] doubles = line.split(" ");

		for (String aDoubleString : doubles) {
			if (aDoubleString.trim().length() > 0) {
				al.add(Double.parseDouble(aDoubleString));
			}
		}

		// convert back t double[]
		double[] target = new double[al.size()];
		for (int i = 0; i < target.length; i++) {
			target[i] = al.get(i);
		}

		return target;
	}

	private final Font textFont = new java.awt.Font("Courier", java.awt.Font.BOLD, 15);
	private Complex referenceResistance = PLUS_50;
	private transient SmithPanelDataSupplier dataSupplier;

	private VNAConfig config = VNAConfig.getSingleton();
	private List<SmithDiagramCurve> admittanceRealCircles = new ArrayList<>();
	private List<SmithDiagramCurve> admittanceImaginaryCircles = new ArrayList<>();
	private List<SmithDiagramCurve> impedanceRealCircles = new ArrayList<>();
	private List<SmithDiagramCurve> impedanceImaginaryCircles = new ArrayList<>();
	private double[] swrCircles;
	private Color colText;
	private Color colData;
	private Color colBackground;
	private Color colMarker;
	private Color colSWR;
	private boolean showPhase;
	private boolean showRL;
	private boolean showRS;

	private boolean showXS;

	private boolean showZ;

	private boolean showMag;

	private boolean showSwr;

	private Color colImpedance;

	private Color colAdmittance;

	public SmithPanel(SmithPanelDataSupplier pDataSupplier) {
		super();
		this.dataSupplier = pDataSupplier;

		calculateSmithChart();

		setBounds(0, 0, 250, 250);

		readConfig();

		setBackground(colBackground);

		setToolTipText(VNAMessages.getString("SmithPanel.tooltip"));

		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				doConfig();
			}

			public void mouseEntered(MouseEvent e) {
				// not used
			}

			public void mouseExited(MouseEvent e) {
				// not used
			}

			public void mousePressed(MouseEvent e) {
				// not used
			}

			public void mouseReleased(MouseEvent e) {
				// not used
			}
		});
	}

	private void calculateSmithChart() {
		TraceHelper.entry(this, "calculateSmithChart");

		admittanceRealCircles.clear();
		for (double real : readDoubleList(config.getProperty(VNASmithDiagramConfigDialog.SMITH_PANEL_CIRCLES_ADMITTANCE_REAL, VNASmithDiagramConfigDialog.DEFAULT_CIRCLES_ADMITTANCE_REAL))) {
			admittanceRealCircles.add(createCircleAdmittanceReal(real));
		}

		admittanceImaginaryCircles.clear();
		for (double imag : readDoubleList(config.getProperty(VNASmithDiagramConfigDialog.SMITH_PANEL_CIRCLES_ADMITTANCE_IMAGINARY, VNASmithDiagramConfigDialog.DEFAULT_CIRCLES_ADMITTANCE_IMAG))) {
			admittanceImaginaryCircles.add(createCircleAdmittanceImaginary(imag));
		}

		impedanceRealCircles.clear();
		for (double cond : readDoubleList(config.getProperty(VNASmithDiagramConfigDialog.SMITH_PANEL_CIRCLES_IMPEDANCE_REAL, VNASmithDiagramConfigDialog.DEFAULT_CIRCLES_IMPEDANCE_REAL))) {
			impedanceRealCircles.add(createCircleImpedanceReal(cond));
		}

		impedanceImaginaryCircles.clear();
		for (double cond : readDoubleList(config.getProperty(VNASmithDiagramConfigDialog.SMITH_PANEL_CIRCLES_IMPEDANCE_IMAGINARY, VNASmithDiagramConfigDialog.DEFAULT_CIRCLES_IMPEDANCE_IMAG))) {
			impedanceImaginaryCircles.add(createCircleImpedanceImaginary(cond));
		}

		swrCircles = readDoubleList(config.getProperty(VNASmithDiagramConfigDialog.SMITH_PANEL_CIRCLES_SWR, VNASmithDiagramConfigDialog.DEFAULT_CIRCLES_SWR));

		TraceHelper.exit(this, "calculateSmithChart");
	}

	public SmithDataCurve createDataCurve(VNACalibratedSample[] samples) {
		TraceHelper.entry(this, "createDataCurve");
		final Point2D[] points = new Point2D[samples.length];

		for (int i = 0; i < samples.length; ++i) {
			final VNACalibratedSample sample = samples[i];

			// get real component
			double real = sample.getR();
			// clip invalid values ...
			if (real < 0)
				real = 0;

			// get imag component
			final double imag = sample.getX();
			final Complex compSample = new Complex(real, imag);

			// build reflection coefficient
			final Complex gamma = compSample.subtract(referenceResistance).divide(compSample.add(referenceResistance));

			Point2D aPoint = new Point2D.Double(gamma.getReal() * SCALING_FACTOR, -gamma.getImaginary() * SCALING_FACTOR);
			points[i] = aPoint;
		}

		// fill return data
		final SmithDataCurve rc = new SmithDataCurve(samples.length);
		rc.setSamples(samples);
		rc.addPoints(points);

		TraceHelper.exit(this, "createDataCurve");
		return rc;
	}

	/**
	 * 
	 */
	protected void doConfig() {
		TraceHelper.entry(this, "doConfig");
		new VNASmithDiagramConfigDialog();
		readConfig();
		calculateSmithChart();
		repaint();
		TraceHelper.exit(this, "doConfig");

	}

	/**
	 * @param g
	 * @param samples
	 */
	private void drawInfoFrequencyRange(final Graphics g, final VNACalibratedSample[] samples) {
		final long startFrq = samples[0].getFrequency();
		final long stopFrq = samples[samples.length - 1].getFrequency();
		final int x = getWidth() - g.getFontMetrics().stringWidth(String.format("Start %,12dHz  ", 1));
		g.drawString(String.format("Start %,13dHz", startFrq), x, getHeight() - 25);
		g.drawString(String.format("Stop  %,13dHz", stopFrq), x, getHeight() - 10);
	}

	/**
	 * @param g
	 * @param samples
	 * @param tuples
	 */
	private void drawInfoMarkers(final Graphics g, final VNACalibratedSample[] samples, final SelectedSampleTuple[] tuples) {
		final String txtMarker = String.format("%s: %,12dHz  ", "1", 100);
		final Rectangle2D textDimension = g.getFontMetrics().getStringBounds(txtMarker, g);
		final int x = (int) (getWidth() - textDimension.getWidth());
		final int yOffset = TEXT_TOP_OFFSET;
		final int yStep = (int) textDimension.getHeight();

		int index = 0;
		for (SelectedSampleTuple tuple : tuples) {
			VNACalibratedSample sample = samples[tuple.getIndex()];

			g.drawString(String.format("%s: %,12dHz", tuple.getName(), sample.getFrequency()), x, yOffset + yStep * index++);

			if (showRL)
				g.drawString(String.format("    RL %7.2fdB", sample.getReflectionLoss()), x, yOffset + yStep * index++);

			if (showPhase)
				g.drawString(String.format("    RP %7.2f\u00B0", sample.getReflectionPhase()), x, yOffset + yStep * index++);

			if (showZ)
				g.drawString(String.format("    Z  %6.1f\u03A9", sample.getZ()), x, yOffset + yStep * index++);

			if (showRS)
				g.drawString(String.format("    Rs %6.1f\u03A9", sample.getR()), x, yOffset + yStep * index++);

			if (showXS)
				g.drawString(String.format("    Xs %6.1f\u03A9", sample.getX()), x, yOffset + yStep * index++);

			if (showSwr)
				g.drawString(String.format("    Swr %5.1f:1", sample.getSWR()), x, yOffset + yStep * index++);

			if (showMag)
				g.drawString(String.format("    Mag %7.3f", sample.getMag()), x, yOffset + yStep * index++);
		}
	}

	private void drawInfoDecoration(final Graphics g) {
		// top left
		g.setColor(colText);
		g.setFont(textFont);
		g.drawString("L    vna/J " + VERSION_INFO + "  " + COPYRIGHT_INFO, TEXT_LEFT_OFFSET, TEXT_TOP_OFFSET);

		// bottom left
		g.drawString("C    Ref=" + referenceResistance.getReal() + "+" + referenceResistance.getImaginary() + "i", TEXT_LEFT_OFFSET, getHeight() - 10);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		final Graphics2D grafObject = (Graphics2D) g.create();

		// setup colors

		// clear panel
		g.setColor(colBackground);
		g.fillRect(0, 0, getWidth(), getHeight());

		drawInfoDecoration(g);

		final SmithDataCurve curve = dataSupplier.getDataCurve();
		final SelectedSampleTuple[] tuples = dataSupplier.getSelectedTuples();

		final double minVal = Math.min(getWidth(), getHeight()) * 0.93;
		final double scale = minVal / (SCALING_FACTOR * 2);
		final Font f = grafObject.getFont().deriveFont((float) (12 / scale)).deriveFont(Font.BOLD);
		grafObject.setFont(f);

		AffineTransform transformTranslate = AffineTransform.getTranslateInstance(getWidth() / 2.0, getHeight() / 2.0);
		AffineTransform transformScale = AffineTransform.getScaleInstance(scale, scale);

		grafObject.transform(transformTranslate);
		grafObject.transform(transformScale);

		final int X_OFFSET = 40;

		// plot admittance curves
		grafObject.setColor(colAdmittance);
		for (SmithDiagramCurve polygon : admittanceRealCircles) {
			grafObject.drawString(polygon.getLabel(), polygon.xpoints[polygon.npoints / 2], polygon.ypoints[polygon.npoints / 2] + X_OFFSET);
			grafObject.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
		}

		// plot imaginary curves
		for (SmithDiagramCurve polygon : admittanceImaginaryCircles) {
			//
			int x = polygon.xpoints[0];
			if (x < 0)
				x -= X_OFFSET;
			else if (x > 0)
				x += X_OFFSET;

			grafObject.drawString(polygon.getLabel(), x, polygon.ypoints[0]);
			grafObject.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
		}

		// plot impedance circles
		grafObject.setColor(colImpedance);
		for (SmithDiagramCurve polygon : impedanceRealCircles) {
			grafObject.drawString(polygon.getLabel(), polygon.xpoints[polygon.npoints / 2] + 10, polygon.ypoints[polygon.npoints / 2] - 10);
			grafObject.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
		}

		for (SmithDiagramCurve polygon : impedanceImaginaryCircles) {
			//
			int x = polygon.xpoints[0];
			if (x < 0)
				x += X_OFFSET;
			else if (x > 0)
				x -= X_OFFSET;

			grafObject.drawString(polygon.getLabel(), x, polygon.ypoints[0]);
			grafObject.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
		}

		// plot SWR curves
		// swr mag
		// 1 0
		// 1.5 0.2
		// 2.0 0.333
		// 3.0 0,5
		// 4.0 0,4
		for (double aSWR : swrCircles) {
			double mag = (aSWR - 1) / (aSWR + 1);

			int ac = (int) (mag * 2000);
			// draw swr circle
			grafObject.setColor(colSWR);
			// draw circle around center of chart with radius ac/2
			grafObject.drawOval(-ac / 2, -ac / 2, ac, ac);
		}

		//
		grafObject.setColor(colData);

		if (curve != null)

		{

			final Line2D[] lines = curve.getLines();
			int i = 0;
			for (Line2D line : lines) {
				grafObject.draw(line);
				// marker tuples provided?
				if (tuples != null) {
					// yes
					// for each tuple

					for (int s = 0; s < tuples.length; ++s) {
						// tuples matched current draw index i?
						if (tuples[s].getIndex() == i) {
							// yes
							// switch color to marker
							grafObject.setColor(colMarker);
							// draw small rectangle
							grafObject.drawRect((int) line.getX1() - 15, (int) line.getY1() - 15, 30, 30);
							// draw marker id
							grafObject.drawString(tuples[s].getName(), (int) line.getX1() - 7, (int) line.getY1() - 20);
							// switch color back to data
							grafObject.setColor(colData);
						}
					}
				}
				++i;
			}

			g.setColor(colText);
			g.setFont(textFont);
			final VNACalibratedSample[] samples = curve.getSamples();
			if (samples != null) {
				drawInfoFrequencyRange(g, samples);
				if (tuples != null) {
					drawInfoMarkers(g, samples, tuples);
				}
			}
		}
	}

	/**
	 * 
	 */
	private void readConfig() {
		TraceHelper.entry(this, "readConfig");
		// get the colors
		colText = config.getColor(VNASmithDiagramConfigDialog.SMITH_PANEL_COL_TEXT, VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_TEXT);
		colData = config.getColor(VNASmithDiagramConfigDialog.SMITH_PANEL_COL_DATA, VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_DATA);
		colBackground = config.getColor(VNASmithDiagramConfigDialog.SMITH_PANEL_COL_BACKGROUND, VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_BACKGROUND);
		colMarker = config.getColor(VNASmithDiagramConfigDialog.SMITH_PANEL_COL_MARKER, VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_MARKER);

		colSWR = config.getColor(VNASmithDiagramConfigDialog.SMITH_PANEL_COL_SWR, VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_SWR);
		colImpedance = config.getColor(VNASmithDiagramConfigDialog.SMITH_PANEL_COL_IMPEDANCE, VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_IMPEDANCE);
		colAdmittance = config.getColor(VNASmithDiagramConfigDialog.SMITH_PANEL_COL_ADMITTANCE, VNASmithDiagramConfigDialog.SMITH_PANEL_DEFCOL_ADMITTANCE);

		referenceResistance = config.getSmithReference();

		// get the elements to show
		showPhase = config.getBoolean(VNASmithDiagramConfigDialog.SMITH_PANEL_SHOW_MARKER_PHASE, true);
		showRL = config.getBoolean(VNASmithDiagramConfigDialog.SMITH_PANEL_SHOW_MARKER_RL, true);
		showRS = config.getBoolean(VNASmithDiagramConfigDialog.SMITH_PANEL_SHOW_MARKER_RS, true);
		showXS = config.getBoolean(VNASmithDiagramConfigDialog.SMITH_PANEL_SHOW_MARKER_XS, true);
		showZ = config.getBoolean(VNASmithDiagramConfigDialog.SMITH_PANEL_SHOW_MARKER_Z, true);
		showMag = config.getBoolean(VNASmithDiagramConfigDialog.SMITH_PANEL_SHOW_MARKER_MAG, true);
		showSwr = config.getBoolean(VNASmithDiagramConfigDialog.SMITH_PANEL_SHOW_MARKER_SWR, true);

		TraceHelper.exit(this, "readConfig");
	}
}
