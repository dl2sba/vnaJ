/**
 * Copyright (C) 2015 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.serial.metro;

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
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.metro.gui.VNADriverSerialMetroDialog;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.generator.VNAGeneratorDialog;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialMetro extends VNADriverSerialBase {

	private static final int NUM_BYTES_PER_SAMPLE = 4;

	public VNADriverSerialMetro() {
		final String methodName = "VNADriverSerialMetro";
		TraceHelper.entry(this, methodName);

		setMathHelper(new VNADriverSerialMetroMathHelper(this));
		setDeviceInfoBlock(new VNADriverSerialMetroDIB());
		//
		getDeviceInfoBlock().restore(this.config, getDriverConfigPrefix());
		TraceHelper.exit(this, methodName);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.serial.VNADriverIF#destroy()
	 */
	public void destroy() {
		final String methodName = "destroy";
		TraceHelper.entry(this, methodName);
		if (getPort() != null) {
			getPort().close();
			setPort(null);
		}
		TraceHelper.exit(this, methodName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.VNAGenericDriver#getDeviceFirmwareInfo()
	 */
	public String getDeviceFirmwareInfo() {
		return "???";
	}

	@Override
	public Double getDeviceSupply() {
		return 5.0;
	}

	public String getDriverConfigPrefix() {
		return "VNADriver.Serial.MetroVNA.";
	}

	@Override
	public VNACalibrationRange[] getSpecificCalibrationRanges() {
		return new VNACalibrationRange[] {
				new VNACalibrationRange(getDeviceInfoBlock().getMinFrequency(), getDeviceInfoBlock().getMaxFrequency(), MAXIMUM_SCAN_POINTS, 1)
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.serial.VNADriverIF#init(java.util.Properties)
	 */
	public void init() throws InitializationException {
		final String methodeName = "init";
		TraceHelper.entry(this, methodeName);
		super.init();
		//
		VNADriverSerialMetroDIB dib = (VNADriverSerialMetroDIB) getDeviceInfoBlock();
		dib.restore(this.config, getDriverConfigPrefix());

		try {
			CommPortIdentifier portId = getPortIDForName(getPortname());
			if (portId != null) {
				setPort((SerialPort) portId.open(getAppname(), dib.getOpenTimeout()));
				getPort().setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				getPort().setSerialPortParams(dib.getBaudrate(), SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);
				getPort().enableReceiveTimeout(dib.getReadTimeout());
				getPort().setInputBufferSize(65536);
			} else {
				InitializationException e = new InitializationException("Port [" + getPortname() + "] not found");
				ErrorLogHelper.exception(this, methodeName, e);
				throw e;
			}
		} catch (PortInUseException e) {
			ErrorLogHelper.exception(this, methodeName, e);
			throw new InitializationException(e);
		} catch (UnsupportedCommOperationException e) {
			ErrorLogHelper.exception(this, methodeName, e);
			throw new InitializationException(e);
		} catch (Throwable t) {
			ErrorLogHelper.text(this, methodeName, t.getMessage());
			throw new InitializationException(t);
		}
		TraceHelper.exit(this, methodeName);
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
	 * @see krause.vna.device.IVNADriver#runVNAInRawMode(boolean, int, int, int)
	 */
	public VNASampleBlock scan(VNAScanMode pScanMode, long pFrequencyLow, long pFrequencyHigh, int pSamples, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
		final String methodName = "scan";
		TraceHelper.entry(this, methodName);
		long frequencyStep = (pFrequencyHigh - pFrequencyLow) / pSamples;

		VNASampleBlock rc = new VNASampleBlock();
		rc.setAnalyserType(getDeviceInfoBlock().getType());
		rc.setScanMode(pScanMode);
		rc.setNumberOfSteps(pSamples);
		rc.setStartFrequency(pFrequencyLow);
		rc.setStopFrequency(pFrequencyHigh);
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
			sendFrequency(pFrequencyLow);
			sendAsAsciiString(Integer.toString(pSamples));
			sendFrequency(frequencyStep);
			// receive data
			rc.setSamples(receiveRawMessage(pScanMode, pFrequencyLow, pSamples, frequencyStep, pListener));

			// stop the output
			stopGenerator();
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
		String msg = getFrequencyFormat().format((frq / 1000000.0) * getDeviceInfoBlock().getDdsTicksPerMHz());
		TraceHelper.text(this, "sendFrequency", msg);
		sendAsAsciiString(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#showDriverDialog(krause.vna.gui.VNAMainFrame )
	 */
	public void showDriverDialog(VNAMainFrame pMF) {
		TraceHelper.entry(this, "showDriverDialog");
		VNADriverSerialMetroDialog dlg = new VNADriverSerialMetroDialog(pMF, this);
		dlg.dispose();
		TraceHelper.exit(this, "showDriverDialog");

	}

	public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
		TraceHelper.entry(this, "showGeneratorDialog");
		new VNAGeneratorDialog(pMF, this);
		TraceHelper.exit(this, "showGeneratorDialog");
	}

	public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
		TraceHelper.entry(this, "startGenerator");
		TraceHelper.entry(this, "startGenerator", "" + frequencyI);
		if (getPort() != null) {
			flushInputStream();
			sendAsAsciiString("0");
			sendFrequency(frequencyI);
			sendAsAsciiString("1");
			sendAsAsciiString("0");

			// read the raw from the analyser
			receiveRawMessage(VNAScanMode.MODE_TRANSMISSION, 0, 1, 1, null);

			// and sleep a while after
			VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock) getDeviceInfoBlock();
			wait(dib.getAfterCommandDelay());
		}
		TraceHelper.exit(this, "startGenerator");
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

			// read the raw from the analyser
			receiveRawMessage(VNAScanMode.MODE_TRANSMISSION, 0, 1, 1, null);

			// and sleep a while after
			VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock) getDeviceInfoBlock();
			wait(dib.getAfterCommandDelay());
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 * @param pScanMode
	 * @param frequency
	 * @param pNumSamples
	 * @param frequencyStep
	 * @param listener
	 * @param transmission
	 * @return
	 * @throws ProcessingException
	 */
	private VNABaseSample[] receiveRawMessage(VNAScanMode pScanMode, long frequency, int pNumSamples, long frequencyStep, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
		final String methodName = "receiveRawMessage";
		TraceHelper.entry(this, methodName, "fStart=" + frequency + " #=" + pNumSamples + " fStep=" + frequencyStep);

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
			tempSample.setAngle((buffer[offset] & 0x000000ff) + 256 * (buffer[offset + 1] & 0x000000ff));
			tempSample.setLoss((buffer[offset + 2] & 0x000000ff) + 256 * (buffer[offset + 3] & 0x000000ff));
			tempSample.setFrequency(localFrequency);
			// store in rc
			rc[i] = tempSample;
			// next frequency
			localFrequency += frequencyStep;
		}
		TraceHelper.exit(this, methodName);
		return rc;
	}
}