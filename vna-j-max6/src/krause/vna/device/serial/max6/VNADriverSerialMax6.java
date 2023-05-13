/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.device.serial.max6;

import java.text.MessageFormat;

import krause.common.exception.DialogNotImplementedException;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.device.serial.VNADriverSerialBase;
import krause.vna.device.serial.max6.gui.VNADriverSerialMax6Dialog;
import krause.vna.device.serial.max6.gui.VNAGeneratorMAX6Dialog;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class VNADriverSerialMax6 extends VNADriverSerialBase {

	public VNADriverSerialMax6() {
		final String methodName = "VNADriverSerialMax6";
		TraceHelper.entry(this, methodName);

		setMathHelper(new VNADriverSerialMax6MathHelper(this));
		setDeviceInfoBlock(new VNADriverSerialMax6DIB());
		//
		getDeviceInfoBlock().restore(this.config, getDriverConfigPrefix());
		TraceHelper.exit(this, methodName);
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
		VNADriverSerialMax6DIB dib = (VNADriverSerialMax6DIB) getDeviceInfoBlock();
		dib.restore(this.config, getDriverConfigPrefix());
		//
		try {
			CommPortIdentifier portId = getPortIDForName(getPortname());
			if (portId != null) {
				setPort((SerialPort) portId.open(getAppname(), dib.getOpenTimeout()));
				getPort().setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				getPort().setSerialPortParams(dib.getBaudrate(), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				getPort().enableReceiveTimeout(dib.getReadTimeout());
				getPort().setInputBufferSize(20000);

				TraceHelper.text(this, methodeName, "getReceiveThreshold=" + getPort().getReceiveThreshold());
				TraceHelper.text(this, methodeName, "getInputBufferSize=" + getPort().getInputBufferSize());
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

	/**
	 * 
	 * @param dib
	 * @param mode
	 * @param frequency
	 * @param samples
	 * @param frequencyStep
	 * @param listener
	 * @param transmission
	 * @return
	 * @throws ProcessingException
	 */
	private VNABaseSample[] receiveRawMessage(VNAScanMode mode, long frequency, int samples, long frequencyStep, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
		final String methodName = "receiveRawMessage";
		TraceHelper.entry(this, methodName);

		VNABaseSample[] rc = new VNABaseSample[samples];
		int totalChars = 0;

		long localFrequency = frequency;
		byte[] innerdata = new byte[20];
		//
		if (listener != null) {
			listener.publishProgress(0);
		}

		//
		for (int i = 0; i < samples; ++i) {
			if (listener != null) {
				// issue life signs
				if (i % 100 == 0) {
					listener.publishProgress((int) (i * 100.0 / samples));
				}
			}
			VNABaseSample tempSample = new VNABaseSample();

			// rss1 mode
			if (mode.isRss1Mode()) {
				// yes
				int read = readBuffer(innerdata, 0, 6);
				if (read != 6) {
					String msg = MessageFormat.format(VNADriverSerialMax6Messages.getString("NoChars"), i, read);

					ProcessingException e = new ProcessingException(msg);
					ErrorLogHelper.exception(this, methodName, e);
					throw e;
				}
				totalChars += read;
				tempSample.setAngle((innerdata[0] & 0x000000ff) + 256 * (innerdata[1] & 0x000000ff));
				tempSample.setLoss((innerdata[2] & 0x000000ff) + 256 * (innerdata[3] & 0x000000ff));
				tempSample.setRss1((innerdata[4] & 0x000000ff) + 256 * (innerdata[5] & 0x000000ff));
				tempSample.setFrequency(localFrequency);

			} else if (mode.isRss3Mode()) {
				// yes
				int read = readBuffer(innerdata, 0, 10);
				if (read != 10) {
					String msg = MessageFormat.format(VNADriverSerialMax6Messages.getString("NoChars"), i, read);

					ProcessingException e = new ProcessingException(msg);
					ErrorLogHelper.exception(this, methodName, e);
					throw e;
				}
				totalChars += read;
				tempSample.setAngle((innerdata[0] & 0x000000ff) + 256 * (innerdata[1] & 0x000000ff));
				tempSample.setLoss((innerdata[2] & 0x000000ff) + 256 * (innerdata[3] & 0x000000ff));
				tempSample.setRss1((innerdata[4] & 0x000000ff) + 256 * (innerdata[5] & 0x000000ff));
				tempSample.setRss2((innerdata[6] & 0x000000ff) + 256 * (innerdata[7] & 0x000000ff));
				tempSample.setRss3((innerdata[8] & 0x000000ff) + 256 * (innerdata[9] & 0x000000ff));
				tempSample.setFrequency(localFrequency);

				// reflection mode ?
			} else if (mode.isReflectionMode()) {
				// yes
				int read = readBuffer(innerdata, 0, 4);
				if (read != 4) {
					String msg = MessageFormat.format(VNADriverSerialMax6Messages.getString("NoChars"), i, read);

					ProcessingException e = new ProcessingException(msg);
					ErrorLogHelper.exception(this, methodName, e);
					throw e;
				}
				totalChars += read;
				tempSample.setAngle((innerdata[0] & 0x000000ff) + 256 * (innerdata[1] & 0x000000ff));
				tempSample.setLoss((innerdata[2] & 0x000000ff) + 256 * (innerdata[3] & 0x000000ff));
				tempSample.setFrequency(localFrequency);
			}
			// store in rc
			rc[i] = tempSample;
			// next frequency
			localFrequency += frequencyStep;
		}
		//
		if (listener != null) {
			listener.publishProgress(100);
		}
		TraceHelper.exitWithRC(this, methodName, totalChars + " chars received");
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
	 * @see krause.vna.device.IVNADriver#runVNAInRawMode(boolean, int, int, int)
	 */
	public VNASampleBlock scan(VNAScanMode mode, long frequencyLow, long frequencyHigh, int samples, IVNABackgroundTaskStatusListener listener) throws ProcessingException {
		final String methodName = "scan";
		TraceHelper.entry(this, methodName);
		long frequencyStep = (frequencyHigh - frequencyLow) / samples;

		VNADriverSerialMax6DIB dib = (VNADriverSerialMax6DIB) getDeviceInfoBlock();
		VNASampleBlock rc = new VNASampleBlock();
		rc.setAnalyserType(getDeviceInfoBlock().getType());
		rc.setScanMode(mode);
		rc.setNumberOfSteps(samples);
		rc.setStartFrequency(frequencyLow);
		rc.setStopFrequency(frequencyHigh);
		rc.setMathHelper(getMathHelper());
		try {
			if (getPort() != null) {
				flushInputStream();

				if (mode.isRss1Mode()) {
					sendAsAsciiString("M2");
				} else if (mode.isRss3Mode()) {
					sendAsAsciiString("M3");
				} else if (mode.isReflectionMode()) {
					sendAsAsciiString("M0");
				} else {
					throw new ProcessingException("Unsupported scan mode " + mode);
				}

				Thread.sleep(dib.getAfterCommandDelay());
				sendFrequency(frequencyLow);

				Thread.sleep(dib.getAfterCommandDelay());
				sendAsAsciiString(Integer.toString(samples));

				Thread.sleep(dib.getAfterCommandDelay());
				sendFrequency(frequencyStep);
				// receive data
				rc.setSamples(receiveRawMessage(mode, frequencyLow, samples, frequencyStep, listener));
			}
		} catch (InterruptedException e) {
			ProcessingException p = new ProcessingException(e);
			ErrorLogHelper.exception(this, methodName, p);
			throw p;
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
		VNADriverSerialMax6Dialog dlg = new VNADriverSerialMax6Dialog(pMF, this);
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
		final String methodName = "showGeneratorDialog";
		TraceHelper.entry(this, methodName);
		new VNAGeneratorMAX6Dialog(pMF, this);
		TraceHelper.exit(this, methodName);
	}

	@Override
	public String getDeviceFirmwareInfo() {
		String rc = "";
		final String methodName = "readFirmwareVersion";
		TraceHelper.entry(this, methodName);

		try {
			rc += "Version:";
			rc += readVersion();
			Thread.sleep(((VNADriverSerialMax6DIB) getDeviceInfoBlock()).getAfterCommandDelay());
			rc += GlobalSymbols.LINE_SEPARATOR;
			rc += "Serial:";
			rc += readSerial();
		} catch (InterruptedException | ProcessingException e) {
			ErrorLogHelper.exception(this, methodName, e);
		}

		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;
	}

	/*
	 * 
	 */
	private String readVersion() {
		String rc = "???";
		final String methodName = "readVersion";
		TraceHelper.entry(this, methodName);

		try {
			flushInputStream();
			sendAsAsciiString("version");
			rc = readLine(false);
			rc += readLine(true);
		} catch (Exception e) {
			ErrorLogHelper.exception(this, methodName, e);
		}
		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;
	}

	/**
	 * 
	 * @return
	 * @throws ProcessingException
	 */
	private String readSerial() throws ProcessingException {
		String rc = "";
		final String methodName = "readSerial";
		TraceHelper.entry(this, methodName);

		flushInputStream();
		sendAsAsciiString("serial");
		rc = readLine(true);

		TraceHelper.exitWithRC(this, methodName, rc);
		return rc;
	}

	/**
	 * 
	 * @param att
	 * @throws ProcessingException
	 */
	protected void sendAttenuation(int att) throws ProcessingException {
		String msg = getFrequencyFormat().format(16383 - att);
		sendAsAsciiString(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#startGenerator(int, int, int, int, int, int)
	 */
	public void startGenerator(long frequencyI, long frequencyQ, int attenuationI, int attenuationQ, int phase, int mainAttenuation) throws ProcessingException {
		final String methodName = "startGenerator";
		TraceHelper.entry(this, methodName);
		TraceHelper.text(this, methodName, "fI=" + frequencyI);
		TraceHelper.text(this, methodName, "fQ=" + frequencyQ);
		TraceHelper.text(this, methodName, "aI=" + attenuationI);
		TraceHelper.text(this, methodName, "aQ=" + attenuationQ);
		TraceHelper.text(this, methodName, "ph=" + phase);

		if (getPort() != null) {
			flushInputStream();
			sendAsAsciiString("M4");
			sendFrequency(frequencyI);
			sendAsAsciiString("1");
			sendAsAsciiString("0");
			sendAttenuation(attenuationI);
			receiveRawMessage(VNAScanMode.MODE_RSS3, frequencyI, 1, 0, null);
		}
		TraceHelper.exit(this, methodName);
	}

	public String getDriverConfigPrefix() {
		return "VNADriver.Serial.MAX6.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.IVNADriver#readPowerStatus()
	 */
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
}