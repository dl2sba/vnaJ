package krause.vna.device.serial.max6.gui;

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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.complex.Complex;

import krause.common.validation.DoubleValidator;
import krause.common.validation.IntegerValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAScanMode;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.max6.VNADriverSerialMax6DIB;
import krause.vna.device.serial.max6.VNADriverSerialMax6Messages;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNADriverSerialMax6Dialog extends VNADriverDialog {
	private VNADriverSerialMax6DIB dib;

	private JButton btOK;
	private JPanel panel;
	private JLabel lblPhaseMin;
	private JLabel lblMax;
	private JLabel lblOpenTimeout;
	private JLabel lblReadTimeout;
	private JLabel lblCommandDelay;
	private JLabel lblBaudrate;
	private JLabel lblReference;
	private JTextField txtLossMin;
	private JTextField txtLossMax;
	private JTextField txtLevelMin;
	private JTextField txtLevelMax;
	private JTextField txtPhaseMin;
	private JTextField txtPhaseMax;
	private JTextField txtFreqMin;
	private JTextField txtFreqMax;
	private JTextField txtSteps;
	private JTextField txtTicks;
	private IVNADriver driver;
	private JTextField txtOpenTimeout;
	private JTextField txtReadTimeout;
	private JTextField txtCommandDelay;
	private JTextField txtBaudrate;
	private ComplexInputField referenceValue;
	private JTextField txtRss1Scale;
	private JTextField txtRss2Scale;
	private JTextField txtRss3Scale;
	private JTextField txtReflectionOffset;
	private JTextField txtReflectionScale;
	private JTextField txtRss1Offset;
	private JTextField txtRss2Offset;
	private JTextField txtRss3Offset;
	private JTextArea txtFirmware;

	private JLabel lblFirmware;
	private JButton button_1;
	private JButton button_2;

	public VNADriverSerialMax6Dialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), pMainFrame);
		TraceHelper.entry(this, "VNADriverSerialMax6Dialog");
		setProperties(VNAConfig.getSingleton());
		setConfigurationPrefix("VNADriverSerialMax6Dialog");

		driver = pDriver;
		dib = (VNADriverSerialMax6DIB) driver.getDeviceInfoBlock();

		setTitle(VNADriverSerialMax6Messages.getString("Dialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(500, 500));
		setPreferredSize(new Dimension(650, 550));

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[grow][][][][]", "[]"));

		//
		panel.add(new JLabel(), "cell 0 0");

		//
		JLabel lblMin = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblLossMax.text"));
		panel.add(lblMin, "cell 1 0");

		//
		lblMax = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblPhaseMax.text"));
		panel.add(lblMax, "cell 2 0");

		//
		JLabel lblLoss = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblLossMin.text"));
		panel.add(lblLoss, "cell 0 1");
		txtLossMin = new JTextField();
		txtLossMin.setEditable(false);
		txtLossMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLossMin.setColumns(10);
		panel.add(txtLossMin, "cell 1 1");
		txtLossMax = new JTextField();
		txtLossMax.setEditable(false);
		txtLossMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLossMax.setColumns(10);
		panel.add(txtLossMax, "cell 2 1");

		//
		JLabel lblLevel = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblLevel.text"));
		panel.add(lblLevel, "cell 0 2");
		txtLevelMax = new JTextField();
		txtLevelMax.setEditable(false);
		txtLevelMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLevelMax.setColumns(10);
		panel.add(txtLevelMax, "cell 1 2");
		txtLevelMin = new JTextField();
		txtLevelMin.setEditable(false);
		txtLevelMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLevelMin.setColumns(10);
		panel.add(txtLevelMin, "cell 2 2");

		lblPhaseMin = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblPhaseMin.text"));
		panel.add(lblPhaseMin, "cell 0 3");

		txtPhaseMin = new JTextField();
		txtPhaseMin.setEditable(false);
		txtPhaseMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPhaseMin.setColumns(10);
		panel.add(txtPhaseMin, "cell 1 3");

		txtPhaseMax = new JTextField();
		txtPhaseMax.setEditable(false);
		txtPhaseMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPhaseMax.setColumns(10);
		panel.add(txtPhaseMax, "cell 2 3");

		JLabel lblFreqMin = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblFreqMin.text"));
		panel.add(lblFreqMin, "cell 0 4");

		txtFreqMin = new JTextField();
		txtFreqMin.setEditable(false);
		txtFreqMin.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtFreqMin, "cell 1 4");
		txtFreqMin.setColumns(10);

		txtFreqMax = new JTextField();
		txtFreqMax.setEditable(false);
		txtFreqMax.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtFreqMax, "cell 2 4");
		txtFreqMax.setColumns(10);

		JLabel lblNoOfSteps = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblNoOfSteps.text"));
		panel.add(lblNoOfSteps, "cell 0 5");

		txtSteps = new JTextField();
		txtSteps.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSteps.setColumns(10);
		panel.add(txtSteps, "cell 1 5");

		//
		JLabel lblDDSTicks = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblDDSTicks.text"));
		panel.add(lblDDSTicks, "cell 0 6");

		txtTicks = new JTextField();
		txtTicks.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTicks.setColumns(10);
		panel.add(txtTicks, "cell 1 6");

		//
		panel.add(new JLabel(" "), "cell 0 7");

		//
		panel.add(new JLabel(), "cell 0 8");
		panel.add(new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblReflection")), "cell 1 8");
		JLabel label = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblRss1"));
		panel.add(label, "cell 2 8");
		JLabel label_1 = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblRss2"));
		panel.add(label_1, "cell 3 8");
		JLabel label_2 = new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblRss3"));
		panel.add(label_2, "cell 4 8");

		//
		panel.add(new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblScale")), "cell 0 9");
		txtReflectionScale = new JTextField();
		txtReflectionScale.setHorizontalAlignment(SwingConstants.RIGHT);
		txtReflectionScale.setColumns(10);
		panel.add(txtReflectionScale, "cell 1 9");
		txtRss1Scale = new JTextField();
		txtRss1Scale.setHorizontalAlignment(SwingConstants.RIGHT);
		txtRss1Scale.setColumns(10);
		panel.add(txtRss1Scale, "cell 2 9");
		txtRss2Scale = new JTextField();
		txtRss2Scale.setEnabled(false);
		txtRss2Scale.setHorizontalAlignment(SwingConstants.RIGHT);
		txtRss2Scale.setColumns(10);
		panel.add(txtRss2Scale, "cell 3 9");
		txtRss3Scale = new JTextField();
		txtRss3Scale.setEnabled(false);
		txtRss3Scale.setHorizontalAlignment(SwingConstants.RIGHT);
		txtRss3Scale.setColumns(10);
		panel.add(txtRss3Scale, "cell 4 9");

		//
		panel.add(new JLabel(VNADriverSerialMax6Messages.getString("Dialog.lblOffset")), "cell 0 10");
		txtReflectionOffset = new JTextField();
		txtReflectionOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		txtReflectionOffset.setColumns(10);
		panel.add(txtReflectionOffset, "cell 1 10");
		txtRss1Offset = new JTextField();
		txtRss1Offset.setHorizontalAlignment(SwingConstants.RIGHT);
		txtRss1Offset.setColumns(10);
		panel.add(txtRss1Offset, "cell 2 10");
		txtRss2Offset = new JTextField();
		txtRss2Offset.setEnabled(false);
		txtRss2Offset.setHorizontalAlignment(SwingConstants.RIGHT);
		txtRss2Offset.setColumns(10);
		panel.add(txtRss2Offset, "cell 3 10");
		txtRss3Offset = new JTextField();
		txtRss3Offset.setEnabled(false);
		txtRss3Offset.setHorizontalAlignment(SwingConstants.RIGHT);
		txtRss3Offset.setColumns(10);
		panel.add(txtRss3Offset, "cell 4 10");

		//
		panel.add(new JLabel(" "), "cell 0 11");

		JButton button;

		button = new JButton(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.btCalRSS1.text")); //$NON-NLS-1$
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VNATLCalibrationDialog(mainFrame, driver, VNAScanMode.MODE_RSS1);
				updateFieldsFromDIB(dib);
			}
		});
		panel.add(button, "cell 2 11,grow");

		button_1 = new JButton(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.btCalRSS1.text")); //$NON-NLS-1$
		button_1.setEnabled(false);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VNATLCalibrationDialog(mainFrame, driver, VNAScanMode.MODE_RSS2);
				updateFieldsFromDIB(dib);
			}
		});
		panel.add(button_1, "cell 3 11,grow");

		button_2 = new JButton(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.btCalRSS1.text")); //$NON-NLS-1$
		button_2.setEnabled(false);
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new VNATLCalibrationDialog(mainFrame, driver, VNAScanMode.MODE_RSS3);
				updateFieldsFromDIB(dib);
			}
		});
		panel.add(button_2, "cell 4 11,grow");

		//
		lblOpenTimeout = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblOpenTimeout.text")); //$NON-NLS-1$
		panel.add(lblOpenTimeout, "cell 0 12");
		txtOpenTimeout = new JTextField();
		txtOpenTimeout.setText("0");
		txtOpenTimeout.setHorizontalAlignment(SwingConstants.RIGHT);
		txtOpenTimeout.setColumns(10);
		panel.add(txtOpenTimeout, "cell 1 12");

		lblReadTimeout = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblReadTimeout.text")); //$NON-NLS-1$
		panel.add(lblReadTimeout, "cell 0 13");

		txtReadTimeout = new JTextField();
		txtReadTimeout.setText("0");
		txtReadTimeout.setHorizontalAlignment(SwingConstants.RIGHT);
		txtReadTimeout.setColumns(10);
		panel.add(txtReadTimeout, "cell 1 13");

		lblCommandDelay = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblCommandDelay.text")); //$NON-NLS-1$
		panel.add(lblCommandDelay, "cell 0 14");

		txtCommandDelay = new JTextField();
		txtCommandDelay.setText("0");
		txtCommandDelay.setHorizontalAlignment(SwingConstants.RIGHT);
		txtCommandDelay.setColumns(10);
		panel.add(txtCommandDelay, "cell 1 14");

		//
		lblBaudrate = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblBaudrate.text")); //$NON-NLS-1$
		panel.add(lblBaudrate, "cell 0 15");

		txtBaudrate = new JTextField();
		txtBaudrate.setText("0");
		txtBaudrate.setHorizontalAlignment(SwingConstants.RIGHT);
		txtBaudrate.setColumns(10);
		panel.add(txtBaudrate, "cell 1 15");

		//
		lblReference = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblReference.text")); //$NON-NLS-1$
		panel.add(lblReference, "cell 0 16");
		referenceValue = new ComplexInputField(null);
		referenceValue.setMaximum(new Complex(5000, 5000));
		referenceValue.setMinimum(new Complex(-5000, -5000));
		FlowLayout flowLayout = (FlowLayout) referenceValue.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(referenceValue, "cell 1 16 5 1");

		//
		lblFirmware = new JLabel(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblFirmware.text")); //$NON-NLS-1$
		panel.add(lblFirmware, "cell 0 17");
		txtFirmware = new JTextArea();
		txtFirmware.setEditable(false);
		txtFirmware.setRows(5);
		panel.add(txtFirmware, "cell 1 17 5 1,grow");

		//
		JPanel pnlButtons = new JPanel();
		pnlButtons.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		pnlButtons.add(new HelpButton(this, "VNADriverSerialMax6Dialog"));

		JButton btnReset = new JButton(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.btnReset.text"));
		pnlButtons.add(btnReset);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doReset();
			}
		});
		btnReset.setToolTipText(VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.btnReset.toolTipText"));

		btOK = new JButton(VNADriverSerialMax6Messages.getString("Button.OK"));
		btOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doOK();
			}
		});

		JButton btCancel = new JButton(VNADriverSerialMax6Messages.getString("Button.Cancel"));
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
		TraceHelper.exit(this, "VNADriverSerialMax6Dialog");
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		//
		updateFieldsFromDIB(dib);
		txtFirmware.setText(driver.getDeviceFirmwareInfo());
		//
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");
		ValidationResults results = new ValidationResults();
		int frq = IntegerValidator.parse(txtTicks.getText(), 999999, 999999999, VNADriverSerialMax6Messages.getString("Dialog.lblDDSTicks.text"), results);
		int openTimeout = IntegerValidator.parse(txtOpenTimeout.getText(), 500, 99000, VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblOpenTimeout.text"), results);
		int readTimeout = IntegerValidator.parse(txtReadTimeout.getText(), 500, 99000, VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblReadTimeout.text"), results);
		int commandDelay = IntegerValidator.parse(txtCommandDelay.getText(), 50, 99000, VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblCommandDelay.text"), results);
		int baudrate = IntegerValidator.parse(txtBaudrate.getText(), 1200, 115200, VNADriverSerialMax6Messages.getString("VNADriverSerialMax6Dialog.lblBaudrate.text"), results);
		int steps = IntegerValidator.parse(txtSteps.getText(), 2000, 25000, VNADriverSerialMax6Messages.getString("Dialog.lblNoOfSteps.text"), results);

		double rss1Scale = DoubleValidator.parse(txtRss1Scale.getText(), 0.0001, 1.0, VNADriverSerialMax6Messages.getString("Dialog.lblScale"), results);
		double rss1Offset = DoubleValidator.parse(txtRss1Offset.getText(), -10.0, 100.0, VNADriverSerialMax6Messages.getString("Dialog.lblOffset"), results);
		double rss2Scale = DoubleValidator.parse(txtRss2Scale.getText(), 0.0001, 1.0, VNADriverSerialMax6Messages.getString("Dialog.lblScale"), results);
		double rss2Offset = DoubleValidator.parse(txtRss2Offset.getText(), -10.0, 100.0, VNADriverSerialMax6Messages.getString("Dialog.lblOffset"), results);
		double rss3Scale = DoubleValidator.parse(txtRss3Scale.getText(), 0.0001, 1.0, VNADriverSerialMax6Messages.getString("Dialog.lblScale"), results);
		double rss3Offset = DoubleValidator.parse(txtRss3Offset.getText(), -10.0, 100.0, VNADriverSerialMax6Messages.getString("Dialog.lblOffset"), results);

		double reflectionScale = DoubleValidator.parse(txtReflectionScale.getText(), 0.0001, 1.0, VNADriverSerialMax6Messages.getString("Dialog.lblScale"), results);
		double reflectionOffset = DoubleValidator.parse(txtReflectionOffset.getText(), -100.0, 100.0, VNADriverSerialMax6Messages.getString("Dialog.lblOffset"), results);

		if (results.isEmpty()) {
			dib.setDdsTicksPerMHz(frq);
			dib.setAfterCommandDelay(commandDelay);
			dib.setReadTimeout(readTimeout);
			dib.setOpenTimeout(openTimeout);
			dib.setBaudrate(baudrate);
			dib.setNumberOfSamples4Calibration(steps);
			dib.setRss1Scale(rss1Scale);
			dib.setRss1Offset(rss1Offset);
			dib.setRss2Scale(rss2Scale);
			dib.setRss2Offset(rss2Offset);
			dib.setRss3Scale(rss3Scale);
			dib.setRss3Offset(rss3Offset);
			dib.setReflectionScale(reflectionScale);
			dib.setReflectionOffset(reflectionOffset);
			// store it
			dib.store(config, driver.getDriverConfigPrefix());
			setVisible(false);
			dispose();
		} else {
			new ValidationResultsDialog(getOwner(), results, VNAMessages.getString("VNANetworkDialog.ErrorDialogHeader"));
		}
		TraceHelper.exit(this, "doOK");

	}

	private void doReset() {
		TraceHelper.entry(this, "doReset");
		dib.reset();
		updateFieldsFromDIB(dib);
		TraceHelper.exit(this, "doReset");
	}

	private void updateFieldsFromDIB(VNADriverSerialMax6DIB pDIB) {
		txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxFrequency()));
		txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinFrequency()));

		txtLossMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxLoss()));
		txtLossMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinLoss()));

		txtLevelMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getLevelMax()));
		txtLevelMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getLevelMin()));

		txtPhaseMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxPhase()));
		txtPhaseMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinPhase()));

		txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getNumberOfSamples4Calibration()));
		txtTicks.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getDdsTicksPerMHz()));

		txtOpenTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getOpenTimeout()));
		txtReadTimeout.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getReadTimeout()));
		txtCommandDelay.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getAfterCommandDelay()));
		txtBaudrate.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getBaudrate()));
		txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getNumberOfSamples4Calibration()));

		txtReflectionScale.setText(VNAFormatFactory.getTransmissionScaleFormat().format(pDIB.getReflectionScale()));
		txtReflectionOffset.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getReflectionOffset()));
		txtRss1Scale.setText(VNAFormatFactory.getTransmissionScaleFormat().format(pDIB.getRss1Scale()));
		txtRss1Offset.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getRss1Offset()));
		txtRss2Scale.setText(VNAFormatFactory.getTransmissionScaleFormat().format(pDIB.getRss2Scale()));
		txtRss2Offset.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getRss2Offset()));
		txtRss3Scale.setText(VNAFormatFactory.getTransmissionScaleFormat().format(pDIB.getRss3Scale()));
		txtRss3Offset.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getRss3Offset()));

		referenceValue.setComplexValue(pDIB.getReferenceResistance());
	}
}
