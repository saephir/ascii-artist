package org.saephir.asciiartist.converter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import org.saephir.asciiartist.fonts.AsciiCharacter;
import org.saephir.asciiartist.fonts.AsciiCharacterMap;
import org.saephir.asciiartist.fonts.AsciiCharacterMetrics;
import org.saephir.asciiartist.fonts.AsciiFont;

/**
 * @author saephir
 */
public class AsciiArtConverter {

	private final AsciiCharacterMap charMap;
	
	private final AsciiFont font;
	
	private final AsciiArtConverterParameters params;
	
	public AsciiArtConverter(AsciiFont font, AsciiArtConverterParameters params) {
		this.font = font;
		this.params = params;
		charMap = new AsciiCharacterMap(font);
	}
	
	/**
	 * Returns the ASCII art in pure text format, with lines separated by \n character.
	 * @param image
	 * @return
	 */
	public String convertToAsciiArt(BufferedImage image) {
		
		String result = "";
		BufferedImage grayscaleVersion = convertToGrayscale(resizeToMatchProportions(scaleDown(image)));
		
		for(int i = 0; i < grayscaleVersion.getHeight(); i++) {
			for(int j = 0; j < grayscaleVersion.getWidth(); j++) {
				result += getBestCharacterFor(normalizeMatrix(getNeighborhoodMatrix(grayscaleVersion, j, i))).getCharacter();
			}
			result += "\n";
		}
		
		return result;
		
	}
	
	private int[][] getNeighborhoodMatrix(BufferedImage img, int centerX, int centerY) {
		
		int[][] matrix = new int[3][3];
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				int xCoord = centerX + i - 1;
				int yCoord = centerY + j - 1;
				if(xCoord < 0 || yCoord < 0 || xCoord >= img.getWidth() || yCoord >= img.getHeight()) {
					// uznajemy że zewnętrze obrazu jest białe
					matrix[i][j] = 255;
				} else {
					matrix[i][j] = extractValue(new Color(img.getRGB(xCoord, yCoord)));
				}
			}
		}
		
		return matrix;
				
	}
	
	private double[][] normalizeMatrix(int[][] matrix) {
		
		double[][] resultMatrix = new double[matrix.length][matrix[0].length];
		
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[i].length; j++) {
				resultMatrix[i][j] = matrix[i][j] / 255.0;
			}
		}
		
		return resultMatrix;
		
	}
	
	private int extractValue(Color color) {
		return color.getRed();
	}
	
	private double getHeightToWidthRatio() {
		return ((double) font.getCharacterHeight()) / font.getCharacterWidth();
	}
	
	private BufferedImage scaleDown(BufferedImage originalImage) {
		
		final int WIDTH_LIMIT = 300;
		final int HEIGHT_LIMIT = 500;
		double limitRatio = ((double) WIDTH_LIMIT) / HEIGHT_LIMIT;
		double imageRatio = ((double) originalImage.getWidth()) / originalImage.getHeight();
		
		if(imageRatio >= limitRatio) {
			double widthScaleDownFactor = ((double) originalImage.getWidth()) / WIDTH_LIMIT;
			int matchingHeight = (int) Math.round(originalImage.getHeight() / widthScaleDownFactor);
			return resizeImage(originalImage, WIDTH_LIMIT, matchingHeight);
		} else {
			double heightScaleDownFactor = ((double) originalImage.getHeight()) / HEIGHT_LIMIT;
			int matchingWidth = (int) Math.round(originalImage.getWidth() / heightScaleDownFactor);
			return resizeImage(originalImage, matchingWidth, HEIGHT_LIMIT);
		}
		
	}
	
	private BufferedImage resizeToMatchProportions(BufferedImage originalImage) {
		int newImageHeight = (int) (originalImage.getHeight() / getHeightToWidthRatio());
		return resizeImage(originalImage, originalImage.getWidth(), newImageHeight);
	}
	
	private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}
	
	private BufferedImage convertToGrayscale(BufferedImage image) {
		
		BufferedImage imageCopy = deepCopy(image);
		
		for(int i = 0; i < imageCopy.getWidth(); i++) {
			for(int j = 0; j < imageCopy.getHeight(); j++) {
				Color pixelColor = new Color(imageCopy.getRGB(i, j));
				int grayscaleValue = getGrayscaleValue(pixelColor);
				imageCopy.setRGB(i, j, new Color(grayscaleValue, grayscaleValue, grayscaleValue).getRGB());
			}
		}
		
		return imageCopy;
		
	}

	private int getGrayscaleValue(Color pixelColor) {
		switch(params.getGrayscaleConversionAlgorithm()) {
		case SIMPLE_AVERAGE:
			return (pixelColor.getRed() + pixelColor.getGreen() + pixelColor.getBlue()) / 3;
		case WEIGHTED_AVERAGE:
			return (77 * pixelColor.getRed() + 151 * pixelColor.getGreen() + 28 * pixelColor.getBlue()) / 256;
		default:
			throw new RuntimeException("Unknown average type!");
		}
	}
	
	private BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	private AsciiCharacter getBestCharacterFor(double[][] matrix) {
		
		AsciiCharacter bestCharacter = null;
		double bestDiff = 1e10;
		
		for(AsciiCharacter ch : charMap.values()) {

			double diff = calculatePixelToCharacterDiff(matrix, ch.getMetrics());
			
			if(diff < bestDiff) {
				bestDiff = diff;
				bestCharacter = ch;
			}
		}
		
		return bestCharacter;
		
	}
	
	private double calculatePixelToCharacterDiff(double[][] pixelNeighborhood, AsciiCharacterMetrics characterMetrics) {
		switch(params.getCharacterChoosingAlgorithm()) {
		case EXTENDED_ALGORITHM:
			return extendedAlgorithmDiff(pixelNeighborhood, characterMetrics);
		case SIMPLE_ALGORITHM:
			return simpleAlgorithmDiff(pixelNeighborhood, characterMetrics);
		default:
			throw new RuntimeException("Unknown algorithm type!");
		}
	}
	
	private double extendedAlgorithmDiff(double[][] pixelNeighborhood, AsciiCharacterMetrics characterMetrics) {
		
		double[][] characterWeights = characterMetrics.getWeights();
		double diff = 0.0;

		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				double partialDiff = pixelNeighborhood[i][j] - characterWeights[i][j];
				diff += partialDiff * partialDiff;
			}
		}

		diff = Math.sqrt(diff);
		return diff;
		
	}
	
	private double simpleAlgorithmDiff(double[][] pixelNeighborhood, AsciiCharacterMetrics characterMetrics) {
		double characterWeight = ((double) (characterMetrics.getArea() - characterMetrics.getNonWhitePixels())) / characterMetrics.getArea();
		return Math.abs(pixelNeighborhood[1][1] - characterWeight);
	}
	
}
