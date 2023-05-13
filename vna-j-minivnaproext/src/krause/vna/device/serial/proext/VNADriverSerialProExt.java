/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.serial.proext;

import krause.common.exception.DialogNotImplementedException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.serial.pro.VNADriverSerialPro;
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.device.serial.pro.VNADriverSerialProMathHelper;
import krause.vna.device.serial.proext.gui.VNADriverSerialProExtDialog;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.generator.VNAGeneratorDialog;

public class VNADriverSerialProExt extends VNADriverSerialPro {
	
	public final static int NUM_BYTES_PER_SAMPLE = 4;

	/**
	 * 
	 */
	public VNADriverSerialProExt() {
		TraceHelper.entry(this, "VNADriverSerialProExt");
		setMathHelper(new VNADriverSerialProMathHelper(this));
		setDeviceInfoBlock(new VNADriverSerialProExtDIB());
		getDeviceInfoBlock().restore(this.config, getDriverConfigPrefix());
		TraceHelper.exit(this, "VNADriverSerialProExt");
	}

	@Override
	public String getDriverConfigPrefix() {
		return "VNADriver.Serial.ProExt.";
	}

	@Override
	public VNACalibrationRange[] getSpecificCalibrationRanges() {
		VNACalibrationRange[] rc = null;
		long min = getDeviceInfoBlock().getMinFrequency();
		long max = getDeviceInfoBlock().getMaxFrequency();
		rc = new VNACalibrationRange[] {
				new VNACalibrationRange(min, 419999999, 1000, 1),
				new VNACalibrationRange(420000000, 449999999, 2000, 1),
				new VNACalibrationRange(450000000, 849999999, 500, 1),
				new VNACalibrationRange(850000000, 899999999, 2000, 1),
				new VNACalibrationRange(900000000, 1099999999, 1000, 1),
				new VNACalibrationRange(1100000000, max, 2000, 1)
		};
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#isScanSupported(int, krause.vna.data.VNAFrequencyRange, krause.vna.data.VNAScanMode)
	 */
	public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
		boolean rc = true;
		return rc;
	}

