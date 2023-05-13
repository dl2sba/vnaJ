package krause.vna.device.serial.std2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.complex.Complex;

import krause.common.validation.IntegerValidator;
import krause.common.validation.LongValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.std2.VNADriverSerialStd2DIB;
import krause.vna.device.serial.std2.VNADriverSerialStd2Messages;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNADriverSerialStd2Dialog extends VNADriverDialog {
	private JButton btOK;
	private JPanel panel;
	private JTextField txtLossMin;
	private JTextField txtLossMax;
	private JLabel lblPhaseMin;
	private JTextField txtPhaseMin;
	private JLabel lblMax;
	private JTextField txtPhaseMax;
	private JTextField txtFreqMin;
	private JTextField txtFreqMax;
	private JTextField txtSteps;
	private JTextField txtTicks;
	private transient IVNADriver driver;
	private JLabel lblOpenTimeout;
	private JTextField txtOpenTimeout;
	private VNADriverSerialStd2DIB dib;
	private JLabel lblReadTimeout;
	private JTextField txtReadTimeout;
	private JLabel lblCommandDelay;
	private JTextField txtCommandDelay;
	private JLabel lblBaudrate;
	private JTextField txtBaudrate;
	private ComplexInputField referenceValue;
	private JLabel lblReference;
	private JLabel lblNoOfSteps;

	public VNADriverSerialStd2Dialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), pMainFrame);
		final String methodName = "VNADriverSerialStd2Dialog";
		TraceHelper.entry(this, methodName);

		this.driver = pDriver;
		this.dib = (VNADriverSerialStd2DIB) this.driver.getDeviceInfoBlock();

		setTitle(VNADriverSerialStd2Messages.getString("title"));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setProperties(this.config);
		setConfigurationPrefix(methodName);
		setPreferredSize(new Dimension(395, 385));

		this.panel = new JPanel();
		getContentPane().add(this.panel, BorderLayout.CENTER);
		this.panel.setLayout(new MigLayout("", "", ""));

		//
		this.panel.add(new JLabel(), "");

		//
		this.lblMax = new JLabel(VNADriverSerialStd2Messages.getString("lblPhaseMax.text"));
		this.panel.add(this.lblMax, "");

		//
		JLabel lblMin = new JLabel(VNADriverSerialStd2Messages.getString("lblLossMax.text"));
		this.panel.add(lblMin, "wrap");

		JLabel lblLoss = new JLabel(VNADriverSerialStd2Messages.getString("lblLossMin.text"));
		this.panel.add(lblLoss, "");

		this.txtLossMin = new JTextField();
		this.txtLossMin.setEditable(false);
		this.txtLossMin.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtLossMin.setColumns(10);
		this.panel.add(this.txtLossMin, "");

		this.txtLossMax = new JTextField();
		this.txtLossMax.setEditable(false);
		this.txtLossMax.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtLossMax.setColumns(10);
		this.panel.add(this.txtLossMax, "wrap");

		this.lblPhaseMin = new JLabel(VNADriverSerialStd2Messages.getString("lblPhaseMin.text"));
		this.panel.add(this.lblPhaseMin, "");

		this.txtPhaseMin = new JTextField();
		this.txtPhaseMin.setEditable(false);
		this.txtPhaseMin.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtPhaseMin.setColumns(10);
		this.panel.add(this.txtPhaseMin, "");

		this.txtPhaseMax = new JTextField();
		this.txtPhaseMax.setEditable(false);
		this.txtPhaseMax.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtPhaseMax.setColumns(10);
		this.panel.add(this.txtPhaseMax, "wrap");

		JLabel lblFreqMin = new JLabel(VNADriverSerialStd2Messages.getString("lblFreqMin.text"));
		this.panel.add(lblFreqMin, "");

		this.txtFreqMin = new JTextField();
		this.txtFreqMin.setEditable(true);
		this.txtFreqMin.setHorizontalAlignment(SwingConstants.RIGHT);
		this.panel.add(this.txtFreqMin, "");
		this.txtFreqMin.setColumns(10);

		this.txtFreqMax = new JTextField();
		this.txtFreqMax.setEditable(true);
		this.txtFreqMax.setHorizontalAlignment(SwingConstants.RIGHT);
		this.panel.add(this.txtFreqMax, "wrap");
		this.txtFreqMax.setColumns(10);

		this.lblNoOfSteps = new JLabel(VNADriverSerialStd2Messages.getString("lblNoOfSteps.text"));
		this.panel.add(this.lblNoOfSteps, "");

		this.txtSteps = new JTextField();
		this.txtSteps.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtSteps.setColumns(10);
		this.panel.add(this.txtSteps, "wrap");

		JLabel lblDDSTicks = new JLabel(VNADriverSerialStd2Messages.getString("lblDDSTicks.text"));
		panel.add(lblDDSTicks, "");

		txtTicks = new JTextField();
		txtTicks.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTicks.setColumns(10);
		panel.add(txtTicks, "wrap");

		lblOpenTimeout = new JLabel(VNADriverSerialStd2Messages.getString("lblOpenTimeout.text")); //$NON-NLS-1$
		panel.add(lblOpenTimeout, "");

		txtOpenTimeout = new JTextField();
		txtOpenTimeout.setText("0");
		txtOpenTimeout.setHorizontalAlignment(SwingConstants.RIGHT);
		txtOpenTimeout.setColumns(10);
		panel.add(txtOpenTimeout, "wrap");

		lblReadTimeout = new JLabel(VNADriverSerialStd2Messages.getString("lblReadTimeout.text")); //$NON-NLS-1$
		panel.add(lblReadTimeout, "");

		txtReadTimeout = new JTextField();
		txtReadTimeout.setText("0");
		txtReadTimeout.setHorizontalAlignment(SwingConstants.RIGHT);
		txtReadTimeout.setColumns(10);
		panel.add(txtReadTimeout, "wrap");

		lblCommandDelay = new JLabel(VNADriverSerialStd2Messages.getString("lblCommandDelay.text")); //$NON-NLS-1$
		panel.add(lblCommandDelay, "");

		txtCommandDelay = new JTextField();
		txtCommandDelay.setText("0");
		txtCommandDelay.setHorizontalAlignment(SwingConstants.RIGHT);
		txtCommandDelay.setColumns(10);
		panel.add(txtCommandDelay, "wrap");

		//
		lblBaudrate = new JLabel(VNADriverSerialStd2Messages.getString("lblBaudrate.text")); //$NON-NLS-1$
		panel.add(lblBaudrate, "");

		txtBaudrate = new JTextField();
		txtBaudrate.setText("0");
		txtBaudrate.setHorizontalAlignment(SwingConstants.RIGHT);
		txtBaudrate.setColumns(10);
		panel.add(txtBaudrate, "wrap");

		// reference value
		lblReference = new JLabel(VNADriverSerialStd2Messages.getString("lblReference.text")); //$NON-NLS-1$
		lblReference.setBounds(10, 310, 200, 30);
		panel.add(lblReference, "");

		referenceValue = new ComplexInputField(null);
		referenceValue.setMaximum(new Complex(5000, 5000));
		referenceValue.setMinimum(new Complex(-5000, -5000));
		FlowLayout flowLayout = (FlowLayout) referenceValue.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(referenceValue, "span 2");

		JPanel pnlButtons = new JPanel();
		pnlButtons.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		JButton btnReset = new JButton(VNADriverSerialStd2Messages.getString("btnReset.text"));
		pnlButtons.add(btnReset);
		btnReset.addActionListener(e -> doReset());
		btnReset.setToolTipText(VNADriverSerialStd2Messages.getString("btnReset.toolTipText"));

		btOK = new JButton(VNADriverSerialStd2Messages.getString("Button.OK"));
		btOK.addActionListener(e -> doOK());

		JButton btCancel = new JButton(VNADriverSerialStd2Messages.getString("Button.Cancel"));
		btCancel.addActionListener(e -> doDialogCancel());
		pnlButtons.add(btCancel);
		pnlButtons.add(btOK);
		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNADriverSerialStd2Dialog");
	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");
		ValidationResults results = new ValidationResults();
		int ticks = IntegerValidator.parse(txtTicks.getText(), 999999, 999999999, VNADriverSerialStd2Messages.getString("lblDDSTicks.text"), results);
		int openTimeout = IntegerValidator.parse(txtOpenTimeout.getText(), 500, 99000, VNADriverSerialStd2Messages.getString("lblOpenTimeout.text"), results);
		int readTimeout = IntegerValidator.parse(txtReadTimeout.getText(), 500, 99000, VNADriverSerialStd2Messages.getString("lblReadTimeout.text"), results);
		int commandDelay = IntegerValidator.parse(txtCommandDelay.getText(), 50, 99000, VNADriverSerialStd2Messages.getString("lblCommandDelay.text"), results);
		int baudrate = IntegerValidator.parse(txtBaudrate.getText(), 1200, 115200, VNADriverSerialStd2Messages.getString("lblBaudrate.text"), results);
		int steps = IntegerValidator.parse(txtSteps.getText(), 2000, 25000, VNADriverSerialStd2Messages.getString("lblNoOfSteps.text"), results);
		long freqMin = LongValidator.parse(txtFreqMin.getText(), 100000, 1000000000, "min f", results);
		long freqMax = LongValidator.parse(txtFreqMax.getText(), 100000, 1000000000, "max f", results);

		if (results.isEmpty()) {
			dib.setMinFrequency(freqMin);
			dib.setMaxFrequency(freqMax);
			dib.setDdsTicksPerMHz(ticks);
			dib.setAfterCommandDelay(commandDelay);
			dib.setReadTimeout(readTimeout);
			dib.setOpenTimeout(openTimeout);
			dib.setBaudrate(baudrate);
			dib.setReferenceResistance(referenceValue.getComplexValue());
			dib.setNumberOfSamples4Calibration(steps);
			dib.store(config, driver.getDriverConfigPrefix());
			setVisible(false);
		} else {
			new ValidationResultsDialog(getOwner(), results, VNAMessages.getString("VNANetworkDialog.ErrorDialogHeader"));
		}
		TraceHelper.exit(this, "doOK");

	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		TraceHelper.exit(this, "doCANCEL");
	}

	private void updateFieldsFromDIB(VNADriverSerialStd2DIB pDIB) {
		txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxFrequency()));
		txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinFrequency()));

		txtLossMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxLoss()));
		txtLossMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinLoss()));

		txtPhaseMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxPhase()));
		txtPhaseMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinPhase()));

		txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getNumberOfSamples4Calibration()));
		txtTicks.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getDdsTicksPerMHz()));

		txtOpenTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getOpenTimeout()));
		txtReadTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getReadTimeout()));
		txtCommandDelay.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getAfterCommandDelay()));
		txtBaudrate.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getBaudrate()));

		txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getNumberOfSamples4Calibration()));
		referenceValue.setComplexValue(pDIB.getReferenceResistance());
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		updateFieldsFromDIB(dib);
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	private void doReset() {
		TraceHelper.entry(this, "doReset");
		dib.reset();
		updateFieldsFromDIB(dib);
		TraceHelper.exit(this, "doReset");
	}
}
