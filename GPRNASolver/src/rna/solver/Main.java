package rna.solver;

import rna.solver.linear.LinearIndividual;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.panayotis.gnuplot.JavaPlot;

import rna.resultwindow.ResultViewer;
import rna.resultwindow.StatPlot;

public class Main
{

    /**
     * Testsequenzen:
     *
     * GGGGGGGGGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCCCCC ACUCGGUUACGAG
     * GAUGCAUACCCAAGUGCCUGA ACGUCCCACGUAAAAAGGGACGUUUUUACGU
     */
    private static void bondingTest(String rna, String instructions)
    {
        LinearIndividual indiv = new LinearIndividual();
        indiv.runBondingTest(rna, instructions);

        ResultViewer.showResults(indiv);
    }

    private static void optimize(ArrayList<String> sequences)
    {
        GPRunner runner = new GPRunner(sequences);
        runner.generations = 4000;
        runner.mutateProbability = 1.0f / 20.0f;

        //runner.createInitialTreePopulation(3, 3);
        runner.createInitialLinearPopulation(20, 3, 20, 3);
        
        Individual best = runner.evolve();

        ResultViewer.showResults(best);
    }

    public static void main(String[] args)
    {
        ArrayList<String> sequences = new ArrayList<String>();
//		sequences.add("GGGGGGGGGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCCCCC");
//		sequences.add("AAAAAAAAAAAAAAAAAAAAAAAAUAUAUUUUUUUUUUUUUUU");

//		sequences.add("GGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCC");
//		sequences.add("AAAAAAAAAAAAAAAAAUAUAUUUUUUUUUUUU");
                
                sequences.add("GGGGGGGGGGGGGGGCGCGCCCCCCCCCCCC");
		sequences.add("AAAAAAAAAAAAAAAUAUAUUUUUUUUUUUU");
//        sequences.add("ACUCGGUUACGAG");
//        sequences.add("ACUGCGGUUACGCAG");
                
//		sequences.add("GAUGCAUACCCAAGUGCCUGA");

               // sequences.add("GGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCC");
        optimize(sequences);
    }

}
