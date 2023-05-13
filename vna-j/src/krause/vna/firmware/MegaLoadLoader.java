package krause.vna.firmware;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.serial.VNADriverSerialBase;
import purejavacomm.SerialPort;

public class MegaLoadLoader extends VNABaseFirmwareFlasher {

	enum MemoryType {
		FLASH, EEPROM
	}

	/**
	 * 
	 */
	public MegaLoadLoader() {
		final String methodName = "MegaLoadLoader";
		TraceHelper.entry(this, methodName);
		TraceHelper.entry(this, methodName);
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
		long endTime = System.currentTimeMillis() + timeout;
		try {
			InputStream stream = port.getInputStream();
			while (stream.available() == 0) {
				Thread.sleep(10);
				if (System.currentTimeMillis() > endTime) {
					throw new ProcessingException("No chars");
				}
			}
			rc = stream.read();
		} catch (Exception e) {
			ErrorLogHelper.exception(this, "readChar", e);
			throw new ProcessingException(e);
		}
		return rc;
	}

	public void burnBuffer(FirmwareFileParser hfp, IVNADriver driver) throws ProcessingException {
		TraceHelper.entry(this, "burnBuffer");

		VNADriverSerialBase serialDriver = (VNADriverSerialBase) driver;
		SerialPort port = serialDriver.getPort();
		serialDriver.flushInputStream();

		OutputStream oStream = null;
		InputStream iStream = null;
		try {
			oStream = port.getOutputStream();
			iStream = port.getInputStream();

			// auto reset?
			if (isAutoReset()) {
				TraceHelper.text(this, "burnBuffer", "sending reset command");
				oStream.write('9');
				oStream.write('9');
				oStream.write(0x0d);
				oStream.flush();
			}

			int lastChar;

			MemoryType memoryType = null;

			while (true) {
				lastChar = readChar(port, 5000);
				TraceHelper.text(this, "burnBuffer", "read [" + (char) lastChar + "] - " + lastChar);
				// send me FLASH data ?
				if (lastChar == '>') {
					// yes
					// sequence always starts with flash
					// send welcome to device
					oStream.write('<');
					oStream.flush();
					memoryType = MemoryType.FLASH;
					setPagePtr(0);
					setRetryCount(0);
				} else if (lastChar == ')') {
					memoryType = MemoryType.EEPROM;
					throw new ProcessingException("EEPROM not supported");
				} else if (lastChar == '!') {
					// send me new data
					// flash?
					if (memoryType == MemoryType.FLASH) {
						// yes
						if (sendFlashBlock(oStream, hfp.getFlashMax(), hfp.getFlash())) {
							break;
						}
						setPagePtr(getPagePtr() + 1);

						// EEPROM?
					} else if (memoryType == MemoryType.EEPROM) {
						// yes
						throw new ProcessingException("EEPROM not supported");
					}
				} else if (lastChar == '@') {
					// resend last data block
					// flash memory?
					if (memoryType == MemoryType.FLASH) {
						// yes
						--pagePtr;
						sendFlashBlock(oStream, hfp.getFlashMax(), hfp.getFlash());
						++pagePtr;
						++retryCount;
						if (retryCount > 3) {
							oStream.write(0xff);
							oStream.write(0xff);
							throw new ProcessingException("Retry count exceeded");
						}
						// eeprom
					} else if (memoryType == MemoryType.EEPROM) {
						// yes
						throw new ProcessingException("EEPROM not supported");
					}
				} else if (lastChar == '%') {
					throw new ProcessingException("LockBits not supported");
				} else if (lastChar == 'A') {
					deviceType = "Mega 8";
				} else if (lastChar == 'B') {
					deviceType = "Mega 16";
				} else if (lastChar == 'C') {
					deviceType = "Mega 64";
				} else if (lastChar == 'D') {
					deviceType = "Mega 128";
				} else if (lastChar == 'E') {
					deviceType = "Mega 32";
				} else if (lastChar == 'F') {
					deviceType = "Mega 162";
				} else if (lastChar == 'G') {
					deviceType = "Mega 169";
				} else if (lastChar == 'H') {
					deviceType = "Mega8515";
				} else if (lastChar == 'I') {
					deviceType = "Mega8535";
				} else if (lastChar == 'J') {
					deviceType = "Mega163";
				} else if (lastChar == 'K') {
					deviceType = "Mega323";
				} else if (lastChar == 'L') {
					deviceType = "Mega48";
				} else if (lastChar == 'M') {
					deviceType = "Mega88";
				} else if (lastChar == 'N') {
					deviceType = "Mega168";
				} else if (lastChar == 0x80) {
					deviceType = "Mega165";
				} else if (lastChar == 0x81) {
					deviceType = "Mega3250";
				} else if (lastChar == 0x82) {
					deviceType = "Mega6450";
				} else if (lastChar == 0x83) {
					deviceType = "Mega3290";
				} else if (lastChar == 0x84) {
					deviceType = "Mega6490";
				} else if (lastChar == 0x85) {
					deviceType = "Mega406";
				} else if (lastChar == 0x86) {
					deviceType = "Mega640";
				} else if (lastChar == 0x87) {
					deviceType = "Mega1280";
				} else if (lastChar == 0x88) {
					deviceType = "Mega2560";
				} else if (lastChar == 0x89) {
					deviceType = "MCAN128";
				} else if (lastChar == 0x8a) {
					deviceType = "Mega164";
				} else if (lastChar == 0x8b) {
					deviceType = "Mega328";
				} else if (lastChar == 0x8c) {
					deviceType = "Mega324";
				} else if (lastChar == 0x8d) {
					deviceType = "Mega325";
				} else if (lastChar == 0x8e) {
					deviceType = "Mega644";
				} else if (lastChar == 0x8f) {
					deviceType = "Mega645";
				} else if (lastChar == 0x90) {
					deviceType = "Mega1281";
				} else if (lastChar == 0x91) {
					deviceType = "Mega2561";
				} else if (lastChar == 0x92) {
					deviceType = "Mega2560";
				} else if (lastChar == 0x93) {
					deviceType = "Mega404";
				} else if (lastChar == 0x94) {
					deviceType = "MUSB1286";
				} else if (lastChar == 0x95) {
					deviceType = "MUSB1287";
				} else if (lastChar == 0x96) {
					deviceType = "MUSB162";
				} else if (lastChar == 0x97) {
					deviceType = "MUSB646";
				} else if (lastChar == 0x98) {
					deviceType = "MUSB647";
				} else if (lastChar == 0x99) {
					deviceType = "MUSB82";
				} else if (lastChar == 0x9a) {
					deviceType = "MCAN32";
				} else if (lastChar == 0x9b) {
					deviceType = "MCAN64";
				} else if (lastChar == 0x9c) {
					deviceType = "Mega329";
				} else if (lastChar == 0x9d) {
					deviceType = "Mega649";
				} else if (lastChar == 0x9e) {
					deviceType = "Mega256";
				} else if (lastChar == 'Q') {
					pageSize = 32;
				} else if (lastChar == 'R') {
					pageSize = 64;
				} else if (lastChar == 'S') {
					pageSize = 128;
				} else if (lastChar == 'T') {
					pageSize = 256;
				} else if (lastChar == 'V') {
					pageSize = 512;
				} else if (lastChar == 'a') {
					bootSize = 128;
				} else if (lastChar == 'b') {
					bootSize = 256;
				} else if (lastChar == 'c') {
					bootSize = 512;
				} else if (lastChar == 'd') {
					bootSize = 1024;
				} else if (lastChar == 'e') {
					bootSize = 2048;
				} else if (lastChar == 'f') {
					bootSize = 4096;
				} else if (lastChar == 'g') {
					flashSize = 1 * 1024;
				} else if (lastChar == 'h') {
					flashSize = 2 * 1024;
				} else if (lastChar == 'i') {
					flashSize = 4 * 1024;
				} else if (lastChar == 'l') {
					flashSize = 8 * 1024;
				} else if (lastChar == 'm') {
					flashSize = 16 * 1024;
				} else if (lastChar == 'n') {
					flashSize = 32 * 1024;
				} else if (lastChar == 'o') {
					flashSize = 64 * 1024;
				} else if (lastChar == 'p') {
					flashSize = 128 * 1024;
				} else if (lastChar == 'q') {
					flashSize = 256 * 1024;
				} else if (lastChar == 'r') {
					flashSize = 40 * 1024;
				} else if (lastChar == '.') {
					eEpromSize = 512;
				} else if (lastChar == '/') {
					eEpromSize = 512;
				} else if (lastChar == '0') {
					eEpromSize = 512;
				} else if (lastChar == '1') {
					eEpromSize = 512;
				} else if (lastChar == '2') {
					eEpromSize = 1 * 1024;
				} else if (lastChar == '3') {
					eEpromSize = 2 * 1024;
				} else if (lastChar == '4') {
					eEpromSize = 4 * 1024;
				}
			}
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "burnBuffer", e);
			throw new ProcessingException(e);
		} finally {
			if (oStream != null) {
				try {
					oStream.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, "burnBuffer", e);
				}
			}
			if (iStream != null) {
				try {
					iStream.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, "burnBuffer", e);
				}
			}
		}
		TraceHelper.text(this, "burnBuffer", "PageSize:   " + pageSize + " bytes");
		TraceHelper.text(this, "burnBuffer", "bootSize:   " + bootSize + " words");
		TraceHelper.text(this, "burnBuffer", "flashSize:  " + flashSize + " bytes");
		TraceHelper.text(this, "burnBuffer", "EEpromSize: " + eEpromSize + " bytes");
		TraceHelper.text(this, "burnBuffer", "retryCount: " + retryCount);
		TraceHelper.exit(this, "burnBuffer");
	}

	/**
	 * 
	 * @param oStream
	 * @param flashMax
	 * @return true end of flash reached
	 * @throws IOException
	 */
	private boolean sendFlashBlock(OutputStream oStream, int flashMax, byte[] flash) throws IOException {
		boolean rc = false;
		TraceHelper.entry(this, "sendFlashBlock");
		TraceHelper.text(this, "sendFlashBlock", "pagePtr:" + pagePtr);
		if ((pagePtr * pageSize) > flashMax) // All data was send
		{
			oStream.write(0xff);
			oStream.write(0xff);

			TraceHelper.text(this, "sendFlashBlock", "Last page send");
			rc = true;
		} else {
			getMessenger().publishMessage("Sending page " + pagePtr + " to device");
			TraceHelper.text(this, "sendFlashBlock", "sending page:" + pagePtr);
			oStream.write((byte) ((pagePtr >> 8) & 0x00ff));
			oStream.write((byte) (pagePtr & 0x00ff));

			byte checkSum = 0;
			int bytesSend = 0;

			TraceHelper.text(this, "sendFlashBlock", "sending data");
			oStream.write(flash, (pagePtr * pageSize), pageSize);
			while (bytesSend < pageSize) {
				checkSum += flash[(pagePtr * pageSize) + bytesSend];
				bytesSend++;
			}
			TraceHelper.text(this, "sendFlashBlock", "sending checksum");
			oStream.write(checkSum);
		}
		TraceHelper.exit(this, "sendFlashBlock");
		return rc;
	}
}
