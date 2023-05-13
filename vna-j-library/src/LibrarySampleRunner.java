import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.library.VNALibrary;

public class LibrarySampleRunner {

	/**
	 * Run some sample scans
	 * 
	 * @param args [0] driver name, [1] serial port
	 */
	public static void main(String[] args) {
		VNALibrary lib = null;
		VNACalibratedSampleBlock rc = null;
		try {
			lib = new VNALibrary();
			//
			// lib.loadDriverByName(args[0], args[1]);
			lib.loadDriverByName("miniVNA Tiny", "COM13");
			// lib.loadDriverByName("miniVNA-pro", "COM3");
			// lib.loadDriverByName("miniVNA-pro2", "COM3");
			// lib.loadDriverByName("miniVNA-pro-extender", "COM3");
			// lib.loadDriverByName("miniVNA", "COM3");

			//
			// test for reflection scan
			System.out.printf("Testing reflection scan ...\n");
			lib.loadCalibrationFile("C:/Users/dietmar/vnaJ.3.4/calibration/REFL_miniVNA Tiny.cal");
			rc = lib.scan(1000000, 2000000, 100, "REFL");
			System.out.printf("   num REFL samples read = %d\n", rc.getCalibratedSamples().length);
			System.out.printf("   [0] f =%d\n", rc.getCalibratedSamples()[0].getFrequency());
			System.out.printf("   [0] rl=%f\n", rc.getCalibratedSamples()[0].getReflectionLoss());
			System.out.printf("   [0] rp=%f\n", rc.getCalibratedSamples()[0].getReflectionPhase());

			//
			// test for transmission scan
			System.out.printf("Testing transmission scan ...\n");
			lib.loadCalibrationFile("C:/Users/dietmar/vnaJ.3.4/calibration/TRAN_miniVNA Tiny.cal");
			rc = lib.scan(1000000, 2000000, 100, "TRAN");
			System.out.printf("   num TRAN samples read = %d\n", rc.getCalibratedSamples().length);
			System.out.printf("   [0] f =%d\n", rc.getCalibratedSamples()[0].getFrequency());
			System.out.printf("   [0] tl=%f\n", rc.getCalibratedSamples()[0].getTransmissionLoss());
			System.out.printf("   [0] tp=%f\n", rc.getCalibratedSamples()[0].getTransmissionPhase());
			

		} catch (Exception e) {
			System.out.println("failed with " + e.getMessage());
		} finally {
			if (lib != null) {
				lib.shutdown();
			}
		}
	}
}
