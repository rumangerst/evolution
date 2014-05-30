package resultwindow;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import rna.solver.Individual;
import rna.solver.Nucleotide;
import rna.solver.RNAField;

public class BasePairResultDialog extends Frame
{	
	
	public BasePairResultDialog(Individual indiv)
	{	
		this.setSize(1024, 768);
		this.setBackground(Color.WHITE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		
		this.setTitle("Basenpaarung | Energy: " + indiv.structure.energy() + ", Fitness: " + indiv.fitness());
		this.add( new BasePairCanvas(indiv.structure, indiv.rna));
	}
	
	public static void showResults(Individual indiv)
	{
		BasePairResultDialog dlg = new BasePairResultDialog(indiv);
		
		dlg.setVisible(true);
	}
	
	
}
