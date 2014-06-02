package rna.solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Individual implements Comparable
{
	public String rna;
	public double fitness;

	public ProgramType type;

	/**
	 * Holds sequence
	 */
	public LinkedList<Integer> sequence;

	/**
	 * Holds geometric representation of secondary structure
	 */
	public RNAField structure;

	/**
	 * Main function
	 */
	public Function mainFunction;

	/**
	 * ADF functions
	 */
	public ArrayList<Function> adfs;

	public Individual(ProgramType type)
	{
		sequence = new LinkedList<Integer>();
		structure = new RNAField();
		this.type = type;
	}

	/**
	 * Copy-Konstruktor
	 * 
	 * @param tocopy
	 */
	public Individual(Individual tocopy)
	{
		this(tocopy.type);

		this.mainFunction = new Function(tocopy.mainFunction);
		this.adfs = new ArrayList<Function>();
		for (Function reg : tocopy.adfs)
		{
			adfs.add(new Function(reg));
		}
	}

	/**
	 * Put next nucleotide onto
	 * 
	 * @param dir
	 */
	public int put(RelativeDirection dir)
	{
		if (sequence.isEmpty())
			return 0;

		if (structure.append(dir,
				NucleotideType.fromInteger(sequence.remove()), false))
		{
			return structure.structureLength;
		}

		return 0;
	}

	public int undoPut()
	{
		if (structure.undo())
		{
			return structure.structureLength;
		}

		return 0;
	}

	/**
	 * Checks if put would collide or otherwise not possible
	 * 
	 * @param dir
	 */
	public int checkCollision(RelativeDirection dir)
	{
		if (sequence.isEmpty())
			return 0;

		if (structure.append(dir,
				NucleotideType.fromInteger(sequence.getFirst()), true))
		{
			return 0;
		}

		return structure.structureLength;
	}

	/**
	 * Clears registers and creates a new random one
	 */
	public void random(int reg, int adfs_count, int adfs_param, int adfs_reg)
	{
		adfs = new ArrayList<Function>();
		adfs.clear();

		for (int i = 0; i < adfs_count; i++)
		{
			adfs.add(new Function(type, adfs_param, adfs_reg, null));
		}

		mainFunction = new Function(type, 0, reg, adfs);
	}

	public void runBondingTest(String rna, String instructions)
	{
		// Truncate
		rna = rna.substring(0,
				Math.min(rna.length(), instructions.length()) - 1);

		// Prepare RNA data representation
		this.rna = rna = rna.toUpperCase();

		// Fill stack with A=0, C=1, U=2, G=3
		for (char c : rna.toCharArray())
		{
			if (c == 'A')
				sequence.add(0);
			else if (c == 'C')
				sequence.add(1);
			else if (c == 'U')
				sequence.add(2);
			else
				sequence.add(3);
		}

		// Initialize values (set cursor, direction and initial nucleotide
		structure.initial(0, 0, NucleotideType.fromInteger(sequence.remove()),
				NucleotideDirection.EAST);

		// Ok, ready. Run registers for each character in RNA string
		// Cancel if stack is empty

		for (int ins = 0; ins < Math.min(rna.length(), instructions.length()); ins++)
		{
			RelativeDirection dir = RelativeDirection.STRAIGHT;

			if (instructions.charAt(ins) == 'L')
			{
				dir = RelativeDirection.LEFT;
			}
			else if (instructions.charAt(ins) == 'S')
			{
				dir = RelativeDirection.STRAIGHT;
			}
			else
			{
				dir = RelativeDirection.RIGHT;
			}

			put(dir);
		}

		this.fitness = fitness();
	}

	/**
	 * Run program, using given RNA string
	 * 
	 * @param rna
	 */
	public void run(String rna)
	{
		// Prepare RNA data representation
		this.rna = rna = rna.toUpperCase();

		// Fill stack with A=0, C=1, U=2, G=3
		for (char c : rna.toCharArray())
		{
			if (c == 'A')
				sequence.add(0);
			else if (c == 'C')
				sequence.add(1);
			else if (c == 'U')
				sequence.add(2);
			else
				sequence.add(3);
		}

		// Initialize values (set cursor, direction and initial nucleotide
		structure.initial(0, 0, NucleotideType.fromInteger(sequence.remove()),
				NucleotideDirection.EAST);

		// Ok, ready. Run registers for each character in RNA string
		// Cancel if stack is empty

		for (int run = 0; run < rna.length() * 10; run++)
		{
			if (sequence.isEmpty())
				break;

			/**
			 * Main-Funktion wird aufgeführt und Ausgabe in Direction
			 * umgewandelt
			 */
			{
				int value = mainFunction.execute(this, null, adfs);
				if (type == ProgramType.EFFECT)
				{
					RelativeDirection dir = RelativeDirection
							.fromInteger(value);
					put(dir);
				}
			}
		}

		this.fitness = fitness();
	}

	// /**
	// * Bewertet lange gerade Strecken negativ, außer wenn eine Bindung besteht
	// *
	// *
	// * @return
	// */
	// public double compactness()
	// {
	// double score = 0;
	//
	// for(Nucleotide nuc : structure.structure.values())
	// {
	// if(nuc.previous != null && !nuc.isBond())
	// {
	// if(nuc.dir == nuc.previous.dir)
	// {
	// score += 1.5;
	// }
	// /**
	// * Update 1 - prevent diagonal straight lines
	// */
	// // else if(nuc.previous.previous != null &&
	// !nuc.previous.previous.isBond() && nuc.previous.previous.dir == nuc.dir)
	// // {
	// // score += 1;
	// // }
	// }
	// }
	//
	// return score;
	// }

	/**
	 * Calculates fitness of this object
	 * 
	 * 
	 * @return
	 */
	public double fitness()
	{
		double leftover_sequence = rna.length() - structure.structureLength;
		double energy = structure.energy();

		// return energy + leftover_sequence * leftover_sequence;
		return energy + 8 * leftover_sequence;
	}

	/**
	 * Mutiert alle Funktionen
	 * 
	 * @param p
	 */
	public void mutate(float p)
	{
		mainFunction.mutate(p);

		for (Function f : adfs)
		{
			f.mutate(p);
		}
	}

	/**
	 * Recombine using one-point X-Over
	 * 
	 * @param indiv1
	 * @param indiv2
	 * @param px
	 *            Recombination probability
	 */
	public static void recombine(Individual indiv1, Individual indiv2, float px)
	{
		Function.recombine(indiv1.mainFunction, indiv2.mainFunction, px);

		for (int n = 0; n < indiv1.adfs.size(); n++)
		{
			Function.recombine(indiv1.adfs.get(n), indiv2.adfs.get(n), px);
		}
	}

	@Override
	public int compareTo(Object arg0)
	{
		Individual other = (Individual) arg0;

		if (other.fitness < fitness)
			return 1;
		if (fitness < other.fitness)
			return -1;

		return 0;
	}

	public void write(String file) throws IOException
	{
		FileWriter wr = new FileWriter(file);

		wr.write(">Individual with Fitness " + fitness + "\n");
		wr.write(type.name() +"\n");
		wr.write(rna + "\n");

		mainFunction.write(wr, "MAIN");

		for (int i = 0; i < adfs.size(); i++)
		{
			adfs.get(i).write(wr, "ADF" + i);
		}

		wr.close();
	}

	public static Individual load(String file) throws IOException
	{
		// BufferedReader rd = new BufferedReader(new FileReader(file));
		//
		// rd.readLine(); // ignore
		//
		// String rna = rd.readLine();
		// ArrayList<Register> registers = new ArrayList<Register>();
		//
		// String buffer = null;
		//
		// while ((buffer = rd.readLine()) != null)
		// {
		// String[] cmd = buffer.split(" ");
		//
		// if (cmd.length == 0)
		// break;
		//
		// String label = cmd[0];
		// String[] params = Arrays.copyOfRange(cmd, 1, cmd.length);
		//
		// registers.add(new Register(label, params));
		// }
		//
		// Individual indiv = new Individual();
		// indiv.registers = registers;
		//
		// indiv.run(rna);
		//
		// return indiv;

		throw new RuntimeException("Funktion nicht fertig!");
	}
}
