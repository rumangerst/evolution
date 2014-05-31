package rna.solver;

import java.util.Random;

/**
 * Register class, returns a value
 * 
 * Every register has LABEL and PARAMETERS
 * 
 * 
 * 
 * @author ruman
 * 
 */
public class Register
{
	public static final Random RANDOM = new Random();
	/**
	 * Terminale Funktionen ohne Parameter
	 */
	public static final String[] TERMINAL_FUNCTIONS = new String[] { "NUMBER",
			"REGISTER_VALUE", "COLLIDE_STRAIGHT", "COLLIDE_LEFT",
			"COLLIDE_RIGHT", "ENERGY", "LENGTH", "STACK", "PREV", "NEXT",
			"SKIP", "SKIP%", "LEFT", "RIGHT", "STRAIGHT", "ROUT" }; // Umstellung
	// auf
	// Effect-Model:
	// Effekt
	// ist
	// Rückgabe
	// vom
	// höchsten
	// Register,
	// kein
	// Seiteneffekt
	// von PUT_x
	// UNDO entfernt

	/**
	 * Funktionen für Variablenabfragen ...
	 */
	public static final String[] FUNCTIONS = new String[] { "TERMINAL",
			"IF_LESS", "IF_GREATER", "IF_EQUALS", "ADD", "SUBTRACT",
			"MULTIPLY", "DIVIDE", "LOOK_BACK", "LOOK_FORWARD",
			"CALCULATE_ENERGY", "GETBOND", "PURINE", "PYRIMIDINE", "SUM",
			"PRG5", "RETURN" }; // UPDATE_BONDING entfernt

	public String label;
	public String[] parameters;

	/**
	 * Current value of this register
	 */
	public int value;

	public Register(String label, String... parameters)
	{
		this.label = label;
		this.parameters = parameters;

		this.value = 0;
	}

	public Register(Register tocopy)
	{
		this.label = tocopy.label;
		this.parameters = tocopy.parameters.clone();
		this.value = 0;

		// Hinweis: value nicht kopieren!
		// Nur label und Parameter sind wichtig!
	}

	/**
	 * Executes a given parameter string
	 * 
	 * Interpreter
	 * 
	 * @param param
	 * @return
	 */
	public int executeTerminal(Individual individual, String label)
	{
		if (label.length() == 0)
			return 0;

		// /**
		// * PUT_X functions
		// */
		// if (label.equals("PUT_STRAIGHT"))
		// {
		// return individual.put(RelativeDirection.STRAIGHT);
		// }
		// if (label.equals("PUT_LEFT"))
		// {
		// return individual.put(RelativeDirection.LEFT);
		//
		// }
		// if (label.equals("PUT_RIGHT"))
		// {
		// return individual.put(RelativeDirection.RIGHT);
		//
		// }

		/**
		 * COLLIDE_X functions
		 */
		if (label.equals("COLLIDE_STRAIGHT"))
		{
			return individual.checkCollision(RelativeDirection.STRAIGHT);

		}
		if (label.equals("COLLIDE_LEFT"))
		{
			return individual.checkCollision(RelativeDirection.LEFT);
		}
		if (label.equals("COLLIDE_RIGHT"))
		{
			return individual.checkCollision(RelativeDirection.RIGHT);

		}

		/**
		 * UNDO
		 */
		if (label.equals("UNDO"))
		{
			return individual.undoPut();

		}

		/**
		 * ENERGY current energy level
		 */
		if (label.equals("ENERGY"))
		{
			return individual.structure.energy();

		}

		/**
		 * LENGTH - current structure length
		 */
		if (label.equals("LENGTH"))
		{
			return individual.structure.structureLength;

		}

		/**
		 * STACK - nucleotides to put
		 */
		if (label.equals("STACK"))
		{
			return individual.sequence.size();

		}

		/**
		 * A,C,U,G
		 */
		/*
		 * if (label.equals("A")) { return 0; } if (label.equals("C")) { return
		 * 1; } if (label.equals("U")) { return 2; } if (label.equals("G")) {
		 * return 3; }
		 */

		/**
		 * PREV (Zuletzt hinzugefügter) NEXT (neu zu hinzufügendes Nuc)
		 */
		if (label.equals("PREV"))
		{
			return individual.structure.current.type.toInteger();
		}
		if (label.equals("NEXT"))
		{
			if (individual.sequence.isEmpty())
				return -1;
			return individual.sequence.get(0);
		}

		/**
		 * Indicate that next register will be skipped
		 */
		if (label.equals("SKIP"))
		{
			individual.bzr++;
			return 0;
		}

		/**
		 * Konstaten für LINKS, RECHTS und GERADEAUS
		 */
		if (label.equals("LEFT"))
		{
			return -1;
		}
		if (label.equals("STRAIGHT"))
		{
			return 0;
		}
		if (label.equals("RIGHT"))
		{
			return 1;
		}

		/**
		 * ROUT, Pointer auf höchsten Register
		 */
		if (label.equals("ROUT"))
		{
			label = "R" + (individual.registers.size() - 1);
		}

		/**
		 * No known label => must be a number or a Rx reference
		 */
		if (label.startsWith("R"))
		{
			int id = Integer.parseInt(label.substring(1));

			if (id < 0 || id > individual.registers.size())
			{
				return -1;
			}

			return individual.registers.get(id).value;
		}
		else if (label.startsWith("SKIP")) //SKIPi-Funktion
		{
			int id = Integer.parseInt(label.substring(4));

			if (id < 0)
			{
				return -1;
			}
			
			individual.bzr+=id;

			return 0;
		}
		else
		{
			return Integer.parseInt(label);
		}
	}

