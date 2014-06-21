package rna.solver;

import java.util.ArrayList;

import resultwindow.ResultViewer;

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
		runner.generations = 4000;
		
		Individual best = runner.evolve();
		
		ResultViewer.showResults(best);
	}
	

	public static void main(String[] args)
	{
		ArrayList<String> sequences = new ArrayList<String>();
////		sequences.add("GGGGGGGGGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCCCCC");
////		sequences.add("AAAAAAAAAAAAAAAAAAAAAAAAUAUAUUUUUUUUUUUUUUU");
//		
////		sequences.add("GGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCC");
////		sequences.add("AAAAAAAAAAAAAAAAAUAUAUUUUUUUUUUUU");
//		
//		sequences.add("ACUCGGUUACGAG");
////		sequences.add("GAUGCAUACCCAAGUGCCUGA");
//                
//               // sequences.add("GGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCC");
//		
//		optimize(sequences);
            
            bondingTest("ACUCGGUUACGAG", "SSSLRRRRRRLSS");
	}

}
