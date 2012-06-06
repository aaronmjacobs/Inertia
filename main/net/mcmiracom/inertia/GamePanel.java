package net.mcmiracom.inertia;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

/**
 * Class that represents the JPanel in the window, and handles all game loop operations
 * 
 * @author Aaron Jacobs
 */
public class GamePanel extends JPanel
{	
	private static final long serialVersionUID = -527596709059080356L;
	private SimpleTimer physTimer;
	private Player ship;
	private World world;
	private Timer timer;

	private BufferedImage shipTexture, meteoroidTexture;
	private volatile boolean debug, showClosest, resetCalled, paused;
	private int numEnemies;

	public GamePanel()
	{
		super();

		paused = false;
		debug = false;
		resetCalled = false;
		numEnemies = 0;

		// JPanel settings
		setFocusable(true);
		setDoubleBuffered(true);

		// Listeners
		addKeyListener(new KeyboardHandler());
		MouseInput m = new MouseInput();
		addMouseListener(m);
		addMouseMotionListener(m);

		// Physics timer
		physTimer = new SimpleTimer();

		Globals.grid = new Grid(Globals.levelSize, Globals.levelSize, Globals.gridQuality);

		// Load files
		loadFiles();

		MusicPlayer.play(MusicPlayer.ANTON); //TODO
	}

