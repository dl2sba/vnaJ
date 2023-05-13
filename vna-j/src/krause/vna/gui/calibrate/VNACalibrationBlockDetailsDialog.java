package krause.vna.gui.calibrate;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

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
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.export.JFSeries;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

/**
 * 
 * @author Dietmar
 * 
 */
public class VNACalibrationBlockDetailsDialog extends KrauseDialog {
	private static final float STROKE_WIDTH = 1;
	private final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	private final Font TICK_FONT = new Font("SansSerif", Font.PLAIN, 10);

	private JFreeChart chart;
	private ChartPanel lblImage;
	private JButton btCancel;

	/**
	 * Create the dialog.
	 */
	public VNACalibrationBlockDetailsDialog(Frame pMainFrame, VNACalibrationBlock block, String headerID) {
		super(pMainFrame, true);
		TraceHelper.entry(this, "VNACalibrationBlockDetailsDialog");

		chart = createChart(block, headerID);

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
		btCancel = SwingUtil.createJButton("Button.OK", e -> setVisible(false));
		buttonPane.add(btCancel, BorderLayout.EAST);
		btCancel.setActionCommand("Cancel");

		getRootPane().setDefaultButton(btCancel);

		doDialogInit();
		TraceHelper.exit(this, "VNACalibrationBlockDetailsDialog");
	}

	/**
	 * 
	 * @return
	 */
	private JFreeChart createChart(VNACalibrationBlock block, String headerID) {
		TraceHelper.entry(this, "createChart");
		XYPlot plot;
		NumberAxis rangeAxis;

		JFSeries series1 = new JFSeries();
		JFSeries series2 = new JFSeries();

		XYSeries xySeries1 = new XYSeries("E00-real");
		XYSeries xySeries2 = new XYSeries("E00-imag");

		for (VNACalibrationPoint data : block.getCalibrationPoints()) {
			xySeries1.add(data.getFrequency(), data.getDeltaE().getReal());
			xySeries2.add(data.getFrequency(), data.getDeltaE().getImaginary());
		}
		series1.setSeries(xySeries1);
		series2.setSeries(xySeries2);

		JFreeChart chart = ChartFactory.createXYLineChart(VNAMessages.getString(headerID), VNAMessages.getString("Plot.frequency"), null, null, PlotOrientation.VERTICAL, true, false, false);

		plot = chart.getXYPlot();
		rangeAxis = new NumberAxis(series1.getDataset().getSeries(0).getKey().toString());
		rangeAxis.setLabelFont(LABEL_FONT);
		rangeAxis.setTickLabelFont(TICK_FONT);
		plot.setRangeAxis(0, rangeAxis);
		plot.setDataset(0, series1.getDataset());
		plot.mapDatasetToRangeAxis(0, 0);

		plot = chart.getXYPlot();
		rangeAxis = new NumberAxis(series2.getDataset().getSeries(0).getKey().toString());
		rangeAxis.setLabelFont(LABEL_FONT);
		rangeAxis.setTickLabelFont(TICK_FONT);
		plot.setRangeAxis(1, rangeAxis);
		plot.setDataset(1, series2.getDataset());
		plot.mapDatasetToRangeAxis(1, 1);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.darkGray);
		plot.setRangeGridlinePaint(Color.darkGray);

		plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
		plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

		plot.getRenderer(0).setSeriesPaint(0, Color.RED);
		plot.getRenderer(1).setSeriesPaint(0, Color.BLUE);

		plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));
		plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));

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
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}
}