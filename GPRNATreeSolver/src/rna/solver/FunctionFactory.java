package rna.solver;

/**
 * Ein Factory, das Register herstellt
 * @author ruman
 *
 */
public class FunctionFactory
{
	String label;
	int parameters;
	
	public FunctionFactory(String label)
	{
		this.label = label;
		this.parameters = 0;
	}
	
	public FunctionFactory(String label, int parameters)
	{
		this.label = label;
		this.parameters = parameters;
	}
	
	
	/**
	 * Erstellt ein neues zufälliges Register auf Basis der Funktion
	 * @param parent
	 * @return
	 */
	public Function createFunction(Function parent, int depth)
	{
		Function func = new Function(label, parent);
		for(int i = 0; i < parameters; i++)
		{
			func.parameters.add(parent.randomFunction(depth + 1));
		}
		
		return func;
	}
}
