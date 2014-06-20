package rna.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.panayotis.gnuplot.JavaPlot;

import resultwindow.ResultViewer;
import resultwindow.StatPlot;

public class Main
{
	/**
	 * Testsequenzen:
	 * 
	 * GGGGGGGGGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCCCCC
	 * ACUCGGUUACGAG
	 * GAUGCAUACCCAAGUGCCUGA
	 * ACGUCCCACGUAAAAAGGGACGUUUUUACGU
	 */	
	
	
	private static void bondingTest(String rna, String instructions)
	{
		Individual indiv = new Individual();
		indiv.runBondingTest(rna, instructions);
		
		ResultViewer.showResults(indiv);
	}
	
	private static void optimize(ArrayList<String> sequences)
	{
		GPRunner runner = new GPRunner(sequences);
		runner.generations = 2000;
		
		Individual best = runner.evolve();
		
		ResultViewer.showResults(best);
	}
	

	public static void main(String[] args)
	{
		ArrayList<String> sequences = new ArrayList<String>();
		sequences.add("GGGGGGGGGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCCCCC");
		sequences.add("AAAAAAAAAAAAAAAAAAAAAAAAUAUAUUUUUUUUUUUUUUU");
		optimize(sequences);
	}

}
