/**
 * Copyright (C) 2012 Dietmar Krause, DL2SBA
 */
package krause.vna.firmware;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jdesktop.swingworker.SwingWorker;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNAFlashableDevice;

public class VnaBackgroundFlashBurner extends SwingWorker<Integer, String> implements StringMessenger {
	private boolean autoReset = false;
	private IVNABackgroundFlashBurnerConsumer consumer = null;
	private IVNADriver driver = null;
	private FirmwareFileParser flashFile = null;
	private SimpleStringListbox listbox = null;

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdesktop.swingworker.SwingWorker#doInBackground()
	 */
	public Integer doInBackground() {
		int rc = 0;
		TraceHelper.entry(this, "doInBackground");

		try {
			publish("Checking for matching firmware loader ... ");

			// get the matching loader class name
			String flasherClassName = ((IVNAFlashableDevice) getDriver()).getFirmwareLoaderClassName();

			// set?
			if (flasherClassName != null) {
				// yes
				publish("Starting firmware download ... ");
				// use it to burn firmware
				IVNAFirmwareFlasher burner = (IVNAFirmwareFlasher) Class.forName(flasherClassName.trim()).getDeclaredConstructor().newInstance();
				burner.setMessenger(this);
				burner.setAutoReset(autoReset);
				burner.burnBuffer(getFlashFile(), getDriver());
			} else {
				// no
				// just issue message
				publish("Firmware flashing not supported in this configuration");
			}
		} catch (Exception e) {
			ErrorLogHelper.exception(this, "doInBackground", e);
			publish(e.getMessage());
			rc = 1;
		}
		TraceHelper.exit(this, "doInBackground");
		return rc;
	}

	/**
	 * Informs all registered data consumers with the data
	 */
	@Override
	protected void done() {
		TraceHelper.entry(this, "done");

		try {
			// get the result of the job
			Integer rc = get();
			// now send the datablock to the consumers
			consumer.consumeReturnCode(rc);
		} catch (InterruptedException e) {
			ErrorLogHelper.exception(this, "done", e);
		} catch (ExecutionException e) {
			ErrorLogHelper.exception(this, "done", e);
		}

		TraceHelper.exit(this, "done");
	}

	public IVNADriver getDriver() {
		return driver;
	}

	public FirmwareFileParser getFlashFile() {
		return flashFile;
	}

	public SimpleStringListbox getListbox() {
		return listbox;
	}

	@Override
	/**
	 * This method is called in context of AWTThread Here we can handle all AWT
	 * call etc.
	 */
	protected void process(List<String> messages) {
		// TraceHelper.entry(this, "process");
		if (listbox != null) {
			for (String message : messages) {
				listbox.addMessage(message);
			}
		}
		// TraceHelper.exit(this, "process");
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

	public void setAutoReset(boolean selected) {
		autoReset = selected;
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

	public void setDriver(IVNADriver driver) {
		this.driver = driver;
	}

	public void setFlashFile(FirmwareFileParser hexFile) {
		this.flashFile = hexFile;
	}

	public void setListbox(SimpleStringListbox listbox) {
		this.listbox = listbox;
	}
}