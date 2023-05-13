package krause.vna.device.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNAScanMode;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNAGenericDriver;
import krause.vna.gui.OptionDialogHelper;
import krause.vna.gui.VNAMainFrame;
import krause.vna.resources.VNAMessages;
import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;

public abstract class VNADriverSerialBase extends VNAGenericDriver {

	public static final int DEFAULT_INPUTBUFFER_SIZE = 32000;
	private static NumberFormat theFormat = null;

	public static NumberFormat getFrequencyFormat() {
		if (theFormat == null) {
			theFormat = NumberFormat.getNumberInstance(Locale.US);
			theFormat.setGroupingUsed(false);
			theFormat.setMaximumFractionDigits(0);
			theFormat.setMinimumFractionDigits(0);
		}
		return theFormat;
	}

	private String appname;

	private CommPort commPort;

	private SerialPort port;

	@Override
	public boolean checkForDevicePresence(boolean viaSlowConnection) {
		boolean rc = false;
		final String methodName = "checkForDevicePresence";
		TraceHelper.entry(this, methodName);

		try {
			VNADeviceInfoBlock dib = getDeviceInfoBlock();

			init();
			if (viaSlowConnection) {
				Thread.sleep(4000);
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

	/**
	 * Method flushInputStream.
	 */
	public void flushInputStream() {
		final String methodName = "flushInputStream";
		TraceHelper.entry(this, methodName);
		int cnt = 0;
		int read = 0;
		int avl = 0;
		try {
			do {
				avl = port.getInputStream().available();
				if (avl > 0) {
					byte[] tempBuffer = new byte[avl];

					read = port.getInputStream().read(tempBuffer);

					ErrorLogHelper.text(this, methodName, "Flushed %d chars", read);
					cnt += read;

					Thread.sleep(20);
				}
			} while (avl > 0);
		} catch (IOException | InterruptedException e) {
			ErrorLogHelper.exception(this, methodName, e);
		}

		if (cnt > 0) {
			ErrorLogHelper.text(this, methodName, "total %d chars flushed from stream", cnt);
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * @return the appname
	 */
	public String getAppname() {
		return appname;
	}

	public CommPort getCommPort() {
		return commPort;
	}

	/**
	 * @return the port
	 */
	public SerialPort getPort() {
		return port;
	}

	/**
	 * Find the named serial port in the available ports on the system
	 * 
	 * @param portname
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected CommPortIdentifier getPortIDForName(String portname) {
		CommPortIdentifier rc = null;
		TraceHelper.entry(this, "getPortIDForName", portname);

		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			CommPortIdentifier aPortId = portList.nextElement();
			if ((aPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) && (aPortId.getName().equals(getPortname()))) {
				rc = aPortId;
				break;
			}
		}
		TraceHelper.exitWithRC(this, "getPortIDForName", rc);
		return rc;
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public final List<String> getPortList() throws ProcessingException {
		final String methodeName = "getPortList";
		List<String> rc = new ArrayList<>();
		TraceHelper.entry(this, methodeName);
		//
		try {

			Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
			while (portList.hasMoreElements()) {
				CommPortIdentifier portId = null;
				portId = portList.nextElement();
				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					String name = portId.getName();
					// ignore the tty interface
					if ("tty".equals(name)) {
						// ignore TTYs
					} else if (name.startsWith("tty")) {
						// ignore all tty<nn> interfaces
						String sName = name.substring(3);
						try {
							Integer.parseInt(sName);
						} catch (NumberFormatException e) {
							rc.add(name);
						}
					} else {
						rc.add(name);
					}
				}
			}
		} catch (NumberFormatException t) {
			ProcessingException p = new ProcessingException(t);
			ErrorLogHelper.exception(this, methodeName, p);
			throw p;
		}
		TraceHelper.exitWithRC(this, methodeName, rc);
		return rc;
	}

	/**
	 * Calculate a time frame within the given number of bytes must be received
	 * 
	 * @param pNumBytes
	 * @param realBaudrate
	 *            The baudrate to use assume for this analyzer
	 * @return the maximum number of ms allowed to receive this bunch of data
	 */
	protected int getTimeoutBasedOnNumberOfBytesAndBaudrate(final int pNumBytes, final int realBaudrate) {
		// assume bytes per sec
		final int bytesPerSec = realBaudrate / 10;
		final int rawTime = 1 + pNumBytes / bytesPerSec;
		return rawTime * 1000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.device.VNAGenericDriver#init()
	 */
	@Override
	public void init() throws InitializationException {
		TraceHelper.entry(this, "init");
		super.init();
		appname = config.getProperty(getDriverConfigPrefix() + VNADriverSerialBaseSymbols.PROPERTIES_APPNAME, "VNA-J2");
		TraceHelper.exit(this, "init");
	}

	/**
	 * Read count character into the buffer starting at offset.
	 * 
	 * @param buffer
	 * @param offset
	 * @param count
	 * @return the number of characters read of -1 if timed out
	 * @throws ProcessingException
	 */
	protected int readBuffer(byte[] buffer, int offset, int count) throws ProcessingException {
		int rc = -1;
		long endTime = System.currentTimeMillis() + port.getReceiveTimeout();
		InputStream stream = null;
		try {
			stream = port.getInputStream();
			while (stream.available() < count) {
				Thread.sleep(10);
				if (System.currentTimeMillis() > endTime) {
					return rc;
				}
			}
			rc = stream.read(buffer, offset, count);
		} catch (IOException | InterruptedException e) {
			ErrorLogHelper.exception(this, "readBuffer", e);
			throw new ProcessingException(e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, "readBuffer", e);
				}
			}
		}
		return rc;
	}

	/**
	 * Read one character from the ports input stream
	 * 
	 * @return the character or -1 if no char available after timeout
	 * @throws ProcessingException
	 */
	public int readChar() throws ProcessingException {
		int rc = -1;
		try {
			long endTime = System.currentTimeMillis() + port.getReceiveTimeout();
			InputStream stream = port.getInputStream();
			while (stream.available() == 0) {
				Thread.sleep(10);
				if (System.currentTimeMillis() > endTime) {
					return rc;
				}
			}
			rc = stream.read();
		} catch (IOException | InterruptedException e) {
			ErrorLogHelper.exception(this, "readChar", e);
			throw new ProcessingException(e);
		}
		return rc;
	}

	/**
	 * Read one line of characters from the ports input stream. The receive ends, if CR or LF is received.
	 * 
	 * @param endWithLF
	 *            true=LF is end of line false?CR is end of line
	 * 
	 * @return the line red excluding CR
	 * @throws ProcessingException
	 */
	public String readLine(boolean endWithLF) throws ProcessingException {
		StringBuilder sb = new StringBuilder();
		boolean end = false;
		while (!end) {
			int ch = readChar();
			if (ch == -1) {
				ProcessingException e = new ProcessingException("Timeout");
				ErrorLogHelper.exception(this, "readLine", e);
				throw e;
			} else {
				char c = (char) ch;
				if (c == 13) {
					if (!endWithLF) {
						end = true;
					}
				} else if (c == 10) {
					if (endWithLF) {
						end = true;
					}
				} else {
					sb.append((char) ch);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param pNumBytes
	 * @param pListener
	 * @return
	 * @throws ProcessingException
	 */
	protected byte[] receiveBytestream(final int pNumBytes, final IVNABackgroundTaskStatusListener pListener) throws ProcessingException {
		final String methodName = "receiveBytestream";
		TraceHelper.entry(this, methodName, "#=%d", pNumBytes);

		InputStream stream = null;
		int remainingBytes = pNumBytes;
		byte[] buffer = new byte[remainingBytes];
		int lastPercentage = 0;

		try {
			stream = getPort().getInputStream();

			// read the desired bytes
			int readBytes = 0;

			final int realBaudrate = getDeviceInfoBlock().calculateRealBaudrate(getPort().getBaudRate());

			// calculate the average time to complete this scan
			long endTime = System.currentTimeMillis() + getTimeoutBasedOnNumberOfBytesAndBaudrate(remainingBytes, realBaudrate);

			while (remainingBytes > 0) {
				if (stream.available() > 0) {
					int currBytesRead = stream.read(buffer, readBytes, remainingBytes);
					readBytes += currBytesRead;
					remainingBytes -= currBytesRead;

					// publish progress
					if (pListener != null) {
						int currentPercentage = (int) (readBytes * 100.00 / (readBytes + remainingBytes));
						if (currentPercentage >= lastPercentage + 10) {
							pListener.publishProgress(currentPercentage);
							lastPercentage = currentPercentage;
						}
					}
				}
				final long now = System.currentTimeMillis();
				if (now > endTime) {
					String msg = MessageFormat.format(VNAMessages.getString("Timeout"), readBytes, remainingBytes, endTime - now);
					ProcessingException e = new ProcessingException(msg);
					ErrorLogHelper.exception(this, methodName, e);
					throw e;
				}
				endTime = now + getTimeoutBasedOnNumberOfBytesAndBaudrate(remainingBytes, realBaudrate);
			}
			TraceHelper.text(this, methodName, "all bytes read");
		} catch (IOException e) {
			ErrorLogHelper.exception(this, methodName, e);
			throw new ProcessingException(e);
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
		//
		TraceHelper.exit(this, methodName);
		return buffer;
	}

	protected void sendAsAsciiString(String buffer) throws ProcessingException {
		final String methodName = "sendAsAsciiString";
		OutputStream oStream = null;
		try {
			String local = buffer + "\r";
			// convert to byte array
			byte[] tbuf = local.getBytes(StandardCharsets.US_ASCII);
			oStream = port.getOutputStream();
			oStream.write(tbuf);
		} catch (IOException e) {
			ErrorLogHelper.exception(this, methodName, e);
			throw new ProcessingException(e);
		} finally {
			if (oStream != null) {
				try {
					oStream.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, methodName, e);
				}
			}
		}
	}

	/**
	 * @param appname
	 *            the appname to set
	 */
	public void setAppname(String appname) {
		this.appname = appname;
	}

	public void setCommPort(CommPort commPort) {
		this.commPort = commPort;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(SerialPort port) {
		this.port = port;
	}

	public void showDriverNetworkDialog(VNAMainFrame pMF) {
		OptionDialogHelper.showInfoDialog(pMF.getJFrame(), "VNADriverSerialBase.Network.1", "VNADriverSerialBase.Network.2");
	}

	/**
	 * Wait for the given amount of milliseconds
	 * 
	 * @param ms
	 *            time to wait
	 * @throws ProcessingException
	 */
	public void wait(int ms) throws ProcessingException {
		final String methodName = "wait";
		// only if we really have to wait
		if (ms > 0) {
			// and now wait before the next run
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				ErrorLogHelper.exception(this, methodName, e);
				throw new ProcessingException(e);
			}
		}
	}
}