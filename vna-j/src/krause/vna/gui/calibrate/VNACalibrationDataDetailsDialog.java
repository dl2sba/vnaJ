package krause.vna.gui.calibrate;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNASampleBlock;
import krause.vna.export.JFSeries;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

/**
 * 
 * @author Dietmar
 * 
 */
public class VNACalibrationDataDetailsDialog extends KrauseDialog {
	private static final float STROKE_WIDTH = (float) 0.5;
	private final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	private final Font TICK_FONT = new Font("SansSerif", Font.PLAIN, 10);

	private JFreeChart chart;
	private ChartPanel lblImage;
	private JButton btOK;
	private VNASampleBlock sampleBlock;
	private final VNAConfig config = VNAConfig.getSingleton();
	private String typeId;

	/**
	 * Create the dialog.
	 */
	public VNACalibrationDataDetailsDialog(Window pMainFrame, VNASampleBlock samples, String headerID) {
		super(pMainFrame, true);
		TraceHelper.entry(this, "VNACalibrationDataDetailsDialog");

		typeId = headerID;
		sampleBlock = samples;
		chart = createChart();

		setTitle(VNAMessages.getString("VNACalibrationDataDetailsDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 689, 602);
		getContentPane().setLayout(new BorderLayout());
		lblImage = new ChartPanel(chart, true);
		getContentPane().add(lblImage, BorderLayout.CENTER);
		lblImage.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		lblImage.setIgnoreRepaint(true);
		lblImage.setPreferredSize(new Dimension(640, 480));
		lblImage.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new BorderLayout(0, 0));

		btOK = SwingUtil.createJButton("Button.OK", e -> setVisible(false));
		buttonPane.add(btOK, BorderLayout.EAST);

		btOK = SwingUtil.createJButton("Button.EXPORT", e -> doExport());
		buttonPane.add(btOK, BorderLayout.WEST);

		btOK.setActionCommand("Cancel");

		getRootPane().setDefaultButton(btOK);

		doDialogInit();
		TraceHelper.exit(this, "VNACalibrationDataDetailsDialog");
	}

	protected void doExport() {
		final String methodName = "doExport";
		TraceHelper.entry(this, methodName);

		try {
			final HSSFWorkbook workBook = new HSSFWorkbook();

			// add new sheet
			final HSSFSheet workSheet = workBook.createSheet("vnaJ");

			if ((sampleBlock.getSamples().length > 0) && (sampleBlock.getSamples()[0].hasPData())) {
				// add header rows
				int rowNum = 0;
				int cell = 0;
				HSSFRow row = workSheet.createRow(rowNum++);
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("Plot.frequency")));
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p1")));
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p2")));
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p3")));
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p4")));
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p1Ref")));
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p2Ref")));
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p3Ref")));
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p4Ref")));

				// add data rows
				for (VNABaseSample data : sampleBlock.getSamples()) {
					cell = 0;
					row = workSheet.createRow(rowNum);
					row.createCell(cell++).setCellValue(data.getFrequency());
					row.createCell(cell++).setCellValue(data.getP1());
					row.createCell(cell++).setCellValue(data.getP2());
					row.createCell(cell++).setCellValue(data.getP3());
					row.createCell(cell++).setCellValue(data.getP4());
					row.createCell(cell++).setCellValue(data.getP1Ref());
					row.createCell(cell++).setCellValue(data.getP2Ref());
					row.createCell(cell++).setCellValue(data.getP3Ref());
					row.createCell(cell++).setCellValue(data.getP4Ref());
					++rowNum;
				}
			} else {
				// add header rows
				int rowNum = 0;
				int cell = 0;
				HSSFRow row = workSheet.createRow(rowNum++);
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("Plot.frequency")));
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.1")));
				row.createCell(cell++).setCellValue(new HSSFRichTextString(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.2")));

				// add data rows
				for (VNABaseSample data : sampleBlock.getSamples()) {
					cell = 0;
					row = workSheet.createRow(rowNum);
					row.createCell(cell++).setCellValue(data.getFrequency());
					row.createCell(cell++).setCellValue(data.getLoss());
					row.createCell(cell++).setCellValue(data.getAngle());
					++rowNum;
				}
			}
			// generate filename
			String fn = config.getExportDirectory() + System.getProperty("file.separator") + "CalData_" + VNAMessages.getString(typeId) + "." + System.currentTimeMillis() + ".xls";

			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(fn);
			workBook.write(fileOut);
			fileOut.close();
		} catch (IOException e) {
			ErrorLogHelper.exception(this, methodName, e);
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 * @return
	 */
	private JFreeChart createChart() {
		TraceHelper.entry(this, "createChart");
		JFreeChart rcChart = null;
		XYPlot plot;

		if ((sampleBlock.getSamples().length > 0) && (sampleBlock.getSamples()[0].hasPData())) {
			JFSeries series1 = new JFSeries();
			JFSeries series2 = new JFSeries();
			JFSeries series3 = new JFSeries();
			JFSeries series4 = new JFSeries();

			XYSeries xySeries1 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p1"));
			XYSeries xySeries2 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p2"));
			XYSeries xySeries3 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p3"));
			XYSeries xySeries4 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.p4"));

			for (VNABaseSample data : sampleBlock.getSamples()) {
				long f = data.getFrequency();
				xySeries1.add(f, data.getP1());
				xySeries2.add(f, data.getP2());
				xySeries3.add(f, data.getP3());
				xySeries4.add(f, data.getP4());
			}
			series1.setSeries(xySeries1);
			series2.setSeries(xySeries2);
			series3.setSeries(xySeries3);
			series4.setSeries(xySeries4);

			rcChart = ChartFactory.createXYLineChart(VNAMessages.getString(typeId), VNAMessages.getString("Plot.frequency"), null, null, PlotOrientation.VERTICAL, true, false, false);

			plot = rcChart.getXYPlot();
			NumberAxis rangeAxis1 = new NumberAxis(series1.getDataset().getSeries(0).getKey().toString());
			rangeAxis1.setLabelFont(LABEL_FONT);
			rangeAxis1.setTickLabelFont(TICK_FONT);
			rangeAxis1.setAutoRange(false);
			rangeAxis1.setRange(xySeries1.getMinY(), xySeries1.getMaxY());
			plot.setRangeAxis(0, rangeAxis1);
			plot.setDataset(0, series1.getDataset());
			plot.mapDatasetToRangeAxis(0, 0);

			plot = rcChart.getXYPlot();
			NumberAxis rangeAxis2 = new NumberAxis(series2.getDataset().getSeries(0).getKey().toString());
			rangeAxis2.setLabelFont(LABEL_FONT);
			rangeAxis2.setTickLabelFont(TICK_FONT);
			rangeAxis2.setAutoRange(false);
			rangeAxis2.setRange(xySeries2.getMinY(), xySeries2.getMaxY());
			plot.setRangeAxis(1, rangeAxis2);
			plot.setDataset(1, series2.getDataset());
			plot.mapDatasetToRangeAxis(1, 1);

			plot = rcChart.getXYPlot();
			NumberAxis rangeAxis3 = new NumberAxis(series3.getDataset().getSeries(0).getKey().toString());
			rangeAxis3.setLabelFont(LABEL_FONT);
			rangeAxis3.setTickLabelFont(TICK_FONT);
			rangeAxis3.setAutoRange(false);
			rangeAxis3.setRange(xySeries3.getMinY(), xySeries3.getMaxY());
			plot.setRangeAxis(2, rangeAxis3);
			plot.setDataset(2, series3.getDataset());
			plot.mapDatasetToRangeAxis(2, 2);

			plot = rcChart.getXYPlot();
			NumberAxis rangeAxis4 = new NumberAxis(series4.getDataset().getSeries(0).getKey().toString());
			rangeAxis4.setLabelFont(LABEL_FONT);
			rangeAxis4.setTickLabelFont(TICK_FONT);
			rangeAxis4.setAutoRange(false);
			rangeAxis4.setRange(xySeries4.getMinY(), xySeries4.getMaxY());
			plot.setRangeAxis(3, rangeAxis4);
			plot.setDataset(3, series4.getDataset());
			plot.mapDatasetToRangeAxis(3, 3);

			rcChart.setBackgroundPaint(Color.white);

			plot = rcChart.getXYPlot();
			plot.setBackgroundPaint(Color.white);
			plot.setDomainGridlinePaint(Color.darkGray);
			plot.setRangeGridlinePaint(Color.darkGray);

			plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
			plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));
			plot.setRenderer(2, new XYLineAndShapeRenderer(true, false));
			plot.setRenderer(3, new XYLineAndShapeRenderer(true, false));

			plot.getRenderer(0).setSeriesPaint(0, Color.RED);
			plot.getRenderer(1).setSeriesPaint(0, Color.BLUE);
			plot.getRenderer(2).setSeriesPaint(0, Color.GREEN);
			plot.getRenderer(3).setSeriesPaint(0, Color.BLACK);

			plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));
			plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));
			plot.getRenderer(2).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));
			plot.getRenderer(3).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));

			// set font for x-axis
			plot.getDomainAxis().setLabelFont(LABEL_FONT);
			plot.getDomainAxis().setTickLabelFont(TICK_FONT);
		} else {
			JFSeries series1 = new JFSeries();
			JFSeries series2 = new JFSeries();

			XYSeries xySeries1 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.1"));
			XYSeries xySeries2 = new XYSeries(VNAMessages.getString("VNACalibrationDataDetailsDialog.Series.2"));

			for (VNABaseSample data : sampleBlock.getSamples()) {
				xySeries1.add(data.getFrequency(), data.getLoss());
				xySeries2.add(data.getFrequency(), data.getAngle());
			}
			series1.setSeries(xySeries1);
			series2.setSeries(xySeries2);

			rcChart = ChartFactory.createXYLineChart(VNAMessages.getString(typeId), VNAMessages.getString("Plot.frequency"), null, null, PlotOrientation.VERTICAL, true, false, false);

			plot = rcChart.getXYPlot();
			NumberAxis rangeAxis1 = new NumberAxis(series1.getDataset().getSeries(0).getKey().toString());
			rangeAxis1.setLabelFont(LABEL_FONT);
			rangeAxis1.setTickLabelFont(TICK_FONT);
			rangeAxis1.setAutoRange(false);
			rangeAxis1.setRange(xySeries1.getMinY(), xySeries1.getMaxY());
			plot.setRangeAxis(0, rangeAxis1);
			plot.setDataset(0, series1.getDataset());
			plot.mapDatasetToRangeAxis(0, 0);

			plot = rcChart.getXYPlot();
			NumberAxis rangeAxis2 = new NumberAxis(series2.getDataset().getSeries(0).getKey().toString());
			rangeAxis2.setLabelFont(LABEL_FONT);
			rangeAxis2.setTickLabelFont(TICK_FONT);
			rangeAxis2.setAutoRange(false);
			rangeAxis2.setRange(xySeries2.getMinY(), xySeries2.getMaxY());
			plot.setRangeAxis(1, rangeAxis2);
			plot.setDataset(1, series2.getDataset());
			plot.mapDatasetToRangeAxis(1, 1);

			// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
			rcChart.setBackgroundPaint(Color.white);

			// get a reference to the plot for further customisation...
			plot = rcChart.getXYPlot();
			plot.setBackgroundPaint(Color.white);
			plot.setDomainGridlinePaint(Color.darkGray);
			plot.setRangeGridlinePaint(Color.darkGray);

			plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
			plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

			plot.getRenderer(0).setSeriesPaint(0, Color.RED);
			plot.getRenderer(1).setSeriesPaint(0, Color.BLUE);

			plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));
			plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));

			// set font for x-axis
			plot.getDomainAxis().setLabelFont(LABEL_FONT);
			plot.getDomainAxis().setTickLabelFont(TICK_FONT);
		}

		// OPTIONAL CUSTOMISATION COMPLETED.
		TraceHelper.exit(this, "createChart");
		return rcChart;
	}

	/**
	 * 
	 */
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doExit");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doExit");
	}

	/**
	 * 
	 */
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");

		lblImage.setChart(chart);
		addEscapeKey();
		showCentered(getWidth(), getHeight());
		TraceHelper.exit(this, "doInit");
	}
}