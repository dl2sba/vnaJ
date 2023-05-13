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

import java.io.FileOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;

public class JpegExporter extends VNAExporter {

	public JpegExporter(VNAMainFrame mainFrame) {
		super(mainFrame);
	}

	private void writeChart2JPEG(JFreeChart aChart, String aFileName, int aWidth, int aHeight) throws IOException {
		TraceHelper.entry(this, "createChart");
		FileOutputStream fos = new FileOutputStream(aFileName);
		ChartUtilities.writeChartAsJPEG(fos, aChart, aWidth, aHeight);
		fos.close();
		TraceHelper.exit(this, "createChart");
	}

	public String export(String fnp, boolean overwrite) throws ProcessingException {
		final String methodName = "export";
		TraceHelper.entry(this, methodName, fnp);

		String currFilename = "not saved";
		VNACalibratedSampleBlock blk = datapool.getCalibratedData();
		VNACalibratedSample[] samples = blk.getCalibratedSamples();
		try {
			currFilename = check4FileToDelete(fnp, overwrite);
			if (currFilename != null) {
				JFreeChart chart = createChart(samples);
				writeChart2JPEG(chart, currFilename, config.getExportDiagramWidth(), config.getExportDiagramHeight());
			}
		} catch (IOException e) {
			ErrorLogHelper.exception(this, methodName, e);
			throw new ProcessingException(e);
		}
		TraceHelper.exitWithRC(this, methodName, currFilename);
		return currFilename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.export.VNAExporter#getExtension()
	 */
	@Override
	public String getExtension() {
		return ".jpg";
	}
}
