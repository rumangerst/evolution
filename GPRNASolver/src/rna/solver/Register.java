package rna.solver;

import java.awt.Point;
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
	public static final int FALSE = -1;

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
	public int executeTerminal(Individual individual, Function parent,
			String label)
	{
		if (label.length() == 0)
			return 0;

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
		 * ENERGY current energy level
		 */
		if (label.equals("ENERGY"))
		{
			return individual.structure.energy();

		}
		/**
		 * ENERGY_OLD - energy without latest nucleotide
		 */
		if (label.equals("ENERGY_OLD"))
		{
			if (individual.structure.current.previous == null)
				return Integer.MAX_VALUE;
			return individual.structure.energy()
					- individual.structure.current.energy(individual.structure);

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

		if (label.equals("A"))
		{
			return 0;
		}
		if (label.equals("C"))
		{
			return 1;
		}
		if (label.equals("U"))
		{
			return 2;
		}
		if (label.equals("G"))
		{
			return 3;
		}

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
				return FALSE;
			return individual.sequence.get(0);
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
		 * Himmelsrichtungen, die zurückgeben, welches Nukleotid an POS N ist
		 */
		if (label.equals("NORTH"))
		{
			Nucleotide nuc = individual.structure.get(
					individual.structure.current.x,
					individual.structure.current.y - 1);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}
		if (label.equals("SOUTH"))
		{
			Nucleotide nuc = individual.structure.get(
					individual.structure.current.x,
					individual.structure.current.y + 1);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}
		if (label.equals("EAST"))
		{
			Nucleotide nuc = individual.structure.get(
					individual.structure.current.x + 1,
					individual.structure.current.y);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}
		if (label.equals("WEST"))
		{
			Nucleotide nuc = individual.structure.get(
					individual.structure.current.x - 1,
					individual.structure.current.y);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}
		if (label.equals("NORTH_EAST"))
		{
			Nucleotide nuc = individual.structure.get(
					individual.structure.current.x + 1,
					individual.structure.current.y - 1);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}
		if (label.equals("NORTH_WEST"))
		{
			Nucleotide nuc = individual.structure.get(
					individual.structure.current.x - 1,
					individual.structure.current.y - 1);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}
		if (label.equals("SOUTH_EAST"))
		{
			Nucleotide nuc = individual.structure.get(
					individual.structure.current.x + 1,
					individual.structure.current.y + 1);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}
		if (label.equals("SOUTH_WEST"))
		{
			Nucleotide nuc = individual.structure.get(
					individual.structure.current.x - 1,
					individual.structure.current.y + 1);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}

		/**
		 * Nukleotide, relativ zu current (siehe Himmelsrichtung)
		 */
		if (label.equals("RLEFT"))
		{
			Nucleotide current = individual.structure.current;
			Point p = NucleotideDirection.shiftByDir(current.x, current.y,
					RelativeDirection.LEFT.toAbsolute(current.dir));

			Nucleotide nuc = individual.structure.get(p.x, p.y);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}
		if (label.equals("RSTRAIGHT"))
		{
			Nucleotide current = individual.structure.current;
			Point p = NucleotideDirection.shiftByDir(current.x, current.y,
					RelativeDirection.STRAIGHT.toAbsolute(current.dir));

			Nucleotide nuc = individual.structure.get(p.x, p.y);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}
		if (label.equals("RRIGHT"))
		{
			Nucleotide current = individual.structure.current;
			Point p = NucleotideDirection.shiftByDir(current.x, current.y,
					RelativeDirection.RIGHT.toAbsolute(current.dir));

			Nucleotide nuc = individual.structure.get(p.x, p.y);

			if (nuc == null)
				return FALSE;
			return nuc.type.toInteger();
		}

		/**
		 * No known label => must be a number or a Rx reference
		 */
		if (label.startsWith("R"))
		{
			int id = Integer.parseInt(label.substring(1));

			if (id < 0 || id > parent.registers.size())
			{
				return -1;
			}

			return parent.registers.get(id).value;
		}
		else if (label.startsWith("SKIP")) // SKIPi-Funktion
		{
			int id = Integer.parseInt(label.substring(4));

			if (id < 0)
			{
				return 0;
			}

			parent.bzr += id;

			return 1;
		}
		else if (label.startsWith("P")) // Zugriff auf Parameter Pi
		{
			int id = Integer.parseInt(label.substring(1));

			if (id < 0)
			{
				return -1;
			}

			return parent.parameters[id];
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
	public void execute(Individual individual, Function parent)
	{
		if (label.equals("IF_GREATER"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);
			int r2 = executeTerminal(individual, parent, parameters[1]);

			if (r1 > r2)
			{
				value = executeTerminal(individual, parent, parameters[2]);
			}
			else
			{
				value = executeTerminal(individual, parent, parameters[3]);
			}

			return;

		}
		if (label.equals("IF_LESS"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);
			int r2 = executeTerminal(individual, parent, parameters[1]);

			if (r1 < r2)
			{
				value = executeTerminal(individual, parent, parameters[2]);
			}
			else
			{
				value = executeTerminal(individual, parent, parameters[3]);
			}

			return;

		}
		if (label.equals("IF_EQUAL"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);
			int r2 = executeTerminal(individual, parent, parameters[1]);

			if (r1 == r2)
			{
				value = executeTerminal(individual, parent, parameters[2]);
			}
			else
			{
				value = executeTerminal(individual, parent, parameters[3]);
			}

			return;

		}

		if (label.equals("ADD"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);
			int r2 = executeTerminal(individual, parent, parameters[1]);

			value = r1 + r2;

			return;
		}

		if (label.equals("SUBTRACT"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);
			int r2 = executeTerminal(individual, parent, parameters[1]);

			value = r1 - r2;

			return;
		}

		if (label.equals("MULTIPLY"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);
			int r2 = executeTerminal(individual, parent, parameters[1]);

			value = r1 * r2;

			return;
		}

		if (label.equals("DIVIDE"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);
			int r2 = executeTerminal(individual, parent, parameters[1]);

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
			int r1 = executeTerminal(individual, parent, parameters[0]);

			if (r1 < 0 || r1 >= individual.sequence.size())
				value = FALSE;
			else
				value = individual.sequence.get(r1);

			return;
		}

		if (label.equals("LOOK_BACK"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);

			if (r1 < 0 || r1 >= individual.structure.structureLength)
				value = FALSE;
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
			int r1 = executeTerminal(individual, parent, parameters[0]);
			int r2 = executeTerminal(individual, parent, parameters[1]);

			NucleotideType type1 = NucleotideType.fromInteger(r1);
			NucleotideType type2 = NucleotideType.fromInteger(r2);

			value = Nucleotide.calculateEnergy(type1, type2);

			return;
		}

		if (label.equals("GETBOND"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);

			if (r1 < 0 || r1 >= individual.structure.structureLength)
				value = FALSE;
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
			int r1 = executeTerminal(individual, parent, parameters[0]);

			NucleotideType nuc = NucleotideType.fromInteger(r1);

			if (nuc == NucleotideType.A || nuc == NucleotideType.G)
				value = individual.structure.structureLength;
			else
				value = FALSE;

			return;
		}

		if (label.equals("PYRIMIDINE"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);

			NucleotideType nuc = NucleotideType.fromInteger(r1);

			if (nuc == NucleotideType.U || nuc == NucleotideType.C)
				value = individual.structure.structureLength;
			else
				value = FALSE;

			return;
		}

		/**
		 * Return setzt Wert von Ausgaberegister auf P1, dann BZR auf genügend
		 * großen Wert; Schleife wird abbrechen und P1 wird vom Programm als
		 * PUT-Richtung benutzt
		 */
		if (label.equals("RETURN"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);

			value = r1;

			parent.registers.get(parent.registers.size() - 1).value = r1;
			parent.bzr = parent.registers.size();

			return;
		}

		/**
		 * ADF-Funktion ausführen
		 */
		if (label.startsWith("ADF"))
		{
			int id = Integer.parseInt(label.substring(3));

			Function adf = parent.adfs.get(id);

			int[] params = new int[adf.parameterCount];

			for (int i = 0; i < params.length; i++)
			{
				params[i] = executeTerminal(individual, parent, parameters[i]);
			}

			value = adf.execute(individual, params, null);
			return;
		}

		if (label.equals("RDIR"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);

			value = RelativeDirection.fromInteger(r1).toInteger();

			return;
		}
		if (label.equals("NUC"))
		{
			int r1 = executeTerminal(individual, parent, parameters[0]);

			value = NucleotideType.fromInteger(r1).toInteger();

			return;
		}

		if (label.equals("MIN"))
		{
			value = Integer.MAX_VALUE;

			for (int i = 0; i < 5; i++)
			{
				int r = executeTerminal(individual, parent, parameters[i]);

				if (r < value)
					value = r;
			}

			return;
		}

		if (label.equals("MAX"))
		{
			value = Integer.MIN_VALUE;

			for (int i = 0; i < 5; i++)
			{
				int r = executeTerminal(individual, parent, parameters[i]);

				if (r > value)
					value = r;
			}

			return;
		}

		if (label.equals("AVG"))
		{
			value = 0;

			for (int i = 0; i < 5; i++)
			{
				int r = executeTerminal(individual, parent, parameters[i]);
				value += r;
			}

			value = value / 5;

			return;
		}

		// Unknown Function, must be a TERMINAL
		value = executeTerminal(individual, parent, label);
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
}
