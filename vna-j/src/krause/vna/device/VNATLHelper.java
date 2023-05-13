package krause.vna.device;

import org.apache.commons.math3.complex.Complex;

public class VNATLHelper {
	public static final double SL = 983.571056430446; // = 299.792458 / 0.3048 (Freq=MHz)
	public static final double SLfps = 983571056.430446; // = 299792458 / 0.3048 (Freq=Hz)
	public static final double NEPER = 20.0 / Math.log(10); // 8.68588963806504 = 20 / Ln(10) dB/neper

	public static final double DEG2RAD = Math.PI / 180.0;
	public static final double RAD2DEG = 180.0 / Math.PI;
	public static final double PI_HALF = Math.PI / 2.0;
	public static final double TWO_PI = 2.0 * Math.PI;

	/**
	 * 
	 * @param freq
	 *            in MHz
	 * @param lenFt
	 * @param Zohf
	 * @param VFhf
	 * @param K1
	 * @param K2
	 * @param K0
	 * @return
	 * 
	 *         GetTLParms(Freq, LenFt, Zohf, VFhf, K1, K2, K0) GetTLParms(10, 37.9, 50, 0.66, 0.1234, 0.003456, 0.123) GetTLParms(AF48, AF49, AF50, AF51, AF52, AF53, AF54) UPDATE Sep/Oct/Nov 2010: Use Chipman/Johnson method to compute RLGC and hence Zo and Gamma at Freq. See Owen Duffy, http://vk1od.net/transmissionline/concept/mptl.htm See Johnson, "High Speed Signal Propagation", section 3.1, equations 3.3 and 3.4. Include correction for internal inductance. Include correction for DC resistance. Compute corrected VF at Freq from Gamma.
	 */
	public static VNATLParms getTLParms(double freq, double lenFt, double Zohf, double VFhf, double K1, double K2, double K0) {
		double Rdc;
		double Rhf;
		double Lhf;
		double Ghf;
		double Chf;
		double w;
		Complex Zint;
		Complex RjwL;
		Complex GjwC;
		Complex Zo;
		Complex Gamma;
		VNATLParms results = new VNATLParms();

		Rdc = 2 * (K0 / 100 / NEPER) * Zohf; // Ohms/ft (note K0 is dB/100ft)
		Rhf = 2 * (K1 / 100 / NEPER * Math.sqrt(freq)) * Zohf; // Ohms/ft (note K1 is dB/100ft)
		Lhf = Zohf / (SLfps * VFhf); // Henries/ft
		Ghf = 2 * (K2 / 100 / NEPER * freq) / Zohf; // Siemens/ft (note K2 is dB/100ft)
		Chf = 1 / (Zohf * SLfps * VFhf); // Farads/ft
		w = TWO_PI * freq * 1000000; // omega

		// Zint = IMSQRT(IMSUM(COMPLEX(Rdc ^ 2, 0), IMPOWER(COMPLEX(Rhf, Rhf), 2))) internal impedance, Rdc+Rhf+Li
		Zint = new Complex(Rhf, Rhf).multiply(new Complex(Rhf, Rhf)).add(new Complex(Rdc * Rdc, 0)).sqrt();

		// RjwL = IMSUM(Zint, COMPLEX(0, w * Lhf)) ; //Rdc+Rhf, Li+Lhf (Le)
		RjwL = new Complex(0, w * Lhf).add(Zint);

		// GjwC = COMPLEX(Ghf, w * Chf);
		GjwC = new Complex(Ghf, w * Chf);

		// Zo = IMSQRT(IMDIV(RjwL, GjwC));
		Zo = RjwL.divide(GjwC).sqrt();

		// Gamma = IMSQRT(IMPRODUCT(RjwL, GjwC));
		Gamma = RjwL.multiply(GjwC).sqrt();

		results.setZ0(Zo);
		results.setCorrectedVf(w / (SLfps * Gamma.getImaginary())); // corrected VF
		results.setLoss(Gamma.getReal() * NEPER * lenFt); // loss over LenFt, dB

		return results;
	}

	/**
	 * Returns Z (complex) at input end of transmission line. LenFt is set negative to transform in the opposite direction.
	 * 
	 * @param Zload
	 *            Z (complex) at load end of line
	 * @param Freq
	 *            Frequency, Hz
	 * @param lenFt
	 *            Line length in feet
	 * @param Zohf
	 *            Characteristic impedance (nominal)
	 * @param VFhf
	 *            Velocity factor (nominal
	 * @param K1
	 *            Coefficient for conductor loss
	 * @param K2
	 *            Coefficient for dielectric loss
	 * @param K0
	 *            DC resistance, ohms/Kft
	 * @return
	 */
	public static Complex ZIZL(Complex Zload, double Freq, double lenFt, double Zohf, double VFhf, double K1, double K2, double K0) {

		VNATLParms vTLParms;
		Complex Zo;
		double VF;
		double Loss;
		double AlphaL;
		double BetaL;
		Complex Sinh_gL;
		Complex Cosh_gL;
		Complex rc;

		Freq /= 1000000;

		// Get Zo (true Zo at Freq), VF (true VF at Freq), and Loss (dB per LenFt).
		vTLParms = getTLParms(Freq, lenFt, Zohf, VFhf, K1, K2, K0);
		Zo = vTLParms.getZ0();
		VF = vTLParms.getCorrectedVf();
		Loss = vTLParms.getLoss();

		// Propagation constant, real and imaginary components of GammaL.
		AlphaL = Loss / NEPER;
		BetaL = (TWO_PI * Freq) / (SL * VF) * lenFt;

		// Sinh_gL = COMPLEX(Cos(BetaL) * .Sinh(AlphaL), Sin(BetaL) * .Cosh(AlphaL))
		Sinh_gL = new Complex(Math.cos(BetaL) * Math.sinh(AlphaL), Math.sin(BetaL) * Math.cosh(AlphaL));

		// Cosh_gL = COMPLEX(Cos(BetaL) * .Cosh(AlphaL), Sin(BetaL) * .Sinh(AlphaL))
		Cosh_gL = new Complex(Math.cos(BetaL) * Math.cosh(AlphaL), Math.sin(BetaL) * Math.sinh(AlphaL));

		// ZIZL = IMPRODUCT(Zo, IMDIV(IMSUM(IMPRODUCT(Zload, Cosh_gL), IMPRODUCT(Zo, Sinh_gL)), IMSUM(IMPRODUCT(Zo, Cosh_gL),IMPRODUCT(Zload, Sinh_gL))))
		rc = Zo.multiply(Zload.multiply(Cosh_gL).add(Zo.multiply(Sinh_gL)).divide(Zo.multiply(Cosh_gL).add(Zload.multiply(Sinh_gL))));

		return rc;
	}
}