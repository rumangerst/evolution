package resultwindow;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import rna.solver.Function;
import rna.solver.Individual;

import javax.swing.JTabbedPane;

import java.awt.ScrollPane;

import javax.swing.BoxLayout;

import java.awt.TextArea;
import java.awt.Font;

public class ResultViewer extends JFrame
{
	private BasePairCanvas basePairCanvas;
	private StructureCanvas structureCanvas;
	private TextArea code;

	/**
	 * Launch the application.
	 */
	public static void showResults(Individual indiv)
	{
		try
		{
			ResultViewer dialog = new ResultViewer();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);

			dialog.refreshData(indiv);

			dialog.setExtendedState(dialog.getExtendedState()
					| JFrame.MAXIMIZED_BOTH);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ResultViewer()
	{
		this.basePairCanvas = new BasePairCanvas();
		this.structureCanvas = new StructureCanvas();

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			getContentPane().add(tabbedPane, BorderLayout.CENTER);
			{
				JPanel structurePanel = new JPanel();
				tabbedPane.addTab("Structure", null, structurePanel, null);
				structurePanel.setLayout(new BoxLayout(structurePanel,
						BoxLayout.X_AXIS));
				{
					ScrollPane scrollPane = new ScrollPane();

					scrollPane.add(structureCanvas);

					structurePanel.add(scrollPane);
				}
			}
			{
				JPanel basePairPanel = new JPanel();
				tabbedPane.addTab("Base Pairs", null, basePairPanel, null);
				basePairPanel.setLayout(new BoxLayout(basePairPanel,
						BoxLayout.X_AXIS));
				{
					ScrollPane scrollPane = new ScrollPane();

					scrollPane.add(basePairCanvas);

					basePairPanel.add(scrollPane);
				}
			}
			{
				JPanel codePanel = new JPanel();
				tabbedPane.addTab("Code", null, codePanel, null);
				codePanel.setLayout(new BoxLayout(codePanel, BoxLayout.X_AXIS));
				{
					code = new TextArea();
					code.setFont(new Font("Courier New", Font.PLAIN, 12));
					codePanel.add(code);
				}
			}
		}
	}

	private String formatCode(Function f, String name)
	{
		StringBuilder str = new StringBuilder();
		String[] commands = f.getRegisterCommands();
		
		str.append(name + "\n-----------------\n");

		for (int i = 0; i < commands.length; i++)
		{
			str.append("R" + i + ":\t" + commands[i] + "\n");
		}
		
		str.append("\n\n\n");

		return str.toString();
	}

	public void refreshData(Individual indiv)
	{
		this.setTitle(String.format("Energy: %d, Fitness: %f, %s",
				indiv.structure.energy(), indiv.fitness, indiv.type.name()));

		basePairCanvas.setData(indiv.structure, indiv.rna);
		structureCanvas.setData(indiv.structure);

		StringBuilder str = new StringBuilder();
		
		str.append(formatCode(indiv.mainFunction, "MAIN"));
		
		for(int i = 0; i < indiv.adfs.size(); i++)
		{
			str.append(formatCode(indiv.adfs.get(i), "ADF" + i));
		}
		
		code.setText(str.toString());
	}

}
