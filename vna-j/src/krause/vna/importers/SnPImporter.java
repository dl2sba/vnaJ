/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: SnPImporter.java
 *  Part of:   vna-j
 */

package krause.vna.importers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.math3.complex.Complex;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.importers.SnPInfoBlock.FORMAT;
import krause.vna.importers.SnPInfoBlock.PARAMETER;

/**
 * 
 * <a href="http://www.eda.org/pub/ibis/connector/touchstone_spec11.pdf">S- parameter file spec</a>
 * 
 * @author Dietmar
 * 
 */
public class SnPImporter {

	private Map<String, PARAMETER> PARAMETER_MAP = new HashMap<String, PARAMETER>();
	private Map<String, FORMAT> FORMAT_MAP = new HashMap<String, FORMAT>();

	/**
	 * 
	 */
	public SnPImporter() {
		PARAMETER_MAP.put("S", PARAMETER.S);
		PARAMETER_MAP.put("Y", PARAMETER.Y);
		PARAMETER_MAP.put("Z", PARAMETER.Z);
		PARAMETER_MAP.put("H", PARAMETER.H);
		PARAMETER_MAP.put("G", PARAMETER.G);

		FORMAT_MAP.put("DB", FORMAT.DB);
		FORMAT_MAP.put("MA", FORMAT.MA);
		FORMAT_MAP.put("RI", FORMAT.RI);
	};

	/**
	 * @param infoblock
	 * @param line
	 * @return
	 */
	private SnPRecord analyseDataLine(SnPInfoBlock infoblock, String line) {
		SnPRecord rc = null;
		TraceHelper.entry(this, "analyseDataLine");
		String[] parts = line.toUpperCase().split("\\s+");

		if (parts.length > 1) {
			rc = new SnPRecord();

			//
			double freq = Double.parseDouble(parts[0]);
			rc.setFrequency((long) (freq * infoblock.getFrequencyMultiplier()));

			int pairs = (parts.length - 1) / 2;

			if (infoblock.getFormat() == FORMAT.DB) {
				for (int i = 0; i < pairs; ++i) {

					double loss = Double.parseDouble(parts[1 + 2 * i]);
					double phase = Double.parseDouble(parts[2 + 2 * i]);

					rc.getLoss()[i] = loss;
					rc.getPhase()[i] = phase;
				}

			} else if (infoblock.getFormat() == FORMAT.RI) {
				for (int i = 0; i < pairs; ++i) {

					double real = Double.parseDouble(parts[1 + 2 * i]);
					double imag = Double.parseDouble(parts[2 + 2 * i]);

					// 20 * Log(sqr(Re^2 + Im^2))
					rc.getLoss()[i] = 20.0 * Math.log(Math.sqrt(real * real + imag * imag));

					// arctan(Im / Re)
					rc.getPhase()[i] = Math.atan(imag / real);
				}
				infoblock.setFormat(FORMAT.DB);
			}

			// fill not supplied pairs with NaN
			for (int i = pairs; i < 4; ++i) {
				rc.getLoss()[i] = Double.NaN;
				rc.getPhase()[i] = Double.NaN;
			}
		}

		TraceHelper.exit(this, "analyseDataLine");
		return rc;
	}

	/**
	 * @param infoBlock
	 * @param line
	 * @return
	 */
	private int analyseOptionLine(SnPInfoBlock infoBlock, String line) {
		int rc = 99;
		TraceHelper.entry(this, "analyseOptionLine");
		String[] parts = line.toUpperCase().split("\\s+");

		if (parts.length == 6) {
			//
			if ("GHZ".equals(parts[1])) {
				infoBlock.setFrequencyMultiplier(1000000000l);
			} else if ("MHZ".equals(parts[1])) {
				infoBlock.setFrequencyMultiplier(1000000l);
			} else if ("KHZ".equals(parts[1])) {
				infoBlock.setFrequencyMultiplier(1000l);
			} else {
				infoBlock.setFrequencyMultiplier(1l);
			}

			//
			infoBlock.setParameter(PARAMETER_MAP.get(parts[2]));

			//
			infoBlock.setFormat(FORMAT_MAP.get(parts[3]));

			//
			infoBlock.setReference(new Complex(Double.parseDouble(parts[5]), 0));

			rc = 1;
		}

		TraceHelper.exit(this, "analyseOptionLine");
		return rc;

	}

	/**
	 * US-ASCII Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
	 * 
	 * ISO-8859-1 ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
	 * 
	 * UTF-8 Eight-bit UCS Transformation Format
	 * 
	 * UTF-16BE Sixteen-bit UCS Transformation Format, big-endian byte order
	 * 
	 * UTF-16LE Sixteen-bit UCS Transformation Format, little-endian byte order
	 * 
	 * UTF-16 Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark
	 * 
	 * 
	 * @param filename
	 * @param encoding
	 * @return
	 * @throws ProcessingException
	 */
	public SnPInfoBlock readFile(String filename, String encoding) throws ProcessingException {
		SnPInfoBlock rc = null;
		TraceHelper.entry(this, "readFile");

		Scanner scanner = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);
			scanner = new Scanner(fis, encoding);

			rc = new SnPInfoBlock();
			rc.setFilename(filename);

			int state = 0;
			while (scanner.hasNextLine() && state != 99) {
				String line = scanner.nextLine();
				TraceHelper.text(this, "readFile", state + "::" + line);

				line = line.trim();
				if (line.length() == 0)
					continue;

				switch (state) {
				case 0:
					if (line.startsWith("!")) {
						state = 0;
						if (rc.getComment() == null) {
							rc.setComment(line);
						} else {
							rc.setComment(rc.getComment() + line);
						}
					} else if (line.startsWith("#")) {
						state = analyseOptionLine(rc, line);
					}
					break;

				case 1:
					SnPRecord inpRec = analyseDataLine(rc, line);
					if (inpRec != null) {
						rc.getRecords().add(inpRec);
					} else {
						state = 99;
					}
					break;

				case 99:
					throw new ProcessingException("Illegal file format");
				}
			}
		} catch (FileNotFoundException e) {
			ErrorLogHelper.exception(this, "readFile", e);
			throw new ProcessingException(e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					ErrorLogHelper.exception(this, "readFile", e);
				}
			}
		}
		TraceHelper.exit(this, "readFile");
		return rc;
	}
}
