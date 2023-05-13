/**
 * Copyright (C) 2007 Dietmar Krause, DL2SBA
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
package krause.vna.gui.format;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Simple factory for commonly used formats inside VNA
 * 
 * @author Dietmar Krause
 * 
 */
public final class VNAFormatFactory {
	private static NumberFormat formatFrequency = null;
	private static NumberFormat formatFrequencyExport = null;
	private static NumberFormat formatPhase = null;
	private static NumberFormat formatReturnLoss = null;
	private static NumberFormat formatRSS = null;
	private static NumberFormat formatSWR = null;
	private static NumberFormat formatResistanceBase = null;
	private static NumberFormat formatResistance = null;
	private static NumberFormat formatRs = null;
	private static NumberFormat formatXs = null;
	private static NumberFormat formatZ = null;
	private static NumberFormat formatTheta = null;
	private static NumberFormat formatFrequencyCalibration = null;
	private static NumberFormat formatLength = null;
	private static NumberFormat formatVelocity = null;
	private static NumberFormat formatQ = null;
	private static NumberFormat formatInductivity = null;
	private static NumberFormat formatCapacity = null;
	private static NumberFormat formatTransScale = null;
	private static NumberFormat formatMag = null;
	private static NumberFormat formatGain = null;
	private static NumberFormat formatTemp = null;
	private static NumberFormat formatGroupDelay = null;
	private static NumberFormat formatMemoryMiB = null;
	private static DateFormat formatDateTime = null;
	private static NumberFormat formatUKNumber = null;
	private static NumberFormat formatPortExtensionLength = null;

	private VNAFormatFactory() {
	}

	public static NumberFormat getCapacityFormat() {
		if (formatCapacity == null) {
			formatCapacity = new VNACapacityFormat();
		}
		return formatCapacity;
	}

