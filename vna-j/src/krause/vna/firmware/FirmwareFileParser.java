package krause.vna.firmware;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class FirmwareFileParser {
	private static final String LAST_LINE = ":00000001FF";
	private static final int RADIX = 16;
	private static final byte DATA_RECORD = 0;
	private static final byte EOF_RECORD = 1;
	private static final byte EXTENDED_ADDRESS_RECORD = 2;
	private static final byte EXTENDED_LINEAR_ADDRESS_RECORD = 4;

	private int flashMin;
	private int flashMax;
	private int memOffset;
	private int memUsage;
	private byte[] flash = null;
	private boolean intelHexFile = false;

	private File file = null;

	public FirmwareFileParser(File file) {
		super();
		this.file = file;
		this.intelHexFile = file.getName().toUpperCase().endsWith(".HEX");
	}

	public void parseFile() throws ProcessingException {
		final String methodName = "parseFile";
		TraceHelper.entry(this, methodName);
		if (isIntelHexFile()) {
			parseHexFile();
		} else {
			parseBinFile();
		}
		TraceHelper.entry(this, methodName);
	}

	public void parseBinFile() throws ProcessingException {
		final String methodName = "parseBinFile";
		TraceHelper.entry(this, methodName);

		int fileLen = (int) this.file.length();

		// buffer for the binary file
		flash = new byte[fileLen];

		try (InputStream fin = new FileInputStream(this.file)) {
			long numRead = fin.read(flash, 0, fileLen);
			TraceHelper.text(this, methodName, numRead + " bytes read");
		} catch (IOException e) {
			ErrorLogHelper.exception(this, methodName, e);
			throw new ProcessingException(e);
		}

		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 * @throws ProcessingException
	 */
	public void parseHexFile() throws ProcessingException {
		TraceHelper.entry(this, "parseHexFile");
		String record = "";
		int recordNum = 0;
		flashMin = Integer.MAX_VALUE;
		flashMax = 0;
		memOffset = 0;
		memUsage = 0;

		// buffer for the hex file
		flash = new byte[256 * 1024];

		try (FileReader fileReader = new FileReader(file); BufferedReader br = new BufferedReader(fileReader);) {

			while ((record = br.readLine()) != null) {
				// reset checksum for this line
				byte computedChecksum = 0;

				// line starts with a colon?
				if (!record.startsWith(":")) {
					// no
					// boom
					throw new ProcessingException("No Intel Hex file");
				}

				// now we have next record
				recordNum++;

				// read length of current line
				byte dataLength = (byte) Integer.parseInt(record.substring(1, 3), RADIX);
				computedChecksum += dataLength;

				// read address byte-wise to calculate checksum
				byte addressHi = (byte) Integer.parseInt(record.substring(3, 5), RADIX);
				computedChecksum += addressHi;

				byte addressLo = (byte) Integer.parseInt(record.substring(5, 7), RADIX);
				computedChecksum += addressLo;

				// read address word-wise to get address
				int address = Integer.parseInt(record.substring(3, 7), RADIX);

				// read record type
				byte recordType = (byte) Integer.parseInt(record.substring(7, 9), RADIX);
				computedChecksum += recordType;

				// we have a data record
				if (recordType == DATA_RECORD) {
					if (flashMin > (memOffset + address))
						flashMin = (memOffset + address);
					for (int i = 0; i < dataLength; i++) {
						int x = 9 + 2 * i;
						flash[memOffset + address + i] = (byte) Integer.parseInt(record.substring(x, x + 2), RADIX);
						computedChecksum += flash[memOffset + address + i];
						if (flashMax < (memOffset + address + i))
							flashMax = (memOffset + address + i);
					}
				} else if (recordType == EOF_RECORD) {
					memUsage = flashMax - flashMin + 1;
					break;
				} else if (recordType == EXTENDED_ADDRESS_RECORD) {
					addressHi = (byte) Integer.parseInt(record.substring(9, 11), RADIX);
					computedChecksum += addressHi;

					addressLo = (byte) Integer.parseInt(record.substring(11, 13), RADIX);
					computedChecksum += addressLo;

					memOffset = Integer.parseInt(record.substring(9, 13), RADIX);
					memOffset = (memOffset << 4);
				} else if (recordType == EXTENDED_LINEAR_ADDRESS_RECORD) {
					addressHi = (byte) Integer.parseInt(record.substring(9, 11), RADIX);
					computedChecksum += addressHi;

					addressLo = (byte) Integer.parseInt(record.substring(11, 13), RADIX);
					computedChecksum += addressLo;

					memOffset = Integer.parseInt(record.substring(9, 13), RADIX);
					memOffset = (memOffset << 16);
				} else {
					throw new ProcessingException("No valid record identifier [" + recordType + "]");
				}

				byte fileChecksum = (byte) Integer.parseInt(record.substring((record.length() - 2), record.length()), RADIX);

				computedChecksum = (byte) (256 - computedChecksum);
				memUsage = flashMax - flashMin + 1;

				if (computedChecksum != fileChecksum)
					throw new IllegalArgumentException("invalid checksum in record=" + recordNum + " read=" + fileChecksum + " calc=" + computedChecksum);

			}
		} catch (IOException ex) {
			throw new ProcessingException(ex);
		}
		TraceHelper.exit(this, "parseHexFile");
	}

	public int getFlashMin() {
		return flashMin;
	}

	public int getFlashMax() {
		return flashMax;
	}

	public int getMemOffset() {
		return memOffset;
	}

	public int getMemUsage() {
		return memUsage;
	}

	public byte[] getFlash() {
		return flash;
	}

	/**
	 * 
	 * @param address
	 * @param length
	 * @return
	 */
	public String getFlashAsHexFileLine(int address, int length) {
		StringBuilder rc = new StringBuilder(":");

		byte cs = 0;

		rc.append(String.format("%02X", length));
		cs += length;

		rc.append(String.format("%04X", address));
		cs += address / 256;
		cs += address % 256;

		rc.append(String.format("%02X", DATA_RECORD));
		cs += DATA_RECORD;

		for (int i = 0; i < length; ++i) {
			byte b = flash[address + i];
			rc.append(String.format("%02X", b));
			cs += b;
		}

		// two complement checksum
		cs = (byte) (256 - cs);
		rc.append(String.format("%02X", cs));

		return rc.toString();
	}

	public String getLastHexFileLine() {
		return LAST_LINE;
	}

	public boolean isIntelHexFile() {
		return intelHexFile;
	}
}
