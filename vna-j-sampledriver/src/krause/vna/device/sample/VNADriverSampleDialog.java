package krause.vna.device.sample;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverDialog;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;


public class VNADriverSampleDialog extends VNADriverDialog {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("krause.vna.device.sample.driver");

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
	private IVNADriver driver;

	public VNADriverSampleDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), pMainFrame);
		TraceHelper.entry(this, "VNADriverSampleDialog");

		driver = pDriver;
		setTitle(BUNDLE.getString("Dialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 420, 237);

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JLabel lblLossMin = new JLabel(BUNDLE.getString("Dialog.lblLossMin.text"));
		lblLossMin.setBounds(9, 40, 151, 18);
		panel.add(lblLossMin);

		txtLossMin = new JTextField();
		txtLossMin.setEditable(false);
		txtLossMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLossMin.setBounds(170, 40, 86, 20);
		panel.add(txtLossMin);
		txtLossMin.setColumns(10);

		JLabel lblMin = new JLabel(BUNDLE.getString("Dialog.lblLossMax.text"));
		lblMin.setBounds(170, 12, 90, 18);
		panel.add(lblMin);

		txtLossMax = new JTextField();
		txtLossMax.setEditable(false);
		txtLossMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLossMax.setBounds(280, 40, 86, 20);
		panel.add(txtLossMax);
		txtLossMax.setColumns(10);

		lblPhaseMin = new JLabel(BUNDLE.getString("Dialog.lblPhaseMin.text"));
		lblPhaseMin.setBounds(10, 70, 151, 18);
		panel.add(lblPhaseMin);

		txtPhaseMin = new JTextField();
		txtPhaseMin.setEditable(false);
		txtPhaseMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPhaseMin.setBounds(170, 70, 86, 20);
		panel.add(txtPhaseMin);
		txtPhaseMin.setColumns(10);

		lblMax = new JLabel(BUNDLE.getString("Dialog.lblPhaseMax.text"));
		lblMax.setBounds(279, 10, 90, 20);
		panel.add(lblMax);

		txtPhaseMax = new JTextField();
		txtPhaseMax.setEditable(false);
		txtPhaseMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPhaseMax.setBounds(280, 70, 86, 20);
		panel.add(txtPhaseMax);
		txtPhaseMax.setColumns(10);

		JLabel lblFreqMin = new JLabel(BUNDLE.getString("Dialog.lblFreqMin.text"));
		lblFreqMin.setBounds(9, 100, 151, 18);
		panel.add(lblFreqMin);

		txtFreqMin = new JTextField();
		txtFreqMin.setEditable(false);
		txtFreqMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFreqMin.setBounds(170, 100, 86, 20);
		panel.add(txtFreqMin);
		txtFreqMin.setColumns(10);

		txtFreqMax = new JTextField();
		txtFreqMax.setEditable(false);
		txtFreqMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFreqMax.setBounds(280, 100, 120, 20);
		panel.add(txtFreqMax);
		txtFreqMax.setColumns(12);

		JLabel lblNoOfSteps = new JLabel(BUNDLE.getString("Dialog.lblNoOfSteps.text"));
		lblNoOfSteps.setBounds(9, 132, 151, 18);
		panel.add(lblNoOfSteps);

		txtSteps = new JTextField();
		txtSteps.setEditable(false);
		txtSteps.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSteps.setBounds(170, 130, 86, 20);
		panel.add(txtSteps);
		txtSteps.setColumns(10);

		JPanel pnlButtons = new JPanel();
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		pnlButtons.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlButtons.setLayout(new BorderLayout(0, 0));

		JButton btCancel = new JButton(BUNDLE.getString("Button.Cancel"));
		btCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDialogCancel();
			}
		});
		pnlButtons.add(btCancel, BorderLayout.WEST);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNADriverSampleDialog");
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		VNADeviceInfoBlock dib = driver.getDeviceInfoBlock();

		txtFreqMax.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMaxFrequency()));
		txtFreqMin.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMinFrequency()));

		txtLossMax.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMaxLoss()));
		txtLossMin.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMinLoss()));

		txtPhaseMax.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMaxPhase()));
		txtPhaseMin.setText(VNAFormatFactory.getFrequencyFormat().format(dib.getMinPhase()));

		txtSteps.setText(VNAFormatFactory.getFrequencyCalibrationFormat().format(dib.getNumberOfSamples4Calibration()));

		addEscapeKey();
		showCentered(getWidth(), getHeight());
		TraceHelper.exit(this, "doInit");
	}
}
