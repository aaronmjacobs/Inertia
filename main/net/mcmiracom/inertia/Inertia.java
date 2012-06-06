package net.mcmiracom.inertia;

import javax.swing.JDialog;
import javax.swing.UIManager;

/**
 * Main class
 * 
 * @author Aaron Jacobs
 */
public class Inertia
{
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		LauncherWindow window = new LauncherWindow();
		window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
}
