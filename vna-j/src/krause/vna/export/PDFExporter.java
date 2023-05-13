/**
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 */
package krause.vna.export;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;

import org.jfree.chart.JFreeChart;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAScanMode;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.resources.VNAMessages;

public class PDFExporter extends VNAExporter {

	private static final int IMAGE_WIDTH = 2000;

	private static final int IMAGE_HEIGHT = 1500;

	private final Font FONT_CELL_VALUE;
	private final Font FONT_CELL_HEADER;
	private final Font FONT_HEADER;
	private final Font FONT_FOOTER;
	private final Font FONT_MARKER;
	private final Font FONT_TITLE;
	private final Font FONT_COMMENT;

	public PDFExporter(VNAMainFrame mainFrame) {
		super(mainFrame);
		if (Locale.getDefault().getCountry().equals(Locale.JAPAN.getCountry())) {
			FONT_CELL_HEADER = FontFactory.getFont(FontFactory.TIMES, 9, Font.BOLD);
			FONT_CELL_VALUE = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
			FONT_HEADER = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
			FONT_FOOTER = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);
			FONT_MARKER = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);
			FONT_TITLE = new Font(Font.TIMES_ROMAN, config.getExportTitleFontSize(), Font.BOLD);
			FONT_COMMENT = FontFactory.getFont("MS UI Gothic", 10, Font.NORMAL);
		} else {
			FONT_CELL_HEADER = FontFactory.getFont(FontFactory.COURIER, 9, Font.BOLD);
			FONT_CELL_VALUE = new Font(Font.COURIER, 9, Font.NORMAL);
			FONT_HEADER = new Font(Font.HELVETICA, 8, Font.NORMAL);
			FONT_FOOTER = new Font(Font.HELVETICA, 8, Font.NORMAL);
			FONT_MARKER = new Font(Font.COURIER, 10, Font.BOLD);
			FONT_TITLE = new Font(Font.HELVETICA, config.getExportTitleFontSize(), Font.BOLD);
			FONT_COMMENT = new Font(Font.COURIER, 10, Font.NORMAL);
		}
	}

	private void createMarkerTable(Document doc) throws DocumentException {
		VNAMarkerPanel mp = mainFrame.getMarkerPanel();
		VNAMarker[] markers = mp.getMarkers();

		final float[] COL_WIDTH = {
				40f,
				80f,
				50f,
				50f,
				50f,
				50f,
				50f,
				50f,
				50f
		};

		PdfPTable table = new PdfPTable(COL_WIDTH.length);

		table.setTotalWidth(COL_WIDTH);
		table.setLockedWidth(true);

		createMarkerTableHeader(table);

		for (VNAMarker marker : markers) {
			if (marker.isVisible()) {
				createMarkerTableRow(marker, table, false);
				if ("2".equals(marker.getShortName())) {
					if (mp.getDeltaMarker().getSample() != null) {
						createMarkerTableRow(mp.getDeltaMarker(), table, true);
					}
				}
			}
		}

		Paragraph p = new Paragraph();
		p.setSpacingBefore(10);
		p.setSpacingAfter(10);
		p.add(table);
		doc.add(p);
	}

	private void createMarkerTableHeader(PdfPTable table) {
		table.addCell(createLeftHeaderCell(VNAMessages.getString("Marker")));
		table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.Frequency")));
		if (VNAScanMode.MODE_REFLECTION.equals(datapool.getScanMode())) {
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.RL")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.PhaseRL")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.Z")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.R")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.X")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.Theta")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.SWR")));
		} else {
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.TL")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.PhaseTL")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.Z")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.R")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.X")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.Theta")));
			table.addCell(createCenteredHeaderCell(VNAMessages.getString("Marker.GrpDelay")));
		}
	}

	/**
	 * @param string
	 * @return
	 */
	private PdfPCell createLeftHeaderCell(String text) {
		Chunk c = new Chunk(text, FONT_CELL_HEADER);
		Paragraph p = new Paragraph(c);
		PdfPCell cell = new PdfPCell(p);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		return cell;
	}

	private PdfPCell createValueCell(String text) {
		Chunk c = new Chunk(text, FONT_CELL_VALUE);
		Paragraph p = new Paragraph(c);
		PdfPCell cell = new PdfPCell(p);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		return cell;
	}

	private PdfPCell createCenteredHeaderCell(String text) {
		Chunk c = new Chunk(text, FONT_CELL_HEADER);
		Paragraph p = new Paragraph(c);
		PdfPCell cell = new PdfPCell(p);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		return cell;
	}

	/**
	 * 
	 * @param marker
	 * @param table
	 * @param isDeltaMarker
	 */
	private void createMarkerTableRow(VNAMarker marker, PdfPTable table, boolean isDeltaMarker) {
		if (isDeltaMarker) {
			table.addCell(createMarkerNameCell("1-2"));
			table.addCell(createValueCell(marker.getTxtFrequency().getText()));
			table.addCell(createValueCell(marker.getTxtLoss().getText()));
			table.addCell(createValueCell(marker.getTxtPhase().getText()));
			table.addCell(createValueCell(marker.getTxtZAbsolute().getText()));
			table.addCell(createValueCell(marker.getTxtRs().getText()));
			table.addCell(createValueCell(marker.getTxtXsAbsolute().getText()));
			table.addCell(createValueCell(marker.getTxtTheta().getText()));
			table.addCell(createValueCell("---"));
		} else {
			table.addCell(createMarkerNameCell(marker.getName()));
			table.addCell(createValueCell(marker.getTxtFrequency().getText()));
			table.addCell(createValueCell(marker.getTxtLoss().getText()));
			table.addCell(createValueCell(marker.getTxtPhase().getText()));
			table.addCell(createValueCell(marker.getTxtZAbsolute().getText()));
			table.addCell(createValueCell(marker.getTxtRs().getText()));
			table.addCell(createValueCell(marker.getTxtXsAbsolute().getText()));
			table.addCell(createValueCell(marker.getTxtTheta().getText()));
			table.addCell(createValueCell(marker.getTxtSwrGrpDelay().getText()));
		}
	}

	private PdfPCell createMarkerNameCell(String text) {
		Chunk c = new Chunk(text, FONT_MARKER);
		Paragraph p = new Paragraph(c);
		PdfPCell cell = new PdfPCell(p);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		return cell;
	}

	/**
	 * 
	 * @param filename
	 * @return
	 * @throws ProcessingException
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	private Document createDocument(String filename) throws ProcessingException, FileNotFoundException, DocumentException {
		Document document = new Document(PageSize.A4);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
		PdfPageEventHelper pageHandler = new PdfPageEventHelper() {
			@Override
			public void onEndPage(PdfWriter writer, Document document) {
				Rectangle page = document.getPageSize();

				PdfPTable table = new PdfPTable(3);
				PdfPCell cell;

				cell = new PdfPCell(new Paragraph(VNAMessages.getString("Application.copyright"), FONT_FOOTER));
				cell.setBorder(Rectangle.TOP);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(MessageFormat.format(VNAMessages.getString("Application.header"), VNAMessages.getString("Application.version")), FONT_FOOTER));
				cell.setBorder(Rectangle.TOP);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);

				cell = new PdfPCell(new Paragraph(VNAMessages.getString("Application.URL"), FONT_FOOTER));
				cell.setBorder(Rectangle.TOP);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(cell);

				table.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
				table.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), writer.getDirectContent());
			}

			@Override
			public void onStartPage(PdfWriter writer, Document document) {
				Rectangle page = document.getPageSize();
				PdfPTable table = new PdfPTable(1);

				PdfPCell cell;

				Object[] parms = new Object[] {
						new Date(),
						System.getProperty("user.name")
				};
				String title = MessageFormat.format(VNAMessages.getString("Export.PDF.Title"), parms);
				cell = new PdfPCell(new Paragraph(title, FONT_HEADER));
				cell.setBorder(Rectangle.BOTTOM);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);

				table.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
				table.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - document.topMargin() + table.getTotalHeight(), writer.getDirectContent());
			}
		};

		writer.setPageEvent(pageHandler);

		document.open();
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.export.VNAExporter#export(krause.vna.gui.VNAMainFrame)
	 */
	public String export(String fnp, boolean overwrite) throws ProcessingException {
		final String methodName = "export";
		TraceHelper.entry(this, methodName);
		String currFilename = null;
		VNACalibratedSampleBlock blk = datapool.getCalibratedData();
		VNACalibratedSample[] pDataList = blk.getCalibratedSamples();
		try {
			currFilename = check4FileToDelete(fnp, overwrite);
			if (currFilename != null) {
				// create the chart
				JFreeChart chart = createChart(pDataList);
				chart.setTitle("");
				Document doc = createDocument(currFilename);
				createTitle(doc);
				createImage(chart, doc);
				createMarkerTable(doc);
				createComment(doc);
				doc.close();
			}
		} catch (Exception e) {
			ErrorLogHelper.exception(this, methodName, e);
			throw new ProcessingException(e);
		}
		TraceHelper.entry(this, methodName);
		return currFilename;
	}

	private void createTitle(Document doc) throws DocumentException {
		Paragraph p;
		Chunk c;
		p = new Paragraph();
		p.setSpacingAfter(10);
		c = new Chunk("");
		p.add(c);
		doc.add(p);

		c = new Chunk(replaceParameters(config.getExportTitle()), FONT_TITLE);
		p = new Paragraph();
		p.setSpacingBefore(15);
		p.setAlignment(Element.ALIGN_CENTER);
		p.setSpacingAfter(5);
		p.add(c);
		doc.add(p);
	}

	private void createImage(JFreeChart chart, Document doc) throws IOException, DocumentException {
		Image awtImg = chart.createBufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT);
		com.lowagie.text.Image itImg = com.lowagie.text.Image.getInstance(awtImg, null);
		float w = PageSize.A4.getWidth() - 100.0f;
		float h = w / 1.33f;
		itImg.scaleAbsolute(w, h);
		itImg.setAlignment(com.lowagie.text.Image.MIDDLE);
		Paragraph p = new Paragraph();
		doc.add(new Paragraph(" "));
		p.add(itImg);
		doc.add(p);
	}

	private void createComment(Document doc) throws DocumentException {
		Paragraph p;
		Chunk c;
		p = new Paragraph();
		p.setSpacingAfter(10);
		c = new Chunk(VNAMessages.getString("Export.PDF.Comment"));
		c.setUnderline(0.2f, -2f);
		p.add(c);
		doc.add(p);

		c = new Chunk(replaceParameters(config.getExportComment()), FONT_COMMENT);
		p = new Paragraph();
		p.setIndentationLeft(10);
		p.setIndentationRight(10);
		p.setSpacingAfter(10);
		p.add(c);
		doc.add(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.vna.export.VNAExporter#getExtension()
	 */
	@Override
	public String getExtension() {
		return ".pdf";
	}
}
