/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rna.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import rna.solver.linear.LinearRegister;

public abstract class Individual implements Comparable
{

    public ArrayList<String> sequences;
    public double fitness;
    public int energy;

    /**
     * Holds sequence
     */
    public LinkedList<Integer> sequence;

    /**
     * Holds geometric representation of secondary structure
     */
    public RNAField structure;

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
    }

    /**
     * Put next nucleotide onto
     *
     * @param dir
     */
    public int put(RelativeDirection dir)
    {
        if (sequence.isEmpty())
        {
            return 0;
        }

        if (structure.append(dir,
                NucleotideType.fromInteger(sequence.remove()), false))
        {
            return structure.structureLength;
        }

        return LinearRegister.FALSE;
    }

    public int undoPut()
    {
        if (structure.undo())
        {
            return structure.structureLength;
        }

        return LinearRegister.FALSE;
    }

    /**
     * Checks if put would collide or otherwise not possible
     *
     * @param dir
     */
    public int checkCollision(RelativeDirection dir)
    {
        if (sequence.isEmpty())
        {
            return 0;
        }

        if (structure.append(dir,
                NucleotideType.fromInteger(sequence.getFirst()), true))
        {
            return LinearRegister.FALSE;
        }

        return LinearRegister.TRUE;
    }

    /**
     * Clears registers and creates a new random one
     *
     * @param settings
     */
    public abstract void random(int... settings);

    public void runBondingTest(String rna, String instructions)
    {
        // Truncate
        rna = rna.substring(0,
                Math.min(rna.length(), instructions.length()) - 1);

        // Prepare RNA data representation
        rna = rna.toUpperCase();

        // Fill stack with A=0, C=1, U=2, G=3
        for (char c : rna.toCharArray())
        {
            if (c == 'A')
            {
                sequence.add(0);
            }
            else if (c == 'C')
            {
                sequence.add(1);
            }
            else if (c == 'U')
            {
                sequence.add(2);
            }
            else
            {
                sequence.add(3);
            }
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
    public abstract double run(String rna);

    /**
     * Calculates fitness of this object
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

        return fitness;
    }

    /**
     * Mutiert alle Funktionen
     *
     * @param p
     */
    public abstract void mutate(float p);
    
    public abstract void recombine(Individual indiv2, float px);

    @Override
    public int compareTo(Object arg0)
    {
        Individual other = (Individual) arg0;

        if (other.fitness < fitness)
        {
            return 1;
        }
        if (fitness < other.fitness)
        {
            return -1;
        }

        return 0;
    }

    public abstract void write(String file) throws IOException;

    public abstract Individual copy();

}
