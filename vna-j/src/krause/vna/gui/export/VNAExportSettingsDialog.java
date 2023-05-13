/**
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.export;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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

public class VNAExportSettingsDialog extends KrauseDialog implements ActionListener {
	private final JPanel contentPanel;

	private VNAConfig config = VNAConfig.getSingleton();
	private JTextField txtName;
	private JTextField txtDirectory;
	private JTextArea txtComment;
	private JCheckBox cbOverwrite;
	private JTextField txtTitle;
	private JRadioButton rbDecSepComma;
	private JRadioButton rbDecSepDot;
	private JButton btnSearch;
	private JButton btnSave;
	private JButton btnCancel;

	private JTextField txtJPGWidth;

	private JTextField txtJPGHeight;

	private JRadioButton rbMarkerSizeSmall;

	private JRadioButton rbMarkerSizeMedium;

	private JRadioButton rbMarkerSizeLarge;

	private JCheckBox cbMarkerDataInDiagram;

	private JCheckBox cbMarkerDataHorizontal;

	private JCheckBox cbSubLegend;

	private JCheckBox cbMainLegend;

	private JCheckBox cbFooter;

	private JComboBox<String> cbFontTextMarker;

	private JComboBox<String> cbFontHeadline;

	/**
	 * Create the dialog.
	 */
	public VNAExportSettingsDialog(VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);
		TraceHelper.entry(this, "VNAExportSettingsDialog");

		setTitle(VNAMessages.getString("VNAExportDialog.Title")); //$NON-NLS-1$ 
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		setConfigurationPrefix("VNAExportSettingsDialog");
		setProperties(config);

		setModal(true);
		setMinimumSize(new Dimension(630, 520));
		setPreferredSize(new Dimension(850, 550));
		getContentPane();

		contentPanel = new JPanel();
		contentPanel.setLayout(new MigLayout("", "[grow,fill]", "0[]0[grow,fill]0[]0[]0"));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);

		//
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new MigLayout("", "[][grow,fill][]", "0[]0[]0"));
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.Outputfile"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		JLabel lblName = new JLabel(VNAMessages.getString("VNAExportDialog.Filename"));
		lblName.setBounds(12, 26, 87, 16);
		lblName.setLabelFor(txtName);
		panel_1.add(lblName, "");

		txtName = new JTextField();
		txtName.setToolTipText(VNAMessages.getString("VNAExportDialog.txtName.toolTipText")); //$NON-NLS-1$
		txtName.setColumns(10);
		panel_1.add(txtName, "");

		cbOverwrite = new JCheckBox(VNAMessages.getString("VNAExportDialog.CbOverwrite"));
		cbOverwrite.setToolTipText(VNAMessages.getString("VNAExportDialog.cbOverwrite.toolTipText")); //$NON-NLS-1$
		panel_1.add(cbOverwrite, "wrap");

		//
		JLabel lblDirectory = new JLabel(VNAMessages.getString("VNAExportDialog.Directory"));
		lblDirectory.setBounds(12, 60, 87, 16);
		panel_1.add(lblDirectory, "");

		txtDirectory = new JTextField();
		txtDirectory.setEditable(false);
		panel_1.add(txtDirectory, "");

		btnSearch = new JButton(VNAMessages.getString("VNAExportDialog.ButtonSearch"));
		btnSearch.setToolTipText(VNAMessages.getString("VNAExportSettingsDialog.btnSearch.toolTipText")); //$NON-NLS-1$
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doSelectExportDirectory();
			}
		});
		panel_1.add(btnSearch, "wrap");

		//
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new MigLayout("", "[grow,fill][]", "0[][grow,fill]"));
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.Headline"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		txtTitle = new JTextField();
		txtTitle.setBorder(new LineBorder(new Color(171, 173, 179)));
		panel_2.add(txtTitle, "");

		panel_2.add(new JLabel(VNAMessages.getString("VNAExportDialog.FontSizeTextMarker")), "");
		cbFontHeadline = new JComboBox<String>(new String[] {
				"10",
				"11",
				"12",
				"13",
				"14",
				"15",
				"16",
				"17",
				"18",
				"19",
				"20",
				"21",
				"22",
				"23",
				"24",
				"25",
				"26",
				"27",
				"28",
				"29",
				"30",
		});
		panel_2.add(cbFontHeadline, "wrap");
		//
		txtComment = new JTextArea();
		if (Locale.getDefault().getCountry().equals(Locale.JAPAN.getCountry())) {
			txtComment.setFont(new Font("Monospaced", Font.PLAIN, 12));
		} else {
			txtComment.setFont(new Font("Courier New", Font.PLAIN, 12));
		}
		txtComment.setLineWrap(true);
		txtComment.setWrapStyleWord(true);
		JScrollPane sp = new JScrollPane(txtComment);
		panel_2.add(sp, "span 3");

		//
		//
		JPanel panel_3 = new JPanel();
		panel_3.setLayout(new MigLayout("", "[][]", "0[]0"));
		panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.DecimalSeparator"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		rbDecSepComma = SwingUtil.createJRadioButton("VNAExportDialog.DecimalSeparatorComma", this);
		rbDecSepDot = SwingUtil.createJRadioButton("VNAExportDialog.DecimalSeparatorDot", this);
		panel_3.add(rbDecSepComma, "");
		panel_3.add(rbDecSepDot, "");

		ButtonGroup aGroup = new ButtonGroup();
		aGroup.add(rbDecSepComma);
		aGroup.add(rbDecSepDot);

		//
		//
		JPanel panel_6 = new JPanel();
		panel_6.setLayout(new MigLayout("", "[][]", "0[]0"));
		panel_6.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.MarkerSize"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		rbMarkerSizeSmall = SwingUtil.createJRadioButton("VNAExportDialog.MarkerSizeSmall", this);
		rbMarkerSizeMedium = SwingUtil.createJRadioButton("VNAExportDialog.MarkerSizeMedium", this);
		rbMarkerSizeLarge = SwingUtil.createJRadioButton("VNAExportDialog.MarkerSizeLarge", this);
		panel_6.add(rbMarkerSizeSmall, "");
		panel_6.add(rbMarkerSizeMedium, "");
		panel_6.add(rbMarkerSizeLarge, "wrap");

		aGroup = new ButtonGroup();
		aGroup.add(rbMarkerSizeSmall);
		aGroup.add(rbMarkerSizeMedium);
		aGroup.add(rbMarkerSizeLarge);

		panel_6.add(new JLabel(VNAMessages.getString("VNAExportDialog.FontSizeTextMarker")), "");
		cbFontTextMarker = new JComboBox<String>(new String[] {
				"10",
				"15",
				"20",
				"25"
		});
		panel_6.add(cbFontTextMarker, "");

		//
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(new MigLayout("", "[][]", "0[]0"));
		panel_5.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.MarkerData"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		cbMarkerDataInDiagram = SwingUtil.createJCheckbox("VNAExportDialog.MarkerDataInDiagramm", this);
		cbMarkerDataHorizontal = SwingUtil.createJCheckbox("VNAExportDialog.MarkerDataHorizontal", this);
		panel_5.add(cbMarkerDataInDiagram, "wrap");
		panel_5.add(cbMarkerDataHorizontal, "");

		//
		JPanel panel_7 = new JPanel();
		panel_7.setLayout(new MigLayout("", "[][]", "0[]0"));
		panel_7.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.Legends"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		cbSubLegend = SwingUtil.createJCheckbox("VNAExportDialog.ShowSubLegend", this);
		cbMainLegend = SwingUtil.createJCheckbox("VNAExportDialog.ShowMainLegend", this);
		cbFooter = SwingUtil.createJCheckbox("VNAExportDialog.ShowFooter", this);
		panel_7.add(cbMainLegend, "");
		panel_7.add(cbSubLegend, "");
		panel_7.add(cbFooter, "");

		//
		//
		JPanel panel_4 = new JPanel();
		panel_4.setLayout(new MigLayout("", "[][]", "0[]0"));
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNAExportDialog.JPGSize"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		txtJPGWidth = new JTextField();
		txtJPGWidth.setColumns(8);
		txtJPGWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		txtJPGHeight = new JTextField();
		txtJPGHeight.setColumns(8);
		txtJPGHeight.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_4.add(new JLabel(VNAMessages.getString("VNAExportDialog.JPGSize.Width")), "");
		panel_4.add(txtJPGWidth, "");
		panel_4.add(new JLabel(VNAMessages.getString("VNAExportDialog.JPGSize.Height")), "");
		panel_4.add(txtJPGHeight, "");

		//
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		btnSave = SwingUtil.createJButton("Button.Save", this);
		btnCancel = SwingUtil.createJButton("Button.Cancel", this);

		buttonPane.add(new HelpButton(this, "VNAExportSettingsDialog"));
		buttonPane.add(btnCancel);
		btnSave.setActionCommand("OK");
		buttonPane.add(btnSave);
		getRootPane().setDefaultButton(btnSave);

		contentPanel.add(panel_1, "grow, span 3,wrap");
		contentPanel.add(panel_2, "grow, span 3,wrap");
		contentPanel.add(panel_3, "grow");
		contentPanel.add(panel_5, "");
		contentPanel.add(panel_6, "wrap");
		contentPanel.add(panel_4, "");
		contentPanel.add(panel_7, "span 2, grow, wrap");
		contentPanel.add(buttonPane, "grow, span 3,wrap");

		doDialogInit();
		TraceHelper.exit(this, "VNAExportSettingsDialog");
	}

	protected void doDialogInit() {
		loadDefaults();
		//
		addEscapeKey();
		doDialogShow();
	}

	private void loadDefaults() {
		txtDirectory.setText(config.getExportDirectory());
		txtName.setText(config.getExportFilename());
		txtComment.setText(config.getExportComment());
		txtTitle.setText(config.getExportTitle());
		cbOverwrite.setSelected(config.isExportOverwrite());
		//
		rbDecSepComma.setSelected(",".equals(config.getExportDecimalSeparator()));
		rbDecSepDot.setSelected(".".equals(config.getExportDecimalSeparator()));
		//
		rbMarkerSizeSmall.setSelected(config.getMarkerSize() == 1);
		rbMarkerSizeMedium.setSelected(config.getMarkerSize() == 2);
		rbMarkerSizeLarge.setSelected(config.getMarkerSize() == 3);

		cbMarkerDataHorizontal.setSelected(config.isPrintMarkerDataHorizontal());
		cbMarkerDataInDiagram.setSelected(config.isPrintMarkerDataInDiagramm());

		cbSubLegend.setSelected(config.isPrintSubLegend());
		cbMainLegend.setSelected(config.isPrintMainLegend());
		cbFooter.setSelected(config.isPrintFooter());

		txtJPGWidth.setText("" + config.getExportDiagramWidth());
		txtJPGHeight.setText("" + config.getExportDiagramHeight());

		cbFontTextMarker.setSelectedItem("" + config.getFontSizeTextMarker());
		cbFontHeadline.setSelectedItem("" + config.getExportTitleFontSize());
	}

	private void saveDefaults() {
		config.setExportComment(txtComment.getText());
		config.setExportDirectory(txtDirectory.getText());
		config.setExportFilename(txtName.getText());
		config.setExportTitle(txtTitle.getText());
		config.setExportOverwrite(cbOverwrite.isSelected());
		config.setExportDecimalSeparator(rbDecSepComma.isSelected() ? "," : ".");
		//
		if (rbMarkerSizeLarge.isSelected()) {
			config.setMarkerSize(3);
		} else if (rbMarkerSizeMedium.isSelected()) {
			config.setMarkerSize(2);
		} else {
			config.setMarkerSize(1);
		}

		config.setPrintMarkerDataHorizontal(cbMarkerDataHorizontal.isSelected());
		config.setPrintMarkerDataInDiagramm(cbMarkerDataInDiagram.isSelected());
		config.setPrintSubLegend(cbSubLegend.isSelected());
		config.setPrintMainLegend(cbMainLegend.isSelected());
		config.setPrintFooter(cbFooter.isSelected());

		config.setExportDiagramWidth(Integer.parseInt(txtJPGWidth.getText()));
		config.setExportDiagramHeight(Integer.parseInt(txtJPGHeight.getText()));
		config.setFontSizeTextMarker(Integer.parseInt((String) cbFontTextMarker.getSelectedItem()));
		config.setExportTitleFontSize(Integer.parseInt((String) cbFontHeadline.getSelectedItem()));
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
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
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
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}
}
