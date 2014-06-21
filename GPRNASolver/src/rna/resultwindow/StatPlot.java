package rna.resultwindow;

import java.util.LinkedList;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;

public class StatPlot
{
	private StatPlot()
	{
		
	}
	
	public static void plotFitnessOverGeneration(LinkedList<Double> data)
	{
		JavaPlot plot = new JavaPlot();
		
		
		/**
		 * Generate data
		 */
		PointDataSet<Double> plotdata = new PointDataSet<>();
		for(int i = 0; i < data.size();i++)
		{
			plotdata.addPoint((double)i, data.get(i));
		}
		
		plot.addPlot(plotdata);
		
		plot.plot();
	}
}
