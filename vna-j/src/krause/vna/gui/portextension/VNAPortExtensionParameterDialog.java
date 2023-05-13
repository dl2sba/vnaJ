package krause.vna.gui.portextension;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAPortExtensionParameterDialog extends KrauseDialog {

	private JButton btCancel;
	private JButton btOK;
	private JTextField txtLength;
	private JTextField txtVf;
	private VNAConfig config = VNAConfig.getSingleton();
	private JPanel pnlButtons;
	private JCheckBox cbEnabled;

	/**
	 * 
	 * @param pMainFrame
	 */
	public VNAPortExtensionParameterDialog(VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);
		TraceHelper.entry(this, "VNAPortExtensionParameterDialog");

		setConfigurationPrefix("VNAPortExtensionParameterDialog");
		setProperties(config);

		setTitle(VNAMessages.getString("VNAPortExtensionParameterDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(350, 160));
		getContentPane().setLayout(new MigLayout("", "[][grow,fill]", "[top, grow,fill][][]"));

		// ***********************************************************************
		add(new JLabel(VNAMessages.getString("VNAPortExtensionParameterDialog.lblVf")), "");

		txtVf = new JTextField();
		txtVf.setColumns(6);
		add(txtVf, "wrap");

		// ***********************************************************************
		add(new JLabel(VNAMessages.getString("VNAPortExtensionParameterDialog.lblLength.text")), "");

		txtLength = new JTextField();
		txtLength.setColumns(6);
		add(txtLength, "wrap");

		// ***********************************************************************
		add(new JLabel(VNAMessages.getString("VNAPortExtensionParameterDialog.enabled")), "");

		cbEnabled = new JCheckBox("");
		add(cbEnabled, "wrap");

		// ***********************************************************************
		pnlButtons = new JPanel();
		pnlButtons.setLayout(new MigLayout("", "[][]", "[]"));
		getContentPane().add(pnlButtons, "span 2, right");

		btCancel = SwingUtil.createJButton("VNAPortExtensionParameterDialog.Button.Cancel", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDialogCancel();
			}
		});
		pnlButtons.add(btCancel, "width 100px");

		btOK = SwingUtil.createJButton("VNAPortExtensionParameterDialog.Button.OK", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doOK();
			}
		});
		pnlButtons.add(btOK, "width 100px");
		//
		doDialogInit();
		TraceHelper.exit(this, "VNAPortExtensionParameterDialog");
	}

	/**
	 * 
	 */
	protected void doOK() {
		TraceHelper.entry(this, "doOK");

		double len;
		double vf;

		try {
			len = VNAFormatFactory.getLengthFormat().parse(txtLength.getText()).doubleValue();
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(getOwner(), VNAMessages.getString("VNAPortExtensionParameterDialog.LenNoNumber.msg"), VNAMessages.getString("VNAPortExtensionParameterDialog.title"), JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			vf = VNAFormatFactory.getVelocityFormat().parse(txtVf.getText()).doubleValue();
			if ((vf <= 0) || (vf > 1.0)) {
				JOptionPane.showMessageDialog(getOwner(), VNAMessages.getString("VNAPortExtensionParameterDialog.VFError.msg"), VNAMessages.getString("VNAPortExtensionParameterDialog.title"), JOptionPane.WARNING_MESSAGE);
				return;
			}
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(getOwner(), VNAMessages.getString("VNAPortExtensionParameterDialog.VFNoNumber.msg"), VNAMessages.getString("VNAPortExtensionParameterDialog.title"), JOptionPane.WARNING_MESSAGE);
			return;
		}

		config.setPortExtensionCableLength(len);
		config.setPortExtensionVf(vf);
		config.setPortExtensionState(cbEnabled.isSelected());
		setVisible(false);
		dispose();

		TraceHelper.exit(this, "doOK");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doExit()
	 */
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		//
		txtVf.setText(VNAFormatFactory.getVelocityFormat().format(config.getPortExtensionVf()));
		txtLength.setText(VNAFormatFactory.getPortExtensionLengthFormat().format(config.getPortExtensionCableLength()));
		cbEnabled.setSelected(config.isPortExtensionEnabled());
		// add escape key to window
		addEscapeKey();

		//
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}
}
