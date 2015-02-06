package org.asciiartist.gui;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.saephir.asciiartist.converter.AsciiArtConverterParameters;
import org.saephir.asciiartist.converter.CharacterChoosingAlgorithm;
import org.saephir.asciiartist.converter.GrayscaleConversionAlgorithm;

/**
 * @author saephir
 */
public class AsciiArtistOptionsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1779850226198623580L;

	public AsciiArtistOptionsPanel() {
		super();
		initControls();
		isolateInRadioGroups();
		placeControls();
	}
	
	private void initControls() {
		simpleAverageOption = new JRadioButton("arithmetic average");
		weightedAverageOption = new JRadioButton("weighted average");
		simpleCharacterChooseOption = new JRadioButton("simple");
		advancedCharacterChooseOption = new JRadioButton("extended");
	}
	
	private void isolateInRadioGroups() {
		
		ButtonGroup averageGroup = new ButtonGroup();
		averageGroup.add(simpleAverageOption);
		averageGroup.add(weightedAverageOption);
		weightedAverageOption.setSelected(true);
		
		ButtonGroup algorithmGroup = new ButtonGroup();
		algorithmGroup.add(simpleCharacterChooseOption);
		algorithmGroup.add(advancedCharacterChooseOption);
		advancedCharacterChooseOption.setSelected(true);
		
	}
	
	private void placeControls() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JPanel averagePanel = new JPanel();
		averagePanel.setLayout(new BoxLayout(averagePanel, BoxLayout.Y_AXIS));
		averagePanel.setBorder(new TitledBorder("grayscale"));
		averagePanel.add(simpleAverageOption);
		averagePanel.add(weightedAverageOption);

		JPanel algorithmPanel = new JPanel();
		algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.Y_AXIS));
		algorithmPanel.setBorder(new TitledBorder("algorithm"));
		algorithmPanel.add(simpleCharacterChooseOption);
		algorithmPanel.add(advancedCharacterChooseOption);

		this.add(averagePanel);
		this.add(algorithmPanel);
	}
	
	public AsciiArtConverterParameters getSelectedParameters() {
		
		CharacterChoosingAlgorithm charChooseAlgo;
		
		if(simpleCharacterChooseOption.isSelected()) {
			charChooseAlgo = CharacterChoosingAlgorithm.SIMPLE_ALGORITHM;
		} else {
			charChooseAlgo = CharacterChoosingAlgorithm.EXTENDED_ALGORITHM;
		}
		
		GrayscaleConversionAlgorithm grayscaleAlgo;
		
		if(simpleAverageOption.isSelected()) {
			grayscaleAlgo = GrayscaleConversionAlgorithm.SIMPLE_AVERAGE;
		} else {
			grayscaleAlgo = GrayscaleConversionAlgorithm.WEIGHTED_AVERAGE;
		}
		
		return new AsciiArtConverterParameters(
				false,
				charChooseAlgo,
				grayscaleAlgo);
	}
	
	private JRadioButton simpleAverageOption;
	private JRadioButton weightedAverageOption;
	
	private JRadioButton simpleCharacterChooseOption;
	private JRadioButton advancedCharacterChooseOption;
	
}
