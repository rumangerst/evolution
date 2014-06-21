package rna.solver.tree;

import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import rna.solver.GPRunner;
import rna.solver.Nucleotide;
import rna.solver.NucleotideDirection;
import rna.solver.NucleotideType;
import rna.solver.RelativeDirection;

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
public class TreeFunction
{
	public static final int FALSE = -1;
	public static final int TRUE = 1;
        public static final int TREE_MAX_DEPTH = 7;

	/**
	 * Alle Funktionen
	 */
	public static final TreeFunctionFactory[] STATIC_FUNCTIONS = new TreeFunctionFactory[] {

	new TreeFunctionFactory("COLLIDE_STRAIGHT"),
			new TreeFunctionFactory("COLLIDE_LEFT"),
			new TreeFunctionFactory("COLLIDE_RIGHT"),
			new TreeFunctionFactory("ENERGY"), new TreeFunctionFactory("ENERGY_OLD"),
			new TreeFunctionFactory("LENGTH"), new TreeFunctionFactory("STACK"),
			new TreeFunctionFactory("PREV"), new TreeFunctionFactory("NEXT"),
			new TreeFunctionFactory("NORTH"), new TreeFunctionFactory("SOUTH"),
			new TreeFunctionFactory("EAST"), new TreeFunctionFactory("WEST"),
			new TreeFunctionFactory("NORTH_WEST"),
			new TreeFunctionFactory("NORTH_EAST"),
			new TreeFunctionFactory("SOUTH_WEST"),
			new TreeFunctionFactory("SOUTH_EAST"), new TreeFunctionFactory("RLEFT"),
			new TreeFunctionFactory("RRIGHT"), new TreeFunctionFactory("RSTRAIGHT"),
			new TreeFunctionFactory("A"), new TreeFunctionFactory("C"),
			new TreeFunctionFactory("U"), new TreeFunctionFactory("G"),
			new TreeFunctionFactory("LEFT"), new TreeFunctionFactory("RIGHT"),
			new TreeFunctionFactory("STRAIGHT"), new TreeFunctionFactory("IF_LESS", 4),
			new TreeFunctionFactory("IF_GREATER", 4),
			new TreeFunctionFactory("IF_EQUAL", 4),
			new TreeFunctionFactory("ADD", 2),
			new TreeFunctionFactory("SUBTRACT", 2),
			new TreeFunctionFactory("MULTIPLY", 2),
			new TreeFunctionFactory("DIVIDE", 2),
			new TreeFunctionFactory("LOOK_BACK", 1),
			new TreeFunctionFactory("LOOK_FORWARD", 1),
			new TreeFunctionFactory("CALCULATE_ENERGY", 2),
			new TreeFunctionFactory("GETBOND", 2),
			new TreeFunctionFactory("PURINE", 1),
			new TreeFunctionFactory("PYRIMIDINE", 1),
			new TreeFunctionFactory("RDIR", 1), // Wandelt Zahl in eine Richtung um
			new TreeFunctionFactory("NUC", 1), // Wandelt Zahl in Nucleotid um

			new TreeFunctionFactory("MIN", 5), new TreeFunctionFactory("MAX", 5),
			new TreeFunctionFactory("AVG", 5)
	};

	public TreeFunction parent;

	public ArrayList<TreeFunction> parameters;
	public String label;
	public int inputCount;

	public ArrayList<TreeFunctionFactory> dynamic_Functions;

	/**
	 * Speichert aktuell verwendete ADF
	 */
	public ArrayList<TreeTopLevelFunction> adfs;
	
	

	
	public TreeFunction(String label, int inputs, ArrayList<TreeTopLevelFunction> adfs)
	{
        this.dynamic_Functions = new ArrayList<>();
        this.parameters = new ArrayList<>();

        this.label = label;
		this.inputCount = inputs;
		this.adfs = adfs;
		this.parent = null;
		
		for (int i = 0; i < adfs.size(); i++)
		{
			dynamic_Functions.add(new TreeFunctionFactory("ADF" + i,
					adfs.get(i).inputCount));
		}

		for (int i = 0; i < inputCount; i++)
		{
			dynamic_Functions.add(new TreeFunctionFactory("P" + i));
		}
	}

	public TreeFunction(String label, TreeFunction parent)
	{
		this(label, parent.inputCount, parent.adfs);
		this.parent = parent;
	}