	/**
	 * @return
	 */
	public static DateFormat getDateTimeFormat() {
		if (formatDateTime == null) {
			formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		return formatDateTime;
	}

	/**
	 * A common format for DDS ticks values
	 * 
	 * @return the format for SWR values
	 */
	public static NumberFormat getFrequencyCalibrationFormat() {
		if (formatFrequencyCalibration == null) {
			formatFrequencyCalibration = NumberFormat.getNumberInstance();
			formatFrequencyCalibration.setGroupingUsed(false);
			formatFrequencyCalibration.setMaximumFractionDigits(0);
			formatFrequencyCalibration.setMinimumFractionDigits(0);
			formatFrequencyCalibration.setMaximumIntegerDigits(10);
			formatFrequencyCalibration.setMinimumIntegerDigits(1);
		}
		return formatFrequencyCalibration;
	}

	/**
	 * A common format for frequencies
	 * 
	 * @return the format for frequencies
	 */
	public static NumberFormat getFrequencyFormat() {
		if (formatFrequency == null) {
			formatFrequency = NumberFormat.getIntegerInstance();
			formatFrequency.setGroupingUsed(true);
			formatFrequency.setMaximumFractionDigits(0);
			formatFrequency.setMinimumFractionDigits(0);
			formatFrequency.setMaximumIntegerDigits(12);
			formatFrequency.setMinimumIntegerDigits(1);
		}
		return formatFrequency;
	}

	/**
	 * A common format for frequencies
	 * 
	 * @return the format for frequencies
	 */
	public static NumberFormat getFrequencyFormat4Export() {
		if (formatFrequencyExport == null) {
			formatFrequencyExport = NumberFormat.getIntegerInstance();
			formatFrequencyExport.setMaximumFractionDigits(0);
			formatFrequencyExport.setMinimumFractionDigits(0);
			formatFrequencyExport.setMaximumIntegerDigits(10);
			formatFrequencyExport.setMinimumIntegerDigits(1);
			formatFrequencyExport.setGroupingUsed(false);
		}
		return formatFrequencyExport;
	}

	public static NumberFormat getInductivityFormat() {
		if (formatInductivity == null) {
			formatInductivity = new VNAInductivityFormat();
		}
		return formatInductivity;
	}

	public static NumberFormat getLengthFormat() {
		if (formatLength == null) {
			formatLength = NumberFormat.getNumberInstance();
			formatLength.setGroupingUsed(false);
			formatLength.setMaximumFractionDigits(3);
			formatLength.setMinimumFractionDigits(3);
			formatLength.setMaximumIntegerDigits(4);
			formatLength.setMinimumIntegerDigits(1);
		}
		return formatLength;
	}

	public static NumberFormat getPortExtensionLengthFormat() {
		if (formatPortExtensionLength == null) {
			formatPortExtensionLength = NumberFormat.getNumberInstance();
			formatPortExtensionLength.setGroupingUsed(false);
			formatPortExtensionLength.setMaximumFractionDigits(5);
			formatPortExtensionLength.setMinimumFractionDigits(5);
			formatPortExtensionLength.setMaximumIntegerDigits(4);
			formatPortExtensionLength.setMinimumIntegerDigits(1);
		}
		return formatPortExtensionLength;
	}

	/**
	 * A common format for resistance
	 * 
	 * @return the format for frequencies
	 */
	public static NumberFormat getMagFormat() {
		if (formatMag == null) {
			formatMag = NumberFormat.getNumberInstance();
			formatMag.setGroupingUsed(false);
			formatMag.setMaximumFractionDigits(3);
			formatMag.setMinimumFractionDigits(3);
			formatMag.setMaximumIntegerDigits(1);
			formatMag.setMinimumIntegerDigits(1);
		}
		return formatMag;
	}

	/**
	 * A common format for phase values
	 * 
	 * @return the format for phase values
	 */
	public static NumberFormat getPhaseFormat() {
		if (formatPhase == null) {
			formatPhase = NumberFormat.getNumberInstance();
			formatPhase.setGroupingUsed(false);
			formatPhase.setMaximumFractionDigits(2);
			formatPhase.setMinimumFractionDigits(2);
			formatPhase.setMaximumIntegerDigits(3);
			formatPhase.setMinimumIntegerDigits(1);
		}
		return formatPhase;
	}

	public static NumberFormat getQFormat() {
		if (formatQ == null) {
			formatQ = NumberFormat.getNumberInstance();
			formatQ.setGroupingUsed(false);
			formatQ.setMaximumFractionDigits(1);
			formatQ.setMinimumFractionDigits(1);
			formatQ.setMaximumIntegerDigits(5);
			formatQ.setMinimumIntegerDigits(1);
		}
		return formatQ;
	}

	/**
	 * A common format for return loss values
	 * 
	 * @return the format for return loss values
	 */
	public static NumberFormat getReflectionLossFormat() {
		if (formatReturnLoss == null) {
			formatReturnLoss = NumberFormat.getNumberInstance();
			formatReturnLoss.setGroupingUsed(false);
			formatReturnLoss.setMaximumFractionDigits(2);
			formatReturnLoss.setMinimumFractionDigits(2);
			formatReturnLoss.setMaximumIntegerDigits(3);
			formatReturnLoss.setMinimumIntegerDigits(1);
		}
		return formatReturnLoss;
	}

	/**
	 * A common format for resistance without unit
	 * 
	 * @return the format for frequencies
	 */
	public static NumberFormat getResistanceBaseFormat() {
		if (formatResistanceBase == null) {
			formatResistanceBase = new VNAResistenceBaseFormat();
		}
		return formatResistanceBase;
	}

	/**
	 * A common format for resistance
	 * 
	 * @return the format for frequencies
	 */
	public static NumberFormat getResistanceFormat() {
		if (formatResistance == null) {
			formatResistance = new VNAResistenceFormat();
		}
		return formatResistance;
	}

	/**
	 * A common format for Rs
	 * 
	 * @return the format for frequencies
	 */
	public static NumberFormat getRsFormat() {
		if (formatRs == null) {
			formatRs = NumberFormat.getNumberInstance();
			formatRs.setGroupingUsed(false);
			formatRs.setMaximumFractionDigits(1);
			formatRs.setMinimumFractionDigits(1);
			formatRs.setMaximumIntegerDigits(5);
			formatRs.setMinimumIntegerDigits(1);
		}
		return formatRs;
	}

	public static NumberFormat getRSSFormat() {
		if (formatRSS == null) {
			formatRSS = NumberFormat.getNumberInstance();
			formatRSS.setGroupingUsed(false);
			formatRSS.setMaximumFractionDigits(2);
			formatRSS.setMinimumFractionDigits(2);
			formatRSS.setMaximumIntegerDigits(3);
			formatRSS.setMinimumIntegerDigits(1);
		}
		return formatRSS;
	}

	/**
	 * A common format for SWR values
	 * 
	 * @return the format for SWR values
	 */
	public static NumberFormat getSwrFormat() {
		if (formatSWR == null) {
			formatSWR = NumberFormat.getNumberInstance();
			formatSWR.setGroupingUsed(false);
			formatSWR.setMaximumFractionDigits(2);
			formatSWR.setMinimumFractionDigits(2);
			formatSWR.setMaximumIntegerDigits(3);
			formatSWR.setMinimumIntegerDigits(1);
		}
		return formatSWR;
	}

	/**
	 * @return
	 */
	public static NumberFormat getTransmissionScaleFormat() {
		if (formatTransScale == null) {
			formatTransScale = NumberFormat.getNumberInstance();
			formatTransScale.setGroupingUsed(false);
			formatTransScale.setMaximumFractionDigits(5);
			formatTransScale.setMinimumFractionDigits(5);
			formatTransScale.setMaximumIntegerDigits(1);
			formatTransScale.setMinimumIntegerDigits(1);
		}
		return formatTransScale;
	}

	public static NumberFormat getVelocityFormat() {
		if (formatVelocity == null) {
			formatVelocity = NumberFormat.getNumberInstance();
			formatVelocity.setGroupingUsed(false);
			formatVelocity.setMaximumFractionDigits(2);
			formatVelocity.setMinimumFractionDigits(2);
			formatVelocity.setMaximumIntegerDigits(1);
			formatVelocity.setMinimumIntegerDigits(1);
		}
		return formatVelocity;
	}

	/**
	 * A common format for resistance
	 * 
	 * @return the format for frequencies
	 */
	public static NumberFormat getXsFormat() {
		if (formatXs == null) {
			formatXs = NumberFormat.getNumberInstance();
			formatXs.setGroupingUsed(false);
			formatXs.setMaximumFractionDigits(1);
			formatXs.setMinimumFractionDigits(1);
			formatXs.setMaximumIntegerDigits(5);
			formatXs.setMinimumIntegerDigits(1);
		}
		return formatXs;
	}

	/**
	 * A common format for resistance
	 * 
	 * @return the format for frequencies
	 */
	public static NumberFormat getZFormat() {
		if (formatZ == null) {
			formatZ = NumberFormat.getNumberInstance();
			formatZ.setGroupingUsed(false);
			formatZ.setMaximumFractionDigits(1);
			formatZ.setMinimumFractionDigits(1);
			formatZ.setMaximumIntegerDigits(5);
			formatZ.setMinimumIntegerDigits(1);
		}
		return formatZ;
	}

	/**
	 * A common format for gain
	 * 
	 * @return the format for gain
	 */
	public static NumberFormat getGainFormat() {
		if (formatGain == null) {
			formatGain = NumberFormat.getNumberInstance();
			formatGain.setGroupingUsed(false);
			formatGain.setMaximumFractionDigits(5);
			formatGain.setMinimumFractionDigits(5);
			formatGain.setMaximumIntegerDigits(1);
			formatGain.setMinimumIntegerDigits(1);
		}
		return formatGain;
	}

	/**
	 * A common format for temperature
	 * 
	 * @return the format for gain
	 */
	public static NumberFormat getTemperatureFormat() {
		if (formatTemp == null) {
			formatTemp = NumberFormat.getNumberInstance();
			formatTemp.setGroupingUsed(false);
			formatTemp.setMaximumFractionDigits(1);
			formatTemp.setMinimumFractionDigits(1);
			formatTemp.setMaximumIntegerDigits(2);
			formatTemp.setMinimumIntegerDigits(1);
		}
		return formatTemp;
	}

	/**
	 * a common format for theta
	 * 
	 * @return
	 */
	public static NumberFormat getThetaFormat() {
		if (formatTheta == null) {
			formatTheta = NumberFormat.getNumberInstance();
			formatTheta.setGroupingUsed(false);
			formatTheta.setMaximumFractionDigits(1);
			formatTheta.setMinimumFractionDigits(1);
			formatTheta.setMaximumIntegerDigits(3);
			formatTheta.setMinimumIntegerDigits(1);
		}
		return formatTheta;
	}

	/**
	 * a common format for group delay
	 * 
	 * @return
	 */
	public static NumberFormat getGroupDelayFormat() {
		if (formatGroupDelay == null) {
			formatGroupDelay = NumberFormat.getNumberInstance();
			formatGroupDelay.setGroupingUsed(false);
			formatGroupDelay.setMaximumFractionDigits(1);
			formatGroupDelay.setMinimumFractionDigits(1);
			formatGroupDelay.setMaximumIntegerDigits(4);
			formatGroupDelay.setMinimumIntegerDigits(1);
		}
		return formatGroupDelay;
	}

	/**
	 * format the given values in bytes in MiB
	 * 
	 * @param mem
	 * @return
	 */
	public static String formatMemoryInMiB(final long mem) {
		if (formatMemoryMiB == null) {
			formatMemoryMiB = NumberFormat.getNumberInstance();
			formatMemoryMiB.setGroupingUsed(false);
			formatMemoryMiB.setMaximumFractionDigits(1);
			formatMemoryMiB.setMinimumFractionDigits(1);
			formatMemoryMiB.setMaximumIntegerDigits(10);
			formatMemoryMiB.setMinimumIntegerDigits(1);
		}
		return formatMemoryMiB.format(mem / 1048576.0);
	}

	/**
	 * format the given values in bytes in KiB
	 * 
	 * @param mem
	 * @return
	 */
	public static String formatMemoryInKiB(long mem) {
		if (formatMemoryMiB == null) {
			formatMemoryMiB = NumberFormat.getNumberInstance();
			formatMemoryMiB.setGroupingUsed(true);
			formatMemoryMiB.setMaximumFractionDigits(1);
			formatMemoryMiB.setMinimumFractionDigits(1);
			formatMemoryMiB.setMaximumIntegerDigits(10);
			formatMemoryMiB.setMinimumIntegerDigits(0);
		}
		return formatMemoryMiB.format(mem / 1024.0);
	}

	/**
	 * format the frequency given in Hz in kHz
	 * 
	 * @param f
	 * @return
	 */
	public static String formatFrequency(long f) {
		if (formatFrequency == null) {
			formatFrequency = NumberFormat.getNumberInstance();
			formatFrequency.setGroupingUsed(true);
			formatFrequency.setMaximumFractionDigits(0);
			formatFrequency.setMinimumFractionDigits(0);
			formatFrequency.setMaximumIntegerDigits(12);
			formatFrequency.setMinimumIntegerDigits(1);
		}

		return formatFrequency.format(f);
	}

	/**
	 * return a number instance in UK format
	 * 
	 * @param n
	 * @return
	 */
	public static NumberFormat getUKNumberFormat() {
		if (formatUKNumber == null) {
			formatUKNumber = NumberFormat.getNumberInstance(Locale.ENGLISH);
			formatUKNumber.setGroupingUsed(false);
			formatUKNumber.setMaximumFractionDigits(3);
			formatUKNumber.setMinimumFractionDigits(3);
			formatUKNumber.setMaximumIntegerDigits(1);
			formatUKNumber.setMinimumIntegerDigits(1);
		}

		return formatUKNumber;
	}
}
