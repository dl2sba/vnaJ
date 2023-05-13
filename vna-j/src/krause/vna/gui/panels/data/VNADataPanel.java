/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.panels.data;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;

import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.LogManager;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNAApplicationState.INNERSTATE;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNAScanModeParameter;
import krause.vna.device.VNAScanRange;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.input.FrequencyInputField;
import krause.vna.gui.panels.VNADiagramPanel;
import krause.vna.gui.panels.VNAImagePanel;
import krause.vna.gui.panels.VNAScaleSelectPanel;
import krause.vna.gui.panels.data.table.VNAEditableFrequencyPairTable;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.gui.util.SwingUtil;
import krause.vna.gui.util.VNAFrequencyPair;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * @author Dietmar Krause
 * 
 */
public class VNADataPanel extends JPanel implements FocusListener, ActionListener, IVNADataConsumer, VNAApplicationStateObserver {

	public static final String SCAN_LIST_FILENAME = "scanlist.xml";
	public static final int MIN_SCAN_RANGE = 5000;

	private final VNAConfig config = VNAConfig.getSingleton();
	private final transient VNADataPool datapool = VNADataPool.getSingleton();

	private FrequencyInputField txtStartFreq = null;
	private FrequencyInputField txtStopFreq = null;
	private JButton buttonScan = null;
	private JButton buttonZoom = null;
	private VNAScanModeComboBox cbMode = null;
	private JCheckBox cbFreeRun = null;
	private VNAEditableFrequencyPairTable tblFrequencies = null;
	private transient VNAMainFrame mainFrame;
	private JPanel gbFreqEnt;
	private JSlider sldSpeed;
	private JSlider sldAverage;
	private JLabel lblAverage;
	private JLabel lblSpeed;
	private JCheckBox cbGaussianFilter = null;
	private JCheckBox cbPhosphor;
	private JButton btnSetMaxFrequency;
	private JButton btnSetMinFrequency;

