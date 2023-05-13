package krause.vna.device.serial.std.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.complex.Complex;

import krause.common.validation.DoubleValidator;
import krause.common.validation.IntegerValidator;
import krause.common.validation.LongValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.std.VNADriverSerialStdDIB;
import krause.vna.device.serial.std.VNADriverSerialStdMessages;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNADriverSerialStdDialog extends VNADriverDialog {
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
	private IVNADriver driver;
	private JLabel lblOpenTimeout;
	private JTextField txtOpenTimeout;
	private VNADriverSerialStdDIB dib;
	private JLabel lblReadTimeout;
	private JTextField txtReadTimeout;
	private JLabel lblCommandDelay;
	private JTextField txtCommandDelay;
	private JLabel lblBaudrate;
	private JTextField txtBaudrate;
	private ComplexInputField referenceValue;
	private JLabel lblReference;
	private JLabel lblNoOfSteps;

	public VNADriverSerialStdDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), pMainFrame);
		TraceHelper.entry(this, "VNADriverSerialStdDialog");

		driver = pDriver;
		dib = (VNADriverSerialStdDIB) driver.getDeviceInfoBlock();

		setTitle(VNADriverSerialStdMessages.getString("Dialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setProperties(config);
		setConfigurationPrefix("VNADriverSerialStdDialog");
		setPreferredSize(new Dimension(400, 360));

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "", ""));

		//
		panel.add(new JLabel(), "");

		//
		lblMax = new JLabel(VNADriverSerialStdMessages.getString("Dialog.lblPhaseMax.text"));
		panel.add(lblMax, "");

		//
		JLabel lblMin = new JLabel(VNADriverSerialStdMessages.getString("Dialog.lblLossMax.text"));
		panel.add(lblMin, "wrap");

		JLabel lblLoss = new JLabel(VNADriverSerialStdMessages.getString("Dialog.lblLossMin.text"));
		panel.add(lblLoss, "");

		txtLossMin = new JTextField();
		txtLossMin.setEditable(false);
		txtLossMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLossMin.setColumns(10);
		panel.add(txtLossMin, "");

		txtLossMax = new JTextField();
		txtLossMax.setEditable(true);
		txtLossMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLossMax.setColumns(10);
		panel.add(txtLossMax, "wrap");

		lblPhaseMin = new JLabel(VNADriverSerialStdMessages.getString("Dialog.lblPhaseMin.text"));
		panel.add(lblPhaseMin, "");

		txtPhaseMin = new JTextField();
		txtPhaseMin.setEditable(false);
		txtPhaseMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPhaseMin.setColumns(10);
		panel.add(txtPhaseMin, "");

		txtPhaseMax = new JTextField();
		txtPhaseMax.setEditable(false);
		txtPhaseMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPhaseMax.setColumns(10);
		panel.add(txtPhaseMax, "wrap");

		JLabel lblFreqMin = new JLabel(VNADriverSerialStdMessages.getString("Dialog.lblFreqMin.text"));
		panel.add(lblFreqMin, "");

		txtFreqMin = new JTextField();
		txtFreqMin.setEditable(true);
		txtFreqMin.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtFreqMin, "");
		txtFreqMin.setColumns(10);

		txtFreqMax = new JTextField();
		txtFreqMax.setEditable(true);
		txtFreqMax.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtFreqMax, "wrap");
		txtFreqMax.setColumns(10);

		lblNoOfSteps = new JLabel(VNADriverSerialStdMessages.getString("Dialog.lblNoOfSteps.text"));
		panel.add(lblNoOfSteps, "");

		txtSteps = new JTextField();
		txtSteps.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSteps.setColumns(10);
		panel.add(txtSteps, "wrap");

		JLabel lblDDSTicks = new JLabel(VNADriverSerialStdMessages.getString("Dialog.lblDDSTicks.text"));
		panel.add(lblDDSTicks, "");

		txtTicks = new JTextField();
		txtTicks.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTicks.setColumns(10);
		panel.add(txtTicks, "wrap");

		lblOpenTimeout = new JLabel(VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.lblOpenTimeout.text")); //$NON-NLS-1$
		panel.add(lblOpenTimeout, "");

		txtOpenTimeout = new JTextField();
		txtOpenTimeout.setText("0");
		txtOpenTimeout.setHorizontalAlignment(SwingConstants.RIGHT);
		txtOpenTimeout.setColumns(10);
		panel.add(txtOpenTimeout, "wrap");

		lblReadTimeout = new JLabel(VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.lblReadTimeout.text")); //$NON-NLS-1$
		panel.add(lblReadTimeout, "");

		txtReadTimeout = new JTextField();
		txtReadTimeout.setText("0");
		txtReadTimeout.setHorizontalAlignment(SwingConstants.RIGHT);
		txtReadTimeout.setColumns(10);
		panel.add(txtReadTimeout, "wrap");

		lblCommandDelay = new JLabel(VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.lblCommandDelay.text")); //$NON-NLS-1$
		panel.add(lblCommandDelay, "");

		txtCommandDelay = new JTextField();
		txtCommandDelay.setText("0");
		txtCommandDelay.setHorizontalAlignment(SwingConstants.RIGHT);
		txtCommandDelay.setColumns(10);
		panel.add(txtCommandDelay, "wrap");

		//
		lblBaudrate = new JLabel(VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.lblBaudrate.text")); //$NON-NLS-1$
		panel.add(lblBaudrate, "");

		txtBaudrate = new JTextField();
		txtBaudrate.setText("0");
		txtBaudrate.setHorizontalAlignment(SwingConstants.RIGHT);
		txtBaudrate.setColumns(10);
		panel.add(txtBaudrate, "wrap");

		// reference value
		lblReference = new JLabel(VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.lblReference.text")); //$NON-NLS-1$
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

		JButton btnReset = new JButton(VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.btnReset.text"));
		pnlButtons.add(btnReset);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doReset();
			}
		});
		btnReset.setToolTipText(VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.btnReset.toolTipText"));

		btOK = new JButton(VNADriverSerialStdMessages.getString("Button.OK"));
		btOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOK();
			}
		});

		JButton btCancel = new JButton(VNADriverSerialStdMessages.getString("Button.Cancel"));
		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDialogCancel();
			}
		});
		pnlButtons.add(btCancel);
		pnlButtons.add(btOK);
		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNADriverSerialStdDialog");
	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");
		ValidationResults results = new ValidationResults();
		int ticks = IntegerValidator.parse(txtTicks.getText(), 999999, 999999999, VNADriverSerialStdMessages.getString("Dialog.lblDDSTicks.text"), results);
		int openTimeout = IntegerValidator.parse(txtOpenTimeout.getText(), 500, 99000, VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.lblOpenTimeout.text"), results);
		int readTimeout = IntegerValidator.parse(txtReadTimeout.getText(), 500, 99000, VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.lblReadTimeout.text"), results);
		int commandDelay = IntegerValidator.parse(txtCommandDelay.getText(), 50, 99000, VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.lblCommandDelay.text"), results);
		int baudrate = IntegerValidator.parse(txtBaudrate.getText(), 1200, 115200, VNADriverSerialStdMessages.getString("VNADriverSerialStdDialog.lblBaudrate.text"), results);
		int steps = IntegerValidator.parse(txtSteps.getText(), 2000, 25000, VNADriverSerialStdMessages.getString("Dialog.lblNoOfSteps.text"), results);
		long freqMin = LongValidator.parse(txtFreqMin.getText(), 100000, 3000000000l, "min f", results);
		long freqMax = LongValidator.parse(txtFreqMax.getText(), 100000, 3000000000l, "max f", results);
		double lossMax = DoubleValidator.parse(txtLossMax.getText(), -200.0, 200.0, VNADriverSerialStdMessages.getString("Dialog.lblLossMin.text"), results);

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
			
			dib.setMaxLoss(lossMax);
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

	private void updateFieldsFromDIB(VNADriverSerialStdDIB pDIB) {
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
