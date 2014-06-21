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
	public ArrayList<String> sequences;
	public double fitness;

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
	public TopLevelFunction mainFunction;

	/**
	 * ADF functions
	 */
	public ArrayList<TopLevelFunction> adfs;

	/**
	 * Memory of invididual
	 */
	public int accumulator;

	/**
	 * Old energy
	 * 
	 * @param type
	 */
	public int runtime_energy_old;

	public Individual()
	{
		sequence = new LinkedList<Integer>();
	}

	/**
	 * Copy-Konstruktor
	 * 
	 * @param tocopy
	 */
	public Individual(Individual tocopy)
	{
		this();

		this.mainFunction = new TopLevelFunction(tocopy.mainFunction);
		this.adfs = new ArrayList<TopLevelFunction>();
		for (TopLevelFunction reg : tocopy.adfs)
		{
			adfs.add(new TopLevelFunction(reg));
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

		return Function.FALSE;
	}

	public int undoPut()
	{
		if (structure.undo())
		{
			return structure.structureLength;
		}

		return Function.FALSE;
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
			return Function.FALSE;
		}

		return Function.TRUE;
	}

	/**
	 * Clears registers and creates a new random one
	 */
	public void random(int adfs_count, int adfs_param)
	{
		adfs = new ArrayList<TopLevelFunction>();
		adfs.clear();

		for (int i = 0; i < adfs_count; i++)
		{
			TopLevelFunction adf = new TopLevelFunction("ADF" + i, adfs_param,
					new ArrayList<TopLevelFunction>());
			adfs.add(adf);
		}

		mainFunction = new TopLevelFunction("MAIN", 0, adfs);
		mainFunction.adfs = adfs;

		System.out.println(mainFunction.toString());
		this.toString();

	}

	public void runBondingTest(String rna, String instructions)
	{
		// Truncate
		rna = rna.substring(0,
				Math.min(rna.length(), instructions.length() + 1));

		// Prepare RNA data representation
		rna = rna.toUpperCase();
                
                sequences = new ArrayList<String>();
                sequences.add(rna);
                structure = new RNAField(rna);

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
	}

	/**
	 * Run program, using given RNA string
	 * 
	 * @param rna
	 */
	public double run(String rna)
	{
		// clear structure
		structure = new RNAField(rna);
		sequence.clear();

		// Prepare RNA data representation
		rna = rna.toUpperCase();

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
		accumulator = 0;

		for (int run = 0; run < rna.length() * 10; run++)
		{
			if (sequence.isEmpty())
				break;

			/**
			 * Main-Funktion wird aufgeführt und Ausgabe in Direction
			 * umgewandelt
			 */
			{
				int value = mainFunction.execute(this, new int[0]);

				RelativeDirection dir = RelativeDirection.fromInteger(value);

				if (dir != RelativeDirection.UNDO)
				{
					put(dir);
				}
				else
				{
					structure.undo();
				}
			}
		}

		return structure.fitness();
	}

	/**
	 * Calculates fitness of this object
	 * 
	 * Method 1: Sum Method 2: use max (worst) fitness [schlecht]
	 * 
	 * 
	 * @return
	 */
	public double fitness(ArrayList<String> rnas)
	{
		this.sequences = rnas;

		fitness = 0;

		for (String rna : rnas)
		{
			double f = run(rna);

			fitness += f;
		}

		return fitness / rnas.size();
	}

	/**
	 * Mutiert alle Funktionen
	 * 
	 * @param p
	 */
	public void mutate(float p)
	{
		/*
		 * mainFunction.mutate(p);
		 * 
		 * for (Function f : adfs) { f.mutate(p); }
		 */

		/**
		 * Suche eine Funktion aus, die mutiert werden soll
		 */
		int adf = Function.RANDOM.nextInt(adfs.size() + 1);

		if (adf >= adfs.size())
		{
			mainFunction.mutate(p);
		}
		else
		{
			adfs.get(adf).mutate(p);
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
		if (1 - Function.RANDOM.nextFloat() <= px)
		{
			TopLevelFunction
					.recombine(indiv1.mainFunction, indiv2.mainFunction);

			for (int n = 0; n < indiv1.adfs.size(); n++)
			{
				TopLevelFunction.recombine(indiv1.adfs.get(n),
						indiv2.adfs.get(n));
			}
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
		for (String rna : sequences)
		{
			wr.write("~" + rna + "\n");
		}

		for (int i = 0; i < adfs.size(); i++)
		{
			wr.write("ADF" + i + ":" + adfs.get(i).toString() + "\n");
		}

		wr.write("MAIN:" + mainFunction.toString());

		wr.close();
	}

	public static Individual load(String file) throws IOException
	{
		throw new RuntimeException("Not implemented!");

		// BufferedReader rd = new BufferedReader(new FileReader(file));
		//
		// rd.readLine(); // ignore name
		//
		// String rna = rd.readLine();
		//
		// String buffer = null;
		//
		// /**
		// * Create individual
		// */
		// Individual indiv = new Individual();
		// indiv.adfs = new ArrayList<TopLevelFunction>();
		//
		// /**
		// * Function reading
		// */
		//
		// Function current = null;
		//
		//
		// indiv.run(rna);
		//
		// return indiv;
	}
}
