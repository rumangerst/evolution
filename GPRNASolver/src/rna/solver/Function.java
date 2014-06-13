package rna.solver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Klasse, die Funktionen behandelt
 * 
 * Es wird eine solche Klasse für die Hauptfunktion benötigt und verschiedene
 * Klassen für ADF
 * 
 * Jede Funktion hat zusätzlich zu dem unspezifischen Satz an Registerfunktionen
 * einen Satz an spezifischen Funktionen (nur Main-Funktion, um ADF aufrufen zu
 * können)
 * 
 * Jede Function hat eigene Mutations- und Rekombinationsmethoden.
 * 
 * @author ruman
 * 
 */
public class Function
{
	/**
	 * Terminale Funktionen ohne Parameter Statische Terminal-Funktionen, die
	 * IMMER verfügbar sind
	 */
	public static final String[] STATIC_TERMINAL_FUNCTIONS = new String[] {
			"COLLIDE_STRAIGHT", "COLLIDE_LEFT", "COLLIDE_RIGHT", "ENERGY",
			"ENERGY_OLD", "LENGTH", "STACK", "PREV", "NEXT", 
			"NORTH", "SOUTH", "EAST", "WEST", "NORTH_WEST", "NORTH_EAST",
			"SOUTH_WEST", "SOUTH_EAST", "RLEFT", "RRIGHT", "RSTRAIGHT", "A",
			"C", "U", "G", "LEFT", "RIGHT", "STRAIGHT"};

	/**
	 * Funktionen mit Parameter new RegisterFactory("",0) Statische Funktionen,
	 * die IMMER verfügbar sind
	 */
	public static final RegisterFactory[] STATIC_FUNCTIONS = new RegisterFactory[] {
			new RegisterFactory("IF_LESS", 4),
			new RegisterFactory("IF_GREATER", 4),
			new RegisterFactory("IF_EQUAL", 4), new RegisterFactory("ADD", 2),
			new RegisterFactory("SUBTRACT", 2),
			new RegisterFactory("MULTIPLY", 2),
			new RegisterFactory("DIVIDE", 2),
			new RegisterFactory("LOOK_BACK", 1),
			new RegisterFactory("LOOK_FORWARD", 1),
			new RegisterFactory("CALCULATE_ENERGY", 2),
			new RegisterFactory("GETBOND", 2),
			new RegisterFactory("PURINE", 1),
			new RegisterFactory("PYRIMIDINE", 1),
			// new RegisterFactory("PRG5", 5),
			new RegisterFactory("RETURN", 1),
			new RegisterFactory("RDIR", 1), //Wandelt Zahl in eine Richtung um
			new RegisterFactory("NUC", 1), //Wandelt Zahl in Nucleotid um
			
			new RegisterFactory("MIN", 5),
			new RegisterFactory("MAX", 5),
			new RegisterFactory("AVG", 5)
	/*
	 * new RegisterFactory("AND", 2), new RegisterFactory("OR", 2), new
	 * RegisterFactory("XOR", 2), new RegisterFactory("NOT", 1)
	 */};

	public ArrayList<Register> registers;
	public int parameterCount;

	public ArrayList<RegisterFactory> dynamic_Functions;
	public ArrayList<String> dynamic_TerminalFunctions;

	/**
	 * Befehlszeilenregister
	 */
	public int bzr;

	/**
	 * Speichert aktuell verwendete Parameter, die vom execute übergeben wurden
	 */
	public int[] parameters;

	/**
	 * Speichert aktuell verwendete ADF
	 */
	public ArrayList<Function> adfs;

	/*
	 * Das Ausgaberegister
	 */
	public int outputRegister;

