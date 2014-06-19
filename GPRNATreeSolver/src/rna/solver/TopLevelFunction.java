package rna.solver;

import java.util.ArrayList;
import java.util.LinkedList;

public class TopLevelFunction extends Function
{
	public TopLevelFunction(String label, int inputs, ArrayList<TopLevelFunction> adfs)
	{
		super(label, inputs, adfs);
		
		/**
		 * One nested random function
		 */
		parameters.add(randomFunction(0));
	}
	
	public TopLevelFunction(TopLevelFunction toCopy)
	{
		super(toCopy);
	}
	
	@Override
	public int execute(Individual individual, int[] args)
	{
		return parameters.get(0).execute(individual, args);
	}
	
	@Override
	public LinkedList<Function> traverse(LinkedList<Function> container)
	{
		container.add(parameters.get(0));
		return container;
	}
	
	/**
	 * Mutation: Koza-Mutation: zufälligen Teilbaum austauschen
	 */
	public void mutate(float p)
	{
		if (1 - Function.RANDOM.nextFloat() <= p)
		{
			LinkedList<Function> subfunctions = traverse(new LinkedList<Function>());

			Function rand = subfunctions
					.get(RANDOM.nextInt(subfunctions.size()));

			if (rand.parent != null)
			{
				int exisisting = rand.parent.parameters.indexOf(rand);
				rand.parent.parameters.set(exisisting,
						rand.parent.randomFunction(0));
			}
			else
			{
				System.out.println("Root mutation");
			}
		}
	}

	/**
	 * Koza-Rekombination: Zwei züfällig gewähle TB tauschen
	 */
	public static void recombine(Function indiv1, Function indiv2)
	{
		LinkedList<Function> subfunctions1 = indiv1.traverse(new LinkedList<Function>());
		LinkedList<Function> subfunctions2 = indiv2.traverse(new LinkedList<Function>());

		Function rand1 = subfunctions1
				.get(RANDOM.nextInt(subfunctions1.size()));
		Function rand2 = subfunctions2
				.get(RANDOM.nextInt(subfunctions2.size()));

		if (rand1.parent != null && rand2.parent != null)
		{
			int exisisting1 = rand1.parent.parameters.indexOf(rand1);
			int exisisting2 = rand2.parent.parameters.indexOf(rand2);

			/**
			 * Tauschen
			 */
			Function p1 = rand1.parent;
			Function p2 = rand2.parent;

			p1.parameters.set(exisisting1, rand2);
			p2.parameters.set(exisisting2, rand1);

			rand1.parent = p2;
			rand2.parent = p1;
		}
	}
}
