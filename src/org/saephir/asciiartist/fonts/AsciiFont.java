package org.saephir.asciiartist.fonts;

import java.awt.Font;

/**
 * @author saephir
 */
public class AsciiFont {
	
	private final Font font;

	private final AsciiCharacter sampleCharacter;
	
	public AsciiFont(String typeface, int size) {
		font = new Font(typeface, Font.PLAIN, size);
		sampleCharacter = getCharacter('a');
	}
	
	public AsciiCharacter getCharacter(char ch) {
		return new AsciiCharacter(ch, font);
	}
	
	public int getCharacterWidth() {
		return sampleCharacter.getMetrics().getWidth();
	}
	
	public int getCharacterHeight() {
		return sampleCharacter.getMetrics().getHeight();
	}

}
