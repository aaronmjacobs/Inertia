package net.mcmiracom.inertia;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Class of global constants and static variables
 * 
 * @author Aaron Jacobs
 */
public class Globals
{
	protected static double timeStep = 1.0;
	protected static int panelWidth = 1280, panelHeight = 720;
	protected static int difficulty, gridQuality, levelSize;
	public static final int LOW_QUALITY = 600,
			MEDIUM_QUALITY = 1000,
			HIGH_QUALITY = 1500,
			SMALL_WORLD = 5000,
			MEDIUM_WORLD = 7000,
			LARGE_WORLD = 10000;
	protected static Camera camera = null;
	protected static Grid grid;

	protected static boolean gameOver = false;

	public static final Vec2 right = new Vec2(1.0, 0.0);

	public static final double TERMINAL_VEL = 1400.0,
			MOVEMENT_VEL = 175.0,
			FRICTION = 880.0,
			GRAVITY = 350.0,
			DEFAULT_MASS = 5.0,
			JUMP_VEL = 175.0,
			GRAVITY_ATTRACTION = 100000.0,
			THRUST_FORCE = 300.0,
			MAX_THRUST = 500.0,
			LASER_VELOCITY = 800.0;

	public static final int FRAMES_PER_SECOND = 60,
			EASY = 120,
			MEDIUM = 70,
			HARD = 10,
			LASER_LIFE_TIME = 1500,
			DEFAULT_HEALTH = 100,
			LASER_DAMAGE = 10;

	public static final String TEXTURE_FOLDER = "images/",
			LASER_EXPLOSION_FOLDER = "laser_explosion/",
			EXPLOSION_FOLDER = "explosion/";

	public static final int LASER_EXPLOSION_FRAMECOUNT = 17;
	public static BufferedImage[] laserExplosion;

	public static final int EXPLOSION_FRAMECOUNT = 17;
	public static BufferedImage[] explosion;

	public static BufferedImage background, midground, foreground;
	public static BufferedImage laser;

	/**
	 * Loads required game images
	 */
	public static void loadImages()
	{
		try
		{
			laserExplosion = new BufferedImage[LASER_EXPLOSION_FRAMECOUNT];
			for(int i = 0; i < LASER_EXPLOSION_FRAMECOUNT; ++i)
			{
				laserExplosion[i] = createCompatibleImage(ImageIO.read(GameObject.class.getClassLoader().getResource(TEXTURE_FOLDER + LASER_EXPLOSION_FOLDER + "laser_explosion" + i + ".png")));
			}

			explosion = new BufferedImage[EXPLOSION_FRAMECOUNT];
			for(int i = 0; i < LASER_EXPLOSION_FRAMECOUNT; ++i)
			{
				explosion[i] = createCompatibleImage(ImageIO.read(GameObject.class.getClassLoader().getResource(TEXTURE_FOLDER + EXPLOSION_FOLDER + "explosion" + i + ".png")));
			}

			background = createCompatibleImage(ImageIO.read(GameObject.class.getClassLoader().getResource(TEXTURE_FOLDER + "space_dark.jpg")));
			midground = createCompatibleImage(ImageIO.read(GameObject.class.getClassLoader().getResource(TEXTURE_FOLDER + "galaxy.png")));
			foreground = createCompatibleImage(ImageIO.read(GameObject.class.getClassLoader().getResource(TEXTURE_FOLDER + "clouds.png")));

			laser = createCompatibleImage(ImageIO.read(GamePanel.class.getClassLoader().getResource(Globals.TEXTURE_FOLDER + "laser.png")));

			background.setAccelerationPriority(1.0f);
			midground.setAccelerationPriority(1.0f);
			foreground.setAccelerationPriority(1.0f);
		}
		catch(IOException e)
		{
			//TODO
			e.printStackTrace();
		}
		GameObject.class.getClassLoader().getResource(Globals.TEXTURE_FOLDER + "space_dark.jpg");
	}

	private static GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	/**
	 * Creates an image of the same format as the screen (allowing it to be drawn more efficiently)
	 * 
	 * @param source
	 * @return A compatible BufferedImage
	 */
	public static BufferedImage createCompatibleImage(BufferedImage source)
	{
		BufferedImage newImage = gc.createCompatibleImage(source.getWidth(), source.getHeight(), source.getTransparency());
		Graphics2D g2d = (Graphics2D)newImage.getGraphics();
		g2d.drawImage(source, 0, 0, null);
		g2d.dispose();

		return newImage;
	}

	/**
	 * Sets the icon of a Window
	 * 
	 * @param window The Window to apply the icon to.
	 */
	public static void setWindowIcon(Window window)
	{
		ImageIcon icon = new ImageIcon(GamePanel.class.getClassLoader().getResource(Globals.TEXTURE_FOLDER + "meteoroid.png"));

		window.setIconImage(icon.getImage());
	}
}
