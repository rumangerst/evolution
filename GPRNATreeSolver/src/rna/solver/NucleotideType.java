package rna.solver;

public enum NucleotideType
{
	A, C, U, G;

	public static NucleotideType fromInteger(int i)
	{
		i = Math.abs(i) % 4; // Will cycle through ACUG

		switch (i)
		{
		case 0:
			return A;
		case 1:
			return C;
		case 2:
			return U;
		default:
			return G;
		}
	}

	public int toInteger()
	{
		switch (this)
		{
		case A:
			return 0;
		case C:
			return 1;
		case U:
			return 2;
		default:
			return 3;
		}
	}
}
