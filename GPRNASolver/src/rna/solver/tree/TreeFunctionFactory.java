package rna.solver.tree;

/**
 * Ein Factory, das Register herstellt
 * @author ruman
 *
 */
public class TreeFunctionFactory
{
	String label;
	int parameters;
	
	public TreeFunctionFactory(String label)
	{
		this.label = label;
		this.parameters = 0;
	}
	
	public TreeFunctionFactory(String label, int parameters)
	{
		this.label = label;
		this.parameters = parameters;
	}
	
	
	/**
	 * Erstellt ein neues zufälliges Register auf Basis der Funktion
	 * @param parent
	 * @return
	 */
	public TreeFunction createFunction(TreeFunction parent, int depth)
	{
		TreeFunction func = new TreeFunction(label, parent);
		for(int i = 0; i < parameters; i++)
		{
			func.parameters.add(parent.randomFunction(depth + 1));
		}
		
		return func;
	}
}
