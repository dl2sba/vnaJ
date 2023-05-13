package krause.vna.gui.calibrate;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
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

import com.l2fprod.common.swing.StatusBar;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.common.validation.IntegerValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.helper.VNASampleBlockHelper;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverFactorySymbols;
import krause.vna.device.VNAGenericDriver;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.export.JFSeries;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.calibrate.mode1.VNACalibrationRangeTable;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.ComponentTitledBorder;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Dietmar
 * 
 */
public class VNACalibrationDialog extends KrauseDialog implements IVNADataConsumer {
	private enum MeasurementMode {
		LOAD, OPEN, SHORT, LOOP
	}

	private VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private static final float STROKE_WIDTH = 1;
	private final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);
	private final Font TICK_FONT = new Font("SansSerif", Font.PLAIN, 10);
	private boolean dataValid = false;
	private long startTime;

	private VNACalibrationBlock calibrationBlock = null;

	private JButton btOK;
	private JButton btReadOpen;
	private JButton btReadShort;
	private JButton btReadLoad;
	private JButton btCancel;
	private JButton btLOAD;
	private JButton btReadLoop;
	private JButton btSAVE;

	private MeasurementMode currentMode = null;
	private ChartPanel chartOPEN;
	private ChartPanel chartSHORT;
	private ChartPanel chartLOAD;
	private ChartPanel chartLOOP;

	private VNAMainFrame mainFrame;
	private JPanel pnlShort;
	private JPanel pnlLoad;
	private JPanel pnlOpen;
	private JPanel pnlLoop;
	private JTextField txtOverscan;
	private JTextField txtNumSamples;
	private StatusBar statusBar;
	private JRadioButton rdbtnMode1;
	private JRadioButton rdbtnMode2;
	private JTextArea txtLOAD;
	private JTextArea txtLOOP;
	private JTextArea txtOPEN;
	private JTextArea txtSHORT;

	/**
	 * Create the dialog.
	 * 
	 * @throws ProcessingException
	 */
	public VNACalibrationDialog(final VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);
		TraceHelper.entry(this, "VNACalibrationDialog");

		mainFrame = pMainFrame;
		setConfigurationPrefix("VNACalibrationDialog");
		setProperties(config);
		//
		calibrationBlock = new VNACalibrationBlock();
		calibrationBlock.setScanMode(datapool.getScanMode());
		//
		String tit = VNAMessages.getString("VNACalibrationDialog.title");
		setTitle(MessageFormat.format(tit, datapool.getScanMode().toString()));

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(850, 500));
		setPreferredSize(getMinimumSize());
		getContentPane().setLayout(new MigLayout("", "[200px]0[200px]0[200px]0[200px]", "[grow,fill]0[]0[]0[]"));

		//
		JFreeChart chart = createChart(new VNASampleBlock(), true);
		getContentPane().add(pnlOpen = createPanelOpen(chart), "");
		getContentPane().add(pnlShort = createPanelShort(chart), "");
		getContentPane().add(pnlLoad = createPanelLoad(chart), "");
		getContentPane().add(pnlLoop = createPanelLoop(chart), "wrap");
		getContentPane().add(createBottomPanel(), "span 4, grow, left, wrap");
		getContentPane().add(createButtonPanel(), "span 4, right,wrap");
		getContentPane().add(statusBar = createStatusPanel(), "span 4, grow,left");
		getRootPane().setDefaultButton(btOK);

		doDialogInit();
		TraceHelper.exit(this, "VNACalibrationDialog");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.data.IVNADataConsumer#consumeDataBlock(java.util.List)
	 */
	public void consumeDataBlock(List<VNABackgroundJob> jobs) {
		TraceHelper.entry(this, "consumeDataBlock");
		VNASampleBlock data = null;

		// we run in normal calibration mode?
		if (rdbtnMode2.isSelected()) {
			// yes
			// each job contains the same scan range
			// we receive more than one?
			if (jobs.size() > 1) {
				// yes
				List<VNASampleBlock> blocks = new ArrayList<>();
				// calculate average of them
				for (VNABackgroundJob job : jobs) {
					blocks.add(job.getResult());
				}

				data = VNASampleBlockHelper.calculateAverageSampleBlock(blocks);
				// we receive exactly one?
			} else if (jobs.size() == 1) {
				// yes
				// then use this
				data = jobs.get(0).getResult();
			}
		} else {
			// no
			// all jobs are concatenated as one large calibration block
			// determine length of new block
			int blkSize = 0;
			for (VNABackgroundJob job : jobs) {
				blkSize += job.getResult().getSamples().length;
			}

			VNABaseSample[] samples = new VNABaseSample[blkSize];
			int idx = 0;
			long minFreq = Long.MAX_VALUE;
			long maxFreq = Long.MIN_VALUE;

			// copy from all jobs
			for (VNABackgroundJob job : jobs) {
				// get min/max of job
				if (job.getFrequencyRange().getStart() < minFreq) {
					minFreq = job.getFrequencyRange().getStart();
				}
				if (job.getFrequencyRange().getStop() > maxFreq) {
					maxFreq = job.getFrequencyRange().getStop();
				}
				VNASampleBlock asb = job.getResult();
				int l = asb.getSamples().length;
				// copy all samples of the job
				for (int i = 0; i < l; ++i) {
					VNABaseSample s = asb.getSamples()[i];
					samples[idx++] = s;
				}
			}

			final VNASampleBlock frstResult = jobs.get(0).getResult();

			data = new VNASampleBlock();
			data.setAnalyserType(frstResult.getAnalyserType());
			data.setMathHelper(frstResult.getMathHelper());
			data.setScanMode(frstResult.getScanMode());
			data.setDeviceTemperature(frstResult.getDeviceTemperature());

			data.setNumberOfSteps(blkSize);
			data.setSamples(samples);
			data.setStartFrequency(minFreq);
			data.setStopFrequency(maxFreq);
			data.setNumberOfOverscans(frstResult.getNumberOfOverscans());

			TraceHelper.text(this, "consumeDataBlock", "block=" + data);
		}

		// data returned?
		if (data != null) {
			// yes
			// apply any relevant filtering
			data.getMathHelper().applyFilter(data.getSamples());

			if (currentMode == MeasurementMode.LOAD) {
				calibrationBlock.setCalibrationData4Load(data);
				chartLOAD.setChart(createChart(data, true));

			} else if (currentMode == MeasurementMode.OPEN) {
				calibrationBlock.setCalibrationData4Open(data);
				chartOPEN.setChart(createChart(data, true));

			} else if (currentMode == MeasurementMode.LOOP) {
				boolean iqMode = !VNADriverFactorySymbols.TYPE_MAX6.equals(data.getAnalyserType());
				calibrationBlock.setCalibrationData4Loop(data);
				chartLOOP.setChart(createChart(data, iqMode));

			} else {
				calibrationBlock.setCalibrationData4Short(data);
				chartSHORT.setChart(createChart(data, true));
			}
			calibrationBlock.setMathHelper(data.getMathHelper());
		}

		// calculate time used
		long endTime = System.currentTimeMillis();
		long diff = (endTime - startTime) / 1000;
		mainFrame.getStatusBarStatus().setText(MessageFormat.format(VNAMessages.getString("VNACalibrationDialog.scanTime"), data.getNumberOfSteps() * config.getNumberOfOversample(), diff));

		// reenable the buttons
		controlButtonsBasedOnDIB(true);

		processCalibrationsBlock();

		TraceHelper.exit(this, "consumeDataBlock");
	}

	void controlButtonsBasedOnDIB(boolean val) {
		boolean x;

		TraceHelper.entry(this, "controlButtonsBasedOnDIB");
		VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();
		VNAScanModeParameter smp = dib.getScanModeParameterForMode(datapool.getScanMode());

		x = val & smp.isRequiresLoop();
		pnlLoop.setEnabled(x);
		btReadLoop.setEnabled(x);
		chartLOOP.setEnabled(x);
		txtLOOP.setEnabled(x);

		x = val & smp.isRequiresLoad();
		pnlLoad.setEnabled(x);
		btReadLoad.setEnabled(x);
		chartLOAD.setEnabled(x);
		txtLOAD.setEnabled(x);

		x = val & smp.isRequiresOpen();
		pnlOpen.setEnabled(x);
		btReadOpen.setEnabled(x);
		chartOPEN.setEnabled(x);
		txtOPEN.setEnabled(x);

		x = val & smp.isRequiresShort();
		pnlShort.setEnabled(x);
		btReadShort.setEnabled(x);
		chartSHORT.setEnabled(x);
		txtSHORT.setEnabled(x);
	}

	/**
	 * 
	 * @return
	 */
	private JPanel createBottomPanel() {
		TraceHelper.entry(this, "createBottomPanel");
		ButtonGroup bg = new ButtonGroup();

		JPanel rc = new JPanel();
		rc.setLayout(new MigLayout("", "[50%,left][grow,fill]", "[top,fill]"));

		final JPanel pnlMode1 = new JPanel();
		rdbtnMode1 = new JRadioButton(VNAMessages.getString("VNACalibrationDialog.rdbtnMode1")); //$NON-NLS-1$
		bg.add(rdbtnMode1);

		final JPanel pnlMode2 = new JPanel();
		rdbtnMode2 = new JRadioButton(VNAMessages.getString("VNACalibrationDialog.rdbtnMode2")); //$NON-NLS-1$
		bg.add(rdbtnMode2);

		rdbtnMode1.addActionListener(e -> {
			pnlMode1.repaint();
			pnlMode2.repaint();
		});

		rdbtnMode2.addActionListener(e -> {
			pnlMode1.repaint();
			pnlMode2.repaint();
		});

		// *************************************
		// panel for mode 1
		rdbtnMode1.setSelected(false);
		rdbtnMode1.setFocusPainted(false);

		pnlMode1.setBorder(new ComponentTitledBorder(rdbtnMode1, pnlMode1, BorderFactory.createEtchedBorder()));

		pnlMode1.setLayout(new MigLayout("", "[fill]", "[top,100px]"));
		rc.add(pnlMode1, "");

		VNACalibrationRange[] calRanges = datapool.getDriver().getCalibrationRanges();
		VNACalibrationRangeTable jlist = new VNACalibrationRangeTable(calRanges);
		JScrollPane scrollPane = new JScrollPane(jlist);
		scrollPane.setViewportBorder(null);
		scrollPane.setBackground(pnlMode1.getBackground());
		pnlMode1.add(scrollPane);

		// *************************************
		// panel for mode 2
		rdbtnMode2.setSelected(true);
		rdbtnMode2.setFocusPainted(false);

		pnlMode2.setBorder(new ComponentTitledBorder(rdbtnMode2, pnlMode2, BorderFactory.createEtchedBorder()));
		pnlMode2.setLayout(new MigLayout("", "[left][]", "[top]"));
		rc.add(pnlMode2, "");

		//
		JLabel lblOverscan = new JLabel(VNAMessages.getString("VNACalibrationDialog.lblOverscan.text")); //$NON-NLS-1$
		txtOverscan = new JTextField();
		txtOverscan.setColumns(3);
		pnlMode2.add(lblOverscan, "");
		pnlMode2.add(txtOverscan, "wrap");

		//
		JLabel lblNumSamples = new JLabel(VNAMessages.getString("VNACalibrationDialog.lblNumSamples.text")); //$NON-NLS-1$
		txtNumSamples = new JTextField();
		txtNumSamples.setColumns(6);
		pnlMode2.add(lblNumSamples, "");
		pnlMode2.add(txtNumSamples, "");

		TraceHelper.exit(this, "createBottomPanel");
		return rc;
	}

	/**
	 * 
	 * @return
	 */
	private JPanel createButtonPanel() {
		TraceHelper.entry(this, "createButtonPanel");
		JPanel rc = new JPanel();
		rc.setLayout(new MigLayout("", "[fill][fill][fill][fill][fill]", "[center]"));

		rc.add(new HelpButton(this, "VNACalibrationDialog"), "wmin 100px");

		//
		btCancel = new JButton(VNAMessages.getString("Button.Cancel"));
		btCancel.addActionListener(e -> {
			dataValid = false;
			setVisible(false);
		});
		btCancel.setActionCommand("Cancel");
		rc.add(btCancel, "wmin 100px");

		btLOAD = new JButton(VNAMessages.getString("Button.Load")); //$NON-NLS-1$
		btLOAD.addActionListener(e -> doLOAD());
		rc.add(btLOAD, "wmin 100px");

		//
		btSAVE = new JButton(VNAMessages.getString("Button.Save"));
		btSAVE.addActionListener(e -> doSAVE());
		btSAVE.setEnabled(false);
		rc.add(btSAVE, "wmin 100px");

		//
		btOK = new JButton(VNAMessages.getString("Button.Update"));
		btOK.addActionListener(e -> {
			dataValid = true;
			setVisible(false);
		});
		btOK.setActionCommand("OK");
		rc.add(btOK, "wmin 100px");

		TraceHelper.exit(this, "createButtonPanel");
		return rc;
	}

	/**
	 * 
	 * @return
	 */
	private JFreeChart createChart(VNASampleBlock pBlock, boolean iqMode) {
		TraceHelper.entry(this, "createChart");
		XYPlot plot;
		NumberAxis rangeAxis;

		JFSeries series1 = new JFSeries(null);
		JFSeries series2 = new JFSeries(null);

		XYSeries xySeries1 = null;
		XYSeries xySeries2 = null;
		if (iqMode) {
			xySeries1 = new XYSeries("I");
			xySeries2 = new XYSeries("Q");
		} else {
			xySeries1 = new XYSeries("I");
			xySeries2 = new XYSeries("RSS");
		}

		if (pBlock != null && pBlock.getSamples() != null) {
			VNABaseSample[] pDataList = pBlock.getSamples();

			if (iqMode) {
				for (int i = 0; i < pDataList.length; ++i) {
					VNABaseSample data = pDataList[i];
					xySeries1.add(data.getFrequency(), data.getLoss());
					xySeries2.add(data.getFrequency(), data.getAngle());
				}
			} else {
				for (int i = 0; i < pDataList.length; ++i) {
					VNABaseSample data = pDataList[i];
					xySeries1.add(data.getFrequency(), data.getLoss());
					xySeries2.add(data.getFrequency(), data.getRss1());
				}
			}
		}
		series1.setSeries(xySeries1);
		series2.setSeries(xySeries2);

		JFreeChart chart = ChartFactory.createXYLineChart(null, null, null, null, PlotOrientation.VERTICAL, false, false, false);

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

	private JPanel createPanelLoad(JFreeChart chart) {
		TraceHelper.entry(this, "createPanelLoad");
		JPanel rc;
		rc = new JPanel();
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalibrationDialog.grpLoad.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rc.setLayout(new MigLayout("", "[180px,fill]", "[180px]0[]0[]"));

		chartLOAD = new ChartPanel(chart, true);
		chartLOAD.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (calibrationBlock.getCalibrationData4Load() != null) {
					final VNASampleBlock blk = new VNASampleBlock(calibrationBlock);
					blk.setSamples(calibrationBlock.getCalibrationData4Load().getSamples());
					new VNACalibrationDataDetailsDialog(getOwner(), blk, "VNACalibrationDataDetailsDialog.Series.Load");
				}
			}
		});
		chartLOAD.setIgnoreRepaint(true);

		txtLOAD = new JTextArea();
		txtLOAD.setEditable(false);
		txtLOAD.setBackground(UIManager.getColor("Viewport.background"));
		txtLOAD.setFont(UIManager.getFont("TextField.font"));
		if (datapool.getDeviceType().equals(VNADriverFactorySymbols.TYPE_MININVNAPRO_EXT)) {
			if (calibrationBlock.getScanMode().isTransmissionMode()) {
				txtLOAD.setText(VNAMessages.getString("VNACalibrationDialog.txtLoad.tranExtender"));
			} else {
				txtLOAD.setText(VNAMessages.getString("VNACalibrationDialog.txtLoad.reflExtender"));
			}
		} else {
			if (calibrationBlock.getScanMode().isTransmissionMode()) {
				txtLOAD.setText(VNAMessages.getString("VNACalibrationDialog.txtLoad.tran"));
			} else {
				txtLOAD.setText(VNAMessages.getString("VNACalibrationDialog.txtLoad.refl"));
			}
		}
		txtLOAD.setWrapStyleWord(true);
		txtLOAD.setLineWrap(true);

		btReadLoad = new JButton(VNAMessages.getString("VNACalibrationDialog.btReadLoad.text"));
		btReadLoad.addActionListener(e -> {
			currentMode = MeasurementMode.LOAD;
			doMeasure();
		});
		rc.add(chartLOAD, "wrap");
		rc.add(btReadLoad, "wrap");
		rc.add(txtLOAD, "");
		TraceHelper.exit(this, "createPanelLoad");
		return rc;
	}

	/**
	 * 
	 * @param chart
	 */
	private JPanel createPanelLoop(JFreeChart chart) {
		JPanel rc;
		TraceHelper.entry(this, "cratePanelLoop");
		rc = new JPanel();
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalibrationDialog.grpLoop.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rc.setLayout(new MigLayout("", "[180px,fill]", "[180px]0[]0[]"));

		chartLOOP = new ChartPanel(chart, true);
		chartLOOP.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (calibrationBlock.getCalibrationData4Loop() != null) {
					final VNASampleBlock blk = new VNASampleBlock(calibrationBlock);
					blk.setSamples(calibrationBlock.getCalibrationData4Loop().getSamples());
					new VNACalibrationDataDetailsDialog(getOwner(), blk, "VNACalibrationDataDetailsDialog.Series.Loop");
				}
			}
		});
		chartLOOP.setIgnoreRepaint(true);

		txtLOOP = new JTextArea();
		txtLOOP.setEditable(false);
		txtLOOP.setBackground(UIManager.getColor("Viewport.background"));
		txtLOOP.setFont(UIManager.getFont("TextField.font"));

		if (datapool.getDeviceType().equals(VNADriverFactorySymbols.TYPE_MININVNAPRO_EXT)) {
			if (calibrationBlock.getScanMode().isTransmissionMode()) {
				txtLOOP.setText(VNAMessages.getString("VNACalibrationDialog.txtLoop.tranExtender"));
			} else {
				txtLOOP.setText(VNAMessages.getString("VNACalibrationDialog.txtLoop.reflExtender"));
			}
		} else {
			if (calibrationBlock.getScanMode().isTransmissionMode()) {
				txtLOOP.setText(VNAMessages.getString("VNACalibrationDialog.txtLoop.tran"));
			} else {
				txtLOOP.setText(VNAMessages.getString("VNACalibrationDialog.txtLoop.refl"));
			}
		}

		txtLOOP.setWrapStyleWord(true);
		txtLOOP.setLineWrap(true);

		btReadLoop = new JButton(VNAMessages.getString("VNACalibrationDialog.btReadLoop.text"));
		btReadLoop.addActionListener(e -> {
			currentMode = MeasurementMode.LOOP;
			doMeasure();
		});
		rc.add(chartLOOP, "wrap");
		rc.add(btReadLoop, "wrap");
		rc.add(txtLOOP, "");
		TraceHelper.exit(this, "cratePanelLoop");
		return rc;
	}

	/**
	 * 
	 * @param chart
	 * @return
	 */
	private JPanel createPanelOpen(JFreeChart chart) {
		JPanel rc;
		TraceHelper.entry(this, "createPanelOpen");
		//
		rc = new JPanel();
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalibrationDialog.grpOpen.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rc.setLayout(new MigLayout("", "[180px,fill]", "[180px]0[]0[]"));

		chartOPEN = new ChartPanel(chart, true);
		chartOPEN.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (calibrationBlock.getCalibrationData4Open() != null) {
					final VNASampleBlock blk = new VNASampleBlock(calibrationBlock);
					blk.setSamples(calibrationBlock.getCalibrationData4Open().getSamples());
					new VNACalibrationDataDetailsDialog(getOwner(), blk, "VNACalibrationDataDetailsDialog.Series.Open");
				}
			}
		});
		chartOPEN.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		chartOPEN.setIgnoreRepaint(true);

		txtOPEN = new JTextArea();
		txtOPEN.setEditable(false);
		txtOPEN.setBackground(UIManager.getColor("Viewport.background"));
		txtOPEN.setFont(UIManager.getFont("TextField.font"));
		txtOPEN.setLineWrap(true);
		txtOPEN.setWrapStyleWord(true);
		if (datapool.getDeviceType().equals(VNADriverFactorySymbols.TYPE_MININVNAPRO_EXT)) {
			if (calibrationBlock.getScanMode().isTransmissionMode()) {
				txtOPEN.setText(VNAMessages.getString("VNACalibrationDialog.txtOpen.tranExtender"));
			} else {
				txtOPEN.setText(VNAMessages.getString("VNACalibrationDialog.txtOpen.reflExtender"));
			}
		} else {
			if (calibrationBlock.getScanMode().isTransmissionMode()) {
				txtOPEN.setText(VNAMessages.getString("VNACalibrationDialog.txtOpen.tran"));
			} else {
				txtOPEN.setText(VNAMessages.getString("VNACalibrationDialog.txtOpen.refl"));
			}
		}

		btReadOpen = new JButton(VNAMessages.getString("VNACalibrationDialog.btReadOpen.text"));
		btReadOpen.addActionListener(e -> {
			currentMode = MeasurementMode.OPEN;
			doMeasure();
		});

		rc.add(chartOPEN, "wrap");
		rc.add(btReadOpen, "wrap");
		rc.add(txtOPEN, "");
		TraceHelper.exit(this, "createPanelOpen");
		return rc;
	}

	private JPanel createPanelShort(JFreeChart chart) {
		JPanel rc;
		TraceHelper.entry(this, "createPanelShort");
		//
		rc = new JPanel();
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNACalibrationDialog.grpShort.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rc.setLayout(new MigLayout("", "[180px,fill]", "[180px]0[]0[]"));

		chartSHORT = new ChartPanel(chart, true);
		chartSHORT.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (calibrationBlock.getCalibrationData4Short() != null) {
					VNASampleBlock blk = new VNASampleBlock(calibrationBlock);
					blk.setSamples(calibrationBlock.getCalibrationData4Short().getSamples());
					new VNACalibrationDataDetailsDialog(getOwner(), blk, "VNACalibrationDataDetailsDialog.Series.Short");
				}
			}
		});
		chartSHORT.setIgnoreRepaint(true);

		txtSHORT = new JTextArea();
		txtSHORT.setEditable(false);
		txtSHORT.setBackground(UIManager.getColor("Viewport.background"));
		txtSHORT.setFont(UIManager.getFont("TextField.font"));
		txtSHORT.setWrapStyleWord(true);
		txtSHORT.setLineWrap(true);
		if (datapool.getDeviceType().equals(VNADriverFactorySymbols.TYPE_MININVNAPRO_EXT)) {
			if (calibrationBlock.getScanMode().isTransmissionMode()) {
				txtSHORT.setText(VNAMessages.getString("VNACalibrationDialog.txtShort.tranExtender"));
			} else {
				txtSHORT.setText(VNAMessages.getString("VNACalibrationDialog.txtShort.reflExtender"));
			}
		} else {
			if (calibrationBlock.getScanMode().isTransmissionMode()) {
				txtSHORT.setText(VNAMessages.getString("VNACalibrationDialog.txtShort.tran"));
			} else {
				txtSHORT.setText(VNAMessages.getString("VNACalibrationDialog.txtShort.refl"));
			}
		}

		btReadShort = new JButton(VNAMessages.getString("VNACalibrationDialog.btReadShort.text"));
		btReadShort.addActionListener(e -> {
			currentMode = MeasurementMode.SHORT;
			doMeasure();
		});
		rc.add(chartSHORT, "wrap");
		rc.add(btReadShort, "wrap");
		rc.add(txtSHORT, "");

		TraceHelper.exit(this, "createPanelShort");
		return rc;

	}

	private StatusBar createStatusPanel() {
		StatusBar rc;
		rc = new StatusBar();
		JLabel lbl = new JLabel(VNAMessages.getString("Message.Ready"));
		lbl.setOpaque(true);
		rc.addZone("status", lbl, "*");
		return rc;
	}

	/**
	 * 
	 */
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doExit");
		dataValid = false;
		setVisible(false);
		TraceHelper.exit(this, "doExit");
	}

	/**
	 * 
	 */
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();

		controlButtonsBasedOnDIB(true);

		btOK.setEnabled(false);
		txtNumSamples.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getNumberOfSamples4Calibration()));
		txtOverscan.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getNumberOfOverscans4Calibration()));

		addEscapeKey();
		//
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	protected void doLOAD() {
		TraceHelper.entry(this, "doLOAD");
		VNACalibrationLoadDialog dlg = new VNACalibrationLoadDialog(getOwner());
		VNACalibrationBlock cal = dlg.getSelectedCalibrationBlock();
		if (cal != null) {
			// store for later use
			calibrationBlock = cal;

			//
			boolean iqMode = !VNADriverFactorySymbols.TYPE_MAX6.equals(cal.getAnalyserType());

			// create the diagrams
			chartOPEN.setChart(createChart(cal.getCalibrationData4Open(), true));
			chartSHORT.setChart(createChart(cal.getCalibrationData4Short(), true));
			chartLOAD.setChart(createChart(cal.getCalibrationData4Load(), true));
			chartLOOP.setChart(createChart(cal.getCalibrationData4Loop(), iqMode));

			// enable the buttons
			btSAVE.setEnabled(true);
			btOK.setBackground(Color.GREEN);
			btOK.setEnabled(true);

			// and we have a valid calibration block created
			dataValid = true;
		}
		dlg.dispose();
		TraceHelper.exit(this, "doLOAD");
	}

	private void doMeasure() {
		TraceHelper.entry(this, "doMeasure");

		VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();

		// we run in normal calibration mode?
		if (rdbtnMode2.isSelected()) {
			// yes
			// each job contains the same scan range

			ValidationResults results = new ValidationResults();

			int overScans = IntegerValidator.parse(txtOverscan.getText(), 1, 10, VNAMessages.getString("VNACalibrationDialog.lblOverscan.text"), results);
			int numSamples = IntegerValidator.parse(txtNumSamples.getText(), VNAGenericDriver.MINIMUM_SCAN_POINTS, VNAGenericDriver.MAXIMUM_SCAN_POINTS, VNAMessages.getString("VNACalibrationDialog.lblNumSamples.text"), results);

			if (results.isEmpty()) {
				// store in config
				config.setNumberOfOversample(overScans);

				// disable all buttons
				btOK.setEnabled(false);
				btSAVE.setEnabled(false);
				btLOAD.setEnabled(false);

				controlButtonsBasedOnDIB(false);

				// create job
				VNABackgroundJob job = new VNABackgroundJob();
				job.setNumberOfSamples(numSamples);
				job.setFrequencyRange(dib);
				job.setSpeedup(1);
				job.setOverScan(overScans);
				job.setAverage(0);
				job.setScanMode(datapool.getScanMode());

				VnaBackgroundTask backgroundTask = new VnaBackgroundTask(datapool.getDriver());
				backgroundTask.addJob(job);
				backgroundTask.setStatusLabel((JLabel) statusBar.getZone("status"));
				backgroundTask.addDataConsumer(this);
				backgroundTask.execute();

				startTime = System.currentTimeMillis();
			} else {
				new ValidationResultsDialog(getOwner(), results, getTitle());
			}
		} else {
			// no
			// all jobs are concatenated as one large calibration block
			// disable all buttons
			btOK.setEnabled(false);
			btSAVE.setEnabled(false);
			btLOAD.setEnabled(false);
			btReadLoad.setEnabled(false);
			btReadOpen.setEnabled(false);
			btReadShort.setEnabled(false);
			btReadLoop.setEnabled(false);

			VNACalibrationRange[] calRanges = datapool.getDriver().getCalibrationRanges();
			VnaBackgroundTask backgroundTask = new VnaBackgroundTask(datapool.getDriver());

			// create jobs
			for (VNACalibrationRange calRange : calRanges) {
				// job for lf range
				VNABackgroundJob jobLF = new VNABackgroundJob();
				jobLF.setNumberOfSamples(calRange.getNumScanPoints());
				jobLF.setFrequencyRange(calRange);
				jobLF.setSpeedup(1);
				jobLF.setScanMode(datapool.getScanMode());
				jobLF.setAverage(calRange.getNumOverScans());
				backgroundTask.addJob(jobLF);
			}
			backgroundTask.setStatusLabel((JLabel) statusBar.getZone("status"));
			backgroundTask.addDataConsumer(this);
			backgroundTask.execute();

			startTime = System.currentTimeMillis();

		}
		TraceHelper.exit(this, "doMeasure");
	}

	/**
	 * write the acquired raw data block to a file
	 */
	private void doSAVE() {
		TraceHelper.entry(this, "doSAVE()");
		VNACalibrationSaveDialog dlg = new VNACalibrationSaveDialog(getOwner(), calibrationBlock);
		dlg.dispose();
		TraceHelper.exit(this, "doSAVE()");
	}

	/**
	 * @return the calibration
	 */
	public VNACalibrationBlock getCalibration() {
		return calibrationBlock;
	}

	public boolean isDataValid() {
		return dataValid;
	}

	/**
	 * 
	 */
	private void processCalibrationsBlock() {
		final String methodName = "processCalibrationsBlock";
		TraceHelper.entry(this, methodName);
		try {
			if ((calibrationBlock.getMathHelper() != null) && (calibrationBlock.satisfiedDeviceInfoBlock(datapool.getDriver().getDeviceInfoBlock()))) {
				VNACalibrationContext ct = calibrationBlock.getMathHelper().createCalibrationContextForCalibrationPoints(calibrationBlock, datapool.getCalibrationKit());

				calibrationBlock = calibrationBlock.getMathHelper().createCalibrationBlockFromRaw(ct, calibrationBlock.getCalibrationData4Open(), calibrationBlock.getCalibrationData4Short(), calibrationBlock.getCalibrationData4Load(), calibrationBlock.getCalibrationData4Loop());
				calibrationBlock.setFile(null);

				// enable the buttons
				btOK.setBackground(Color.GREEN);

				btOK.setEnabled(true);
				btSAVE.setEnabled(true);

				// and we have a valid calibration block created
				dataValid = true;
			}
		} catch (ProcessingException e) {
			ErrorLogHelper.exception(this, methodName, e);
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * @param calibration
	 *            the calibration to set
	 */
	public void setCalibration(VNACalibrationBlock calibration) {
		this.calibrationBlock = calibration;
	}
}
