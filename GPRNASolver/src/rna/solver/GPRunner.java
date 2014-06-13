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

	public int generations;

	public int populationSize;
	public int children;

	/**
	 * Turniersekeltion Turniere
	 */
	public int tournaments;

	public float recombProbability;
	public float mutateProbability;

	public int registerCount;
	public int adfCount;
	public int adfRegisters;
	public int adfParameters;

	public LinkedList<Individual> population = new LinkedList<>();

	/**
	 * Data for plot
	 * 
	 * @param rna
	 */
	public LinkedList<Double> results_BestFitness = new LinkedList<>();

	public GPRunner(String rna)
	{
		this.rna = rna;

		this.generations = 1000;

		this.populationSize = 800;
		this.children = 400; // jeder elter erzeugt 2 Kinder
		this.registerCount = 40;
		this.adfCount = 3;
		this.adfRegisters = registerCount / 2;
		this.adfParameters = 5;

		this.tournaments = 10;

		recombProbability = 0.2f;
		mutateProbability = 1.0f / this.registerCount;
	}

	public List<Individual> tournamentSelection()
	{
		LinkedList<Individual> results = new LinkedList<Individual>();

		for (int i = 0; i < tournaments; i++)
		{
			results.add(population.get(Register.RANDOM.nextInt(population
					.size())));
		}

		Collections.sort(results);

		return results.subList(0, 2);
	}

	public Individual evolve()
	{
		/**
		 * Create inital population
		 */
		for (int i = 0; i < populationSize; i++)
		{
			Individual indiv = new Individual();
			indiv.random(registerCount, adfCount, adfParameters, adfRegisters);

			population.add(indiv);
		}

		/**
		 * Evolution loop
		 */
		for (int generation = 0; generation < generations; generation++)
		{
			System.out.println("Generation " + (generation + 1));

			/**
			 * Run programs (will also calculate fitness)
			 */
			for (Individual indiv : population)
			{
				indiv.run(rna);
			}

			Individual best = population.getFirst();

			for (Individual indiv : population)
			{
				if (indiv.fitness < best.fitness)
				{
					best = indiv;
				}
			}

			System.out.println("Best individual: " + best.fitness + " energy: "
					+ best.energy);
			System.out.println("Last register of main output ("
					+ best.mainFunction.outputRegister
					+ "): "
					+ best.mainFunction.registers.get(
							best.mainFunction.outputRegister).toString());

			results_BestFitness.add(best.fitness);

			// /**
			// * Sort population by fitness
			// */
			// Collections.sort(population);
			//
			// // Just some text
			// System.out
			// .println("Best fitness: " + population.getFirst().fitness);

			// /**
			// * Select parents
			// */
			// List<Individual> parents = population.subList(0, this.parents -
			// 1);

			/**
			 * Create new population *
			 */
			LinkedList<Individual> newpop = new LinkedList<Individual>();

			/**
			 * OPTINAL: Plus strat selection type
			 * 
			 */
			// for(Individual indiv : parents)
			// {
			// newpop.add(new Individual(indiv));
			// }

			//
			// /**
			// * Make new children by pairwise recombining parents
			// */
			// while (!parents.isEmpty())
			// {
			// Individual father = parents.remove(0);
			//
			// for (Individual mother : parents)
			// {
			// for (int i = 0; i < children; i++)
			// {
			// Individual child1 = new Individual(father);
			// Individual child2 = new Individual(mother);
			//
			// Individual.recombine(child1, child2, recombProbability);
			// child1.mutate(mutateProbability);
			//
			// population.add(child1);
			// population.add(child2);
			// }
			// }
			// }

			for (int i = 0; i < children; i++)
			{
				List<Individual> parents = tournamentSelection();

				// Testweise Plus-Strategie
				// newpop.add( new Individual(parents.get(0)));
				// newpop.add( new Individual(parents.get(1)));

				Individual child1 = new Individual(parents.get(0));
				Individual child2 = new Individual(parents.get(1));

				Individual.recombine(child1, child2, recombProbability);

				child1.mutate(mutateProbability);
				child2.mutate(mutateProbability);

				newpop.add(child1);
				newpop.add(child2);
			}

			/**
			 * Population aktualisieren
			 */

			this.population = newpop;

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

		try
		{
			population.getFirst().write("best_individual.RNASLV");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return population.getFirst();

	}
}
