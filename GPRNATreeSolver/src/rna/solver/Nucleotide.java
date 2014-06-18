package rna.solver;

public class Nucleotide
{
	/**
	 * Base
	 */
	public NucleotideType type;
	/**
	 * world direction of nucleotide
	 */
	public NucleotideDirection dir;

	/**
	 * Relative direction of this nucleotide
	 */
	public RelativeDirection reldir;

	/**
	 * Source nucleotide
	 */
	public Nucleotide previous;

	/**
	 * Next nucleotide
	 */
	public Nucleotide next;

	/**
	 * Nucleotide position
	 */
	public int x;
	/**
	 * Nucleotide position
	 */
	public int y;

	/**
	 * Bonded nucleotide
	 */
	public Nucleotide bond;

	public Nucleotide(NucleotideType type, NucleotideDirection dir,
			RelativeDirection reldir)
	{
		this.type = type;
		this.dir = dir;
		this.reldir = reldir;
		this.bond = null;
	}

	public boolean isBond()
	{
		return bond != null;
	}

	public void bondTo(Nucleotide nuc)
	{
		if (nuc == this)
			return;
		if (isBond())
			return;
		if (nuc.isBond())
			return;

		bond = nuc;
		nuc.bond = this;
	}

	public void unBond()
	{
		if (bond != null)
		{
			bond.bond = null;
			bond = null;
		}
	}

	/**
	 * Energy, this nucleotide will add to the molecule Low energy => optimum
	 * 
	 * @return
	 */
	public int energy(RNAField structure)
	{
		int energy = 0;
//		/**
//		 * Sterische Konflikte: Betrachte non-prev und non-next Nukleotide, die
//		 * in direkter Nachbarschaft liegen
//		 */
//		for (int x = this.x - 1; x <= this.x + 1; x++)
//		{
//			for (int y = this.y - 1; y <= this.y + 1; y++)
//			{
//				/**
//				 * Betrachte nur Richtungen, von denen sterische Konflikte ausgehen können
//				 * 
//				 */
//				if(this.dir.isDiagonal())
//				{
//					if(this.x == x || this.y == y)
//						continue;
//				}
//				else
//				{
//					if(this.x != x && this.y != y)
//						continue;
//				}
//				
//				/**
//				 * Lese anderes Nukleotid
//				 */
//				Nucleotide other = structure.get(x, y);
//				
//				if(other == null || other == this)
//					continue;
//				/**
//				 * Prüfe, ob anderes NICHT prev oder NEXT sind
//				 */
//				if (other != this.next
//						&& other != this.previous)
//				{
//					/**
//					 * Wenn der aktuelle gebunden ist ,dann prüfe, ob der andere NICHT der bond, bond.prev oder bond.next ist
//					 */
//					if (this.isBond())
//					{
//						if(other != this.bond && other != this.bond.previous && other != this.bond.next)
//						{
//							energy += 5;
//						}
//					}
//					else
//					{
//						energy += 5;
//					}
//				}
//			}
//		}

		/**
		 * Nukleotidenergie
		 */

		// Keine Bindung zählt +2
		if (!isBond())
		{
			energy += 2;
		}
		else
		{
			// Energiefunktionen
			energy += calculateEnergy(this.type, bond.type);
		}

		return energy;
	}

	public static int calculateEnergy(Nucleotide n1, Nucleotide n2)
	{
		return calculateEnergy(n1.type, n2.type);
	}

	/**
	 * Energiefunktionen nach Energieminimum-Methode
	 * 
	 * Major: A-U: -2 G-C: -3 G-U: -1
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	public static int calculateEnergy(NucleotideType n1, NucleotideType n2)
	{

		// Loop Bonding => Spannungen
		// if (isCurve)
		// return 2;

		// A - U: -2
		if (n1 == NucleotideType.A && n2 == NucleotideType.U
				|| n1 == NucleotideType.U && n2 == NucleotideType.A)
			return -2 * 2;
		// G - C: -3
		if (n1 == NucleotideType.G && n2 == NucleotideType.C
				|| n1 == NucleotideType.C && n2 == NucleotideType.G)
			return -3 * 2;

		// Wobble G - U: -1
		if (n1 == NucleotideType.G && n2 == NucleotideType.U
				|| n1 == NucleotideType.U && n2 == NucleotideType.G)
			return -1 * 2;
	
		return 2 * 2;
	}

	public Nucleotide getPrevious(int n)
	{
		if (n == 0 || previous == null)
			return this;

		return previous.getPrevious(n - 1);
	}

	/**
	 * Index in sequence
	 * 
	 * @return
	 */
	public int getIndex()
	{
		if (previous == null)
			return 0;

		return 1 + previous.getIndex();
	}
}
