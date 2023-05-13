package krause.vna.gui.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.laf.VNALookAndFeelEntry;
import krause.vna.gui.laf.VNALookAndFeelHelper;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAColorConfigDialog extends KrauseDialog {

	private VNAConfig config = VNAConfig.getSingleton();
	private JComboBox<VNALookAndFeelEntry> cbTheme;

	/**
	 * 
	 * @param mainFrame
	 * @param aFrame
	 */
	public VNAColorConfigDialog(VNAMainFrame mainFrame, Frame aFrame) {
		super(aFrame, true);
		setConfigurationPrefix("ColorDialog");
		setProperties(config);
		setTitle(VNAMessages.getString("ColorDialog.Title"));

		setPreferredSize(new Dimension(410, 370));

		getContentPane().setLayout(new BorderLayout(5, 5));

		JPanel pnlButtons = new JPanel();
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		JButton btnDefault = SwingUtil.createJButton("Button.Default", e -> doResetDefaults());
		pnlButtons.add(btnDefault);

		JButton btnOK = SwingUtil.createJButton("Button.Close", e -> doDialogCancel());
		pnlButtons.add(btnOK);

		//
		JPanel pnlCenter = new JPanel(new MigLayout("", "", ""));
		getContentPane().add(pnlCenter, BorderLayout.CENTER);

		//
		JPanel pnlScales = new JPanel();
		pnlScales.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("ColorDialog.scales"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlCenter.add(pnlScales, "wrap,grow");
		JButton btnLeftScale = SwingUtil.createJButton("ColorDialog.Title.LeftScale", null);
		btnLeftScale.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(getOwner(), VNAMessages.getString("ColorDialog.select"), config.getColorScaleLeft());
			if (newColor != null) {
				config.setColorScaleLeft(newColor);
			}
		});
		pnlScales.add(btnLeftScale);

		JButton btnRightScale = SwingUtil.createJButton("ColorDialog.Title.RightScale", null);
		btnRightScale.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(getOwner(), VNAMessages.getString("ColorDialog.select"), config.getColorScaleRight());
			if (newColor != null) {
				config.setColorScaleRight(newColor);
			}
		});
		pnlScales.add(btnRightScale);

		//
		JPanel pnlMarkers = new JPanel();
		pnlMarkers.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("ColorDialog.markers"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlCenter.add(pnlMarkers, "grow,wrap");
		JButton btnMarker = SwingUtil.createJButton("Marker." + VNAMarkerPanel.MARKER_0, null);
		btnMarker.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(getOwner(), "Color", config.getColorMarker(VNAMarkerPanel.MARKER_0));
			if (newColor != null) {
				config.setColorMarker(VNAMarkerPanel.MARKER_0, newColor);
			}
		});
		pnlMarkers.add(btnMarker);

		//
		btnMarker = SwingUtil.createJButton("Marker." + VNAMarkerPanel.MARKER_1, null);
		btnMarker.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(getOwner(), "Color", config.getColorMarker(VNAMarkerPanel.MARKER_1));
			if (newColor != null) {
				config.setColorMarker(VNAMarkerPanel.MARKER_1, newColor);
			}
		});
		pnlMarkers.add(btnMarker);

		//
		btnMarker = SwingUtil.createJButton("Marker." + VNAMarkerPanel.MARKER_2, null);
		btnMarker.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(getOwner(), "Color", config.getColorMarker(VNAMarkerPanel.MARKER_2));
			if (newColor != null) {
				config.setColorMarker(VNAMarkerPanel.MARKER_2, newColor);
			}
		});
		pnlMarkers.add(btnMarker);

		//
		btnMarker = SwingUtil.createJButton("Marker." + VNAMarkerPanel.MARKER_3, null);
		btnMarker.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(getOwner(), "Color", config.getColorMarker(VNAMarkerPanel.MARKER_3));
			if (newColor != null) {
				config.setColorMarker(VNAMarkerPanel.MARKER_3, newColor);
			}
		});
		pnlMarkers.add(btnMarker);

		//
		JPanel pnlDiag = new JPanel();
		pnlDiag.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("ColorDialog.diagram"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlCenter.add(pnlDiag, "wrap");

		JButton btnDiagramBackground = SwingUtil.createJButton("ColorDialog.Title.DiagramBackground", null);
		btnDiagramBackground.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(getOwner(), "Color", config.getColorDiagram());
			if (newColor != null) {
				config.setColorDiagram(newColor);
			}
		});

		JButton btnDiagramLines = SwingUtil.createJButton("ColorDialog.Title.DiagramLines", null);
		btnDiagramLines.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(getOwner(), "Color", config.getColorDiagramLines());
			if (newColor != null) {
				config.setColorDiagramLines(newColor);
			}
		});
		JButton btnReferenceColor = SwingUtil.createJButton("ColorDialog.Title.Reference", null);
		btnReferenceColor.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(getOwner(), VNAMessages.getString("ColorDialog.select"), config.getColorReference());
			if (newColor != null) {
				config.setColorReference(newColor);
			}
		});

		JButton btnBandmapColor = SwingUtil.createJButton("ColorDialog.Title.Bandmap", null);
		btnBandmapColor.addActionListener(e -> {
			Color newColor = JColorChooser.showDialog(getOwner(), VNAMessages.getString("ColorDialog.select"), config.getColorBandmap());
			if (newColor != null) {
				config.setColorBandmap(newColor);
			}
		});

		pnlDiag.add(btnReferenceColor);
		pnlDiag.add(btnDiagramLines);
		pnlDiag.add(btnDiagramBackground);
		pnlDiag.add(btnBandmapColor);

		//
		JPanel pnlScheme = new JPanel();
		pnlScheme.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("ColorDialog.scheme"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlCenter.add(pnlScheme, "grow,wrap");

		cbTheme = new JComboBox<>(new VNALookAndFeelHelper().getThemeList());
		pnlScheme.add(cbTheme, "grow,wrap");
		cbTheme.addItemListener(e -> {
			int idx = cbTheme.getSelectedIndex();
			if (idx != -1) {
				config.setThemeID(idx);
			}
		});
		doDialogInit();
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		cbTheme.setSelectedIndex(config.getThemeID());
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	private void doResetDefaults() {
		TraceHelper.entry(this, "doResetDefaults");
		config.setColorDiagram(Color.BLACK);
		config.setColorDiagramLines(Color.LIGHT_GRAY);
		config.setColorMarker(VNAMarkerPanel.MARKER_0, Color.YELLOW);
		config.setColorMarker(VNAMarkerPanel.MARKER_1, Color.YELLOW);
		config.setColorMarker(VNAMarkerPanel.MARKER_2, Color.YELLOW);
		config.setColorMarker(VNAMarkerPanel.MARKER_3, Color.YELLOW);
		config.setColorScaleLeft(Color.GREEN);
		config.setColorScaleRight(Color.CYAN);
		config.setColorBandmap(Color.DARK_GRAY);
		cbTheme.setSelectedIndex(0);
		TraceHelper.exit(this, "doResetDefaults");
	}
}
