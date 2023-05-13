package krause.vna.device.serial.pro2.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
import krause.vna.device.serial.pro2.VNADriverSerialPro2DIB;
import krause.vna.device.serial.pro2.VNADriverSerialPro2Messages;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.util.SwingUtil;
import net.miginfocom.swing.MigLayout;

public class VNADriverSerialPro2Dialog extends VNADriverDialog {
	private JButton btOK;
	private JPanel panel;
	private JTextField txtLossMin;
	private JTextField txtLossMax;
	private JTextField txtPhaseMin;
	private JLabel lblPhaseMax;
	private JTextField txtPhaseMax;
	private JTextField txtFreqMin;
	private JTextField txtFreqMax;
	private JTextField txtTicks;
	private JTextField txtFirmware;
	private IVNADriver driver;
	private JButton btnReset;
	private ComplexInputField referenceValue;
	private JLabel lblReference;
	private JTextField txtPower;
	private VNADriverSerialPro2DIB dib;
	private JComboBox<Integer> cbSampleRate;

	public VNADriverSerialPro2Dialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), pMainFrame);
		TraceHelper.entry(this, "VNADriverSerialProDialog");

		this.driver = pDriver;
		this.dib = (VNADriverSerialPro2DIB) this.driver.getDeviceInfoBlock();

		setTitle(VNADriverSerialPro2Messages.getString("Dialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setProperties(this.config);
		setConfigurationPrefix("VNADriverSerialPro2Dialog");
		setPreferredSize(new Dimension(422, 420));

		this.panel = new JPanel();
		getContentPane().add(this.panel, BorderLayout.CENTER);
		this.panel.setLayout(new MigLayout("", "[grow][][][]", ""));

		//
		this.panel.add(new JLabel(), "");
		this.lblPhaseMax = new JLabel(VNADriverSerialPro2Messages.getString("Dialog.lblPhaseMax.text"));
		this.panel.add(this.lblPhaseMax, "");
		JLabel lblLossMax = new JLabel(VNADriverSerialPro2Messages.getString("Dialog.lblLossMax.text"));
		this.panel.add(lblLossMax, "wrap");

		//
		this.panel.add(new JLabel(VNADriverSerialPro2Messages.getString("Dialog.lblLossMin.text")), "");
		this.txtLossMin = new JTextField();
		this.txtLossMin.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtLossMin.setColumns(10);
		this.txtLossMin.setEditable(false);
		this.panel.add(this.txtLossMin, "");
		this.txtLossMax = new JTextField();
		this.txtLossMax.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtLossMax.setColumns(10);
		this.txtLossMax.setEditable(false);
		this.panel.add(this.txtLossMax, "wrap");

		//
		this.panel.add(new JLabel(VNADriverSerialPro2Messages.getString("Dialog.lblPhaseMin.text")), "");
		this.txtPhaseMin = new JTextField();
		this.txtPhaseMin.setEditable(true);
		this.txtPhaseMin.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtPhaseMin.setColumns(10);
		this.txtPhaseMin.setEditable(false);
		this.panel.add(this.txtPhaseMin, "");

		this.txtPhaseMax = new JTextField();
		this.txtPhaseMax.setEditable(true);
		this.txtPhaseMax.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtPhaseMax.setColumns(10);
		this.txtPhaseMax.setEditable(false);
		this.panel.add(this.txtPhaseMax, "wrap");

		//
		this.panel.add(new JLabel(VNADriverSerialPro2Messages.getString("Dialog.lblFreqMin.text")), "");
		this.txtFreqMin = new JTextField();
		this.txtFreqMin.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtFreqMin.setColumns(10);
		this.txtFreqMin.setEditable(false);
		this.panel.add(this.txtFreqMin, "");

		this.txtFreqMax = new JTextField();
		this.txtFreqMax.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtFreqMax.setColumns(10);
		this.txtFreqMax.setEditable(false);
		this.panel.add(this.txtFreqMax, "wrap");

		//
		this.panel.add(new JLabel(VNADriverSerialPro2Messages.getString("Dialog.lblDDSTicks.text")), "");
		this.txtTicks = new JTextField(10);
		this.txtTicks.setHorizontalAlignment(SwingConstants.RIGHT);
		this.txtTicks.setEditable(false);
		this.panel.add(this.txtTicks, "wrap");

		//
		this.panel.add(new JLabel(VNADriverSerialPro2Messages.getString("Dialog.lblFirmware.text")), "");
		this.txtFirmware = new JTextField();
		this.txtFirmware.setEditable(false);
		this.panel.add(this.txtFirmware, "grow,span 3,wrap");

		//
		//
		this.panel.add(new JLabel(VNADriverSerialPro2Messages.getString("Dialog.lblPower.text")), "");
		this.txtPower = new JTextField();
		this.txtPower.setEditable(false);
		this.panel.add(this.txtPower, "grow,span 3,wrap");

		//
		this.lblReference = new JLabel(VNADriverSerialPro2Messages.getString("VNADriverSerialProDialog.lblReference.text")); //$NON-NLS-1$
		this.lblReference.setBounds(10, 330, 141, 30);
		this.panel.add(this.lblReference, "");
		this.referenceValue = new ComplexInputField(null);
		this.referenceValue.setMaximum(new Complex(5000, 5000));
		this.referenceValue.setMinimum(new Complex(-5000, -5000));
		FlowLayout flowLayout = (FlowLayout) this.referenceValue.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		this.panel.add(this.referenceValue, "grow,span 3,wrap");

		this.panel.add(new JLabel(VNADriverSerialPro2Messages.getString("lblSampleRate")), "");
		this.cbSampleRate = new JComboBox<Integer>(new Integer[] {
				0,
				1,
				2,
				3,
				4,
				5,
				6,
				7,
				8
		});
		this.cbSampleRate.setMaximumRowCount(4);
		this.panel.add(this.cbSampleRate, "wrap");

		//
		JPanel pnlButtons = new JPanel();
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		pnlButtons.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		pnlButtons.add(new HelpButton(this, "VNADriverSerialProDialog"));

		JButton btCancel = SwingUtil.createJButton("Button.Cancel", e -> doDialogCancel());

		this.btnReset = new JButton(VNADriverSerialPro2Messages.getString("VNADriverSerialProDialog.btnReset.text")); //$NON-NLS-1$
		this.btnReset.addActionListener(e -> doReset());
		pnlButtons.add(this.btnReset);
		pnlButtons.add(btCancel);

		this.btOK = SwingUtil.createJButton("Button.OK", e -> doOK());
		pnlButtons.add(this.btOK);
		//
		getRootPane().setDefaultButton(this.btOK);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNADriverSerialProDialog");
	}

	protected void doOK() {
		TraceHelper.entry(this, "doOK");
		ValidationResults results = new ValidationResults();

		int ticks = IntegerValidator.parse(this.txtTicks.getText(), 999999, 999999999, VNADriverSerialPro2Messages.getString("Dialog.lblDDSTicks.text"), results);
		double lossMin = DoubleValidator.parse(this.txtLossMin.getText(), -200.0, 200.0, VNADriverSerialPro2Messages.getString("Dialog.lblLossMin.text"), results);
		double lossMax = DoubleValidator.parse(this.txtLossMax.getText(), -200.0, 200.0, VNADriverSerialPro2Messages.getString("Dialog.lblLossMin.text"), results);
		long frqMin = LongValidator.parse(this.txtFreqMin.getText(), 1, 999999999999l, VNADriverSerialPro2Messages.getString("Dialog.lblFreqMin.text"), results);
		long frqMax = LongValidator.parse(this.txtFreqMax.getText(), 1, 999999999999l, VNADriverSerialPro2Messages.getString("Dialog.lblFreqMin.text"), results);

		if (results.isEmpty()) {
			this.dib.setDdsTicksPerMHz(ticks);
			this.dib.setReferenceResistance(this.referenceValue.getComplexValue());
			this.dib.setMinFrequency(frqMin);
			this.dib.setMaxFrequency(frqMax);
			this.dib.setMinLoss(lossMin);
			this.dib.setMaxLoss(lossMax);
			this.dib.setSampleRate(((Integer) this.cbSampleRate.getSelectedItem()).intValue());
			this.dib.store(this.config, this.driver.getDriverConfigPrefix());
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
		updateFieldsFromDIB(this.dib);

		this.txtFirmware.setText(this.driver.getDeviceFirmwareInfo());
		this.txtPower.setText(VNAFormatFactory.getResistanceBaseFormat().format(this.driver.getDeviceSupply()));

		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	/**
	 * 
	 */
	private void doReset() {
		TraceHelper.entry(this, "doReset");
		this.dib.reset();
		updateFieldsFromDIB(this.dib);
		TraceHelper.exit(this, "doReset");
	}

	/**
	 * 
	 * @param pDIB
	 */
	private void updateFieldsFromDIB(VNADriverSerialPro2DIB pDIB) {
		TraceHelper.entry(this, "updateFieldsFromDIB");
		this.txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMaxFrequency()));
		this.txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(pDIB.getMinFrequency()));

		this.txtLossMax.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getMaxLoss()));
		this.txtLossMin.setText(VNAFormatFactory.getReflectionLossFormat().format(pDIB.getMinLoss()));

		this.txtPhaseMax.setText(VNAFormatFactory.getPhaseFormat().format(pDIB.getMaxPhase()));
		this.txtPhaseMin.setText(VNAFormatFactory.getPhaseFormat().format(pDIB.getMinPhase()));

		this.txtTicks.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(pDIB.getDdsTicksPerMHz()));

		this.referenceValue.setComplexValue(pDIB.getReferenceResistance());

		this.cbSampleRate.setSelectedItem(Integer.valueOf(pDIB.getSampleRate()));

		TraceHelper.exit(this, "updateFieldsFromDIB");
	}
}