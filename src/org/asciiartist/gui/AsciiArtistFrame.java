package org.asciiartist.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.saephir.asciiartist.converter.AsciiArtConverter;
import org.saephir.asciiartist.export.Exporter;
import org.saephir.asciiartist.export.PDFExporter;
import org.saephir.asciiartist.fonts.AsciiFont;

/**
 * @author saephir
 */
public class AsciiArtistFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2899227684738786870L;

	public AsciiArtistFrame() {
		setTitle("Ascii Artist");
		setSize(480, 320);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initControls();
		setupEvents();
		placeControls();
	}

	private void initControls() {

		titleLabel = new JLabel("Ascii Artist");
		titleLabel.setFont(titleLabel.getFont().deriveFont(32.0f));
		titleLabel.setHorizontalAlignment(JLabel.CENTER);

		authorLabel = new JLabel("Dominik Jastrzębski ©");
		authorLabel.setHorizontalAlignment(SwingConstants.CENTER);

		loadImageButton = new JButton("load an image");
		fileNameLabel = new JLabel("waiting for an image...");

		saveToPDFButton = new JButton("save and open PDF");
		saveToPDFButton.setEnabled(false);

		optionsPanel = new AsciiArtistOptionsPanel();

	}

	private void setupEvents() {
		loadImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					loadImage();
				} catch (IOException exc) {
					JOptionPane.showMessageDialog(AsciiArtistFrame.this,
							"Failed to load the file!", "Błąd", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		saveToPDFButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveToPDF();
			}
		});
	}

	private void placeControls() {

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		titlePanel.add(titleLabel);
		titlePanel.add(authorLabel, BorderLayout.SOUTH);

		getContentPane().add(titlePanel, BorderLayout.NORTH);

		JPanel mainPanel = new JPanel();

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel buttonsPanel = new JPanel();

		buttonsPanel.add(loadImageButton);
		buttonsPanel.add(saveToPDFButton);

		mainPanel.add(new JSeparator());
		mainPanel.add(buttonsPanel);

		JPanel labelPanel = new JPanel();
		labelPanel.add(fileNameLabel);
		mainPanel.add(labelPanel);

		mainPanel.add(optionsPanel);

		getContentPane().add(mainPanel);
	}

	private void loadImage() throws IOException {
		int selectionResult = fileChooser.showOpenDialog(this);
		if (selectionResult == JFileChooser.APPROVE_OPTION) {
			currentlyOpenedImage = ImageIO.read(fileChooser.getSelectedFile());
			String absolutePath = fileChooser.getSelectedFile().getAbsolutePath();
			pdfExportName = getPDFNameForImageName(absolutePath);
			fileNameLabel.setText(absolutePath);
			saveToPDFButton.setEnabled(true);
		}
	}

	private void saveToPDF() {

		if (currentlyOpenedImage == null) {
			throw new RuntimeException("Tried to save the PDF before loading an image!");
		}

		AsciiArtConverter converter = new AsciiArtConverter(asciiFont,
				optionsPanel.getSelectedParameters());
		String asciiArt = converter.convertToAsciiArt(currentlyOpenedImage);

		try {
			FileOutputStream outputStream = new FileOutputStream(pdfExportName);
			asciiExporter.exportToFile(asciiArt, outputStream);
			outputStream.flush();
			outputStream.close();
			openPDF(new File(pdfExportName));
		} catch (IOException exc) {
			JOptionPane.showMessageDialog(AsciiArtistFrame.this, "Failed to load the PDF file!",
					"Błąd", JOptionPane.ERROR_MESSAGE);
		}

	}

	private void openPDF(File exportedPDF) {
		try {
			Desktop.getDesktop().open(exportedPDF);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(AsciiArtistFrame.this, "Failed to load the PDF file!",
					"Błąd", JOptionPane.ERROR_MESSAGE);
		}
	}

	private String getPDFNameForImageName(String imagePath) {
		Matcher regexMatcher = FILE_NAME_WITHOUT_EXTENSION_REGEX.matcher(imagePath);
		if (regexMatcher.matches()) {
			return regexMatcher.group(1) + ".pdf";
		} else {
			return imagePath + ".pdf";
		}
	}

	private JFileChooser fileChooser = new JFileChooser();

	private final AsciiFont asciiFont = new AsciiFont("Courier New", 18);
	private final Exporter asciiExporter = new PDFExporter();

	private JLabel titleLabel;
	private JLabel authorLabel;
	private JButton loadImageButton;
	private JLabel fileNameLabel;
	private JButton saveToPDFButton;

	private AsciiArtistOptionsPanel optionsPanel;

	private BufferedImage currentlyOpenedImage;
	private String pdfExportName;

	private final static Pattern FILE_NAME_WITHOUT_EXTENSION_REGEX = Pattern.compile("(.+)\\..+$");

}
