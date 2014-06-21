package rna.solver.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import rna.solver.GPRunner;

public class TreeTopLevelFunction extends TreeFunction
{
	public TreeTopLevelFunction(String label, int inputs, ArrayList<TreeTopLevelFunction> adfs)
	{
		super(label, inputs, adfs);
		
		/**
		 * One nested random function
		 */
		parameters.add(randomFunction(0));
	}
	
	public TreeTopLevelFunction(TreeTopLevelFunction toCopy)
	{
		super(toCopy);
	}
	
	@Override
	public int execute(TreeIndividual individual, int[] args)
	{
		return parameters.get(0).execute(individual, args);
	}
	
	@Override
	public LinkedList<TreeFunction> traverse(LinkedList<TreeFunction> container)
	{
		container.add(parameters.get(0));
		return container;
	}
	
	/**
	 * Mutation: Koza-Mutation: zufälligen Teilbaum austauschen
	 */
	public void mutate(float p)
	{
		if (1 - GPRunner.RANDOM.nextFloat() <= p)
		{
			LinkedList<TreeFunction> subfunctions = traverse(new LinkedList<TreeFunction>());

			TreeFunction rand = subfunctions
					.get(GPRunner.RANDOM.nextInt(subfunctions.size()));

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
	public static void recombine(TreeFunction indiv1, TreeFunction indiv2)
	{
		LinkedList<TreeFunction> subfunctions1 = indiv1.traverse(new LinkedList<TreeFunction>());
		LinkedList<TreeFunction> subfunctions2 = indiv2.traverse(new LinkedList<TreeFunction>());

		TreeFunction rand1 = subfunctions1
				.get(GPRunner.RANDOM.nextInt(subfunctions1.size()));
		TreeFunction rand2 = subfunctions2
				.get(GPRunner.RANDOM.nextInt(subfunctions2.size()));

		if (rand1.parent != null && rand2.parent != null)
		{
			int exisisting1 = rand1.parent.parameters.indexOf(rand1);
			int exisisting2 = rand2.parent.parameters.indexOf(rand2);

			/**
			 * Tauschen
			 */
			TreeFunction p1 = rand1.parent;
			TreeFunction p2 = rand2.parent;

			p1.parameters.set(exisisting1, rand2);
			p2.parameters.set(exisisting2, rand1);

			rand1.parent = p2;
			rand2.parent = p1;
		}
	}
}
