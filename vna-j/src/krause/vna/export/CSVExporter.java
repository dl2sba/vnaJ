/**
 * ********************************************************************************** 
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package krause.vna.export;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;

import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;

public class CSVExporter extends VNAExporter {

	public CSVExporter(VNAMainFrame mainFrame) {
		super(mainFrame);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.export.VNAExporter#export(java.lang.String, boolean)
	 */
	public String export(String fnp, boolean overwrite) throws ProcessingException {
		TraceHelper.entry(this, "export", fnp);
		VNACalibratedSampleBlock blk = datapool.getCalibratedData();
		VNACalibratedSample[] samples = blk.getCalibratedSamples();
		String currFilename = check4FileToDelete(fnp, overwrite);
		if (currFilename != null) {
			DecimalFormat df = new DecimalFormat();
			char decSep = df.getDecimalFormatSymbols().getDecimalSeparator();
			char valSep = ',';
			if (decSep == ',') {
				valSep = ';';
			} else if (decSep == ';') {
				valSep = ',';
			}

			try (FileOutputStream fos = new FileOutputStream(currFilename); Writer w = new BufferedWriter(new OutputStreamWriter(fos, "Cp850"));) {
				// write header
				w.write("Frequency(Hz)");
				w.write(valSep);
				boolean isTransmissionMode = datapool.getScanMode().isTransmissionMode();
				if (isTransmissionMode) {
					w.write("Transmission Loss(dB)");
				} else {
					w.write("Return Loss(dB)");
				}
				w.write(valSep);
				w.write("Phase(deg)");
				w.write(valSep);
				w.write("Rs");
				w.write(valSep);
				w.write("SWR");
				w.write(valSep);
				w.write("Xs");
				w.write(valSep);
				w.write("|Z|");
				w.write(valSep);
				w.write("Theta");
				w.write(GlobalSymbols.LINE_SEPARATOR);

				// write data
				for (int i = 0; i < samples.length; ++i) {
					VNACalibratedSample data = samples[i];
					w.write(VNAFormatFactory.getFrequencyFormat4Export().format(data.getFrequency()));
					w.write(valSep);
					if (isTransmissionMode) {
						w.write(VNAFormatFactory.getReflectionLossFormat().format(data.getTransmissionLoss()));
						w.write(valSep);
						w.write(VNAFormatFactory.getPhaseFormat().format(data.getTransmissionPhase()));
						w.write(valSep);
					} else {
						w.write(VNAFormatFactory.getReflectionLossFormat().format(data.getReflectionLoss()));
						w.write(valSep);
						w.write(VNAFormatFactory.getPhaseFormat().format(data.getReflectionPhase()));
						w.write(valSep);
					}
					w.write(VNAFormatFactory.getRsFormat().format(data.getR()));
					w.write(valSep);
					w.write(VNAFormatFactory.getSwrFormat().format(data.getSWR()));
					w.write(valSep);
					w.write(VNAFormatFactory.getXsFormat().format(data.getX()));
					w.write(valSep);
					w.write(VNAFormatFactory.getZFormat().format(data.getZ()));
					w.write(valSep);
					w.write(VNAFormatFactory.getZFormat().format(data.getTheta()));
					w.write(GlobalSymbols.LINE_SEPARATOR);
				}
				w.flush();
			} catch (IOException e) {
				ErrorLogHelper.exception(CSVExporter.class, "doExportCSV", e);
				throw new ProcessingException(e);
			}
		}
		TraceHelper.exit(CSVExporter.class, "doExportCSV");
		return currFilename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.export.VNAExporter#getExtension()
	 */
	@Override
	public String getExtension() {
		return ".csv";
	}
}
