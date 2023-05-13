package krause.vna.gui.smith;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.complex.Complex;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.input.ComplexInputField;
import krause.vna.gui.input.ComplexInputFieldValueChangeListener;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNASmithDiagramConfigDialog extends KrauseDialog implements ComplexInputFieldValueChangeListener, MouseListener {
	// default colors
	public static final Color SMITH_PANEL_DEFCOL_IMPEDANCE = Color.RED;
	public static final Color SMITH_PANEL_DEFCOL_ADMITTANCE = Color.BLUE;
	public static final Color SMITH_PANEL_DEFCOL_SWR = Color.CYAN;
	public static final Color SMITH_PANEL_DEFCOL_MARKER = Color.YELLOW;
	public static final Color SMITH_PANEL_DEFCOL_BACKGROUND = Color.BLACK;
	public static final Color SMITH_PANEL_DEFCOL_DATA = Color.GREEN;
	public static final Color SMITH_PANEL_DEFCOL_TEXT = Color.WHITE;

	// color constants in config
	public static final String SMITH_PANEL_COL_MARKER = "SmithPanel.colMarker";
	public static final String SMITH_PANEL_COL_BACKGROUND = "SmithPanel.colBackground";
	public static final String SMITH_PANEL_COL_DATA = "SmithPanel.colLines";
	public static final String SMITH_PANEL_COL_TEXT = "SmithPanel.colText";
	public static final String SMITH_PANEL_COL_SWR = "SmithPanel.colSWR";
	public static final String SMITH_PANEL_COL_IMPEDANCE = "SmithPanel.colImpedance";
	public static final String SMITH_PANEL_COL_ADMITTANCE = "SmithPanel.colInductance";

	// show these elements
	public static final String SMITH_PANEL_SHOW_MARKER_SWR = "SmithPanel.showMarker.SWR";
	public static final String SMITH_PANEL_SHOW_MARKER_Z = "SmithPanel.showMarker.Z";
	public static final String SMITH_PANEL_SHOW_MARKER_XS = "SmithPanel.showMarker.XS";
	public static final String SMITH_PANEL_SHOW_MARKER_RS = "SmithPanel.showMarker.RS";
	public static final String SMITH_PANEL_SHOW_MARKER_RL = "SmithPanel.showMarker.RL";
	public static final String SMITH_PANEL_SHOW_MARKER_PHASE = "SmithPanel.showMarker.Phase";
	public static final String SMITH_PANEL_SHOW_MARKER_MAG = "SmithPanel.showMarker.Mag";

	// names in ressources
	private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_TEXT_COLOR = "VNASmithDiagramConfigDialog.textColor";
	private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_BACK_COLOR = "VNASmithDiagramConfigDialog.backColor";
	private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_SWR_COLOR = "VNASmithDiagramConfigDialog.swrColor";
	private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_MARKER_COLOR = "VNASmithDiagramConfigDialog.markerColor";
	private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_DATA_COLOR = "VNASmithDiagramConfigDialog.dataColor";
	private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_IMPEDANCE_COLOR = "VNASmithDiagramConfigDialog.impedanceColor";
	private static final String VNA_SMITH_DIAGRAM_CONFIG_DIALOG_ADMITTANCE_COLOR = "VNASmithDiagramConfigDialog.admittanceColor";

	// names in config
	public static final String SMITH_PANEL_CIRCLES_SWR = "SmithPanel.circlesSWR";
	public static final String SMITH_PANEL_CIRCLES_ADMITTANCE_REAL = "SmithPanel.circlesAdmittanceReal";
	public static final String SMITH_PANEL_CIRCLES_ADMITTANCE_IMAGINARY = "SmithPanel.circlesAdmittanceImaginary";
	public static final String SMITH_PANEL_CIRCLES_IMPEDANCE_REAL = "SmithPanel.circlesImpedanceReal";
	public static final String SMITH_PANEL_CIRCLES_IMPEDANCE_IMAGINARY = "SmithPanel.circlesImpedanceImaginary";

	private VNAConfig config = VNAConfig.getSingleton();

	public static final String DEFAULT_CIRCLES_ADMITTANCE_IMAG = "";
	public static final String DEFAULT_CIRCLES_ADMITTANCE_REAL = "";
	public static final String DEFAULT_CIRCLES_IMPEDANCE_IMAG = "-5.0 -2.0 -1.0 -0.5 -0.2 0.0 0.2 0.5 1.0 2.0 5.0";
	public static final String DEFAULT_CIRCLES_IMPEDANCE_REAL = "0.0 0.2 0.5 1.0 2.0 5.0";
	public static final String DEFAULT_CIRCLES_SWR = "2.0 3.0";

	private JCheckBox cbRL;
	private JCheckBox cbRS;
	private JCheckBox cbXS;
	private JCheckBox cbZ;
	private JCheckBox cbPHASE;
	private ComplexInputField referenceResistance;

	private JTextField txtColBackground;
	private JTextField txtColText;
	private JTextField txtColData;
	private JTextField txtColMarker;
	private JTextField txtColSWR;
	private JTextField txtColAdmittance;
	private JTextField txtColImpedance;

	private JCheckBox cbMag;

	private AbstractButton cbSWR;

	private JTextField txtAdmittanceRealCircles;
	private JTextField txtAdmittanceImaginaryCircles;

	private JTextField txtImpedanceRealCircles;
	private JTextField txtImpedanceImaginaryCircles;

	private JTextField txtSWRCircles;

	public VNASmithDiagramConfigDialog() {
		super(true);
		final String methodName = "VNASmithDiagramConfigDialog";
		TraceHelper.entry(this, methodName);

		setConfigurationPrefix(methodName);
		setProperties(VNAConfig.getSingleton());

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		setTitle(VNAMessages.getString("VNASmithDiagramConfigDialog.title"));
		setResizable(true);
		setPreferredSize(new Dimension(390, 670));
		getContentPane().setLayout(new MigLayout("", "", ""));

		getContentPane().add(createCenterPanel(), "grow,wrap");
		getContentPane().add(createButtonPanel(), "grow,wrap");

		doDialogInit();
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 * @return
	 */
	private Component createButtonPanel() {
		final String methodName = "createButtonPanel";
		TraceHelper.entry(this, methodName);

		JPanel pnlButton = new JPanel();

		pnlButton.add(SwingUtil.createJButton("Button.Default", e -> doDefaults()));
		pnlButton.add(SwingUtil.createJButton("Button.Cancel", e -> doDialogCancel()));
		pnlButton.add(SwingUtil.createJButton("Button.OK", e -> doOK()));

		TraceHelper.exit(this, methodName);
		return pnlButton;
	}

	private JPanel createMarkerOptionPanel() {
		final String methodName = "createMarkerOptionPanel";
		TraceHelper.entry(this, methodName);

		JPanel pnlMarkerOpt = new JPanel(new MigLayout("", "[][][][]", "[]"));
		pnlMarkerOpt.setBorder(new TitledBorder(null, VNAMessages.getString("VNASmithDiagramConfigDialog.markers"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlMarkerOpt.add(cbRL = SwingUtil.createJCheckbox("Marker.RL", null), "");
		pnlMarkerOpt.add(cbPHASE = SwingUtil.createJCheckbox("Marker.PhaseRL", null), "");
		pnlMarkerOpt.add(cbZ = SwingUtil.createJCheckbox("Marker.Z", null), "");
		pnlMarkerOpt.add(cbRS = SwingUtil.createJCheckbox("Marker.R", null), "wrap");
		pnlMarkerOpt.add(cbXS = SwingUtil.createJCheckbox("Marker.X", null), "");
		pnlMarkerOpt.add(cbSWR = SwingUtil.createJCheckbox("Marker.SWR", null), "");
		pnlMarkerOpt.add(cbMag = SwingUtil.createJCheckbox("Marker.Magnitude", null), "");

		// remove marker tooltips as some of them contains not valid toolstips
		// for this dialog
		cbRL.setToolTipText(null);
		cbPHASE.setToolTipText(null);
		cbZ.setToolTipText(null);
		cbRS.setToolTipText(null);
		cbXS.setToolTipText(null);
		cbSWR.setToolTipText(null);
		cbMag.setToolTipText(null);

		TraceHelper.exit(this, methodName);
		return pnlMarkerOpt;

	}

	private JPanel createColorOptionPanel() {
		TraceHelper.entry(this, "createColorOptionPanel");
		//
		JPanel pnlColors = new JPanel(new MigLayout("", "[grow,fill][][]", "[][][][][][][]"));
		pnlColors.setBorder(new TitledBorder(null, VNAMessages.getString("VNASmithDiagramConfigDialog.colors"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		txtColBackground = createColorSelectField(pnlColors, VNA_SMITH_DIAGRAM_CONFIG_DIALOG_BACK_COLOR, this);
		txtColText = createColorSelectField(pnlColors, VNA_SMITH_DIAGRAM_CONFIG_DIALOG_TEXT_COLOR, this);
		txtColData = createColorSelectField(pnlColors, VNA_SMITH_DIAGRAM_CONFIG_DIALOG_DATA_COLOR, this);
		txtColMarker = createColorSelectField(pnlColors, VNA_SMITH_DIAGRAM_CONFIG_DIALOG_MARKER_COLOR, this);
		txtColAdmittance = createColorSelectField(pnlColors, VNA_SMITH_DIAGRAM_CONFIG_DIALOG_ADMITTANCE_COLOR, this);
		txtColImpedance = createColorSelectField(pnlColors, VNA_SMITH_DIAGRAM_CONFIG_DIALOG_IMPEDANCE_COLOR, this);
		txtColSWR = createColorSelectField(pnlColors, VNA_SMITH_DIAGRAM_CONFIG_DIALOG_SWR_COLOR, this);

		pnlColors.add(SwingUtil.createJButton("VNASmithDiagramConfigDialog.invert", e -> doInvert()), "grow,wrap");

		TraceHelper.exit(this, "createColorOptionPanel");
		return pnlColors;
	}

	private JPanel createReferenceOptionPanel() {
		TraceHelper.entry(this, "createReferenceOptionPanel");
		//
		JPanel pnlRefRes = new JPanel(new MigLayout("", "[][grow,fill]", ""));
		pnlRefRes.setBorder(new TitledBorder(null, VNAMessages.getString("VNASmithDiagramConfigDialog.reference"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		referenceResistance = new ComplexInputField(config.getSmithReference());
		referenceResistance.setMaximum(new Complex(5000, 5000));
		referenceResistance.setMinimum(new Complex(-5000, -5000));
		referenceResistance.setListener(this);
		pnlRefRes.add(referenceResistance);

		TraceHelper.exit(this, "createReferenceOptionPanel");
		return pnlRefRes;
	}

	private JPanel createCircleOptionPanel() {
		TraceHelper.entry(this, "createCircleOptionPanel");
		//
		JPanel rc = new JPanel(new MigLayout("", "[][grow,fill]", ""));
		rc.setBorder(new TitledBorder(null, VNAMessages.getString("VNASmithDiagramConfigDialog.circles"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		rc.add(new JLabel(VNAMessages.getString("VNASmithDiagramConfigDialog.admittanceRealCircles")));
		txtAdmittanceRealCircles = new JTextField();
		txtAdmittanceRealCircles.setEditable(true);
		txtAdmittanceRealCircles.setHorizontalAlignment(SwingConstants.RIGHT);
		txtAdmittanceRealCircles.setColumns(40);
		txtAdmittanceRealCircles.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.admittanceRealCircles.tooltip"));
		rc.add(txtAdmittanceRealCircles, "grow,wrap");

		rc.add(new JLabel(VNAMessages.getString("VNASmithDiagramConfigDialog.admittanceImaginaryCircles")));
		txtAdmittanceImaginaryCircles = new JTextField();
		txtAdmittanceImaginaryCircles.setEditable(true);
		txtAdmittanceImaginaryCircles.setHorizontalAlignment(SwingConstants.RIGHT);
		txtAdmittanceImaginaryCircles.setColumns(40);
		txtAdmittanceImaginaryCircles.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.admittanceImaginaryCircles.tooltip"));
		rc.add(txtAdmittanceImaginaryCircles, "grow,wrap");

		rc.add(new JLabel(VNAMessages.getString("VNASmithDiagramConfigDialog.impedanceRealCircles")));
		txtImpedanceRealCircles = new JTextField();
		txtImpedanceRealCircles.setEditable(true);
		txtImpedanceRealCircles.setHorizontalAlignment(SwingConstants.RIGHT);
		txtImpedanceRealCircles.setColumns(40);
		txtImpedanceRealCircles.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.impedanceRealCircles.tooltip"));
		rc.add(txtImpedanceRealCircles, "grow,wrap");

		rc.add(new JLabel(VNAMessages.getString("VNASmithDiagramConfigDialog.impedanceImaginaryCircles")));
		txtImpedanceImaginaryCircles = new JTextField();
		txtImpedanceImaginaryCircles.setEditable(true);
		txtImpedanceImaginaryCircles.setHorizontalAlignment(SwingConstants.RIGHT);
		txtImpedanceImaginaryCircles.setColumns(40);
		txtImpedanceImaginaryCircles.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.impedanceImaginaryCircles.tooltip"));
		rc.add(txtImpedanceImaginaryCircles, "grow,wrap");

		rc.add(new JLabel(VNAMessages.getString("VNASmithDiagramConfigDialog.swrCircles")));
		txtSWRCircles = new JTextField();
		txtSWRCircles.setEditable(true);
		txtSWRCircles.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSWRCircles.setColumns(40);
		txtSWRCircles.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.swrCircles.tooltip"));
		rc.add(txtSWRCircles, "grow,wrap");

		TraceHelper.exit(this, "createCircleOptionPanel");
		return rc;
	}

	private JPanel createCenterPanel() {
		TraceHelper.entry(this, "createCenterPanel");
		JPanel rc = new JPanel(new MigLayout("", "[grow,fill]", ""));

		rc.add(createMarkerOptionPanel(), "wrap,grow");
		rc.add(createReferenceOptionPanel(), "wrap,grow");
		rc.add(createColorOptionPanel(), "wrap, grow");
		rc.add(createCircleOptionPanel(), "wrap, grow");

		TraceHelper.exit(this, "createCenterPanel");
		return rc;
	}

	/**
	 * 
	 * @param panel
	 * @param id
	 * @param color
	 * @param mouseListener
	 */
	private JTextField createColorSelectField(JPanel panel, String id, MouseListener mouseListener) {
		TraceHelper.entry(this, "createColorSelectField");

		JLabel lbl = new JLabel(VNAMessages.getString(id));
		panel.add(lbl, "");

		JTextField rc = new JTextField(10);
		rc.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		rc.setEditable(false);
		rc.setOpaque(true);
		rc.setForeground(SMITH_PANEL_DEFCOL_BACKGROUND);
		rc.addMouseListener(mouseListener);
		rc.setToolTipText(VNAMessages.getString("VNASmithDiagramConfigDialog.clickToSelect"));
		panel.add(rc, "width 40,wrap");

		TraceHelper.exit(this, "createColorSelectField");

		return rc;
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	/**
	 * 
	 */
	protected void doDefaults() {
		TraceHelper.entry(this, "doDefaults");

		cbZ.setSelected(true);
		cbRS.setSelected(true);
		cbXS.setSelected(true);
		cbRL.setSelected(true);
		cbPHASE.setSelected(true);
		cbMag.setSelected(true);

		referenceResistance.setComplexValue(new Complex(50.0, 0.0));

		txtColText.setBackground(SMITH_PANEL_DEFCOL_TEXT);
		txtColData.setBackground(SMITH_PANEL_DEFCOL_DATA);
		txtColBackground.setBackground(SMITH_PANEL_DEFCOL_BACKGROUND);
		txtColMarker.setBackground(SMITH_PANEL_DEFCOL_MARKER);

		txtColAdmittance.setBackground(SMITH_PANEL_DEFCOL_ADMITTANCE);
		txtColImpedance.setBackground(SMITH_PANEL_DEFCOL_IMPEDANCE);
		txtColSWR.setBackground(SMITH_PANEL_DEFCOL_SWR);

		txtAdmittanceImaginaryCircles.setText(DEFAULT_CIRCLES_ADMITTANCE_IMAG);
		txtAdmittanceRealCircles.setText(DEFAULT_CIRCLES_ADMITTANCE_REAL);

		txtImpedanceImaginaryCircles.setText(DEFAULT_CIRCLES_IMPEDANCE_IMAG);
		txtImpedanceRealCircles.setText(DEFAULT_CIRCLES_IMPEDANCE_REAL);

		txtSWRCircles.setText(DEFAULT_CIRCLES_SWR);

		TraceHelper.exit(this, "doDefaults");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");

		// set if an element is shown
		cbMag.setSelected(config.getBoolean(SMITH_PANEL_SHOW_MARKER_MAG, true));
		cbPHASE.setSelected(config.getBoolean(SMITH_PANEL_SHOW_MARKER_PHASE, true));
		cbRL.setSelected(config.getBoolean(SMITH_PANEL_SHOW_MARKER_RL, true));
		cbRS.setSelected(config.getBoolean(SMITH_PANEL_SHOW_MARKER_RS, true));
		cbXS.setSelected(config.getBoolean(SMITH_PANEL_SHOW_MARKER_XS, true));
		cbZ.setSelected(config.getBoolean(SMITH_PANEL_SHOW_MARKER_Z, true));
		cbSWR.setSelected(config.getBoolean(SMITH_PANEL_SHOW_MARKER_SWR, true));

		referenceResistance.setComplexValue(config.getSmithReference());

		// set the colors of the text.fields
		txtColText.setBackground(config.getColor(SMITH_PANEL_COL_TEXT, SMITH_PANEL_DEFCOL_TEXT));
		txtColData.setBackground(config.getColor(SMITH_PANEL_COL_DATA, SMITH_PANEL_DEFCOL_DATA));
		txtColBackground.setBackground(config.getColor(SMITH_PANEL_COL_BACKGROUND, SMITH_PANEL_DEFCOL_BACKGROUND));
		txtColMarker.setBackground(config.getColor(SMITH_PANEL_COL_MARKER, SMITH_PANEL_DEFCOL_MARKER));

		txtColSWR.setBackground(config.getColor(SMITH_PANEL_COL_SWR, SMITH_PANEL_DEFCOL_SWR));
		txtColAdmittance.setBackground(config.getColor(SMITH_PANEL_COL_ADMITTANCE, SMITH_PANEL_DEFCOL_ADMITTANCE));
		txtColImpedance.setBackground(config.getColor(SMITH_PANEL_COL_IMPEDANCE, SMITH_PANEL_DEFCOL_IMPEDANCE));

		// circle data
		txtAdmittanceImaginaryCircles.setText(config.getProperty(SMITH_PANEL_CIRCLES_ADMITTANCE_IMAGINARY, DEFAULT_CIRCLES_ADMITTANCE_IMAG));
		txtAdmittanceRealCircles.setText(config.getProperty(SMITH_PANEL_CIRCLES_ADMITTANCE_REAL, DEFAULT_CIRCLES_ADMITTANCE_REAL));
		txtImpedanceImaginaryCircles.setText(config.getProperty(SMITH_PANEL_CIRCLES_IMPEDANCE_IMAGINARY, DEFAULT_CIRCLES_IMPEDANCE_IMAG));
		txtImpedanceRealCircles.setText(config.getProperty(SMITH_PANEL_CIRCLES_IMPEDANCE_REAL, DEFAULT_CIRCLES_IMPEDANCE_REAL));

		txtSWRCircles.setText(config.getProperty(SMITH_PANEL_CIRCLES_SWR, DEFAULT_CIRCLES_SWR));

		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	/**
	 * 
	 */
	protected void doInvert() {
		TraceHelper.entry(this, "doInvert");

		invertBackgroundColor(txtColBackground);
		invertBackgroundColor(txtColData);
		invertBackgroundColor(txtColAdmittance);
		invertBackgroundColor(txtColMarker);
		invertBackgroundColor(txtColText);
		invertBackgroundColor(txtColSWR);

		TraceHelper.exit(this, "doInvert");
	}

	private static void invertBackgroundColor(JTextField tf) {
		tf.setBackground(new Color(~tf.getBackground().getRGB()));
	}

	/**
	 * 
	 */
	protected void doOK() {
		TraceHelper.entry(this, "doOK");

		config.putBoolean(SMITH_PANEL_SHOW_MARKER_MAG, cbMag.isSelected());
		config.putBoolean(SMITH_PANEL_SHOW_MARKER_PHASE, cbPHASE.isSelected());
		config.putBoolean(SMITH_PANEL_SHOW_MARKER_RL, cbRL.isSelected());
		config.putBoolean(SMITH_PANEL_SHOW_MARKER_RS, cbRS.isSelected());
		config.putBoolean(SMITH_PANEL_SHOW_MARKER_XS, cbXS.isSelected());
		config.putBoolean(SMITH_PANEL_SHOW_MARKER_Z, cbZ.isSelected());
		config.putBoolean(SMITH_PANEL_SHOW_MARKER_SWR, cbSWR.isSelected());

		Complex oldRes = config.getSmithReference();
		Complex newRes = referenceResistance.getComplexValue();

		if (!oldRes.equals(newRes)) {
			JOptionPane.showMessageDialog(this, VNAMessages.getString("SmithPanel.1"), getTitle(), JOptionPane.INFORMATION_MESSAGE);
		}
		config.setSmithReference(newRes);

		config.putColor(SMITH_PANEL_COL_TEXT, txtColText.getBackground());
		config.putColor(SMITH_PANEL_COL_DATA, txtColData.getBackground());
		config.putColor(SMITH_PANEL_COL_BACKGROUND, txtColBackground.getBackground());
		config.putColor(SMITH_PANEL_COL_MARKER, txtColMarker.getBackground());
		config.putColor(SMITH_PANEL_COL_SWR, txtColSWR.getBackground());

		config.putColor(SMITH_PANEL_COL_ADMITTANCE, txtColAdmittance.getBackground());
		config.putColor(SMITH_PANEL_COL_IMPEDANCE, txtColImpedance.getBackground());

		config.put(SMITH_PANEL_CIRCLES_ADMITTANCE_IMAGINARY, txtAdmittanceImaginaryCircles.getText());
		config.put(SMITH_PANEL_CIRCLES_ADMITTANCE_REAL, txtAdmittanceRealCircles.getText());

		config.put(SMITH_PANEL_CIRCLES_IMPEDANCE_IMAGINARY, txtImpedanceImaginaryCircles.getText());
		config.put(SMITH_PANEL_CIRCLES_IMPEDANCE_REAL, txtImpedanceRealCircles.getText());

		config.put(SMITH_PANEL_CIRCLES_SWR, txtSWRCircles.getText());

		doDialogCancel();
		TraceHelper.exit(this, "doOK");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		JTextField tf = (JTextField) e.getComponent();
		tf.setBackground(JColorChooser.showDialog(this, VNAMessages.getString("ColorDialog.select"), tf.getBackground()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// not used
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.gui.input.ComplexInputFieldValueChangeListener#valueChanged (org.apache.commons.math.complex.Complex,
	 * org.apache.commons.math.complex.Complex)
	 */
	public void valueChanged(Complex oldValue, Complex newValue) {
		TraceHelper.entry(this, "valueChanged");
		TraceHelper.exit(this, "valueChanged");
	}
}
