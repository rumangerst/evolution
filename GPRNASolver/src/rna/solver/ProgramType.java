package rna.solver;

/**
 * Programmtyp
 * 
 * Wer von den Java-Erschaffern ist eigentlich auf die beschissene Idee
 * gekommen, Preprocessoren nicht einfügen zu wollen?! Jetzt muss ich SO eine
 * kacke machen! "Performance - ich nix wissen Performance" C# ist WIKLICH das
 * bessere Java, denn das KANN Preprocessoren #IF #ELSE #IFDEF ...
 * 
 * Mensch!!!!!
 * 
 * @author ruman
 * 
 */
public enum ProgramType
{
	/**
	 * Benutzt letzten Register (Ausgaberegister) der Main-Funktion, um
	 * PUT-Richtung zu generieren
	 */
	EFFECT,
	/**
	 * Benutzt PUT_x-Funktionen, um Nukleotide hinzuzufügen
	 */
	SIDEEFFECT
}
