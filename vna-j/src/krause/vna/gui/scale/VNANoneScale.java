package krause.vna.gui.scale;

import java.awt.Graphics;
import java.text.NumberFormat;

import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

public class VNANoneScale extends VNAGenericScale {

	public VNANoneScale() {
		super(VNAMessages.getString("Scale.None"), VNAMessages.getString("Scale.None.Description"), SCALE_TYPE.SCALE_NONE, null, NumberFormat.getNumberInstance(), 0, 0);
	}

	@Override
	public int getScaledSampleValue(VNACalibratedSample sample, int height) {
		return -100;
	}

	@Override
	public void initScaleFromConfigOrDib(VNADeviceInfoBlock block, VNAConfig config) {
		// nfa
	}

	@Override
	public void paintScale(int width, int height, Graphics g) {
		// nfa
	}

	@Override
	public int getScaledSampleValue(double value, int height) {
		return 50;
	}
}
