package rna.solver;

public enum RelativeDirection
{
	STRAIGHT, LEFT, RIGHT, UNDO;

	public NucleotideDirection toAbsolute(NucleotideDirection previousdir)
	{
		if (this == STRAIGHT)
			return previousdir;

		if (this == LEFT)
			return previousdir.rotateLeft();

		return previousdir.rotateRight();
	}

	/**
	 * Zyklus durch Integer bis -1000000, dann UNDO
	 * @param i
	 * @return
	 */
	public static RelativeDirection fromInteger(int i)
	{
		if( i <= -1000000)
			return UNDO;
		
		i += 1; // Shift nach Rechts
		i = Math.abs(i) % 3;

		if (i == 0)
			return LEFT;
		else if (i == 1)
			return STRAIGHT;
		else
			return RIGHT;
	}
}
