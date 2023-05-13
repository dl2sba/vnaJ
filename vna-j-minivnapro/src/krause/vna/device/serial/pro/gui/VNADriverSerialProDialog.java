package krause.vna.device.serial.pro.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.device.serial.pro.VNADriverSerialProMessages;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.util.SwingUtil;
import net.miginfocom.swing.MigLayout;

public class VNADriverSerialProDialog extends VNADriverDialog {
	private JButton btOK;
	private JPanel panel;
	private JTextField txtLossMin;
	private JTextField txtLossMax;
	private JTextField txtPhaseMin;
	private JTextField txtPhaseMax;
	private JTextField txtFreqMin;
	private JTextField txtFreqMax;
	private JTextField txtSteps;
	private JTextField txtTicks;
	private JTextField txtFirmware;
	private IVNADriver driver;
	private JTextField txtOpenTimeout;
	private JTextField txtCommandDelay;
	private JLabel lblCommandDelay;
	private JTextField txtReadTimeout;
	private JButton btnReset;
	private ComplexInputField referenceValue;
	private JLabel lblReference;
	private JTextField txtPower;
	private VNADriverSerialProDIB dib;
	private JCheckBox cbFirmware;
	private JCheckBox cb6dB;
	private JTextField txtAttenIOffset;
	private JTextField txtAttenQOffset;

