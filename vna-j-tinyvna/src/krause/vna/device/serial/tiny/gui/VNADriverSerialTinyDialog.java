package krause.vna.device.serial.tiny.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.math3.complex.Complex;

import krause.common.validation.DoubleValidator;
import krause.common.validation.IntegerValidator;
import krause.common.validation.LongValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNADriverMathHelper;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.tiny.VNADriverSerialTiny;
import krause.vna.device.serial.tiny.VNADriverSerialTinyDIB;
import krause.vna.device.serial.tiny.VNADriverSerialTinyMessages;
import krause.vna.device.serial.tiny.calibration.PhaseCalibrationHelper;
import krause.vna.gui.HelpButton;
import krause.vna.gui.StatusBarLabel;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.util.SwingUtil;
import net.miginfocom.swing.MigLayout;

public class VNADriverSerialTinyDialog extends VNADriverDialog {

	private final VNAConfig config = VNAConfig.getSingleton();
	private final VNADataPool datapool = VNADataPool.getSingleton();
	private final VNADriverSerialTinyDIB dib;
	private final VNADriverSerialTiny driver;

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
	private JTextField txtIFPhaseCorrection;
	private JTextField txtLossMax;
	private JTextField txtLossMin;
	private JTextField txtPhaseCorrection;
	private JTextField txtPhaseMax;
	private JTextField txtPhaseMin;
	private JTextField txtPower;
	private JTextField txtSteps;
	private JTextField txtTempCorrection;
	private JTextField txtTemperature;
	// private JComboBox<String> cbFiltermode;
	private JCheckBox cbPeakSuppression;

