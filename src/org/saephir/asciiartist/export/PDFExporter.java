package org.saephir.asciiartist.export;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * @author saephir
 */
public class PDFExporter implements Exporter {

	@Override
	public void exportToFile(String asciiArt, OutputStream outputStream) throws IOException {
		// Create a document and add a page to it
		PDDocument document = new PDDocument();
		PDPage page = new PDPage();
		document.addPage(page);

		// Create a new font object selecting one of the PDF base fonts
		PDFont font = PDType1Font.COURIER;

		// Start a new content stream which will "hold" the to be created content
		PDPageContentStream contentStream = new PDPageContentStream(document, page);

		// Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"

		int verticalOffset = 700;

		String[] asciiArtSplit = asciiArt.split("\r?\n");

		for(String line : asciiArtSplit) {
			contentStream.beginText();
			contentStream.setFont(font, 2);
			contentStream.moveTextPositionByAmount(100, verticalOffset);
			contentStream.drawString(line);
			contentStream.endText();
			verticalOffset -= 2;
		}

		// Make sure that the content stream is closed:
		contentStream.close();

		// Save the results and ensure that the document is properly closed:
		try {
			document.save(outputStream);
		} catch (COSVisitorException e) {
			throw new RuntimeException(e);
		}
		
		document.close();
	}
}
