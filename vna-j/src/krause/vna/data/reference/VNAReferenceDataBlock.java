/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNAReferenceSampleBlock.java
 *  Part of:   vna-j
 */

package krause.vna.data.reference;

import java.io.File;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;

/**
 * @author Dietmar
 * 
 */
public class VNAReferenceDataBlock {
	private VNACalibratedSample[] samples;
	private VNACalibratedSample[] resizedSamples;
	private long minFrequency = Long.MAX_VALUE;
	private long maxFrequency = Long.MIN_VALUE;
	private File file = null;
	private String comment = null;

	/**
	 * @param refData
	 */
	public VNAReferenceDataBlock(VNACalibratedSampleBlock refData) {
		TraceHelper.entry(this, "VNAReferenceSampleBlock");
		if (refData != null) {
			// store as reference
			samples = refData.getCalibratedSamples();

			int len = samples.length;

			// contain samples?
			if (len > 0) {
				// yes
				// set min freq
				setMinFrequency(samples[0].getFrequency());

				// set max freq
				setMaxFrequency(samples[len - 1].getFrequency());
			}
			setComment(refData.getComment());
		}
		TraceHelper.exit(this, "VNAReferenceSampleBlock");
	}

	public VNACalibratedSample[] getSamples() {
		return samples;
	}

	public void setSamples(VNACalibratedSample[] samples) {
		this.samples = samples;
	}

	@Override
	public String toString() {
		return "VNAReferenceSampleBlock [maxFrequency=" + maxFrequency + ", minFrequency=" + minFrequency;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	/**
	 * Create a sample data block that matches the given parameters
	 * 
	 * @param scanSamples
	 * @param startFreq
	 * @param stopFreq
	 */
	public void prepare(VNACalibratedSample[] scanSamples, long startFreq, long stopFreq) {
		TraceHelper.entry(this, "prepare");

		int numScanSamples = scanSamples.length;
		int numRefSamples = samples.length;

		if (resizedSamples == null) {
			resizedSamples = new VNACalibratedSample[numScanSamples];
		} else {
			if (resizedSamples.length != numScanSamples) {
				resizedSamples = new VNACalibratedSample[numScanSamples];
			}
		}

		if (scanSamples[0].getFrequency() > samples[numRefSamples - 1].getFrequency())
			return;

		if (scanSamples[numScanSamples - 1].getFrequency() < samples[0].getFrequency())
			return;

		int refIndex = 0;

		// for all samples in the target space
		for (int x = 0; x < numScanSamples; ++x) {
			VNACalibratedSample scanSample = scanSamples[x];

			// end of reference samples reached?
			if (refIndex < numRefSamples) {
				// no
				VNACalibratedSample refSample = samples[refIndex];

				// find reference sample that is at least the scan frequency
				while (refSample.getFrequency() < scanSample.getFrequency()) {
					++refIndex;
					// end of reference reached?
					if (refIndex >= numRefSamples) {
						// yes
						// leave loop
						break;
					}
					refSample = samples[refIndex];
				}

				// end of reference reached ?
				if (refIndex < numRefSamples) {
					// no
					// use the reference
					resizedSamples[x] = refSample;
				} else {
					// yes
					// set to NULL
					resizedSamples[x] = null;
				}
			} else {
				// yes
				// set to NULL
				resizedSamples[x] = null;
			}
		}
		TraceHelper.exit(this, "prepare");
	}

	/**
	 * @return the resizedSamples
	 */
	public VNACalibratedSample[] getResizedSamples() {
		return resizedSamples;
	}

	public long getMinFrequency() {
		return minFrequency;
	}

	public void setMinFrequency(long minFrequency) {
		this.minFrequency = minFrequency;
	}

	public long getMaxFrequency() {
		return maxFrequency;
	}

	public void setMaxFrequency(long maxFrequency) {
		this.maxFrequency = maxFrequency;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
}
