package net.mcmiracom.inertia;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Enemy class (controlled by AI)
 * 
 * @author Aaron Jacobs
 */
public class Enemy extends ControllableObject
{
	private ArrayList<PhysObject> dangerousObjects;
	private Player player = null;
	private long cooldown, lastShotTime;

	public Enemy(double mass, int health)
	{
		super(mass, health);

		dangerousObjects = new ArrayList<PhysObject>();
		thrust = true;

		angle = 0;

		cooldown = 300;
		lastShotTime = System.currentTimeMillis();
	}

	@Override
	protected void handleThrust()
	{
		avoidMeteors();

		if(player != null)
		{
			attackPlayer();
		}
	}

	/**
	 * Uses vector math to try to avoid meteors
	 */
	private void avoidMeteors()
	{
		Vec2 awayFromObject = null;
		Vec2 escape = new Vec2();
		for(int i = 0; i < dangerousObjects.size(); ++i)
		{
			awayFromObject = this.position.subtract(dangerousObjects.get(i).position).normalize();

			// If the "away" y velocity will head toward the object, flip it
			if(dangerousObjects.get(i).position.y < this.position.y && awayFromObject.y < 0
					|| dangerousObjects.get(i).position.y > this.position.y && awayFromObject.y > 0)
			{
				awayFromObject.y = -awayFromObject.y;
			}
			// If the "away" x velocity will head toward the object, flip it
			if(dangerousObjects.get(i).position.x < this.position.x && awayFromObject.x < 0
					|| dangerousObjects.get(i).position.x > this.position.x && awayFromObject.x > 0)
			{
				awayFromObject.x = -awayFromObject.x;
			}

			double escapeThrust = Globals.THRUST_FORCE / (this.position.distance(dangerousObjects.get(i).position) * .01);
			if(escapeThrust > Globals.THRUST_FORCE)
			{
				escapeThrust = Globals.THRUST_FORCE;
			}
			awayFromObject.selfNormalize();
			awayFromObject.multiplyOn(escapeThrust);

			escape.addOn(awayFromObject);
		}
		escape.selfNormalize();
		escape.multiplyOn(Globals.THRUST_FORCE);

		if(awayFromObject != null)
		{
			dangerousObjects.clear();
			acceleration.addOn(escape);
		}
	}

	/**
	 * Attacks the player (if in range and in sight)
	 */
	private void attackPlayer()
	{
		double distance = this.position.distance(player.position);
		if(distance < 2)
		{
			distance = 2;
		}
		double thresholdAngle = (1 / Math.log10(distance)) * 2;

		Vec2 anticipatedPlayerPosition = player.position.add(new Vec2(player.velocity.x * (distance / 20) * Globals.timeStep, player.velocity.y * (distance / 20) * Globals.timeStep));
		Vec2 toAnticipatedPlayer = anticipatedPlayerPosition.subtract(this.position);

		Vec2 facing = new Vec2(Math.cos(angle), Math.sin(angle));

		// If we are looking at the player
		if(facing.angle(toAnticipatedPlayer) < thresholdAngle && distance < 700)
		{
			if(System.currentTimeMillis() - lastShotTime > cooldown)
			{
				double laserAngle = Math.acos(toAnticipatedPlayer.normalize().dot(Globals.right));
				if(toAnticipatedPlayer.y < 0)
				{
					laserAngle = -laserAngle;
				}
				shoot(anticipatedPlayerPosition.x, anticipatedPlayerPosition.y, laserAngle);
				lastShotTime = System.currentTimeMillis();
			}
		}
	}

	@Override
	protected void handleAllCollisions()
	{
		checkBounds();

		Globals.grid.handlePhysicsCollisions(this);
		Globals.grid.handleBodyCollisions(this);
	}

	@Override
	protected int handleCollisions(PhysObject other)
	{
		int code = super.handleCollisions(other);

		if(other instanceof Player)
		{
			player = (Player)other;
		}

		return code;
	}

	@Override
	protected int handleBodyCollision(PhysObject other)
	{
		double distance = this.position.distance(other.position);
		if(distance < 2)
		{
			distance = 2;
		}
		double thresholdAngle = (1 / Math.log10(distance)) * 2;

		// If the other object is within 500 pixels of us, and they (or we) are on a collision course
		if(distance < 500 && ((other.velocity.angle(this.position.subtract(other.position)) < thresholdAngle) || (this.velocity.angle(other.position.subtract(this.position)) < thresholdAngle)))
		{
			dangerousObjects.add(other);
		}

		// Don't do any absorptions
		return PhysObject.NO_EVENT;
	}

	@Override
	public void draw(Graphics2D canvas2D)
	{
		// Don't draw if off screen
		if((position.x - (bounds.getWidth() / 2)) - Globals.camera.bounds.getX() > Globals.panelWidth
				|| position.x + (bounds.getWidth() / 2) - Globals.camera.bounds.getX() < 0
				|| position.y - (bounds.getHeight() / 2) - Globals.camera.bounds.getY() > Globals.panelHeight
				|| position.y + (bounds.getHeight() / 2) - Globals.camera.bounds.getY() < 0)
		{
			return;
		}

		if(velocity.magnitude() > 10)
		{
			angle = Math.acos(velocity.normalize().dot(Globals.right));
			if(velocity.y < 0)
			{
				angle = -angle;
			}
		}

		affineTransform = new AffineTransform();
		affineTransform.translate(bounds.getX() - Globals.camera.bounds.getX(), bounds.getY() - Globals.camera.bounds.getY()); // Translate into position
		affineTransform.translate(bounds.getWidth() / 2, bounds.getHeight() / 2); // Translate to the center of the enemy
		affineTransform.rotate(angle); // Rotate the enemy
		affineTransform.translate(-bounds.getWidth() / 2, -bounds.getHeight() / 2); // Translate back to the main position

		canvas2D.drawImage(texture, affineTransform, null);

		//drawVelocity(canvas2D);
	}
}
