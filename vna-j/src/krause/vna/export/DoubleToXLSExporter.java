package krause.vna.export;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class DoubleToXLSExporter {
	private DoubleToXLSExporter() {

	}

	private static void dumpData(double[] input, double[] output, HSSFWorkbook wb) {

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
		row.createCell(cell++).setCellValue(new HSSFRichTextString("input"));
		row.createCell(cell).setCellValue(new HSSFRichTextString("output"));

		// add data rows
		for (int i = 0; i < input.length; ++i) {
			cell = 0;
			row = sheet.createRow(rowNum);
			row.createCell(cell++).setCellValue(input[i]);
			row.createCell(cell).setCellValue(output[i]);
			++rowNum;
		}
		// autofit column size
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
	}

	public static void export(String fnp, double[] input, double[] output) {
		final String methodName = "export";
		TraceHelper.entry(XLSExporter.class, methodName);
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			dumpData(input, output, wb);

			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(fnp);
			wb.write(fileOut);
			fileOut.close();

		} catch (IOException e) {
			ErrorLogHelper.exception(XLSExporter.class, methodName, e);
		}
		TraceHelper.exit(XLSExporter.class, methodName);
	}

}