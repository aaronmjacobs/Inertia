package net.mcmiracom.inertia;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Displays the game's controlls
 * 
 * @author Aaron Jacobs
 */
public class ControlsDialog extends JDialog implements WindowListener {

	private static final long serialVersionUID = 1186852691431735302L;
	private final JPanel contentPanel = new JPanel();
	private GamePanel panel;

	/**
	 * Create the dialog.
	 */
	public ControlsDialog(GamePanel gPanel) {
		Globals.setWindowIcon(this);
		this.panel = gPanel;
		addWindowListener(this);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Inertia - Controls");
		setBounds(100, 100, 450, 330);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel label = new JLabel("<html><font size='12'>Inertia - Controls</font></html>");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(label, BorderLayout.NORTH);
		}
		{
			JLabel label = new JLabel("<html><font size='7'>Mouse: </font><font size='5'>Rotates the player's ship</font>\r\n<br><font size='7'>Left Mouse Button: </font><font size='5'>Fires lasers</font>\r\n<br><font size='7'>Spacebar: </font><font size='5'>Activates ship's thrusters</font>\r\n<br><font size='7'>D: </font><font size='5'>Toggles the display of debug lines</font></html>");
			contentPanel.add(label, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(panel.isPaused())
						{
							panel.unPause();
						}
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		if(panel.isPaused())
		{
			panel.unPause();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
