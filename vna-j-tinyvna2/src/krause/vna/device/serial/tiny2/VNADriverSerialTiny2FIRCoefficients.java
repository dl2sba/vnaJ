/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNADriverSerialProFIRCoefficients.java
 *  Part of:   vna-j-minivnapro
 */

package krause.vna.device.serial.tiny2;

/**
 * @author Dietmar
 * 
 */
public interface VNADriverSerialTiny2FIRCoefficients {
	public static double NULL[] = {
			1
	};

	public static double MOVING_AVERAGE_4[] = {
			0.25,
			0.25,
			0.25,
			0.25,
	};

	public static double MOVING_AVERAGE_10[] = {
			0.1,
			0.1,
			0.1,
			0.1,
			0.1,
			0.1,
			0.1,
			0.1,
			0.1,
			0.1,
	};

	// FilterType = LoPass, fcut1 = 250Hz, fcut2 = niHz
	// fsample = 1kHz, Window = Blackman, Tabs = 77
	public static double FIR_Lowpass_250Hz_1kHz_77[] = {
			-0.000000,
			0.000005,
			-0.000000,
			-0.000051,
			0.000000,
			0.000157,
			-0.000000,
			-0.000345,
			0.000000,
			0.000645,
			-0.000000,
			-0.001099,
			0.000000,
			0.001761,
			-0.000000,
			-0.002692,
			0.000000,
			0.003972,
			-0.000000,
			-0.005696,
			0.000000,
			0.007988,
			-0.000000,
			-0.011018,
			0.000000,
			0.015039,
			-0.000000,
			-0.020472,
			0.000000,
			0.028099,
			-0.000000,
			-0.039594,
			0.000000,
			0.059337,
			-0.000000,
			-0.103457,
			0.000000,
			0.317419,
			0.500000,
			0.317419,
			0.000000,
			-0.103457,
			-0.000000,
			0.059337,
			0.000000,
			-0.039594,
			-0.000000,
			0.028099,
			0.000000,
			-0.020472,
			-0.000000,
			0.015039,
			0.000000,
			-0.011018,
			-0.000000,
			0.007988,
			0.000000,
			-0.005696,
			-0.000000,
			0.003972,
			0.000000,
			-0.002692,
			-0.000000,
			0.001761,
			0.000000,
			-0.001099,
			-0.000000,
			0.000645,
			0.000000,
			-0.000345,
			-0.000000,
			0.000157,
			0.000000,
			-0.000051,
			-0.000000,
			0.000005,
			-0.000000,
	};

	// FilterType = LoPass, fcut1 = 100Hz, fcut2 = niHz
	// fsample = 1kHz, Window = Blackman, Tabs = 77
	public static double[] FIR_Lowpass_100Hz_1kHz_77 = {
			0.000000,
			-0.000005,
			-0.000013,
			0.000000,
			0.000056,
			0.000149,
			0.000227,
			0.000203,
			-0.000000,
			-0.000379,
			-0.000808,
			-0.001046,
			-0.000823,
			0.000000,
			0.001286,
			0.002560,
			0.003122,
			0.002335,
			-0.000000,
			-0.003348,
			-0.006431,
			-0.007597,
			-0.005524,
			0.000000,
			0.007573,
			0.014303,
			0.016684,
			0.012033,
			-0.000000,
			-0.016516,
			-0.031568,
			-0.037656,
			-0.028174,
			0.000000,
			0.044718,
			0.098393,
			0.149677,
			0.186574,
			0.200000,
			0.186574,
			0.149677,
			0.098393,
			0.044718,
			0.000000,
			-0.028174,
			-0.037656,
			-0.031568,
			-0.016516,
			-0.000000,
			0.012033,
			0.016684,
			0.014303,
			0.007573,
			0.000000,
			-0.005524,
			-0.007597,
			-0.006431,
			-0.003348,
			-0.000000,
			0.002335,
			0.003122,
			0.002560,
			0.001286,
			0.000000,
			-0.000823,
			-0.001046,
			-0.000808,
			-0.000379,
			-0.000000,
			0.000203,
			0.000227,
			0.000149,
			0.000056,
			0.000000,
			-0.000013,
			-0.000005,
			0.000000,
	};

