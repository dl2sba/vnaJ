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

public class MCSLoader extends VNABaseFirmwareFlasher {

	// Constants for xmodem protocol
	private static final byte NAK = 0x15;
	private static final byte ACK = 0x06;
	private static final byte SOH = 0x01;
	private static final byte EOT = 0x04;
	private static final int BUFFER_SIZE = 128;
	private static final int DATA_OFFSET = 3;
	private static final int CHECKSUM_OFFSET = DATA_OFFSET + BUFFER_SIZE;
	private static final long TIMEOUT_READ = 1000;

	private int oldBaudRate;
	private int oldDataBits;
	private int oldFlowMode;
	private int oldParity;
	private int oldStopBits;

	public MCSLoader() {
		TraceHelper.entry(this, "MCSLoader");
		TraceHelper.exit(this, "MCSLoader");
	}

	/**
	 * 
	 * @param port
	 * @throws IOException
	 */
	private void bootloaderSendResetCommand(SerialPort port) throws IOException {
		TraceHelper.entry(this, "bootloaderSendResetCommand");
		getMessenger().publishMessage("Resetting analyzer");
		OutputStream oStream = port.getOutputStream();
		oStream.write('9');
		oStream.write('9');
		oStream.write(0x0d);
		oStream.flush();
		TraceHelper.exit(this, "bootloaderSendResetCommand");
	}

	/**
	 * 
	 * @param port
	 * @throws ProcessingException
	 */
	private void bootloaderStartCommandMode(SerialPort port) throws ProcessingException {
		final String methodName = "bootloaderStartCommandMode";
		TraceHelper.entry(this, methodName);

		getMessenger().publishMessage("Entering bootloader ...");
		flushInputBuffer(port);

		boolean connected = false;

		try {
			OutputStream oStream = port.getOutputStream();
			for (int i = 0; i < 100; ++i) {
				try {
					oStream.write(123);
					oStream.flush();

					int ch;
					// now read the chars and wait for prompt char "{"
					do {
						ch = readChar(port, 100);
						TraceHelper.text(this, methodName, "ch=" + ch);
					} while (ch != 123);
					connected = true;
					break;
				} catch (ProcessingException e) {
					// not used
				}
			}
			if (connected) {
				getMessenger().publishMessage("Bootloader in command mode");
			} else {
				throw new ProcessingException("No response from bootloader");
			}
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
		TraceHelper.entry(this, methodName);
	}

	/**
	 * 
	 * @param port
	 * @throws ProcessingException
	 */
	private void bootloaderStartFlashMode(SerialPort port) throws ProcessingException {
		final String methodName = "bootloaderStartFlashMode";
		TraceHelper.entry(this, methodName);

		getMessenger().publishMessage("Waiting for bootloader start request ...");

		int ch;
		// now read the chars and wait for NAK
		do {
			ch = readChar(port, 1000);
			TraceHelper.text(this, methodName, "ch=" + ch);
		} while (ch != NAK);

		getMessenger().publishMessage("Bootloader start request received");

		TraceHelper.exit(this, methodName);
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

		OutputStream oStream = null;
		try {
			oStream = port.getOutputStream();

			// save old comm parms
			safeCommParms(port);

			// now send reset to tiny with old parms
			bootloaderSendResetCommand(port);

			// set com port parameters of bootloader
			IVNAFlashableDevice fdev = (IVNAFlashableDevice) driver;

			setBootloaderCommParms(port, fdev.getFirmwareLoaderBaudRate());

			// try to enter command mode of bootloader
			bootloaderStartCommandMode(port);

			// tell the bootloader hex download starts
			bootloaderStartFlashMode(port);

			byte[] fileBuffer = hfp.getFlash();
			int fileRemaining = fileBuffer.length;

			getMessenger().publishMessage("Will send " + fileRemaining / BUFFER_SIZE + " full packets to the AVR");

			byte[] flashBuffer = new byte[BUFFER_SIZE + 4];
			int packetNum = 1;
			byte last_response = ACK;
			int fileOffset = 0;
			int toCopy = BUFFER_SIZE;

			do {
				// Send the packet number
				flashBuffer[0] = SOH;
				flashBuffer[1] = (byte) packetNum;
				flashBuffer[2] = (byte) ~flashBuffer[1];

				if (last_response != NAK) {
					if (fileRemaining <= BUFFER_SIZE) {
						toCopy = fileRemaining;
						getMessenger().publishMessage("Sending LAST Page " + packetNum + " with " + toCopy + " bytes ...");
					}
					TraceHelper.text(this, methodName, "to copy  =" + toCopy);

					for (int i = 0; i < toCopy; ++i) {
						flashBuffer[DATA_OFFSET + i] = fileBuffer[fileOffset + i];
					}
					fileRemaining -= toCopy;
					fileOffset += toCopy;
					TraceHelper.text(this, methodName, "remaining=" + fileRemaining);
					TraceHelper.text(this, methodName, "offset   =" + fileOffset);
				}

				TraceHelper.text(this, methodName, "[0]=" + flashBuffer[0]);
				TraceHelper.text(this, methodName, "[1]=" + flashBuffer[1]);
				TraceHelper.text(this, methodName, "[2]=" + flashBuffer[2]);

				// calculate checksum on flash buffer
				int chksum = 0;
				for (int i = DATA_OFFSET; i < DATA_OFFSET + BUFFER_SIZE; i++) {
					chksum += flashBuffer[i];
				}
				flashBuffer[CHECKSUM_OFFSET] = (byte) chksum;

				// now send the buffer out to the device
				oStream.write(flashBuffer, 0, flashBuffer.length);
				oStream.flush();

				byte response = (byte) readChar(port, TIMEOUT_READ);
				if (response == ACK) {
					getMessenger().publishMessage("Page " + packetNum + " flashed");
					packetNum++;
				} else if (response == NAK) {
					getMessenger().publishMessage("Page " + packetNum + " failed");
					int csSend = readChar(port, TIMEOUT_READ);
					int csAVR = readChar(port, TIMEOUT_READ);
					getMessenger().publishMessage(" CS local=" + chksum);
					getMessenger().publishMessage(" CS sent =" + csSend);
					getMessenger().publishMessage(" CS AVR  =" + csAVR);

					last_response = NAK;
				} else {
					String msg = "Unknown response from bootloader [" + (int) response + "]";
					getMessenger().publishMessage(msg);
					throw new ProcessingException(msg);
				}
			} while (fileRemaining > 0);

			// now tell the bootloader, that the world ends here
			oStream.write(EOT);
			byte response = (byte) readChar(port, TIMEOUT_READ);
			if (response == ACK) {
				getMessenger().publishMessage("Flashing done");
			} else {
				String msg = "Unknown response from bootloader [" + (int) response + "] for END FLASH";
				getMessenger().publishMessage(msg);
				throw new ProcessingException(msg);
			}

		} catch (IOException | UnsupportedCommOperationException e) {
			ErrorLogHelper.exception(this, methodName, e);
			throw new ProcessingException(e);
		} finally {
			try {
				// finally we must go back to the old parms for regular scanning
				restoreCommParms(port);
			} catch (UnsupportedCommOperationException e) {
				ErrorLogHelper.exception(this, methodName, e);
				throw new ProcessingException(e);
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
	 *             when timeout occurs
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

		port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		port.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

		getMessenger().publishMessage("Using " + baud + "Bd");

		TraceHelper.exit(this, "setBootloaderCommParms");
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// not used
		}
	}
}
