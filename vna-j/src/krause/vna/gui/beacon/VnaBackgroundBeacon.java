/**
 * Copyright (C) 2012 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.beacon;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jdesktop.swingworker.SwingWorker;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.firmware.IVNABackgroundFlashBurnerConsumer;
import krause.vna.firmware.SimpleStringListbox;
import krause.vna.firmware.StringMessenger;
import krause.vna.gui.format.VNAFormatFactory;

public class VnaBackgroundBeacon extends SwingWorker<Integer, String> implements StringMessenger {

	private IVNABackgroundFlashBurnerConsumer consumer = null;
	private SimpleStringListbox listbox = null;
	private IVNADriver driver = null;
	private long frequency = 10000000;
	private int pause = 1;
	private int bpm = 60;
	private String message;

	private HashMap<Character, String> morseCodes = new HashMap<Character, String>();

	public VnaBackgroundBeacon() {
		super();
		morseCodes.put('A', "01");
		morseCodes.put('B', "1000");
		morseCodes.put('C', "1010");
		morseCodes.put('D', "100");
		morseCodes.put('E', "0");
		morseCodes.put('F', "0010");
		morseCodes.put('G', "110");
		morseCodes.put('H', "0000");
		morseCodes.put('I', "00");
		morseCodes.put('J', "0111");
		morseCodes.put('K', "101");
		morseCodes.put('L', "0100");
		morseCodes.put('M', "11");
		morseCodes.put('N', "10");
		morseCodes.put('O', "111");
		morseCodes.put('P', "0110");
		morseCodes.put('Q', "1101");
		morseCodes.put('R', "010");
		morseCodes.put('S', "000");
		morseCodes.put('T', "1");
		morseCodes.put('U', "001");
		morseCodes.put('V', "0001");
		morseCodes.put('W', "011");
		morseCodes.put('X', "1001");
		morseCodes.put('Y', "1011");
		morseCodes.put('Z', "1100");
		morseCodes.put('1', "01111");
		morseCodes.put('2', "00111");
		morseCodes.put('3', "00011");
		morseCodes.put('4', "00001");
		morseCodes.put('5', "00000");
		morseCodes.put('6', "10000");
		morseCodes.put('7', "11000");
		morseCodes.put('8', "11100");
		morseCodes.put('9', "11110");
		morseCodes.put('0', "11111");
		morseCodes.put('/', "10010");
		morseCodes.put('#', "000101");
		morseCodes.put('+', "01010");
		morseCodes.put('.', "010101");
		morseCodes.put(',', "110011");
		morseCodes.put('-', "100001");
		morseCodes.put('?', "001100");
		morseCodes.put('!', "101011");
		morseCodes.put('=', "10001");
		morseCodes.put(':', "111000");
	}

	/**
	 * When the doInBackgroud has finished its task, it will inform the
	 * registered consumer with this data
	 * 
	 * @param pConsumer
	 *            The consumer to inform
	 */
	public void setDataConsumer(IVNABackgroundFlashBurnerConsumer pConsumer) {
		TraceHelper.entry(this, "setDataConsumer");
		consumer = pConsumer;
		TraceHelper.exit(this, "setDataConsumer");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.vna.megaload.IVNABackgroundFlashBurnerMessanger#publishMessage
	 * (java.lang.String)
	 */
	public void publishMessage(String message) {
		publish(message);
	}

	@Override
	public Integer doInBackground() {
		int rc = 0;
		TraceHelper.entry(this, "doInBackground");

		try {
			publish("Starting beacon mode ...");
			while (!isCancelled()) {
				sendMorse();
				Thread.sleep(pause * 1000);
			}
		} catch (Exception e) {
			ErrorLogHelper.exception(this, "doInBackground", e);
			publish(e.getMessage());
			rc = 1;
		} finally {
		}
		TraceHelper.exit(this, "doInBackground");
		return rc;
	}

	private void sendMorse() {
		TraceHelper.entry(this, "sendMorse");
		publish(VNAFormatFactory.getDateTimeFormat().format(new Date()) + "-" + message);
		for (int i = 0; i < message.length(); ++i) {
			if (isCancelled()) {
				break;
			}
			char c = message.charAt(i);
			sendChar(c);
		}
		TraceHelper.exit(this, "sendMorse");
	}

	private void sendChar(char c) {
		int dit = ((int) (50.0 / bpm * 100.0));
		TraceHelper.text(this, "sendChar", "" + dit);
		try {
			if (c == ' ') {
				Thread.sleep(4 * dit);
			} else {
				String code = morseCodes.get(Character.toUpperCase(c));
				if (code != null) {
					for (int i = 0; i < code.length(); ++i) {
						driver.startGenerator(frequency, frequency, 0, 0, 0, 0);
						if (code.charAt(i) == '1') {
							Thread.sleep(4 * dit);
						} else {
							Thread.sleep(dit);
						}
						driver.stopGenerator();
						Thread.sleep(dit);
					}
				}
			}
			Thread.sleep(3 * dit);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * This method is called in context of AWTThread
	 * Here we can handle all AWT call etc.
	 */
	protected void process(List<String> messages) {
		TraceHelper.entry(this, "process");
		if (listbox != null) {
			for (String message : messages) {
				listbox.addMessage(message);
			}
		}
		TraceHelper.exit(this, "process");
	}

	/**
	 * Informs all registered data consumers with the data
	 */
	@Override
	protected void done() {
		TraceHelper.entry(this, "done");
		// now send the datablock to the consumers
		consumer.consumeReturnCode(0);
		TraceHelper.exit(this, "done");
	}

	public void setListbox(SimpleStringListbox listbox) {
		this.listbox = listbox;
	}

	public void setDriver(IVNADriver driver) {
		this.driver = driver;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public void setPause(int pause) {
		this.pause = pause;
	}

	public void setBpm(int bpm) {
		this.bpm = bpm;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}