package krause.vna.device.serial.tiny2.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.apache.commons.math3.complex.Complex;

import krause.common.validation.DoubleValidator;
import krause.common.validation.IntegerValidator;
import krause.common.validation.LongValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.tiny2.VNADriverSerialTiny2;
import krause.vna.device.serial.tiny2.VNADriverSerialTiny2DIB;
import krause.vna.device.serial.tiny2.VNADriverSerialTiny2Messages;
import krause.vna.gui.HelpButton;
import krause.vna.gui.StatusBarLabel;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.util.SwingUtil;
import net.miginfocom.swing.MigLayout;

public class VNADriverSerialTiny2Dialog extends VNADriverDialog {

	private final transient VNADataPool datapool = VNADataPool.getSingleton();
	private final VNADriverSerialTiny2DIB dib;
	private final transient VNADriverSerialTiny2 driver;

	private JButton btCal;
	private JButton btCancel;
	private JButton btOK;
	private JButton btDefault;

	private JLabel lblPhaseMax;
	private JLabel lblPhaseMin;
	private JLabel lblReference;
	private StatusBarLabel lblStatusbar;
	private JPanel panel;
	private ComplexInputField referenceValue;
	private JTextField txtBootloaderBaudRate;
	private JTextField txtFirmware;
	private JTextField txtFreqMax;
	private JTextField txtFreqMin;

	private JTextField txtGainCorrection;
	private JTextField txtLossMax;
	private JTextField txtLossMin;
	private JTextField txtPhaseCorrection;
	private JTextField txtPhaseMax;
	private JTextField txtPhaseMin;
	private JTextField txtPower;
	private JTextField txtSteps;
	private JTextField txtTempCorrection;
	private JTextField txtTemperature;
	private JCheckBox cbPeakSuppression;
	private JCheckBox cbReferenceChannel;