	/**
	 * Create the data panel for the application
	 * 
	 */
	public VNADataPanel(VNAMainFrame pMainFrame) {
		TraceHelper.entry(this, "VNADataPanel");
		mainFrame = pMainFrame;

		setLayout(new MigLayout("", "[grow,fill]", "[][grow,fill][]"));

		// ========================================================================
		// create the frequency entry group
		// ========================================================================
		gbFreqEnt = new JPanel(new MigLayout("", "[][grow,fill][20px]", "[][]"));
		gbFreqEnt.setBorder(BorderFactory.createTitledBorder(VNAMessages.getString("Panel.Data.1")));
		gbFreqEnt.add(new JLabel(VNAMessages.getString("Panel.Data.2")), "");
		//
		txtStartFreq = new FrequencyInputField("fromFreq", datapool.getFrequencyRange().getStart());
		txtStartFreq.setColumns(10);
		gbFreqEnt.add(txtStartFreq, "right");
		btnSetMinFrequency = SwingUtil.createImageButton("Panel.Data.min", this);
		gbFreqEnt.add(btnSetMinFrequency, "gap 0, wmax 20px, left, wrap");

		gbFreqEnt.add(new JLabel(VNAMessages.getString("Panel.Data.3")), "");
		txtStopFreq = new FrequencyInputField("toFreq", datapool.getFrequencyRange().getStop());
		txtStopFreq.setColumns(10);
		gbFreqEnt.add(txtStopFreq, "right");
		btnSetMaxFrequency = SwingUtil.createImageButton("Panel.Data.max", this);
		gbFreqEnt.add(btnSetMaxFrequency, "gap 0, wmax 20px, left, wrap");

		txtStopFreq.addFocusListener(this);
		txtStartFreq.addFocusListener(this);

		txtStopFreq.addActionListener(this);
		txtStartFreq.addActionListener(this);

		add(gbFreqEnt, "top,wrap");
		// ========================================================================
		// create the frequency group
		// ========================================================================
		JPanel gbFreq = new JPanel(new MigLayout("", "[left,grow,fill]", "[top,grow,fill]"));
		gbFreq.setBorder(BorderFactory.createTitledBorder(VNAMessages.getString("Panel.Data.4")));

		//
		JScrollPane spFrequencies = createListbox();
		gbFreq.add(spFrequencies, "");

		//
		add(gbFreq, "wrap");

		// ========================================================================
		// create the mode group
		// ========================================================================
		JPanel pnlButtons = new JPanel(new MigLayout("", "[left,fill][grow,right]", ""));
		pnlButtons.setBorder(BorderFactory.createTitledBorder(VNAMessages.getString("Panel.Data.5")));
		pnlButtons.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (LogManager.getSingleton().isTracingEnabled()) {
					LogManager.getSingleton().setTracingEnabled(false);
					mainFrame.getStatusBarStatus().setText("Tracing disabled");
				} else {
					LogManager.getSingleton().setTracingEnabled(true);
					mainFrame.getStatusBarStatus().setText("Tracing enabled");
				}
				java.awt.Toolkit.getDefaultToolkit().beep();
			}
		});

		cbMode = new VNAScanModeComboBox();
		cbMode.addActionListener(this);
		pnlButtons.add(cbMode, "span 2,wrap");

		pnlButtons.add(new JLabel(), "");

		buttonZoom = SwingUtil.createJButton("Panel.Data.ButtonZoom", this);
		pnlButtons.add(buttonZoom, "grow, wrap");

		cbFreeRun = SwingUtil.createJCheckBox("Panel.Data.ButtonFree", this);
		pnlButtons.add(cbFreeRun, "");

		buttonScan = SwingUtil.createJButton("Panel.Data.ButtonScan", this);
		buttonScan.setBackground(Color.GREEN);
		buttonScan.registerKeyboardAction(buttonScan.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
		buttonScan.registerKeyboardAction(buttonScan.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);
		pnlButtons.add(buttonScan, "grow,wrap");

		//
		// speedup slider
		lblSpeed = new JLabel(MessageFormat.format(VNAMessages.getString("Panel.Data.SpeedLabel"), config.getScanSpeed()));
		pnlButtons.add(lblSpeed, "");
		sldSpeed = new JSlider(1, 6, config.getScanSpeed());
		sldSpeed.setPaintLabels(false);
		sldSpeed.setPaintTicks(false);
		sldSpeed.setMajorTickSpacing(1);
		sldSpeed.setMinorTickSpacing(1);
		sldSpeed.setSnapToTicks(true);
		sldSpeed.setToolTipText(VNAMessages.getString("Panel.Data.Speed"));
		sldSpeed.putClientProperty("JComponent.sizeVariant", "small");
		sldSpeed.addChangeListener(e -> {
			final int val = sldSpeed.getValue();
			lblSpeed.setText(MessageFormat.format(VNAMessages.getString("Panel.Data.SpeedLabel"), val));
			config.setScanSpeed(val);
		});
		pnlButtons.add(sldSpeed, "grow, wrap");
		//
		//
		// speedup slider
		lblAverage = new JLabel(MessageFormat.format(VNAMessages.getString("Panel.Data.AverageLabel"), config.getAverage()));
		pnlButtons.add(lblAverage, "");
		sldAverage = new JSlider(1, 9, config.getAverage());
		sldAverage.setPaintLabels(false);
		sldAverage.setPaintTicks(false);
		sldAverage.setMajorTickSpacing(3);
		sldAverage.setMinorTickSpacing(1);
		sldAverage.setSnapToTicks(true);
		sldAverage.setToolTipText(VNAMessages.getString("Panel.Data.Average"));
		sldAverage.putClientProperty("JComponent.sizeVariant", "small");
		sldAverage.addChangeListener(e -> {
			final int val = sldAverage.getValue();
			lblAverage.setText(MessageFormat.format(VNAMessages.getString("Panel.Data.AverageLabel"), val));
			config.setAverage(val);
		});
		pnlButtons.add(sldAverage, "grow, wrap");
		sldAverage.setValue(config.getAverage());

		cbPhosphor = SwingUtil.createJCheckBox("Panel.Data.Phosphor", null);
		cbPhosphor.setSelected(config.isPhosphor());
		cbPhosphor.addActionListener(e -> config.setPhosphor(cbPhosphor.isSelected()));
		pnlButtons.add(cbPhosphor, "grow");

		cbGaussianFilter = SwingUtil.createJCheckBox("Panel.Data.GaussianFilter", null);
		cbGaussianFilter.setSelected(config.isApplyGaussianFilter());
		cbGaussianFilter.addActionListener(e -> config.setApplyGaussianFilter(cbGaussianFilter.isSelected()));
		pnlButtons.add(cbGaussianFilter, "grow");

		//
		add(pnlButtons, "");
		TraceHelper.exit(this, "createDataPanel");
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		Object src = e.getSource();
		TraceHelper.entry(this, "actionPerformed", cmd);
		if (src == cbMode) {
			doChangeMode();
		} else if (src == buttonScan) {
			doSingleScan();
		} else if (src == buttonZoom) {
			doZoom();
		} else if (src == cbFreeRun) {
			doFreeRun();
		} else if (src == txtStartFreq) {
			txtStopFreq.requestFocusInWindow();
		} else if (src == txtStopFreq) {
			buttonScan.requestFocusInWindow();
		} else if (e.getSource() == tblFrequencies) {
			handleFrequencyList(cmd);
		} else if (e.getSource() == btnSetMaxFrequency) {
			IVNADriver drv = datapool.getDriver();
			VNADeviceInfoBlock dib = drv.getDeviceInfoBlock();
			txtStopFreq.setFrequency(dib.getMaxFrequency());

		} else if (e.getSource() == btnSetMinFrequency) {
			IVNADriver drv = datapool.getDriver();
			VNADeviceInfoBlock dib = drv.getDeviceInfoBlock();
			txtStartFreq.setFrequency(dib.getMinFrequency());
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	private void doChangeMode() {
		// switch globally
		datapool.setScanMode(cbMode.getSelectedMode());
		mainFrame.getApplicationState().evtScanModeChanged();

		//
		IVNADriver drv = datapool.getDriver();
		VNADeviceInfoBlock dib = drv.getDeviceInfoBlock();
		VNAScanModeParameter smp = dib.getScanModeParameterForMode(datapool.getScanMode());
		VNADiagramPanel dp = mainFrame.getDiagramPanel();
		VNAScaleSelectPanel ssp = dp.getScaleSelectPanel();
		if (smp != null) {
			// handle left scale
			ssp.getCbLeftScale().setSelectedItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(smp.getScaleLeft()));
			ssp.getCbRightScale().setSelectedItem(VNAScaleSymbols.MAP_SCALE_TYPES.get(smp.getScaleRight()));

			// finalize
			dp.repaint();
		}
	}

	/**
	 * handle the commands for the frequency list
	 * 
	 * @param cmd
	 */
	private void handleFrequencyList(String cmd) {
		String[] tokens = cmd.split("\\;", 10);
		if ("ADD".equals(tokens[0])) {
			VNAFrequencyPair fp = new VNAFrequencyPair(txtStartFreq.getFrequency(), txtStopFreq.getFrequency());
			tblFrequencies.addFrequency(fp);
		} else if ("USE".equals(tokens[0])) {
			long start = Long.parseLong(tokens[1]);
			long stop = Long.parseLong(tokens[2]);

			// write list values to fields
			txtStartFreq.setFrequency(start);
			txtStopFreq.setFrequency(stop);

			// change the frequency range
			changeFrequencyBasedOnFields();

			// check if auto scan after selection is set
			if (config.isScanAfterTableSelect() && buttonScan.isEnabled()) {
				doSingleScan();
			}
		}
	}

	/**
	 * 
	 */
	private void changeFrequencyBasedOnFields() {
		TraceHelper.entry(this, "changeFrequencyBasedOnFields");
		datapool.setFrequencyRange(txtStartFreq.getFrequency(), txtStopFreq.getFrequency());
		// clear the current calibration block
		datapool.clearResizedCalibrationBlock();

		// clear the raw data
		datapool.setRawData(null);
		datapool.getRawDataBlocks().clear();

		TraceHelper.exit(this, "changeFrequencyBasedOnFields");
	}

	public void changeFrequencyRange(VNAFrequencyRange range) {
		TraceHelper.entry(this, "changeFrequencyRange");
		txtStartFreq.setFrequency(range.getStart());
		txtStopFreq.setFrequency(range.getStop());
		changeFrequencyBasedOnFields();
		TraceHelper.exit(this, "changeFrequencyRange");
	}

	public void changeState(INNERSTATE oldState, INNERSTATE newState) {
		if (newState == INNERSTATE.DRIVERLOADED) {
			buttonScan.setEnabled(false);
			buttonZoom.setEnabled(false);
			cbFreeRun.setEnabled(false);
			tblFrequencies.setEnabled(false);
			txtStartFreq.setEditable(false);
			txtStopFreq.setEditable(false);

			// now set the borders depending on the driver
			VNADeviceInfoBlock devInfo = datapool.getDriver().getDeviceInfoBlock();
			txtStartFreq.setLowerLimit(devInfo.getMinFrequency());
			txtStartFreq.setUpperLimit(devInfo.getMaxFrequency());
			//
			txtStopFreq.setLowerLimit(devInfo.getMinFrequency());
			txtStopFreq.setUpperLimit(devInfo.getMaxFrequency());

			// change input fields to fullrange scan
			txtStartFreq.setFrequency(devInfo.getMinFrequency());
			txtStopFreq.setFrequency(devInfo.getMaxFrequency());

			// mode selection box
			// driver is now loaded
			// came from GUI init?
			if (oldState == INNERSTATE.GUIINITIALIZED) {
				// yes
				// then populate the combobox for modes
				cbMode.setModes(devInfo.getScanModeParameters());
			}
			cbMode.setEnabled(true);

			// and write it to config
			datapool.setFrequencyRange(devInfo.getMinFrequency(), devInfo.getMaxFrequency());

		} else if (newState == INNERSTATE.CALIBRATED) {
			buttonScan.setEnabled(true);
			buttonZoom.setEnabled(true);
			cbFreeRun.setEnabled(true);
			cbMode.setEnabled(true);
			// we came from driver loading?
			if (oldState == INNERSTATE.DRIVERLOADED) {
				// yes
				// then we select the scan mode from datapool
				cbMode.setSelectedMode(datapool.getScanMode());
			}

			txtStartFreq.setEditable(true);
			txtStopFreq.setEditable(true);
			tblFrequencies.setEnabled(true);
			sldSpeed.setEnabled(true);
			sldAverage.setEnabled(true);
			cbGaussianFilter.setEnabled(true);
			cbPhosphor.setEnabled(true);
		} else if (newState == INNERSTATE.RUNNING) {
			buttonScan.setEnabled(false);
			buttonZoom.setEnabled(false);
			if (!cbFreeRun.isSelected()) {
				cbFreeRun.setEnabled(false);
			}
			cbMode.setEnabled(false);
			tblFrequencies.setEnabled(false);
			txtStartFreq.setEditable(false);
			txtStopFreq.setEditable(false);
			sldSpeed.setEnabled(false);
			sldAverage.setEnabled(false);
			cbGaussianFilter.setEnabled(false);
			cbPhosphor.setEnabled(false);
		} else {
			buttonScan.setEnabled(false);
			buttonZoom.setEnabled(false);
			cbFreeRun.setEnabled(false);
			cbMode.setEnabled(false);
			tblFrequencies.setEnabled(false);
			txtStartFreq.setEditable(false);
			txtStopFreq.setEditable(false);
			sldSpeed.setEnabled(false);
			sldAverage.setEnabled(false);
			cbGaussianFilter.setEnabled(false);
			cbPhosphor.setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.data.IVNADataConsumer#consumeDataBlock(java.util.List)
	 */
	public void consumeDataBlock(List<VNABackgroundJob> jobs) {
		final String methodName = "consumeDataBlock";
		TraceHelper.entry(this, methodName);

		// restore cursor
		mainFrame.getJFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		// switch global state
		mainFrame.getApplicationState().evtMeasureEnded();

		// running in free run mode?
		if (cbFreeRun.isSelected()) {
			// yes
			// than trigger the next scan
			doSingleScan();
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * @return
	 */
	private JScrollPane createListbox() {
		JScrollPane rc = null;
		TraceHelper.entry(this, "createListbox");
		//
		tblFrequencies = new VNAEditableFrequencyPairTable();
		tblFrequencies.addActionListener(this);
		//
		rc = new JScrollPane(tblFrequencies);
		rc.setViewportBorder(null);
		TraceHelper.exit(this, "createListbox");
		return rc;
	}

	/**
	 * 
	 */
	private void doFreeRun() {
		TraceHelper.entry(this, "doFreeRun");
		if (cbFreeRun.isSelected()) {
			buttonScan.setEnabled(false);
			// trigger a single shot
			doSingleScan();
		} else {
			buttonScan.setEnabled(true);
		}
		TraceHelper.exit(this, "doFreeRun");
	}

	/**
	 * 
	 */
	private void doSingleScan() {
		final String methodName = "doSingleScan";
		TraceHelper.entry(this, methodName);

		// setup cursor
		mainFrame.getJFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		// read from fields
		VNAScanRange range = new VNAScanRange(txtStartFreq.getFrequency(), txtStopFreq.getFrequency(), config.getNumberOfSamples());

		// now ask the driver whether the range is fine
		ValidationResults valRes = datapool.getDriver().validateScanRange(range);

		if (valRes.isEmpty()) {
			// ok
			// update input fields
			txtStartFreq.setFrequency(range.getStart());
			txtStopFreq.setFrequency(range.getStop());
			//
			datapool.setFrequencyRange(range);
			//
			if (!datapool.getDriver().isScanSupported(config.getNumberOfSamples(), range, datapool.getScanMode())) {
				JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Panel.Data.miniVNApro.1"), VNAMessages.getString("Panel.Data.miniVNApro.2"), JOptionPane.INFORMATION_MESSAGE);
				// restore cursor
				mainFrame.getJFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}
			//
			if (datapool.getResizedCalibrationBlock() == null && datapool.getMainCalibrationBlock() != null) {
				VNACalibrationBlock newBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(datapool.getMainCalibrationBlock(), datapool.getFrequencyRange().getStart(), datapool.getFrequencyRange().getStop(), config.getNumberOfSamples());
				datapool.setResizedCalibrationBlock(newBlock);
			}
			//
			mainFrame.getApplicationState().evtMeasureStarted();

			// create one instance
			VnaBackgroundTask backgroundTask = new VnaBackgroundTask(datapool.getDriver());
			backgroundTask.setStatusLabel(mainFrame.getStatusBarStatus());
			backgroundTask.addDataConsumer(mainFrame.getDiagramPanel());
			backgroundTask.addDataConsumer(this);

			// now build the job
			VNABackgroundJob job = new VNABackgroundJob();
			job.setSpeedup(sldSpeed.getValue());
			job.setAverage(sldAverage.getValue());
			job.setNumberOfSamples(config.getNumberOfSamples());
			job.setFrequencyRange(range);
			job.setScanMode(datapool.getScanMode());
			backgroundTask.addJob(job);

			backgroundTask.execute();
		} else {
			new ValidationResultsDialog(null, valRes, VNAMessages.getString("VNANetworkDialog.ErrorDialogHeader"));
		}
		//
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 */
	private void doZoom() {
		TraceHelper.entry(this, "doZoom");
		VNAMarkerPanel mp = mainFrame.getMarkerPanel();
		if (!mp.getMarker(VNAMarkerPanel.MARKER_0).isVisible() && !mp.getMarker(VNAMarkerPanel.MARKER_1).isVisible()) {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Panel.Data.Zoom.Msg1"), VNAMessages.getString("Panel.Data.Zoom.Title"), JOptionPane.INFORMATION_MESSAGE);
		} else {
			long fStart;
			long fStop;
			VNAMarker marker = null;

			VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();
			// both markers visible?
			if (mp.getMarker(VNAMarkerPanel.MARKER_0).isVisible() && mp.getMarker(VNAMarkerPanel.MARKER_1).isVisible()) {
				// yes
				fStart = mp.getMarker(VNAMarkerPanel.MARKER_0).getFrequency();
				fStop = mp.getMarker(VNAMarkerPanel.MARKER_1).getFrequency();
				// swap if start < stop
				if (fStop < fStart) {
					long i = fStart;
					fStart = fStop;
					fStop = i;
				}
			} else if (mp.getMarker(VNAMarkerPanel.MARKER_0).isVisible()) {
				// only marker one visible?
				marker = mp.getMarker(VNAMarkerPanel.MARKER_0);
				long oldSpan = txtStopFreq.getFrequency() - txtStartFreq.getFrequency();
				long newSpan = oldSpan / 10;
				fStart = Math.max(mp.getMarker(VNAMarkerPanel.MARKER_0).getFrequency() - newSpan, dib.getMinFrequency());
				fStop = Math.min(mp.getMarker(VNAMarkerPanel.MARKER_0).getFrequency() + newSpan, dib.getMaxFrequency());

			} else {
				// only marker one visible?
				marker = mp.getMarker(VNAMarkerPanel.MARKER_1);
				long oldSpan = txtStopFreq.getFrequency() - txtStartFreq.getFrequency();
				long newSpan = oldSpan / 10;
				fStart = Math.max(mp.getMarker(VNAMarkerPanel.MARKER_1).getFrequency() - newSpan, dib.getMinFrequency());
				fStop = Math.min(mp.getMarker(VNAMarkerPanel.MARKER_1).getFrequency() + newSpan, dib.getMaxFrequency());
			}
			// check that frequency range is not too tight
			if (fStart + MIN_SCAN_RANGE > fStop) {
				String msg = MessageFormat.format(VNAMessages.getString("Panel.Data.Zoom.Msg2"), MIN_SCAN_RANGE);
				JOptionPane.showMessageDialog(mainFrame.getJFrame(), msg, VNAMessages.getString("Panel.Data.Zoom.Title"), JOptionPane.INFORMATION_MESSAGE);
			} else {
				if (marker != null) {
					// center marker in middle of diagram
					VNAImagePanel ip = mainFrame.getDiagramPanel().getImagePanel();
					int x = ip.getWidth() / 2;
					VNACalibratedSample sample = ip.getSampleAtMousePosition(x);
					mp.getMarker(VNAMarkerPanel.MARKER_0).update(sample);

				} else {
					mp.getMarker(VNAMarkerPanel.MARKER_0).clearFields();
					mp.getMarker(VNAMarkerPanel.MARKER_1).clearFields();
					mp.getDeltaMarker().clearFields();
				}
				// write back to gui
				txtStartFreq.setFrequency(fStart);
				txtStopFreq.setFrequency(fStop);

				// change frequency range
				changeFrequencyBasedOnFields();

				// hard switch to single scan mode?
				cbFreeRun.setSelected(false);
				//
				if (config.isScanAfterZoom() && buttonScan.isEnabled()) {
					doSingleScan();
				} else {
					mainFrame.getDiagramPanel().repaint();
				}
			}
		}
		TraceHelper.exit(this, "doZoom");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		TraceHelper.entry(this, "focusGained");
		TraceHelper.exit(this, "focusGained");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		TraceHelper.entry(this, "focusLost");

		// check whether all two frequencies are valid
		if (txtStartFreq.isValidData() && txtStopFreq.isValidData()) {
			// first swap start stop if necessary
			long fStart = txtStartFreq.getFrequency();
			long fStop = txtStopFreq.getFrequency();

			if (fStop < fStart) {
				long x = fStop;
				fStop = fStart;
				fStart = x;
				txtStartFreq.setFrequency(fStart);
				txtStopFreq.setFrequency(fStop);
			}
			changeFrequencyBasedOnFields();
		}
		TraceHelper.exit(this, "focusLost");
	}

	/**
	 * @return the buttonScan
	 */
	public JButton getButtonScan() {
		return buttonScan;
	}

	public VNAScanModeComboBox getCbMode() {
		return cbMode;
	}

	public FrequencyInputField getTxtStartFreq() {
		return txtStartFreq;
	}

	public FrequencyInputField getTxtStopFreq() {
		return txtStopFreq;
	}

	public boolean isIdleMode() {
		return buttonScan.isEnabled() && !cbFreeRun.isSelected();
	}

	/**
	 * 
	 */
	public void load() {
		TraceHelper.entry(this, "load");
		txtStartFreq.setFrequency(datapool.getFrequencyRange().getStart());
		txtStopFreq.setFrequency(datapool.getFrequencyRange().getStop());
		tblFrequencies.load(config.getVNAConfigDirectory() + "/" + SCAN_LIST_FILENAME);
		TraceHelper.exit(this, "load");
	}

	public void save() {
		tblFrequencies.save(config.getVNAConfigDirectory() + "/" + SCAN_LIST_FILENAME);
	}

	public void setSingleScanMode() {
		TraceHelper.entry(this, "setSingleScanMode");
		//
		buttonScan.setEnabled(false);
		cbFreeRun.setSelected(false);

		TraceHelper.exit(this, "setSingleScanMode");
	}

	/**
	 * 
	 */
	public void startFreeRun() {
		TraceHelper.entry(this, "startFreeRun");
		if (!cbFreeRun.isSelected()) {
			cbFreeRun.setSelected(true);
			if (buttonScan.isEnabled()) {
				doFreeRun();
			}
		}
		TraceHelper.exit(this, "startFreeRun");
	}

	/**
	 * 
	 */
	public void startSingleScan() {
		TraceHelper.entry(this, "startSingleScan");
		if (cbFreeRun.isSelected()) {
			cbFreeRun.setSelected(false);
		}
		if (buttonScan.isEnabled()) {
			doSingleScan();
		}
		TraceHelper.exit(this, "startSingleScan");
	}

}