package krause.vna.library.test;

import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.library.VNALibrary;

import org.junit.Test;

public class Instantiate {

	static void begin(String name) {
		System.out.printf("%-46s", name);
	}

	static void fail(String format, Object... args) {
		System.out.println(" FAILED");
		System.out.println("------------------------------------------------------------");
		System.out.printf(format, args);
		System.out.println();
		System.out.println("------------------------------------------------------------");
	}

	static void finishedOK() {
		finishedOK("");
	}

	static void finishedOK(String format, Object... args) {
		System.out.printf(" OK " + format, args);
		System.out.println();
	}

	static public void init(String[] args) {
	}

	private void dumpCalSamples(VNACalibratedSampleBlock rc) {
		System.out.println("------------------------------------------------------------");
		System.out.printf("num CAL samples = %d\n", rc.getCalibratedSamples().length);
		System.out.println("------------------------------------------------------------");
	}

	private void dumpRawSamples(VNASampleBlock rc) {
		System.out.println("------------------------------------------------------------");
		System.out.printf("num RAW samples = %d\n", rc.getSamples().length);
		System.out.println("------------------------------------------------------------");
	}

	@Test
	public void test_Refl() {
		VNALibrary lib = null;
		try {
			lib = new VNALibrary();
			//
			lib.loadDriverByName("miniVNA Tiny", "COM13");
			// lib.loadDriverByName("miniVNA-pro", "COM3");
			// lib.loadDriverByName("miniVNA-pro-extender", "COM3");
			// lib.loadDriverByName("miniVNA", "COM3");
			//
			lib.loadCalibrationFile("C:/Users/dietmar/vnaJ.3.4/calibration/REFL_miniVNA Tiny.cal");
			VNACalibratedSampleBlock rc = lib.scan(1000000, 2000000, 100, "REFL");
			dumpCalSamples(rc);
			finishedOK("test_Refl::all fine");
		} catch (Exception e) {
			fail("failed with", e.getMessage());
		} finally {
			if (lib != null) {
				lib.shutdown();
			}
		}

	}

	@Test
	public void test_Tran() {
		VNALibrary lib = null;
		try {
			lib = new VNALibrary();
			//
			lib.loadDriverByName("miniVNA Tiny", "COM13");
			// lib.loadDriverByName("miniVNA-pro", "COM3");
			// lib.loadDriverByName("miniVNA-pro-extender", "COM3");
			// lib.loadDriverByName("miniVNA", "COM3");
			//
			lib.loadCalibrationFile("C:/Users/dietmar/vnaJ.3.4/calibration/TRAN_miniVNA Tiny.cal");
			VNACalibratedSampleBlock rc = lib.scan(1000000, 2000000, 100, "TRAN");
			dumpCalSamples(rc);
			finishedOK("test_Tran::all fine");
		} catch (Exception e) {
			fail("failed with", e.getMessage());
		} finally {
			if (lib != null) {
				lib.shutdown();
			}
		}

	}

	@Test
	public void test_RAW() {
		VNALibrary lib = null;
		try {
			lib = new VNALibrary();
			//
			lib.loadDriverByName("miniVNA Tiny", "COM13");
			// lib.loadDriverByName("miniVNA-pro", "COM3");
			// lib.loadDriverByName("miniVNA-pro-extender", "COM3");
			// lib.loadDriverByName("miniVNA", "COM3");
			//
			lib.loadCalibrationFile("C:/Users/dietmar/vnaJ.3.4/calibration/REFL_miniVNA Tiny.cal");
			VNASampleBlock rc = lib.scanRaw(1000000, 2000000, 100, "REFL");
			dumpRawSamples(rc);
			finishedOK("test_RAW::all fine");
		} catch (Exception e) {
			fail("failed with", e.getMessage());
		} finally {
			if (lib != null) {
				lib.shutdown();
			}
		}

	}

}
