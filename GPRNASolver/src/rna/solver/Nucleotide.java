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

	public Nucleotide(NucleotideType type, NucleotideDirection dir, RelativeDirection reldir)
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
		if(nuc == this)
			return;
		if(isBond())
			return;		
		if(nuc.isBond())
			return;
		
		bond = nuc;
		nuc.bond = this;		
	}
	
	public void unBond()
	{
		if(bond != null)
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
	public int energy()
	{
		// Keine Bindung zählt +2
		if (bond == null)
			return 2;

		//Energiefunktionen
		return calculateEnergy(this.type, bond.type);
	}
	
	public static int calculateEnergy(Nucleotide n1, Nucleotide n2)
	{
		return calculateEnergy(n1.type, n2.type);
	}

	/**
	 * Energiefunktionen nach Energieminimum-Methode
	 * 
	 * Major:
	 * A-U: -2
	 * G-C: -3
	 * G-U: -1
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	public static int calculateEnergy(NucleotideType n1, NucleotideType n2)
	{	
		
		// Loop Bonding => Spannungen
//		if (isCurve)
//			return 2;

		// A - U: -2
		if (n1 == NucleotideType.A && n2 == NucleotideType.U
				|| n1 == NucleotideType.U && n2 == NucleotideType.A)
			return -4;
		// G - C: -3
		if (n1 == NucleotideType.G && n2 == NucleotideType.C
				|| n1 == NucleotideType.C && n2 == NucleotideType.G)
			return -6;
		
		//Wobble G - U: -1
		if (n1 == NucleotideType.G && n2 == NucleotideType.U
				|| n1 == NucleotideType.U && n2 == NucleotideType.G)
			return -2;

		//Strange 'bond' counts + 1, where no bond counts +2, because the nucleotide is element of a structure!
		return 1;
	}
	
	public Nucleotide getPrevious(int n)
	{
		if(n == 0 || previous== null)
			return this;
		
		return previous.getPrevious(n - 1);
	}
	
	/**
	 * Index in sequence
	 * @return
	 */
	public int getIndex()
	{
		if(previous== null)
			return 0;
		
		return 1 + previous.getIndex();
	}
}
