package net.mcmiracom.inertia;

/**
 * Object that is controllable, either by a player or AI
 * 
 * @author Aaron Jacobs
 */
public class ControllableObject extends PhysObject
{
	protected int health;

	public ControllableObject(double mass, int health)
	{
		super(mass);
		this.health = health;
		// TODO Auto-generated constructor stub
	}	

	/**
	 * Shoots a laser
	 * 
	 * @param targetX The x coordinate of the target
	 * @param targetY The y coordinate of the target
	 */
	protected void shoot(double targetX, double targetY)
	{
		new Laser(this, targetX, targetY, this.angle);
	}

	/**
	 * Shoots a laser
	 * 
	 * @param targetX The x coordinate of the target
	 * @param targetY The y coordinate of the target
	 * @param angle The angle of the laser
	 */
	protected void shoot(double targetX, double targetY, double angle)
	{
		new Laser(this, targetX, targetY, angle);
	}

	@Override
	protected boolean takeDamage(double value)
	{
		health -= value;
		if(health <= 0)
		{
			health = 0;
			delete();
			return true;
		}
		return false;
	}
}
