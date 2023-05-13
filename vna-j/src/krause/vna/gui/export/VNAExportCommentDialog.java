/**
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.export;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAExportCommentDialog extends KrauseDialog implements ActionListener {
	private final JPanel contentPanel;

	private VNAConfig config = VNAConfig.getSingleton();
	private JTextArea txtComment;
	private JTextField txtTitle;
	private JButton btnSave;
	private JButton btnCancel;
	boolean dialogCancelled = false;

	/**
	 * Create the dialog.
	 */
	public VNAExportCommentDialog(VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);
		TraceHelper.entry(this, "VNAExportCommentDialog");

		setTitle(VNAMessages.getString("VNAExportCommentDialog.Title")); //$NON-NLS-1$ 
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 678, 472);
		getContentPane();

		contentPanel = new JPanel();
		contentPanel.setLayout(new MigLayout("", "[grow,fill]", "[][grow,fill][]"));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);

		//	
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new MigLayout("", "[grow,fill][]", "[]"));
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.Headline"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPanel.add(panel_2, "wrap");

		txtTitle = new JTextField();
		txtTitle.setBorder(new LineBorder(new Color(171, 173, 179)));
		panel_2.add(txtTitle, "");

		//
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.Comment"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPanel.add(panel_1, "wrap");

		txtComment = new JTextArea();
		txtComment.setFont(new Font("Courier New", Font.PLAIN, 12));
		txtComment.setLineWrap(true);
		txtComment.setWrapStyleWord(true);
		JScrollPane sp = new JScrollPane(txtComment);
		panel_1.add(sp);

		//
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		contentPanel.add(buttonPane, "wrap");
		buttonPane.add(new HelpButton(this, "VNAExportCommentDialog"));
		btnSave = SwingUtil.createJButton("Button.Save", this);
		btnCancel = SwingUtil.createJButton("Button.Cancel", this);

		buttonPane.add(btnCancel);
		btnSave.setActionCommand("OK");
		buttonPane.add(btnSave);
		getRootPane().setDefaultButton(btnSave);
		doDialogInit();
		TraceHelper.exit(this, "VNAExportCommentDialog");
	}

	protected void doDialogInit() {
		loadDefaults();
		//
		addEscapeKey();
		showCentered(getWidth(), getHeight());
	}

	private void loadDefaults() {
		txtComment.setText(config.getExportComment());
		txtTitle.setText(config.getExportTitle());
	}

	private void saveDefaults() {
		config.setExportComment(txtComment.getText());
		config.setExportTitle(txtTitle.getText());
	}

	/**
	 * 
	 */
	protected void doSave() {
		TraceHelper.entry(this, "doSave");
		dialogCancelled = false;
		saveDefaults();
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doSave");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		if (e.getSource() == btnCancel) {
			doDialogCancel();
		} else if (e.getSource() == btnSave) {
			doSave();
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doCANCEL()
	 */
	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		dialogCancelled = true;
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	public boolean isDialogCancelled() {
		return dialogCancelled;
	}
}
