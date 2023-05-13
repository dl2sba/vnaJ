package krause.vna.gui.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.filter.Gaussian;
import krause.vna.data.filter.VNABaseFilterHelper;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAGaussianFilterCreatorDialog extends KrauseDialog implements ActionListener {
	private static final String PROPERTIES_PREFIX = "VNAGaussianFilterCreatorDialog";
	public final static int FONT_SIZE = 30;

	private VNAConfig config = VNAConfig.getSingleton();
	private JButton btOK;
	private JButton btCreate;
	private JPanel panel;
	private JTextField txtSigma;
	private JComboBox cbLength;

	/**
	 * 
	 * @param pMainFrame
	 */
	public VNAGaussianFilterCreatorDialog(Window wnd) {
		super(wnd, true);

		setConfigurationPrefix(PROPERTIES_PREFIX);
		setProperties(config);

		setTitle(VNAMessages.getString("VNAGaussianFilterCreatorDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(280, 130));
		addWindowListener(this);

		// content pane
		getContentPane().setLayout(new BorderLayout(5, 5));

		// content panel
		panel = new JPanel(new MigLayout("", "[100px]0[grow]", "[][][][]"));
		getContentPane().add(panel, BorderLayout.CENTER);

		panel.add(new JLabel(VNAMessages.getString("VNAGaussianFilterCreatorDialog.length")), "");
		cbLength = new JComboBox<Integer>(new Integer[] {
				3,
				5,
				7,
				9,
				11,
				13,
				15,
				17,
				19,
				21,
				23,
				25,
				27,
		});
		cbLength.setMaximumRowCount(4);
		panel.add(cbLength, "wrap");

		panel.add(new JLabel(VNAMessages.getString("VNAGaussianFilterCreatorDialog.sigma")), "");
		txtSigma = new JTextField();
		panel.add(txtSigma, "grow,wrap");

		// button panel
		btOK = SwingUtil.createJButton("Button.Cancel", this);
		panel.add(btOK, "left");

		btCreate = SwingUtil.createJButton("Button.Create", this);
		panel.add(btCreate, "right");

		doDialogInit();
	}

	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		TraceHelper.text(this, "actionPerformed", e.toString());
		if (e.getSource() == btOK) {
			doDialogCancel();
		} else if (e.getSource() == btCreate) {
			doCreateFile();
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	private void doCreateFile() {
		TraceHelper.entry(this, "doCreateFile");
		try {
			double sigma = VNAFormatFactory.getTemperatureFormat().parse(txtSigma.getText()).doubleValue();
			int length = (int) cbLength.getSelectedItem();
			double[] rc = new Gaussian(sigma).kernel1D(length);

			VNABaseFilterHelper.saveFilterdata(config.getGaussianFilterFileName(), rc);

			setVisible(false);
			dispose();

		} catch (ParseException e) {
			ErrorLogHelper.exception(this, "doCreateFile", e);
		}
		TraceHelper.exit(this, "doCreateFile");
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		// and hide myself
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
		txtSigma.setText(VNAFormatFactory.getTemperatureFormat().format(1.0));
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

}
