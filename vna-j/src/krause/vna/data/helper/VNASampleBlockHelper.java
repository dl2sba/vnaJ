/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNASampleBlockHelper.java
 *  Part of:   vna-j
 */

package krause.vna.data.helper;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import krause.util.ras.logging.LogManager;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNASampleBlock;
import krause.vna.gui.format.VNAFormatFactory;

public final class VNASampleBlockHelper {
	static final VNASampleBlockHelper instance = new VNASampleBlockHelper();

	/*
	 * 
	 */
	private VNASampleBlockHelper() {

	}

	/**
	 * return a sample block with the geometric mean of all passed blocks
	 * 
	 * @param blocks
	 * @return
	 */
	public static VNASampleBlock calculateAverageSampleBlock(List<VNASampleBlock> blocks) {
		final String methodName = "calculateAverageSampleBlock";
		TraceHelper.entry(instance, methodName);

		VNASampleBlock rc = null;

		final int numBlocks = blocks.size();

		TraceHelper.text(instance, methodName, "  blocks=%d", numBlocks);
		// averaging makes sense for more thank one block :-)
		if (numBlocks > 1) {
			rc = new VNASampleBlock();
			final VNASampleBlock firstBlock = blocks.get(0);
			final int blockLen = firstBlock.getSamples().length;

			VNABaseSample[] samples = new VNABaseSample[blockLen];

			double deviceTemp = 0;

			for (VNASampleBlock block : blocks) {
				if (block.getDeviceTemperature() != null) {
					deviceTemp += block.getDeviceTemperature();
				}
			}

			deviceTemp /= numBlocks;

			for (int i = 0; i < blockLen; ++i) {

				final VNABaseSample sumSample = new VNABaseSample();

				for (VNASampleBlock block : blocks) {
					VNABaseSample curSample = block.getSamples()[i];
					//
					sumSample.setFrequency(curSample.getFrequency());

					//
					sumSample.setAngle(sumSample.getAngle() + curSample.getAngle());
					sumSample.setLoss(sumSample.getLoss() + curSample.getLoss());

					//
					sumSample.setP1(sumSample.getP1() + curSample.getP1());
					sumSample.setP2(sumSample.getP2() + curSample.getP2());
					sumSample.setP3(sumSample.getP3() + curSample.getP3());
					sumSample.setP4(sumSample.getP4() + curSample.getP4());

					//
					sumSample.setP1Ref(sumSample.getP1Ref() + curSample.getP1Ref());
					sumSample.setP2Ref(sumSample.getP2Ref() + curSample.getP2Ref());
					sumSample.setP3Ref(sumSample.getP3Ref() + curSample.getP3Ref());
					sumSample.setP4Ref(sumSample.getP3Ref() + curSample.getP4Ref());

					//
					sumSample.setRss1(sumSample.getRss1() + curSample.getRss1());
					sumSample.setRss2(sumSample.getRss2() + curSample.getRss2());
					sumSample.setRss3(sumSample.getRss3() + curSample.getRss3());
				}

				// average
				sumSample.setAngle(sumSample.getAngle() / numBlocks);
				sumSample.setLoss(sumSample.getLoss() / numBlocks);

				sumSample.setP1(sumSample.getP1() / numBlocks);
				sumSample.setP2(sumSample.getP2() / numBlocks);
				sumSample.setP3(sumSample.getP3() / numBlocks);
				sumSample.setP4(sumSample.getP4() / numBlocks);

				sumSample.setP1Ref(sumSample.getP1Ref() / numBlocks);
				sumSample.setP2Ref(sumSample.getP2Ref() / numBlocks);
				sumSample.setP3Ref(sumSample.getP3Ref() / numBlocks);
				sumSample.setP4Ref(sumSample.getP4Ref() / numBlocks);

				sumSample.setRss1(sumSample.getRss1() / numBlocks);
				sumSample.setRss2(sumSample.getRss2() / numBlocks);
				sumSample.setRss3(sumSample.getRss3() / numBlocks);

				samples[i] = sumSample;
			}

			rc.setAnalyserType(firstBlock.getAnalyserType());
			rc.setMathHelper(firstBlock.getMathHelper());
			rc.setNumberOfSteps(firstBlock.getNumberOfSteps());
			rc.setSamples(samples);
			rc.setStartFrequency(firstBlock.getStartFrequency());
			rc.setStopFrequency(firstBlock.getStopFrequency());
			rc.setScanMode(firstBlock.getScanMode());
			rc.setDeviceTemperature(deviceTemp);
			rc.setNumberOfOverscans(numBlocks);
		} else {
			rc = blocks.get(0);
		}
		TraceHelper.exit(instance, methodName);
		return rc;
	}

	/**
	 * replace all measurement data at specified frequency pSwitchPoints from pPoints
	 * 
	 * @param pBlock
	 *            sample blockdata to filter
	 * @param pSwitchPoints
	 *            frequency point to remove from pSamples
	 */
	public static void removeSwitchPoints(VNASampleBlock pBlock, long[] pSwitchPoints) {
		final String methodName = "removeSwitchPoints";
		TraceHelper.entry(instance, methodName, "SwitchPoints=%s", Arrays.toString(pSwitchPoints));

		final VNABaseSample[] pSamples = pBlock.getSamples();

		// iterate over all switch points
		for (long currSwitchPointFreq : pSwitchPoints) {
			VNABaseSample lastSample = pSamples[0];
			if (LogManager.getSingleton().isTracingEnabled()) {
				TraceHelper.text(instance, methodName, "processing switch frequency: " + currSwitchPointFreq);
			}

			// iterate over all except first two and last one
			for (int i = 2; i < pSamples.length - 1; ++i) {
				VNABaseSample currSample = pSamples[i];
				// are we at a switch point?
				if ((lastSample.getFrequency() <= currSwitchPointFreq) && (currSample.getFrequency() > currSwitchPointFreq)) {
					// yes
					if (LogManager.getSingleton().isTracingEnabled()) {
						NumberFormat nf = VNAFormatFactory.getFrequencyFormat();
						TraceHelper.text(instance, methodName, "switch point between " + i + " (" + nf.format(currSample.getFrequency()) + ") and " + (i - 1) + " (" + nf.format(lastSample.getFrequency()) + ")");
					}
					// copy relevant data from previous previous point
					// pSamples[i - 1].copy(pSamples[i - 2])
					// pSamples[i].copy(pSamples[i - 1])
					// pSamples[i + 1].copy(pSamples[i + 2])
					currSample.copy(lastSample);
					break;
				}
				lastSample = currSample;
			}
		}
		TraceHelper.exit(instance, methodName);
	}
}
