package net.mcmiracom.inertia;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import java.awt.FlowLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Window used for resetting the game
 * 
 * @author Aaron Jacobs
 */
public class ResetWindow extends JDialog implements WindowListener {

	private static final long serialVersionUID = 3957269922852316242L;
	private JPanel contentPane;
	public static final int NOT_OVER = 0,
			WIN = 1,
			LOSE = 2;

	/**
	 * Create the frame.
	 */
	public ResetWindow(final GamePanel panel, int result) {
		Globals.setWindowIcon(this);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Inertia");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 300, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		addWindowListener(this);

		final ButtonGroup difficultyGroup = new ButtonGroup();

		final ButtonGroup qualityGroup = new ButtonGroup();

		JPanel launchPanel = new JPanel();
		contentPane.add(launchPanel, BorderLayout.CENTER);
		launchPanel.setLayout(new BorderLayout(0, 0));

		JLabel winOrLoseLabel = new JLabel("");
		winOrLoseLabel.setHorizontalAlignment(SwingConstants.CENTER);
		launchPanel.add(winOrLoseLabel, BorderLayout.NORTH);
		switch(result)
		{
		case WIN:
			winOrLoseLabel.setText("<html><font size='12'>You Win!</font></html>");
			break;
		case LOSE:
			winOrLoseLabel.setText("<html><font size='12'>You Lose</font></html>");
			break;
		}

		JPanel selectionsPanel = new JPanel();
		launchPanel.add(selectionsPanel, BorderLayout.CENTER);
		selectionsPanel.setLayout(new BoxLayout(selectionsPanel, BoxLayout.LINE_AXIS));

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
						panel.resetStart();
					}
				});
				dispose();
			}
		});
		buttonPanel.add(btnLaunch);

		JButton btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		buttonPanel.add(btnQuit);
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}
