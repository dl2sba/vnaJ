package krause.vna.gui.cable;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

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
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNACableLengthDialog extends KrauseDialog implements IVNADataConsumer {
	private JButton btMeasure;
	private JButton btOK;
	private VNAVelocityFactorTable tblVelocity;

	private JLabel lblVelocityFactor1;
	private JTextField txtLength1;
	private transient VNAMainFrame mainFrame;
	private JTextField txtLength2;
	private JTextField txtVelocityFactor2;
	private JPanel pnlVariableVelocity;
	private JLabel lblVelocityFactor3;
	private JTextField txtVelocityFactor3;
	private JLabel lblLength3;
	private JTextField txtLength3;
	private JPanel pnlKnownCableLength;
	private JPanel pnlKnownVelocity;

	private VNADeviceInfoBlock dib;
	private final VNAConfig config = VNAConfig.getSingleton();
	private final transient VNADataPool datapool = VNADataPool.getSingleton();

	private JRadioButton rdbtnM;
	private JRadioButton rdbtnFeet;
	private JLabel lblUnit;

	private long startFreq;
	private long stopFreq;
	private int numIterations;
	private StatusBar statusBar;

	/**
	 * 
	 * @param pMainFrame
	 */
	public VNACableLengthDialog(VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);
		this.mainFrame = pMainFrame;
		this.dib = datapool.getDriver().getDeviceInfoBlock();
		setConfigurationPrefix("CableLength");
		setProperties(config);

		setTitle(VNAMessages.getString("VNACableLengthDialog.title"));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(850, 600));
		getContentPane().setLayout(new MigLayout("", "[grow,fill]", "[][][][grow,fill]"));

		// ***********************************************************************
		pnlKnownVelocity = new JPanel();
		getContentPane().add(pnlKnownVelocity, "wrap");
		pnlKnownVelocity.setBorder(new TitledBorder(null, VNAMessages.getString("VNACableLengthDialog.rdbtnKnowVelocityFactor.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlKnownVelocity.setLayout(new MigLayout("", "[][grow,fill]", "[]"));

		lblVelocityFactor1 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblVelocityFactor.text"));
		pnlKnownVelocity.add(lblVelocityFactor1, "");

		tblVelocity = new VNAVelocityFactorTable();
		JScrollPane scrollPane = new JScrollPane(tblVelocity);
		scrollPane.setViewportBorder(null);
		pnlKnownVelocity.add(scrollPane, "wrap");

		JLabel lblLength1 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblLength.text"));
		pnlKnownVelocity.add(lblLength1, "");

		txtLength1 = new JTextField();
		txtLength1.setFocusable(false);
		txtLength1.setFocusTraversalKeysEnabled(false);
		pnlKnownVelocity.add(txtLength1, "");
		txtLength1.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLength1.setEditable(false);
		txtLength1.setColumns(6);

		// ***********************************************************************
		pnlKnownCableLength = new JPanel();
		getContentPane().add(pnlKnownCableLength, "wrap");
		pnlKnownCableLength.setBorder(new TitledBorder(null, VNAMessages.getString("VNACableLengthDialog.rdbtnKnownCableLength.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlKnownCableLength.setLayout(new MigLayout("", "[][][][grow,fill]", "[]"));

		JLabel lblLength2 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblKnownLength.text"));
		pnlKnownCableLength.add(lblLength2, "");

		txtLength2 = new JTextField();
		txtLength2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					String t = txtLength2.getText();
					if (t != null && t.length() > 0) {
						double clen = VNAFormatFactory.getLengthFormat().parse(txtLength2.getText()).doubleValue();
						txtLength2.setText(VNAFormatFactory.getLengthFormat().format(clen));
					}
				} catch (ParseException e1) {
					String m = MessageFormat.format(VNAMessages.getString("VNACableLengthDialog.Err.1"), txtLength2.getText());
					JOptionPane.showMessageDialog(mainFrame.getJFrame(), m, VNAMessages.getString("VNACableLengthDialog.Err.2"), JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		txtLength2.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlKnownCableLength.add(txtLength2, "");
		txtLength2.setColumns(6);

		JLabel lblVelocityFactor2 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblVelocityFactor.text"));
		pnlKnownCableLength.add(lblVelocityFactor2, "");

		txtVelocityFactor2 = new JTextField();
		txtVelocityFactor2.setFocusable(false);
		txtVelocityFactor2.setFocusTraversalKeysEnabled(false);
		txtVelocityFactor2.setHorizontalAlignment(SwingConstants.RIGHT);
		txtVelocityFactor2.setEditable(false);
		txtVelocityFactor2.setColumns(6);
		pnlKnownCableLength.add(txtVelocityFactor2, "");

		// ***********************************************************************
		pnlVariableVelocity = new JPanel();
		getContentPane().add(pnlVariableVelocity, "wrap");
		pnlVariableVelocity.setLayout(new MigLayout("", "[][][][grow,fill]", "[]"));

		pnlVariableVelocity.setBorder(new TitledBorder(null, VNAMessages.getString("VNACableLengthDialog.rdbtnVariableVelocityFactor.text"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		lblVelocityFactor3 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblVelocityFactor.text"));
		pnlVariableVelocity.add(lblVelocityFactor3, "");

		txtVelocityFactor3 = new JTextField();
		txtVelocityFactor3.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					String t = txtVelocityFactor3.getText();
					if (t != null && t.length() > 0) {
						double clen = VNAFormatFactory.getVelocityFormat().parse(txtVelocityFactor3.getText()).doubleValue();
						txtVelocityFactor3.setText(VNAFormatFactory.getVelocityFormat().format(clen));
					}
				} catch (ParseException e1) {
					String m = MessageFormat.format(VNAMessages.getString("VNACableLengthDialog.Err.1"), txtVelocityFactor3.getText());
					JOptionPane.showMessageDialog(mainFrame.getJFrame(), m, VNAMessages.getString("VNACableLengthDialog.Err.2"), JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		txtVelocityFactor3.setHorizontalAlignment(SwingConstants.RIGHT);
		txtVelocityFactor3.setColumns(6);
		pnlVariableVelocity.add(txtVelocityFactor3, "");

		lblLength3 = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblLength.text"));
		pnlVariableVelocity.add(lblLength3, "");

		txtLength3 = new JTextField();
		txtLength3.setFocusTraversalKeysEnabled(false);
		txtLength3.setFocusable(false);
		txtLength3.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLength3.setEditable(false);
		txtLength3.setColumns(6);
		pnlVariableVelocity.add(txtLength3, "");
		ButtonGroup bg = new ButtonGroup();

		// ***********************************************************************
		JPanel pnlButtons = new JPanel();
		getContentPane().add(pnlButtons, "wrap");
		pnlButtons.setLayout(new MigLayout("", "[grow,fill][grow][grow][grow][grow,fill]", "[]"));

		btMeasure = new JButton(VNAMessages.getString("VNACableLengthDialog.btMeasure.text"));
		pnlButtons.add(btMeasure, "cell 0 0,alignx left,aligny center");
		btMeasure.addActionListener(e -> doMeasure());

		lblUnit = new JLabel(VNAMessages.getString("VNACableLengthDialog.lblUnit.text")); //$NON-NLS-1$
		pnlButtons.add(lblUnit, "");

		rdbtnM = new JRadioButton(VNAMessages.getString("VNACableLengthDialog.rdbtnM.text")); //$NON-NLS-1$
		pnlButtons.add(rdbtnM, "");
		bg.add(rdbtnM);

		rdbtnFeet = new JRadioButton(VNAMessages.getString("VNACableLengthDialog.rdbtnFeet.text")); //$NON-NLS-1$
		pnlButtons.add(rdbtnFeet, "");

		bg.add(rdbtnFeet);

		btOK = new JButton(VNAMessages.getString("Button.Close"));
		pnlButtons.add(btOK, "");
		btOK.addActionListener(e -> doDialogCancel());

		// ***********************************************************************
		JPanel pnlStatus = new JPanel();
		pnlStatus.setLayout(new MigLayout("", "[][grow,fill]", "[]"));
		getContentPane().add(pnlStatus, "wrap");

		pnlStatus.add(new JLabel(VNAMessages.getString("VNACableLengthDialog.lblStatus")), "");
		statusBar = new StatusBar();
		pnlStatus.add(statusBar, "");
		JLabel lbl;

		//
		lbl = new JLabel(VNAMessages.getString("Message.Ready"));
		lbl.setOpaque(true);
		statusBar.addZone("status", lbl, "*");
		//
		doDialogInit();
	}

	/**
	 * 
	 */
	private void initiateScan() {
		// setup cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
	}

	/**
	 * 
	 */
	protected void doMeasure() {
		// disable user actions
		btMeasure.setEnabled(false);
		btOK.setEnabled(false);

		//
		startFreq = datapool.getMainCalibrationBlock().getStartFrequency();
		stopFreq = datapool.getMainCalibrationBlock().getStopFrequency();

		//
		numIterations = 1;

		//
		initiateScan();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.data.IVNADataConsumer#consumeDataBlock(java.util.List)
	 */
	public void consumeDataBlock(List<VNABackgroundJob> jobs) {
		final String methodName = "consumeDataBlock";
		TraceHelper.entry(this, methodName);

		boolean enableButtons = true;

		//
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
						final VNACalibrationBlock resizedCalibrationBlock = VNACalibrationBlockHelper.createResizedCalibrationBlock(mainCalibrationBlock, startFreq, stopFreq, rawData.getNumberOfSteps());

						// reflection mode?
						if (rawData.getScanMode().isReflectionMode()) {
							// yes
							// reflection mode
							// calibrate them
							final VNACalibrationContext context = mathHelper.createCalibrationContextForCalibratedSamples(resizedCalibrationBlock);
							context.setConversionTemperature(rawData.getDeviceTemperature());

							final VNACalibratedSampleBlock samples = mathHelper.createCalibratedSamples(context, rawData);

							// create helper
							VNACableMeasurementHelper helper = new VNACableMeasurementHelper((dib.getMinPhase() < 0), rdbtnM.isSelected());

							List<VNACalibratedSample> allPoints = helper.findAllCrossingPoints(samples);
							for (VNACalibratedSample aSample : allPoints) {
								TraceHelper.text(this, methodName, "crossingPoint f=" + aSample.getFrequency() + " rl=" + aSample.getReflectionLoss());
							}
							// find phase crossings
							List<VNACalibratedSample> points = helper.findTwoCrossingPoints(samples);
							// two crossings found?
							if (points.size() == 2) {
								// yes
								TraceHelper.text(this, methodName, "Point1=" + points.get(0).getFrequency());
								TraceHelper.text(this, methodName, "Point2=" + points.get(1).getFrequency());
								if (numIterations > 0) {
									// yes
									// now recalulate the start stop limits
									startFreq = (long) (points.get(0).getFrequency() * 0.9);
									startFreq = Math.max(startFreq, dib.getMinFrequency());

									stopFreq = (long) (points.get(1).getFrequency() * 1.1);
									stopFreq = Math.min(stopFreq, dib.getMaxFrequency());

									//
									--numIterations;

									// and measure again
									initiateScan();

									enableButtons = false;
								} else {
									// no
									// now display the result
									updateFields(points, helper);
								}
							} else {
								clearFields();
							}
						} else {
							clearFields();
						}
					} else {
						clearFields();
					}
				} else {
					clearFields();
				}
			} else {
				clearFields();
			}
		} else {
			clearFields();
		}
		if (enableButtons) {
			// enable user actions
			btMeasure.setEnabled(true);
			btOK.setEnabled(true);
			// setup cursor
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		TraceHelper.exit(this, methodName);
	}

	private void clearFields() {
		txtLength1.setText("???");
		txtVelocityFactor2.setText("???");
		txtLength3.setText("???");
	}

	private void updateFields(List<VNACalibratedSample> points, VNACableMeasurementHelper helper) {
		TraceHelper.entry(this, "updateFields");
		//
		VNACableMeasurementPoint result;

		double velocityEntered = tblVelocity.getSelectedItem().getVf();
		result = helper.calculateLength(points, velocityEntered);
		txtLength1.setBackground(Color.BLACK);
		txtLength1.setForeground(Color.YELLOW);
		txtLength1.setText(VNAFormatFactory.getLengthFormat().format(result.getLength()) + (rdbtnM.isSelected() ? " m" : " ft"));

		try {
			double clen = VNAFormatFactory.getLengthFormat().parse(txtLength2.getText()).doubleValue();
			result = helper.calculateVelocityFactor(points, clen);
			txtVelocityFactor2.setBackground(Color.BLACK);
			txtVelocityFactor2.setForeground(Color.YELLOW);
			txtVelocityFactor2.setText(VNAFormatFactory.getVelocityFormat().format(result.getVelocityFactor()));
		} catch (ParseException e) {
			// ignore
		}

		try {
			velocityEntered = VNAFormatFactory.getVelocityFormat().parse(txtVelocityFactor3.getText()).doubleValue();
			result = helper.calculateLength(points, velocityEntered);
			txtLength3.setBackground(Color.BLACK);
			txtLength3.setForeground(Color.YELLOW);
			txtLength3.setText(VNAFormatFactory.getLengthFormat().format(result.getLength()) + (rdbtnM.isSelected() ? " m" : " ft"));
		} catch (ParseException e) {
			// ignore
		}

		TraceHelper.exit(this, "updateFields");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doExit()
	 */
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		config.putInteger(getConfigurationPrefix() + ".selIdx", tblVelocity.getSelectedRow());
		config.put(getConfigurationPrefix() + ".userLength", txtLength2.getText());
		config.put(getConfigurationPrefix() + ".userVelFactor", txtVelocityFactor3.getText());

		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		//
		int selIdx = config.getInteger(getConfigurationPrefix() + ".selIdx", 0);
		tblVelocity.selectRow(selIdx);

		//
		txtLength2.setText(config.getProperty(getConfigurationPrefix() + ".userLength", ""));
		txtVelocityFactor3.setText(config.getProperty(getConfigurationPrefix() + ".userVelFactor", ""));
		//
		rdbtnM.setSelected(true);
		// add escape key to window
		addEscapeKey();
		//
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}
}
