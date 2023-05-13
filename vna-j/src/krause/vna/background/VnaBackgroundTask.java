/**
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package krause.vna.background;

import java.awt.Color;
import java.net.ConnectException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;

import org.jdesktop.swingworker.SwingWorker;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.IVNADataConsumer;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNABaseSampleHelper;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.helper.VNASampleBlockHelper;
import krause.vna.device.IVNADriver;
import krause.vna.message.ErrorMessage;
import krause.vna.message.GenericMessage;
import krause.vna.message.InfoMessage;
import krause.vna.resources.VNAMessages;

public class VnaBackgroundTask extends SwingWorker<List<VNABackgroundJob>, GenericMessage> implements IVNABackgroundTaskStatusListener {
	private final VNAConfig config = VNAConfig.getSingleton();
	private List<IVNADataConsumer> consumers = new ArrayList<>();
	private long currJob = 0;
	private long currOver = 0;
	private IVNADriver driver;
	private List<VNABackgroundJob> jobs = new ArrayList<>();
	private long maxJobs = 0;
	private JLabel statusLabel = null;

	public VnaBackgroundTask(IVNADriver pDriver) {
		driver = pDriver;
	}

	/**
	 * When the doInBackgroud has finished its task, it will inform the registered consumer with this data
	 * 
	 * @param pConsumer
	 *            The consumer to inform
	 */
	public void addDataConsumer(IVNADataConsumer pConsumer) {
		TraceHelper.entry(this, "addConsumer");
		consumers.add(pConsumer);
		TraceHelper.exit(this, "addConsumer");
	}

	public void addJob(VNABackgroundJob job) {
		getJobs().add(job);
	}

	/**
	 * Create intermediates that fit between {@code currentSample} and {@code nextSample}. Create {@code numberIntermediateSamples}
	 * intermediates and insert into {@code newSamples}.
	 * 
	 * @param idx
	 * @param currentSample
	 * @param nextSample
	 * @param newSamples
	 * @param numberIntermediateSamples
	 * @return
	 */
	private static int createIntermediateSamples(int idx, VNABaseSample currentSample, VNABaseSample nextSample, VNABaseSample[] newSamples, int numberIntermediateSamples) {
		// calculate
		final VNABaseSample deltaSample = VNABaseSampleHelper.createDeltaSample(currentSample, nextSample, numberIntermediateSamples);

		// put current sample in first slots
		newSamples[idx] = currentSample;

		// insert into next position
		++idx;

		// is duplicated by the speedup
		for (int j = 1; j < numberIntermediateSamples; ++j) {
			// create new intermediate sample
			final VNABaseSample newSample = VNABaseSampleHelper.createNewSampleWithDelta(newSamples[idx - 1], deltaSample);

			// put current sample in new slots
			newSamples[idx] = newSample;
			++idx;
		}
		return idx;
	}

	@Override
	public List<VNABackgroundJob> doInBackground() {
		final String methodName = "doInBackground";
		TraceHelper.entry(this, methodName);
		try {
			publish(new InfoMessage("Background.1"));
			publish(new InfoMessage("Background.2"));

			this.maxJobs = jobs.size();
			this.currJob = 0;

			// for each job
			for (VNABackgroundJob job : jobs) {
				// increment job counter for display
				++this.currJob;

				// create data
				final int numTargetSamples = job.getNumberOfSamples();
				final int speedup = job.getSpeedup();
				final int overscan = job.getOverScan();

				int numSamplesToScan = numTargetSamples / speedup;
				if (speedup > 1) {
					if (numTargetSamples % speedup > 0) {
						numSamplesToScan += 2;
					} else {
						numSamplesToScan += 1;
					}
				}

				TraceHelper.text(this, methodName, "Speedup=%d", speedup);
				TraceHelper.text(this, methodName, "Overscan=%d", overscan);
				TraceHelper.text(this, methodName, "numTargetSamples=%d", numTargetSamples);
				TraceHelper.text(this, methodName, "numSamplesToScan=%d", numSamplesToScan);

				// ======================================================================================
				// now scan numSamplesToScan
				VNASampleBlock scannedSamples = null;
				if (overscan > 1) {
					// yes
					List<VNASampleBlock> blocks = new ArrayList<>();

					// now execute the number of overscans
					for (int i = 0; i < overscan; ++i) {
						//
						this.currOver = i;
						// scan once
						VNASampleBlock aBlock = driver.scan(job.getScanMode(), job.getFrequencyRange().getStart(), job.getFrequencyRange().getStop(), numSamplesToScan, this);

						// add to list of blocks
						blocks.add(aBlock);
					}

					// calculate average of all scanned blocks
					scannedSamples = VNASampleBlockHelper.calculateAverageSampleBlock(blocks);
				} else {
					// no
					this.currOver = 0;
					// so simply scan once
					scannedSamples = driver.scan(job.getScanMode(), job.getFrequencyRange().getStart(), job.getFrequencyRange().getStop(), numSamplesToScan, this);
				}

				// ======================================================================================
				// do we have speedup
				if (speedup > 1) {
					// yes
					// now scale up to the needed size
					VNABaseSample[] targetSamples = new VNABaseSample[numTargetSamples];
					VNABaseSample[] sourceSamples = scannedSamples.getSamples();

					// position to insert first sample
					int idx = 0;

					// each sample of the scannedSamples
					final int max = numSamplesToScan - 1;
					int numIntermediateSamples = speedup;
					for (int i = 0; i < max; ++i) {
						// get current sample
						final VNABaseSample currentSample = sourceSamples[i];

						// get next sample
						final VNABaseSample nextSample = sourceSamples[i + 1];

						if (i + 1 >= max) {
							numIntermediateSamples = numTargetSamples - ((numTargetSamples / speedup) * speedup);
							if (numIntermediateSamples == 0) {
								numIntermediateSamples = speedup;
							}
						}
						if (numIntermediateSamples > 0) {
							idx = createIntermediateSamples(idx, currentSample, nextSample, targetSamples, numIntermediateSamples);
						}
					}

					// and put back to original scan result
					scannedSamples.setSamples(targetSamples);
					scannedSamples.setNumberOfSteps(numTargetSamples);
				}
				job.setResult(scannedSamples);

				// turn off the generator
				if (config.isTurnOffGenAfterScan()) {
					driver.stopGenerator();
				}
			}
		} catch (ProcessingException e) {
			ErrorLogHelper.exception(this, methodName, e);
			Throwable root = e;
			if (e.getCause() != null) {
				root = e.getCause();
			}
			if (root instanceof ConnectException) {
				String msg = VNAMessages.getString("VnaBackgroundTask.ConnectException");
				publish(new ErrorMessage(MessageFormat.format(msg, e.getMessage())));
			} else {
				publish(new ErrorMessage(e.getMessage()));
			}
		}

		TraceHelper.exit(this, methodName);

		return getJobs();
	}

	/**
	 * Informs all registered data consumers with the data
	 */
	@Override
	protected void done() {
		final String methodName = "done";
		TraceHelper.entry(this, methodName);

		try {
			List<VNABackgroundJob> data = get();
			for (IVNADataConsumer consumer : consumers) {
				// yes
				// now send the datablock to the consumers
				consumer.consumeDataBlock(data);
			}
		} catch (InterruptedException | ExecutionException e) {
			ErrorLogHelper.exception(this, methodName, e);
		}
		TraceHelper.exit(this, methodName);
	}

	public List<VNABackgroundJob> getJobs() {
		return jobs;
	}

	public JLabel getStatusLabel() {
		return statusLabel;
	}

	@Override
	/**
	 * This method is called in context of AWTThread Here we can handle all AWT call etc.
	 */
	protected void process(List<GenericMessage> chunks) {
		if (statusLabel != null) {
			for (GenericMessage message : chunks) {
				statusLabel.setOpaque(true);
				if (message instanceof ErrorMessage) {
					statusLabel.setForeground(Color.RED);
					statusLabel.setBackground(Color.BLACK);
				} else {
					statusLabel.setForeground(Color.BLACK);
					statusLabel.setBackground(Color.GREEN);
				}
				statusLabel.setText(message.getMessage());
				TraceHelper.text(this, "process", message.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.background.IVNABackgorundTaskStatusListener#publishProgress (int)
	 */
	public void publishProgress(int percentage) {
		publish(new InfoMessage("Background.3", this.currOver, this.currJob, this.maxJobs, percentage));
	}

	public void setStatusLabel(JLabel statusLabel) {
		this.statusLabel = statusLabel;
	}
}