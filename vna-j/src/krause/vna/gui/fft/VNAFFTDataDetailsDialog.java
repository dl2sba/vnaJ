package krause.vna.gui.fft;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.text.ParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.export.JFSeries;
import krause.vna.gui.cable.VNACableMeasurementPoint;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Dietmar
 * 
 */
public class VNAFFTDataDetailsDialog extends KrauseDialog implements IVNADataConsumer {
	private static final float STROKE_WIDTH = 1;
	private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	private static final Font TICK_FONT = new Font("SansSerif", Font.PLAIN, 10);

	private JFreeChart chart;
	private ChartPanel lblImage;
	private JButton btCancel;
	private JTextField txtPeakAt;
	private JTextField txtTickLen;
	private JButton btScan;

	private transient VNADataPool datapool = VNADataPool.getSingleton();
	private transient VNAConfig config = VNAConfig.getSingleton();
	private transient IVNADriver driver = datapool.getDriver();
	private VNADeviceInfoBlock dib = driver.getDeviceInfoBlock();
	private JLabel lblStatus;
	private JTextField txtVelocityFactor;
	private VNAFFTPeakTable tblPeaks;
	private JTextField txtPeakLimit;

	/**
	 * Create the dialog.
	 */
	public VNAFFTDataDetailsDialog(Window wnd) {
		super(wnd, true);
		final String methodName = "VNAFFTDataDetailsDialog";
		TraceHelper.entry(this, methodName);

		setTitle(VNAMessages.getString("FFT.title"));

		setProperties(VNAConfig.getSingleton());
		setConfigurationPrefix(methodName);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(689, 602));

		getContentPane().setLayout(new MigLayout("", "[][][grow,fill][25%]", "[grow,fill][][][]"));

		chart = ChartFactory.createXYLineChart("", "Distance", null, null, PlotOrientation.VERTICAL, true, false, false);

		lblImage = new ChartPanel(chart, true);
		lblImage.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		lblImage.setIgnoreRepaint(true);
		lblImage.setPreferredSize(new Dimension(640, 480));
		lblImage.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		getContentPane().add(lblImage, "span 3");

		this.tblPeaks = new VNAFFTPeakTable();
		final JScrollPane scrollPane = new JScrollPane(this.tblPeaks);
		getContentPane().add(scrollPane, "wrap");

		getContentPane().add(new JLabel(VNAMessages.getString("VNACableLengthDialog.lblVelocityFactor.text")), "");
		txtVelocityFactor = new JTextField();
		txtVelocityFactor.setFocusTraversalKeysEnabled(false);
		txtVelocityFactor.setHorizontalAlignment(SwingConstants.RIGHT);
		txtVelocityFactor.setColumns(6);
		txtVelocityFactor.setText(VNAFormatFactory.getVelocityFormat().format(config.getDouble("VNAFFTDataDetailsDialog.vf", 0.66)));
		getContentPane().add(txtVelocityFactor, "wrap");

		getContentPane().add(new JLabel(VNAMessages.getString("VNACableLengthDialog.lblPeakLimit.text")), "");
		txtPeakLimit= new JTextField();
		txtPeakLimit.setFocusTraversalKeysEnabled(false);
		txtPeakLimit.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPeakLimit.setColumns(6);
		txtPeakLimit.setText(VNAFormatFactory.getVelocityFormat().format(config.getDouble("VNAFFTDataDetailsDialog.peakLimit", 0.5)));
		getContentPane().add(txtPeakLimit, "wrap");

		getContentPane().add(new JLabel(VNAMessages.getString("FFT.SampleLen")), "");
		txtTickLen = new JTextField(10);
		txtTickLen.setEditable(false);
		txtTickLen.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(txtTickLen, "wrap");

		getContentPane().add(new JLabel(VNAMessages.getString("FFT.PeaktAt")), "");
		txtPeakAt = new JTextField(10);
		txtPeakAt.setEditable(false);
		txtPeakAt.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(txtPeakAt, "wrap");