	/**
	 * Execute register and store output to value
	 * 
	 * Interpreter
	 * 
	 * @return
	 */
	public void execute(Individual individual)
	{
		if (label.equals("IF_GREATER"))
		{
			int r1 = executeTerminal(individual, parameters[0]);
			int r2 = executeTerminal(individual, parameters[1]);

			if (r1 > r2)
			{
				value = executeTerminal(individual, parameters[2]);
			}
			else
			{
				value = executeTerminal(individual, parameters[3]);
			}

			return;

		}
		if (label.equals("IF_LESS"))
		{
			int r1 = executeTerminal(individual, parameters[0]);
			int r2 = executeTerminal(individual, parameters[1]);

			if (r1 < r2)
			{
				value = executeTerminal(individual, parameters[2]);
			}
			else
			{
				value = executeTerminal(individual, parameters[3]);
			}

			return;

		}
		if (label.equals("IF_EQUAL"))
		{
			int r1 = executeTerminal(individual, parameters[0]);
			int r2 = executeTerminal(individual, parameters[1]);

			if (r1 == r2)
			{
				value = executeTerminal(individual, parameters[2]);
			}
			else
			{
				value = executeTerminal(individual, parameters[3]);
			}

			return;

		}

		if (label.equals("ADD"))
		{
			int r1 = executeTerminal(individual, parameters[0]);
			int r2 = executeTerminal(individual, parameters[1]);

			value = r1 + r2;

			return;
		}

		if (label.equals("SUBTRACT"))
		{
			int r1 = executeTerminal(individual, parameters[0]);
			int r2 = executeTerminal(individual, parameters[1]);

			value = r1 - r2;

			return;
		}

		if (label.equals("MULTIPLY"))
		{
			int r1 = executeTerminal(individual, parameters[0]);
			int r2 = executeTerminal(individual, parameters[1]);

			value = r1 * r2;

			return;
		}

		if (label.equals("DIVIDE"))
		{
			int r1 = executeTerminal(individual, parameters[0]);
			int r2 = executeTerminal(individual, parameters[1]);

			if (r2 == 0)
			{
				value = 0;
				return;
			}

			value = r1 / r2;

			return;
		}

		if (label.equals("LOOK_FORWARD"))
		{
			int r1 = executeTerminal(individual, parameters[0]);

			if (r1 < 0 || r1 >= individual.sequence.size())
				value = -1;
			else
				value = individual.sequence.get(r1);

			return;
		}

		if (label.equals("LOOK_BACK"))
		{
			int r1 = executeTerminal(individual, parameters[0]);

			if (r1 < 0 || r1 >= individual.structure.structureLength)
				value = -1;
			else
			{
				Nucleotide current = individual.structure.current;

				for (int i = 0; i < r1; i++)
				{
					current = current.previous;
				}

				value = current.type.toInteger();
			}

			return;
		}

		if (label.equals("CALCULATE_ENERGY"))
		{
			int r1 = executeTerminal(individual, parameters[0]);
			int r2 = executeTerminal(individual, parameters[1]);

			NucleotideType type1 = NucleotideType.fromInteger(r1);
			NucleotideType type2 = NucleotideType.fromInteger(r2);

			value = Nucleotide.calculateEnergy(type1, type2);

			return;
		}

		if (label.equals("GETBOND"))
		{
			int r1 = executeTerminal(individual, parameters[0]);

			if (r1 < 0 || r1 >= individual.structure.structureLength)
				value = 0;
			else
			{
				Nucleotide current = individual.structure.current
						.getPrevious(r1);

				if (!current.isBond())
					value = 0;
				else
				{
					value = current.getIndex() - current.bond.getIndex();
				}
			}

			return;
		}

		if (label.equals("PURINE"))
		{
			int r1 = executeTerminal(individual, parameters[0]);

			NucleotideType nuc = NucleotideType.fromInteger(r1);

			if (nuc == NucleotideType.A || nuc == NucleotideType.G)
				value = individual.structure.structureLength;
			else
				value = -1;

			return;
		}

		if (label.equals("PYRIMIDINE"))
		{
			int r1 = executeTerminal(individual, parameters[0]);

			NucleotideType nuc = NucleotideType.fromInteger(r1);

			if (nuc == NucleotideType.U || nuc == NucleotideType.C)
				value = individual.structure.structureLength;
			else
				value = -1;

			return;
		}

		if (label.equals("SUM"))
		{
			int r1 = executeTerminal(individual, parameters[0]);

			if (r1 < 0 || r1 > 100)
			{
				value = -1;
				return;
			}
			else
			{
				value = 0;

				for (int i = 0; i < r1; i++)
				{
					value += executeTerminal(individual, parameters[1]);
				}
			}

			return;
		}

		if (label.equals("PRG5"))
		{
			value = 0;

			for (int i = 0; i < parameters.length; i++)
			{
				value += executeTerminal(individual, parameters[i]);
			}

			return;
		}

		/**
		 * Return setzt Wert von Ausgaberegister auf P1, dann BZR auf genügend
		 * großen Wert; Schleife wird abbrechen und P1 wird vom Programm als
		 * PUT-Richtung benutzt
		 */
		if (label.equals("RETURN"))
		{
			int r1 = executeTerminal(individual, parameters[0]);

			value = r1;

			individual.registers.get(individual.registers.size() - 1).value = r1;
			individual.bzr = individual.registers.size();

			return;
		}

		if (label.equals("GOTO") || label.equals("UPDATE_BONDING"))
		{
			value = 0;
			return;
		}

		// Unknown Function, must be a TERMINAL
		value = executeTerminal(individual, label);
	}

