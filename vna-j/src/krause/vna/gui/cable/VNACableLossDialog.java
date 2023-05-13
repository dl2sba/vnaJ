package krause.vna.gui.cable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.l2fprod.common.swing.StatusBar;

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
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Dietmar
 * 
 */
public class VNACableLossDialog extends KrauseDialog implements IVNADataConsumer {
	private static final Color COLOR_LEFT_SCALE = Color.BLUE;
	private static final float STROKE_WIDTH = 1;
	private final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	private final Font TICK_FONT = new Font("SansSerif", Font.PLAIN, 10);

	private JButton btMeasure;
	private JButton btOK;

	private VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private ChartPanel lblImage;
	private StatusBar statusBar;
	private JCheckBox cbPeakSuppression;
	private VNAPhaseCrossingTable tblCrossings;

	/**
	 * Create the dialog.
	 */
	public VNACableLossDialog(Frame pMainFrame) {
		super(pMainFrame, true);
		final String methodName = "VNACableLossDialog";
		TraceHelper.entry(this, methodName);

		setConfigurationPrefix(methodName);
		setProperties(config);

		setTitle(VNAMessages.getString(methodName));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(850, 600));
		getContentPane().setLayout(new MigLayout("", "[grow,fill][][]", "[grow,fill][][]"));

		// ***********************************************************************
		lblImage = new ChartPanel(null, true);
		lblImage.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		lblImage.setIgnoreRepaint(true);
		lblImage.setPreferredSize(new Dimension(400, 300));
		lblImage.setMinimumSize(new Dimension(400, 300));
		getContentPane().add(lblImage, "span 2");

		tblCrossings = new VNAPhaseCrossingTable();
		JScrollPane scrollPane = new JScrollPane(tblCrossings);
		scrollPane.setViewportBorder(null);
		getContentPane().add(scrollPane, "wrap");

		// ***********************************************************************
		btOK = new JButton(VNAMessages.getString("Button.Close"));
		getContentPane().add(btOK, "left");
		btOK.addActionListener(e -> doDialogCancel());

		cbPeakSuppression = new JCheckBox(VNAMessages.getString("VNACableLengthDialog.cbAverage"));
		cbPeakSuppression.setSelected(true);
		getContentPane().add(cbPeakSuppression, "center");

		btMeasure = new JButton(VNAMessages.getString("VNACableLengthDialog.btMeasure.text"));
		getContentPane().add(btMeasure, "right,wrap");
		btMeasure.addActionListener(e -> doMeasure());

		// ***********************************************************************
		statusBar = new StatusBar();
		getContentPane().add(statusBar, "span 3,grow");

		//
		JLabel lbl = new JLabel();
		lbl.setOpaque(true);
		statusBar.addZone("status", lbl, "*");

