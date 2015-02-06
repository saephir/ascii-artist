package org.saephir.asciiartist.fonts;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * @author saephir
 */
public class AsciiCharacter {

	private final char ch;
	
	private final BufferedImage renderedCharacter;
	
	private final AsciiCharacterMetrics metrics;
	
	public AsciiCharacter(char ch, Font font) {
		this.ch = ch;
		renderedCharacter = createCharacterImage(ch, font);
		paintChar(renderedCharacter.getGraphics(), font);
		metrics = calculateMetrics();
	}
	
	private BufferedImage createCharacterImage(char ch, Font font) {
		JComponent dummyComponent = new JComponent() {
			private static final long serialVersionUID = -5225577902725895031L;
		};
		FontMetrics fontMetrics = dummyComponent.getFontMetrics(font);
		Rectangle2D chRect = fontMetrics.getStringBounds(String.valueOf(ch), dummyComponent.getGraphics());
		return new BufferedImage(
				(int) Math.round(chRect.getWidth()),
				fontMetrics.getHeight(),
				BufferedImage.TYPE_INT_RGB);
	}
	
	private void paintChar(Graphics imageGraphics, Font font) {
		Graphics2D g2d = (Graphics2D) imageGraphics;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		imageGraphics.setFont(font);
		imageGraphics.setColor(Color.white);
		//int offset = renderedCharacter.getWidth() / 2 - fontMetrics.charWidth(ch) / 2;
		imageGraphics.fillRect(0, 0, renderedCharacter.getWidth(), renderedCharacter.getHeight());
		imageGraphics.setColor(Color.black);
		imageGraphics.drawString(String.valueOf(ch), 0, font.getSize());
	}
	
	public char getCharacter() {
		return ch;
	}
	
	public BufferedImage getRenderedCharacter() {
		return renderedCharacter;
	}
	
	public AsciiCharacterMetrics getMetrics() {
		return metrics;
	}
	
	private AsciiCharacterMetrics calculateMetrics() {
		return new AsciiCharacterMetrics(
				renderedCharacter.getWidth(),
				renderedCharacter.getHeight(),
				calculateBlackPixels(),
				calculateWeights());
	}
	
	private int calculateBlackPixels() {
		int nonWhitePixels = 0;
		for(int i = 0; i < renderedCharacter.getWidth(); i++) {
			for(int j = 0; j < renderedCharacter.getHeight(); j++) {
				if(renderedCharacter.getRGB(i, j) != Color.white.getRGB()) {
					nonWhitePixels++;
				}
			}
		}
		return nonWhitePixels;
	}
	
	private double[][] calculateWeights() {
		
		double[][] weights = new double[3][3];
		
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				weights[i][j] = calculateWeight(i, j);
			}
		}
		
		return weights;
		
	}
	
	private double calculateWeight(int x, int y) {
		
		int horizontalPixels[] = splitMod3(renderedCharacter.getWidth());
		int verticalPixels[] = splitMod3(renderedCharacter.getHeight());
		
		int startX = 0;
		int startY = 0;
		
		for(int i = 0; i < x; i++) {
			startX += horizontalPixels[i];
		}
		
		for(int j = 0; j < y; j++) {
			startY += verticalPixels[j];
		}
		
		int endX = startX + horizontalPixels[x];
		int endY = startY + verticalPixels[y];
		
		int nonWhitePixels = 0;
		
		for(int i = startX; i < endX; i++) {
			for(int j = startY; j < endY; j++) {
				if((renderedCharacter.getRGB(i, j) & 0xffffff) == 0xffffff) {
					nonWhitePixels++;
				}
			}
		}
		
		int maxNonWhitePixels = horizontalPixels[x] * verticalPixels[y];
		
		return ((double) nonWhitePixels) / maxNonWhitePixels;
		
	}
	
	int[] splitMod3(int number) {
		switch(number % 3) {
		case 0:
			return new int[] { number / 3, number / 3, number / 3 };
		case 1:
			return new int[] { (number - 1) / 3, (number - 1) / 3 + 1, (number - 1) / 3 };
		case 2:
			return new int[] { (number - 2) / 3 + 1, (number - 2) / 3, (number - 2) / 3 + 1 };
		default:
			throw new RuntimeException("WTF error");
		}
	}

}