	@Override
	public String toString()
	{
		// Warum hat JAVA kein String.join?? LOL!
		String output = label;

		for (String param : parameters)
		{
			output += " " + param;
		}

		return output;
	}

	/**
	 * Returns a random terminal: TERMINAL function, NUMBER or Rx-Reference
	 * (which is a number)
	 * 
	 * @return
	 */
	public static String randomTerminal(int registerCount)
	{
		String label = TERMINAL_FUNCTIONS[RANDOM
				.nextInt(TERMINAL_FUNCTIONS.length)];

		// A number
		if (label.equals("NUMBER"))
		{
			return ("" + RANDOM.nextInt());
		}
		// A register Rx reference
		if (label.equals("REGISTER_VALUE"))
		{
			return ("R" + RANDOM.nextInt(registerCount));
		}
		// Skip x registers
		if (label.equals("SKIP%"))
		{
			return ("SKIP" + RANDOM.nextInt(registerCount));
		}

		// GOTO entfernt, hat nur probleme gemacht!
		// // A goto register command
		// if (label.equals("GOTO"))
		// {
		// return ("GOTO" + RANDOM.nextInt(Individual.REGISTERS));
		// }

		return label;
	}

	public static Register random(int registerCount)
	{
		String label = FUNCTIONS[RANDOM.nextInt(FUNCTIONS.length)];

		if (label.equals("TERMINAL"))
		{
			return new Register(randomTerminal(registerCount));
		}

		/**
		 * If functions
		 * 
		 * IF (X,Y) in R -> T
		 */
		if (label.equals("IF_LESS"))
		{
			return new Register("IF_LESS", randomTerminal(registerCount),
					randomTerminal(registerCount),
					randomTerminal(registerCount),
					randomTerminal(registerCount));
		}
		if (label.equals("IF_GREATER"))
		{
			return new Register("IF_GREATER", randomTerminal(registerCount),
					randomTerminal(registerCount),
					randomTerminal(registerCount),
					randomTerminal(registerCount));
		}
		if (label.equals("IF_EQUAL"))
		{
			return new Register("IF_EQUAL", randomTerminal(registerCount),
					randomTerminal(registerCount),
					randomTerminal(registerCount),
					randomTerminal(registerCount));
		}

		/**
		 * ADD, SUBTRACT, MULTIPLY, DIVIDE
		 */
		if (label.equals("ADD"))
		{
			return new Register("ADD", randomTerminal(registerCount),
					randomTerminal(registerCount));
		}
		if (label.equals("SUBTRACT"))
		{
			return new Register("SUBTRACT", randomTerminal(registerCount),
					randomTerminal(registerCount));
		}
		if (label.equals("MULTIPLY"))
		{
			return new Register("MULTIPLY", randomTerminal(registerCount),
					randomTerminal(registerCount));
		}
		if (label.equals("DIVIDE"))
		{
			return new Register("DIVIDE", randomTerminal(registerCount),
					randomTerminal(registerCount));
		}

		/**
		 * LOOK AROUND
		 */
		if (label.equals("LOOK_BACK"))
		{
			return new Register("LOOK_BACK", randomTerminal(registerCount));
		}
		if (label.equals("LOOK_FORWARD"))
		{
			return new Register("LOOK_FORWARD", randomTerminal(registerCount));
		}

		/**
		 * Entfernt aus Random Generierung
		 */
		// /**
		// * Energy between nucleotides
		// */
		// if (label.equals("CALCULATE_ENERGY"))
		// {
		// return new Register("CALCULATE_ENERGY",
		// randomTerminal(registerCount),
		// randomTerminal(registerCount));
		// }

		/**
		 * Nucleotide, given one one is bond to
		 */
		if (label.equals("GETBOND"))
		{
			return new Register("GETBOND", randomTerminal(registerCount));
		}

		/**
		 * PURINE, PYRIMIDINE
		 */
		if (label.equals("PURINE"))
		{
			return new Register("PURINE", randomTerminal(registerCount));
		}
		if (label.equals("PYRIMIDINE"))
		{
			return new Register("PYRIMIDINE", randomTerminal(registerCount));
		}

		/**
		 * DO P1 P2
		 */
		if (label.equals("SUM"))
		{
			return new Register("SUM", randomTerminal(registerCount),
					randomTerminal(registerCount));
		}

		/**
		 * PRG5 P1 P2 P3 P4 P5
		 */
		if (label.equals("PRG5"))
		{
			return new Register("PRG5", randomTerminal(registerCount),
					randomTerminal(registerCount),
					randomTerminal(registerCount),
					randomTerminal(registerCount),
					randomTerminal(registerCount));
		}

		/**
		 * RETURN P1
		 */
		if (label.equals("RETURN"))
		{
			return new Register("RETURN", randomTerminal(registerCount));
		}

		return new Register(randomTerminal(registerCount));
	}

}
