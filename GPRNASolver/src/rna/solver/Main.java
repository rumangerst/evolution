package rna.solver;

import java.io.IOException;

import resultwindow.ResultDialog;

public class Main
{
	
	/**
	 * Test mit einer sehr einfachen RNA
	 * 
	 * GGGGGGGGGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCCCCC
	 */
	private static void easyTopoplogyTest1()
	{
		GPRunner runner = new GPRunner("GGGGGGGGGGGGGGGGGGGGGGGGCGCGCCCCCCCCCCCCCCC"); //6 C's fehlen am Ende
		runner.evolve();
		
		openResult("best_individual_0.RNASLV");
	}
	
	/**
	 * Test mit einer sehr einfachen RNA
	 * 
	 * ACUCGGUUACGAG
	 */
	private static void easyTopoplogyTest2()
	{
		GPRunner runner = new GPRunner("ACUCGGUUACGAG"); 
		runner.evolve();
		
		openResult("best_individual_0.RNASLV");
	}
	
	/**
	 * Test mit einer bösen miRNA
	 * 
	 * UGUGUGGAUGAAAUGUAAUCACAGAACCGGUUUUCAUUUUCGAUCUGACUUAUUUUUUUCACAAACAAGUGAGAUCACUUUGAAAGCUGAUUUUGUACAAUUAAUUCAACG
	 */
	private static void evilTopoplogyTest1()
	{
		GPRunner runner = new GPRunner("UGUGUGGAUGAAAUGUAAUCACAGAACCGGUUUUCAUUUUCGAUCUGACUUAUUUUUUUCACAAACAAGUGAGAUCACUUUGAAAGCUGAUUUUGUACAAUUAAUUCAACG"); //Evil!
		runner.evolve();
		
		openResult("best_individual_0.RNASLV");
	}
	
	/**
	 * Test mit einer bösen tRNA
	 * 
	 * GGGGGUAUAGCUCAGUUGGUAGAGCGCUGCCUUUGCACGGCAGAUGUCAGGGGUUCGAGUCCCCUUACCUCCA
	 */
	private static void evilTopoplogyTest2()
	{
		GPRunner runner = new GPRunner("GGGGGUAUAGCUCAGUUGGUAGAGCGCUGCCUUUGCACGGCAGAUGUCAGGGGUUCGAGUCCCCUUACCUCCA"); //Evil!
		runner.evolve();
		
		openResult("best_individual_0.RNASLV");
	}
	 
	/**
	 * Test mit einer bösen Riboswitch
	 * 
	 * ACUCAUAUAAUCGCGUGGAUAUGGCACGCAAGUUUCUACCGGGCACCGUAAAUGUCCGACUAUGGGUG
	 */
	private static void evilTopoplogyTest3()
	{
		GPRunner runner = new GPRunner("ACUCAUAUAAUCGCGUGGAUAUGGCACGCAAGUUUCUACCGGGCACCGUAAAUGUCCGACUAUGGGUG"); //Evil!
		runner.evolve();
		
		openResult("best_individual_0.RNASLV");
	}
	
	/**
	 * Test mit einer anderen bösen miRNA
	 * 
	 * UGGGAUGAGGUAGUAGGUUGUAUAGUUUUAGGGUCACACCCACCACUGGGAGAUAACUAUACAAUCUACUGUCUUUCCUA
	 */
	private static void evilTopoplogyTest4()
	{
		GPRunner runner = new GPRunner("UGGGAUGAGGUAGUAGGUUGUAUAGUUUUAGGGUCACACCCACCACUGGGAGAUAACUAUACAAUCUACUGUCUUUCCUA"); //Evil!
		runner.evolve();
		
		openResult("best_individual_0.RNASLV");
	}
	
	
	private static void openResult(String filename)
	{
		try
		{
			ResultDialog.showResults(Individual.load(filename));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Zur Überprüfung des BOND-Mechanismus
	 */
	private static void test()
	{
		Individual indiv = new Individual();
		
		indiv.registers.add(new Register("PUT_STRAIGHT"));
		indiv.registers.add(new Register("PUT_STRAIGHT"));
		indiv.registers.add(new Register("PUT_STRAIGHT"));
		indiv.registers.add(new Register("PUT_STRAIGHT"));
		indiv.registers.add(new Register("PUT_STRAIGHT"));
		indiv.registers.add(new Register("PUT_LEFT"));
		indiv.registers.add(new Register("PUT_RIGHT"));
		indiv.registers.add(new Register("PUT_RIGHT"));
		indiv.registers.add(new Register("PUT_RIGHT"));
		indiv.registers.add(new Register("PUT_RIGHT"));
		indiv.registers.add(new Register("PUT_RIGHT"));
		indiv.registers.add(new Register("PUT_RIGHT"));
		indiv.registers.add(new Register("PUT_LEFT"));
		indiv.registers.add(new Register("PUT_STRAIGHT"));
		indiv.registers.add(new Register("PUT_STRAIGHT"));
		
		
		//indiv.registers.add(new Register("PUT_RIGHT"));
		//indiv.registers.add(new Register("PUT_RIGHT"));
		
		indiv.run("AAAAAAAAAAAAAAA");
		
		ResultDialog.showResults(indiv);
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
		
		evilTopoplogyTest2();
		//easyTopoplogyTest2();
		
		//openResult("Results/Evil tRNA/Result5.RNASLV");
	}

}
