/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNAPresetLoadDialog.java
 *  Part of:   vna-j
 */

package krause.vna.gui.preset;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.jfree.ui.ExtensionFileFilter;

import krause.common.TypedProperties;
import krause.common.gui.KrauseDialog;
import krause.util.PropertiesHelper;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.VNAScaleCheckBox;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * @author Dietmar
 * 
 */
public class VNAPresetSaveDialog extends KrauseDialog {
	private VNAMainFrame mainFrame = null;
	private VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private JCheckBox cbFreq;
	private JCheckBox cbScanMode;

	private ArrayList<VNAScaleCheckBox> lstScales = new ArrayList<VNAScaleCheckBox>();
	private JCheckBox cbMarkers;

	public final static String PREFS_EXTENSION = ".preset";
	public final static String PREFS_DESCRIPTION = "vna/J preset files(*" + PREFS_EXTENSION + ")";

	public VNAPresetSaveDialog(VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);
		TraceHelper.entry(this, "VNAPresetSaveDialog");
		mainFrame = pMainFrame;
		setConfigurationPrefix("VNAPresetSaveDialog");
		setProperties(config);
		setMinimumSize(new Dimension(350, 300));
		setPreferredSize(getMinimumSize());

		setTitle(VNAMessages.getString("VNAPresetSaveDialog.title"));
		//
		getContentPane().setLayout(new BorderLayout());

		//
		JPanel pnlButtons = new JPanel();
		pnlButtons.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);

		//
		pnlButtons.add(new HelpButton(this, "VNAPresetSaveDialog"));

		//
		JButton btn = new JButton(VNAMessages.getString("Button.Close"));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDialogCancel();
			}
		});
		pnlButtons.add(btn);

		pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		btn = SwingUtil.createJButton("Button.Save", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doSAVE();
			}
		});
		pnlButtons.add(btn);

		getRootPane().setDefaultButton(btn);

		//
		JPanel dataPanel = new JPanel();
		dataPanel.setLayout(new MigLayout("", "[grow][][][][]", "[]"));
		getContentPane().add(createDataPanel(), BorderLayout.CENTER);

		//
		doDialogInit();
		TraceHelper.entry(this, "VNAPresetSaveDialog");
	}

	/**
	 * @return
	 */
	private JPanel createDataPanel() {
		TraceHelper.entry(this, "createDataPanel");
		//
		JPanel rc = new JPanel();
		rc.setLayout(new MigLayout("", "[grow]", "[][][]"));

		//
		rc.add(new JLabel(VNAMessages.getString("VNAPresetSaveDialog.help")), "grow,wrap");

		//
		JPanel pnlScales = new JPanel(new GridLayout(4, 4));
		pnlScales.setBorder(new TitledBorder(null, VNAMessages.getString("VNAPresetSaveDialog.scales"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		for (VNAGenericScale currScale : VNAScaleSymbols.MAP_SCALE_TYPES.values()) {
			if (currScale.supportsCustomScaling()) {
				VNAScaleCheckBox cb = new VNAScaleCheckBox(currScale.getName(), currScale);
				cb.setSelected(true);
				lstScales.add(cb);
				pnlScales.add(cb);
			}
		}
		rc.add(pnlScales, "wrap,grow");

		//
		cbFreq = new JCheckBox(VNAMessages.getString("VNAPresetSaveDialog.frequency"));
		cbFreq.setSelected(true);
		rc.add(cbFreq, "wrap,grow");

		//
		cbScanMode = new JCheckBox(VNAMessages.getString("VNAPresetSaveDialog.scanmode"));
		cbScanMode.setSelected(true);
		rc.add(cbScanMode, "wrap,grow");

		//
		cbMarkers = new JCheckBox(VNAMessages.getString("VNAPresetSaveDialog.markers"));
		cbMarkers.setSelected(true);
		rc.add(cbMarkers, "wrap,grow");

		TraceHelper.exit(this, "createDataPanel");
		return rc;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	/*
	 * 
	 */
	private void doSAVE() {
		TraceHelper.entry(this, "doSAVE");
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new ExtensionFileFilter(PREFS_DESCRIPTION, PREFS_EXTENSION));
		fc.setCurrentDirectory(new File(config.getPresetsDirectory()));
		int returnVal = fc.showSaveDialog(mainFrame.getJFrame());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// uses presses SAVE
			try {
				File file = fc.getSelectedFile();

				config.setPresetsDirectory(file.getParent());
				// file has correct extension?
				if (!file.getName().endsWith(PREFS_EXTENSION)) {
					// no
					// append extension
					file = new File(file.getAbsolutePath() + PREFS_EXTENSION);
				}

				// file already there?
				if (file.exists()) {
					// yes
					// ask for overwrite?
					String msg = MessageFormat.format(VNAMessages.getString("VNAPresetSaveDialog.save.1"), file.getName());
					int response = JOptionPane.showOptionDialog(mainFrame.getJFrame(), msg, VNAMessages //$NON-NLS-1$
							.getString("VNAPresetSaveDialog.title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, //$NON-NLS-1$
							null);
					// user selects overwrite?
					if (response == JOptionPane.CANCEL_OPTION)
						// no
						// end
						return;
				}

				TypedProperties props = createProperties();
				PropertiesHelper.saveXMLProperties(props, file.getAbsolutePath());
				doDialogCancel();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(mainFrame.getJFrame(), e.getMessage(), VNAMessages.getString("Message.Export.2"), JOptionPane.ERROR_MESSAGE);
				ErrorLogHelper.exception(this, "doExport", e);
			}
		}
		TraceHelper.exit(this, "doSAVE");
	}

	/**
	 * @return
	 */
	private TypedProperties createProperties() {
		TraceHelper.entry(this, "createProperties");

		TypedProperties rc = new TypedProperties();
		for (VNAScaleCheckBox cb : lstScales) {
			if (cb.getScale().supportsCustomScaling()) {
				if (cb.isSelected()) {
					cb.getScale().saveToProperties(rc);
				}
			}
		}
		if (cbFreq.isSelected()) {
			datapool.getFrequencyRange().saveToProperties(rc);
		}
		if (cbScanMode.isSelected()) {
			datapool.getScanMode().saveToProperties(rc);
		}
		if (cbMarkers.isSelected()) {
			VNAMarkerPanel mp = mainFrame.getMarkerPanel();
			VNAMarker m;
			//
			m = mp.getMarker(0);
			if (m.isVisible()) {
				rc.putLong("Marker1.frq", m.getFrequency());
			}
			m = mp.getMarker(1);
			if (m.isVisible()) {
				rc.putLong("Marker2.frq", m.getFrequency());
			}
			m = mp.getMarker(2);
			if (m.isVisible()) {
				rc.putLong("Marker3.frq", m.getFrequency());
			}
			m = mp.getMarker(3);
			if (m.isVisible()) {
				rc.putLong("Marker4.frq", m.getFrequency());
			}
		}
		TraceHelper.exit(this, "createProperties");
		return rc;
	}
}
