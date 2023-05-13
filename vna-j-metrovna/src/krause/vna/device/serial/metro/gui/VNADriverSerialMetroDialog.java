package krause.vna.device.serial.metro.gui;

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
import krause.vna.device.serial.metro.VNADriverSerialMetroDIB;
import krause.vna.device.serial.metro.VNADriverSerialMetroMessages;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNADriverSerialMetroDialog extends VNADriverDialog {
	private JButton btOK;
	private JPanel panel;
	private JTextField txtReturnLossMin;
	private JTextField txtReturnLossMax;
	private JTextField txtTransmissionLossMin;
	private JTextField txtTransmissionLossMax;
	private JLabel lblPhaseMin;
	private JTextField txtPhaseMin;
	private JTextField txtPhaseMax;
	private JTextField txtFreqMin;
	private JTextField txtFreqMax;
	private JTextField txtSteps;
	private JTextField txtTicks;
	private IVNADriver driver;
	private JLabel lblOpenTimeout;
	private JTextField txtOpenTimeout;
	private VNADriverSerialMetroDIB dib;
	private JLabel lblReadTimeout;
	private JTextField txtReadTimeout;
	private JLabel lblCommandDelay;
	private JTextField txtCommandDelay;
	private JLabel lblBaudrate;
	private JTextField txtBaudrate;
	private ComplexInputField referenceValue;
	private JLabel lblReference;
	private JLabel lblNoOfSteps;

	public VNADriverSerialMetroDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), pMainFrame);
		TraceHelper.entry(this, "VNADriverSerialMetroDialog");

		driver = pDriver;
		dib = (VNADriverSerialMetroDIB) driver.getDeviceInfoBlock();

		setTitle(VNADriverSerialMetroMessages.getString("Dialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setProperties(config);
		setConfigurationPrefix("VNADriverSerialMetroDialog");
		setPreferredSize(new Dimension(390, 390));

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "", ""));

		//
		panel.add(new JLabel(), "");
		panel.add(new JLabel(VNADriverSerialMetroMessages.getString("minimum")), "");
		panel.add(new JLabel(VNADriverSerialMetroMessages.getString("maximum")), "wrap");

		//
		panel.add(new JLabel(VNADriverSerialMetroMessages.getString("lblReturnLoss")), "");
		txtReturnLossMax = new JTextField();
		txtReturnLossMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtReturnLossMax.setColumns(10);
		panel.add(txtReturnLossMax, "");

		txtReturnLossMin = new JTextField();
		txtReturnLossMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtReturnLossMin.setColumns(10);
		txtReturnLossMin.setEditable(true);
		panel.add(txtReturnLossMin, "wrap");

		//
		panel.add(new JLabel(VNADriverSerialMetroMessages.getString("lblTransmissionLoss")));

		txtTransmissionLossMax = new JTextField();
		txtTransmissionLossMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTransmissionLossMax.setColumns(10);
		panel.add(txtTransmissionLossMax, "");

		txtTransmissionLossMin = new JTextField();
		txtTransmissionLossMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTransmissionLossMin.setColumns(10);
		txtTransmissionLossMin.setEditable(false);
		panel.add(txtTransmissionLossMin, "wrap");

		//
		lblPhaseMin = new JLabel(VNADriverSerialMetroMessages.getString("Dialog.lblPhaseMin.text"));
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

		JLabel lblFreqMin = new JLabel(VNADriverSerialMetroMessages.getString("lblFreq"));
		panel.add(lblFreqMin, "");

		txtFreqMin = new JTextField();
		txtFreqMin.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtFreqMin, "");
		txtFreqMin.setColumns(10);

		txtFreqMax = new JTextField();
		txtFreqMax.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtFreqMax, "wrap");
		txtFreqMax.setColumns(10);

		lblNoOfSteps = new JLabel(VNADriverSerialMetroMessages.getString("Dialog.lblNoOfSteps.text"));
		panel.add(lblNoOfSteps, "");

		txtSteps = new JTextField();
		txtSteps.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSteps.setColumns(10);
		panel.add(txtSteps, "wrap");

		JLabel lblDDSTicks = new JLabel(VNADriverSerialMetroMessages.getString("Dialog.lblDDSTicks.text"));
		panel.add(lblDDSTicks, "");

		txtTicks = new JTextField();
		txtTicks.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTicks.setColumns(10);
		panel.add(txtTicks, "wrap");

		lblOpenTimeout = new JLabel(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblOpenTimeout.text")); //$NON-NLS-1$
		panel.add(lblOpenTimeout, "");

		txtOpenTimeout = new JTextField();
		txtOpenTimeout.setText("0");
		txtOpenTimeout.setHorizontalAlignment(SwingConstants.RIGHT);
		txtOpenTimeout.setColumns(10);
		panel.add(txtOpenTimeout, "wrap");

		lblReadTimeout = new JLabel(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblReadTimeout.text")); //$NON-NLS-1$
		panel.add(lblReadTimeout, "");

		txtReadTimeout = new JTextField();
		txtReadTimeout.setText("0");
		txtReadTimeout.setHorizontalAlignment(SwingConstants.RIGHT);
		txtReadTimeout.setColumns(10);
		panel.add(txtReadTimeout, "wrap");

		lblCommandDelay = new JLabel(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblCommandDelay.text")); //$NON-NLS-1$
		panel.add(lblCommandDelay, "");

		txtCommandDelay = new JTextField();
		txtCommandDelay.setText("0");
		txtCommandDelay.setHorizontalAlignment(SwingConstants.RIGHT);
		txtCommandDelay.setColumns(10);
		panel.add(txtCommandDelay, "wrap");

		//
		lblBaudrate = new JLabel(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblBaudrate.text")); //$NON-NLS-1$
		panel.add(lblBaudrate, "");

		txtBaudrate = new JTextField();
		txtBaudrate.setText("0");
		txtBaudrate.setHorizontalAlignment(SwingConstants.RIGHT);
		txtBaudrate.setColumns(10);
		panel.add(txtBaudrate, "wrap");

		// reference value
		lblReference = new JLabel(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblReference.text")); //$NON-NLS-1$
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

		JButton btnReset = new JButton(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.btnReset.text"));
		pnlButtons.add(btnReset);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doReset();
			}
		});
		btnReset.setToolTipText(VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.btnReset.toolTipText"));

		btOK = new JButton(VNADriverSerialMetroMessages.getString("Button.OK"));
		btOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOK();
			}
		});

		JButton btCancel = new JButton(VNADriverSerialMetroMessages.getString("Button.Cancel"));
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
		TraceHelper.exit(this, "VNADriverSerialMetroDialog");
	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");
		ValidationResults results = new ValidationResults();
		int frq = IntegerValidator.parse(txtTicks.getText(), 999999, 999999999, VNADriverSerialMetroMessages.getString("Dialog.lblDDSTicks.text"), results);
		int openTimeout = IntegerValidator.parse(txtOpenTimeout.getText(), 500, 99000, VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblOpenTimeout.text"), results);
		int readTimeout = IntegerValidator.parse(txtReadTimeout.getText(), 500, 99000, VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblReadTimeout.text"), results);
		int commandDelay = IntegerValidator.parse(txtCommandDelay.getText(), 50, 99000, VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblCommandDelay.text"), results);
		int baudrate = IntegerValidator.parse(txtBaudrate.getText(), 1200, 115200, VNADriverSerialMetroMessages.getString("VNADriverSerialStdDialog.lblBaudrate.text"), results);
		int steps = IntegerValidator.parse(txtSteps.getText(), 2000, 25000, VNADriverSerialMetroMessages.getString("Dialog.lblNoOfSteps.text"), results);

		long freqMin = LongValidator.parse(txtFreqMin.getText(), VNADriverSerialMetroDIB.MIN_FREQUENCY, VNADriverSerialMetroDIB.MAX_FREQUENCY, VNADriverSerialMetroMessages.getString("lblFreq"), results);
		long freqMax = LongValidator.parse(txtFreqMax.getText(), VNADriverSerialMetroDIB.MIN_FREQUENCY, VNADriverSerialMetroDIB.MAX_FREQUENCY, VNADriverSerialMetroMessages.getString("lblFreq"), results);

		double maxTransmissionLoss = DoubleValidator.parse(txtTransmissionLossMax.getText(), VNADriverSerialMetroDIB.MAX_TRANSMISSIONLOSS, VNADriverSerialMetroDIB.MIN_LOSS, VNADriverSerialMetroMessages.getString("lblTransmissionLoss"), results);
		double minReturnLoss = DoubleValidator.parse(txtReturnLossMin.getText(), VNADriverSerialMetroDIB.MAX_TRANSMISSIONLOSS, VNADriverSerialMetroDIB.MIN_LOSS, VNADriverSerialMetroMessages.getString("lblReturnLoss"), results);
		double maxReturnLoss = DoubleValidator.parse(txtReturnLossMax.getText(), VNADriverSerialMetroDIB.MAX_TRANSMISSIONLOSS, VNADriverSerialMetroDIB.MIN_LOSS, VNADriverSerialMetroMessages.getString("lblReturnLoss"), results);
				
		if (results.isEmpty()) {
			dib.setDdsTicksPerMHz(frq);
			dib.setAfterCommandDelay(commandDelay);
			dib.setReadTimeout(readTimeout);
			dib.setOpenTimeout(openTimeout);
			dib.setBaudrate(baudrate);
			dib.setReferenceResistance(referenceValue.getComplexValue());
			dib.setNumberOfSamples4Calibration(steps);
			
			dib.setMaxReflectionLoss(maxReturnLoss);
			dib.setMaxTransmissionLoss(maxTransmissionLoss);
			
			dib.setMinLoss(minReturnLoss);
			
			dib.setMinFrequency(freqMin);
			dib.setMaxFrequency(freqMax);
			
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

	private void updateFieldsFromDIB(VNADriverSerialMetroDIB pDIB) {
		txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxFrequency()));
		txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinFrequency()));

		txtReturnLossMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxReflectionLoss()));
		txtReturnLossMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinLoss()));

		txtTransmissionLossMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxTransmissionLoss()));
		txtTransmissionLossMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinLoss()));

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
