package krause.vna.export;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.math3.complex.Complex;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class ComplexToXLSExporter {

	private ComplexToXLSExporter() {

	}

	/**
	 * 
	 * @param input
	 * @param output
	 * @param wb
	 */
	private static void dumpData(Complex[] input, Complex[] output, HSSFWorkbook wb) {

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
		row.createCell(cell++).setCellValue(new HSSFRichTextString("C1.real"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("C1.imag"));
		row.createCell(cell++).setCellValue(new HSSFRichTextString("C2.real"));
		row.createCell(cell).setCellValue(new HSSFRichTextString("C2.imag"));

		// add data rows
		for (int i = 0; i < input.length; ++i) {
			cell = 0;
			row = sheet.createRow(rowNum);
			row.createCell(cell++).setCellValue(input[i].getReal());
			row.createCell(cell++).setCellValue(input[i].getImaginary());
			row.createCell(cell++).setCellValue(output[i].getReal());
			row.createCell(cell).setCellValue(output[i].getImaginary());
			++rowNum;
		}
		// autofit column size
		sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);
		sheet.autoSizeColumn((short) 3);
	}

	public static void export(String fnp, Complex[] input, Complex[] output) {
		final String methodName = "export";
		TraceHelper.entry(ComplexToXLSExporter.class, methodName);
		try {
			HSSFWorkbook wb = new HSSFWorkbook();
			dumpData(input, output, wb);

			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(fnp);
			wb.write(fileOut);
			fileOut.close();

		} catch (IOException e) {
			ErrorLogHelper.exception(ComplexToXLSExporter.class, methodName, e);
		}
		TraceHelper.exit(ComplexToXLSExporter.class, methodName);
	}

}