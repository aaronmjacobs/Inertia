package net.mcmiracom.inertia;

import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Main window (displays the game)
 * 
 * @author Aaron Jacobs
 */
public class MainWindow {

	private JFrame frmInertia;
	private boolean musicEnabled = true;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmInertia = new JFrame();
		frmInertia.setTitle("Inertia");
		frmInertia.setResizable(false);
		frmInertia.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Globals.setWindowIcon(frmInertia);

		final GamePanel graphicsPanel = new GamePanel();
		graphicsPanel.setPreferredSize(new Dimension(Globals.panelWidth, Globals.panelHeight));
		frmInertia.getContentPane().add(graphicsPanel);

		JMenuBar menuBar = new JMenuBar();
		frmInertia.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		JMenuItem mntmReset = new JMenuItem("Reset");
		mntmReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				graphicsPanel.reset();
			}
		});
		mnFile.add(mntmReset);
		mnFile.add(mntmExit);

		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);

		final JMenuItem mntmDisableMusic = new JMenuItem("Disable Music");
		mntmDisableMusic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(musicEnabled)
				{
					MusicPlayer.stop();
					mntmDisableMusic.setText("Enable Music");
				}
				else
				{
					MusicPlayer.play(MusicPlayer.ANTON);//TODO
					mntmDisableMusic.setText("Disable Music");
				}
				musicEnabled = !musicEnabled;
			}
		});
		mnOptions.add(mntmDisableMusic);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmControls = new JMenuItem("Controls");
		mntmControls.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final ControlsDialog dialog = new ControlsDialog(graphicsPanel);
				dialog.setLocationRelativeTo(null);
				EventQueue.invokeLater(new Runnable()
				{
					public void run()
					{
						graphicsPanel.pause();
						dialog.setVisible(true);
					}
				});
			}
		});
		mnHelp.add(mntmControls);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final AboutDialog dialog = new AboutDialog(graphicsPanel);
				dialog.setLocationRelativeTo(null);
				EventQueue.invokeLater(new Runnable()
				{
					public void run()
					{
						graphicsPanel.pause();
						dialog.setVisible(true);
					}
				});
			}
		});
		mnHelp.add(mntmAbout);
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				graphicsPanel.start();
				EventQueue.invokeLater(new Runnable(){
					public void run()
					{
						frmInertia.pack();
						frmInertia.setLocationRelativeTo(null);
						frmInertia.setVisible(true);
					}
				});
			}
		}, 300);
	}
}
