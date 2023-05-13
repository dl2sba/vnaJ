/**
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 */
package krause.vna.export;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.Locale;

import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.resources.VNAMessages;

/**
 * http://www.eda.org/pub/ibis/connector/touchstone_spec11.pdf
 * 
 * @author Dietmar
 * 
 */
public class SnPExporter extends VNAExporter {

	public SnPExporter(VNAMainFrame mainFrame) {
		super(mainFrame);
	}

	public String export(String fnp, boolean overwrite) throws ProcessingException {
		TraceHelper.entry(this, "export", fnp);
		String currFilename = "not saved";

		if (datapool.getScanMode().isTransmissionMode()) {
			currFilename = exportS2P(fnp, overwrite);
		} else if (datapool.getScanMode().isReflectionMode()) {
			currFilename = exportS1P(fnp, overwrite);
		} else if (datapool.getScanMode().isRss1Mode()) {
			currFilename = exportS2P(fnp, overwrite);
		}
		TraceHelper.exit(this, "export");
		return currFilename;
	}

	/**
	 * @throws ProcessingException
	 * 
	 */
	private String exportS1P(String fnp, boolean overwrite) throws ProcessingException {
		final String methodName = "exportS1P";
		TraceHelper.entry(this, methodName);

		final VNACalibratedSampleBlock blk = datapool.getCalibratedData();
		final VNACalibratedSample[] samples = blk.getCalibratedSamples();
		String currFilename = check4FileToDelete(fnp, overwrite);
		if (currFilename != null) {
			//
			final DecimalFormatSymbols dfs = getDecimalFormatSymbols();
			final DecimalFormat fmtFrequency = new DecimalFormat("0", dfs);
			final DecimalFormat fmtLoss = new DecimalFormat("0.00000000", dfs);
			final DecimalFormat fmtPhase = new DecimalFormat("0.00000000", dfs);

			try (FileOutputStream fos = new FileOutputStream(currFilename); Writer w = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.ISO_8859_1))) {

				// write header
				w.write("! created by ");
				w.write(System.getProperty("user.name"));
				w.write(" at ");
				w.write(new Date().toString());
				w.write(GlobalSymbols.LINE_SEPARATOR);
				w.write("! generated using vna/J Version ");
				w.write(VNAMessages.getString("Application.version"));
				w.write(GlobalSymbols.LINE_SEPARATOR);

				final String resistance = "" + ((int) (datapool.getDriver().getDeviceInfoBlock().getReferenceResistance().getReal()));

				w.write("# Hz S DB R " + resistance);
				w.write(GlobalSymbols.LINE_SEPARATOR);

				// write data
				for (int i = 0; i < samples.length; ++i) {
					VNACalibratedSample data = samples[i];
					w.write(fmtFrequency.format(data.getFrequency()));
					w.write(" ");
					w.write(fmtLoss.format(data.getReflectionLoss()));
					w.write(" ");
					w.write(fmtPhase.format(data.getReflectionPhase()));
					w.write(GlobalSymbols.LINE_SEPARATOR);
				}
			} catch (IOException e) {
				ErrorLogHelper.exception(this, methodName, e);
				throw new ProcessingException(e);
			}
		}
		TraceHelper.exitWithRC(this, methodName, currFilename);
		return currFilename;
	}

	/**
	 * @return
	 */
	private DecimalFormatSymbols getDecimalFormatSymbols() {
		if (".".equals(config.getExportDecimalSeparator())) {
			return new DecimalFormatSymbols(Locale.ENGLISH);
		} else {
			return new DecimalFormatSymbols(Locale.GERMAN);
		}
	}

	/**
	 * @throws ProcessingException
	 * 
	 */
	private String exportS2P(String fnp, boolean overwrite) throws ProcessingException {
		final String methodName = "exportS2P";
		TraceHelper.entry(this, methodName);

		final VNACalibratedSampleBlock blk = datapool.getCalibratedData();
		final VNACalibratedSample[] samples = blk.getCalibratedSamples();

		String currFilename = check4FileToDelete(fnp, overwrite);
		if (currFilename != null) {
			//
			DecimalFormatSymbols dfs = getDecimalFormatSymbols();
			DecimalFormat fmtFrequency = new DecimalFormat("0", dfs);
			DecimalFormat fmtLoss = new DecimalFormat("0.00000000", dfs);
			DecimalFormat fmtPhase = new DecimalFormat("0.00000000", dfs);

			try (FileOutputStream fos = new FileOutputStream(currFilename); Writer w = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.ISO_8859_1))) {

				// write header
				w.write("! created by ");
				w.write(System.getProperty("user.name"));
				w.write(" at ");
				w.write(new Date().toString());
				w.write(GlobalSymbols.LINE_SEPARATOR);
				w.write("! generated using vna/J Version ");
				w.write(VNAMessages.getString("Application.version"));
				w.write(GlobalSymbols.LINE_SEPARATOR);
				w.write("# Hz S DB R 50");
				w.write(GlobalSymbols.LINE_SEPARATOR);

				// write data
				for (int i = 0; i < samples.length; ++i) {
					VNACalibratedSample data = samples[i];
					//
					w.write(fmtFrequency.format(data.getFrequency()));
					w.write(" ");
					//
					w.write(fmtLoss.format(data.getReflectionLoss()));
					w.write(" ");
					w.write(fmtPhase.format(data.getReflectionPhase()));
					w.write(" ");
					//
					w.write(fmtLoss.format(data.getTransmissionLoss()));
					w.write(" ");
					w.write(fmtPhase.format(data.getTransmissionPhase()));
					w.write(" ");
					//
					w.write(fmtLoss.format(0));
					w.write(" ");
					w.write(fmtPhase.format(0));
					w.write(" ");
					//
					w.write(fmtLoss.format(0));
					w.write(" ");
					w.write(fmtPhase.format(0));
					w.write(" ");

					w.write(GlobalSymbols.LINE_SEPARATOR);
				}
			} catch (IOException e) {
				ErrorLogHelper.exception(this, methodName, e);
				throw new ProcessingException(e);
			}
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
		if (datapool.getScanMode().isTransmissionMode()) {
			return ".s2p";
		} else if (datapool.getScanMode().isReflectionMode()) {
			return ".s1p";
		} else if (datapool.getScanMode().isRss1Mode()) {
			return ".s2p";
		} else {
			return ".xxx";
		}
	}
}
