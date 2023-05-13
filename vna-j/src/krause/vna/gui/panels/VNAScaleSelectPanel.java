package krause.vna.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAApplicationState.INNERSTATE;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.VNATemperatureButton;
import krause.vna.gui.calibrate.VNACalibrationDataDetailsDialog;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.portextension.VNAPortExtensionParameterDialog;
import krause.vna.gui.reference.VNAReferenceDataLoadDialog;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAMeasurementScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.gui.smith.VNASyncedSmithDiagramDialog;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAScaleSelectPanel extends JPanel implements ActionListener, VNAApplicationStateObserver {
	private JButton buttonPortExtension;
	private JButton buttonPortExtensionActive;

	private JButton buttonPortExtensionInactive;
	private JButton buttonReference;
	private JButton buttonReferenceLoaded;
	private JButton buttonReferenceNotLoaded;
	private JCheckBox cbAutoScale;
	private VNAScaleSelectComboBox cbLeftScale;
	private VNAScaleSelectComboBox cbRightScale;
	private JToggleButton cbSmith;
	private final VNAConfig config = VNAConfig.getSingleton();
	private final transient VNADataPool datapool = VNADataPool.getSingleton();
	private JLabel labelMemory;
	private JLabel labelPowerstatus;
	private VNATemperatureButton labelTemperature;
	private transient VNAMainFrame mainFrame;
	private VNASyncedSmithDiagramDialog smithDialog;

	public VNAScaleSelectPanel(VNAMainFrame pMainFrame, VNAMeasurementScale leftScale, VNAMeasurementScale rightScale) {
		TraceHelper.entry(this, "VNAScaleSelectPanel");
		mainFrame = pMainFrame;
		setLayout(new BorderLayout());

		cbLeftScale = new VNAScaleSelectComboBox();
		cbLeftScale.setToolTipText(VNAMessages.getString("Panel.Scale.Left"));

		cbRightScale = new VNAScaleSelectComboBox();
		cbRightScale.setToolTipText(VNAMessages.getString("Panel.Scale.Right"));

		cbAutoScale = SwingUtil.createJCheckBox("Panel.Scale.AutoScale", this);
		cbAutoScale.setSelected(config.isAutoscaleEnabled());

		cbLeftScale.setSelectedItem(leftScale.getScale());
		cbRightScale.setSelectedItem(rightScale.getScale());

		cbLeftScale.addActionListener(this);
		cbRightScale.addActionListener(this);

		add(cbLeftScale, BorderLayout.WEST);
		JToolBar pnlX = new JToolBar();
		// not movable
		pnlX.setFloatable(false);
		pnlX.add(cbAutoScale);
		pnlX.addSeparator();

		cbSmith = SwingUtil.createToggleButton("Panel.Scale.Smith", this);
		pnlX.add(cbSmith);
		pnlX.addSeparator();

		//
		buttonReference = SwingUtil.createToolbarButton("Button.Reference.NotLoaded", this);
		buttonReferenceLoaded = SwingUtil.createToolbarButton("Button.Reference.Loaded", this);
		buttonReferenceNotLoaded = SwingUtil.createToolbarButton("Button.Reference.NotLoaded", this);
		pnlX.add(buttonReference);
		pnlX.addSeparator();

		//
		buttonPortExtension = SwingUtil.createToolbarButton("Button.PortExtension.Inactive", this);
		buttonPortExtensionActive = SwingUtil.createToolbarButton("Button.PortExtension.Active", this);
		buttonPortExtensionInactive = SwingUtil.createToolbarButton("Button.PortExtension.Inactive", this);
		pnlX.add(buttonPortExtension);
		pnlX.addSeparator();

		// labelPowerstatus = new BatteryButton("Panel.Scale.Powerlabel", this)
		labelPowerstatus = new JLabel();
		labelPowerstatus.setVisible(false);
		pnlX.add(labelPowerstatus);

		labelTemperature = new VNATemperatureButton(mainFrame, "Panel.Scale.Templabel", null);
		labelTemperature.setVisible(false);
		pnlX.add(labelTemperature);

		pnlX.addSeparator();
		labelMemory = new JLabel();
		labelMemory.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (datapool.getRawData() != null) {
					VNASampleBlock blk = new VNASampleBlock();
					blk.setSamples(datapool.getRawData().getSamples());
					new VNACalibrationDataDetailsDialog(mainFrame.getJFrame(), blk, "Panel.Scale.RawData");
				}
			}
		});
		pnlX.add(labelMemory);

		//
		add(pnlX, BorderLayout.CENTER);
		add(cbRightScale, BorderLayout.EAST);

		setupColors();
		setPortExtensionState();

		TraceHelper.exit(this, "VNAScaleSelectPanel");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		final String methodName = "actionPerformed";
		TraceHelper.entry(this, methodName, cmd);
		VNADiagramPanel dp = mainFrame.getDiagramPanel();
		if (e.getSource() == cbLeftScale) {
			VNAGenericScale st = (VNAGenericScale) cbLeftScale.getSelectedItem();
			dp.getScaleLeft().setScale(st);
			dp.repaint();
		} else if (e.getSource() == cbRightScale) {
			VNAGenericScale st = (VNAGenericScale) cbRightScale.getSelectedItem();
			dp.getScaleRight().setScale(st);
			dp.repaint();
		} else if (e.getSource() == cbSmith) {
			doHandleSmithDiagram();
		} else if (e.getSource() == buttonReference) {
			doHandleReference();
		} else if (e.getSource() == buttonPortExtension) {
			doHandlePortExtension();
		} else if (e.getSource() == cbAutoScale) {
			doHandleAutoScale();
		}
		TraceHelper.exit(this, methodName);
	}

	public void changeState(INNERSTATE oldState, INNERSTATE newState) {
		cbSmith.setEnabled((newState != INNERSTATE.RUNNING) && (datapool.getScanMode() != null) && (datapool.getScanMode().isReflectionMode()));
		buttonReference.setEnabled(newState != INNERSTATE.RUNNING);
		buttonPortExtension.setEnabled((newState != INNERSTATE.RUNNING) && (datapool.getScanMode() != null) && (datapool.getScanMode().isReflectionMode()));

		final VNASampleBlock rawData = datapool.getRawData();

		// data present ?
		if (rawData != null) {
			// yes
			// get voltage
			final Double volt = rawData.getDeviceSupply();
			if (volt != null) {
				labelPowerstatus.setVisible(true);
				labelPowerstatus.setText(VNAFormatFactory.getTemperatureFormat().format(volt) + " V  ");
			} else {
				labelPowerstatus.setVisible(false);
			}

			// get temperature
			final Double temp = rawData.getDeviceTemperature();
			labelTemperature.setVisible(temp != null);
			labelTemperature.setTemperature(temp);
		}
	}

	public void disableAutoScale() {
		//
		cbAutoScale.setSelected(false);
		config.setAutoscaleEnabled(false);

		//
		VNADiagramPanel diagPanel = mainFrame.getDiagramPanel();
		diagPanel.repaint();
	}

	private void doHandleAutoScale() {
		TraceHelper.entry(this, "doHandleAutoScale");
		VNADiagramPanel diagPanel = mainFrame.getDiagramPanel();

		if (!cbAutoScale.isSelected()) {
			for (VNAGenericScale scale : VNAScaleSymbols.MAP_SCALE_TYPES.values()) {
				scale.resetDefault();
			}
		} else {
			diagPanel.rescaleScalesToData();
		}
		config.setAutoscaleEnabled(cbAutoScale.isSelected());
		diagPanel.repaint();
		TraceHelper.exit(this, "doHandleAutoScale");
	}

	/**
	 * 
	 */
	public void doHandlePortExtension() {
		TraceHelper.entry(this, "doHandlePortExtension");
		if (buttonPortExtension.isEnabled()) {
			new VNAPortExtensionParameterDialog(mainFrame);
			mainFrame.getDiagramPanel().clearScanData();
		}
		setPortExtensionState();
		mainFrame.getDiagramPanel().getImagePanel().updateUI();
		TraceHelper.exit(this, "doHandlePortExtension");
	}

	/**
	 * 
	 */
	public void doHandleReference() {
		TraceHelper.entry(this, "doHandleReference");
		if (buttonReference.isEnabled()) {
			new VNAReferenceDataLoadDialog(mainFrame.getJFrame());
			if (datapool.getReferenceData() != null) {
				buttonReference.setIcon(buttonReferenceLoaded.getIcon());
				mainFrame.getStatusBarStatus().setText(VNAMessages.getString("Panel.Scale.Reference.ReferenceLoaded"));
			} else {
				buttonReference.setIcon(buttonReferenceNotLoaded.getIcon());
				mainFrame.getStatusBarStatus().setText(VNAMessages.getString("Panel.Scale.Reference.ReferenceCleared"));
			}
			mainFrame.getDiagramPanel().getImagePanel().updateUI();
		}
		TraceHelper.exit(this, "doHandleReference");
	}

	/**
	 * shows or hides the dynamic smith chart dialog
	 */
	public void doHandleSmithDiagram() {
		TraceHelper.entry(this, "doHandleSmithDiagram");

		if (smithDialog == null) {
			smithDialog = new VNASyncedSmithDiagramDialog(mainFrame);
			if (datapool.getCalibratedData() != null) {
				smithDialog.consumeCalibratedData(datapool.getCalibratedData());
			}
			smithDialog.setVisible(true);
			cbSmith.setSelected(true);
		} else {
			smithDialog.setVisible(false);
			smithDialog.dispose();
			smithDialog = null;
			cbSmith.setSelected(false);
		}
		TraceHelper.exit(this, "doHandleSmithDiagram");
	}

	public void enableAutoScale() {
		if (!cbAutoScale.isSelected()) {
			cbAutoScale.setSelected(true);
		}
	}

	public VNAScaleSelectComboBox getCbLeftScale() {
		return cbLeftScale;
	}

	public VNAScaleSelectComboBox getCbRightScale() {
		return cbRightScale;
	}

	public JLabel getLabelDebug() {
		return labelMemory;
	}

	public VNASyncedSmithDiagramDialog getSmithDialog() {
		return smithDialog;
	}

	/**
	 * calculate the state of port extension
	 * 
	 * @return true if port extension is active
	 */
	boolean setPortExtensionState() {
		if (config.isPortExtensionEnabled()) {
			buttonPortExtension.setIcon(buttonPortExtensionActive.getIcon());
			buttonPortExtension.setToolTipText(buttonPortExtensionActive.getToolTipText());
			return true;
		} else {
			buttonPortExtension.setIcon(buttonPortExtensionInactive.getIcon());
			buttonPortExtension.setToolTipText(buttonPortExtensionInactive.getToolTipText());
			return false;
		}
	}

	public void setSmithDialog(VNASyncedSmithDiagramDialog smithDialog) {
		this.smithDialog = smithDialog;
	}

	public void setupColors() {
		final String methodName = "setupColors";
		TraceHelper.entry(this, methodName);

		cbRightScale.setBackground(new Color(~(config.getColorScaleRight().getRGB())));
		cbRightScale.setForeground(config.getColorScaleRight());

		cbLeftScale.setBackground(new Color(~(config.getColorScaleLeft().getRGB())));
		cbLeftScale.setForeground(config.getColorScaleLeft());

		TraceHelper.exit(this, methodName);
	}
}
