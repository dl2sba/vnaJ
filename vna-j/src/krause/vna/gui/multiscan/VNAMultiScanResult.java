package krause.vna.gui.multiscan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JInternalFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYSeries;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.export.JFSeries;
import krause.vna.export.SWRLogarithmicAxis;
import krause.vna.gui.scale.VNAMeasurementScale;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public class VNAMultiScanResult extends JInternalFrame {
	private VNADataPool datapool = VNADataPool.getSingleton();

	private final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	private ChartPanel lblDiag;

	private VNAMeasurementScale mainFrameLeftScale;
	private long startFrequency = 1000000;
	private long stopFrequency = 2000000;
	private final Font TICK_FONT = new Font("SansSerif", Font.PLAIN, 10);

	public VNAMultiScanResult(VNAMultiScanControl vnaMultiScanControl, long startFrq, long stopFrq, VNAMeasurementScale pScale) {
		super("Result", true, false, false, true);
		startFrequency = startFrq;
		stopFrequency = stopFrq;

		mainFrameLeftScale = pScale;

		setLocation(50, 50);
		setSize(538, 306);
		setBackground(Color.YELLOW);

		lblDiag = new ChartPanel(null);
		lblDiag.setChart(createChart());
		getContentPane().add(lblDiag, BorderLayout.CENTER);

		updateTitle();

		setVisible(true);
	}

	public void consumeSampleBlock(VNASampleBlock data) {
		TraceHelper.entry(this, "consumeSampleBlock");

		final IVNADriverMathHelper mathHelper = data.getMathHelper();
		// math helper set?
		if (mathHelper != null) {
			// yes
			// apply filtering to raw IQ data
			mathHelper.applyFilter(data.getSamples());

			// is a main calibration block set?
			if (datapool.getMainCalibrationBlock() != null) {
				// yes
				// now create a resized cal block for exactly this scan data
				final VNACalibrationBlock calBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(datapool.getMainCalibrationBlock(), data.getStartFrequency(), data.getStopFrequency(), data.getNumberOfSteps());

				// create calibration context for exactly this scan
				final VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(calBlock);
				context.setConversionTemperature(data.getDeviceTemperature());

				// and create the calibrated samples based on the data
				final VNACalibratedSampleBlock scanResult = mathHelper.createCalibratedSamples(context, data);

				// and send data to chart
				updateSeriesInChart(scanResult);
			}
		}

		TraceHelper.exit(this, "consumeSampleBlock");
	}

	protected JFreeChart createChart() {
		TraceHelper.entry(this, "createChart");

		JFreeChart chart = ChartFactory.createXYLineChart("", VNAMessages.getString("Plot.frequency"), null, null, PlotOrientation.VERTICAL, true, // include
				// legend
				true, // tooltips
				false // urls
		);

		// setup legend
		chart.getLegend().setItemFont(LABEL_FONT);
		chart.getTitle().setFont(LABEL_FONT);

		XYPlot plot = chart.getXYPlot();

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customization...
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.darkGray);
		plot.setRangeGridlinePaint(Color.darkGray);

		plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
		plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

		plot.getRenderer(0).setSeriesPaint(0, Color.BLACK);

		// plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(1));

		// set font for x-axis
		plot.getDomainAxis().setLabelFont(LABEL_FONT);
		plot.getDomainAxis().setTickLabelFont(TICK_FONT);

		// OPTIONAL CUSTOMISATION COMPLETED.
		TraceHelper.exit(this, "createChart");
		return chart;
	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	/**
	 * 
	 * @param series
	 * @return
	 */
	private NumberAxis generateRangeAxisBasedOnScale(JFSeries series) {
		NumberAxis rangeAxis = null;

		SCALE_TYPE scaleTypeNo = series.getScale().getScale().getType();

		if (scaleTypeNo != SCALE_TYPE.SCALE_NONE) {
			if (scaleTypeNo == SCALE_TYPE.SCALE_SWR) {
				rangeAxis = new SWRLogarithmicAxis(series.getDataset().getSeries(0).getKey().toString());
				NumberFormat nf = new DecimalFormat("0:1");
				rangeAxis.setNumberFormatOverride(nf);

				rangeAxis.setAutoRange(false);
				rangeAxis.setRange(series.getScale().getScale().getCurrentMinValue(), series.getScale().getScale().getCurrentMaxValue());
				rangeAxis.setRangeType(RangeType.FULL);
				rangeAxis.setAutoTickUnitSelection(true);

				rangeAxis.setTickMarksVisible(true);
				rangeAxis.setTickLabelsVisible(true);
			} else {
				rangeAxis = new NumberAxis(series.getDataset().getSeries(0).getKey().toString());
				rangeAxis.setAutoRange(false);
				rangeAxis.setRange(series.getScale().getScale().getCurrentMinValue(), series.getScale().getScale().getCurrentMaxValue());

				rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				rangeAxis.setAutoRangeIncludesZero(false);
				rangeAxis.setInverted(false);
			}
			rangeAxis.setLabelFont(LABEL_FONT);
			rangeAxis.setTickLabelFont(TICK_FONT);

		}
		return rangeAxis;
	}

	public VNAScanMode getScanMode() {
		return datapool.getScanMode();
	}

	public long getStartFrequency() {
		return startFrequency;
	}

	public long getStopFrequency() {
		return stopFrequency;
	}

	public void setStartFrequency(long startFrequency) {
		this.startFrequency = startFrequency;
	}

	public void setStopFrequency(long stopFrequency) {
		this.stopFrequency = stopFrequency;
	}

	public String toString() {
		return getTitle();
	}

	/**
	 * create a new data series in the chart based on the calibrated samples
	 * 
	 * @param dataList
	 */
	private void updateSeriesInChart(VNACalibratedSampleBlock calibratedSamples) {
		final VNACalibratedSample[] dataList = calibratedSamples.getCalibratedSamples();
		final JFSeries series = new JFSeries(mainFrameLeftScale);
		final XYPlot plot = lblDiag.getChart().getXYPlot();
		final SCALE_TYPE scaleTypeNo = mainFrameLeftScale.getScale().getType();

		if (scaleTypeNo != SCALE_TYPE.SCALE_NONE) {

			final XYSeries xySeries = new XYSeries(mainFrameLeftScale.getScale().toString());

			for (int i = 0; i < dataList.length; ++i) {
				VNACalibratedSample data = dataList[i];
				xySeries.add(data.getFrequency(), data.getDataByScaleType(scaleTypeNo));
			}
			series.setSeries(xySeries);

			final NumberAxis rangeAxis1 = generateRangeAxisBasedOnScale(series);
			plot.setRangeAxis(0, rangeAxis1);
			plot.setDataset(0, series.getDataset());
			plot.mapDatasetToRangeAxis(0, 0);
		}
	}

	private void updateTitle() {
		String msg = "Scan {0}-{1}";
		setTitle(MessageFormat.format(msg, getStartFrequency(), getStopFrequency()));
	}
}
