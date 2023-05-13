package krause.vna.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JPanel;
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

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.export.JFSeries;
import krause.vna.gui.util.SwingUtil;

/**
 * 
 * @author Dietmar
 * 
 */
public class VNATemperatureDetailsDialog extends KrauseDialog {
	private static final float STROKE_WIDTH = 1;
	private final Font fontLabel = new Font("SansSerif", Font.PLAIN, 10);
	private final Font fontTick = new Font("SansSerif", Font.PLAIN, 10);

	private JFreeChart chart;
	private ChartPanel lblImage;
	private JButton btCancel;

	/**
	 * Create the dialog.
	 */
	public VNATemperatureDetailsDialog(Window pMainFrame, double[] tempList) {
		super(pMainFrame, true);
		TraceHelper.entry(this, "VNATemperatureDetailsDialog.java");

		chart = createChart(tempList);

		setTitle("Device temperature history");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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
		TraceHelper.exit(this, "VNATemperatureDetailsDialog.java");
	}

	/**
	 * 
	 * @return
	 */
	private JFreeChart createChart(double[] tempList) {
		TraceHelper.entry(this, "createChart");
		XYPlot plot;

		JFSeries series1 = new JFSeries();
		XYSeries xySeries1 = new XYSeries("Temperature °C");

		int i = 0;
		for (double data : tempList) {
			xySeries1.add(i++, data);
		}
		series1.setSeries(xySeries1);

		final JFreeChart rcChart = ChartFactory.createXYLineChart("Temperature", "Samples", null, null, PlotOrientation.VERTICAL, true, false, false);

		plot = rcChart.getXYPlot();
		NumberAxis rangeAxis1 = new NumberAxis(series1.getDataset().getSeries(0).getKey().toString());
		rangeAxis1.setLabelFont(fontLabel);
		rangeAxis1.setTickLabelFont(fontTick);
		rangeAxis1.setAutoRange(false);
		rangeAxis1.setRange(xySeries1.getMinY() - 5.0, xySeries1.getMaxY() + 5.0);
		plot.setRangeAxis(0, rangeAxis1);
		plot.setDataset(0, series1.getDataset());
		plot.mapDatasetToRangeAxis(0, 0);

		rcChart.setBackgroundPaint(Color.white);

		plot = rcChart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.darkGray);
		plot.setRangeGridlinePaint(Color.darkGray);

		plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
		plot.getRenderer(0).setSeriesPaint(0, Color.RED);
		plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));

		// set font for x-axis
		plot.getDomainAxis().setLabelFont(fontLabel);
		plot.getDomainAxis().setTickLabelFont(fontTick);

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