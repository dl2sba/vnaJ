package krause.vna.export;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;

public class XLSExporter extends VNAExporter {
	public XLSExporter(VNAMainFrame mainFrame) {
		super(mainFrame);
	}

	private void dumpData(VNACalibratedSample[] dataList, HSSFWorkbook wb) throws IOException {

		// check if old sheet present
		int idx = wb.getSheetIndex("vnaJ");
		if (idx >= 0) {
			//
			// delete old sheet
			wb.removeSheetAt(idx);
		}

		// add new sheet
		HSSFSheet sheet = wb.createSheet("vnaJ");

		// add header rows
		int rowNum = 0;
		int cell = 0;
		HSSFRow row = sheet.createRow(rowNum++);
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Frequency (Hz)"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Returnloss (dB)"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Returnphase (°)"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Transmissionloss (dB)"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Transmissionphase (°)"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Rs (Ohm)"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Xs (Ohm)"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("|Z| (Ohm)"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Magnitude"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Rho real"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Rho imag"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("SWR"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("Theta"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("GroupDelay (nS)"));

		// add data rows
		for (VNACalibratedSample data : dataList) {
			cell = 0;
			row = sheet.createRow(rowNum);
			row.createCell(cell++).setCellValue(data.getFrequency());
			row.createCell(cell++).setCellValue(data.getReflectionLoss());
			row.createCell(cell++).setCellValue(data.getReflectionPhase());
			row.createCell(cell++).setCellValue(data.getTransmissionLoss());
			row.createCell(cell++).setCellValue(data.getTransmissionPhase());
			row.createCell(cell++).setCellValue(data.getR());
			row.createCell(cell++).setCellValue(data.getX());
			row.createCell(cell++).setCellValue(data.getZ());
			row.createCell(cell++).setCellValue(data.getMag());
			if (data.getRHO() != null) {
				row.createCell(cell++).setCellValue(data.getRHO().getReal());
				row.createCell(cell++).setCellValue(data.getRHO().getImaginary());
			}else {
				row.createCell(cell++);
				row.createCell(cell++);
			}
			row.createCell(cell++).setCellValue(data.getSWR());
			row.createCell(cell++).setCellValue(data.getTheta());
			row.createCell(cell++).setCellValue(data.getGroupDelay());
			++rowNum;
		}
		// autofit column size
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
	}

	public String export(String fnp, boolean overwrite) throws ProcessingException {
		TraceHelper.entry(this, "export");
		String currFilename = null;
		VNACalibratedSampleBlock blk = datapool.getCalibratedData();
		VNACalibratedSample[] pDataList = blk.getCalibratedSamples();
		try {
			currFilename = check4FileToDelete(fnp, overwrite);
			if (currFilename != null) {
				HSSFWorkbook wb = new HSSFWorkbook();
				dumpData(pDataList, wb);

				// Write the output to a file
				FileOutputStream fileOut = new FileOutputStream(currFilename);
				wb.write(fileOut);
				fileOut.close();
			}
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "export", e);
			throw new ProcessingException(e);
		}
		TraceHelper.exit(this, "export");
		return currFilename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.export.VNAExporter#getExtension()
	 */
	@Override
	public String getExtension() {
		return ".xls";
	}
}