	// FilterType = BandPass, fcut1 = 4kHz, fcut2 = 6kHz
	// fsample = 20kHz, Window = Blackman, Tabs = 77

	public static double[] FIR_Bandpass_4kHz_6kHz_20kHz_77 = {
			-0.000000,
			-0.000000,
			-0.000042,
			0.000000,
			0.000182,
			-0.000000,
			-0.000281,
			0.000000,
			-0.000000,
			0.000000,
			0.000999,
			-0.000000,
			-0.002664,
			0.000000,
			0.004162,
			-0.000000,
			-0.003859,
			0.000000,
			-0.000000,
			0.000000,
			0.007949,
			-0.000000,
			-0.017876,
			0.000000,
			0.024507,
			-0.000000,
			-0.020623,
			0.000000,
			-0.000000,
			0.000000,
			0.039020,
			-0.000000,
			-0.091172,
			0.000000,
			0.144711,
			-0.000000,
			-0.185011,
			0.000000,
			0.200000,
			0.000000,
			-0.185011,
			-0.000000,
			0.144711,
			0.000000,
			-0.091172,
			-0.000000,
			0.039020,
			0.000000,
			-0.000000,
			0.000000,
			-0.020623,
			-0.000000,
			0.024507,
			0.000000,
			-0.017876,
			-0.000000,
			0.007949,
			0.000000,
			-0.000000,
			0.000000,
			-0.003859,
			-0.000000,
			0.004162,
			0.000000,
			-0.002664,
			-0.000000,
			0.000999,
			0.000000,
			-0.000000,
			0.000000,
			-0.000281,
			-0.000000,
			0.000182,
			0.000000,
			-0.000042,
			-0.000000,
			-0.000000,
	};

	// FilterType = LoPass, fcut1 = 300Hz, fcut2 = niHz
	// fsample = 10kHz, Window = Hamming, Tabs = 45
	public static double[] FIR_Lowpass_300Hz_10kHz_45 = {
			-0.000977,
			-0.000936,
			-0.000923,
			-0.000867,
			-0.000673,
			-0.000226,
			0.000595,
			0.001910,
			0.003822,
			0.006405,
			0.009699,
			0.013693,
			0.018329,
			0.023495,
			0.029032,
			0.034737,
			0.040381,
			0.045717,
			0.050497,
			0.054490,
			0.057497,
			0.059366,
			0.060000,
			0.059366,
			0.057497,
			0.054490,
			0.050497,
			0.045717,
			0.040381,
			0.034737,
			0.029032,
			0.023495,
			0.018329,
			0.013693,
			0.009699,
			0.006405,
			0.003822,
			0.001910,
			0.000595,
			-0.000226,
			-0.000673,
			-0.000867,
			-0.000923,
			-0.000936,
			-0.000977,
	};

	public static double[] FIR_Lowpass_100Hz_1kHz_45 = {
			-0.000000,
			0.000016,
			-0.000000,
			-0.000173,
			-0.000548,
			-0.000953,
			-0.000949,
			0.000000,
			0.002137,
			0.004933,
			0.006864,
			0.005783,
			-0.000000,
			-0.010261,
			-0.021771,
			-0.028417,
			-0.022952,
			0.000000,
			0.040874,
			0.093564,
			0.146377,
			0.185539,
			0.200000,
			0.185539,
			0.146377,
			0.093564,
			0.040874,
			0.000000,
			-0.022952,
			-0.028417,
			-0.021771,
			-0.010261,
			-0.000000,
			0.005783,
			0.006864,
			0.004933,
			0.002137,
			0.000000,
			-0.000949,
			-0.000953,
			-0.000548,
			-0.000173,
			-0.000000,
			0.000016,
			-0.000000,
	};
}
