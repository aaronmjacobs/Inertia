package net.mcmiracom.inertia;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.JRadioButton;
import java.awt.FlowLayout;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * The game's launcher
 * 
 * @author Aaron Jacobs
 */
public class LauncherWindow extends JFrame {

	private static final long serialVersionUID = 1926033749031420939L;

	public static final String INSTRUCTIONS_PANEL = "Instructions Panel",
			CONTROLS_PANEL = "Controls Panel",
			LAUNCH_PANEL = "Launch Panel";

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public LauncherWindow() {
		Globals.setWindowIcon(this);
		setResizable(false);
		setTitle("Inertia");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		final Resolution[] resolutions = {new Resolution(1920, 1080), new Resolution(1680, 1050), new Resolution(1440, 900), new Resolution(1280, 720), new Resolution(1024, 768), new Resolution(800, 600)};

		final JPanel cardPanel = new JPanel();
		contentPane.add(cardPanel, BorderLayout.CENTER);
		cardPanel.setLayout(new CardLayout(0, 0));

		JPanel instructionsPanel = new JPanel();
		cardPanel.add(instructionsPanel, INSTRUCTIONS_PANEL);
		instructionsPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblInertia = new JLabel("<html><font size='12'>Inertia</font></html>");
		lblInertia.setHorizontalAlignment(SwingConstants.CENTER);
		instructionsPanel.add(lblInertia, BorderLayout.NORTH);

		JLabel lblInstructions = new JLabel("<html><font face='Arial' size='3'>Inertia is a 2D space shooter (reminiscent of the classic \"Asteroids\", though with a few twists). In Inertia, you control a space ship that is navigating a meteoroid field. Your goal is to eliminate the enemy ships that are hiding throughout the field. You have a laser, though your enemies do as well! Additionally, the meteoroids around you are extremely dense, and therefore cause a gravitational pull; do your best not to get pulled in! When the number of enemies remaining drops to 10 or less, a line will appear (if in range) pointing to the closest enemy in order to assist in finding the last few stragglers.<br><br>\r\nWhen starting Inertia, you will have a few options to choose from: resolution, difficulty, and quality.<br><br>\r\nResolution is the size of the game's window - please make sure you select a resolution that will fit on your screen.<br><br>\r\nThe difficulty setting changes how many enemies / meteroids there are, how much health you have, and how much damage you take.<br><br>\r\nThe quality setting controls the range of the physics calculations - higher qualities will result in more accurate calculations, but are more tolling on your computer's processor. If the game feels jumpy, try setting the quality to a lower setting.<br><br><br>\r\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Push \"Next\" to continue.</font></html>");
		lblInstructions.setVerticalAlignment(SwingConstants.TOP);
		instructionsPanel.add(lblInstructions, BorderLayout.CENTER);

		JPanel continuePanel = new JPanel();
		instructionsPanel.add(continuePanel, BorderLayout.SOUTH);

		JButton btnContinue = new JButton("Next");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CardLayout cl = (CardLayout)(cardPanel.getLayout());
				cl.show(cardPanel, CONTROLS_PANEL);
			}
		});
		continuePanel.add(btnContinue);

		JPanel controlsPanel = new JPanel();
		cardPanel.add(controlsPanel, CONTROLS_PANEL);
		controlsPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblinertiaControls = new JLabel("<html><font size='12'>Inertia - Controls</font></html>");
		lblinertiaControls.setHorizontalAlignment(SwingConstants.CENTER);
		controlsPanel.add(lblinertiaControls, BorderLayout.NORTH);

		JLabel lblControls = new JLabel("<html><font size='7'>Mouse: </font><font size='5'>Rotates the player's ship</font>\r\n<br><font size='7'>Left Mouse Button: </font><font size='5'>Fires lasers</font>\r\n<br><font size='7'>Spacebar: </font><font size='5'>Activates ship's thrusters</font>\r\n<br><font size='7'>D: </font><font size='5'>Toggles the display of debug lines</font></html>");
		controlsPanel.add(lblControls, BorderLayout.CENTER);

		JPanel secondContinuePanel = new JPanel();
		controlsPanel.add(secondContinuePanel, BorderLayout.SOUTH);

		JButton btnContinue_1 = new JButton("Next");
		btnContinue_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout)(cardPanel.getLayout());
				cl.show(cardPanel, LAUNCH_PANEL);
			}
		});

		JButton btnBack_1 = new JButton("Back");
		btnBack_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout)(cardPanel.getLayout());
				cl.show(cardPanel, INSTRUCTIONS_PANEL);
			}
		});
		secondContinuePanel.add(btnBack_1);
		secondContinuePanel.add(btnContinue_1);

		JPanel launchPanel = new JPanel();
		cardPanel.add(launchPanel, LAUNCH_PANEL);
		launchPanel.setLayout(new BorderLayout(0, 0));

		JPanel selectionsPanel = new JPanel();
		launchPanel.add(selectionsPanel, BorderLayout.CENTER);
		selectionsPanel.setLayout(new BoxLayout(selectionsPanel, BoxLayout.LINE_AXIS));

		JPanel resolutionPanel = new JPanel();
		selectionsPanel.add(resolutionPanel);
		resolutionPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Resolution", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		resolutionPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		resolutionPanel.add(scrollPane, BorderLayout.CENTER);
		final JList resolutionList = new JList(resolutions);
		resolutionList.setSelectedIndex(0);
		scrollPane.setViewportView(resolutionList);
		scrollPane.setPreferredSize(new Dimension(150, scrollPane.getPreferredSize().height));

		JPanel difficultyPanel = new JPanel();
		selectionsPanel.add(difficultyPanel);
		difficultyPanel.setBorder(new TitledBorder(null, "Difficulty", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		difficultyPanel.setLayout(new BorderLayout(0, 0));

		JPanel difficultyRadioButtonPanel = new JPanel();
		difficultyPanel.add(difficultyRadioButtonPanel, BorderLayout.CENTER);
		difficultyRadioButtonPanel.setLayout(new BoxLayout(difficultyRadioButtonPanel, BoxLayout.PAGE_AXIS));

		final JRadioButton rdbtnEasy = new JRadioButton("Easy");
		rdbtnEasy.setHorizontalAlignment(SwingConstants.LEFT);
		difficultyRadioButtonPanel.add(rdbtnEasy);

		final JRadioButton rdbtnMedium = new JRadioButton("Medium");
		difficultyRadioButtonPanel.add(rdbtnMedium);

		final JRadioButton rdbtnHard = new JRadioButton("Hard");
		difficultyRadioButtonPanel.add(rdbtnHard);

		final ButtonGroup difficultyGroup = new ButtonGroup();
		difficultyGroup.add(rdbtnEasy);
		difficultyGroup.add(rdbtnMedium);
		difficultyGroup.add(rdbtnHard);
		rdbtnEasy.setSelected(true); // Make easy default

		JPanel qualityPanel = new JPanel();
		selectionsPanel.add(qualityPanel);
		qualityPanel.setBorder(new TitledBorder(null, "Quality", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		qualityPanel.setLayout(new BorderLayout(0, 0));

		JPanel qualityRadioButtonPanel = new JPanel();
		qualityPanel.add(qualityRadioButtonPanel, BorderLayout.CENTER);
		qualityRadioButtonPanel.setLayout(new BoxLayout(qualityRadioButtonPanel, BoxLayout.PAGE_AXIS));

		final JRadioButton rdbtnHigh = new JRadioButton("High");
		qualityRadioButtonPanel.add(rdbtnHigh);

		final JRadioButton rdbtnMedium_1 = new JRadioButton("Medium");
		qualityRadioButtonPanel.add(rdbtnMedium_1);

		final JRadioButton rdbtnLow = new JRadioButton("Low");
		qualityRadioButtonPanel.add(rdbtnLow);

		final ButtonGroup qualityGroup = new ButtonGroup();
		qualityGroup.add(rdbtnHigh);
		qualityGroup.add(rdbtnMedium_1);
		qualityGroup.add(rdbtnLow);
		rdbtnMedium_1.setSelected(true); // Make medium default

		JPanel buttonPanel = new JPanel();
		launchPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnLaunch = new JButton("Launch");
		btnLaunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Resolution res = resolutions[resolutionList.getSelectedIndex()];
				Globals.panelWidth = res.getWidth();
				Globals.panelHeight = res.getHeight();

				if(rdbtnEasy.isSelected())
				{
					Globals.difficulty = Globals.EASY;
					Globals.levelSize = Globals.SMALL_WORLD;
					Player.maxHealth = Globals.DEFAULT_HEALTH * 5;
				}
				else if(rdbtnMedium.isSelected())
				{
					Globals.difficulty = Globals.MEDIUM;
					Globals.levelSize = Globals.MEDIUM_WORLD;
					Player.maxHealth = Globals.DEFAULT_HEALTH * 4;
				}
				else if(rdbtnHard.isSelected())
				{
					Globals.difficulty = Globals.HARD;
					Globals.levelSize = Globals.LARGE_WORLD;
					Player.maxHealth = Globals.DEFAULT_HEALTH * 3;
				}

				if(rdbtnHigh.isSelected())
				{
					Globals.gridQuality = Globals.HIGH_QUALITY;
				}
				else if(rdbtnMedium_1.isSelected())
				{
					Globals.gridQuality = Globals.MEDIUM_QUALITY;
				}
				else if(rdbtnLow.isSelected())
				{
					Globals.gridQuality = Globals.LOW_QUALITY;
				}

				EventQueue.invokeLater(new Runnable(){
					public void run()
					{
						new MainWindow();
					}
				});
				dispose();
			}
		});

		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout)(cardPanel.getLayout());
				cl.show(cardPanel, CONTROLS_PANEL);
			}
		});
		buttonPanel.add(btnBack);
		buttonPanel.add(btnLaunch);
	}

	/**
	 * Class that represents different screen resolutions
	 * 
	 * @author Aaron Jacobs
	 */
	private static class Resolution
	{
		private int width, height;

		public Resolution(int width, int height)
		{
			this.width = width;
			this.height = height;
		}

		public int getWidth()
		{
			return width;
		}

		public int getHeight()
		{
			return height;
		}

		public String toString()
		{
			return width + " x " + height;
		}
	}
}
