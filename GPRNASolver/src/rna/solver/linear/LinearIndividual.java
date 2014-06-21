package rna.solver.linear;

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

public class LinearIndividual extends Individual implements Comparable
{
    /**
     * Main function
     */
    public LinearFunction mainFunction;

    /**
     * ADF functions
     */
    public ArrayList<LinearFunction> adfs;
    
    /**
     *
     */
    public LinearIndividual()
    {
        super();
    }

    /**
     * Copy-Konstruktor
     *
     * @param tocopy
     */
    public LinearIndividual(LinearIndividual tocopy)
    {
        this();

        this.mainFunction = new LinearFunction(tocopy.mainFunction);
        this.adfs = new ArrayList<>();
        for (LinearFunction reg : tocopy.adfs)
        {
            adfs.add(new LinearFunction(reg));
        }
    }

    /**
     * Clears registers and creates a new random one
     */
    public void random(int... settings)
    {
        int reg = settings[0];
        int adfs_count = settings[1];
        int adfs_param = settings[2];
        int adfs_reg = settings[3];

        adfs = new ArrayList<LinearFunction>();
        adfs.clear();

        for (int i = 0; i < adfs_count; i++)
        {
            adfs.add(new LinearFunction("ADF" + i, adfs_param, adfs_reg, null));
        }

        mainFunction = new LinearFunction("MAIN",0, reg, adfs);
    }

    /**
     * Run program, using given RNA string
     *
     * @param rna
     */
    public double run(String rna)
    {
        //clear structure
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
                int value = mainFunction.execute(this, null, adfs);

                RelativeDirection dir = RelativeDirection
                        .fromInteger(value);

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
        LinearIndividual indiv1 = this;
        LinearIndividual indiv2 = (LinearIndividual) i2;

        if (1 - GPRunner.RANDOM.nextFloat() <= px)
        {
            LinearFunction.recombine(indiv1.mainFunction, indiv2.mainFunction);

            for (int n = 0; n < indiv1.adfs.size(); n++)
            {
                LinearFunction.recombine(indiv1.adfs.get(n), indiv2.adfs.get(n));
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

        for (LinearFunction adf : adfs)
        {
            b.append(adf.toString());
        }
        
        b.append(mainFunction.toString());

        return b.toString();
    }

    public static LinearIndividual load(String file) throws IOException
    {
        BufferedReader rd = new BufferedReader(new FileReader(file));

        rd.readLine(); // ignore name

        String rna = rd.readLine();

        String buffer = null;

        /**
         * Create individual
         */
        LinearIndividual indiv = new LinearIndividual();
        indiv.adfs = new ArrayList<LinearFunction>();

        /**
         * Function reading
         */
        LinearFunction current = null;

        while ((buffer = rd.readLine()) != null)
        {
            String[] cmd = buffer.split(" ");

            if (cmd.length == 0)
            {
                break;
            }

            if (buffer.startsWith("#"))
            {
                int registerCount = Integer.parseInt(cmd[1]);
                int parameterCount = Integer.parseInt(cmd[2]);

                if (buffer.startsWith("#MAIN"))
                {
                    indiv.mainFunction = current = new LinearFunction(
                            "MAIN",parameterCount, registerCount, indiv.adfs);
                }
                else
                {
                    current = new LinearFunction("ADF" + indiv.adfs.size(),parameterCount, registerCount,
                            indiv.adfs);
                    indiv.adfs.add(current);
                }

                current.registers.clear();
            }
            else
            {
                String label = cmd[0];
                String[] params = Arrays.copyOfRange(cmd, 1, cmd.length);

                current.registers.add(new LinearRegister(label, params));
            }
        }

        indiv.run(rna);

        return indiv;
    }

    public Individual copy()
    {
        return new LinearIndividual(this);
    }
}
