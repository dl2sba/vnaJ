package krause.vna.data.presets;

import java.awt.Graphics;
import java.io.File;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jfree.ui.ExtensionFileFilter;

import krause.common.TypedProperties;
import krause.util.PropertiesHelper;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.data.VNAScanModeComboBox;
import krause.vna.gui.preset.VNAPresetSaveDialog;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.scale.VNAScaleSymbols;
import krause.vna.resources.VNAMessages;

public class VNAPresetHelper {

	private static VNAConfig config = VNAConfig.getSingleton();

	private VNAMainFrame mainFrame;

	public VNAPresetHelper(VNAMainFrame pMF) {
		TraceHelper.exit(this, "VNAPresetHelper");
		mainFrame = pMF;
		TraceHelper.exit(this, "VNAPresetHelper");
	}

	/**
	 * @param mainFrame2
	 * 
	 */
	public void processScanMode(TypedProperties properties) {
		TraceHelper.exit(this, "processScanMode");

		VNAScanMode tsm = new VNAScanMode();
		tsm.restoreFromProperties(properties);
		if (tsm.getMode() != VNAScanMode.MODENUM_UNKNOWN) {
			VNAScanModeComboBox cbm = mainFrame.getDataPanel().getCbMode();
			cbm.setSelectedMode(tsm);
			mainFrame.getApplicationState().evtScanModeChanged();
		}
		TraceHelper.exit(this, "processScanMode");
	}

	/**
	 * 
	 */
	public void processFrequencyRange(TypedProperties properties) {
		TraceHelper.entry(this, "processFrequencyRange");

		VNAFrequencyRange fr = new VNAFrequencyRange();
		fr.restoreFromProperties(properties);
		if (fr.isValid()) {
			mainFrame.getDataPanel().changeFrequencyRange(fr);
		}
		TraceHelper.exit(this, "processFrequencyRange");
	}

	/**
	 * 
	 */
	public void processScales(TypedProperties properties) {
		TraceHelper.entry(this, "processScales");
		for (VNAGenericScale currScale : VNAScaleSymbols.MAP_SCALE_TYPES.values()) {
			if (currScale.supportsCustomScaling()) {
				VNAGenericScale genScale = new VNAGenericScale(null, null, currScale.getType(), null, null, 0, 0) {

					@Override
					public void paintScale(int width, int height, Graphics g) {
						// nfa
					}

					@Override
					public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
						// nfa
					}

					@Override
					public int getScaledSampleValue(VNACalibratedSample sample, int height) {
						return 0;
					}

					@Override
					public int getScaledSampleValue(double value, int height) {
						return 0;
					}
				};
				genScale.restoreFromProperties(properties);
				if ((genScale.getCurrentMaxValue() != Double.MAX_VALUE) && (genScale.getCurrentMinValue() != Double.MIN_VALUE)) {
					currScale.setCurrentMinMaxValue(genScale.getCurrentMinMaxValue());
					currScale.rescale();
				}
			}
		}
		mainFrame.getDiagramPanel().getScaleSelectPanel().disableAutoScale();
		TraceHelper.exit(this, "processScales");
	}

	public void doLoadPresets() {
		TraceHelper.entry(this, "doLoadPresets");
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new ExtensionFileFilter(VNAPresetSaveDialog.PREFS_DESCRIPTION, VNAPresetSaveDialog.PREFS_EXTENSION));
		fc.setSelectedFile(new File(config.getPresetsDirectory() + "/."));
		int returnVal = fc.showOpenDialog(mainFrame.getJFrame());

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				Properties props = PropertiesHelper.loadXMLProperties(file.getAbsolutePath(), null);
				TypedProperties tProps = new TypedProperties();
				tProps.putAll(props);

				processMarkers(tProps);
				processScanMode(tProps);
				processFrequencyRange(tProps);
				processScales(tProps);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(mainFrame.getJFrame(), e.getMessage(), VNAMessages.getString("Message.Export.2"), JOptionPane.ERROR_MESSAGE);
				ErrorLogHelper.exception(this, "doLoadPresets", e);
			}
		}
		TraceHelper.exit(this, "doLoadPresets");

	}

	private void processMarkers(TypedProperties tProps) {
		TraceHelper.entry(this, "processMarkers");
		long freq1 = tProps.getLong("Marker1.frq", 0);
		if (freq1 != 0) {
			mainFrame.getMarkerPanel().getMarker(0).moveMarkerToFrequency(freq1);
		}
		long freq2 = tProps.getLong("Marker2.frq", 0);
		if (freq2 != 0) {
			mainFrame.getMarkerPanel().getMarker(1).moveMarkerToFrequency(freq2);
		}
		long freq3 = tProps.getLong("Marker3.frq", 0);
		if (freq3 != 0) {
			mainFrame.getMarkerPanel().getMarker(2).moveMarkerToFrequency(freq3);
		}
		long freq4 = tProps.getLong("Marker4.frq", 0);
		if (freq4 != 0) {
			mainFrame.getMarkerPanel().getMarker(3).moveMarkerToFrequency(freq4);
		}
		TraceHelper.exit(this, "processMarkers");
	}

	public void doSavePresets() {
		TraceHelper.entry(this, "doSavePresets");
		new VNAPresetSaveDialog(mainFrame);
		TraceHelper.exit(this, "doSavePresets");
	}
}
