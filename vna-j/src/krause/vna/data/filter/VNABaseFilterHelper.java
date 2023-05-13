package krause.vna.data.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.LogHelper;
import krause.util.ras.logging.TraceHelper;

public final class VNABaseFilterHelper {

	private VNABaseFilterHelper() {

	}

	/**
	 * 
	 * @return
	 */
	public static double[] loadFilterParameters(String fName) {
		final String methodeName = "loadFilterParameters";
		double[] rc = null;
		TraceHelper.entry(VNABaseFilterHelper.class, methodeName);
		List<Double> listParameters = new ArrayList<>();

		try ( // try to open file
				FileInputStream fstream = new FileInputStream(fName);
				// Get the object of DataInputStream
				DataInputStream dis = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(dis));) {

			String line;
			// Read File Line By Line
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					Double aParm = Double.valueOf(line);
					listParameters.add(aParm);
				} else {
					ErrorLogHelper.text(VNABaseFilterHelper.class, methodeName, "Empty line ignored");
				}
			}
			TraceHelper.text(VNABaseFilterHelper.class, methodeName, "Filter file read");
		} catch (IOException e) {
			ErrorLogHelper.exception(VNABaseFilterHelper.class, methodeName, e);
		}

		int paramSize = listParameters.size();
		TraceHelper.text(VNABaseFilterHelper.class, methodeName, "Filter with " + paramSize + " lines");

		// any data read and odd size?
		if ((paramSize > 0) && ((paramSize % 2) == 1)) {
			rc = new double[paramSize];
			int i = 0;
			// yes
			for (Double aDouble : listParameters) {
				rc[i++] = aDouble.doubleValue();
			}
		}

		// did we read any valid data?
		if (rc == null) {
			// no
			LogHelper.text(VNABaseFilterHelper.class, methodeName, "Creating default filter set");
			//
			rc = new Gaussian(1.0).kernel1D(15);

			// but save the data to the file
			saveFilterdata(fName, rc);
		}
		TraceHelper.entry(VNABaseFilterHelper.class, methodeName);
		return rc;
	}

	/**
	 * write the given parameters to the named file#
	 * 
	 * @param filename
	 * @param parms
	 */
	public static void saveFilterdata(final String filename, final double[] parms) {
		final String methodeName = "saveFilterdata";
		TraceHelper.entry(VNABaseFilterHelper.class, methodeName);

		try (FileOutputStream os = new FileOutputStream(filename); BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));) {
			// try to open file

			for (double parm : parms) {
				bw.write("" + parm);
				bw.newLine();
			}
			TraceHelper.text(VNABaseFilterHelper.class, methodeName, "Filter file [" + filename + "] written");
		} catch (IOException e) {
			ErrorLogHelper.exception(VNABaseFilterHelper.class, methodeName, e);
		}
		TraceHelper.entry(VNABaseFilterHelper.class, methodeName);
	}
}
