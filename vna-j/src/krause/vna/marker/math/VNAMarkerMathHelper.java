package krause.vna.marker.math;

import krause.util.ras.logging.TraceHelper;

public class VNAMarkerMathHelper {
	private static final double TWO_PI = Math.PI * 2;

	/*
	 * 
	 */
	private VNAMarkerMathHelper() {

	}

	/**
	 * Cs = (1 / (TWO_PI * f * Z)); 
	 * Ls = (Z / (TWO_PI * f )); 
	 * Rs = Rs from Scan data 
	 * Xs = Xs from Scan data 
	 * Rp = (Rs * (1 + ((Xs / Rs) * (Xs / Rs)))); 
	 * Xp = (Rp * Rs / Xs);
	 * 
	 * @param input
	 * @return
	 */
	public static VNAMarkerMathResult execute(VNAMarkerMathInput input) {
		VNAMarkerMathResult rc = new VNAMarkerMathResult(input);
		TraceHelper.entry(VNAMarkerMathResult.class, "execute");

		if (input.getHighFrequency() - input.getLowFrequency() > 0) {
			rc.setBandWidth(input.getHighFrequency() - input.getLowFrequency());
			rc.setQ((double) input.getCenterFrequency() / rc.getBandWidth());
		}

		final double kreisFrequenz = TWO_PI * input.getCenterFrequency();

		rc.setSerialCapacity(1 / (kreisFrequenz * input.getZ()));
		rc.setSerialInductance(input.getZ() / kreisFrequenz);

		rc.setRp(input.getRs() * (1 + ((input.getXs() / input.getRs()) * (input.getXs() / input.getRs()))));
		rc.setXp(rc.getRp() * input.getRs() / input.getXs());

		TraceHelper.exit(VNAMarkerMathResult.class, "execute");
		return rc;
	}
}
