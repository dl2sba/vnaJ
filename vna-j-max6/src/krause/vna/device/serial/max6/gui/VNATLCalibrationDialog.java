package krause.vna.device.serial.max6.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import krause.util.ras.logging.TraceHelper;
import krause.vna.background.VNABackgroundJob;
import krause.vna.background.VnaBackgroundTask;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.max6.VNADriverSerialMax6DIB;
import krause.vna.device.serial.max6.VNADriverSerialMax6Messages;
import krause.vna.device.serial.max6.VNARssPair;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.FrequencyInputField;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNATLCalibrationDialog extends VNADriverDialog implements IVNADataConsumer {
	private VNADriverSerialMax6DIB dib;
	private IVNADriver driver;

	private JButton btOK;
	private JPanel panel;

	private FrequencyInputField frq;

	private VNAScanMode mode;
	private int currStep = 0;

	private JTextField txtMessUss;
	private VNARssPair currPair;
	private JTextField txtMessdBm;
	private double ONE_MW = 0.001;
	private JButton btLoop;
	private JButton btAttn;
	private JTextField txtRawLoop;
	private JTextField txtRawAttn;
	private JTextField txtCalAttn;
	private JTextField txtCalLoop;
	private JTextField txtAttn;
	private JTextField txtCalRssOffset;
	private JTextField txtCalRssScale;
	private JLabel lblStatus;

	public VNATLCalibrationDialog(VNAMainFrame pMainFrame, IVNADriver pDriver, VNAScanMode pMode) {
		super(pMainFrame.getJFrame(), pMainFrame);
		TraceHelper.entry(this, "VNATLCalibrationDialog");
		setProperties(VNAConfig.getSingleton());
		setConfigurationPrefix("VNATLCalibrationDialog");

		driver = pDriver;
		dib = (VNADriverSerialMax6DIB) driver.getDeviceInfoBlock();
		mode = pMode;

		currPair = createInitialRssPair();

		setTitle(VNAMessages.getString("VNATLCalibrationDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(300, 300));
		setPreferredSize(new Dimension(400, 340));

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[][][][][]", "[]"));

		JTextField tf;
		//
		panel.add(new JLabel("Cal. freq. (Hz):"), "");
		frq = new FrequencyInputField("fromFreq", dib.getMinFrequency(), dib.getMinFrequency(), dib.getMaxFrequency());
		frq.setColumns(10);
		frq.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				doStep0();
			}
		});
		panel.add(frq, "grow,wrap");

		//
		panel.add(new JLabel("Measured Uss (V):"), "");
		txtMessUss = new JTextField();
		txtMessUss.setHorizontalAlignment(JTextField.RIGHT);
		txtMessUss.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				txtMessUss.select(0, 999);
			}

			public void focusLost(FocusEvent e) {
				calculatedBmFromUss();
			}
		});
		panel.add(txtMessUss, "grow");

		txtMessdBm = new JTextField();
		txtMessdBm.setHorizontalAlignment(JTextField.RIGHT);
		txtMessdBm.setEditable(false);
		panel.add(txtMessdBm, "grow");
		panel.add(new JLabel("dBm"), "wrap");

		panel.add(new JLabel("Cal. Attenuator (dB):"), "");
		txtAttn = new JTextField();
		txtAttn.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				try {
					double v = VNAFormatFactory.getReflectionLossFormat().parse(txtAttn.getText()).doubleValue();
					txtAttn.setText(VNAFormatFactory.getReflectionLossFormat().format(v));
				} catch (ParseException e1) {
				}
			}
		});
		txtAttn.setHorizontalAlignment(JTextField.RIGHT);
		panel.add(txtAttn, "grow,wrap");

		panel.add(new JLabel(), "");
		panel.add(new JLabel("Loop"), "");
		panel.add(new JLabel("Atten."), "wrap");

		//
		//
		panel.add(new JLabel("Read:"), "");
		//
		btLoop = new JButton("Loop");
		btLoop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doStep2();
			}
		});
		panel.add(btLoop, "grow");

		//
		btAttn = new JButton("Atten.");
		btAttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doStep3();
			}
		});
		panel.add(btAttn, "grow,wrap");

		//
		panel.add(new JLabel("Raw:"), "");
		txtRawLoop = new JTextField();
		txtRawLoop.setHorizontalAlignment(JTextField.RIGHT);
		txtRawLoop.setEditable(false);
		txtRawLoop.setColumns(10);
		panel.add(txtRawLoop, "");

		txtRawAttn = new JTextField();
		txtRawAttn.setHorizontalAlignment(JTextField.RIGHT);
		txtRawAttn.setEditable(false);
		txtRawAttn.setColumns(10);
		panel.add(txtRawAttn, "wrap");

		//
		panel.add(new JLabel("MAX6:"), "");
		txtCalLoop = new JTextField();
		txtCalLoop.setHorizontalAlignment(JTextField.RIGHT);
		txtCalLoop.setEditable(false);
		txtCalLoop.setColumns(10);
		panel.add(txtCalLoop, "");

		txtCalAttn = new JTextField();
		txtCalAttn.setHorizontalAlignment(JTextField.RIGHT);
		txtCalAttn.setEditable(false);
		txtCalAttn.setColumns(10);
		panel.add(txtCalAttn, "wrap");

		//
		panel.add(new JLabel());
		panel.add(new JLabel("Offset (dB)"), "");
		panel.add(new JLabel("Scale"), "wrap");

		//
		panel.add(new JLabel("Initial:"), "");
		tf = new JTextField(VNAFormatFactory.getReflectionLossFormat().format(currPair.getOffset()));
		tf.setEditable(false);
		tf.setHorizontalAlignment(JTextField.RIGHT);
		tf.setColumns(10);
		panel.add(tf, "");

		//
		tf = new JTextField(VNAFormatFactory.getTransmissionScaleFormat().format(currPair.getScale()));
		tf.setColumns(10);
		tf.setEditable(false);
		tf.setHorizontalAlignment(JTextField.RIGHT);
		panel.add(tf, "grow,wrap");

		//
		panel.add(new JLabel("Calibrated:"), "");
		txtCalRssOffset = new JTextField();
		txtCalRssOffset.setEditable(false);
		txtCalRssOffset.setHorizontalAlignment(JTextField.RIGHT);
		txtCalRssOffset.setColumns(10);
		panel.add(txtCalRssOffset, "");

		//
		txtCalRssScale = new JTextField();
		txtCalRssScale.setColumns(10);
		txtCalRssScale.setEditable(false);
		txtCalRssScale.setHorizontalAlignment(JTextField.RIGHT);
		panel.add(txtCalRssScale, "grow,wrap");

		//
		lblStatus = new JLabel();
		panel.add(lblStatus, "span 4,grow,wrap");
		//
		JPanel pnlButtons = new JPanel();
		pnlButtons.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		btOK = new JButton(VNADriverSerialMax6Messages.getString("Button.OK"));
		btOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOK();
			}
		});
		btOK.setEnabled(false);

		JButton btCancel = new JButton(VNADriverSerialMax6Messages.getString("Button.Cancel"));
		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDialogCancel();
			}
		});
		pnlButtons.add(new HelpButton(this, "VNATLCalibrationDialog"));
		pnlButtons.add(btCancel);
		pnlButtons.add(btOK);
		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNATLCalibrationDialog");
	}

	/**
	 * 
	 */
	protected void calculatedBmFromUss() {
		TraceHelper.entry(this, "calculatedBmFromUss");
		try {
			Number num = VNAFormatFactory.getRSSFormat().parse(txtMessUss.getText());
			double uss = num.doubleValue();
			double ueff = uss / (2 * Math.sqrt(2.0));
			double peff = ueff * ueff / dib.getReferenceResistance().getReal();
			double dbm = 10 * Math.log10(peff / ONE_MW);
			txtMessdBm.setText(VNAFormatFactory.getReflectionLossFormat().format(dbm));
		} catch (ParseException e) {
		}
		TraceHelper.exit(this, "calculatedBmFromUss");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.data.IVNADataConsumer#consumeDataBlock(java.util.List)
	 */
	public void consumeDataBlock(List<VNABackgroundJob> jobs) {
		TraceHelper.entry(this, "consumeDataBlock");
		if (jobs != null) {
			int rss1 = 0;
			for (VNABackgroundJob job : jobs) {
				if (job.getNumberOfSamples() == 1) {
					VNASampleBlock res = job.getResult();
					VNABaseSample sample = res.getSamples()[0];
					rss1 += sample.getRss1();
				}
			}
			rss1 /= jobs.size();

			if (currStep == 2) {
				processRawStep2(rss1);
			} else if (currStep == 3) {
				processRawStep3(rss1);
			} else {

			}
		}
		reenableAllFields();
		TraceHelper.exit(this, "consumeDataBlock");
	}

	/**
	 * @return
	 */
	private VNARssPair createInitialRssPair() {
		VNARssPair rc = null;
		TraceHelper.entry(this, "createInitialRssPair");
		if (mode.isRss1Mode()) {
			rc = new VNARssPair(dib.getRss1Offset(), dib.getRss1Scale());
		} else if (mode.isRss2Mode()) {
			rc = new VNARssPair(dib.getRss2Offset(), dib.getRss2Scale());
		} else if (mode.isRss3Mode()) {
			rc = new VNARssPair(dib.getRss3Offset(), dib.getRss3Scale());
		}
		TraceHelper.exit(this, "createInitialRssPair");
		return rc;
	}

	/**
	 * 
	 */
	private void disableAllFields() {
		TraceHelper.entry(this, "disableAllFields");
		//
		btLoop.setEnabled(true);
		btAttn.setEnabled(true);
		txtMessUss.setEnabled(false);
		txtAttn.setEnabled(false);
		TraceHelper.exit(this, "disableAllFields");
	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		super.dispose();
		TraceHelper.exit(this, "dispose");

	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		frq.setFrequency(100000000);
		frq.grabFocus();
		//
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");
		try {
			dib.setRss1Offset(VNAFormatFactory.getReflectionLossFormat().parse(txtCalRssOffset.getText()).doubleValue());
			dib.setRss1Scale(VNAFormatFactory.getTransmissionScaleFormat().parse(txtCalRssScale.getText()).doubleValue());
			dispose();
		} catch (ParseException e) {
		}
		TraceHelper.exit(this, "doOK");
	}

	/**
	 * 
	 */
	protected void doStep0() {
		TraceHelper.entry(this, "doStep0");
		currStep = 0;

		txtRawAttn.setText("");
		txtRawLoop.setText("");

		txtCalAttn.setText("");
		txtCalLoop.setText("");

		txtCalRssOffset.setText("");
		txtCalRssScale.setText("");

		updateFrequency();
		TraceHelper.exit(this, "doStep0");
	}

	/**
	 * 
	 */
	protected void doStep1() {
		TraceHelper.entry(this, "doStep1");
		currStep = 1;
		updateFrequency();
		TraceHelper.exit(this, "doStep1");
	}

	/**
	 * 
	 */
	protected void doStep2() {
		TraceHelper.entry(this, "doStep2");
		currStep = 2;
		updateFrequency();
		TraceHelper.exit(this, "doStep2");
	}

	/**
	 * 
	 */
	protected void doStep3() {
		TraceHelper.entry(this, "doStep3");
		currStep = 3;
		updateFrequency();
		TraceHelper.exit(this, "doStep3");
	}

	/**
	 * 
	 */
	private void processNewCal() {
		TraceHelper.entry(this, "processNewCal");
		try {
			int rawLoop = Integer.parseInt(txtRawLoop.getText());
			int rawAttn = Integer.parseInt(txtRawAttn.getText());
			double attnRef = VNAFormatFactory.getReflectionLossFormat().parse(txtAttn.getText()).doubleValue();
			double refPwr = VNAFormatFactory.getReflectionLossFormat().parse(txtMessdBm.getText()).doubleValue();

			int rawDelta = rawLoop - rawAttn;
			double oneDB = attnRef / rawDelta;

			double newLoop = rawLoop * oneDB;

			double loopOffset = -(refPwr - newLoop);

			txtCalRssScale.setText(VNAFormatFactory.getTransmissionScaleFormat().format(oneDB));
			txtCalRssOffset.setText(VNAFormatFactory.getReflectionLossFormat().format(loopOffset));

			btOK.setEnabled(true);
		} catch (Exception e) {
			btOK.setEnabled(false);
		}
		TraceHelper.exit(this, "processNewCal");
	}

	/**
	 * @param rss
	 */
	private void processRawStep2(int rss) {
		TraceHelper.entry(this, "processRawStep2");
		txtRawLoop.setText("" + rss);
		double l = rss * currPair.getScale() - currPair.getOffset();
		txtCalLoop.setText(VNAFormatFactory.getReflectionLossFormat().format(l));
		processNewCal();
		TraceHelper.exit(this, "processRawStep2");
	}

	/**
	 * @param rss
	 */
	private void processRawStep3(int rss) {
		TraceHelper.entry(this, "processRawStep3");
		txtRawAttn.setText("" + rss);
		double l = rss * currPair.getScale() - currPair.getOffset();
		txtCalAttn.setText(VNAFormatFactory.getReflectionLossFormat().format(l));
		processNewCal();
		TraceHelper.exit(this, "processRawStep3");
	}

	/**
	 * 
	 */
	private void reenableAllFields() {
		TraceHelper.entry(this, "reenableAllFields");
		//
		btLoop.setEnabled(true);
		btAttn.setEnabled(true);
		txtMessUss.setEnabled(true);
		txtAttn.setEnabled(true);

		TraceHelper.exit(this, "reenableAllFields");
	}

	/**
	 * @param frequency
	 */
	protected void updateFrequency() {
		TraceHelper.entry(this, "updateFrequency", "step=" + currStep);

		//
		disableAllFields();
		btLoop.setEnabled(false);
		btAttn.setEnabled(false);

		//
		long f = frq.getFrequency();

		// create one instance
		VnaBackgroundTask backgroundTask = new VnaBackgroundTask(driver);
		for (int i = 0; i < 10; ++i) {
			VNABackgroundJob job = new VNABackgroundJob();
			job.setNumberOfSamples(1);
			job.setFrequencyRange(new VNAFrequencyRange(f, f));
			job.setScanMode(mode);
			backgroundTask.addJob(job);
		}
		backgroundTask.addDataConsumer(this);
		backgroundTask.setStatusLabel(lblStatus);
		backgroundTask.execute();
		TraceHelper.exit(this, "updateFrequency");
	}
}