	public VNADriverSerialTiny2Dialog(final VNAMainFrame pMainFrame, final IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), pMainFrame);
		TraceHelper.entry(this, "VNADriverSerialTiny2Dialog");

		driver = (VNADriverSerialTiny2) pDriver;
		dib = (VNADriverSerialTiny2DIB) driver.getDeviceInfoBlock();

		setTitle(VNADriverSerialTiny2Messages.getString("drvTitle"));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setProperties(config);
		setConfigurationPrefix("VNADriverSerialTiny2Dialog");
		setPreferredSize(new Dimension(540, 540));

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[grow][][]", ""));

		//
		panel.add(new JLabel(), "");
		lblPhaseMax = new JLabel(VNADriverSerialTiny2Messages.getString("lblMin"));
		panel.add(lblPhaseMax, "");
		JLabel lblLossMax = new JLabel(VNADriverSerialTiny2Messages.getString("lblMax"));
		panel.add(lblLossMax, "wrap");

		//
		panel.add(new JLabel(VNADriverSerialTiny2Messages.getString("lblLoss")), "");

		txtLossMin = new JTextField();
		txtLossMin.setEditable(true);
		txtLossMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLossMin.setColumns(10);
		panel.add(txtLossMin, "");
		txtLossMax = new JTextField();
		txtLossMax.setEditable(true);
		txtLossMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLossMax.setColumns(10);
		panel.add(txtLossMax, "wrap");
		//
		lblPhaseMin = new JLabel(VNADriverSerialTiny2Messages.getString("lblPhase"));
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

		//
		panel.add(new JLabel(VNADriverSerialTiny2Messages.getString("lblFreq")), "");
		txtFreqMin = new JTextField();
		txtFreqMin.setEditable(true);
		txtFreqMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFreqMin.setColumns(10);
		panel.add(txtFreqMin, "");

		txtFreqMax = new JTextField();
		txtFreqMax.setEditable(true);
		txtFreqMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFreqMax.setColumns(10);
		panel.add(txtFreqMax, "wrap");

		//
		panel.add(new JLabel(VNADriverSerialTiny2Messages.getString("lblNoOfSteps")), "");

		txtSteps = new JTextField();
		txtSteps.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSteps.setColumns(10);
		panel.add(txtSteps, "wrap");

		//
		panel.add(new JLabel(VNADriverSerialTiny2Messages.getString("lblFirmware")), "");
		txtFirmware = new JTextField();
		txtFirmware.setEditable(false);
		panel.add(txtFirmware, "grow,span 3,wrap");

		//
		panel.add(new JLabel(VNADriverSerialTiny2Messages.getString("lblPower")), "");
		txtPower = new JTextField();
		txtPower.setEditable(false);
		panel.add(txtPower, "grow,span 3,wrap");

		//
		panel.add(new JLabel(VNADriverSerialTiny2Messages.getString("lblTemperature")), "");
		txtTemperature = new JTextField();
		txtTemperature.setEditable(false);
		panel.add(txtTemperature, "grow,span 3,wrap");

		//
		lblReference = new JLabel(VNADriverSerialTiny2Messages.getString("lblReference")); //$NON-NLS-1$
		lblReference.setBounds(10, 330, 141, 30);
		panel.add(lblReference, "");
		referenceValue = new ComplexInputField(null);
		referenceValue.setMaximum(new Complex(5000, 5000));
		referenceValue.setMinimum(new Complex(-5000, -5000));
		FlowLayout flowLayout = (FlowLayout) referenceValue.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(referenceValue, "grow,span 3,wrap");

		panel.add(new JLabel(MessageFormat.format(VNADriverSerialTiny2Messages.getString("lblPhaseCorr"), VNADriverSerialTiny2DIB.MIN_CORR_PHASE, VNADriverSerialTiny2DIB.MAX_CORR_PHASE)));
		txtPhaseCorrection = new JTextField();
		txtPhaseCorrection.setText("0");
		txtPhaseCorrection.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPhaseCorrection.setColumns(10);
		panel.add(txtPhaseCorrection, "wrap");

		panel.add(new JLabel(MessageFormat.format(VNADriverSerialTiny2Messages.getString("lblGainCorr"), VNADriverSerialTiny2DIB.MIN_CORR_GAIN, VNADriverSerialTiny2DIB.MAX_CORR_GAIN)));
		txtGainCorrection = new JTextField();
		txtGainCorrection.setText("0");
		txtGainCorrection.setHorizontalAlignment(SwingConstants.RIGHT);
		txtGainCorrection.setColumns(10);
		panel.add(txtGainCorrection, "wrap");

		panel.add(new JLabel(MessageFormat.format(VNADriverSerialTiny2Messages.getString("lblTempCorr"), VNADriverSerialTiny2DIB.MIN_CORR_TEMP, VNADriverSerialTiny2DIB.MAX_CORR_TEMP)));
		txtTempCorrection = new JTextField();
		txtTempCorrection.setText("0");
		txtTempCorrection.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTempCorrection.setColumns(10);
		panel.add(txtTempCorrection, "wrap");

		panel.add(new JLabel(MessageFormat.format(VNADriverSerialTiny2Messages.getString("lblBootloaderBaudRate"), VNADriverSerialTiny2DIB.MIN_BOOTBAUD, VNADriverSerialTiny2DIB.MAX_BOOTBAUD)));
		txtBootloaderBaudRate = new JTextField();
		txtBootloaderBaudRate.setText("0");
		txtBootloaderBaudRate.setHorizontalAlignment(SwingConstants.RIGHT);
		txtBootloaderBaudRate.setColumns(10);
		panel.add(txtBootloaderBaudRate, "wrap");

		panel.add(new JLabel(""));
		cbPeakSuppression = new JCheckBox(VNADriverSerialTiny2Messages.getString("lblPeakSuppression"));
		panel.add(cbPeakSuppression, "grow");

		cbReferenceChannel = new JCheckBox(VNADriverSerialTiny2Messages.getString("lblReferenceChannel"));
		panel.add(cbReferenceChannel, "grow, wrap");

		btCancel = SwingUtil.createJButton("Button.Cancel", e -> doDialogCancel());
		btCal = SwingUtil.createJButton("Button.AutoCalibrate", e -> doCalibrate());
		btDefault = SwingUtil.createJButton("Button.Default", e -> doReset());
		btOK = SwingUtil.createJButton("Button.OK", e -> doOK());

		panel.add(btCal, "wmin 100px");
		panel.add(btDefault, "wmin 100px, wrap");
		panel.add(new HelpButton(this, "VNADriverSerialTinyDialog"), "wmin 100px");
		panel.add(btCancel, "wmin 100px");
		panel.add(btOK, "wmin 100px,wrap");
		//
		lblStatusbar = new StatusBarLabel("Ready", 1000);
		lblStatusbar.setBackground(Color.GREEN);
		panel.add(lblStatusbar, "span 3,grow,wrap");

		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNADriverSerialTiny2Dialog");
	}

	protected void doCalibrate() {
		// disable all buttons
		btCancel.setEnabled(false);
		btDefault.setEnabled(false);
		btOK.setEnabled(false);
		btCal.setEnabled(false);

		// setup cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		// setup cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		// reenable buttons
		btCancel.setEnabled(true);
		btDefault.setEnabled(true);
		btCal.setEnabled(true);
		btOK.setEnabled(true);
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		updateFieldsFromDIB();

		txtTemperature.setText(VNAFormatFactory.getResistanceBaseFormat().format(driver.getDeviceTemperature()));
		txtFirmware.setText(driver.getDeviceFirmwareInfo());
		txtPower.setText(VNAFormatFactory.getResistanceBaseFormat().format(driver.getDeviceSupply()));

		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");
		ValidationResults results = new ValidationResults();

		int steps = IntegerValidator.parse(txtSteps.getText(), 200, 25000, VNADriverSerialTiny2Messages.getString("lblNoOfSteps"), results);
		long minFreq = LongValidator.parse(txtFreqMin.getText(), 1, 999999999999l, VNADriverSerialTiny2Messages.getString("lblFreq"), results);
		long maxFreq = LongValidator.parse(txtFreqMax.getText(), 1, 999999999999l, VNADriverSerialTiny2Messages.getString("lblFreq"), results);

		double minLoss = DoubleValidator.parse(txtLossMin.getText(), -200.0, 200.0, VNADriverSerialTiny2Messages.getString("lblLoss"), results);
		double maxLoss = DoubleValidator.parse(txtLossMax.getText(), -200.0, 200.0, VNADriverSerialTiny2Messages.getString("lblLoss"), results);

		double phaseCorrection = DoubleValidator.parse(txtPhaseCorrection.getText(), VNADriverSerialTiny2DIB.MIN_CORR_PHASE, VNADriverSerialTiny2DIB.MAX_CORR_PHASE, VNADriverSerialTiny2Messages.getString("lblPhaseCorr"), results);
		double gainCorrection = DoubleValidator.parse(txtGainCorrection.getText(), VNADriverSerialTiny2DIB.MIN_CORR_GAIN, VNADriverSerialTiny2DIB.MAX_CORR_GAIN, VNADriverSerialTiny2Messages.getString("lblGainCorr"), results);
		double tempCorrection = DoubleValidator.parse(txtTempCorrection.getText(), VNADriverSerialTiny2DIB.MIN_CORR_TEMP, VNADriverSerialTiny2DIB.MAX_CORR_TEMP, VNADriverSerialTiny2Messages.getString("lblTempCorr"), results);
		int blBaud = IntegerValidator.parse(txtBootloaderBaudRate.getText(), VNADriverSerialTiny2DIB.MIN_BOOTBAUD, VNADriverSerialTiny2DIB.MAX_BOOTBAUD, VNADriverSerialTiny2Messages.getString("lblBootloaderBaudRate"), results);
		boolean peakSupp = cbPeakSuppression.isSelected();
		boolean refChannel = cbReferenceChannel.isSelected();

		if (results.isEmpty()) {
			steps = (steps / 100) * 100;
			dib.setNumberOfSamples4Calibration(steps);
			dib.setReferenceResistance(referenceValue.getComplexValue());
			dib.setMinFrequency(minFreq);
			dib.setMaxFrequency(maxFreq);
			dib.setPhaseCorrection(phaseCorrection);
			dib.setGainCorrection(gainCorrection);
			dib.setTempCorrection(tempCorrection);
			dib.setBootloaderBaudrate(blBaud);
			dib.setPeakSuppression(peakSupp);
			dib.setReferenceChannel(refChannel);

			dib.setMinLoss(minLoss);
			dib.setMaxLoss(maxLoss);

			// store to properties
			dib.store(config, driver.getDriverConfigPrefix());

			// recalculate
			// build the calibrated stuff
			IVNADriverMathHelper mathHelper = datapool.getDriver().getMathHelper();

			// create a new calibration block based on
			final VNACalibrationBlock mcb = datapool.getMainCalibrationBlock();
			final VNACalibrationKit kit = datapool.getCalibrationKit();
			final VNACalibrationContext calPoints = mathHelper.createCalibrationContextForCalibrationPoints(mcb, kit);
			mathHelper.createCalibrationPoints(calPoints, mcb);

			// clear the currently set calibration block
			datapool.clearResizedCalibrationBlock();

			// delete also the calibrated data
			datapool.clearCalibratedData();

			setVisible(false);
		} else {
			new ValidationResultsDialog(getOwner(), results, getTitle());
		}
		TraceHelper.exit(this, "doOK");

	}

	private void doReset() {
		TraceHelper.entry(this, "doReset");
		dib.reset();
		updateFieldsFromDIB();
		TraceHelper.exit(this, "doReset");
	}

	/**
	 * 
	 * @param pDIB
	 */
	private void updateFieldsFromDIB() {
		txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMaxFrequency()));
		txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMinFrequency()));

		txtLossMax.setText(VNAFormatFactory.getReflectionLossFormat().format(dib.getMaxLoss()));
		txtLossMin.setText(VNAFormatFactory.getReflectionLossFormat().format(dib.getMinLoss()));

		txtPhaseMax.setText(VNAFormatFactory.getPhaseFormat().format(dib.getMaxPhase()));
		txtPhaseMin.setText(VNAFormatFactory.getPhaseFormat().format(dib.getMinPhase()));

		txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(dib.getNumberOfSamples4Calibration()));

		txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(dib.getNumberOfSamples4Calibration()));
		referenceValue.setComplexValue(dib.getReferenceResistance());

		txtPhaseCorrection.setText(VNAFormatFactory.getGainFormat().format(dib.getPhaseCorrection()));
		txtGainCorrection.setText(VNAFormatFactory.getGainFormat().format(dib.getGainCorrection()));
		txtTempCorrection.setText(VNAFormatFactory.getGainFormat().format(dib.getTempCorrection()));

		txtBootloaderBaudRate.setText(VNAFormatFactory.getFrequencyFormat4Export().format(dib.getBootloaderBaudrate()));

		cbPeakSuppression.setSelected(dib.isPeakSuppression());
		cbReferenceChannel.setSelected(dib.hasReferenceChannel());
	}

}