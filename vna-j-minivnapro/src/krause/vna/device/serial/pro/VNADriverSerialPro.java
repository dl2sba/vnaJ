/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.serial.pro;

import java.io.IOException;
import java.io.InputStream;

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
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.pro.generator.VNAGeneratorProDialog;
import krause.vna.device.serial.pro.gui.VNADriverSerialProDialog;
import krause.vna.firmware.MegaLoadLoader;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialPro extends VNADriverSerialBase implements IVNAFlashableDevice {

	public static final int NUM_BYTES_PER_SAMPLE = 8;

	/**
	 * 
	 */
	public VNADriverSerialPro() {
		TraceHelper.entry(this, "VNADriverSerialPro");
		setMathHelper(new VNADriverSerialProMathHelper(this));
		setDeviceInfoBlock(new VNADriverSerialProDIB());
		getDeviceInfoBlock().restore(this.config, getDriverConfigPrefix());
		TraceHelper.exit(this, "VNADriverSerialPro");
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
			TraceHelper.text(this, methodName, "closing " + getPort().getName());
			getPort().close();
			setPort(null);
			TraceHelper.text(this, methodName, "port closed");
		}
		TraceHelper.exit(this, methodName);
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
		} catch (Exception ex) {

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
		final String methodName = "getDeviceSupply";
		TraceHelper.entry(this, methodName);
		Double rc = null;
		InputStream stream = null;

		if (getPort() != null) {
			try {
				flushInputStream();
				sendAsAsciiString("8");

				byte innerdata[] = new byte[2];

				int ch = readBuffer(innerdata, 0, 2);
				if (ch == -1) {
					ProcessingException e = new ProcessingException("No chars received");
					ErrorLogHelper.exception(this, methodName, e);
					throw e;
				}
				rc = ((innerdata[0] & 0x000000ff) + (innerdata[1] & 0x000000ff) * 256) * 6 / 1024.0;
			} catch (ProcessingException e1) {
			} finally {
				if (stream != null) {
					try {
						stream.close();
						stream = null;
					} catch (IOException e) {
						ErrorLogHelper.exception(this, methodName, e);
					}
				}
			}
		}
		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;
	}

	public String getDriverConfigPrefix() {
		return "VNADriver.Serial.Pro.";
	}

	@Override
	public String getFirmwareLoaderClassName() {
		return MegaLoadLoader.class.getName();
	}

	@Override
	public VNACalibrationRange[] getSpecificCalibrationRanges() {
		long min = getDeviceInfoBlock().getMinFrequency();
		long max = getDeviceInfoBlock().getMaxFrequency();

		return new VNACalibrationRange[] {
				new VNACalibrationRange(min, 999999, 4000, 1), // 225Hz
				new VNACalibrationRange(1000000, 9999999, 4000, 1), // 2.25kHz
				new VNACalibrationRange(10000000, 29999999, 10000, 1), // 2kHz
				new VNACalibrationRange(30000000, max, 10000, 1), // 17kHz
		};
	}

	@Override
	public boolean hasResetButton() {
		return true;
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
		VNADriverSerialProDIB dib = (VNADriverSerialProDIB) getDeviceInfoBlock();
		dib.restore(this.config, getDriverConfigPrefix());
		TraceHelper.text(this, methodeName, "Trying to open port [%s]", getPortname());
		//
		try {
			CommPortIdentifier portId = getPortIDForName(getPortname());
			if (portId != null) {
				TraceHelper.text(this, methodeName, "port [%s] found", getPortname());
				SerialPort aPort = (SerialPort) portId.open(getAppname(), dib.getOpenTimeout());

				TraceHelper.text(this, methodeName, "port [%s] opened", getPortname());

				aPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

				TraceHelper.text(this, methodeName, "port [%s] set to [%d]Bd", getPortname(), dib.getBaudrate());
				aPort.setSerialPortParams(dib.getBaudrate(), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				aPort.enableReceiveTimeout(dib.getReadTimeout());
				aPort.setInputBufferSize(DEFAULT_INPUTBUFFER_SIZE);

				TraceHelper.text(this, methodeName, "port [%s] setup done", getPortname());

				setPort(aPort);
			} else {
				InitializationException e = new InitializationException("Port [" + getPortname() + "] not found");
				ErrorLogHelper.exception(this, methodeName, e);
				throw e;
			}
		} catch (PortInUseException | UnsupportedCommOperationException e) {
			ErrorLogHelper.exception(this, methodeName, e);
			throw new InitializationException(e);
		}
		TraceHelper.exit(this, methodeName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#isScanSupported(int, krause.vna.data.VNAFrequencyRange, krause.vna.data.VNAScanMode)
	 */
	public boolean isScanSupported(int numSamples, VNAFrequencyRange range, VNAScanMode mode) {
		// VNADriverSerialProDIB dib = (VNADriverSerialProDIB)
		// getDeviceInfoBlock();
		// if (dib.getFirmwareVersion() < VNADriverSerialProDIB.FIRMWARE_2_3) {
		// if (numSamples < 100) {
		// return false;
		// } else if (numSamples % 100 != 0) {
		// return false;
		// }
		// }
		return true;
	}

	/**
	 * 
	 * @param pStartFrequency
	 * @param pSamples
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
		TraceHelper.text(this, methodName, "Last frequency stored was " + (currentFrequency - pFrequencyStep));

		//
		if (pListener != null) {
			pListener.publishProgress(100);
		}
		//
		TraceHelper.exit(this, methodName);
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.serial.VNADriverSerialBase#runVNA(boolean, int, int, int)
	 */
	public VNASampleBlock scan(VNAScanMode mode, long frequencyLow, long frequencyHigh, int requestedSamples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
		final String methodName = "scan";
		TraceHelper.entry(this, methodName);
		VNASampleBlock rc = new VNASampleBlock();

		if (getPort() != null) {
			final VNADriverSerialProDIB dib = (VNADriverSerialProDIB) getDeviceInfoBlock();
			//
			rc.setDeviceTemperature(getDeviceTemperature());
			rc.setDeviceSupply(getDeviceSupply());

			//
			flushInputStream();
			// send mode
			if (mode.isTransmissionMode()) {
				if (dib.isFixed6dBOnThru()) {
					sendAsAsciiString("20");
				} else {
					sendAsAsciiString("0");
				}
			} else if (mode.isReflectionMode()) {
				sendAsAsciiString("1");
			} else {
				throw new ProcessingException("Unsupported scan mode " + mode);
			}
			// send frequency
			sendFrequency(frequencyLow);
			// send phase - currently 0
			sendAsAsciiString("0");
			// handle old vnaPRO limitation
			boolean oldVNA = (dib.getFirmwareVersion() < VNADriverSerialProDIB.FIRMWARE_2_3);
			int realSamples = requestedSamples;
			if (oldVNA) {
				if (requestedSamples < 100) {
					realSamples = 100;
				} else {
					if (requestedSamples % 100 != 0) {
						realSamples = ((requestedSamples / 100) + 1) * 100;
					}
				}
			}

			// send number of samples
			sendAsAsciiString(Integer.toString(realSamples));

			// send frequency step width
			final long frequencyStep = (frequencyHigh - frequencyLow) / (realSamples - 1);
			sendFrequency(frequencyStep);

			TraceHelper.text(this, methodName, "Start %d", frequencyLow);
			TraceHelper.text(this, methodName, "Stop  %d", frequencyHigh);
			TraceHelper.text(this, methodName, "Steps %d", requestedSamples);
			TraceHelper.text(this, methodName, "Step  %d", frequencyStep);
			TraceHelper.text(this, methodName, "Last %d", (frequencyLow + (requestedSamples - 1) * frequencyStep));

			// receive data
			final VNABaseSample[] readSamples = receiveRawMessage(frequencyLow, realSamples, frequencyStep, listener);

			if (requestedSamples != realSamples) {
				final VNABaseSample[] newSamples = new VNABaseSample[requestedSamples];
				for (int i = 0; i < requestedSamples; ++i) {
					newSamples[i] = readSamples[i];
				}
				rc.setSamples(newSamples);
			} else {
				// set scan data in result data
				rc.setSamples(readSamples);
			}
			//
			rc.setScanMode(mode);
			rc.setNumberOfSteps(requestedSamples);
			rc.setStartFrequency(frequencyLow);
			rc.setStopFrequency(frequencyHigh);
			rc.setAnalyserType(getDeviceInfoBlock().getType());
			rc.setMathHelper(getMathHelper());
		}
		TraceHelper.exit(this, methodName);
		return rc;
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
	 * Send the frequency in DDS ticks
	 * 
	 * send freq in MHz * DDSTickPerMHZ
	 * 
	 * @param frq
	 * @throws ProcessingException
	 */
	protected void sendFrequency(long frq) throws ProcessingException {
		String msg = getFrequencyFormat().format((frq / 1000000.0) * getDeviceInfoBlock().getDdsTicksPerMHz());
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
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#showDriverDialog(krause.vna.gui.VNAMainFrame )
	 */
	public void showDriverDialog(VNAMainFrame pMF) {
		TraceHelper.entry(this, "showDriverDialog");
		VNADriverSerialProDialog dlg = new VNADriverSerialProDialog(pMF, this);
		dlg.dispose();
		TraceHelper.exit(this, "showDriverDialog");
	}

	public void showGeneratorDialog(VNAMainFrame pMF) throws DialogNotImplementedException {
		TraceHelper.entry(this, "showGeneratorDialog");
		VNAGeneratorProDialog dlg = new VNAGeneratorProDialog(pMF, this);
		dlg.showInPlace();
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

		TraceHelper.text(this, methodName, "aI=" + attenuationI);
		attenuationI += (dib.getAttenOffsetI() * 100.0);
		attenuationI = Math.max(0, attenuationI);
		TraceHelper.text(this, methodName, "aI=" + attenuationI + " !corrected");

		TraceHelper.text(this, methodName, "aQ=" + attenuationQ);
		attenuationQ += (dib.getAttenOffsetQ() * 100.0);
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
			VNADriverSerialProDIB dib = (VNADriverSerialProDIB) getDeviceInfoBlock();
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
	 * @see krause.vna.device.IVNAFlashableDevice#getBootloadBaudRate()
	 */
	public int getFirmwareLoaderBaudRate() {
		VNADriverSerialProDIB dib = (VNADriverSerialProDIB) getDeviceInfoBlock();
		return dib.getBaudrate();
	}
}
