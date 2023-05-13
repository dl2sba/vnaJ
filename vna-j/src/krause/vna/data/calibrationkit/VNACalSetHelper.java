package krause.vna.data.calibrationkit;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class VNACalSetHelper {

	/**
	 * write the calset data out to the defined filename
	 * 
	 * @param filename
	 * @return
	 */
	public boolean save(final List<VNACalibrationKit> calSets, final String myFileName) {
		TraceHelper.entry(this, "save");
		boolean result = false;

		TraceHelper.text(this, "save", "Trying to write to [" + myFileName + "]");

		try (FileOutputStream fos = new FileOutputStream(myFileName); XMLEncoder enc = new XMLEncoder(fos);) {
			for (VNACalibrationKit aCalSet : calSets) {
				enc.writeObject(aCalSet);
			}
			result = true;
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "save", e);
			ErrorLogHelper.text(this, "save", e.getMessage());
		}
		TraceHelper.exitWithRC(this, "save", result);
		return result;
	}

	/**
	 * read the calsets from the named file
	 * 
	 * @param myFileName
	 * @return
	 */
	public List<VNACalibrationKit> load(String myFileName) {
		TraceHelper.entry(this, "load");
		List<VNACalibrationKit> result = new ArrayList<>();
		TraceHelper.text(this, "load", "Trying to read from [" + myFileName + "]");

		try (FileInputStream fis = new FileInputStream(myFileName); XMLDecoder dec = new XMLDecoder(fis);) {
			while (true) {
				result.add((VNACalibrationKit) dec.readObject());
			}
		} catch (ArrayIndexOutOfBoundsException e2) {
			// we have reached the end of the universe
		} catch (IOException e) {
			TraceHelper.text(this, "load", "file [" + myFileName + "] not found. Using defaults.");
		}
		
		if ( result.isEmpty()) {
			result.add(new VNACalibrationKit("DEFAULT"));
		}
		TraceHelper.exitWithRC(this, "load", result);
		return result;
	}

}
