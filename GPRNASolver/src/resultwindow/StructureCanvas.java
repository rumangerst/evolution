package resultwindow;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import rna.solver.Nucleotide;
import rna.solver.RNAField;

public class StructureCanvas extends Canvas
{
	private RNAField structure;
	
	public StructureCanvas(RNAField structure)
	{
		this.structure = structure;
	}
	
	private Point transformPoint(int x, int y)
	{
		return new Point(x * 16 + 16,y * 16 + 16);
	}
	
	/**
	 * Returns point, where to start with drawing
	 * @return
	 */
	private Point findStarterpoint()
	{
		Point p = new Point(structure.structure.length, structure.structure.length);
		
		for(int x = 0; x < structure.structure.length;x++)
		{
			for(int y = 0; y < structure.structure.length; y++)
			{
				Nucleotide nuc = structure.structure[x][y];
				
				if(nuc != null)
				{
					if(p.x > nuc.x)
						p.x = nuc.x;
					if(p.y > nuc.y)
						p.y = nuc.y;
				}
			}
		}
		
		return p;
	}
	
	@Override
	public void paint(Graphics g)
	{
		// TODO Auto-generated method stub
		super.paint(g);
		
		Graphics2D g2 = (Graphics2D)g;	
		
		Point start = findStarterpoint();
		Point trstart = transformPoint(start.x - 5, start.y - 5);
		
		g.translate(-trstart.x, -trstart.y);
		
		
		for(int x = 0; x < structure.structure.length;x++)
		{
			for(int y = 0; y < structure.structure.length; y++)
			{
				Nucleotide nuc = structure.structure[x][y];			
				
				Point pos = transformPoint(x, y);
				
				
				
				
				g.setColor(Color.lightGray);
				g.drawRect(pos.x, pos.y, 16, 16);
				
				if(nuc == structure.initial)
				{
					g.setColor(Color.GRAY);						
					g.fillRect(pos.x, pos.y, 16, 16);
				}
				
				if(nuc != null)
				{
					
					
					switch(nuc.type)
					{
					case A:
						
						g.setColor(Color.orange);
						g.fillOval(pos.x, pos.y, 16, 16);
						
						break;
					case C:
						
						g.setColor(Color.blue);
						g.fillOval(pos.x, pos.y, 16, 16);
						
						break;
					case U:
						
						g.setColor(Color.red);
						g.fillOval(pos.x, pos.y, 16, 16);
						
						break;
					case G:
						
						g.setColor(Color.green);
						g.fillOval(pos.x, pos.y, 16, 16);
						
						break;
					}
					
					
					//Draw bond
					if(nuc.isBond())
					{
						g.setColor(Color.DARK_GRAY);
						g2.setStroke(new BasicStroke(2));
						
						Point otherpos = transformPoint(nuc.bond.x, nuc.bond.y);
						
						g.drawLine(pos.x + 8, pos.y + 8, otherpos.x + 8, otherpos.y + 8);
						
						g2.setStroke(new BasicStroke(1));
					}
					
					//Draw curve 'bond'			
					if(nuc.previous != null)
					{
						g.setColor(Color.DARK_GRAY);
						Point otherpos = transformPoint(nuc.previous.x, nuc.previous.y);
						
						g.drawLine(pos.x + 8, pos.y + 8, otherpos.x + 8, otherpos.y + 8);
					}
					
					
					g.setColor(Color.BLACK);
					g.drawString(nuc.type.name(), pos.x + 3, pos.y + 12);
					
					
					
				}
				
			}
		}
	}
}