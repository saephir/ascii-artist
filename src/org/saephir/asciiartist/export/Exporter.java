package org.saephir.asciiartist.export;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author saephir
 */
public interface Exporter {
	/**
	 * Export the ASCII art given by string to the output stream.
	 * @param asciiArt
	 * @param outputStream
	 * @throws IOException
	 */
	public void exportToFile(String asciiArt, OutputStream outputStream) throws IOException;
}