	/**
	 * Generiert neue Funktionseinheit
	 * 
	 * @param parameterCount
	 *            Anzahl an parametern, die diese Funktion akzeptiert; Bei MAIN
	 *            0
	 * @param registerCount
	 *            Anzahl an Registern, die diese Funktion hat
	 * @param adfCount
	 *            Anzahl an zugreifbaren ADF; Nur für MAIN-Funktion!
	 */
	public Function(int parameterCount, int registerCount,
			ArrayList<Function> adfs)
	{
		this.parameterCount = parameterCount;
		this.dynamic_TerminalFunctions = new ArrayList<String>();
		this.dynamic_Functions = new ArrayList<RegisterFactory>();

		/**
		 * Generiere dynamische Terminalfunktionen für Parameter, etc.
		 */
		for (int i = 0; i < parameterCount; i++)
		{
			dynamic_TerminalFunctions.add("P" + i);
		}
		for (int i = 0; i < registerCount; i++)
		{
			dynamic_TerminalFunctions.add("R" + i);
		}

		/**
		 * nur SKIP und SKIP2 zulassen?
		 */

		for (int i = 1; i < registerCount / 2; i++)
		{
			dynamic_TerminalFunctions.add("SKIP" + i);
		}

//		/**
//		 * Lade Terminale mit Zahlen auf
//		 */
//		for (int i = -registerCount; i <= registerCount; i++)
//		{
//			dynamic_TerminalFunctions.add("" + i);
//		}

		if (adfs != null)
		{
			for (int i = 0; i < adfs.size(); i++)
			{
				dynamic_Functions.add(new RegisterFactory("ADF" + i, adfs
						.get(i).parameterCount));
			}
		}

		this.registers = new ArrayList<Register>();

		/**
		 * Generiere zufällige Register
		 */
		for (int i = 0; i < registerCount; i++)
		{
			registers.add(randomRegister());
		}

		this.outputRegister = registerCount - 1;
	}

	/**
	 * Kopiert die Funktion
	 * 
	 * @param toCopy
	 */
	public Function(Function toCopy)
	{
		this.parameterCount = toCopy.parameterCount;
		this.dynamic_Functions = toCopy.dynamic_Functions;
		this.dynamic_TerminalFunctions = toCopy.dynamic_TerminalFunctions;
		this.outputRegister = toCopy.outputRegister;

		this.registers = new ArrayList<Register>();
		for (Register reg : toCopy.registers)
		{
			this.registers.add(new Register(reg));
		}
	}

	/**
	 * Führt Funktion aus und gibt den Wert des letzten registers
	 * (Ausgaberegister) zurück
	 * 
	 * @param parameters
	 * @return
	 */
	public int execute(Individual individual, int[] parameters,
			ArrayList<Function> adfs)
	{
		this.parameters = parameters;
		this.adfs = adfs;

		bzr = 0;

		/**
		 * test: Wertespeicher vorher löschen
		 */
		for (Register reg : registers)
		{
			reg.value = 0;
		}

		while (bzr < registers.size())
		{
			registers.get(bzr).execute(individual, this);
			bzr++;
		}

		return registers.get(outputRegister).value;
	}

	/**
	 * Generiert ein zufälliges Register ALLE Funktionen und Terminalfunktionen
	 * sind gleichwahrscheinlich
	 * 
	 * @return
	 */
	public Register randomRegister()
	{
		int rand = Register.RANDOM.nextInt(STATIC_FUNCTIONS.length
				+ dynamic_Functions.size() + STATIC_TERMINAL_FUNCTIONS.length
				+ dynamic_TerminalFunctions.size());

		if (rand < STATIC_FUNCTIONS.length)
		{
			return STATIC_FUNCTIONS[rand].createRegister(this);
		}
		else if (rand < STATIC_FUNCTIONS.length + dynamic_Functions.size())
		{
			return dynamic_Functions.get(rand - STATIC_FUNCTIONS.length)
					.createRegister(this);
		}
		else
		{
			return new Register(randomTerminal());
		}
	}

	/**
	 * Gibt eine zufällige Terminalfunktion zurück Es wird nur das Label
	 * benötigt, da Terminalfunktion
	 * 
	 * @return
	 */
	public String randomTerminal()
	{
		String label;
		int rand = Register.RANDOM.nextInt(STATIC_TERMINAL_FUNCTIONS.length
				+ dynamic_TerminalFunctions.size());

		if (rand >= STATIC_TERMINAL_FUNCTIONS.length)
		{
			label = dynamic_TerminalFunctions.get(rand
					- STATIC_TERMINAL_FUNCTIONS.length);
		}
		else
		{
			label = STATIC_TERMINAL_FUNCTIONS[rand];
		}

		return label;
	}

