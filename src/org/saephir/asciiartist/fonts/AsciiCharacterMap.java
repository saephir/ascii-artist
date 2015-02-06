package org.saephir.asciiartist.fonts;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.TreeMap;

/**
 * @author saephir
 */
public class AsciiCharacterMap extends TreeMap<Character, AsciiCharacter> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6411690971259980575L;
	
	private final Charset asciiCharset = Charset.forName("iso8859-1");

	public AsciiCharacterMap(AsciiFont font) {
		super();
		// zestaw znaków ASCII
		for(byte i = 32; i <= 126; i++) {
			char asciiCharacter = asciiCodeToChar(i);
			this.put((char) i, font.getCharacter(asciiCharacter));
		}
	}

	private char asciiCodeToChar(byte asciiCode) {
		ByteBuffer buf = ByteBuffer.wrap(new byte[] { asciiCode });
		return asciiCharset.decode(buf).get();
	}
	
}
