/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.LogManager;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.comparators.VNAPropertyComparator;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.util.SwingUtil;
import krause.vna.gui.util.tables.VNAProperty;
import krause.vna.gui.util.tables.VNAPropertyTableModel;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAConfigEditDialog extends KrauseDialog implements ActionListener {
	private final VNAPropertyTableModel model = new VNAPropertyTableModel();
	private final VNAConfig config = VNAConfig.getSingleton();
	private JCheckBox cbAskOnExit;
	private JCheckBox cbEnableTrace;
	private JTable table;
	private JCheckBox cbAutoAfterSelect;
	private JCheckBox cbAutoAfterZoom;
	private AbstractButton cbMarkerLineMode;
	private JCheckBox cbShowBandmap;
	private JCheckBox cbExportRawData;
	private JCheckBox cbTunGenOffAfterScan;
	private JCheckBox cbDisableResize;

	/**
	 * 
	 * 
	 */
	private void copyConfig2Model() {
		TraceHelper.entry(this, "copyConfig2Model");
		model.getData().clear();
		for (Iterator it = config.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			String value = config.getProperty(key);
			model.addElement(new VNAProperty(key, value));
		}
		Collections.sort(model.getData(), new VNAPropertyComparator());
		TraceHelper.exit(this, "copyConfig2Model");
	}

	/**
	 * 
	 * 
	 */
	private void copyModel2Config() {
		TraceHelper.entry(this, "copyModel2Config");
		List<VNAProperty> data = model.getData();
		for (int i = 0; i < data.size(); ++i) {
			VNAProperty p = data.get(i);
			config.put(p.getKey(), p.getValue());
		}
		for (Iterator it = config.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			String value = config.getProperty(key);
			model.addElement(new VNAProperty(key, value));
		}
		TraceHelper.exit(this, "copyModel2Config");
	}

	/**
	 * @param mainFrame
	 * @param config
	 */
	public VNAConfigEditDialog(VNAMainFrame mainFrame) {
		super(mainFrame.getJFrame(), true);
		TraceHelper.entry(this, "VNAConfigEditDialog");

		setConfigurationPrefix("VNAConfigEditDialog");
		setProperties(config);

		getContentPane().setLayout(new BorderLayout());
		setModal(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 678, 472);
		setTitle(VNAMessages.getString("VNAConfigEditDialog.this.title")); //$NON-NLS-1$

		// Add the scroll pane to this panel.
		JLabel label = new JLabel(VNAMessages.getString("Dlg.Settings.1"));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				table.setEnabled(true);
			}
		});
		getContentPane().add(label, BorderLayout.PAGE_START);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new MigLayout("", "[][][][]", "[][]"));
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		//
		table = new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(600, 400));
		table.setEnabled(false);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);
		centerPanel.add(scrollPane, "span 4, wrap");

		cbAskOnExit = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbAskOnExit.text")); //$NON-NLS-1$
		cbAskOnExit.addActionListener(e -> {
			config.setAskOnExit(cbAskOnExit.isSelected());
			copyConfig2Model();
		});
		centerPanel.add(cbAskOnExit, "");
		//
		cbEnableTrace = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbEnableTrace.text")); //$NON-NLS-1$
		cbEnableTrace.addActionListener(e -> {
			config.putBoolean(LogManager.TRACER_ENABLE, cbEnableTrace.isSelected());
			LogManager.getSingleton().setTracingEnabled(cbEnableTrace.isSelected());
			copyConfig2Model();
		});
		centerPanel.add(cbEnableTrace, "");
		//
		cbExportRawData = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbExportRawData.text")); //$NON-NLS-1$
		cbExportRawData.addActionListener(e -> {
			config.setExportRawData(cbExportRawData.isSelected());
			copyConfig2Model();
		});
		centerPanel.add(cbExportRawData, "wrap");

		cbMarkerLineMode = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbMarkerLineMode"));
		cbMarkerLineMode.addActionListener(e -> {
			config.setMarkerModeLine(cbMarkerLineMode.isSelected());
			copyConfig2Model();
		});
		centerPanel.add(cbMarkerLineMode, "");

		cbTunGenOffAfterScan = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbTunGenOffAfterScan"));
		cbTunGenOffAfterScan.addActionListener(e -> {
			config.setTurnOffGenAfterScan(cbTunGenOffAfterScan.isSelected());
			copyConfig2Model();
		});
		centerPanel.add(cbTunGenOffAfterScan, "");

		cbShowBandmap = new JCheckBox(VNAMessages.getString("VNAConfigEditDialog.cbShowBandmap"));
		cbShowBandmap.addActionListener(e -> {
			config.setShowBandmap(cbShowBandmap.isSelected());
			copyConfig2Model();
		});
		centerPanel.add(cbShowBandmap, "wrap");

		//
		cbAutoAfterSelect = SwingUtil.createJCheckBox("Panel.Data.cbAutoAfterSelect", this);
		cbAutoAfterSelect.addActionListener(e -> {
			config.setScanAfterTableSelect(cbAutoAfterSelect.isSelected());
			copyConfig2Model();
		});
		centerPanel.add(cbAutoAfterSelect, "");

		//
		cbAutoAfterZoom = SwingUtil.createJCheckBox("Panel.Data.cbAutoAfterZoom", this);
		cbAutoAfterZoom.addActionListener(e -> {
			config.setScanAfterZoom(cbAutoAfterZoom.isSelected());
			copyConfig2Model();
		});
		centerPanel.add(cbAutoAfterZoom, "");

		//
		cbDisableResize = SwingUtil.createJCheckBox("Panel.Data.cbDisableResize", this);
		cbDisableResize.addActionListener(e -> {
			config.setResizeLocked(cbDisableResize.isSelected());
			copyConfig2Model();
		});
		centerPanel.add(cbDisableResize, "wrap");

		//
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(SwingUtil.createJButton("VNAConfigEditDialog.butShowConfigDir", this));
		buttonPanel.add(SwingUtil.createJButton("Button.Cancel", this));
		buttonPanel.add(SwingUtil.createJButton("Button.Save", this));
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		//
		doDialogInit();
		TraceHelper.exit(this, "VNAConfigEditDialog");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		TraceHelper.entry(this, "actionPerformed");
		if ("cmdSAVE".equals(cmd)) {
			doSave();
		} else if ("cmdCancel".equals(cmd)) {
			doDialogCancel();
		} else if ("cmdConfig".equals(cmd)) {
			File file = new File(config.getVNAConfigDirectory());
			try {
				java.awt.Desktop.getDesktop().open(file);
			} catch (IOException e1) {
				ErrorLogHelper.exception(this, "actionPerformed", e1);
			}
		}

		TraceHelper.exit(this, "actionPerformed");
	}

	/**
	 * 
	 */
	private void doSave() {
		TraceHelper.entry(this, "doSave");
		copyModel2Config();
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doSave");
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		cbAskOnExit.setSelected(config.isAskOnExit());
		cbAutoAfterSelect.setSelected(config.isScanAfterTableSelect());
		cbAutoAfterZoom.setSelected(config.isScanAfterZoom());
		cbEnableTrace.setSelected(config.getBoolean(LogManager.TRACER_ENABLE, false));
		cbMarkerLineMode.setSelected(config.isMarkerModeLine());
		cbShowBandmap.setSelected(config.isShowBandmap());
		cbExportRawData.setSelected(config.isExportRawData());
		cbDisableResize.setSelected(config.isResizeLocked());
		cbTunGenOffAfterScan.setSelected(config.isTurnOffGenAfterScan());
		//
		copyConfig2Model();
		addEscapeKey();
		doDialogShow();
	}
}
