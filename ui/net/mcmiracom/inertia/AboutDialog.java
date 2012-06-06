package net.mcmiracom.inertia;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Displays an "About" dialog
 * 
 * @author Aaron Jacobs
 */
public class AboutDialog extends JDialog implements WindowListener {

	private static final long serialVersionUID = 468739229122060284L;
	private final JPanel contentPanel = new JPanel();
	private GamePanel panel;

	/**
	 * Create the dialog.
	 */
	public AboutDialog(GamePanel gPanel) {
		Globals.setWindowIcon(this);
		this.panel = gPanel;
		addWindowListener(this);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Inertia - About");
		setBounds(100, 100, 350, 250);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblinertiaAbout = new JLabel("<html><font size='12'>Inertia - About</font></html>");
			lblinertiaAbout.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblinertiaAbout, BorderLayout.NORTH);
		}
		{
			JLabel lblwrittenByAaron = new JLabel("<html><font size='5'><center>Written by Aaron Jacobs\r\n<br><br>Music composed by Anton Riehl<br>(used with permission)</center></html>");
			lblwrittenByAaron.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblwrittenByAaron, BorderLayout.CENTER);
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
