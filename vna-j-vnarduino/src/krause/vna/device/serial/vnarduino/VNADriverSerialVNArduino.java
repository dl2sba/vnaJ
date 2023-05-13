/**
 * Copyright (C) 2020 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.serial.vnarduino;

import krause.common.exception.DialogNotImplementedException;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.std2.VNADriverSerialStd2MathHelper;
import krause.vna.device.serial.vnarduino.gui.VNADriverSerialVNArduinoDialog;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.generator.VNAGeneratorDialog;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialVNArduino extends VNADriverSerialBase {
	static final int NUM_BYTES_PER_SAMPLE = 4;

	public VNADriverSerialVNArduino() {
		final String methodName = "VNADriverSerialVNArduino";
		TraceHelper.entry(this, methodName);

		setMathHelper(new VNADriverSerialStd2MathHelper(this));
		setDeviceInfoBlock(new VNADriverSerialVNArduinoDIB());
		//
		getDeviceInfoBlock().restore(this.config, getDriverConfigPrefix());
		TraceHelper.exit(this, methodName);
	}

	@Override
	public void init() throws InitializationException {
		final String methodeName = "init";
		TraceHelper.entry(this, methodeName);
		super.init();
		//
		VNADriverSerialVNArduinoDIB dib = (VNADriverSerialVNArduinoDIB) getDeviceInfoBlock();
		dib.restore(this.config, getDriverConfigPrefix());

		try {
			CommPortIdentifier portId = getPortIDForName(getPortname());
			if (portId != null) {
				setPort((SerialPort) portId.open(getAppname(), dib.getOpenTimeout()));

				getPort().setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				getPort().setSerialPortParams(dib.getBaudrate(), SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);
				getPort().enableReceiveTimeout(dib.getReadTimeout());
				getPort().setInputBufferSize(65536);

				// 2015-09-19
				// added to prevent reset of ARDUINO NANO Clones
				// On Unix, DTR is pulled low on port open, which causes the
				// Bootloader to start for a few seconds
				//
				// Seems not to work on OSX :-(
				// getPort().setDTR(true)
				// --------
				wait(dib.getAfterCommandDelay());
			} else {
				InitializationException e = new InitializationException("Port [" + getPortname() + "] not found");
				ErrorLogHelper.exception(this, methodeName, e);
				throw e;
			}
		} catch (ProcessingException | PortInUseException | UnsupportedCommOperationException e) {
			ErrorLogHelper.exception(this, methodeName, e);
			throw new InitializationException(e);
		}
		TraceHelper.exit(this, methodeName);
	}

	/**
	 * 
	 * @param frequency
	 * @param pNumSamples
	 * @param frequencyStep
	 * @param listener
	 * @param transmission
	 * @return
	 * @throws ProcessingException
	 */
	private VNABaseSample[] receiveRawMessage(long frequency, int pNumSamples, long frequencyStep, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
		final String methodName = "receiveRawMessage";
		TraceHelper.entry(this, methodName, "fStart=%d #=%d freqStep=%d", frequency, pNumSamples, frequencyStep);

		VNABaseSample[] rc = new VNABaseSample[pNumSamples];

		if (listener != null) {
			listener.publishProgress(0);
		}

		byte[] buffer = receiveBytestream(NUM_BYTES_PER_SAMPLE * pNumSamples, listener);

		// now interpret the buffer
		long localFrequency = frequency;
		for (int i = 0; i < pNumSamples; ++i) {
			int offset = i * NUM_BYTES_PER_SAMPLE;
			VNABaseSample tempSample = new VNABaseSample();
			tempSample.setAngle((buffer[offset] & 0x000000ff) + 256.0 * (buffer[offset + 1] & 0x000000ff));
			tempSample.setLoss((buffer[offset + 2] & 0x000000ff) + 256.0 * (buffer[offset + 3] & 0x000000ff));
			tempSample.setFrequency(localFrequency);
			// store in rc
			rc[i] = tempSample;
			// next frequency
			localFrequency += frequencyStep;
		}
		if (listener != null) {
			listener.publishProgress(100);
		}

		TraceHelper.exit(this, methodName);
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.serial.VNADriverIF#destroy()
	 */
	public void destroy() {
		final String methodName = "destroy";
		TraceHelper.entry(this, methodName);
		if (getPort() != null) {
			TraceHelper.text(this, methodName, "closing " + getPort().getName());
			getPort().close();
			setPort(null);
			TraceHelper.text(this, methodName, "port closed");
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 * @param frq
	 * @throws ProcessingException
	 */
	protected void sendFrequency(long frq) throws ProcessingException {
		String msg = getFrequencyFormat().format((frq / 1000000.0) * getDeviceInfoBlock().getDdsTicksPerMHz());
		TraceHelper.text(this, "sendFrequency", "%d -> [%s]", frq, msg);
		sendAsAsciiString(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#runVNAInRawMode(boolean, int, int, int)
	 */
	public VNASampleBlock scan(VNAScanMode pScanMode, long frequencyLow, long frequencyHigh, int samples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
		final String methodName = "scan";
		TraceHelper.entry(this, methodName);
		long frequencyStep = (frequencyHigh - frequencyLow) / samples;

		VNASampleBlock rc = new VNASampleBlock();
		rc.setAnalyserType(getDeviceInfoBlock().getType());
		rc.setScanMode(pScanMode);
		rc.setNumberOfSteps(samples);
		rc.setStartFrequency(frequencyLow);
		rc.setStopFrequency(frequencyHigh);
		rc.setMathHelper(getMathHelper());

		if (getPort() != null) {
			flushInputStream();

			if (pScanMode.isTransmissionMode()) {
				sendAsAsciiString("1");
			} else if (pScanMode.isReflectionMode()) {
				sendAsAsciiString("0");
			} else {
				throw new ProcessingException("Unsupported scan mode " + pScanMode);
			}
			sendFrequency(frequencyLow);
			sendAsAsciiString(Integer.toString(samples));
			sendFrequency(frequencyStep);
			// receive data
			rc.setSamples(receiveRawMessage(frequencyLow, samples, frequencyStep, listener));

			// stop the output
			stopGenerator();
		}
		TraceHelper.exit(this, methodName);
		return rc;
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
			sendAsAsciiString("0");
			sendFrequency(0);
			sendAsAsciiString("1");
			sendAsAsciiString("0");

			// read the raw from the analyzer
			receiveRawMessage(0, 1, 1000, null);

			// and sleep a while after
			VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock) getDeviceInfoBlock();
			wait(dib.getAfterCommandDelay());
		}
		TraceHelper.exit(this, methodName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#showDriverDialog(krause.vna.gui.VNAMainFrame )
	 */
	public void showDriverDialog(VNAMainFrame pMF) {
		TraceHelper.entry(this, "showDriverDialog");
		VNADriverSerialVNArduinoDialog dlg = new VNADriverSerialVNArduinoDialog(pMF, this);
		dlg.dispose();
		TraceHelper.exit(this, "showDriverDialog");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#calculateInternalFrequencyValue(int)
	 */
	public long calculateInternalFrequencyValue(long frequency) {
		TraceHelper.entry(this, "calculateInternalFrequencyValue", "In=" + frequency);
		long rc;
		rc = (long) ((frequency / 1000000.0) * getDeviceInfoBlock().getDdsTicksPerMHz());
		TraceHelper.exitWithRC(this, "calculateInternalFrequencyValue", rc);
		return rc;
	}

	public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
		TraceHelper.entry(this, "showGeneratorDialog");
		new VNAGeneratorDialog(pMF, this);
		TraceHelper.exit(this, "showGeneratorDialog");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.VNAGenericDriver#getDeviceFirmwareInfo()
	 */
	@Override
	public String getDeviceFirmwareInfo() {
		return "V1.0";
	}

	public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
		final String methodName = "startGenerator";
		TraceHelper.entry(this, methodName, "frq=%d", frequencyI);
		if (getPort() != null) {
			flushInputStream();
			sendAsAsciiString("0");
			sendFrequency(frequencyI);
			sendAsAsciiString("1");
			sendAsAsciiString("0");

			// read the raw from the analyser
			receiveRawMessage(0, 1, 1, null);

			// and sleep a while after
			VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock) getDeviceInfoBlock();
			wait(dib.getAfterCommandDelay());
		}
		TraceHelper.exit(this, methodName);
	}

	public String getDriverConfigPrefix() {
		return "VNADriver.Serial.VNArduino.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#readPowerStatus()
	 */
	@Override
	public Double getDeviceSupply() {
		return 5.0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#isScanSupported(int, krause.vna.data.VNAFrequencyRange, krause.vna.data.VNAScanMode)
	 */
	public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
		return true;
	}

	@Override
	public VNACalibrationRange[] getSpecificCalibrationRanges() {
		return new VNACalibrationRange[] {
				new VNACalibrationRange(getDeviceInfoBlock().getMinFrequency(), getDeviceInfoBlock().getMaxFrequency(), MAXIMUM_SCAN_POINTS, 1)
		};
	}

	@Override
	public boolean checkForDevicePresence(boolean viaSlowConnection) {
		boolean rc = false;
		final String methodName = "checkForDevicePresence";
		TraceHelper.entry(this, methodName);

		try {
			VNADeviceInfoBlock dib = getDeviceInfoBlock();

			init();
			if (viaSlowConnection) {
				Thread.sleep(10000);
			}
			scan(VNAScanMode.MODE_REFLECTION, dib.getMinFrequency(), dib.getMaxFrequency(), 100, null);
			rc = true;
		} catch (ProcessingException | InterruptedException e) {
			ErrorLogHelper.exception(this, methodName, e);
		} finally {
			destroy();
		}
		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;
	}
}