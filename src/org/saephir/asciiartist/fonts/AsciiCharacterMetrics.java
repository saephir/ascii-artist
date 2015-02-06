package org.saephir.asciiartist.fonts;

/**
 * @author saephir
 */
public class AsciiCharacterMetrics {
	
	private final int width;
	
	private final int height;
	
	private final int nonWhitePixels;
	
	private final double[][] weights;
	
	public AsciiCharacterMetrics(int width, int height, int nonWhitePixels, double[][] weights) {
		this.width = width;
		this.height = height;
		this.nonWhitePixels = nonWhitePixels;
		this.weights = weights;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getArea() {
		return width * height;
	}
	
	public int getNonWhitePixels() {
		return nonWhitePixels;
	}

	public double[][] getWeights() {
		return weights;
	}

}
