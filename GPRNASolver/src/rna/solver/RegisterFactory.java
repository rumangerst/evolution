package rna.solver;

import rna.solver.linear.LinearRegister;
import rna.solver.linear.LinearFunction;

/**
 * Ein Factory, das Register herstellt
 * @author ruman
 *
 */
public class RegisterFactory
{
	String label;
	int parameters;
	
	public RegisterFactory(String label, int parameters)
	{
		this.label = label;
		this.parameters = parameters;
	}
	
	/**
	 * Erstellt ein neues zufälliges Register auf Basis der Funktion
	 * @param parent
	 * @return
	 */
	public LinearRegister createRegister(LinearFunction parent)
	{
		String[] params = new String[parameters];
		
		for(int i = 0; i < params.length;i++)
		{
			params[i] = parent.randomTerminal();
		}
		
		return new LinearRegister(label, params);
	}
}
