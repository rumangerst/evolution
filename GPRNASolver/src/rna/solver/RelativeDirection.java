package rna.solver;

public enum RelativeDirection
{
	STRAIGHT,
	LEFT,
	RIGHT;
	
	public NucleotideDirection toAbsolute(NucleotideDirection previousdir)
	{
		if(this == STRAIGHT)
			return previousdir;
		
		if(this == LEFT)
			return previousdir.rotateLeft();
		
		return previousdir.rotateRight();
	}
}