	public VNADriverSerialTinyDialog(final VNAMainFrame pMainFrame, final IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), pMainFrame);
		TraceHelper.entry(this, "VNADriverSerialTinyDialog");

		driver = (VNADriverSerialTiny) pDriver;
		dib = (VNADriverSerialTinyDIB) driver.getDeviceInfoBlock();

		setTitle(VNADriverSerialTinyMessages.getString("drvTitle"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setProperties(config);
		setConfigurationPrefix("VNADriverSerialTinyDialog");
		setPreferredSize(new Dimension(540, 540));

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[grow][][]", ""));

		//
		panel.add(new JLabel(), "");
		lblPhaseMax = new JLabel(VNADriverSerialTinyMessages.getString("lblMin"));
		panel.add(lblPhaseMax, "");
		JLabel lblLossMax = new JLabel(VNADriverSerialTinyMessages.getString("lblMax"));
		panel.add(lblLossMax, "wrap");

		//
		panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblLoss")), "");

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
		lblPhaseMin = new JLabel(VNADriverSerialTinyMessages.getString("lblPhase"));
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
		panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblFreq")), "");
		txtFreqMin = new JTextField();
		txtFreqMin.setEditable(false);
		txtFreqMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFreqMin.setColumns(10);
		panel.add(txtFreqMin, "");

		txtFreqMax = new JTextField();
		txtFreqMax.setEditable(false);
		txtFreqMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFreqMax.setColumns(10);
		panel.add(txtFreqMax, "wrap");

		//
		panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblNoOfSteps")), "");

		txtSteps = new JTextField();
		txtSteps.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSteps.setColumns(10);
		panel.add(txtSteps, "wrap");

		//
		panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblFirmware")), "");
		txtFirmware = new JTextField();
		txtFirmware.setEditable(false);
		panel.add(txtFirmware, "grow,span 3,wrap");

		//
		panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblPower")), "");
		txtPower = new JTextField();
		txtPower.setEditable(false);
		panel.add(txtPower, "grow,span 3,wrap");

		//
		panel.add(new JLabel(VNADriverSerialTinyMessages.getString("lblTemperature")), "");
		txtTemperature = new JTextField();
		txtTemperature.setEditable(false);
		panel.add(txtTemperature, "grow,span 3,wrap");

		//
		lblReference = new JLabel(VNADriverSerialTinyMessages.getString("lblReference")); //$NON-NLS-1$
		lblReference.setBounds(10, 330, 141, 30);
		panel.add(lblReference, "");
		referenceValue = new ComplexInputField(null);
		referenceValue.setMaximum(new Complex(5000, 5000));
		referenceValue.setMinimum(new Complex(-5000, -5000));
		FlowLayout flowLayout = (FlowLayout) referenceValue.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(referenceValue, "grow,span 3,wrap");

		panel.add(new JLabel(MessageFormat.format(VNADriverSerialTinyMessages.getString("lblPhaseCorr"), VNADriverSerialTinyDIB.MIN_CORR_PHASE, VNADriverSerialTinyDIB.MAX_CORR_PHASE)));
		txtPhaseCorrection = new JTextField();
		txtPhaseCorrection.setText("0");
		txtPhaseCorrection.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPhaseCorrection.setColumns(10);
		panel.add(txtPhaseCorrection, "wrap");

		panel.add(new JLabel(MessageFormat.format(VNADriverSerialTinyMessages.getString("lblIFPhaseCorr"), VNADriverSerialTinyDIB.MIN_CORR_IF_PHASE, VNADriverSerialTinyDIB.MAX_CORR_IF_PHASE)));
		txtIFPhaseCorrection = new JTextField();
		txtIFPhaseCorrection.setText("0");
		txtIFPhaseCorrection.setHorizontalAlignment(SwingConstants.RIGHT);
		txtIFPhaseCorrection.setColumns(10);
		panel.add(txtIFPhaseCorrection, "wrap");

		panel.add(new JLabel(MessageFormat.format(VNADriverSerialTinyMessages.getString("lblGainCorr"), VNADriverSerialTinyDIB.MIN_CORR_GAIN, VNADriverSerialTinyDIB.MAX_CORR_GAIN)));
		txtGainCorrection = new JTextField();
		txtGainCorrection.setText("0");
		txtGainCorrection.setHorizontalAlignment(SwingConstants.RIGHT);
		txtGainCorrection.setColumns(10);
		panel.add(txtGainCorrection, "wrap");

		panel.add(new JLabel(MessageFormat.format(VNADriverSerialTinyMessages.getString("lblTempCorr"), VNADriverSerialTinyDIB.MIN_CORR_TEMP, VNADriverSerialTinyDIB.MAX_CORR_TEMP)));
		txtTempCorrection = new JTextField();
		txtTempCorrection.setText("0");
		txtTempCorrection.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTempCorrection.setColumns(10);
		panel.add(txtTempCorrection, "wrap");

		panel.add(new JLabel(MessageFormat.format(VNADriverSerialTinyMessages.getString("lblBootloaderBaudRate"), VNADriverSerialTinyDIB.MIN_BOOTBAUD, VNADriverSerialTinyDIB.MAX_BOOTBAUD)));
		txtBootloaderBaudRate = new JTextField();
		txtBootloaderBaudRate.setText("0");
		txtBootloaderBaudRate.setHorizontalAlignment(SwingConstants.RIGHT);
		txtBootloaderBaudRate.setColumns(10);
		panel.add(txtBootloaderBaudRate, "wrap");

		// panel.add(new
		// JLabel(VNADriverSerialTinyMessages.getString("lblFiltermode")));
		// cbFiltermode = new JComboBox<String>(new String[] {
		// VNADriverSerialTinyMessages.getString("filterMode0"),
		// VNADriverSerialTinyMessages.getString("filterMode1"),
		// VNADriverSerialTinyMessages.getString("filterMode2"),
		// });
		// panel.add(cbFiltermode, "grow, span2, wrap");

		panel.add(new JLabel(""));
		cbPeakSuppression = new JCheckBox(VNADriverSerialTinyMessages.getString("lblPeakSuppression"));
		panel.add(cbPeakSuppression, "grow, span2, wrap");

		btCancel = SwingUtil.createJButton("Button.Cancel", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDialogCancel();
			}
		});

		btCal = SwingUtil.createJButton("Button.AutoCalibrate", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doCalibrate();
			}
		});

		btDefault = SwingUtil.createJButton("Button.Default", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doReset();
			}
		});

		btOK = SwingUtil.createJButton("Button.OK", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOK();
			}
		});

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
		TraceHelper.exit(this, "VNADriverSerialTinyDialog");
	}

	protected void doCalibrate() {
		// disable all buttons
		btCancel.setEnabled(false);
		btDefault.setEnabled(false);
		btOK.setEnabled(false);
		btCal.setEnabled(false);

		// setup cursor
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		new PhaseCalibrationHelper(this, dib).doCalibrate();

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

		int steps = IntegerValidator.parse(txtSteps.getText(), 200, 25000, VNADriverSerialTinyMessages.getString("lblNoOfSteps"), results);
		long minFreq = LongValidator.parse(txtFreqMin.getText(), 1, 999999999999l, VNADriverSerialTinyMessages.getString("lblFreq"), results);
		long maxFreq = LongValidator.parse(txtFreqMax.getText(), 1, 999999999999l, VNADriverSerialTinyMessages.getString("lblFreq"), results);

		double minLoss = DoubleValidator.parse(txtLossMin.getText(), -200.0, 200.0, VNADriverSerialTinyMessages.getString("lblLoss"), results);
		double maxLoss = DoubleValidator.parse(txtLossMax.getText(), -200.0, 200.0, VNADriverSerialTinyMessages.getString("lblLoss"), results);

		double phaseCorrection = DoubleValidator.parse(txtPhaseCorrection.getText(), VNADriverSerialTinyDIB.MIN_CORR_PHASE, VNADriverSerialTinyDIB.MAX_CORR_PHASE, VNADriverSerialTinyMessages.getString("lblPhaseCorr"), results);
		double ifPhaseCorrection = DoubleValidator.parse(txtIFPhaseCorrection.getText(), VNADriverSerialTinyDIB.MIN_CORR_IF_PHASE, VNADriverSerialTinyDIB.MAX_CORR_IF_PHASE, VNADriverSerialTinyMessages.getString("lblIFPhaseCorr"), results);
		double gainCorrection = DoubleValidator.parse(txtGainCorrection.getText(), VNADriverSerialTinyDIB.MIN_CORR_GAIN, VNADriverSerialTinyDIB.MAX_CORR_GAIN, VNADriverSerialTinyMessages.getString("lblGainCorr"), results);
		double tempCorrection = DoubleValidator.parse(txtTempCorrection.getText(), VNADriverSerialTinyDIB.MIN_CORR_TEMP, VNADriverSerialTinyDIB.MAX_CORR_TEMP, VNADriverSerialTinyMessages.getString("lblTempCorr"), results);
		int blBaud = IntegerValidator.parse(txtBootloaderBaudRate.getText(), VNADriverSerialTinyDIB.MIN_BOOTBAUD, VNADriverSerialTinyDIB.MAX_BOOTBAUD, VNADriverSerialTinyMessages.getString("lblBootloaderBaudRate"), results);
		// int filterMode = cbFiltermode.getSelectedIndex();
		boolean peakSupp = cbPeakSuppression.isSelected();

		if (results.isEmpty()) {
			steps = (steps / 100) * 100;
			dib.setNumberOfSamples4Calibration(steps);
			dib.setReferenceResistance(referenceValue.getComplexValue());
			dib.setMinFrequency(minFreq);
			dib.setMaxFrequency(maxFreq);
			dib.setPhaseCorrection(phaseCorrection);
			dib.setIfPhaseCorrection(ifPhaseCorrection);
			dib.setGainCorrection(gainCorrection);
			dib.setTempCorrection(tempCorrection);
			dib.setBootloaderBaudrate(blBaud);
			dib.setPeakSuppression(peakSupp);
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

			mathHelper.createCalibrationPoints(mathHelper.createCalibrationContextForCalibrationPoints(mcb, kit), mcb);

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

		referenceValue.setComplexValue(dib.getReferenceResistance());

		txtPhaseCorrection.setText(VNAFormatFactory.getGainFormat().format(dib.getPhaseCorrection()));
		txtIFPhaseCorrection.setText(VNAFormatFactory.getGainFormat().format(dib.getIfPhaseCorrection()));
		txtGainCorrection.setText(VNAFormatFactory.getGainFormat().format(dib.getGainCorrection()));
		txtTempCorrection.setText(VNAFormatFactory.getGainFormat().format(dib.getTempCorrection()));

		txtBootloaderBaudRate.setText(VNAFormatFactory.getFrequencyFormat4Export().format(dib.getBootloaderBaudrate()));

		// cbFiltermode.setSelectedIndex(dib.getFilterMode());
		cbPeakSuppression.setSelected(dib.isPeakSuppression());
	}

}