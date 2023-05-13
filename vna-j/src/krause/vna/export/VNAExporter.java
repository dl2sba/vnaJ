package krause.vna.export;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPolygonAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAMeasurementScale;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public abstract class VNAExporter {
	protected VNAConfig config = VNAConfig.getSingleton();
	protected VNADataPool datapool = VNADataPool.getSingleton();

	protected static final Color COLOR_LEFT_SCALE = Color.BLUE;
	protected static final Color COLOR_RIGHT_SCALE = Color.RED;

	protected static final Font LABEL_FONT = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 30);
	protected static final Font TICK_FONT = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 25);
	protected static final Font MARKER_FONT = new java.awt.Font("Courier", java.awt.Font.BOLD, 15);

	protected VNAMainFrame mainFrame;
	private JFSeries series1;
	private JFSeries series2;
	private SCALE_TYPE scaleType1;
	private SCALE_TYPE scaleType2;
	private NumberAxis rangeAxis1;
	private NumberAxis rangeAxis2;

	protected JFSeries generateSeriesBasedOnScale(VNAMeasurementScale scale, VNACalibratedSample[] dataList) {
		TraceHelper.entry(this, "generateSeriesBasedOnScale");
		JFSeries series = new JFSeries(scale);

		SCALE_TYPE scaleTypeNo = scale.getScale().getType();

		if (scaleTypeNo != SCALE_TYPE.SCALE_NONE) {

			XYSeries xySeries = new XYSeries(scale.getScale().toString());

			for (int i = 0; i < dataList.length; ++i) {
				VNACalibratedSample data = dataList[i];
				xySeries.add(data.getFrequency(), data.getDataByScaleType(scaleTypeNo));
			}
			series.setSeries(xySeries);
		}
		TraceHelper.exit(this, "generateSeriesBasedOnScale");
		return series;
	}

	/**
	 * 
	 * Date: {0} Mode: {1} Analyser: {2} {3} Scan Start: {4} / {6} Stop: {5} / {7} Samples: {8} Overscan: {9}
	 * 
	 * Calibration Samples: {10} Overscan: {11} File: {12}
	 * 
	 * User: {13}
	 * 
	 * @param in
	 * @return
	 */
	public String replaceParameters(String in) {
		String rc = null;
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(0);
		nf.setMaximumFractionDigits(0);
		nf.setMinimumIntegerDigits(1);
		nf.setMaximumIntegerDigits(10);
		nf.setGroupingUsed(false);
		nf.setParseIntegerOnly(true);

		String pexVf = "<not set>";
		String pexLen = "<not set>";

		if (config.isPortExtensionEnabled()) {
			final double vf = config.getPortExtensionVf();
			final double len = config.getPortExtensionCableLength();

			pexVf = VNAFormatFactory.getVelocityFormat().format(vf);
			pexLen = VNAFormatFactory.getLengthFormat().format(len);
		}

		Object[] parms = {
				new Time(System.currentTimeMillis()),
				datapool.getScanMode().toString(),
				datapool.getDriver().getDeviceInfoBlock().getShortName(),
				datapool.getDriver().getDeviceInfoBlock().getLongName(),
				nf.format(datapool.getFrequencyRange().getStart()),
				nf.format(datapool.getFrequencyRange().getStop()),
				VNAFormatFactory.getFrequencyFormat().format(datapool.getFrequencyRange().getStart()),
				VNAFormatFactory.getFrequencyFormat().format(datapool.getFrequencyRange().getStop()),
				nf.format(datapool.getCalibratedData().getCalibratedSamples().length),
				"?",
				nf.format(datapool.getMainCalibrationBlock().getNumberOfSteps()),
				nf.format(datapool.getMainCalibrationBlock().getNumberOfOverscans()),
				datapool.getMainCalibrationBlock().getFile() != null ? datapool.getMainCalibrationBlock().getFile().getName() : "---",
				System.getProperty("user.name"),
				config.getExportTitle(),
				pexLen,
				pexVf,
		};
		rc = MessageFormat.format(in, parms);

		return rc;
	}

	/**
	 * 
	 * @param filenamePattern
	 * @param overwrite
	 * @return
	 */
	protected String check4FileToDelete(String filenamePattern, boolean overwrite) {
		String currFilename = replaceParameters(filenamePattern + getExtension());
		File fi = new File(currFilename);
		currFilename = fi.getAbsolutePath();
		if (fi.exists()) {
			if (overwrite) {
				fi.delete();
			} else {
				String msg = MessageFormat.format(VNAMessages.getString("Message.Export.1"), currFilename);
				int n = JOptionPane.showOptionDialog(mainFrame.getJFrame(), msg, VNAMessages // $NON-NLS-1$
						.getString("Message.Export.2"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, //$NON-NLS-1$
						null);
				if (n == 0) {
					fi.delete();
				} else {
					currFilename = null;
				}
			}
		}

		return currFilename;
	}

	/***
	 * for every visible marker in marker panel create a caret in chart
	 * 
	 * @param plot
	 */
	protected void createChartMarkers(XYPlot plot) {
		VNAMarkerPanel mp = mainFrame.getMarkerPanel();
		VNAMarker[] markers = mp.getMarkers();

		for (VNAMarker marker : markers) {
			if (marker.isVisible()) {
				createChartMarker(marker, plot, Color.BLACK);
			}
		}
	}

	/***
	 * Create a caret for a marker
	 * 
	 * @param marker
	 * @param plot
	 * @param col
	 */
	private void createChartMarker(VNAMarker marker, XYPlot plot, Color col) {

		int xFactor = 120 - 20 * config.getMarkerSize();
		int yFactor = 70 - 10 * config.getMarkerSize();

		ValueAxis da = plot.getDomainAxis(0);
		double x_w = da.getRange().getLength() / xFactor;
		double x = marker.getFrequency();

		if (series1 != null) {
			XYItemRenderer rend = plot.getRenderer(0);
			double y = marker.getSample().getDataByScaleType(scaleType1);
			double y_w = rangeAxis1.getRange().getLength() / yFactor;
			double ub = rangeAxis1.getRange().getUpperBound();

			if (ub > 0) {
				if ((y + y_w) > ub) {
					createMarkerUp(rend, col, x, y, x_w, y_w, marker.getName());
				} else {
					createMarkerDown(rend, col, x, y, x_w, y_w, marker.getName());
				}
			} else {
				if ((ub - y_w) < y) {
					createMarkerUp(rend, col, x, y, x_w, y_w, marker.getName());
				} else {
					createMarkerDown(rend, col, x, y, x_w, y_w, marker.getName());
				}
			}
		}

		if (series2 != null) {
			XYItemRenderer rend = plot.getRenderer(1);
			double y = marker.getSample().getDataByScaleType(scaleType2);
			double y_w = rangeAxis2.getRange().getLength() / yFactor;
			double ub = rangeAxis2.getRange().getUpperBound();

			if (ub > 0) {
				if ((y + y_w) > ub) {
					createMarkerUp(rend, col, x, y, x_w, y_w, marker.getName());
				} else {
					createMarkerDown(rend, col, x, y, x_w, y_w, marker.getName());
				}
			} else {
				if ((ub - y_w) < y) {
					createMarkerUp(rend, col, x, y, x_w, y_w, marker.getName());
				} else {
					createMarkerDown(rend, col, x, y, x_w, y_w, marker.getName());
				}
			}
		}
	}

	/**
	 * Draw an arrow with tip up
	 * 
	 * @param rend
	 * @param col
	 * @param x
	 * @param wx
	 * @param wy
	 * @param y
	 * @param name
	 */
	void createMarkerUp(XYItemRenderer rend, Color col, double x, double y, double wx, double wy, String name) {
		TraceHelper.entry(this, "createMarkerUp", "" + x + " " + y);
		XYPolygonAnnotation annShape = new XYPolygonAnnotation(new double[] {
				x,
				y,
				x + wx,
				y - wy,
				x - wx,
				y - wy,
				x,
				y
		}, new BasicStroke(2), col, null);
		rend.addAnnotation(annShape);

		XYTextAnnotation annText = new XYTextAnnotation(name, x, y - 2 * wy);
		annText.setFont(LABEL_FONT);
		rend.addAnnotation(annText);
	}

	/**
	 * Draw an arrow with tip down
	 * 
	 * @param rend
	 * @param col
	 * @param x
	 * @param wx
	 * @param wy
	 * @param y
	 * @param name
	 */
	void createMarkerDown(XYItemRenderer rend, Color col, double x, double y, double wx, double wy, String name) {
		TraceHelper.entry(this, "createMarkerDown", "" + x + " " + y);
		XYPolygonAnnotation annShape = new XYPolygonAnnotation(new double[] {
				x,
				y,
				x + wx,
				y + wy,
				x - wx,
				y + wy,
				x,
				y
		}, new BasicStroke(2), col, null);
		rend.addAnnotation(annShape);

		XYTextAnnotation annText = new XYTextAnnotation(name, x, y + 2 * wy);
		annText.setFont(LABEL_FONT);
		rend.addAnnotation(annText);
	}

	/**
	 * 
	 * @param dataList
	 * @return
	 */
	public JFreeChart createChart(VNACalibratedSample[] dataList) {
		TraceHelper.entry(this, "createChart");

		final VNAMeasurementScale scale1 = mainFrame.getDiagramPanel().getScaleLeft();
		scaleType1 = scale1.getScale().getType();
		if (scaleType1 != SCALE_TYPE.SCALE_NONE) {
			series1 = generateSeriesBasedOnScale(scale1, dataList);
		}

		final VNAMeasurementScale scale2 = mainFrame.getDiagramPanel().getScaleRight();
		scaleType2 = scale2.getScale().getType();
		if (scaleType2 != SCALE_TYPE.SCALE_NONE) {
			if (series1 == null) {
				series1 = generateSeriesBasedOnScale(scale2, dataList);
				scaleType1 = scale2.getScale().getType();
			} else {
				series2 = generateSeriesBasedOnScale(scale2, dataList);
			}
		}
		String title = (config.isPrintMainLegend()) ? replaceParameters(config.getExportTitle()) : null;

		JFreeChart chart = ChartFactory.createXYLineChart(title, VNAMessages.getString("Plot.frequency"), null, null, PlotOrientation.VERTICAL, config.isPrintFooter(), true, false);

		// setup legend
		if (config.isPrintFooter()) {
			chart.getLegend().setItemFont(LABEL_FONT);
		}

		// setup title
		if (config.isPrintMainLegend()) {
			chart.getTitle().setFont(LABEL_FONT);
		}

		if (config.isPrintSubLegend()) {
			TextTitle source = new TextTitle(generateLegend(scale1, scale2));
			source.setFont(LABEL_FONT);
			source.setPosition(RectangleEdge.TOP);
			source.setHorizontalAlignment(HorizontalAlignment.CENTER);
			chart.addSubtitle(source);
		}

		final float[] dash = {
				10.0f
		};

		if (series1 != null) {
			final XYPlot plot = chart.getXYPlot();
			rangeAxis1 = generateRangeAxisBasedOnScale(series1);
			plot.setRangeAxis(0, rangeAxis1);
			plot.setDataset(0, series1.getDataset());
			plot.mapDatasetToRangeAxis(0, 0);

			rangeAxis1.setLabelPaint(COLOR_LEFT_SCALE);

			final Double av = scale1.getScale().getGuideLineValue();
			if (av != null) {
				final Marker marker = new ValueMarker(av);
				final VNAGenericScale theScale = scale1.getScale();
				marker.setLabelOffset(new RectangleInsets(5, 70, 0, 0));
				marker.setPaint(COLOR_LEFT_SCALE);
				marker.setLabelFont(LABEL_FONT);
				marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
				marker.setLabelTextAnchor(TextAnchor.BASELINE_CENTER);
				marker.setLabel(theScale.getFormattedValueAsStringWithUnit(av));
				marker.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
				plot.addRangeMarker(0, marker, Layer.FOREGROUND);
			}
		}

		if (series2 != null) {
			final XYPlot plot = chart.getXYPlot();
			rangeAxis2 = generateRangeAxisBasedOnScale(series2);
			plot.setRangeAxis(1, rangeAxis2);
			plot.setDataset(1, series2.getDataset());
			plot.mapDatasetToRangeAxis(1, 1);

			rangeAxis2.setLabelPaint(COLOR_RIGHT_SCALE);

			final Double av = scale2.getScale().getGuideLineValue();
			if (av != null) {
				final Marker marker = new ValueMarker(av);
				final VNAGenericScale theScale = scale2.getScale();
				marker.setLabelOffset(new RectangleInsets(5, 0, 100, 70));
				marker.setPaint(COLOR_RIGHT_SCALE);
				marker.setLabelFont(LABEL_FONT);
				marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
				marker.setLabelTextAnchor(TextAnchor.BASELINE_CENTER);
				marker.setLabel(theScale.getFormattedValueAsStringWithUnit(av));
				marker.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
				plot.addRangeMarker(1, marker, Layer.FOREGROUND);
			}
		}

		// get a reference to the plot for further customization...
		chart.setBackgroundPaint(Color.white);

		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.darkGray);
		plot.setRangeGridlinePaint(Color.darkGray);

		plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
		plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

		plot.getRenderer(0).setSeriesPaint(0, COLOR_LEFT_SCALE);
		plot.getRenderer(1).setSeriesPaint(0, COLOR_RIGHT_SCALE);

		plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(1));
		plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(1));

		// create markers
		createChartMarkers(plot);

		// set font for x-axis
		plot.getDomainAxis().setLabelFont(LABEL_FONT);
		plot.getDomainAxis().setTickLabelFont(TICK_FONT);

		if (config.isPrintMarkerDataInDiagramm()) {
			createTextMarkers(chart);
		}

		// OPTIONAL CUSTOMISATION COMPLETED.
		TraceHelper.exit(this, "createChart");
		return chart;
	}

	/**
	 * 
	 * @param scale12
	 * @param scale22
	 * @return
	 */
	private String generateLegend(VNAMeasurementScale scale11, VNAMeasurementScale scale22) {
		String rc = "";

		if (scale11.getScale().getType() != SCALE_TYPE.SCALE_NONE) {
			rc += scale11.getScale().getName() + "=" + scale11.getScale().getDescription() + "    ";
		}
		if (scale22.getScale().getType() != SCALE_TYPE.SCALE_NONE) {
			rc += scale22.getScale().getName() + "=" + scale22.getScale().getDescription();
		}
		return rc;
	}

	/**
	 * 
	 * @param series
	 * @return
	 */
	private NumberAxis generateRangeAxisBasedOnScale(JFSeries series) {
		NumberAxis rangeAxis = null;

		final SCALE_TYPE scaleTypeNo = series.getScale().getScale().getType();

		if (scaleTypeNo != SCALE_TYPE.SCALE_NONE) {
			if (scaleTypeNo == SCALE_TYPE.SCALE_SWR) {
				final SWRLogarithmicAxis logAxis = new SWRLogarithmicAxis(series.getDataset().getSeries(0).getKey().toString());
				logAxis.setLog10TickLabelsFlag(false);
				logAxis.setExpTickLabelsFlag(false);

				final NumberFormat nf = new DecimalFormat("0.0:1");
				logAxis.setNumberFormatOverride(nf);

				logAxis.setAutoRange(false);
				setRange(logAxis, series.getScale().getScale());
				logAxis.setRangeType(RangeType.FULL);

				logAxis.setMinorTickMarksVisible(true);
				logAxis.setTickMarksVisible(true);
				logAxis.setTickLabelsVisible(true);

				rangeAxis = logAxis;
			} else {
				rangeAxis = new NumberAxis(series.getDataset().getSeries(0).getKey().toString());
				rangeAxis.setAutoRange(false);
				// rangeAxis.setRange(series.getScale().getScale().getCurrentMinValue(),
				// series.getScale().getScale().getCurrentMaxValue())
				setRange(rangeAxis, series.getScale().getScale());
				rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				rangeAxis.setAutoRangeIncludesZero(false);
				rangeAxis.setInverted(false);
			}
			rangeAxis.setLabelFont(LABEL_FONT);
			rangeAxis.setTickLabelFont(TICK_FONT);

		}
		return rangeAxis;
	}

	private void setRange(NumberAxis pAxis, VNAGenericScale pScale) {
		double lower = pScale.getCurrentMinValue();
		double upper = pScale.getCurrentMaxValue();
		pAxis.setRange(Math.floor(lower), Math.ceil(upper));
	}

	/**
	 * 
	 * @param filenamePattern
	 * @param overwrite
	 * @return
	 * @throws ProcessingException
	 */
	public abstract String export(String filenamePattern, boolean overwrite) throws ProcessingException;

	public VNAExporter(VNAMainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
	}

	public abstract String getExtension();

	/***
	 * for every visible marker in markerpanel create a short textblock in diagram
	 * 
	 * @param chart
	 */
	private void createTextMarkers(JFreeChart chart) {
		TraceHelper.entry(this, "createTextMarkers");
		XYPlot plot = chart.getXYPlot();
		VNAMarkerPanel mp = mainFrame.getMarkerPanel();
		VNAMarker[] markers = mp.getMarkers();

		String txtFormat = "";
		txtFormat += "Marker {7}\n";
		txtFormat += "  " + VNAMessages.getString("Marker.Frequency") + " {0}\n";
		if (datapool.getScanMode().isReflectionMode()) {
			txtFormat += "  " + VNAMessages.getString("Marker.RL") + "    {1}\n";
			txtFormat += "  " + VNAMessages.getString("Marker.PhaseRL") + "     {2}\n";
			txtFormat += "  " + VNAMessages.getString("Marker.SWR") + "        {3}\n";
			txtFormat += "  " + VNAMessages.getString("Marker.Z") + "    {4}\n";
			txtFormat += "  " + VNAMessages.getString("Marker.R") + "     {5}\n";
			txtFormat += "  " + VNAMessages.getString("Marker.X") + "     {6}\n";
		} else {
			txtFormat += "  " + VNAMessages.getString("Marker.TL") + "    {1}\n";
			txtFormat += "  " + VNAMessages.getString("Marker.PhaseTL") + "     {2}\n";
			txtFormat += "  " + VNAMessages.getString("Marker.GrpDelay") + "        {3}\n";
		}

		String legend = "";

		if (config.isPrintMarkerDataHorizontal()) {
			for (VNAMarker marker : markers) {
				if (marker.isVisible()) {
					Object[] parms = new Object[] {
							marker.getFrequency(),
							marker.getTxtLoss().getText(),
							marker.getTxtPhase().getText(),
							marker.getTxtSwrGrpDelay().getText(),
							marker.getTxtZAbsolute().getText(),
							marker.getTxtRs().getText(),
							marker.getTxtXsAbsolute().getText(),
							marker.getName()
					};
					String msg = MessageFormat.format(txtFormat, parms);
					legend += msg + "  \n";
				}
			}
			Font lf = MARKER_FONT.deriveFont((float) config.getFontSizeTextMarker());
			TextTitle tt = new TextTitle(legend, lf);
			tt.setTextAlignment(HorizontalAlignment.LEFT);
			XYTitleAnnotation annotation = new XYTitleAnnotation(0.99, 1, tt, RectangleAnchor.TOP_RIGHT);
			plot.addAnnotation(annotation);
		} else {
			for (VNAMarker marker : markers) {
				if (marker.isVisible()) {
					Object[] parms = new Object[] {
							marker.getFrequency(),
							marker.getTxtLoss().getText(),
							marker.getTxtPhase().getText(),
							marker.getTxtSwrGrpDelay().getText(),
							marker.getTxtZAbsolute().getText(),
							marker.getTxtRs().getText(),
							marker.getTxtXsAbsolute().getText(),
							marker.getName()
					};
					String msg = MessageFormat.format(txtFormat, parms);
					legend += msg + "  \n";
				}
			}
			TextTitle tt = new TextTitle(legend, MARKER_FONT);
			tt.setTextAlignment(HorizontalAlignment.LEFT);
			XYTitleAnnotation annotation = new XYTitleAnnotation(0.01, 1, tt, RectangleAnchor.TOP_LEFT);
			plot.addAnnotation(annotation);
		}
		TraceHelper.exit(this, "createTextMarkers");
	}
}