	/**
	 * Starts the game
	 */
	public void start()
	{
		resetCalled = false;
		Globals.gameOver = false;

		generateMeteoroids(Globals.difficulty);
		generateEnemies();

		physTimer.start();

		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				update();
			}
		}, 0, 16); // 60 fps
	}

	/**
	 * Pauses the game
	 */
	public void pause()
	{
		paused = true;
		physTimer.stop();
		timer.cancel();
	}

	/**
	 * Unpauses the game
	 */
	public void unPause()
	{
		paused = false;
		physTimer.start();
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				update();
			}
		}, 0, 16); // 60 fps
	}

	/**
	 * @return If the game is paused
	 */
	public boolean isPaused()
	{
		return paused;
	}

	/**
	 * Game loop (run once per frame)
	 */
	public void update()
	{
		// Synchronized (to prevent concurrency issues)
		synchronized(physTimer)
		{
			/********** Calculate Time Step **********/
			Globals.timeStep = (physTimer.getTicks() / 1000.0);

			/********** Reset timer **********/
			physTimer.start();

			/********** Physics calculations **********/
			for(int i = 0; i < PhysObject.allPhysObjects.size(); ++i)
			{
				if(PhysObject.allPhysObjects.get(i).alive)
				{
					PhysObject.allPhysObjects.get(i).move();
				}
			}

			/********** Lasers **********/
			for(int i = 0; i < Laser.allLasers.size(); ++i)
			{
				Laser.allLasers.get(i).move();
			}
		}

		/********** Rendering **********/
		repaint();

		if(Globals.gameOver)
		{
			if(!resetCalled)
			{
				reset();
			}
		}
	}

	public void paintComponent(Graphics canvas)
	{
		// Synchronized (to prevent concurrency issues)
		synchronized(physTimer)
		{
			super.paintComponent(canvas);
			Graphics2D canvas2D = (Graphics2D)canvas;

			Globals.camera.centerOn(ship.bounds.getBounds2D(), world.bounds);

			/********** Draw Background **********/
			world.draw(canvas2D);

			int numCurrentEnemies = 0;
			/********** Draw All Physics Objects **********/
			for(int i = 0; i < PhysObject.allPhysObjects.size(); ++i)
			{
				if(PhysObject.allPhysObjects.get(i).alive)
				{
					PhysObject.allPhysObjects.get(i).draw(canvas2D);
					if(debug)
					{
						PhysObject.allPhysObjects.get(i).drawBounds(canvas2D);
						PhysObject.allPhysObjects.get(i).drawVelocity(canvas2D);
					}
					if(PhysObject.allPhysObjects.get(i) instanceof Enemy)
					{
						++numCurrentEnemies;
					}
				}
			}
			numEnemies = numCurrentEnemies;
			if(numEnemies == 0)
			{
				Globals.gameOver = true;
			}

			if(numEnemies <= 10)
			{
				showClosest = true;
			}
			else
			{
				showClosest = false;
			}

			/********** Draw All Game Objects **********/
			for(int i = 0; i < GameObject.allGameObjects.size(); ++i)
			{
				GameObject.allGameObjects.get(i).draw(canvas2D);
			}

			/********** Draw All Lasers **********/
			for(int i = 0; i < Laser.allLasers.size(); ++i)
			{
				Laser.allLasers.get(i).draw(canvas2D);
			}

			if(debug)
			{
				Globals.grid.drawGrid(canvas2D);
				Globals.grid.drawVisualAttraction(ship, canvas2D);
			}

			if(showClosest)
			{
				Globals.grid.drawClosestEnemy(ship, canvas2D);
			}

			canvas2D.setColor(Color.red);
			canvas2D.setFont(new Font("Arial", Font.PLAIN, 24));
			canvas2D.drawString("Health: " + ship.health + " / " + Player.maxHealth, 20, 30);
			canvas2D.drawString("Enemies Remaining: " + numEnemies, 20, 60);
		}
	}

	/**
	 * Resets the game
	 */
	protected void reset()
	{
		resetCalled = true;
		// Synchronized (to prevent concurrency issues)
		synchronized(physTimer)
		{
			timer.cancel(); // Stop the game loop

			ResetWindow window = null;
			if(Globals.gameOver)
			{
				if(numEnemies > 0)
				{
					window = new ResetWindow(this, ResetWindow.LOSE);
				}
				else
				{
					window = new ResetWindow(this, ResetWindow.WIN);
				}
			}
			else
			{
				window = new ResetWindow(this, ResetWindow.NOT_OVER);
			}
			window.setLocationRelativeTo(null);
			final ResetWindow resetWindow = window;

			EventQueue.invokeLater(new Runnable(){
				public void run()
				{
					resetWindow.setVisible(true);
				}
			});
		}
	}

	/**
	 * Called after setting the settings for a reset
	 */
	public void resetStart()
	{
		GameObject.allGameObjects = new ArrayList<GameObject>();
		PhysObject.allPhysObjects = new ArrayList<PhysObject>();

		world = new World();
		Globals.camera = new Camera(0.0, 0.0, Globals.panelWidth, Globals.panelHeight);
		Globals.grid = new Grid(Globals.levelSize, Globals.levelSize, Globals.gridQuality);

		ship = new Player(Globals.DEFAULT_MASS, Player.maxHealth);
		ship.setTexture(shipTexture, true);
		ship.setPosition((Globals.levelSize / 2) - (ship.bounds.getWidth() / 2), (Globals.levelSize / 2) - (ship.bounds.getHeight() / 2));

		start();
	}

	/**
	 * Generates a meteoroid field
	 * 
	 * @param frequency The frequency of the meteoroids in the field
	 */
	private void generateMeteoroids(int frequency)
	{
		int totalArea = Globals.levelSize;
		int numMeteoroids = totalArea / frequency;
		double xPos, yPos;

		for(int i = 0; i < numMeteoroids; ++i)
		{
			PhysObject meteoroid = new PhysObject(Globals.DEFAULT_MASS);
			do
			{
				xPos = Math.random() * Globals.levelSize;
				yPos = Math.random() * Globals.levelSize;
			}while(Math.abs(xPos - ship.position.x) < 200 || Math.abs(yPos - ship.position.y) < 200);
			meteoroid.setPosition(xPos, yPos);

			int sign;
			if(Math.random() > 0.5)
			{
				sign = 1;
			}
			else
			{
				sign = -1;
			}
			meteoroid.velocity.x = Math.random() * 300 * sign;
			if(Math.random() > 0.5)
			{
				sign = 1;
			}
			else
			{
				sign = -1;
			}
			meteoroid.velocity.y = Math.random() * 300 * sign;
			meteoroid.setTexture(meteoroidTexture, true);
		}
	}

	/**
	 * Generates the enemy ships
	 */
	private void generateEnemies()
	{
		int numEnemies = 15;
		switch(Globals.difficulty)
		{
		case Globals.EASY:
			numEnemies = 15;
			break;
		case Globals.MEDIUM:
			numEnemies = 30;
			break;
		case Globals.HARD:
			numEnemies = 60;
			break;
		}

		double xPos, yPos;

		for(int i = 0; i < numEnemies; ++i)
		{
			Enemy enemy = new Enemy(Globals.DEFAULT_MASS, Globals.DEFAULT_HEALTH / 2);
			enemy.setTexture(shipTexture, true);
			do
			{
				xPos = Math.random() * Globals.levelSize;
				yPos = Math.random() * Globals.levelSize;
			}while(Math.abs(xPos - ship.position.x) < 200 || Math.abs(yPos - ship.position.y) < 200);

			enemy.setPosition(xPos, yPos);
		}
	}

	/**
	 * Loads the required images
	 */
	private void loadFiles()
	{
		Globals.loadImages();

		try
		{
			shipTexture = Globals.createCompatibleImage(ImageIO.read(GamePanel.class.getClassLoader().getResource(Globals.TEXTURE_FOLDER + "ship.png")));
			meteoroidTexture = Globals.createCompatibleImage(ImageIO.read(GamePanel.class.getClassLoader().getResource(Globals.TEXTURE_FOLDER + "meteoroid.png")));
		}
		catch (IOException e)
		{
			shipTexture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); // Prevent null pointer
			meteoroidTexture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); // Prevent null pointer
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Loading Player Texture", JOptionPane.ERROR_MESSAGE);
		}

		Globals.camera = new Camera(0.0, 0.0, Globals.panelWidth, Globals.panelHeight);
		world = new World();
		ship = new Player(Globals.DEFAULT_MASS, Player.maxHealth);
		ship.setTexture(shipTexture, true);
		ship.setPosition((Globals.levelSize / 2) - (ship.bounds.getWidth() / 2), (Globals.levelSize / 2) - (ship.bounds.getHeight() / 2));

		double x = Math.random() * Globals.levelSize;
		double y = Math.random() * Globals.levelSize;
		if(x < 200)
		{
			x += 200;
		}
		if(x > Globals.levelSize - 200)
		{
			x -= 200;
		}
		if(y < 200)
		{
			y += 200;
		}
		if(y > Globals.levelSize - 200)
		{
			y -= 200;
		}
	}

	/**
	 * Handles keybaord input
	 * 
	 * @author Aaron Jacobs
	 */
	private class KeyboardHandler extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			// Synchronized (to prevent concurrency issues)
			synchronized(physTimer)
			{
				int keyCode = e.getKeyCode();
				if(ship != null)
				{
					ship.handleKeyboardInput(keyCode, true);
				}

				if(keyCode == KeyEvent.VK_D)
				{
					debug = !debug;
				}
			}
		}

		public void keyReleased(KeyEvent e)
		{
			// Synchronized (to prevent concurrency issues)
			synchronized(physTimer)
			{
				int keyCode = e.getKeyCode();
				if(ship != null)
				{
					ship.handleKeyboardInput(keyCode, false);
				}
			}
		}
	}

	/**
	 * Handles mouse input
	 * 
	 * @author Aaron Jacobs
	 */
	private class MouseInput extends MouseInputAdapter
	{
		public void mouseMoved(MouseEvent e)
		{
			// Synchronized (to prevent concurrency issues)
			synchronized(physTimer)
			{
				if(ship != null)
				{
					ship.handleMouseMotion(e.getX(), e.getY());
				}
			}
		}

		public void mousePressed(MouseEvent e)
		{
			// Synchronized (to prevent concurrency issues)
			synchronized(physTimer)
			{
				if(ship != null)
				{
					ship.handleMouseInput(e.getButton());
				}
			}
		}
	}
}
