package rna.solver;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Main GP handling class
 * 
 * @author ruman
 * 
 */
public class GPRunner
{
	public String rna;

	public int populationSize;
	public int parents;
	public int children;

	public float recombProbability;
	public float paramMutateProbability;
	public float registerMutateProbability;

	public LinkedList<Individual> population = new LinkedList<>();

	public GPRunner(String rna)
	{
		this.rna = rna;

		this.populationSize = 400;
		this.children = 100;
		this.parents = 6;

		recombProbability = 0.2f;
		paramMutateProbability = 0.03f;
		registerMutateProbability = 0.05f;
	}

	public void evolve()
	{
		/**
		 * Create inital population
		 */
		for (int i = 0; i < populationSize; i++)
		{
			Individual indiv = new Individual();
			indiv.createRandomRegister();

			population.add(indiv);
		}

		/**
		 * Evolution loop
		 */
		for (int generation = 0; generation < 100; generation++)
		{
			System.out.println("Generation " + (generation + 1));

			/**
			 * Run programs (will also calculate fitness)
			 */
			for (Individual indiv : population)
			{
				indiv.run(rna);
			}

			/**
			 * Sort population by fitness
			 */
			Collections.sort(population);
			
			//Just some text
			System.out.println("Best fitness: " + population.getFirst().fitness);

			/**
			 * Select parents
			 */
			List<Individual> parents = population.subList(0, this.parents - 1);

			/**
			 * Create new population *
			 */
			LinkedList<Individual> newpop = new LinkedList<Individual>();
			
			/**
			 * OPTINAL: Plus strat selection type
			 * 
			 */
//			for(Individual indiv : parents)
//			{
//				newpop.add(new Individual(indiv));
//			}
			
			this.population = newpop;

			/**
			 * Make new children by pairwise recombining parents
			 */
			while (!parents.isEmpty())
			{
				Individual father = parents.remove(0);

				for (Individual mother : parents)
				{
					for (int i = 0; i < children; i++)
					{
						Individual child1 = new Individual(father);
						Individual child2 = new Individual(mother);

						Individual.recombine(child1, child2, recombProbability);
						child1.mutate(registerMutateProbability, paramMutateProbability);
						
						population.add(child1);
						population.add(child2);
					}
				}
			}

			
		}
		
		System.out.println("Evolution loop finished.");
		
		/**
		 * Auswertung
		 */
		
		/**
		 * Run programs (will also calculate fitness)
		 */
		for (Individual indiv : population)
		{
			indiv.run(rna);
		}

		/**
		 * Sort population by fitness
		 */
		Collections.sort(population);
		
		
		for(int i = 0; i < 10; i++)
		{
			try
			{
				population.get(i).write("best_individual_" + i + ".RNASLV");
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
