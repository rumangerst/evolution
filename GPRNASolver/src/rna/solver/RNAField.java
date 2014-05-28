package rna.solver;

import java.awt.Point;

public class RNAField
{
	public Nucleotide[][] structure;

	public int structureLength;
	public Nucleotide current;
	public Nucleotide initial;

	public RNAField(int size)
	{
		structure = new Nucleotide[size][size];
		structureLength = 0;
	}

	public boolean occupied(int x, int y)
	{
		if (x < 0 || y < 0 || x >= structure.length || y >= structure.length)
			return true;

		return structure[x][y] != null;
	}
	
	/**
	 * Checks if adding a nucleotide would cross diagonal structure (evil shit!)
	 * @param currentx
	 * @param currenty
	 * @param dir
	 * @return
	 */
	public boolean crosses(int currentx, int currenty, NucleotideDirection dir)
	{
		Point pleft = NucleotideDirection.shiftByDir(currentx, currenty, dir.rotateLeft());
		Point pright = NucleotideDirection.shiftByDir(currentx, currenty, dir.rotateRight());
		
		Nucleotide nucleft = get(pleft.x, pleft.y);
		Nucleotide nucright = get(pright.x, pright.y);
		
		if(nucleft != null)
		{
			if(nucleft.dir.is90DegreeTo(dir))
				return true;
		}
		if(nucright != null)
		{
			if(nucright.dir.is90DegreeTo(dir))
				return true;
		}
		
		return false;
	}

	/**
	 * Checks if it's next to a bonded nucleotide or a curve
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean disturbsBonding(int xi, int yi)
	{
		if (xi < 0 || yi < 0 || xi >= structure.length || yi >= structure.length)
			return true;

		/*Nucleotide east = get(x + 1, y);
		Nucleotide west = get(x - 1, y);
		Nucleotide north = get(x, y - 1);
		Nucleotide south = get(x, y + 1);

		if (east != null && east.bond != null)
			return true;
		if (west != null && west.bond != null)
			return true;
		if (north != null && north.bond != null)
			return true;
		if (south != null && south.bond != null)
			return true;

		return false;*/
		
		Nucleotide nuc = get(xi, yi);
		
		if(nuc == null)
			return false;
		
		for(int x = nuc.x - 1; x <= nuc.x + 1; x++)
		{
			for(int y = nuc.y - 1; y <= nuc.y; y++)
			{
				Nucleotide other = get(x,y);
				
				if(other != null && other != nuc && other.isBond())
				{
					return true;
				}
			}
		}
		
		return false;
	}

	public Nucleotide get(int x, int y)
	{
		if (x < 0 || y < 0 || x >= structure.length || y >= structure.length)
			return null;

		return structure[x][y];
	}

	/**
	 * Sets initial nucleotide, algorithm will extend it!
	 * 
	 * @param x
	 * @param y
	 * @param type
	 * @param dir
	 */
	public void initial(int x, int y, NucleotideType type,
			NucleotideDirection dir)
	{
		Nucleotide n = new Nucleotide(type, dir, RelativeDirection.STRAIGHT);
		n.x = x;
		n.y = y;
		n.previous = null;

		this.initial = n;
		this.current = n;
		structureLength++;

		structure[x][y] = n;
	}

	/**
	 * Tries to append nucleotide to field
	 */
	public boolean append(RelativeDirection dir, NucleotideType type,
			boolean testOnly)
	{
		// calculate new x,y for the nucleotide
		NucleotideDirection ndir = dir.toAbsolute(current.dir);

		// Use dir to shift x,y
		// Note: For left/right, shift it also by current.dir
		Point cursor = NucleotideDirection.shiftByDir(current.x, current.y,
				ndir);

		// Test x,y
		if(occupied(cursor.x, cursor.y))
			return false;
		if(disturbsBonding(cursor.x, cursor.y))
			return false;
		if(ndir.isDiagonal() && crosses(current.x, current.y, ndir))// Diagonal directions may cross structures => prevent this!
			return false;
			

		if (testOnly)
		{
			return true;
		}

		// Create nucleotide
		Nucleotide nuc = new Nucleotide(type, ndir, dir);

		nuc.x = cursor.x;
		nuc.y = cursor.y;

		// Add to field (if not test)

		this.current.next = nuc;
		nuc.previous = this.current;

		this.current = nuc; // !Reihenfolge!

		structureLength++;

		structure[nuc.x][nuc.y] = nuc;

		// handle bonding:				
		bond(nuc);

		return true;
	}

	/**
	 * handles 'aligning' bonding mechanism; loop "bonding" is handled by
	 * append()
	 * 
	 * @param nuc
	 * @return
	 */
	public boolean bond(Nucleotide nuc)
	{
		// Bonding mechanism
		// Look for (unbound) nucleotide next to it with reversed direction
		// Bond each other to it
		Nucleotide best = null;
		int currentenergy = Integer.MAX_VALUE;

		for(int x = nuc.x - 1; x <= nuc.x + 1; x++)
		{
			for(int y = nuc.y - 1; y <= nuc.y; y++)
			{
				Nucleotide other = get(x,y);
				
				if(other != null && other != nuc && NucleotideDirection.difference(nuc.dir, other.dir.reverse()) <= 0)
				{
					if(best == null)
					{
						best = other;
					}
					else
					{
						int testedenergy = Nucleotide.calculateEnergy(best, nuc);
						int otherexisitingbond = Integer.MAX_VALUE;
						
						if(other.isBond())
						{
							otherexisitingbond = Nucleotide.calculateEnergy(other, other.bond);
						}
						
						if(testedenergy < otherexisitingbond && testedenergy < currentenergy)
						{
							best = other;
							currentenergy = testedenergy;
						}
					}
				}
			}
		}
		
		if(best != null)
		{			
			nuc.bondTo(best);
			
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Removes last nucleotide from structure
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean undo()
	{
		if (initial == current)
			return false;

		structure[current.x][current.y] = null;

		// Reset bonds
		current.unBond();

		// Reset lists
		this.current = current.previous;
		this.current.next = null;
		structureLength--;

		return true;
	}

	/**
	 * Sum of energy of each nucleotide
	 * 
	 * O(n^2)
	 * 
	 * @return
	 */
	public int energy()
	{
		int e = 0;

		for (int x = 0; x < structure.length; x++)
		{
			for (int y = 0; y < structure.length; y++)
			{
				Nucleotide n = structure[x][y];

				if (n != null)
					e += n.energy();
			}
		}

		return e;
	}
}
