/**
 * Copyright (C) 2014 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.serial.pro2;

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
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.pro2.generator.VNAGeneratorPro2Dialog;
import krause.vna.device.serial.pro2.gui.VNADriverSerialPro2Dialog;
import krause.vna.firmware.MCSLoader;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialPro2 extends VNADriverSerialBase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#showDriverDialog(krause.vna.gui.VNAMainFrame )
	 */
	public void showDriverDialog(VNAMainFrame pMF) {
		TraceHelper.entry(this, "showDriverDialog");
		VNADriverSerialPro2Dialog dlg = new VNADriverSerialPro2Dialog(pMF, this);
		dlg.dispose();
		TraceHelper.exit(this, "showDriverDialog");
	}

	public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
		TraceHelper.entry(this, "showGeneratorDialog");
		VNAGeneratorPro2Dialog dlg = new VNAGeneratorPro2Dialog(pMF, this);
		dlg.showInPlace();
		TraceHelper.exit(this, "showGeneratorDialog");
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
	 */
	public VNADriverSerialPro2() {
		final String methodName = "VNADriverSerialPro2";
		TraceHelper.entry(this, methodName);

		setMathHelper(new VNADriverSerialPro2MathHelper(this));
		setDeviceInfoBlock(new VNADriverSerialPro2DIB());
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
		rc = (long) ((frequency / (VNADriverSerialPro2DIB.DDS_TICKS * 1.0)) * getDeviceInfoBlock().getDdsTicksPerMHz());
		TraceHelper.exitWithRC(this, "calculateInternalFrequencyValue", rc);
		return rc;
	}

	public int getFirmwareLoaderBaudRate() {
		VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB) getDeviceInfoBlock();

		return dib.getBootloaderBaudrate();
	}

	public String getDeviceFirmwareInfo() {
		final String methodName = "readFirmwareVersion";
		TraceHelper.entry(this, methodName);
		String rc = "???";
		try {
			flushInputStream();
			sendAsAsciiString("9");
			rc = readLine(true);
		} catch (ProcessingException pex) {
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

				byte[] innerdata = new byte[2];

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

	public String getDriverConfigPrefix() {
		return "VNADriver.Serial.Pro2.";
	}

	public String getFirmwareLoaderClassName() {
		if (this.config.isMac()) {
			return null;
		} else {
			return MCSLoader.class.getName();
		}
	}

	@Override
	public VNACalibrationRange[] getSpecificCalibrationRanges() {
		return new VNACalibrationRange[] {
				new VNACalibrationRange(getDeviceInfoBlock().getMinFrequency(), 30999999, 10000, 1),
				new VNACalibrationRange(31000000l, 48999999l, 500, 1),
				new VNACalibrationRange(49000000l, 52999999l, 2000, 1),
				new VNACalibrationRange(53000000l, 142999999l, 500, 1),
				new VNACalibrationRange(143000000l, 147999999l, 2000, 1),
				new VNACalibrationRange(148000000l, getDeviceInfoBlock().getMaxFrequency(), 2000, 1),
		};
	}

	public boolean hasResetButton() {
		return true;
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
		VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB) getDeviceInfoBlock();
		dib.restore(this.config, getDriverConfigPrefix());
		TraceHelper.text(this, methodeName, "Trying to open port [" + getPortname() + "]");
		//
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
		} catch (PortInUseException | UnsupportedCommOperationException e) {
			ErrorLogHelper.exception(this, methodeName, e);
			throw new InitializationException(e);
		} catch (Throwable t) {
			ErrorLogHelper.text(this, methodeName, t.getMessage());
			throw new InitializationException(t);
		}
		TraceHelper.exit(this, methodeName);
	}

	/**
	 * 
	 * @param pMode
	 * @param pStartFrequency
	 * @param pNumSamples
	 * @param pFrequencyStep
	 * @param pListener
	 * @param transmission
	 * @return
	 * @throws ProcessingException
	 */
	protected VNABaseSample[] receiveRawMessage(VNAScanMode pMode, long pStartFrequency, int pNumSamples, long pFrequencyStep, IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
		final String methodName = "receiveRawMessage";
		TraceHelper.entry(this, methodName, "fStart=%d #=%d", pStartFrequency, pNumSamples);

		VNABaseSample[] rc = new VNABaseSample[pNumSamples];
		int numBytesPerSample;

		if (pListener != null) {
			pListener.publishProgress(0);
		}

		final VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB) getDeviceInfoBlock();
		int resolution = dib.getResolution();

		// set the number of bytes per sample based on the resolution
		if (resolution == VNADriverSerialPro2DIB.RESOLUTION_16BIT) {
			numBytesPerSample = 8;
			TraceHelper.text(this, methodName, "using 8 bytes/sample");
		} else if (resolution == VNADriverSerialPro2DIB.RESOLUTION_24BIT) {
			numBytesPerSample = 12;
			TraceHelper.text(this, methodName, "using 12 bytes/sample");
		} else {
			throw new ProcessingException("Unsupported resolution mode " + resolution);
		}

		byte[] buffer = receiveBytestream(numBytesPerSample * pNumSamples, pListener);

		// set the number of bytes per sample based on the resolution
		if (resolution == VNADriverSerialPro2DIB.RESOLUTION_16BIT) {
			// now interpret the buffer
			long currentFrequency = pStartFrequency;
			for (int i = 0; i < pNumSamples; ++i) {
				int offset = i * numBytesPerSample;
				VNABaseSample tempSample = new VNABaseSample();
				//
				int p1 = (buffer[offset + 0] & 0x000000ff) + (buffer[offset + 1] & 0x000000ff) * 256;
				int p2 = (buffer[offset + 4] & 0x000000ff) + (buffer[offset + 5] & 0x000000ff) * 256;

				// Vi(z)=((adc1+adc2*256)-(adc5+adc6*256))/2
				int real = (p1 - p2) / 2;

				int p3 = (buffer[offset + 2] & 0x000000ff) + (buffer[offset + 3] & 0x000000ff) * 256;
				int p4 = (buffer[offset + 6] & 0x000000ff) + (buffer[offset + 7] & 0x000000ff) * 256;

				// Vq(z)=((adc3+adc4*256)-(adc7+adc8*256))/2
				int imaginary = (p3 - p4) / 2;

				// set in sample object
				tempSample.setLoss(real);
				tempSample.setAngle(imaginary);
				tempSample.setFrequency(currentFrequency);

				// 4 phase data
				tempSample.setP1(p1);
				tempSample.setP2(p2);
				tempSample.setP3(p3);
				tempSample.setP4(p4);
				tempSample.setHasPData(true);

				rc[i] = tempSample;

				// next frequency
				currentFrequency += pFrequencyStep;
			}
			TraceHelper.text(this, methodName, "Last frequency stored was %d", currentFrequency - pFrequencyStep);

		} else if (resolution == VNADriverSerialPro2DIB.RESOLUTION_24BIT) {
			// now interpret the buffer
			long currentFrequency = pStartFrequency;
			for (int i = 0; i < pNumSamples; ++i) {
				int offset = i * numBytesPerSample;
				VNABaseSample tempSample = new VNABaseSample();

				// Vi(z)=((adc1+adc2*256+adc3*65536)-(adc7+adc8*256+adc9*65536))/2
				int p1 = (buffer[offset + 0] & 0x000000ff) + (buffer[offset + 1] & 0x000000ff) * 256 + (buffer[offset + 2] & 0x000000ff) * 65536;
				int p2 = (buffer[offset + 6] & 0x000000ff) + (buffer[offset + 7] & 0x000000ff) * 256 + (buffer[offset + 8] & 0x000000ff) * 65536;
				double real = (p1 - p2) / 2.0;

				// Vq(z)=((adc4+adc5*256+adc6*65536)-(adc10+adc11*256+adc12*65536))/2
				int p3 = (buffer[offset + 3] & 0x000000ff) + (buffer[offset + 4] & 0x000000ff) * 256 + (buffer[offset + 5] & 0x000000ff) * 65536;
				int p4 = (buffer[offset + 9] & 0x000000ff) + (buffer[offset + 10] & 0x000000ff) * 256 + (buffer[offset + 11] & 0x000000ff) * 65536;
				double imaginary = (p3 - p4) / 2.0;

				tempSample.setLoss(real);
				tempSample.setAngle(imaginary);
				tempSample.setFrequency(currentFrequency);

				// 4 phase data
				tempSample.setP1(p1);
				tempSample.setP2(p2);
				tempSample.setP3(p3);
				tempSample.setP4(p4);
				tempSample.setHasPData(true);

				rc[i] = tempSample;

				// next frequency
				currentFrequency += pFrequencyStep;
			}
			TraceHelper.text(this, methodName, "Last frequency requested was %d ", currentFrequency - pFrequencyStep);
			TraceHelper.text(this, methodName, "%d step(s) done", pNumSamples);
		}
		//
		if (pListener != null) {
			pListener.publishProgress(100);
		}

		TraceHelper.exit(this, methodName);
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.serial.VNADriverSerialBase#runVNA(boolean, int, int, int)
	 */
	public VNASampleBlock scan(VNAScanMode scanMode, long frequencyLow, long frequencyHigh, int numSamples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
		final String methodName = "scan";
		TraceHelper.entry(this, methodName);
		VNASampleBlock rc = new VNASampleBlock();

		//
		final VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB) getDeviceInfoBlock();

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
			// send sample rate
			sendAsAsciiString("" + dib.getSampleRate());
			// send number of samples
			sendAsAsciiString("" + numSamples);
			// send frequency step width
			final long frequencyStep = (frequencyHigh - frequencyLow) / (numSamples - 1); // -1
			sendFrequency(frequencyStep);

			TraceHelper.text(this, methodName, "Start %d", frequencyLow);
			TraceHelper.text(this, methodName, "Stop  %d", frequencyHigh);
			TraceHelper.text(this, methodName, "Steps %d", numSamples);
			TraceHelper.text(this, methodName, "Step  %d", frequencyStep);
			TraceHelper.text(this, methodName, "Last  %d", (frequencyLow + (numSamples - 1) * frequencyStep));

			rc.setSamples(receiveRawMessage(scanMode, frequencyLow, numSamples, frequencyStep, listener));

			// set output data
			rc.setScanMode(scanMode);
			rc.setNumberOfSteps(numSamples);
			rc.setStartFrequency(frequencyLow);
			rc.setStopFrequency(frequencyHigh);
			rc.setAnalyserType(getDeviceInfoBlock().getType());
			rc.setMathHelper(getMathHelper());

			// tell the world we are finished
			if (listener != null) {
				listener.publishProgress(100);
			}
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
		sendAsAsciiString(msg);
	}

	/**
	 * Send the attenuation in 1/100dB increments
	 * 
	 * @param att
	 * @throws ProcessingException
	 */
	protected void sendAttenuation(int att) throws ProcessingException {
		String msg = getFrequencyFormat().format(Math.pow(10, (60.2 - att / 100.0) / 20.0));
		sendAsAsciiString(msg);
	}

	/**
	 * Send the phase value in 1/100 degree
	 * 
	 * @param frq
	 * @throws ProcessingException
	 */
	protected void sendPhase(int phase) throws ProcessingException {
		String msg = getFrequencyFormat().format(((phase / 100.0) / 180.0) * 8192);
		sendAsAsciiString(msg);
	}

	/*
	 * Use the
	 * 
	 * @see krause.vna.device.IVNADriver#startGenerator(int)
	 */
	public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
		final String methodName = "startGenerator";
		TraceHelper.entry(this, methodName);

		VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB) getDeviceInfoBlock();

		TraceHelper.text(this, methodName, "fI=" + frequencyI);
		TraceHelper.text(this, methodName, "fQ=" + frequencyQ);

		TraceHelper.text(this, methodName, "aI=" + attenuationI);
		attenuationI = Math.max(0, attenuationI);
		TraceHelper.text(this, methodName, "aI=" + attenuationI + " !corrected");

		TraceHelper.text(this, methodName, "aQ=" + attenuationQ);
		attenuationQ = Math.max(0, attenuationQ);
		TraceHelper.text(this, methodName, "aQ=" + attenuationQ + " !corrected");

		TraceHelper.text(this, methodName, "ph=" + phase);

		if (getPort() != null) {
			flushInputStream();
			sendAsAsciiString("2"); // gen mode
			sendFrequency(frequencyI); // freq 1st channel
			sendFrequency(frequencyQ); // freq 2nd channel
			sendPhase(phase); // phase diff
			sendAsAsciiString("3"); // full power
			sendAttenuation(attenuationQ); // atten 2nd channel
			sendAttenuation(attenuationI); // atten 1st channel

			// and sleep a while after
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
			sendAsAsciiString("2"); // gen mode
			sendFrequency(0); // freq 1st channel
			sendFrequency(0); // freq 2nd channel
			sendAsAsciiString("0"); // phase diff
			sendAsAsciiString("3"); // full power
			sendAsAsciiString("0"); // atten 1st channel
			sendAsAsciiString("0"); // atten 2nd channel
			//
			VNADriverSerialPro2DIB dib = (VNADriverSerialPro2DIB) getDeviceInfoBlock();
			wait(dib.getAfterCommandDelay());
		}
		TraceHelper.exit(this, methodName);
	}

	public boolean supportsAutoReset() {
		return true;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.serial.VNADriverSerialBase#checkForDevicePresence()
	 */
	public boolean checkForDevicePresence(boolean viaSlowConnection) {
		boolean rc = false;
		final String methodName = "checkForDevicePresence";
		TraceHelper.entry(this, methodName);

		try {
			init();
			if (viaSlowConnection) {
				Thread.sleep(5000);
			}
			String fw = getDeviceFirmwareInfo();
			rc = fw.startsWith("PRO2 ");
		} catch (InterruptedException | InitializationException e) {
			ErrorLogHelper.exception(this, methodName, e);
		} finally {
			destroy();
		}
		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;
	}
}
