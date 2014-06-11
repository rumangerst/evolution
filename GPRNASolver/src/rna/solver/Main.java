package rna.solver;

import java.io.IOException;

import com.panayotis.gnuplot.JavaPlot;

import resultwindow.ResultViewer;
import resultwindow.StatPlot;

public class Main
{
	
	/**
	 * Test mit einer sehr einfachen RNA
	 * 
	 * GGGGGGGGGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCCCCC
	 */
	private static void easyGCTest()
	{
		GPRunner runner = new GPRunner("GGGGGGGGGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCCCCC"); //6 C's fehlen am Ende
		
		/**
		 * Settings
		 */
		runner.registerCount = 20;
		runner.mutateProbability = 2.0f / 20.0f;
		runner.generations = 2000;
		
		
		
		ResultViewer.showResults(runner.evolve(-154, true));
		StatPlot.plotFitnessOverGeneration(runner.results_BestFitness);
	}
	
	/**
	 * Test mit einer sehr einfachen RNA
	 * 
	 * ACUCGGUUACGAG
	 */
	private static void easyHairPinTest()
	{
		GPRunner runner = new GPRunner("ACUCGGUUACGAG"); 
		
		/**
		 * Settings
		 */
		runner.registerCount = 20;
		runner.mutateProbability = 1.0f / 20.0f;
		//runner.recombProbability = 0.1f;
		
		runner.adfCount = 3;
		runner.adfParameters = 3;
		runner.adfRegisters = 20;
		
		runner.generations = 5000;
		
		ResultViewer.showResults(runner.evolve(Integer.MIN_VALUE, true));
		StatPlot.plotFitnessOverGeneration(runner.results_BestFitness);
	}
	
	/**
	 * Test mit einer sehr einfachen RNA
	 * 
	 * GCAUACCCAAGUGC
	 */
	private static void easyTopoplogyTest3()
	{
		GPRunner runner = new GPRunner("GAUGCAUACCCAAGUGCCUGA"); 
		
		/**
		 * Settings
		 */
		runner.registerCount = 10;
		runner.mutateProbability = 1.0f / 10.0f;
		
		ResultViewer.showResults(runner.evolve(Integer.MIN_VALUE, true));
		StatPlot.plotFitnessOverGeneration(runner.results_BestFitness);
	}
	
	/**
	 * Test mit einer bösen miRNA
	 * 
	 * UGUGUGGAUGAAAUGUAAUCACAGAACCGGUUUUCAUUUUCGAUCUGACUUAUUUUUUUCACAAACAAGUGAGAUCACUUUGAAAGCUGAUUUUGUACAAUUAAUUCAACG
	 */
	private static void evilTopoplogyTest1()
	{
		GPRunner runner = new GPRunner("UGUGUGGAUGAAAUGUAAUCACAGAACCGGUUUUCAUUUUCGAUCUGACUUAUUUUUUUCACAAACAAGUGAGAUCACUUUGAAAGCUGAUUUUGUACAAUUAAUUCAACG"); //Evil!
		
		/**
		 * Settings
		 */
		runner.registerCount = 40;
		runner.mutateProbability = 1.0f / 40.0f;
		
		runner.adfCount = 5;
		runner.adfParameters = 5;
		runner.adfRegisters = 20;
		
		ResultViewer.showResults(runner.evolve(Integer.MIN_VALUE, true));
		StatPlot.plotFitnessOverGeneration(runner.results_BestFitness);
	}
	
	/**
	 * Test mit einer bösen tRNA
	 * 
	 * GGGGGUAUAGCUCAGUUGGUAGAGCGCUGCCUUUGCACGGCAGAUGUCAGGGGUUCGAGUCCCCUUACCUCCA
	 */
	private static void evilTopoplogyTest2()
	{
		GPRunner runner = new GPRunner("GGGGGUAUAGCUCAGUUGGUAGAGCGCUGCCUUUGCACGGCAGAUGUCAGGGGUUCGAGUCCCCUUACCUCCA"); //Evil!
		
		/**
		 * Settings
		 */
		runner.generations = 1000;
		runner.registerCount = 200;
		runner.mutateProbability = 1.0f / 200.0f;
		
		runner.adfCount = 5;
		runner.adfParameters = 5;
		runner.adfRegisters = 20;
		
		ResultViewer.showResults(runner.evolve(Integer.MIN_VALUE, true));
		StatPlot.plotFitnessOverGeneration(runner.results_BestFitness);
	}
	 
	/**
	 * Test mit einer bösen Riboswitch
	 * 
	 * ACUCAUAUAAUCGCGUGGAUAUGGCACGCAAGUUUCUACCGGGCACCGUAAAUGUCCGACUAUGGGUG
	 */
	private static void evilTopoplogyTest3()
	{
		GPRunner runner = new GPRunner("ACUCAUAUAAUCGCGUGGAUAUGGCACGCAAGUUUCUACCGGGCACCGUAAAUGUCCGACUAUGGGUG"); //Evil!
		
		/**
		 * Settings
		 */
		runner.generations = 1000;
		runner.registerCount = 200;
		runner.mutateProbability = 1.0f / 200.0f;
		
		runner.adfCount = 5;
		runner.adfParameters = 5;
		runner.adfRegisters = 20;
		
		ResultViewer.showResults(runner.evolve(Integer.MIN_VALUE, true));
		StatPlot.plotFitnessOverGeneration(runner.results_BestFitness);
	}
	