	/**
	 * Return register with index, returns "0"-Register if not valid
	 * 
	 * @param index
	 * @return
	 */
	public Register getRegister(int index)
	{
		if (index < 0 || index >= registers.size())
			return new Register("0");

		return registers.get(index);
	}

	/**
	 * Two cases of mutation: I) Parameter mutation (mutate parameter of
	 * register) II) Register mutation (new random register)
	 * 
	 * @param p1
	 *            Register mutation - Probability to select the register *
	 */
	public void mutate(float p)
	{
		int regCount = this.registers.size();

		/**
		 * Parameter mutation replace parameters by random terminals
		 */
		for (int i = 0; i < registers.size(); i++)
		{
			Register reg = this.registers.get(i);

			if (1 - Register.RANDOM.nextFloat() <= p)
			{
				if (reg.parameters.length == 0)
				{
					/**
					 * Mutate whole register
					 */
					this.registers.set(i, randomRegister());
				}
				else
				{
					int parameter = Register.RANDOM
							.nextInt(reg.parameters.length + 1);

					if (parameter >= reg.parameters.length)
					{
						/**
						 * Mutate whole register
						 */
						this.registers.set(i, randomRegister());
					}
					else
					{
						/**
						 * Mutate parameter
						 */
						reg.parameters[parameter] = randomTerminal();
					}
				}
			}
		}

		/**
		 * Mutate output register
		 */
		// if(1 - Register.RANDOM.nextFloat() <= p)
		// {
		// this.outputRegister = Register.RANDOM.nextInt(registers.size());
		// }
	}

	/**
	 * Recombine using two-point X-Over
	 * 
	 * @param indiv1
	 * @param indiv2
	 * @param px
	 *            Recombination probability
	 */
	public static void recombine(Function indiv1, Function indiv2)
	{
		int reg = indiv1.registers.size();

		int index1 = Register.RANDOM.nextInt(reg);
		int index2 = Register.RANDOM.nextInt(reg);

		if (index1 == index2)
			return;
		if (index2 < index1)
		{
			int val1 = index1;
			int val2 = index2;
			index2 = val1;
			index1 = val2;
		}

		// X-Over
		for (int i = index1; i < index2; i++)
		{
			/**
			 * Swappe Register innerhalb in [index1, index2)
			 */
			Register r1 = indiv1.registers.get(i);
			Register r2 = indiv2.registers.get(i);

			indiv1.registers.set(i, r2);
			indiv2.registers.set(i, r1);
		}

	}

	// /**
	// * Recombine using one-point X-Over
	// *
	// * @param indiv1
	// * @param indiv2
	// * @param px
	// * Recombination probability
	// */
	// public static void recombine(Function indiv1, Function indiv2, float px)
	// {
	// int reg = indiv1.registers.size();
	//
	// if (1 - Register.RANDOM.nextFloat() <= px)
	// {
	// int index = Register.RANDOM.nextInt(reg);
	//
	// if (index == 0 || index == reg - 1)
	// return;
	//
	// // X-Over
	// for (int i = 0; i < index; i++)
	// {
	// indiv1.registers.set(i, indiv2.registers.get(i));
	// }
	// for (int i = index; i < reg; i++)
	// {
	// indiv2.registers.set(i, indiv1.registers.get(i));
	// }
	// }
	// }

	public String[] getRegisterCommands()
	{
		String[] output = new String[registers.size()];

		for (int i = 0; i < registers.size(); i++)
		{
			output[i] = registers.get(i).toString();
		}

		return output;
	}

	public void write(FileWriter wr, String name) throws IOException
	{
		wr.write(String.format("#%s %d %d\n", name, registers.size(),
				parameterCount));

		for (Register reg : registers)
		{
			wr.write(reg.label);

			for (int i = 0; i < reg.parameters.length; i++)
			{
				wr.write(" " + reg.parameters[i]);
			}

			wr.write("\n");
		}

	}
}
