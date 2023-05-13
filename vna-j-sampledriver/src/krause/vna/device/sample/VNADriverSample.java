/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.sample;

import java.util.ArrayList;
import java.util.List;

import krause.common.TypedProperties;
import krause.common.exception.DialogNotImplementedException;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNAGenericDriver;
import krause.vna.gui.OptionDialogHelper;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;

public class VNADriverSample extends VNAGenericDriver {
	public VNADriverSample() {
		final String methodName = "VNADriverSample";
		TraceHelper.entry(this, methodName);

		setMathHelper(new VNADriverSampleMathHelper(this));
		setDeviceInfoBlock(new VNADriverSampleDIB());
		//
		getDeviceInfoBlock().restore(this.config, getDriverConfigPrefix());
		TraceHelper.exit(this, methodName);
	}

	public long calculateInternalFrequencyValue(long frequency) {
		TraceHelper.entry(this, "calculateInternalFrequencyValue", "in=" + frequency);
		long rc = frequency;
		TraceHelper.exitWithRC(this, "calculateInternalFrequencyValue", rc);
		return rc;
	}

	public void destroy() {
		TraceHelper.entry(this, "destroy");
		TraceHelper.exit(this, "destroy");
	}

	@Override
	public String getDeviceFirmwareInfo() {
		return "Sample Driver V1.0";
	}

	@Override
	public Double getDeviceSupply() {
		return 5.0;
	}

	public String getDriverConfigPrefix() {
		return "VNADriver.Sample.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.serial.VNADriverIF#getPortList()
	 */
	public List<String> getPortList() {
		List<String> rc = new ArrayList<String>();
		rc.add("DummySamplePort");
		return rc;
	}

	@Override
	public VNACalibrationRange[] getSpecificCalibrationRanges() {
		VNACalibrationRange[] rc = null;
		long min = getDeviceInfoBlock().getMinFrequency();
		long max = getDeviceInfoBlock().getMaxFrequency();
		rc = new VNACalibrationRange[] {
				new VNACalibrationRange(min, max, 20000, 1)
		};
		return rc;
	}

	public void init() throws InitializationException {
		super.init();
		TraceHelper.entry(this, "init");
		getDeviceInfoBlock().restore(config, getDriverConfigPrefix());
		TraceHelper.exit(this, "init");
	}

	/**
	 * 
	 */
	public void init(TypedProperties vnaConfig) throws InitializationException {
		TraceHelper.entry(this, "init");
		TraceHelper.exit(this, "init");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#isScanSupported(int, krause.vna.data.VNAFrequencyRange, krause.vna.data.VNAScanMode)
	 */
	public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#scan(krause.vna.data.VNAScanMode, long, long, int,
	 * krause.vna.background.IVNABackgroundTaskStatusListener)
	 */
	public VNASampleBlock scan(VNAScanMode scanMode, long frequencyLow, long frequencyHigh, int samples, IVNABackgroundTaskStatusListener listener) {
		TraceHelper.entry(this, "scan");
		VNASampleBlock rc = new VNASampleBlock();
		rc.setAnalyserType(getDeviceInfoBlock().getType());

		long freqSteps = (frequencyHigh - frequencyLow) / samples;

		rc.setNumberOfSteps(samples);
		rc.setStartFrequency(frequencyLow);
		rc.setStopFrequency(frequencyHigh);
		rc.setScanMode(scanMode);
		rc.setMathHelper(getMathHelper());

		VNABaseSample[] rawSamples = new VNABaseSample[samples];

		double offset = Math.random();

		double factor = offset * samples;

		for (int i = 0; i < samples; ++i) {
			// issue life signs
			if (i % 100 == 0) {
				listener.publishProgress((int) (i * 100.0 / samples));
			}
			VNABaseSample sample = new VNABaseSample();

			sample.setFrequency(frequencyLow + freqSteps * i);

			sample.setAngle(512 + (int) (512 * Math.sin(offset + (i / factor)) * Math.cos(offset + (i / factor))));
			sample.setLoss(512 + (int) (512 * Math.cos(offset - (i / factor)) * Math.sin(offset + (i / factor))));

			sample.setRss1(512 + (int) (237 * Math.cos(offset + (i / factor)) * Math.sin(offset + (i / factor))));
			sample.setRss2(512 + (int) (148 * Math.cos(offset + (i / factor)) * Math.sin(offset + (i / factor))));
			sample.setRss3(512 + (int) (347 * Math.cos(offset + (i / factor)) * Math.sin(offset + (i / factor))));
			rawSamples[i] = sample;
		}
		rc.setSamples(rawSamples);
		TraceHelper.exit(this, "scan");
		return rc;
	}

	public void showDriverDialog(VNAMainFrame pMF) {
		TraceHelper.entry(this, "showDriverDialog");
		VNADriverSampleDialog dlg = new VNADriverSampleDialog(pMF, this);
		dlg.dispose();
		TraceHelper.exit(this, "showDriverDialog");
	}

	public void showDriverNetworkDialog(VNAMainFrame pMF) {
		TraceHelper.entry(this, "showDriverNetworkDialog");
		OptionDialogHelper.showInfoDialog(pMF.getJFrame(), "VNADriverSerialBase.Network.1", "VNADriverSerialBase.Network.2");
		TraceHelper.exit(this, "showDriverNetworkDialog");
	}

	public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
		TraceHelper.entry(this, "showGeneratorDialog");
		throw new DialogNotImplementedException();
	}

	public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
		TraceHelper.entry(this, "startGenerator");
		TraceHelper.exit(this, "startGenerator");
	}

	public void stopGenerator() throws ProcessingException {
		TraceHelper.entry(this, "stopGenerator");
		TraceHelper.exit(this, "stopGenerator");
	}

	@Override
	public boolean checkForDevicePresence(boolean viaSlowConnection) {
		return true;
	}
}
