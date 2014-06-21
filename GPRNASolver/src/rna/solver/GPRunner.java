package rna.solver;

import rna.solver.linear.LinearRegister;
import rna.solver.linear.LinearIndividual;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import rna.solver.tree.TreeIndividual;

/**
 * Main GP handling class
 *
 * @author ruman
 *
 */
public class GPRunner
{
    public static final Random RANDOM = new Random();

    public int generations;

    public int populationSize;
    public int children;

    /**
     * Turniersekeltion Turniere
     */
    public int tournaments;

    public float recombProbability;
    public float mutateProbability;

    public LinkedList<Individual> population = new LinkedList<>();

    public ArrayList<String> sequences;

    /**
     * Data for plot
     *
     * @param rna
     */
    public LinkedList<Double> results_BestFitness = new LinkedList<>();

    public GPRunner(ArrayList<String> sequences)
    {
        this.sequences = sequences;

        this.generations = 1000;

        this.populationSize = 800;
        this.children = 400; // jeder elter erzeugt 2 Kinder

        this.tournaments = 10;

        recombProbability = 0.2f;
        mutateProbability = 0.05f;
    }

    public List<Individual> tournamentSelection()
    {
        LinkedList<Individual> results = new LinkedList<>();

        for (int i = 0; i < tournaments; i++)
        {
            results.add(population.get(GPRunner.RANDOM.nextInt(population
                    .size())));
        }

        Collections.sort(results);

        return results.subList(0, 2);
    }

    public void createInitialLinearPopulation(int registerCount,
            int adfCount,
            int adfRegisters,
            int adfParameters)
    {
        /**
         * Create inital population
         */
        for (int i = 0; i < populationSize; i++)
        {
            LinearIndividual indiv = new LinearIndividual();
            indiv.random(registerCount, adfCount, adfParameters, adfRegisters);

            population.add(indiv);
        }
    }
    
    public void createInitialTreePopulation(
            int adfCount,            
            int adfParameters)
    {
        /**
         * Create inital population
         */
        for (int i = 0; i < populationSize; i++)
        {
            TreeIndividual indiv = new TreeIndividual();
            indiv.random(adfCount, adfParameters);

            population.add(indiv);
        }
    }

    public Individual evolve()
    {
        if(population.size() == 0)
            throw new RuntimeException("Initialize Population, first!");

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
                indiv.fitness(sequences);
            }

            Individual best = population.getFirst();

            for (Individual indiv : population)
            {
                if (indiv.fitness < best.fitness)
                {
                    best = indiv;
                }
            }

            System.out.println("Best individual: " + best.fitness);

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

                Individual child1 =parents.get(0).copy();
                Individual child2 = parents.get(1).copy();

                child1.recombine(child2, recombProbability);

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
            indiv.fitness(sequences);
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
