/**
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.export;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAAutoExportSettingsDialog extends KrauseDialog implements ActionListener {
	private VNAConfig config = VNAConfig.getSingleton();
	private JTextField txtName;
	private JTextField txtDirectory;
	private JButton btnSearch;
	private JButton btOK;
	private JButton btCancel;

	private JRadioButton rdbtnNone;
	private JRadioButton rdbtnXls;
	private JRadioButton rdbtnCsv;
	private JRadioButton rdbtnPdf;
	private JRadioButton rdbtnJpg;
	private JRadioButton rdbtnXml;
	private JRadioButton rdbtnZPlot;
	private JRadioButton rdbtnSParm;

	/**
	 * Create the dialog.
	 */
	public VNAAutoExportSettingsDialog(VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);
		setResizable(false);
		TraceHelper.entry(this, "VNAAutoExportSettingsDialog");

		JPanel panel;

		setTitle(VNAMessages.getString("VNAAutoExportDialog.Title")); //$NON-NLS-1$ 
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 556, 250);
		getContentPane().setLayout(new MigLayout("", "[grow,fill]", "[][][]"));

		//
		panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Outputfile", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(panel, "wrap");
		panel.setLayout(new MigLayout("", "[][grow,fill][]", "[][]"));

		JLabel lblName = new JLabel(VNAMessages.getString("VNAAutoExportDialog.Filename"));
		panel.add(lblName, "");

		txtName = new JTextField();
		txtName.setToolTipText(VNAMessages.getString("VNAAutoExportDialog.Filename.toolTipText")); //$NON-NLS-1$
		txtName.setColumns(10);
		lblName.setLabelFor(txtName);
		panel.add(txtName, "wrap");

		JLabel lblDirectory = new JLabel(VNAMessages.getString("VNAAutoExportDialog.Directory"));
		panel.add(lblDirectory, "");

		txtDirectory = new JTextField();
		txtDirectory.setEditable(false);
		txtDirectory.setColumns(10);
		panel.add(txtDirectory, "");

		btnSearch = SwingUtil.createJButton("VNAAutoExportDialog.ButtonSearch", this);
		panel.add(btnSearch, "wrap");

		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, VNAMessages.getString("VNAAutoExportDialog.Format"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		rdbtnNone = SwingUtil.createJRadioButton("VNAAutoExportDialog.NoExport", null);
		panel.add(rdbtnNone);

		rdbtnCsv = SwingUtil.createJRadioButton("Menu.Export.CSV", null);
		panel.add(rdbtnCsv);

		rdbtnJpg = SwingUtil.createJRadioButton("Menu.Export.JPG", null);
		panel.add(rdbtnJpg);

		rdbtnPdf = SwingUtil.createJRadioButton("Menu.Export.PDF", null);
		panel.add(rdbtnPdf);

		rdbtnSParm = SwingUtil.createJRadioButton("Menu.Export.S2P", null);
		panel.add(rdbtnSParm);

		rdbtnXls = SwingUtil.createJRadioButton("Menu.Export.XLS", null);
		panel.add(rdbtnXls);

		rdbtnXml = SwingUtil.createJRadioButton("Menu.Export.XML", null);
		panel.add(rdbtnXml);

		rdbtnZPlot = SwingUtil.createJRadioButton("Menu.Export.ZPlot", null);
		panel.add(rdbtnZPlot);

		getContentPane().add(panel, "wrap");

		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnNone);
		bg.add(rdbtnCsv);
		bg.add(rdbtnJpg);
		bg.add(rdbtnPdf);
		bg.add(rdbtnSParm);
		bg.add(rdbtnXls);
		bg.add(rdbtnXml);
		bg.add(rdbtnZPlot);

		//
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, "right,wrap");
		btOK = SwingUtil.createJButton("Button.Save", this); //$NON-NLS-1$ 
		btCancel = SwingUtil.createJButton("Button.Cancel", this); //$NON-NLS-1$
		buttonPane.add(new HelpButton(this, "VNAAutoExportSettingsDialog"));
		buttonPane.add(btCancel);
		btOK.setActionCommand("OK");
		buttonPane.add(btOK);
		getRootPane().setDefaultButton(btOK);

		doDialogInit();
		TraceHelper.exit(this, "VNAAutoExportSettingsDialog");
	}

	protected void doDialogInit() {
		loadDefaults();
		//
		addEscapeKey();
		pack();
		showCentered(getWidth(), getHeight());
	}

	private void loadDefaults() {
		TraceHelper.entry(this, "loadDefaults");
		txtDirectory.setText(config.getAutoExportDirectory());
		txtName.setText(config.getAutoExportFilename());

		rdbtnNone.setSelected(true);
		rdbtnCsv.setSelected(config.getAutoExportFormat() == 1);
		rdbtnJpg.setSelected(config.getAutoExportFormat() == 2);
		rdbtnPdf.setSelected(config.getAutoExportFormat() == 3);
		rdbtnSParm.setSelected(config.getAutoExportFormat() == 4);
		rdbtnXls.setSelected(config.getAutoExportFormat() == 5);
		rdbtnXml.setSelected(config.getAutoExportFormat() == 6);
		rdbtnZPlot.setSelected(config.getAutoExportFormat() == 7);
		TraceHelper.exit(this, "loadDefaults");

	}

	private void saveDefaults() {
		TraceHelper.entry(this, "saveDefaults");
		config.setAutoExportDirectory(txtDirectory.getText());
		config.setAutoExportFilename(txtName.getText());
		if (rdbtnNone.isSelected())
			config.setAutoExportFormat(0);
		else if (rdbtnCsv.isSelected())
			config.setAutoExportFormat(1);
		else if (rdbtnJpg.isSelected())
			config.setAutoExportFormat(2);
		else if (rdbtnPdf.isSelected())
			config.setAutoExportFormat(3);
		else if (rdbtnSParm.isSelected())
			config.setAutoExportFormat(4);
		else if (rdbtnXls.isSelected())
			config.setAutoExportFormat(5);
		else if (rdbtnXml.isSelected())
			config.setAutoExportFormat(6);
		else if (rdbtnZPlot.isSelected())
			config.setAutoExportFormat(7);
		TraceHelper.exit(this, "saveDefaults");
	}

	protected void doDialogCancel() {
		setVisible(false);
		dispose();
	}

	protected void doSave() {
		saveDefaults();
		doDialogCancel();
	}

	private void doSelectExportDirectory() {
		TraceHelper.entry(this, "doSelectExportDirectory");
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setSelectedFile(new File(config.getExportDirectory()));
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			config.setExportDirectory(file.getAbsolutePath());
			txtDirectory.setText(config.getExportDirectory());
		}

		TraceHelper.exit(this, "doSelectExportDirectory");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		if (e.getSource() == btCancel) {
			doDialogCancel();
		} else if (e.getSource() == btOK) {
			doSave();
		} else if (e.getSource() == btnSearch) {
			doSelectExportDirectory();
		}
		TraceHelper.exit(this, "actionPerformed");
	}
}
