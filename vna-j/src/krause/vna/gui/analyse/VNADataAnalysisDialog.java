package krause.vna.gui.analyse;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ExtensionFileFilter;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAMinMaxPair;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.export.JFSeries;
import krause.vna.export.SWRLogarithmicAxis;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.VNAScaleSelectComboBox;
import krause.vna.gui.raw.VNARawHandler;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.gui.smith.VNASmithDiagramDialog;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNADataAnalysisDialog extends KrauseDialog {
	private static final float STROKE_WIDTH = 1;
	private static final int CHART_WIDTH = 1000;
	private static final int CHART_HEIGHT = 800;
	private VNACalibratedSampleBlock blockLeft;
	private VNACalibratedSampleBlock blockRight;
	JButton buttonExport;
	JButton buttonLoadLeft;
	JButton buttonLoadRight;
	JButton buttonSmithLeft;
	JButton buttonSmithRight;
	private VNAScaleSelectComboBox cbScaleLeft;
	private VNAScaleSelectComboBox cbScaleRight;
	JFreeChart chart = null;
	private ChartPanel chartPanel;
	private VNAConfig config = VNAConfig.getSingleton();
	private final String imgExtension = "jpg";
	private Font LABEL_FONT = new Font("Monospaced", Font.PLAIN, 10);
	private VNAMainFrame mainFrame;

	private final Font TICK_FONT = new Font("Monospaced", Font.PLAIN, 10);
	private JTextField txtFilenameLeft;
	private JTextField txtFilenameRight;

	public VNADataAnalysisDialog(VNAMainFrame pMainFrame) {
		super(null, false);
		TraceHelper.entry(this, "VNADataAnalysisDialog");

		setConfigurationPrefix("VNADataAnalysisDialog");
		setProperties(VNAConfig.getSingleton());

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		mainFrame = pMainFrame;
		setBounds(100, 100, 800, 636);
		setTitle(VNAMessages.getString("Dlg.Analysis.Title"));
		JPanel pnlButton = new JPanel();
		getContentPane().add(pnlButton, BorderLayout.SOUTH);

		buttonExport = SwingUtil.createJButton("Button.Save.JPG", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				doExport();
				TraceHelper.exit(this, "actionPerformed");
			}
		});
		pnlButton.add(buttonExport);

		JPanel pnlSelect = new JPanel();
		getContentPane().add(pnlSelect, BorderLayout.NORTH);
		pnlSelect.setLayout(new BorderLayout(0, 0));

		JPanel pnlLeft = new JPanel();
		pnlSelect.add(pnlLeft, BorderLayout.WEST);

		buttonLoadLeft = SwingUtil.createToolbarButton("Button.Load", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				doLoadLeft();
				TraceHelper.exit(this, "actionPerformed");
			}

		});
		pnlLeft.add(buttonLoadLeft);

		txtFilenameLeft = new JTextField(VNAMessages.getString("Dlg.Analysis.NoDatafile"));
		txtFilenameLeft.setEditable(false);
		txtFilenameLeft.setColumns(15);
		pnlLeft.add(txtFilenameLeft);

		cbScaleLeft = new VNAScaleSelectComboBox();
		cbScaleLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				updateChart();
				TraceHelper.exit(this, "actionPerformed");
			}
		});
		cbScaleLeft.setBackground(config.getColorScaleLeft());
		pnlLeft.add(cbScaleLeft);

		pnlLeft.add(buttonSmithLeft = SwingUtil.createToolbarButton("Panel.Scale.Smith", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				if (blockLeft != null) {
					new VNASmithDiagramDialog(blockLeft, blockLeft.getFile().getName());
				}
				TraceHelper.exit(this, "actionPerformed");
			}
		}));

		JPanel pnlRight = new JPanel();
		pnlSelect.add(pnlRight, BorderLayout.EAST);

		cbScaleRight = new VNAScaleSelectComboBox();
		cbScaleRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				updateChart();
				TraceHelper.exit(this, "actionPerformed");
			}
		});

		pnlRight.add(buttonSmithRight = SwingUtil.createToolbarButton("Panel.Scale.Smith", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				if (blockRight != null) {
					new VNASmithDiagramDialog(blockRight, blockRight.getFile().getName());
				}
				TraceHelper.exit(this, "actionPerformed");
			}
		}));

		cbScaleRight.setBackground(config.getColorScaleRight());
		pnlRight.add(cbScaleRight);

		txtFilenameRight = new JTextField(VNAMessages.getString("Dlg.Analysis.NoDatafile"));
		txtFilenameRight.setEditable(false);
		txtFilenameRight.setColumns(15);
		pnlRight.add(txtFilenameRight);

		buttonLoadRight = SwingUtil.createToolbarButton("Button.Load", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				doLoadRight();
				TraceHelper.exit(this, "actionPerformed");
			}
		});
		pnlRight.add(buttonLoadRight);

		createChart();
		chartPanel = new ChartPanel(chart, true);
		getContentPane().add(chartPanel, BorderLayout.CENTER);

		doDialogInit();
		TraceHelper.exit(this, "VNADataAnalysisDialog");
	}

	private void createChart() {
		XYPlot plot;

		chart = ChartFactory.createXYLineChart(null, VNAMessages.getString("Plot.frequency"), null, null, PlotOrientation.VERTICAL, true, true, false);

		plot = chart.getXYPlot();
		plot.setBackgroundPaint(config.getColorDiagram());
		plot.setDomainGridlinePaint(config.getColorDiagramLines());
		plot.setRangeGridlinePaint(config.getColorDiagramLines());

		plot.setRenderer(0, new XYLineAndShapeRenderer(true, false));
		plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

		plot.getRenderer(0).setSeriesPaint(0, config.getColorScaleLeft());
		plot.getRenderer(1).setSeriesPaint(0, config.getColorScaleRight());

		plot.getRenderer(0).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));
		plot.getRenderer(1).setSeriesStroke(0, new BasicStroke(STROKE_WIDTH));

		plot.getRenderer(0).setBaseToolTipGenerator(new VNATooltipRenderer(cbScaleLeft));
		plot.getRenderer(1).setBaseToolTipGenerator(new VNATooltipRenderer(cbScaleRight));
	}

	/**
	 * 
	 * @param series
	 * @param scale
	 * @param block
	 * @param minMaxPairs
	 * @return
	 */
	private NumberAxis createRangeAxisForScale(JFSeries series, VNAGenericScale scale, VNACalibratedSampleBlock block, HashMap<SCALE_TYPE, VNAMinMaxPair> minMaxPairs) {
		TraceHelper.entry(this, "createRangeAxisForScale");
		String scaleText = scale.getName();
		if (block != null) {
			scaleText += " / " + block.getFile().getName();
		}
		NumberAxis rc;

		if ((scale.getType() == SCALE_TYPE.SCALE_RETURNLOSS) || (scale.getType() == SCALE_TYPE.SCALE_TRANSMISSIONLOSS)) {
			rc = new NumberAxis(scaleText);
			rc.setAutoRange(false);
			rc.setAutoRangeIncludesZero(false);
			rc.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			rc.setInverted(false);
			if (block != null) {
				VNAMinMaxPair pair = minMaxPairs.get(scale.getType());
				if (pair != null) {
					rc.setRange(pair.getMinValue(), pair.getMaxValue());
				}
			}
		} else if ((scale.getType() == SCALE_TYPE.SCALE_RS) || (scale.getType() == SCALE_TYPE.SCALE_XS) || (scale.getType() == SCALE_TYPE.SCALE_Z_ABS) || (scale.getType() == SCALE_TYPE.SCALE_RETURNPHASE) || (scale.getType() == SCALE_TYPE.SCALE_TRANSMISSIONPHASE)) {
			rc = new NumberAxis(scaleText);
			rc.setAutoRange(false);
			rc.setAutoRangeIncludesZero(false);
			rc.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			rc.setInverted(false);
			if (block != null) {
				VNAMinMaxPair pair = minMaxPairs.get(scale.getType());
				if (pair != null) {
					rc.setRange(pair.getMinValue(), pair.getMaxValue());
				}
			}
		} else if (scale.getType() == SCALE_TYPE.SCALE_SWR) {
			rc = new SWRLogarithmicAxis(scaleText);
			NumberFormat nf = new DecimalFormat("0:1");
			rc.setNumberFormatOverride(nf);
			rc.setAutoRange(false);
			rc.setRange(1, 10);
			rc.setRangeType(RangeType.FULL);
			rc.setAutoTickUnitSelection(true);
			rc.setTickMarksVisible(true);
			rc.setTickLabelsVisible(true);
		} else {
			rc = new NumberAxis(scaleText);
		}

		rc.setLabelFont(LABEL_FONT);
		rc.setTickLabelFont(TICK_FONT);

		TraceHelper.exit(this, "createRangeAxisForScale");
		return rc;
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");

	}

	private void doExport() {
		TraceHelper.entry(this, "doExport");
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new ExtensionFileFilter("JPEG image(*." + imgExtension + ")", imgExtension));
		fc.setSelectedFile(new File(config.getExportDirectory() + "/."));
		int returnVal = fc.showSaveDialog(mainFrame.getJFrame());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			if (!file.getName().endsWith("." + imgExtension)) {
				file = new File(file.getAbsolutePath() + "." + imgExtension);
			}
			
			config.setExportDirectory(file.getParent());
			if (file.exists()) {
				String msg = MessageFormat.format("File\r\n[{0}]\r\nalready exists. Overwrite?", file.getName());
				int response = JOptionPane.showOptionDialog(mainFrame.getJFrame(), msg, "Export to JPEG file", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, //$NON-NLS-1$
						null);
				if (response == JOptionPane.CANCEL_OPTION)
					return;
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file.getAbsoluteFile());
				ChartUtilities.writeChartAsJPEG(fos, chart, CHART_WIDTH, CHART_HEIGHT);
			} catch (Exception e) {
				ErrorLogHelper.exception(this, "doExport", e);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						ErrorLogHelper.exception(this, "doExport", e);
					}
				}
			}
		}
		TraceHelper.exit(this, "doExport");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		cbScaleLeft.setSelectedIndex(1);
		cbScaleRight.setSelectedIndex(1);

		doDialogShow();

		TraceHelper.exit(this, "doInit");

	}

	private void doLoadLeft() {
		TraceHelper.entry(this, "doLoadLeft");
		blockLeft = new VNARawHandler(this.getOwner()).doImport();
		if (blockLeft != null) {
			txtFilenameLeft.setText(blockLeft.getFile().getName());
			txtFilenameLeft.setToolTipText(blockLeft.getFile().getAbsolutePath());
			updateChart();
		}
		TraceHelper.exit(this, "doLoadLeft");

	}

	/**
	 * 
	 */
	protected void doLoadRight() {
		TraceHelper.entry(this, "doLoadRight");
		blockRight = new VNARawHandler(this.getOwner()).doImport();
		if (blockRight != null) {
			txtFilenameRight.setText(blockRight.getFile().getName());
			txtFilenameRight.setToolTipText(blockRight.getFile().getAbsolutePath());
			updateChart();
		}

		TraceHelper.exit(this, "doLoadRight");
	}

	/**
	 * 
	 * @return
	 */
	private void updateChart() {
		TraceHelper.entry(this, "createChart");
		XYPlot plot;
		NumberAxis rangeAxis;

		HashMap<SCALE_TYPE, VNAMinMaxPair> minMaxPairs = new HashMap<SCALE_TYPE, VNAMinMaxPair>() {
			{
				put(SCALE_TYPE.SCALE_RETURNLOSS, new VNAMinMaxPair(SCALE_TYPE.SCALE_RETURNLOSS));
				put(SCALE_TYPE.SCALE_TRANSMISSIONLOSS, new VNAMinMaxPair(SCALE_TYPE.SCALE_TRANSMISSIONLOSS));
				put(SCALE_TYPE.SCALE_RETURNPHASE, new VNAMinMaxPair(SCALE_TYPE.SCALE_RETURNPHASE));
				put(SCALE_TYPE.SCALE_TRANSMISSIONPHASE, new VNAMinMaxPair(SCALE_TYPE.SCALE_TRANSMISSIONPHASE));
				put(SCALE_TYPE.SCALE_XS, new VNAMinMaxPair(SCALE_TYPE.SCALE_XS));
				put(SCALE_TYPE.SCALE_RS, new VNAMinMaxPair(SCALE_TYPE.SCALE_RS));
				put(SCALE_TYPE.SCALE_Z_ABS, new VNAMinMaxPair(SCALE_TYPE.SCALE_Z_ABS));
				put(SCALE_TYPE.SCALE_SWR, new VNAMinMaxPair(SCALE_TYPE.SCALE_SWR));
			}
		};

		SCALE_TYPE scaleTypeLeft = ((VNAGenericScale) cbScaleLeft.getSelectedItem()).getType();
		SCALE_TYPE scaleTypeRight = ((VNAGenericScale) cbScaleRight.getSelectedItem()).getType();

		JFSeries seriesLeft = new JFSeries();
		JFSeries seriesRight = new JFSeries();

		XYSeries xySeriesLeft = new XYSeries("Left");
		XYSeries xySeriesRight = new XYSeries("Right");

		if (blockLeft != null) {
			if (scaleTypeLeft != SCALE_TYPE.SCALE_NONE) {
				updateMinMaxValues(blockLeft, minMaxPairs);
				for (VNACalibratedSample data : blockLeft.getCalibratedSamples()) {
					xySeriesLeft.add(data.getFrequency(), data.getDataByScaleType(scaleTypeLeft));
				}
			}
		}

		if (blockRight != null) {
			if (scaleTypeRight != SCALE_TYPE.SCALE_NONE) {
				updateMinMaxValues(blockRight, minMaxPairs);
				for (VNACalibratedSample data : blockRight.getCalibratedSamples()) {
					xySeriesRight.add(data.getFrequency(), data.getDataByScaleType(scaleTypeRight));
				}
			}
		}

		seriesLeft.setSeries(xySeriesLeft);
		seriesRight.setSeries(xySeriesRight);

		plot = chart.getXYPlot();
		rangeAxis = createRangeAxisForScale(seriesLeft, (VNAGenericScale) cbScaleLeft.getSelectedItem(), blockLeft, minMaxPairs);
		plot.setRangeAxis(0, rangeAxis);
		plot.setDataset(0, seriesLeft.getDataset());
		plot.mapDatasetToRangeAxis(0, 0);

		plot = chart.getXYPlot();
		rangeAxis = createRangeAxisForScale(seriesRight, (VNAGenericScale) cbScaleRight.getSelectedItem(), blockRight, minMaxPairs);
		plot.setRangeAxis(1, rangeAxis);
		plot.setDataset(1, seriesRight.getDataset());
		plot.mapDatasetToRangeAxis(1, 1);

		TraceHelper.exit(this, "createChart");
	}

	private void updateMinMaxValues(VNACalibratedSampleBlock block, HashMap<SCALE_TYPE, VNAMinMaxPair> minMaxPairs) {
		for (Iterator<Entry<SCALE_TYPE, VNAMinMaxPair>> it = minMaxPairs.entrySet().iterator(); it.hasNext();) {
			Entry<SCALE_TYPE, VNAMinMaxPair> pair = it.next();
			pair.getValue().consume(block.getMinMaxPair(pair.getKey()));
		}
	}
}