	/**
	 * Kopiert die Funktion Es genügt, die Elternfunktion zu kopieren.
	 * 
	 * @param toCopy
	 */
	public TreeFunction(TreeFunction toCopy)
	{
		this.label = toCopy.label;
		this.dynamic_Functions = toCopy.dynamic_Functions;
		this.inputCount = toCopy.inputCount;

		this.parameters = new ArrayList<TreeFunction>();
		this.adfs = toCopy.adfs;
		
		for (TreeFunction f : toCopy.parameters)
		{
			TreeFunction c = new TreeFunction(f);
			c.parent = this;
			this.parameters.add(c);
		}
	}

	/**
	 * Führt Funktion aus und gibt den Wert des letzten registers
	 * (Ausgaberegister) zurück
	 * 
	 *
	 * @return
	 */
	public int execute(TreeIndividual individual, int[] args)
	{
		/**
		 * Execute terminals
		 */
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
		 * Execute parameter functions
		 */
		if (label.equals("IF_GREATER"))
		{
			int r1 = parameters.get(0).execute(individual, args);
			int r2 = parameters.get(1).execute(individual, args);

			if (r1 > r2)
			{
				return parameters.get(2).execute(individual, args);
			}
			else
			{
				return parameters.get(3).execute(individual, args);
			}

		}
		if (label.equals("IF_LESS"))
		{
			int r1 = parameters.get(0).execute(individual, args);
			int r2 = parameters.get(1).execute(individual, args);

			if (r1 < r2)
			{
				return parameters.get(2).execute(individual, args);
			}
			else
			{
				return parameters.get(3).execute(individual, args);
			}
		}
		if (label.equals("IF_EQUAL"))
		{
			int r1 = parameters.get(0).execute(individual, args);
			int r2 = parameters.get(1).execute(individual, args);

			if (r1 == r2)
			{
				return parameters.get(2).execute(individual, args);
			}
			else
			{
				return parameters.get(3).execute(individual, args);
			}
		}

		if (label.equals("ADD"))
		{
			int r1 = parameters.get(0).execute(individual, args);
			int r2 = parameters.get(1).execute(individual, args);

			return r1 + r2;
		}

		if (label.equals("SUBTRACT"))
		{
			int r1 = parameters.get(0).execute(individual, args);
			int r2 = parameters.get(1).execute(individual, args);

			return r1 - r2;
		}

		if (label.equals("MULTIPLY"))
		{
			int r1 = parameters.get(0).execute(individual, args);
			int r2 = parameters.get(1).execute(individual, args);

			return r1 * r2;
		}

		if (label.equals("DIVIDE"))
		{
			int r1 = parameters.get(0).execute(individual, args);
			int r2 = parameters.get(1).execute(individual, args);

			if (r2 == 0)
			{
				return 0;
			}

			return r1 / r2;
		}

		if (label.equals("LOOK_FORWARD"))
		{
			int r1 = parameters.get(0).execute(individual, args);

			if (r1 < 0 || r1 >= individual.sequence.size())
				return FALSE;
			else
				return individual.sequence.get(r1);
		}

		if (label.equals("LOOK_BACK"))
		{
			int r1 = parameters.get(0).execute(individual, args);

			if (r1 < 0 || r1 >= individual.structure.structureLength)
				return FALSE;
			else
			{
				Nucleotide current = individual.structure.current;

				for (int i = 0; i < r1; i++)
				{
					current = current.previous;
				}

				return current.type.toInteger();
			}
		}

		if (label.equals("CALCULATE_ENERGY"))
		{
			int r1 = parameters.get(0).execute(individual, args);
			int r2 = parameters.get(1).execute(individual, args);

			NucleotideType type1 = NucleotideType.fromInteger(r1);
			NucleotideType type2 = NucleotideType.fromInteger(r2);

			return Nucleotide.calculateEnergy(type1, type2);
		}

		if (label.equals("GETBOND"))
		{
			int r1 = parameters.get(0).execute(individual, args);

			if (r1 < 0 || r1 >= individual.structure.structureLength)
				return FALSE;
			else
			{
				Nucleotide current = individual.structure.current
						.getPrevious(r1);

				if (!current.isBond())
					return 0;
				else
				{
					return current.getIndex() - current.bond.getIndex();
				}
			}
		}

		if (label.equals("PURINE"))
		{
			int r1 = parameters.get(0).execute(individual, args);

			NucleotideType nuc = NucleotideType.fromInteger(r1);

			if (nuc == NucleotideType.A || nuc == NucleotideType.G)
				return TRUE;
			else
				return FALSE;
		}

		if (label.equals("PYRIMIDINE"))
		{
			int r1 = parameters.get(0).execute(individual, args);

			NucleotideType nuc = NucleotideType.fromInteger(r1);

			if (nuc == NucleotideType.U || nuc == NucleotideType.C)
				return TRUE;
			else
				return FALSE;
		}

		/**
		 * ADF-Funktion ausführen
		 */
		if (label.startsWith("ADF"))
		{
			int id = Integer.parseInt(label.substring(3));

			TreeFunction adf = adfs.get(id);

			int[] params = new int[adf.inputCount];

			for (int i = 0; i < params.length; i++)
			{
				params[i] = parameters.get(i).execute(individual, args);
			}

			return adf.execute(individual, params);
		}

		if (label.equals("RDIR"))
		{
			int r1 = parameters.get(0).execute(individual, args);

			return RelativeDirection.fromInteger(r1).toInteger();
		}
		if (label.equals("NUC"))
		{
			int r1 = parameters.get(0).execute(individual, args);

			return NucleotideType.fromInteger(r1).toInteger();
		}

		if (label.equals("MIN"))
		{
			int value = Integer.MAX_VALUE;

			for (int i = 0; i < 5; i++)
			{
				int r = parameters.get(i).execute(individual, args);

				if (r < value)
					value = r;
			}

			return value;
		}

		if (label.equals("MAX"))
		{
			int value = Integer.MIN_VALUE;

			for (int i = 0; i < 5; i++)
			{
				int r = parameters.get(i).execute(individual, args);

				if (r > value)
					value = r;
			}

			return value;
		}

		if (label.equals("AVG"))
		{
			int value = 0;

			for (int i = 0; i < 5; i++)
			{
				int r = parameters.get(i).execute(individual, args);
				value += r;
			}

			value = value / 5;

			return value;
		}
		
		/**
		 * Akkumulator/Store
		 */
		if (label.equals("STOR"))
		{
			int r1 = parameters.get(0).execute(individual, args);
			
			individual.accumulator = r1;

			return r1;
		}
		if(label.equals("ACC"))
		{
			return individual.accumulator;
		}

		/**
		 * Parameterterminale
		 */
		if (label.startsWith("P"))
		{
			return args[Integer.parseInt(label.substring(1))];

		}

		throw new RuntimeException("Invalid function: " + label);
	}

  
	/**
	 * Generiert ein zufälliges Register ALLE Funktionen und Terminalfunktionen
	 * sind gleichwahrscheinlich
	 * 
	 * @return
	 */
	public TreeFunction randomFunction(int depth)
	{
		int rand = GPRunner.RANDOM.nextInt(STATIC_FUNCTIONS.length + dynamic_Functions.size());
		
		int i = 0;
		
		while(true)
		{
			TreeFunctionFactory fac;
			
			if( i < STATIC_FUNCTIONS.length)
				fac = STATIC_FUNCTIONS[i];
			else
				fac = dynamic_Functions.get(i - STATIC_FUNCTIONS.length);
			
			if(depth < TREE_MAX_DEPTH && fac.parameters > 0)
			{
				if(rand == 0)
				{
					return fac.createFunction(this, depth);
				}
				else
				{
					rand--;
				}
			}
			
			if(fac.parameters == 0)
			{
				if(rand == 0)
				{
					return fac.createFunction(this, depth);
				}
				else
				{
					rand--;
				}
			}
			
			i++;
			
			if(i >= STATIC_FUNCTIONS.length + dynamic_Functions.size())
				i = 0;
		}
	}

	/**
	 * Traverse through all functions
	 * 
	 * @return
	 */
	public LinkedList<TreeFunction> traverse(LinkedList<TreeFunction> container)
	{		
		container.add(this);

		for (TreeFunction f : parameters)
		{
			// subfunctions.add(f);
			f.traverse(container);
		}

		return container;
	}

	

	@Override
	public String toString()
	{
		String b = "(";
		b += (label + " ");

		for (TreeFunction parameter : parameters)
		{
			b += (parameter.toString() + " ");
		}

		b = b.trim();

		return b + ")";
	}
}