	/**
	 * 
	 * @param startFrequency
	 * @param samples
	 * @param frequencyStep
	 * @param listener
	 * @param transmission
	 * @return
	 * @throws ProcessingException
	 */
	protected VNABaseSample[] receiveRawMessage(long pStartFrequency, int pNumSamples, long pFrequencyStep, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
		final String methodName = "receiveMessage";
		TraceHelper.entry(this, methodName, "fs=" + pStartFrequency + " #=" + pNumSamples);

		VNABaseSample rawSamples[] = new VNABaseSample[pNumSamples];

		if (pListener != null)
			pListener.publishProgress(0);

		byte[] buffer = receiveBytestream(NUM_BYTES_PER_SAMPLE * pNumSamples, pListener);

		// now interpret the buffer
		long currentFrequency = pStartFrequency;
		for (int i = 0; i < pNumSamples; ++i) {
			int offset = i * 4;
			VNABaseSample tempSample = new VNABaseSample();
			//
			// Vi(z)=(adc1+adc2*256)
			int real = (buffer[offset + 0] & 0x000000ff) + ((buffer[offset + 1] & 0x000000ff) * 256);

			// Vq(z)=(adc3+adc4*256)
			int imaginary = (buffer[offset + 2] & 0x000000ff) + ((buffer[offset + 3] & 0x000000ff) * 256);

			// set in sample object
			tempSample.setLoss(real);
			tempSample.setAngle(imaginary);
			tempSample.setFrequency(currentFrequency);
			rawSamples[i] = tempSample;

			// next frequency
			currentFrequency += pFrequencyStep;
		}
		TraceHelper.text(this, methodName, "Last frequency stored was " + (currentFrequency - pFrequencyStep));

		//
		if (pListener != null) {
			pListener.publishProgress(100);
		}
		//
		TraceHelper.exit(this, methodName);
		return rawSamples;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.serial.VNADriverSerialBase#runVNA(boolean, int, int, int)
	 */
	public VNASampleBlock scan(VNAScanMode scanMode, long frequencyLow, long frequencyHigh, int numSamples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
		final String methodName = "scan";
		TraceHelper.entry(this, methodName, "trans=" + scanMode + " low=" + frequencyLow + " high=" + frequencyHigh + " #=" + numSamples);
		VNASampleBlock rc = new VNASampleBlock();
		//
		VNADriverSerialProExtDIB dib = (VNADriverSerialProExtDIB) getDeviceInfoBlock();

		if (getPort() != null) {
			flushInputStream();
			// send mode
			if (scanMode.isTransmissionMode()) {
				sendAsAsciiString(dib.getScanCommandTransmission());
			} else if (scanMode.isReflectionMode()) {
				sendAsAsciiString(dib.getScanCommandReflection());
			} else {
				throw new ProcessingException("Unsupported scan mode " + scanMode);
			}

			// send frequency range
			sendFrequency(frequencyLow);
			sendFrequency(frequencyHigh);

			// send number of samples
			sendAsAsciiString(Integer.toString(numSamples));

			// send spare
			sendAsAsciiString("");

			// receive data
			long frequencyStep = (frequencyHigh - frequencyLow) / numSamples;
			rc.setSamples(receiveRawMessage(frequencyLow, numSamples, frequencyStep, listener));
			//
			rc.setScanMode(scanMode);
			rc.setNumberOfSteps(numSamples);
			rc.setStartFrequency(frequencyLow);
			rc.setStopFrequency(frequencyHigh);
			rc.setAnalyserType(getDeviceInfoBlock().getType());
			rc.setMathHelper(getMathHelper());
		}
		TraceHelper.exit(this, methodName);
		return rc;
	}

	/**
	 * 
	 * @param frq
	 * @throws ProcessingException
	 */
	protected void sendFrequency(long frq) throws ProcessingException {
		TraceHelper.entry(this, "sendFrequency");
		VNADriverSerialProExtDIB dib = (VNADriverSerialProExtDIB) getDeviceInfoBlock();
		TraceHelper.text(this, "sendFrequency", "passed freq=" + frq);
		frq *= (dib.getDdsTicksPerMHz() / 1000000.0);
		frq /= dib.getPrescaler();
		TraceHelper.text(this, "sendFrequency", "used   freq=" + frq);
		String msg = getFrequencyFormat().format(frq);
		sendAsAsciiString(msg);
		TraceHelper.exit(this, "sendFrequency");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#showDriverDialog(krause.vna.gui.VNAMainFrame )
	 */
	public void showDriverDialog(VNAMainFrame pMF) {
		TraceHelper.entry(this, "showDriverDialog");
		VNADriverSerialProExtDialog dlg = new VNADriverSerialProExtDialog(pMF, this);
		dlg.dispose();
		TraceHelper.exit(this, "showDriverDialog");
	}

	public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
		TraceHelper.entry(this, "showGeneratorDialog");
		new VNAGeneratorDialog(pMF, this);
		TraceHelper.exit(this, "showGeneratorDialog");
	}

	/*
	 * Use the
	 * 
	 * @see krause.vna.device.IVNADriver#startGenerator(int)
	 */
	public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
		final String methodName = "startGenerator";
		TraceHelper.entry(this, methodName);

		VNADriverSerialProDIB dib = (VNADriverSerialProDIB) getDeviceInfoBlock();

		TraceHelper.text(this, methodName, "fI=" + frequencyI);
		TraceHelper.text(this, methodName, "fQ=" + frequencyQ);

		if (getPort() != null) {
			flushInputStream();
			sendAsAsciiString("21"); // gen mode
			sendFrequency(frequencyI); // freq 1st channel
			sendFrequency(frequencyI); // freq 2nd channel
			sendAsAsciiString("1"); // one sample
			sendAsAsciiString("0"); // spare

			// read the raw from the analyser
			receiveRawMessage(0, 1, 1, null);

			// and now wait before the next run
			wait(dib.getAfterCommandDelay());
		}
		TraceHelper.exit(this, methodName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#stopGenerator()
	 */
	public void stopGenerator() throws ProcessingException {
		final String methodName = "stopGenerator";
		TraceHelper.entry(this, methodName);
		if (getPort() != null) {
			flushInputStream();
			sendAsAsciiString("7"); // gen mode
			sendFrequency(0); // freq 1st channel
			sendFrequency(0); // freq 2nd channel
			sendAsAsciiString("1"); // one sample
			sendAsAsciiString("0"); // spare
			//
			VNADriverSerialProDIB dib = (VNADriverSerialProDIB) getDeviceInfoBlock();
			wait(dib.getAfterCommandDelay());
		}
		TraceHelper.exit(this, methodName);
	}
}
