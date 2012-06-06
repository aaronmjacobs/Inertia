package net.mcmiracom.inertia;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Class representing a single laser
 * 
 * @author Aaron Jacobs
 */
public class Laser extends MovableObject
{
	protected static ArrayList<Laser> allLasers = new ArrayList<Laser>();

	protected double angle;
	private AffineTransform affineTransform;
	long startTime;
	private ControllableObject shooter;

	public Laser(ControllableObject shooter, double targetX, double targetY, double angle)
	{
		super();
		allLasers.add(this);
		velocity = new Vec2();

		setTexture(Globals.laser, true);

		this.shooter = shooter;
		this.angle = angle;

		setPosition(shooter.position.x + (40 * Math.cos(angle)), shooter.position.y + (40 * Math.sin(angle)));
		velocity.x = targetX - shooter.position.x;
		velocity.y = targetY - shooter.position.y;
		velocity.selfNormalize();
		velocity.multiplyOn(Globals.LASER_VELOCITY);
		velocity.x += shooter.velocity.x;
		velocity.y += shooter.velocity.y;

		startTime = System.currentTimeMillis();
	}

	/**
	 * Checks if the laser hit a (valid) target
	 * 
	 * @param other The target
	 * @return If the laser landed a hit
	 */
	public boolean checkHit(MovableObject other)
	{	
		return ((this.position.distance(other.position) < ((other.bounds.getWidth() / 2) + (other.bounds.getHeight() / 2)) / 2) && other != shooter);
	}

	/**
	 * @return If the laser is out of bounds of the level
	 */
	public boolean outOfBounds()
	{
		return (position.x < 1) || (position.x > Globals.levelSize - 1) || (position.y < 1) || (position.y > Globals.levelSize - 1);
	}

	@Override
	public void move()
	{
		position.addOn(velocity.multiply(Globals.timeStep));
		if(System.currentTimeMillis() - startTime > Globals.LASER_LIFE_TIME || outOfBounds())
		{
			delete();
			return;
		}

		Globals.grid.handleLaserCollisions(this);

		bounds.setFrame(position.x - (bounds.getWidth() / 2), position.y - (bounds.getHeight() / 2), bounds.getWidth(), bounds.getHeight());
	}

	@Override
	public void draw(Graphics2D canvas2D)
	{
		affineTransform = new AffineTransform();
		affineTransform.translate(bounds.getX() - Globals.camera.bounds.getX(), bounds.getY() - Globals.camera.bounds.getY()); // Translate into position
		affineTransform.translate(bounds.getWidth() / 2, bounds.getHeight() / 2); // Translate to the center of the Player
		affineTransform.rotate(angle); // Rotate the Player
		affineTransform.translate(-bounds.getWidth() / 2, -bounds.getHeight() / 2); // Translate back to the main position
		canvas2D.drawImage(texture, affineTransform, null);
	}

	@Override
	public void delete()
	{
		allLasers.remove(this);
	}
}
