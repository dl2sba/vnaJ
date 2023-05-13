/**
 * Copyright (C) 2014 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.serial.tiny;

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
import krause.vna.device.IVNAFlashableDevice;
import krause.vna.device.VNASerialDeviceInfoBlock;
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.tiny.gui.VNADriverSerialTinyDialog;
import krause.vna.firmware.Chip45Loader;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import krause.vna.gui.generator.VNAGeneratorDialog;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialTiny extends VNADriverSerialBase implements IVNAFlashableDevice {
	private static final int NUM_BYTES_PER_SAMPLE = 12;

	/**
	 * 
	 */
	public VNADriverSerialTiny() {
		TraceHelper.entry(this, "VNADriverSerialTiny");
		setMathHelper(new VNADriverSerialTinyMathHelper(this));
		setDeviceInfoBlock(new VNADriverSerialTinyDIB());
		getDeviceInfoBlock().restore(this.config, getDriverConfigPrefix());
		TraceHelper.exit(this, "VNADriverSerialTiny");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#calculateInternalFrequencyValue(int)
	 */
	public long calculateInternalFrequencyValue(long frequency) {
		TraceHelper.entry(this, "calculateInternalFrequencyValue", "In=" + frequency);
		long rc;
		rc = (long) ((frequency / (VNADriverSerialTinyDIB.DDS_TICKS * 1.0)) * getDeviceInfoBlock().getDdsTicksPerMHz());
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
			TraceHelper.text(this, methodName, "closing " + getPort().getName());
			getPort().close();
			setPort(null);
			TraceHelper.text(this, methodName, "port closed");
		}
		TraceHelper.exit(this, methodName);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNAFlashableDevice#getBootloadBaudRate()
	 */
	public int getFirmwareLoaderBaudRate() {
		VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB) getDeviceInfoBlock();

		return dib.getBootloaderBaudrate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.VNAGenericDriver#getDeviceFirmwareInfo()
	 */
	public String getDeviceFirmwareInfo() {
		final String methodName = "readFirmwareVersion";
		TraceHelper.entry(this, methodName);
		String rc = "???";
		try {
			flushInputStream();
			sendAsAsciiString("9");
			rc = readLine(true);
		} catch (ProcessingException pex) {
			;
		}

		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.VNAGenericDriver#getDeviceSupply()
	 */
	public Double getDeviceSupply() {
		final String methodName = "getDevicePowerStatus";
		TraceHelper.entry(this, methodName);
		Double rc = null;

		if (getPort() != null) {
			try {
				flushInputStream();
				sendAsAsciiString("8");

				byte innerdata[] = new byte[2];

				int ch = readBuffer(innerdata, 0, 2);
				if (ch == -1) {
					ProcessingException e = new ProcessingException("No data character received");
					ErrorLogHelper.exception(this, methodName, e);
					throw e;
				}
				rc = ((innerdata[0] & 0x000000ff) + (innerdata[1] & 0x000000ff) * 256) * 6 / 1024.0;
			} catch (ProcessingException pex) {
				rc = -1.0;
			}
		}

		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.VNAGenericDriver#getDeviceTemperature()
	 */
	public Double getDeviceTemperature() {
		final String methodName = "getDeviceTemperature";
		TraceHelper.entry(this, methodName);
		Double rc = -1.0;

		if (getPort() != null) {
			try {
				flushInputStream();
				sendAsAsciiString("10");

				byte innerdata[] = new byte[2];

				int ch = readBuffer(innerdata, 0, 2);
				if (ch == -1) {
					ProcessingException e = new ProcessingException("No data character received");
					ErrorLogHelper.exception(this, methodName, e);
				}
				rc = ((innerdata[0] & 0x000000ff) + (innerdata[1] & 0x000000ff) * 256) / 10.0;
			} catch (ProcessingException e1) {
				ErrorLogHelper.exception(this, methodName, e1);
			}
		}

		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;
	}

	public String getDriverConfigPrefix() {
		return "VNADriver.Serial.Tiny.";
	}

	@Override
	public String getFirmwareLoaderClassName() {
		if (this.config.isMac()) {
			return null;
		} else {
			return Chip45Loader.class.getName();
		}
	}

	@Override
	public VNACalibrationRange[] getSpecificCalibrationRanges() {
		return new VNACalibrationRange[] {
				new VNACalibrationRange(getDeviceInfoBlock().getMinFrequency(), 30999999, 10000, 2),
				new VNACalibrationRange(31000000l, 48999999l, 500, 1),
				new VNACalibrationRange(49000000l, 52999999l, 2000, 2),
				new VNACalibrationRange(53000000l, 142999999l, 500, 1),
				new VNACalibrationRange(143000000l, 147999999l, 2000, 2),
				new VNACalibrationRange(148000000l, 428999999l, 500, 1),
				new VNACalibrationRange(429000000l, 441999999l, 2000, 2),
				new VNACalibrationRange(442000000l, 1229999999l, 500, 1),
				new VNACalibrationRange(1230000000l, 1310999999l, 2000, 2),
				new VNACalibrationRange(1311000000l, 2199999999l, 500, 1),
				new VNACalibrationRange(2200000000l, 2599999999l, 2000, 2),
				new VNACalibrationRange(2600000000l, getDeviceInfoBlock().getMaxFrequency(), 500, 1),
		};
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNAFlashableDevice#hasResetButton()
	 */
	public boolean hasResetButton() {
		return false;
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
		VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB) getDeviceInfoBlock();
		dib.restore(this.config, getDriverConfigPrefix());
		//
		TraceHelper.text(this, methodeName, "Trying to open port [" + getPortname() + "]");
		try {
			CommPortIdentifier portId = getPortIDForName(getPortname());
			if (portId != null) {
				TraceHelper.text(this, methodeName, "port [" + getPortname() + "] found");
				SerialPort aPort = (SerialPort) portId.open(getAppname(), dib.getOpenTimeout());

				TraceHelper.text(this, methodeName, "port [" + getPortname() + "] opened");

				aPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

				TraceHelper.text(this, methodeName, "port [" + getPortname() + "] set to [" + dib.getBaudrate() + "]bd");
				aPort.setSerialPortParams(dib.getBaudrate(), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				aPort.enableReceiveTimeout(dib.getReadTimeout());
				aPort.setInputBufferSize(DEFAULT_INPUTBUFFER_SIZE);

				TraceHelper.text(this, methodeName, "port [" + getPortname() + "] setup done");

				setPort(aPort);
			} else {
				InitializationException e = new InitializationException("Port [" + getPortname() + "] not found");
				ErrorLogHelper.exception(this, methodeName, e);
				throw e;
			}
		} catch (UnsupportedCommOperationException | PortInUseException e) {
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
	 * @see krause.vna.device.IVNADriver#isScanSupported(int,
	 * krause.vna.data.VNAFrequencyRange, krause.vna.data.VNAScanMode)
	 */
	public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
		boolean rc = true;
		return rc;
	}

	/**
	 * 
	 * @param pStartFrequency
	 * @param pNumSamples
	 * @param pFrequencyStep
	 * @param pListener
	 * @param transmission
	 * @return
	 * @throws ProcessingException
	 */
	protected VNABaseSample[] receiveRawMessage(long pStartFrequency, int pNumSamples, long pFrequencyStep, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
		final String methodName = "receiveRawMessage";
		TraceHelper.entry(this, methodName, "fs=" + pStartFrequency + " #=" + pNumSamples);

		VNABaseSample[] rc = new VNABaseSample[pNumSamples];

		if (pListener != null) {
			pListener.publishProgress(0);
		}

		byte[] buffer = receiveBytestream(NUM_BYTES_PER_SAMPLE * pNumSamples, pListener);

		// now interpret the buffer
		long currentFrequency = pStartFrequency;
		for (int i = 0; i < pNumSamples; ++i) {
			int offset = i * NUM_BYTES_PER_SAMPLE;
			VNABaseSample tempSample = new VNABaseSample();

			// Vi(z)=((adc1+adc2*256+adc3*65536)-(adc7+adc8*256+adc9*65536))/2
			int p1 = (buffer[offset + 0] & 0x000000ff) + (buffer[offset + 1] & 0x000000ff) * 256 + (buffer[offset + 2] & 0x000000ff) * 65536;
			int p2 = (buffer[offset + 6] & 0x000000ff) + (buffer[offset + 7] & 0x000000ff) * 256 + (buffer[offset + 8] & 0x000000ff) * 65536;
			double real = (p1 - p2) / 2.0;

			// Vq(z)=((adc4+adc5*256+adc6*65536)-(adc10+adc11*256+adc12*65536))/2
			int p3 = (buffer[offset + 3] & 0x000000ff) + (buffer[offset + 4] & 0x000000ff) * 256 + (buffer[offset + 5] & 0x000000ff) * 65536;
			int p4 = (buffer[offset + 9] & 0x000000ff) + (buffer[offset + 10] & 0x000000ff) * 256 + (buffer[offset + 11] & 0x000000ff) * 65536;
			double imaginary = (p3 - p4) / 2.0;

			// 4 phase data
			tempSample.setP1(p1);
			tempSample.setP2(p2);
			tempSample.setP3(p3);
			tempSample.setP4(p4);
			tempSample.setHasPData(true);

			tempSample.setLoss(real);
			tempSample.setAngle(imaginary);
			tempSample.setFrequency(currentFrequency);
			rc[i] = tempSample;

			// next frequency
			currentFrequency += pFrequencyStep;
		}

		// if someone listens
		if (pListener != null) {
			// publish we've done
			pListener.publishProgress(100);
		}

		TraceHelper.text(this, methodName, "Last frequency requested was " + (currentFrequency - pFrequencyStep));
		TraceHelper.text(this, methodName, pNumSamples + " step(s) done");
		//
		TraceHelper.exit(this, methodName);
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.serial.VNADriverSerialBase#runVNA(boolean, int,
	 * int, int)
	 */
	public VNASampleBlock scan(VNAScanMode scanMode, long frequencyLow, long frequencyHigh, int numSamples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
		final String methodName = "scan";
		TraceHelper.entry(this, methodName);
		VNASampleBlock rc = new VNASampleBlock();

		//
		rc.setDeviceTemperature(getDeviceTemperature());
		rc.setDeviceSupply(getDeviceSupply());

		//
		final VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB) getDeviceInfoBlock();

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
			final long frequencyStep = (frequencyHigh - frequencyLow) / (numSamples - 1);

			TraceHelper.text(this, methodName, "Start %d", frequencyLow);
			TraceHelper.text(this, methodName, "Stop  %d", frequencyHigh);
			TraceHelper.text(this, methodName, "Steps %d", numSamples);
			TraceHelper.text(this, methodName, "Step  %d", frequencyStep);
			TraceHelper.text(this, methodName, "Last  %d", (frequencyLow + (numSamples - 1) * frequencyStep));

			rc.setSamples(receiveRawMessage(frequencyLow, numSamples, frequencyStep, listener));

			// set output data
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
		long corrFrq = calculateInternalFrequencyValue(frq);
		VNADriverSerialTinyDIB dib = (VNADriverSerialTinyDIB) getDeviceInfoBlock();
		String msg = getFrequencyFormat().format(corrFrq / dib.getPrescaler());
		sendAsAsciiString(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.device.IVNADriver#showDriverDialog(krause.vna.gui.VNAMainFrame
	 * )
	 */
	public void showDriverDialog(VNAMainFrame pMF) {
		TraceHelper.entry(this, "showDriverDialog");
		VNADriverSerialTinyDialog dlg = new VNADriverSerialTinyDialog(pMF, this);
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

			// and sleep a while after
			VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock) getDeviceInfoBlock();
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

			// read the raw from the analyser
			receiveRawMessage(0, 1, 1, null);

			// and sleep a while after
			VNASerialDeviceInfoBlock dib = (VNASerialDeviceInfoBlock) getDeviceInfoBlock();
			wait(dib.getAfterCommandDelay());
		}
		TraceHelper.exit(this, methodName);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNAFlashableDevice#supportsAutoReset()
	 */
	public boolean supportsAutoReset() {
		return true;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.device.serial.VNADriverSerialBase#checkForDevicePresence()
	 */
	public boolean checkForDevicePresence(boolean viaSlowConnection) {
		boolean rc = false;
		final String methodName = "checkForDevicePresence";
		TraceHelper.entry(this, methodName);

		try {
			init();
			String fw = getDeviceFirmwareInfo();
			rc = fw.startsWith("FW Tiny ");
		} catch (InitializationException e) {
			ErrorLogHelper.exception(this, methodName, e);
		} finally {
			destroy();
		}
		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;
	}
}