	public VNADriverSerialProDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), pMainFrame);
		TraceHelper.entry(this, "VNADriverSerialProDialog");

		driver = pDriver;
		dib = (VNADriverSerialProDIB) driver.getDeviceInfoBlock();

		setTitle(VNADriverSerialProMessages.getString("Dialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setProperties(config);
		setConfigurationPrefix("VNADriverSerialProDialog");
		setPreferredSize(new Dimension(500, 550));

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[grow][][][]", ""));

		//
		panel.add(new JLabel(), "");
		panel.add(new JLabel(VNADriverSerialProMessages.getString("Dialog.lblPhaseMax.text")), "");
		panel.add(new JLabel(VNADriverSerialProMessages.getString("Dialog.lblLossMax.text")), "wrap");

		//
		panel.add(new JLabel(VNADriverSerialProMessages.getString("Dialog.lblLossMin.text")), "");
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
		panel.add(new JLabel(VNADriverSerialProMessages.getString("Dialog.lblPhaseMin.text")), "");
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
		JLabel lblFreqMin = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblFreqMin.text"));
		lblFreqMin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				txtFreqMin.setEditable(true);
				txtFreqMax.setEditable(true);
			}
		});
		panel.add(lblFreqMin, "");
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
		JLabel lblNoOfSteps = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblNoOfSteps.text"));
		panel.add(lblNoOfSteps, "");

		txtSteps = new JTextField();
		txtSteps.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSteps.setColumns(10);
		txtSteps.setEditable(false);
		panel.add(txtSteps, "wrap");

		//
		JLabel lblDDSTicks = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblDDSTicks.text"));
		panel.add(lblDDSTicks, "");
		txtTicks = new JTextField(10);
		txtTicks.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtTicks, "wrap");

		//
		JLabel lblFirmware = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblFirmware.text"));
		panel.add(lblFirmware, "");
		txtFirmware = new JTextField();
		txtFirmware.setEditable(false);
		panel.add(txtFirmware, "grow,span 3,wrap");

		//
		JLabel lbl = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblFirmwareRevision"));
		panel.add(lbl, "");
		cbFirmware = new JCheckBox(VNADriverSerialProMessages.getString("Dialog.cbFirmwareRevision"));
		cbFirmware.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cbFirmware.isSelected()) {
					cb6dB.setEnabled(true);
				} else {
					cb6dB.setEnabled(false);
					cb6dB.setSelected(false);
				}
			}
		});
		panel.add(cbFirmware, "grow,span 1");

		cb6dB = new JCheckBox(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.cbFixed6dB"));
		panel.add(cb6dB, "grow,span 1,wrap");
		//
		JLabel lblPower = new JLabel(VNADriverSerialProMessages.getString("Dialog.lblPower.text"));
		panel.add(lblPower, "");
		txtPower = new JTextField();
		txtPower.setEditable(false);
		panel.add(txtPower, "grow,span 3,wrap");

		//
		panel.add(new JLabel(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblOpenTimeout.text")), "");

		txtOpenTimeout = new JTextField();
		txtOpenTimeout.setText("0");
		txtOpenTimeout.setHorizontalAlignment(SwingConstants.RIGHT);
		txtOpenTimeout.setColumns(6);
		panel.add(txtOpenTimeout, "wrap");

		//
		lblCommandDelay = new JLabel(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblCommandDelay.text"));
		lblCommandDelay.setBounds(10, 270, 141, 18);
		panel.add(lblCommandDelay, "");
		txtCommandDelay = new JTextField();
		txtCommandDelay.setText("0");
		txtCommandDelay.setHorizontalAlignment(SwingConstants.RIGHT);
		txtCommandDelay.setColumns(6);
		panel.add(txtCommandDelay, "wrap");

		//
		panel.add(new JLabel(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblReadTimeout.text")));
		txtReadTimeout = new JTextField();
		txtReadTimeout.setText("0");
		txtReadTimeout.setHorizontalAlignment(SwingConstants.RIGHT);
		txtReadTimeout.setColumns(6);
		panel.add(txtReadTimeout, "wrap");

		//
		panel.add(new JLabel(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblAttenOffset.text")), "");
		txtAttenIOffset = new JTextField();
		txtAttenIOffset.setText("0");
		txtAttenIOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		txtAttenIOffset.setColumns(6);
		panel.add(txtAttenIOffset, "");
		txtAttenQOffset = new JTextField();
		txtAttenQOffset.setText("0");
		txtAttenQOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		txtAttenQOffset.setColumns(6);
		panel.add(txtAttenQOffset, "wrap");

		//
		lblReference = new JLabel(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblReference.text")); //$NON-NLS-1$
		lblReference.setBounds(10, 330, 141, 30);
		panel.add(lblReference, "");
		referenceValue = new ComplexInputField(null);
		referenceValue.setMaximum(new Complex(5000, 5000));
		referenceValue.setMinimum(new Complex(-5000, -5000));
		FlowLayout flowLayout = (FlowLayout) referenceValue.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(referenceValue, "grow,span 3,wrap");

		//
		JPanel pnlButtons = new JPanel();
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		pnlButtons.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		pnlButtons.add(new HelpButton(this, "VNADriverSerialProDialog"));

		JButton btCancel = SwingUtil.createJButton("Button.Cancel", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDialogCancel();
			}
		});

		btnReset = new JButton(VNADriverSerialProMessages.getString("VNADriverSerialProDialog.btnReset.text")); //$NON-NLS-1$
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doReset();
			}
		});
		pnlButtons.add(btnReset);
		pnlButtons.add(btCancel);

		btOK = SwingUtil.createJButton("Button.OK", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOK();
			}
		});
		pnlButtons.add(btOK);
		//
		getRootPane().setDefaultButton(btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNADriverSerialProDialog");
	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");
		ValidationResults results = new ValidationResults();

		int frq = IntegerValidator.parse(txtTicks.getText(), 999999, 999999999, VNADriverSerialProMessages.getString("Dialog.lblDDSTicks.text"), results);
		int openTimeout = IntegerValidator.parse(txtOpenTimeout.getText(), 500, 99000, VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblOpenTimeout.text"), results);
		int readTimeout = IntegerValidator.parse(txtReadTimeout.getText(), 500, 99000, VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblReadTimeout.text"), results);
		int commandDelay = IntegerValidator.parse(txtCommandDelay.getText(), 50, 99000, VNADriverSerialProMessages.getString("VNADriverSerialProDialog.lblCommandDelay.text"), results);
		int steps = IntegerValidator.parse(txtSteps.getText(), 200, 25000, VNADriverSerialProMessages.getString("Dialog.lblNoOfSteps.text"), results);
		int firmware = (cbFirmware.isSelected()) ? (VNADriverSerialProDIB.FIRMWARE_2_3) : (VNADriverSerialProDIB.FIRMWARE_ORG);
		double attenI = DoubleValidator.parse(txtAttenIOffset.getText(), -100.0, 100.0, VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblAttenuationOffset.text"), results);
		double attenQ = DoubleValidator.parse(txtAttenQOffset.getText(), -100.0, 100.0, VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblAttenuationOffset.text"), results);
		double lossMin = DoubleValidator.parse(txtLossMin.getText(), -200.0, 200.0, VNADriverSerialProMessages.getString("Dialog.lblLossMin.text"), results);
		double lossMax = DoubleValidator.parse(txtLossMax.getText(), -200.0, 200.0, VNADriverSerialProMessages.getString("Dialog.lblLossMin.text"), results);
		long frqMin = LongValidator.parse(txtFreqMin.getText(), 1, 999999999999l, VNADriverSerialProMessages.getString("Dialog.lblFreqMin.text"), results);
		long frqMax = LongValidator.parse(txtFreqMax.getText(), 1, 999999999999l, VNADriverSerialProMessages.getString("Dialog.lblFreqMin.text"), results);

		if (results.isEmpty()) {
			steps = (steps / 100) * 100;
			dib.setNumberOfSamples4Calibration(steps);
			dib.setDdsTicksPerMHz(frq);
			dib.setAfterCommandDelay(commandDelay);
			dib.setReadTimeout(readTimeout);
			dib.setOpenTimeout(openTimeout);
			dib.setReferenceResistance(referenceValue.getComplexValue());
			dib.setFirmwareVersion(firmware);
			dib.setFixed6dBOnThru(cb6dB.isSelected());
			dib.setAttenOffsetI(attenI);
			dib.setAttenOffsetQ(attenQ);
			dib.setMinFrequency(frqMin);
			dib.setMaxFrequency(frqMax);
			dib.setMinLoss(lossMin);
			dib.setMaxLoss(lossMax);

			dib.store(config, driver.getDriverConfigPrefix());
			setVisible(false);
		} else {
			new ValidationResultsDialog(getOwner(), results, getTitle());
		}
		TraceHelper.exit(this, "doOK");

	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		updateFieldsFromDIB(dib);

		txtFirmware.setText(driver.getDeviceFirmwareInfo());
		txtPower.setText(VNAFormatFactory.getResistanceBaseFormat().format(driver.getDeviceSupply()));

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

	/**
	 * 
	 * @param pDIB
	 */
	private void updateFieldsFromDIB(VNADriverSerialProDIB pDIB) {
		txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxFrequency()));
		txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinFrequency()));

		txtLossMax.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getMaxLoss()));
		txtLossMin.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getMinLoss()));

		txtPhaseMax.setText(VNAFormatFactory.getPhaseFormat().format(pDIB.getMaxPhase()));
		txtPhaseMin.setText(VNAFormatFactory.getPhaseFormat().format(pDIB.getMinPhase()));

		txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getNumberOfSamples4Calibration()));
		txtTicks.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getDdsTicksPerMHz()));

		txtOpenTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getOpenTimeout()));
		txtReadTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getReadTimeout()));
		txtCommandDelay.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getAfterCommandDelay()));
		txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getNumberOfSamples4Calibration()));
		referenceValue.setComplexValue(pDIB.getReferenceResistance());

		txtAttenIOffset.setText(VNAFormatFactory.getReflectionLossFormat().format(dib.getAttenOffsetI()));
		txtAttenQOffset.setText(VNAFormatFactory.getReflectionLossFormat().format(dib.getAttenOffsetQ()));

		cbFirmware.setSelected(pDIB.getFirmwareVersion() >= VNADriverSerialProDIB.FIRMWARE_2_3);

		if (cbFirmware.isSelected()) {
			cb6dB.setSelected(pDIB.isFixed6dBOnThru());
			cb6dB.setEnabled(true);
		} else {
			cb6dB.setSelected(false);
			cb6dB.setEnabled(false);
		}
	}
}