		btCancel = SwingUtil.createJButton("Button.Cancel", e -> doDialogCancel());
		btCancel.setActionCommand("Cancel");
		getContentPane().add(btCancel, "center");

		lblStatus = new JLabel("Ready ...");
		getContentPane().add(lblStatus, "span 2,grow");
		
		btScan = SwingUtil.createJButton("Button.START", e -> doSTART());
		getContentPane().add(btScan, "right");

		getRootPane().setDefaultButton(btCancel);

		doDialogInit();
		TraceHelper.exit(this, methodName);
	}

	protected void doSTART() {
		TraceHelper.entry(this, "doSTART");
		// setup cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		//
		btCancel.setEnabled(false);
		btScan.setEnabled(false);

		// create on instance
		VNABackgroundJob job = new VNABackgroundJob();
		job.setNumberOfSamples(1024);
		job.setSpeedup(1);
		job.setAverage(0);
		job.setOverScan(1);
		job.setFrequencyRange(new VNAFrequencyRange(dib.getMinFrequency(), dib.getMaxFrequency()));
		job.setScanMode(VNAScanMode.MODE_REFLECTION);

		VnaBackgroundTask backgroundTask = new VnaBackgroundTask(datapool.getDriver());
		backgroundTask.addJob(job);
		backgroundTask.addDataConsumer(this);
		backgroundTask.setStatusLabel(lblStatus);
		backgroundTask.execute();
		TraceHelper.exit(this, "doSTART");
	}

	/**
	 * 
	 * @return
	 */
	private JFreeChart createChart(final Complex[] input, double oneTickLen) {
		final String methodName = "createChart";
		TraceHelper.entry(this, methodName);
		final int len = input.length;

		final JFSeries series1 = new JFSeries();
		final XYSeries xySeries1 = new XYSeries("Abs()");

		for (int i = 0; i < len; i += 1) {
			xySeries1.add(i * oneTickLen, input[i].abs());
		}
		series1.setSeries(xySeries1);

		this.chart = ChartFactory.createXYLineChart(null, "length (m)", null, null, PlotOrientation.VERTICAL, false, true, false);
		this.chart.setAntiAlias(false);

		NumberAxis rangeAxis1 = new NumberAxis(series1.getDataset().getSeries(0).getKey().toString());
		rangeAxis1.setLabelFont(LABEL_FONT);
		rangeAxis1.setTickLabelFont(TICK_FONT);
		rangeAxis1.setLabelPaint(Color.RED);

		final XYPlot plot = chart.getXYPlot();
		plot.setRangeAxis(0, rangeAxis1);
		plot.setDataset(0, series1.getDataset());
		plot.mapDatasetToRangeAxis(0, 0);
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.darkGray);
		plot.setRangeGridlinePaint(Color.darkGray);
		plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
		plot.getRenderer(0).setSeriesPaint(0, Color.RED);
		plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));
		plot.getDomainAxis().setLabelFont(LABEL_FONT);
		plot.getDomainAxis().setTickLabelFont(TICK_FONT);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// OPTIONAL CUSTOMISATION COMPLETED.
		TraceHelper.exit(this, methodName);
		return chart;
	}

	/**
	 * 
	 */
	protected void doDialogCancel() {
		final String methodName = "doDialogCancel";
		TraceHelper.entry(this, methodName);
		setVisible(false);
		dispose();
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 */
	protected void doDialogInit() {
		final String methodName = "doDialogInit";
		TraceHelper.entry(this, methodName);
		//
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, methodName);
	}

	private void calculate(VNACalibratedSample[] samples) {
		final String methodName = "calculate";
		TraceHelper.entry(this, methodName);

		final int len = samples.length;
		final Complex[] values = new Complex[len];

		for (int i = 0; i < len; ++i) {
			values[i] = samples[i].getRHO();
		}

		final FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		final Complex[] complexFFTData = transformer.transform(values, TransformType.INVERSE);

		try {
			final long startFreq = samples[0].getFrequency();
			final long stopFreq = samples[samples.length - 1].getFrequency();
			final double oneTickTime = (1.0 / (stopFreq - startFreq)) / 2;
			final double vf = VNAFormatFactory.getVelocityFormat().parse(txtVelocityFactor.getText()).doubleValue();
			config.putDouble("VNAFFTDataDetailsDialog.vf", vf);
			final double oneTickLen = VNACableMeasurementPoint.SOL * oneTickTime * vf;

			final double lowerPeakLimit = VNAFormatFactory.getVelocityFormat().parse(txtPeakLimit.getText()).doubleValue();
			config.putDouble("VNAFFTDataDetailsDialog.peakLimit", lowerPeakLimit);

			this.txtTickLen.setText(VNAFormatFactory.getLengthFormat().format(oneTickLen));

			tblPeaks.getModel().getValues().clear();

			// find maximum value and its index
			double maxVal = lowerPeakLimit;
			int maxIndex = 0;
			for (int i = 0; i < len; ++i) {
				final double val = complexFFTData[i].abs();
				if (val > maxVal) {
					maxVal = val;
					maxIndex = i;
				}
			}

			double upperLimit = maxVal;
			for (int x = 0; x < 10; ++x) {
				if (maxIndex != -1) {
					tblPeaks.getModel().getValues().add(new VNAFFTPeakTableEntry(maxIndex, maxVal, oneTickLen * maxIndex));
					upperLimit = maxVal * 0.95;
					TraceHelper.text(this,  methodName, "New max val %f", upperLimit);
					
				}
				maxVal = lowerPeakLimit;
				maxIndex = -1;
				for (int i = 0; i < len; ++i) {
					final double val = complexFFTData[i].abs();
					if (val < upperLimit) {
						if (val > maxVal) {
							maxVal = val;
							maxIndex = i;
						}
					}
				}
			}

			tblPeaks.repaint();
			tblPeaks.updateUI();

			chart = createChart(complexFFTData, oneTickLen);

			lblImage.setChart(chart);
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(this, VNAMessages.getString("FFT.Err.1"), VNAMessages.getString("FFT.title"), JOptionPane.ERROR_MESSAGE);
		}

		TraceHelper.exit(this, methodName);
	}

	@Override
	public void consumeDataBlock(List<VNABackgroundJob> jobs) {
		final String methodName = "consumeDataBlock";
		TraceHelper.entry(this, methodName);

		final VNASampleBlock rawData = jobs.get(0).getResult();

		// data present?
		if (rawData != null) {
			// yes

			final IVNADriverMathHelper mathHelper = rawData.getMathHelper();
			// math helper set?
			if (mathHelper != null) {
				// yes
				// apply filtering to raw IQ data
				// mathHelper.applyFilter(rawData.getSamples())

				//
				final VNACalibrationBlock mainCalibrationBlock = datapool.getMainCalibrationBlock();

				// also a calibration block available
				if (mainCalibrationBlock != null) {
					// yes
					// create a resized one matching the frequency range
					VNACalibrationBlock resizedCalibrationBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(mainCalibrationBlock, dib.getMinFrequency(), dib.getMaxFrequency(), rawData.getNumberOfSteps());

					final VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(resizedCalibrationBlock);
					context.setConversionTemperature(rawData.getDeviceTemperature());

					// reflection mode?
					if (rawData.getScanMode().isReflectionMode()) {
						// yes
						// reflection mode
						final VNACalibratedSampleBlock samples = mathHelper.createCalibratedSamples(context, rawData);

						//
						calculate(samples.getCalibratedSamples());
					}
				}
			}
		}

		// setup cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		//
		btCancel.setEnabled(true);
		btScan.setEnabled(true);

		TraceHelper.exit(this, methodName);
	}

}