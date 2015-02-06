package org.saephir.asciiartist.converter;

/**
 * @author saephir
 */
public class AsciiArtConverterParameters {

	public AsciiArtConverterParameters(
			boolean colorful,
			CharacterChoosingAlgorithm characterChoosingAlgorithm,
			GrayscaleConversionAlgorithm grayscaleConversionAlgorithm)
	{
		this.colorful = colorful;
		this.characterChoosingAlgorithm = characterChoosingAlgorithm;
		this.grayscaleConversionAlgorithm = grayscaleConversionAlgorithm;
	}
	
	public boolean isColorful() {
		return colorful;
	}
	
	public CharacterChoosingAlgorithm getCharacterChoosingAlgorithm() {
		return characterChoosingAlgorithm;
	}
	
	public GrayscaleConversionAlgorithm getGrayscaleConversionAlgorithm() {
		return grayscaleConversionAlgorithm;
	}
	
	private final CharacterChoosingAlgorithm characterChoosingAlgorithm;
	private final GrayscaleConversionAlgorithm grayscaleConversionAlgorithm;
	private final boolean colorful;
	
}
