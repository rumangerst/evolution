package resultwindow;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import rna.solver.Individual;
import rna.solver.Nucleotide;
import rna.solver.RNAField;

public class ResultDialog extends Frame
{	
	
	public ResultDialog(Individual indiv)
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
		
		
		this.setTitle("Energy: " + indiv.structure.energy() + ", Fitness: " + indiv.fitness());
		this.add( new StructureCanvas(indiv.structure));
	}
	
	public static void showResults(Individual indiv)
	{
		ResultDialog dlg = new ResultDialog(indiv);
		
		dlg.setVisible(true);
	}
	
	
}
