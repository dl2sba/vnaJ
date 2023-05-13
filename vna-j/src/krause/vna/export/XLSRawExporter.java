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
import krause.vna.config.VNAConfig;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibrationPoint;

public class XLSRawExporter {
	private static final XLSRawExporter instance = new XLSRawExporter();

	private static String doubleValue(Double val) {
		if (val == null) {
			return "nan";
		} else {
			return val.toString();
		}
	}

	private static String complexRealValue(Complex val) {
		if (val == null) {
			return "nan";
		} else {
			return Double.toString(val.getReal());
		}
	}

	private static String complexImagValue(Complex val) {
		if (val == null) {
			return "nan";
		} else {
			return Double.toString(val.getImaginary());
		}
	}

	/**
	 * 
	 * @param pSamples
	 * @param pFilename
	 */
	public static String export(final VNASampleBlock block, final String pFilename) {
		TraceHelper.entry(instance, "export");
		final String currFilename = VNAConfig.getSingleton().getExportDirectory() + "/" + pFilename + ".xls";
		if (block != null && block.getSamples() != null) {
			final VNABaseSample[] pSamples = block.getSamples();

			TraceHelper.text(instance, "export", "filename=" + currFilename);
			try {
				// create workbook with samples
				final HSSFWorkbook wb = new HSSFWorkbook();
				// add new sheet
				final HSSFSheet sheet = wb.createSheet("raw");

				// add header rows
				int rowNum = 0;
				int cell = 0;
				HSSFRow row = sheet.createRow(rowNum++);
				row.createCell(cell++).setCellValue(new HSSFRichTextString("Frq"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("angle"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("loss"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("P1"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("P2"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("P3"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("P4"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("P1ref"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("P2ref"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("P3ref"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("P4ref"));
				row.createCell(cell++).setCellValue(new HSSFRichTextString("Temp"));

				// add data rows
				for (int i = 0; i < pSamples.length; ++i) {
					final VNABaseSample data = pSamples[i];
					cell = 0;
					row = sheet.createRow(rowNum);
					row.createCell(cell++).setCellValue(data.getFrequency());
					row.createCell(cell++).setCellValue(data.getAngle());
					row.createCell(cell++).setCellValue(data.getLoss());
					row.createCell(cell++).setCellValue(data.getP1());
					row.createCell(cell++).setCellValue(data.getP2());
					row.createCell(cell++).setCellValue(data.getP3());
					row.createCell(cell++).setCellValue(data.getP4());
					row.createCell(cell++).setCellValue(data.getP1Ref());
					row.createCell(cell++).setCellValue(data.getP2Ref());
					row.createCell(cell++).setCellValue(data.getP3Ref());
					row.createCell(cell++).setCellValue(data.getP4Ref());
					row.createCell(cell++).setCellValue(doubleValue(block.getDeviceTemperature()));
					++rowNum;
				}
				// autofit column size
				sheet.autoSizeColumn((short) 0);

				// Write the output to a file
				FileOutputStream fileOut = new FileOutputStream(currFilename);
				wb.write(fileOut);
				fileOut.close();
			} catch (IOException e) {
				ErrorLogHelper.exception(instance, "export", e);
			}
			TraceHelper.exitWithRC(instance, "export", currFilename);
		} else {
			TraceHelper.text(instance, "export", "No calibration data");
		}
		return currFilename;
	}

	/**
	 * 
	 * @param pSamples
	 * @param pFilename
	 */
	public static void export(final VNACalibrationPoint[] pSamples, final String pFilename) {
		TraceHelper.entry(instance, "export");
		final String currFilename = VNAConfig.getSingleton().getExportDirectory() + "/" + pFilename + ".xls";
		TraceHelper.text(instance, "export", "filename=" + currFilename);

		if (pSamples == null) {
			ErrorLogHelper.text(instance, "export", "No calibration points passed");
			return;
		}

		try {
			// create workbook with samples
			final HSSFWorkbook wb = new HSSFWorkbook();
			// add new sheet
			final HSSFSheet sheet = wb.createSheet("CalPoints");

			// add header rows
			int rowNum = 0;
			int cell = 0;
			HSSFRow row = sheet.createRow(rowNum++);
			row.createCell(cell++).setCellValue(new HSSFRichTextString("Frequency (Hz)"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("Loss"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("Phase"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("real(DeltaE)"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("imag(DeltaE)"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("real(E00)"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("imag(E00)"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("real(E01)"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("imag(E01)"));

			// add data rows
			for (int i = 0; i < pSamples.length; ++i) {
				VNACalibrationPoint data = pSamples[i];
				cell = 0;
				row = sheet.createRow(rowNum);
				row.createCell(cell++).setCellValue(data.getFrequency());
				row.createCell(cell++).setCellValue(data.getLoss());
				row.createCell(cell++).setCellValue(data.getPhase());
				row.createCell(cell++).setCellValue(complexRealValue(data.getDeltaE()));
				row.createCell(cell++).setCellValue(complexImagValue(data.getDeltaE()));
				row.createCell(cell++).setCellValue(complexRealValue(data.getE00()));
				row.createCell(cell++).setCellValue(complexImagValue(data.getE00()));
				row.createCell(cell++).setCellValue(complexRealValue(data.getE11()));
				row.createCell(cell++).setCellValue(complexImagValue(data.getE11()));
				++rowNum;
			}
			// autofit column size
			sheet.autoSizeColumn((short) 0);

			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(currFilename);
			wb.write(fileOut);
			fileOut.close();
		} catch (IOException e) {
			ErrorLogHelper.exception(instance, "export", e);
		}
		TraceHelper.exit(instance, "export");
	}

	public static void export(final VNACalibratedSample[] pSamples, final String pFilename) {
		TraceHelper.entry(instance, "export");
		final String currFilename = VNAConfig.getSingleton().getExportDirectory() + "/" + pFilename + ".xls";
		TraceHelper.text(instance, "export", "filename=" + currFilename);
		try {
			// create workbook with samples
			final HSSFWorkbook wb = new HSSFWorkbook();
			// add new sheet
			final HSSFSheet sheet = wb.createSheet("CalPoints");

			// add header rows
			int rowNum = 0;
			int cell = 0;
			HSSFRow row = sheet.createRow(rowNum++);
			row.createCell(cell++).setCellValue(new HSSFRichTextString("Frequency (Hz)"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("Magnitude"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("ReflLoss"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("ReflPhase"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("SWR"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("Theta"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("TransLoss"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("TransPhase"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("Rs"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("Xs"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("|Z|"));
			row.createCell(cell++).setCellValue(new HSSFRichTextString("GrpDly"));

			// add data rows
			for (int i = 0; i < pSamples.length; ++i) {
				VNACalibratedSample data = pSamples[i];
				cell = 0;
				row = sheet.createRow(rowNum);
				row.createCell(cell++).setCellValue(data.getFrequency());
				row.createCell(cell++).setCellValue(data.getMag());
				row.createCell(cell++).setCellValue(data.getReflectionLoss());
				row.createCell(cell++).setCellValue(data.getReflectionPhase());
				row.createCell(cell++).setCellValue(data.getSWR());
				row.createCell(cell++).setCellValue(data.getTheta());
				row.createCell(cell++).setCellValue(data.getTransmissionLoss());
				row.createCell(cell++).setCellValue(data.getTransmissionPhase());
				row.createCell(cell++).setCellValue(data.getR());
				row.createCell(cell++).setCellValue(data.getX());
				row.createCell(cell++).setCellValue(data.getZ());
				row.createCell(cell++).setCellValue(data.getGroupDelay());
				++rowNum;
			}
			// autofit column size
			sheet.autoSizeColumn((short) 0);

			// Write the output to a file
			FileOutputStream fileOut = new FileOutputStream(currFilename);
			wb.write(fileOut);
			fileOut.close();
		} catch (IOException e) {
			ErrorLogHelper.exception(instance, "export", e);
		}
		TraceHelper.exit(instance, "export");
	}
}