		doDialogInit();
		TraceHelper.exit(this, methodName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.data.IVNADataConsumer#consumeDataBlock(java.util.List)
	 */
	public void consumeDataBlock(List<VNABackgroundJob> jobs) {
		TraceHelper.entry(this, "consumeDataBlock");
		if (jobs.size() == 1) {
			VNASampleBlock rawData = jobs.get(0).getResult();
			if (rawData != null) {

				final IVNADriverMathHelper mathHelper = rawData.getMathHelper();
				if (mathHelper != null) {
					// yes
					final VNACalibrationBlock mainCalibrationBlock = datapool.getMainCalibrationBlock();

					// also a calibration block available
					if (mainCalibrationBlock != null) {
						// yes
						// create a resized one matching the frequency range
						final VNACalibrationBlock resizedCalibrationBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(mainCalibrationBlock, rawData.getStartFrequency(), rawData.getStopFrequency(), rawData.getNumberOfSteps());

						// calibrate them
						final VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(resizedCalibrationBlock);
						context.setConversionTemperature(rawData.getDeviceTemperature());

						VNACalibratedSampleBlock samples = mathHelper.createCalibratedSamples(context, rawData);

						// create helper
						VNACableMeasurementHelper helper = new VNACableMeasurementHelper((mathHelper.getDriver().getDeviceInfoBlock().getMinPhase() < 0), false);

						List<VNACalibratedSample> allPoints = helper.findAllCrossingPoints(samples);

						if (allPoints.size() < 4) {
							JOptionPane.showMessageDialog(getOwner(), VNAMessages.getString("VNACableLossDialog.TooShort.msg"), VNAMessages.getString("VNACableLossDialog.TooShort.title"), JOptionPane.WARNING_MESSAGE);
						} else {
							tblCrossings.getModel().clear();
							for (VNACalibratedSample aSample : allPoints) {
								tblCrossings.getModel().add(aSample);
							}
							tblCrossings.updateUI();
							lblImage.setChart(createChart(allPoints));
						}
					} else {
						JOptionPane.showMessageDialog(getOwner(), VNAMessages.getString("VNACableLossDialog.InternalError.msg"), VNAMessages.getString("VNACableLossDialog.InternalError.title"), JOptionPane.WARNING_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(getOwner(), VNAMessages.getString("VNACableLossDialog.InternalError.msg"), VNAMessages.getString("VNACableLossDialog.InternalError.title"), JOptionPane.WARNING_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(getOwner(), VNAMessages.getString("VNACableLossDialog.InternalError.msg"), VNAMessages.getString("VNACableLossDialog.InternalError.title"), JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(getOwner(), VNAMessages.getString("VNACableLossDialog.InternalError.msg"), VNAMessages.getString("VNACableLossDialog.InternalError.title"), JOptionPane.WARNING_MESSAGE);
		}
		// enable user actions
		btMeasure.setEnabled(true);
		btOK.setEnabled(true);

		// setup cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		TraceHelper.exit(this, "consumeDataBlock");
	}

	/**
	 * 
	 * @return
	 */
	private JFreeChart createChart(List<VNACalibratedSample> allPoints) {
		TraceHelper.entry(this, "createChart");

		// correct data
		for (VNACalibratedSample data : allPoints) {
			data.setReflectionLoss(-data.getReflectionLoss() / 2.0);
		}

		if (cbPeakSuppression.isSelected())
			calculateMovingAverage(allPoints);

		// create data
		XYSeries xySeries = new XYSeries(VNAMessages.getString("VNACableLossDialog.1"));
		for (VNACalibratedSample data : allPoints) {
			xySeries.add(data.getFrequency(), data.getReflectionLoss());
		}

		// create collection

		XYSeriesCollection xysc = new XYSeriesCollection();
		xysc.addSeries(xySeries);

		JFreeChart chart = ChartFactory.createXYLineChart(null, VNAMessages.getString("VNACableLossDialog.3"), VNAMessages.getString("VNACableLossDialog.1"), xysc, PlotOrientation.VERTICAL, false, false, false);

		XYPlot plot = chart.getXYPlot();
		NumberAxis rangeAxis = new NumberAxis(xySeries.getKey().toString());
		rangeAxis.setLabelFont(LABEL_FONT);
		rangeAxis.setTickLabelFont(TICK_FONT);

		plot.setRangeAxis(0, rangeAxis);
		plot.setDataset(0, xysc);

		if (rangeAxis.getUpperBound() < 1.0) {
			rangeAxis.setAutoRange(false);
			rangeAxis.setUpperBound(1.0);
		}

		plot.mapDatasetToRangeAxis(0, 0);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.darkGray);
		plot.setRangeGridlinePaint(Color.darkGray);

		plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
		plot.getRenderer(0).setSeriesPaint(0, COLOR_LEFT_SCALE);
		plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));

		// change the auto tick unit selection to integer units only...
		rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// set font for x-axis
		plot.getDomainAxis().setLabelFont(LABEL_FONT);
		plot.getDomainAxis().setTickLabelFont(TICK_FONT);

		// OPTIONAL CUSTOMISATION COMPLETED.
		TraceHelper.exit(this, "createChart");
		return chart;
	}

	private void calculateMovingAverage(List<VNACalibratedSample> pData) {
		TraceHelper.entry(this, "calculateMovingAverage");

		final double[] coeff = {
				0.25,
				0.25,
				0.25,
				0.25,
		};
		final int numCoeff = coeff.length;

		if (pData.size() > numCoeff) {

			final double[] latches = new double[numCoeff];
			final int numSamples = pData.size();

			// setup the latches
			// copy sample data reversed to the latches
			for (int c = 0; c < numCoeff; ++c) {
				latches[numCoeff - c - 1] = pData.get(c).getReflectionLoss();
			}

			// for every point
			// starting at the first position after the initial values
			for (int i = numCoeff; i < numSamples; ++i) {
				// move latches one to the right aka 8 --> 9, 7 --> 8, ..., 0 --> 1
				for (int x = numCoeff - 1; x > 0; --x) {
					latches[x] = latches[x - 1];
				}

				// put current values first
				latches[0] = pData.get(i).getReflectionLoss();

				double newSample = 0;

				// calculate sum of latches weighted by the coefficients
				for (int c = 0; c < numCoeff; ++c) {
					newSample += (coeff[c] * latches[c]);
				}

				// set new value into sample
				pData.get(i).setReflectionLoss(newSample);
			}
		}
		TraceHelper.exit(this, "calculateMovingAverage");
	}

	/**
	 * 
	 */
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	/**
	 * 
	 */
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");

		((JLabel) statusBar.getZone("status")).setText(VNAMessages.getString("VNACableLossDialog.description"));
		// add escape key to window
		addEscapeKey();
		//
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	private void doMeasure() {
		TraceHelper.entry(this, "doMeasure");
		long startFreq = datapool.getMainCalibrationBlock().getStartFrequency();
		long stopFreq = datapool.getMainCalibrationBlock().getStopFrequency();
		// setup cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		btMeasure.setEnabled(false);
		btOK.setEnabled(false);

		// create on instance
		VNABackgroundJob job = new VNABackgroundJob();
		job.setNumberOfSamples(1000);
		job.setFrequencyRange(new VNAFrequencyRange(startFreq, stopFreq));
		job.setScanMode(VNAScanMode.MODE_REFLECTION);
		job.setSpeedup(1);

		VnaBackgroundTask backgroundTask = new VnaBackgroundTask(datapool.getDriver());
		backgroundTask.addJob(job);
		backgroundTask.setStatusLabel((JLabel) statusBar.getZone("status"));
		backgroundTask.addDataConsumer(this);
		backgroundTask.execute();
		TraceHelper.exit(this, "doMeasure");
	}
}