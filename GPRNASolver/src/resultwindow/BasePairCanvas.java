package resultwindow;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import rna.solver.Nucleotide;
import rna.solver.RNAField;

public class BasePairCanvas extends Canvas
{
	private RNAField structure;
	private String rna;

	public BasePairCanvas(RNAField structure, String rna)
	{
		this.structure = structure;
		this.rna = rna;
	}

	private Point transformPoint(int x, int y)
	{
		return new Point(x * 16 + 4, y * 16 + 4);
	}

	@Override
	public void paint(Graphics g)
	{		
		// TODO Auto-generated method stub
		super.paint(g);

		Graphics2D g2 = (Graphics2D) g;

		for (int x = 1; x < rna.length() + 1; x++)
		{
			Point p = transformPoint(x, 0);

			g2.setColor(Color.LIGHT_GRAY);
			g2.drawRect(p.x, p.y, 16, 16);

			g2.setColor(Color.BLACK);
			g2.drawString(rna.charAt(x - 1) + "", p.x + 3, p.y + 12);
		}

		for (int y = 1; y < rna.length() + 1; y++)
		{
			{
				Point p = transformPoint(0, y);

				g2.setColor(Color.LIGHT_GRAY);
				g2.drawRect(p.x, p.y, 16, 16);

				g2.setColor(Color.BLACK);
				g2.drawString(rna.charAt(y - 1) + "", p.x + 3, p.y + 12);
			}

			for (int x = 1; x < rna.length() + 1; x++)
			{
				Point p = transformPoint(x, y);

				g2.setColor(Color.LIGHT_GRAY);
				g2.drawRect(p.x, p.y, 16, 16);

				if (x == y)
				{
					g2.setColor(Color.LIGHT_GRAY);
					g2.fillRect(p.x, p.y, 16, 16);
				}
				else
				{					
					Nucleotide n1 = structure.getByIndex(x - 1);
					Nucleotide n2 = structure.getByIndex(y - 1);
					
					if(n1 == null || n2  == null)
					{
						g2.setColor(Color.black);
						g2.fillRect(p.x, p.y, 16, 16);
						continue;
					}

					if (n1.isBond() && n2.isBond() && n1.bond == n2)
					{
						if (Nucleotide.calculateEnergy(n1, n2) < 0)
							g2.setColor(Color.BLUE);
						else
							g2.setColor(Color.red);
						
						g2.fillRect(p.x, p.y, 16, 16);
					}
				}
			}
		}
	}
}
