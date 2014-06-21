package rna.solver.tree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import rna.solver.GPRunner;
import rna.solver.Individual;
import rna.solver.NucleotideDirection;
import rna.solver.NucleotideType;
import rna.solver.RNAField;
import rna.solver.RelativeDirection;

public class TreeIndividual extends Individual implements Comparable
{
    /**
     * Main function
     */
    public TreeTopLevelFunction mainFunction;

    /**
     * ADF functions
     */
    public ArrayList<TreeTopLevelFunction> adfs;

    /**
     * Memory of invididual
     */
    public int accumulator;
   
    
    public TreeIndividual()
    {
        super();
    }

    /**
     * Copy-Konstruktor
     *
     * @param tocopy
     */
    public TreeIndividual(TreeIndividual tocopy)
    {
        this();
        
        this.mainFunction = new TreeTopLevelFunction(tocopy.mainFunction);
        this.adfs = new ArrayList<TreeTopLevelFunction>();
        for (TreeTopLevelFunction reg : tocopy.adfs)
        {
            adfs.add(new TreeTopLevelFunction(reg));
        }
    }  

    /**
     * Clears registers and creates a new random one
     */
    public void random(int... settings)
    {
        int adfs_count = settings[0];
        int adfs_param = settings[1];
        
        adfs = new ArrayList<TreeTopLevelFunction>();
        adfs.clear();
        
        for (int i = 0; i < adfs_count; i++)
        {
            TreeTopLevelFunction adf = new TreeTopLevelFunction("ADF" + i, adfs_param,
                    new ArrayList<TreeTopLevelFunction>());
            adfs.add(adf);
        }
        
        mainFunction = new TreeTopLevelFunction("MAIN", 0, adfs);
        mainFunction.adfs = adfs;
        
        System.out.println(mainFunction.toString());        
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
        accumulator = 0;
        
        for (int run = 0; run < rna.length() * 10; run++)
        {
            if (sequence.isEmpty())
            {
                break;
            }

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
        int adf = GPRunner.RANDOM.nextInt(adfs.size() + 1);
        
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
     * @param px Recombination probability
     */
    public void recombine(Individual i2, float px)
    {
        TreeIndividual indiv1 = this;
        TreeIndividual indiv2 = (TreeIndividual) i2;
        
        if (1 - GPRunner.RANDOM.nextFloat() <= px)
        {
            TreeTopLevelFunction
                    .recombine(indiv1.mainFunction, indiv2.mainFunction);
            
            for (int n = 0; n < indiv1.adfs.size(); n++)
            {
                TreeTopLevelFunction.recombine(indiv1.adfs.get(n),
                        indiv2.adfs.get(n));
            }
        }
    }   
  
    
    public void write(String file) throws IOException
    {
        FileWriter wr = new FileWriter(file);
        
        wr.write(this.toString());
        
        wr.close();
    }
    
    @Override
    public String toString()
    {
        StringBuilder b = new StringBuilder();
        
        b.append(">Individual with Fitness " + fitness + "\n");
        for (String rna : sequences)
        {
            b.append("~" + rna + "\n");
        }
        
        for (int i = 0; i < adfs.size(); i++)
        {
            b.append("ADF" + i + ":" + adfs.get(i).toString() + "\n");
        }
        
        b.append("MAIN:" + mainFunction.toString());
        
        return b.toString();
    }
    
    public static TreeIndividual load(String file) throws IOException
    {
        throw new RuntimeException("Not implemented!");
    }
    
    public Individual copy()
    {
        return new TreeIndividual(this);
    }
}
