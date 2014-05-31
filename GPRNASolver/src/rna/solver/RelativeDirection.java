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
	
	public static RelativeDirection fromInteger(int i)
	{
		i+=1; //Shift nach Rechts
		i = Math.abs(i) % 3;
		
		if(i == 0)
			return LEFT;
		else if(i == 1)
			return STRAIGHT;
		return RIGHT;
	}
}
