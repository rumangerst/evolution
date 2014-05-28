package rna.solver;

import java.awt.Point;

public enum NucleotideDirection
{
	NORTH, SOUTH, EAST, WEST, NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST;
	
	/**
	 * Returns if direction is diagonal
	 * @return
	 */
	public boolean isDiagonal()
	{
		switch (this)
		{	
		case NORTH_EAST:
			return true;
		case NORTH_WEST:
			return true;
		case SOUTH_EAST:
			return true;
		case SOUTH_WEST:
			return true;
		}

		return false;
	}
	
	public boolean is90DegreeTo(NucleotideDirection dir)
	{
		dir = dir.rotateLeft().rotateLeft();
		
		return dir == this || dir == this.reverse();
	}
	
	/**
	 * Reverses direction
	 * 
	 * @param dir
	 * @return
	 */
	public NucleotideDirection reverse()
	{
		switch (this)
		{
		case EAST:
			return WEST;
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case WEST:
			return EAST;
		case NORTH_EAST:
			return SOUTH_WEST;
		case NORTH_WEST:
			return SOUTH_EAST;
		case SOUTH_EAST:
			return NORTH_WEST;
		case SOUTH_WEST:
			return NORTH_EAST;
		}

		throw new RuntimeException();
	}
	
	public NucleotideDirection rotateLeft()
	{
		switch (this)
		{
		case EAST:
			return NORTH_EAST;
		case NORTH_EAST:
			return NORTH;
		case NORTH:
			return NORTH_WEST;
		case NORTH_WEST:
			return WEST;
		case WEST:
			return SOUTH_WEST;
		case SOUTH_WEST:
			return SOUTH;
		case SOUTH:
			return SOUTH_EAST;
		case SOUTH_EAST:
			return EAST;
		
		}

		throw new RuntimeException();
	}
	
	public NucleotideDirection rotateRight()
	{
		switch (this)
		{
		case EAST:
			return SOUTH_EAST;
		case NORTH_EAST:
			return EAST;
		case NORTH:
			return NORTH_EAST;
		case NORTH_WEST:
			return NORTH;
		case WEST:
			return NORTH_WEST;
		case SOUTH_WEST:
			return WEST;
		case SOUTH:
			return SOUTH_WEST;
		case SOUTH_EAST:
			return SOUTH;
		
		}

		throw new RuntimeException();
	}
	
	public static int difference(NucleotideDirection dir1, NucleotideDirection dir2)
	{
		int o1 = dir1.ordinal();
		int o2 = dir2.ordinal();
		
		return Math.abs(o1 - o2) % 8;
	}
	

	public static Point shiftByDir(int x, int y, NucleotideDirection dir)
	{
		Point p = new Point(x, y);

		switch (dir)
		{
		case NORTH:

			p.y--;
			break;
		case SOUTH:

			p.y++;
			break;
		case EAST:

			p.x++;
			break;
		case WEST:

			p.x--;
			break;
		case NORTH_EAST:

			p.x++;
			p.y--;
			break;

		case NORTH_WEST:

			p.x--;
			p.y--;
			break;

		case SOUTH_EAST:

			p.x++;
			p.y++;
			break;

		case SOUTH_WEST:

			p.x--;
			p.y++;
			break;
		}

		return p;
	}

}