	/**
	 * Test mit einer anderen bösen miRNA
	 * 
	 * UGGGAUGAGGUAGUAGGUUGUAUAGUUUUAGGGUCACACCCACCACUGGGAGAUAACUAUACAAUCUACUGUCUUUCCUA
	 */
	private static void evilTopoplogyTest4()
	{
		GPRunner runner = new GPRunner("UGGGAUGAGGUAGUAGGUUGUAUAGUUUUAGGGUCACACCCACCACUGGGAGAUAACUAUACAAUCUACUGUCUUUCCUA"); //Evil!
		
		/**
		 * Settings
		 */
		runner.registerCount = 40;
		runner.mutateProbability = 2.0f / 40.0f;
		
		runner.adfCount = 5;
		runner.adfParameters = 5;
		runner.adfRegisters = 20;
		
		ResultViewer.showResults(runner.evolve(Integer.MIN_VALUE, true));
		StatPlot.plotFitnessOverGeneration(runner.results_BestFitness);
	}
	
	/**
	 * Test mit einer RNA mit Pseudoknoten
	 * 
	 * ACGUCCCACGUAAAAAGGGACGUUUUUACGU
	 */
	private static void pseudoKnotTest1()
	{
		GPRunner runner = new GPRunner("ACGUCCCACGUAAAAAGGGACGUUUUUACGU"); //Evil!
		
		/**
		 * Settings
		 */
		runner.registerCount = 15;
		runner.mutateProbability = 1.0f / 15.0f;
		
		runner.adfCount = 2;
		runner.adfParameters = 3;
		runner.adfRegisters = 15;
		
		ResultViewer.showResults(runner.evolve(Integer.MIN_VALUE, true));
		StatPlot.plotFitnessOverGeneration(runner.results_BestFitness);
	}
	
	/**
	 *Testet RNA und gibt Result aus
	 */
	private static void test(String rna, int registers, float mutate, float recomb)
	{
		GPRunner runner = new GPRunner(rna);
		
		/**
		 * Settings
		 */
		runner.registerCount = registers;
		runner.mutateProbability = mutate;
		runner.recombProbability = recomb;
		
		ResultViewer.showResults(runner.evolve(Integer.MIN_VALUE, true));
		StatPlot.plotFitnessOverGeneration(runner.results_BestFitness);
	}
	
	
	private static void openResult(String filename)
	{
		try
		{
			Individual indiv = Individual.load(filename);
			
			/*StructureResultDialog.showResults(indiv);
			BasePairResultDialog.showResults(indiv);*/
			ResultViewer.showResults(indiv);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void bondingTest(String rna, String instructions)
	{
		Individual indiv = new Individual();
		indiv.runBondingTest(rna, instructions);
		
		ResultViewer.showResults(indiv);
	}

	public static void main(String[] args)
	{

		
//		
//		ind.createRandomRegister();
//		ind.run("UACACUGUGGAUCCGGUGAGGUAGUAGGUUGUAUAGUUUGGAAUAUUACCACCGGUGAACUAUGCAAUUUUCUACCUUACCGGAGACAGAACUCUUCGA");
//		//ind.run("GCGCGCGCGCCGCCCGC");
//		
//		//ind.printStructure();
//		System.out.println();
//		ind.printRegisters();
//		
//		System.out.println("Energie: " + ind.structure.energy());
//		
//		ResultDialog dlg = new ResultDialog(ind);
//		
//		dlg.setVisible(true);
		
//		GPRunner runner = new GPRunner("UACACUGUGGAUCCGGUGAGGUAGUAGGUUGUAUAGUUUGGAAUAUUACCACCGGUGAACUAUGCAAUUUUCUACCUUACCGGAGACAGAACUCUUCGA");
//		runner.evolve();
		
		//evilTopoplogyTest1();
		//evilTopoplogyTest2();
		//pseudoKnotTest1();
		//easyGCTest();
		//easyHairPinTest();
		//easyTopoplogyTest1();
		//easyTopoplogyTest1();
		
		//openResult("best_individual_0.RNASLV");
		
		//openResult("Results/GC-RNA/result1.RNASLV");
		
		//openResult("best_individual.RNASLV");
		//test();
		
		//bondingTest("AAAAAAAAAAAAAUUUUUUUUUUUUUUUUU", "SSSSSSSSLRRRRRRLSSSSSSSS");
		test("AAAAAAAAAAAAAUUUUUUUUUUUUUUUUU", 7, 1.0f / 7.0f, 0.1f);		
		
		//openResult("Results/GC-RNA/result3.RNASLV");
	}

}
