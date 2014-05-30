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
	
	public int bzr;
	public int goto_count;
	public boolean infinite_loop;

	/**
	 * Holds sequence
	 */
	public LinkedList<Integer> sequence;

	/**
	 * Holds geometric representation of secondary structure
	 */
	public RNAField structure;

	/**
	 * Registers, containing program
	 */
	public ArrayList<Register> registers;

	public Individual()
	{
		sequence = new LinkedList<Integer>();
		structure = new RNAField();
		registers = new ArrayList<Register>();
	}

	/**
	 * Copy-Konstruktor
	 * 
	 * @param tocopy
	 */
	public Individual(Individual tocopy)
	{
		this();

		for (Register reg : tocopy.registers)
		{
			registers.add(new Register(reg));
		}
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

	public void printRegisters()
	{
		for (Register reg : registers)
		{
			System.out.println(reg.toString());
		}
	}

	/**
	 * Clears registers and creates a new random one
	 */
	public void createRandomRegister(int reg)
	{
		registers.clear();

		for (int i = 0; i < reg; i++)
		{
			registers.add(Register.random(reg));
		}
	}
	
	/**
	 * Set BZR to register, called by GOTO
	 * @param register
	 * @return
	 */
	public int gotoRegister(int register)
	{
		if(register < 0 || register >= registers.size())
		{
			return -1;
		}
		
		bzr = register - 1;
		
		goto_count++;
		
		return bzr;
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
		structure.initial(0, 0,
				NucleotideType.fromInteger(sequence.remove()),
				NucleotideDirection.EAST);

		// Ok, ready. Run registers for each character in RNA string
		// Cancel if stack is empty		
		
		infinite_loop = false;

		for (int run = 0; run < rna.length() && !sequence.isEmpty(); run++)
		{
			bzr = 0;
			goto_count = 0;
			
			while(bzr < registers.size())
			{
				if(goto_count >= 1000)
				{
					infinite_loop = true;
					return;
				}
				
				registers.get(bzr).execute(this);
				bzr++;
			}
		}

		this.fitness = fitness();
	}
	
//	/**
//	 * Bewertet lange gerade Strecken negativ, außer wenn eine Bindung besteht
//	 * 
//	 * 
//	 * @return
//	 */
//	public double compactness()
//	{
//		double score = 0;
//		
//		for(Nucleotide nuc : structure.structure.values())
//		{
//			if(nuc.previous != null && !nuc.isBond())
//			{
//				if(nuc.dir == nuc.previous.dir)
//				{					
//					score += 1.5;
//				}
//				/**
//				 * Update 1 - prevent diagonal straight lines
//				 */
////				else if(nuc.previous.previous != null && !nuc.previous.previous.isBond() && nuc.previous.previous.dir == nuc.dir)
////				{
////					score += 1;
////				}
//			}
//		}
//		
//		return score;
//	}

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

		//return energy + leftover_sequence * leftover_sequence;
		return energy + 8*leftover_sequence;
	}

	/**
	 * Two cases of mutation: I) Parameter mutation (mutate parameter of
	 * register) II) Register mutation (new random register)
	 * 
	 * @param p1 Register mutation - Probability to select the register	 *
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
				int parameter = Register.RANDOM.nextInt(reg.parameters.length + 1);
				
				if(parameter == 0)
				{
					/**
					 * Mutate whole register
					 */
					this.registers.set(i, Register.random(regCount));
				}
				else
				{
					/**
					 * Mutate parameter
					 */
					reg.parameters[parameter - 1] = Register.randomTerminal(regCount);
				}
			}
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
		int reg = indiv1.registers.size();
		
		if (1 - Register.RANDOM.nextFloat() <= px)
		{
			int index = Register.RANDOM.nextInt(reg);

			if (index == 0 || index == reg - 1)
				return;

			// X-Over
			for (int i = 0; i < index; i++)
			{
				indiv1.registers.set(i, indiv2.registers.get(i));
			}
			for (int i = index; i < reg; i++)
			{
				indiv2.registers.set(i, indiv1.registers.get(i));
			}
		}
	}

	@Override
	public int compareTo(Object arg0)
	{
		Individual other = (Individual)arg0;
		
		if(other.fitness < fitness)
			return 1;
		if(fitness < other.fitness)
			return -1;
		
		return 0;
	}
	
	public void write(String file) throws IOException
	{
		FileWriter wr = new FileWriter(file);
		
		wr.write(">Individual with Fitness " + fitness + "\n");
		wr.write(rna + "\n");
		
		for(Register reg : registers)
		{
			wr.write(reg.label);
			
			for(int i = 0; i < reg.parameters.length; i++)
			{
				wr.write(" " + reg.parameters[i]);
			}
			
			wr.write("\n");
		}
		
		wr.close();
	}
	
	public static Individual load(String file) throws IOException
	{
		BufferedReader rd = new BufferedReader(new FileReader(file));
		
		rd.readLine(); //ignore
		
		String rna = rd.readLine();
		ArrayList<Register> registers = new ArrayList<Register>();
		
		String buffer = null;
		
		while((buffer = rd.readLine()) != null)
		{
			String[] cmd = buffer.split(" ");
			
			if(cmd.length == 0)
				break;
			
			String label = cmd[0];
			String[] params = Arrays.copyOfRange(cmd, 1, cmd.length);
			
			registers.add(new Register(label, params));
		}
		
		Individual indiv = new Individual();
		indiv.registers = registers;
		
		indiv.run(rna);
		
		return indiv;
	}
}
