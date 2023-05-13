package krause.common.validation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import krause.common.gui.ILocationAwareDialog;
import krause.common.gui.KrauseDialog;
import krause.common.resources.CommonMessages;
import krause.util.ras.logging.TraceHelper;

public class ValidationResultsDialog extends KrauseDialog implements ILocationAwareDialog {

	private final JPanel contentPanel = new JPanel();
	private ValidationResultTable lstMessages;

	/**
	 * Create the dialog.
	 */
	public ValidationResultsDialog(Window mainFrame, ValidationResults pResults, String title) {
		super(mainFrame, true);
		setTitle(title);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		getContentPane().add(panel, BorderLayout.NORTH);
		JLabel lblOneOrMore = new JLabel(CommonMessages.getString("ValidationResultsDialog.headline"));
		lblOneOrMore.setForeground(Color.RED);
		lblOneOrMore.setFont(new Font("Segoe UI", Font.BOLD, 18));
		panel.add(lblOneOrMore);
		FlowLayout flContentPanel = new FlowLayout();
		flContentPanel.setAlignment(FlowLayout.LEFT);
		this.contentPanel.setLayout(flContentPanel);
		this.contentPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		this.lstMessages = new ValidationResultTable();
		this.lstMessages.setFillsViewportHeight(true);
		this.lstMessages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.lstMessages.setRowSelectionAllowed(false);
		this.lstMessages.getModel().setResults(pResults);
		this.lstMessages.setPreferredScrollableViewportSize(new Dimension(600, 200));

		JScrollPane sp = new JScrollPane(this.lstMessages);
		sp.setAlignmentX(LEFT_ALIGNMENT);
		this.contentPanel.add(sp);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		JButton okButton = new JButton(CommonMessages.getString("Button.OK"));
		okButton.addActionListener(e -> doDialogCancel());
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		doDialogInit();
	}

	@Override
	protected void doDialogCancel() {
		setVisible(false);
		dispose();

	}

	@Override
	protected void doDialogInit() {
		addEscapeKey();
		showInPlace();
	}

	public void restoreWindowPosition() {
		TraceHelper.entry(this, "restoreWindowPosition");
		Dimension dimRoot = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dimRoot.width / 2) - (getSize().width / 2);
		int y = (dimRoot.height / 2) - (getSize().height / 2);
		// center on component
		setLocation(x, y);
		TraceHelper.exit(this, "restoreWindowPosition");
	}

	public void restoreWindowSize() {
		TraceHelper.entry(this, "restoreWindowSize");
		TraceHelper.exit(this, "restoreWindowSize");
	}

	public void showInPlace() {
		TraceHelper.entry(this, "showInPlace");
		restoreWindowSize();
		pack();
		restoreWindowPosition();
		setVisible(true);
		TraceHelper.exit(this, "showInPlace");
	}

	public void storeWindowPosition() {
		TraceHelper.entry(this, "storeWindowPosition");
		TraceHelper.exit(this, "storeWindowPosition");

	}

	public void storeWindowSize() {
		TraceHelper.entry(this, "storeWindowSize");
		TraceHelper.exit(this, "storeWindowSize");

	}
}
