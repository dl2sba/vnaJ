package krause.vna.firmware;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.LogManager;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNAFlashableDevice;
import krause.vna.device.serial.VNADriverSerialBase;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class Chip45Loader extends VNABaseFirmwareFlasher {

	private static final byte LINE_END_CHIP45 = 0x0a;
	private static final byte LINE_END_MINIVNA = 0x0d;
	private static final long TIMEOUT_READ = 3000;

	private int oldBaudRate;
	private int oldDataBits;
	private int oldFlowMode;
	private int oldParity;
	private int oldStopBits;

	public Chip45Loader() {
		TraceHelper.entry(this, "Chip45Loader");
		TraceHelper.exit(this, "Chip45Loader");
	}

	/**
	 * 
	 * @param port
	 * @param line
	 * @param lineNo
	 * @throws ProcessingException
	 */
	private void bootloaderSendLine(SerialPort port, String line, int lineNo) throws ProcessingException {
		flushInputBuffer(port);
		try {
			OutputStream oStream = port.getOutputStream();
			for (int i = 0; i < line.length(); ++i) {
				oStream.write((byte) line.charAt(i));
			}
			oStream.write(LINE_END_CHIP45);

			char c = (char) readChar(port, TIMEOUT_READ);
			if (c == '.') {
				// ignore .
			} else if (c == '*') {
				getMessenger().publishMessage("Flashed page " + lineNo);
			} else {
				throw new ProcessingException("Flash error");
			}
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "bootloaderSendLine", e);
			throw new ProcessingException(e);
		}
	}

	/**
	 * 
	 * @param port
	 * @throws IOException
	 */
	private void bootloaderSendResetCommand(SerialPort port) throws IOException {
		TraceHelper.entry(this, "bootloaderSendResetCommand");
		getMessenger().publishMessage("Resetting miniVNAtiny");
		OutputStream oStream = port.getOutputStream();
		oStream.write('9');
		oStream.write('9');
		oStream.write(LINE_END_MINIVNA);
		oStream.flush();
		TraceHelper.exit(this, "bootloaderSendResetCommand");
	}

	/**
	 * 
	 * @param port
	 * @throws ProcessingException
	 */
	private void bootloaderStartApplication(SerialPort port) throws ProcessingException {
		final String methodName = "bootloaderStartApplication";

		TraceHelper.entry(this, methodName);

		getMessenger().publishMessage("Restarting analyser ...");

		flushInputBuffer(port);

		try {
			OutputStream oStream = port.getOutputStream();
			// send some other stuff
			oStream.write('g');
			oStream.write(LINE_END_CHIP45);
			oStream.flush();

			if (readChar(port, TIMEOUT_READ) == 'g') {
				if (readChar(port, TIMEOUT_READ) == '+') {
					// ignore the prompt
					flushInputBuffer(port);
					getMessenger().publishMessage("Device resetting");
				} else {
					throw new ProcessingException("+ missing");
				}
			} else {
				throw new ProcessingException("g missing");
			}
		} catch (IOException e) {
			ErrorLogHelper.exception(this, methodName, e);
			throw new ProcessingException(e);
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 * @param port
	 * @throws ProcessingException
	 */
	private void bootloaderStartCommandMode(SerialPort port) throws ProcessingException {
		TraceHelper.entry(this, "bootloaderStartCommandMode");
		getMessenger().publishMessage("Entering bootloader State #1");
		flushInputBuffer(port);
		try {
			OutputStream oStream = port.getOutputStream();

			// now send a number of Us
			for (int i = 0; i < 100; ++i) {
				oStream.write('U');
				oStream.flush();
				sleep(10);
			}

			getMessenger().publishMessage("Entering bootloader State #2");

			// now read the chars and wait for prompt char ">"
			while (readChar(port, TIMEOUT_READ) != '>')
				;

			getMessenger().publishMessage("Bootloader prompt received");

			// now check, whether loader really active
			oStream.write(LINE_END_CHIP45);
			if (readChar(port, TIMEOUT_READ) == '-') {
				// ignore the prompt
				flushInputBuffer(port);
			} else {
				throw new ProcessingException("- missing");
			}
			getMessenger().publishMessage("Bootloader in command mode");

		} catch (IOException e) {
			throw new ProcessingException(e);
		}
		TraceHelper.exit(this, "bootloaderStartCommandMode");
	}

	/**
	 * 
	 * @param port
	 * @throws ProcessingException
	 */
	private void bootloaderStartFlashMode(SerialPort port) throws ProcessingException {
		TraceHelper.entry(this, "bootloaderStartFlash");

		getMessenger().publishMessage("Start flash process ...");
		flushInputBuffer(port);

		try {
			OutputStream oStream = port.getOutputStream();
			// send some other stuff
			oStream.write('p');
			oStream.write('f');
			oStream.write(LINE_END_CHIP45);
			oStream.flush();

			// now read the response - should be "pf+"
			if (readChar(port, 500) == 'p') {
				if (readChar(port, 500) == 'f') {
					if (readChar(port, 500) == '+') {
						// ignore the prompt
						flushInputBuffer(port);
						getMessenger().publishMessage("Flash process started");
					} else {
						throw new ProcessingException("+ missing");
					}
				} else {
					throw new ProcessingException("f missing");
				}
			} else {
				throw new ProcessingException("p missing");
			}
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "bootloaderEnterCommandMode", e);
			throw new ProcessingException(e);
		}
		TraceHelper.exit(this, "bootloaderStartFlash");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.firmware.IVNAFirmwareFlasher#burnBuffer(krause.vna.firmware .HexFileParser, krause.vna.device.IVNADriver)
	 */
	public void burnBuffer(FirmwareFileParser hfp, IVNADriver driver) throws ProcessingException {
		final String methodName = "burnBuffer";
		TraceHelper.entry(this, methodName);

		final VNADriverSerialBase serialDriver = (VNADriverSerialBase) driver;
		final SerialPort port = serialDriver.getPort();

		try {
			// save old comm parms
			safeCommParms(port);

			// now send reset to tiny with old parms
			bootloaderSendResetCommand(port);

			// set com port parameters of bootloader
			IVNAFlashableDevice fdev = (IVNAFlashableDevice) driver;

			setBootloaderCommParms(port, fdev.getFirmwareLoaderBaudRate());

			// try to enter command mode of bootloader
			bootloaderStartCommandMode(port);

			// now burn the buffer
			final int minAdr = hfp.getFlashMin();
			final int maxAdr = hfp.getFlashMax() + 1;
			final int blockSize = 16;
			final int fullBlocks = (maxAdr - minAdr) / blockSize;
			int lastBlock = (maxAdr - minAdr) - (fullBlocks * blockSize);

			// tell the bootloader hex download starts
			bootloaderStartFlashMode(port);

			// now download the data to the device
			final int addr = minAdr;
			int i = 0;
			for (i = 0; i < fullBlocks; ++i) {
				String line = hfp.getFlashAsHexFileLine(addr + i * blockSize, blockSize);
				bootloaderSendLine(port, line, i);
			}
			if (lastBlock != 0) {
				String line = hfp.getFlashAsHexFileLine(addr + blockSize * fullBlocks, lastBlock);
				bootloaderSendLine(port, line, i);
			}

			// send last line to the device
			bootloaderSendLine(port, hfp.getLastHexFileLine(), 0);

			// wait 1s for bootloader
			sleep(1000);

			// and now restart application
			bootloaderStartApplication(port);
		} catch (IOException | UnsupportedCommOperationException e) {
			ErrorLogHelper.exception(this, methodName, e);
			throw new ProcessingException(e);
		} finally {
			try {
				// finally we must go back to the old parms for regular scanning
				restoreCommParms(port);
			} catch (UnsupportedCommOperationException e) {
				ErrorLogHelper.exception(this, methodName, e);
			}
		}
		TraceHelper.exit(this, methodName);
	}

	private void flushInputBuffer(SerialPort port) throws ProcessingException {
		try {
			final InputStream iStream = port.getInputStream();
			final int avl = iStream.available();
			for (int i = 0; i < avl; ++i) {
				iStream.read();
			}
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "bootloaderEnterCommandMode", e);
			throw new ProcessingException(e);
		}
	}

	/**
	 * Read one character from the given port with given timeout
	 * 
	 * @param port
	 * @param timeout
	 * @return
	 * @throws ProcessingException
	 */
	private int readChar(SerialPort port, long timeout) throws ProcessingException {
		int rc = -1;
		final long endTime = System.currentTimeMillis() + timeout;
		try {
			final InputStream stream = port.getInputStream();
			while (stream.available() == 0) {
				sleep(10);
				if (System.currentTimeMillis() > endTime) {
					throw new ProcessingException("No char received in after " + timeout + "ms");
				}
			}
			rc = stream.read();
			if (LogManager.getSingleton().isTracingEnabled()) {
				if (rc >= 32) {
					LogManager.getSingleton().getTracer().text(this, "readChar", "'" + (char) rc + "'/" + rc);
				} else {
					LogManager.getSingleton().getTracer().text(this, "readChar", "CTRL-" + (char) (rc + 64) + "/" + rc);
				}
			}
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "readChar", e);
			throw new ProcessingException(e);
		}
		return rc;
	}

	private void restoreCommParms(SerialPort port) throws UnsupportedCommOperationException {
		TraceHelper.entry(this, "restoreCommParms");
		port.setFlowControlMode(oldFlowMode);
		port.setSerialPortParams(oldBaudRate, oldDataBits, oldStopBits, oldParity);

		getMessenger().publishMessage("Using " + oldBaudRate + "Bd");
		TraceHelper.exit(this, "restoreCommParms");
	}

	private void safeCommParms(SerialPort port) {
		TraceHelper.entry(this, "safeCommParms");
		oldBaudRate = port.getBaudRate();
		oldDataBits = port.getDataBits();
		oldFlowMode = port.getFlowControlMode();
		oldParity = port.getParity();
		oldStopBits = port.getStopBits();
		TraceHelper.exit(this, "safeCommParms");
	}

	private void setBootloaderCommParms(SerialPort port, int baud) throws UnsupportedCommOperationException {
		TraceHelper.entry(this, "setBootloaderCommParms");

		port.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN + SerialPort.FLOWCONTROL_XONXOFF_OUT);
		port.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

		getMessenger().publishMessage("Using " + baud + "Bd");

		TraceHelper.exit(this, "setBootloaderCommParms");
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// not implemented
		}
	}
}
