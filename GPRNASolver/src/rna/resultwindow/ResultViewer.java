package rna.resultwindow;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.BoxLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import rna.solver.Individual;
import rna.solver.linear.LinearFunction;
import rna.solver.linear.LinearIndividual;

public class ResultViewer extends JFrame implements ActionListener
{
	private BasePairCanvas basePairCanvas;
	private StructureCanvas structureCanvas;
	private TextArea code;

	private Individual currentIndividual;

	/**
	 * Launch the application.
	 */
	public static void showResults(Individual indiv)
	{
		try
		{
			for (String rna : indiv.sequences)
			{
				ResultViewer dialog = new ResultViewer();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);

				dialog.refreshData(indiv, rna);

				dialog.setExtendedState(dialog.getExtendedState()
						| JFrame.MAXIMIZED_BOTH);
			}
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
			JMenuBar menuBar = new JMenuBar();
			getContentPane().add(menuBar, BorderLayout.NORTH);
			{
				JMenu fileMenu = new JMenu("File");
				menuBar.add(fileMenu);
				JMenuItem fileSaveMenuButton = new JMenuItem("Save ...");
				fileMenu.add(fileSaveMenuButton);
				fileSaveMenuButton.setActionCommand("SAVE");
				{
					JMenuItem fileLoadMenuButton = new JMenuItem("Load ...");
					fileMenu.add(fileLoadMenuButton);
					fileLoadMenuButton.setActionCommand("LOAD");
					fileLoadMenuButton.addActionListener(this);
				}
				fileSaveMenuButton.addActionListener(this);
			}

		}
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

	private String formatCode(LinearFunction f, String name)
	{
		if (f == null)
			return "";

		StringBuilder str = new StringBuilder();

		str.append(name + "\n-----------------\n");

		str.append(f.toString());

		str.append("\n\n\n");

		return str.toString();
	}

	public void refreshData(Individual indiv, String rna)
	{
		currentIndividual = indiv;

		/**
		 * Create structure
		 * 
		 */
		indiv.run(rna);
		
		this.setTitle(String.format("Energy: %d, Fitness: %f",
				indiv.structure.energy(), indiv.fitness));
		
		

		basePairCanvas.setData(indiv.structure, rna);
		structureCanvas.setData(indiv.structure);
		
		code.setText(indiv.toString());
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (arg0.getActionCommand().equals("SAVE"))
		{
			if (currentIndividual != null)
			{
				JFileChooser dlg = new JFileChooser();
				dlg.setCurrentDirectory(new File(System.getProperty("user.dir")));
				dlg.setFileFilter(new FileNameExtensionFilter("RNASLV file",
						"RNASLV"));
				if (dlg.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
				{
					try
					{
						currentIndividual.write(dlg.getSelectedFile()
								.getAbsolutePath());
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		else if (arg0.getActionCommand().equals("LOAD"))
		{

			JFileChooser dlg = new JFileChooser();
			dlg.setCurrentDirectory(new File(System.getProperty("user.dir")));
			dlg.setFileFilter(new FileNameExtensionFilter("RNASLV file",
					"RNASLV"));
			if (dlg.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				// try
				// {
				// Individual indiv = Individual.load(dlg.getSelectedFile()
				// .getAbsolutePath());
				// refreshData(indiv);
				// }
				// catch (IOException e)
				// {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}

		}
	}
}
