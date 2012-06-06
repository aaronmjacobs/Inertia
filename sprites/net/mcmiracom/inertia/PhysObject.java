package net.mcmiracom.inertia;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Object that is affected by physics (acceleration, mass, etc.)
 * 
 * @author Aaron Jacobs
 */
public class PhysObject extends MovableObject
{
	public static final int NO_EVENT = 0,
			CALLING_ITEM_ABSORBED = 1,
			OTHER_ITEM_ABSORBED = -1;

	protected static ArrayList<PhysObject> allPhysObjects = new ArrayList<PhysObject>();

	protected Vec2 acceleration;
	protected double mass;
	protected double angle;
	protected boolean thrust;
	protected AffineTransform affineTransform;
	protected boolean alive;

	public PhysObject(double mass)
	{
		super();

		alive = true;
		this.mass = mass;

		velocity = new Vec2(0.0, 0.0);
		acceleration = new Vec2(0.0, Globals.GRAVITY);

		angle = 0.0;
		thrust = false;

		affineTransform = new AffineTransform();

		checkBounds();

		allPhysObjects.add(this);
		Globals.grid.place(this);
	}

	@Override
	public void move()
	{
		// Prevents items from being placed into non-existent GridNodes
		checkBounds();

		Globals.grid.place(this);

		acceleration.x = 0;
		acceleration.y = 0;

		// Calculate the local attractions (based on our grid position)
		Vec2 gravitationalAttraction = Globals.grid.calculateAttraction(this);

		acceleration.addOn(gravitationalAttraction);

		if(thrust)
		{
			handleThrust();
		}

		// Add acceleration
		velocity.addOn(acceleration.multiply(Globals.timeStep));

		// Terminal velocity
		checkTerminalVelocity();

		// Handle collisions
		handleAllCollisions();

		// If the object is no longer alive, do not move it
		if(!alive)
		{
			return;
		}

		// Add velocity
		position.addOn(velocity.multiply(Globals.timeStep));

		checkBounds();

		// Position
		bounds.setFrame(position.x - (bounds.getWidth() / 2), position.y - (bounds.getHeight() / 2), bounds.getWidth(), bounds.getHeight());
	}

	/**
	 * Calculates an attraction vector to the other object
	 * 
	 * @param other The object to calculate the vector from
	 * @return An attraction vector from the other object
	 */
	protected Vec2 calculateAttraction(PhysObject other)
	{
		// Force of gravity = (G*m1*m2)/(distance^2)
		double distanceSquared = (((this.position.x - other.position.x) * (this.position.x - other.position.x)) + ((this.position.y - other.position.y) * (this.position.y - other.position.y)));
		if(distanceSquared < 100.0)
		{
			distanceSquared = 100.0;
		}
		double force = (Globals.GRAVITY_ATTRACTION * this.mass * other.mass) / distanceSquared;

		// f = m * a, a = f / m
		double accelerationMagnitude = force / mass;

		// Generate a vector in the direction of the other object
		Vec2 attraction = other.position.subtract(this.position);

		// Normalize the vector (so we just get the direction);
		attraction.selfNormalize();

		// Apply the calculated magnitude
		attraction.multiplyOn(accelerationMagnitude);

		return attraction;
	}

	protected void handleThrust()
	{
		// Do nothing
	}

	/**
	 * Handles collisions of absorbing bodies
	 * 
	 * @param other The object to handle collision against
	 * @return The result of the check (either NO_EVENT, CALLING_ITEM_ABSORBED, or OTHER_ITEM_ABSORBED)
	 */
	protected int handleBodyCollision(PhysObject other)
	{
		int returnCode = NO_EVENT;

		// Calculate our new position vector
		Vec2 newPosition = position.add(velocity.multiply(Globals.timeStep));

		// If the new coords cause a collision
		if(this.circularIntersects(other, newPosition))
		{
			// Generate the new mass, and the new (weighted) position, velocity, and acceleration
			double newMass = this.mass + other.mass;
			Vec2 combinedPosition = (this.position.multiply(this.mass).add(other.position.multiply(other.mass))).multiply(1.0 / newMass);
			Vec2 combinedVelocity = (this.velocity.multiply(this.mass).add(other.velocity.multiply(other.mass))).multiply(1.0 / newMass);
			Vec2 combinedAcceleration = (this.acceleration.multiply(this.mass).add(other.acceleration.multiply(other.mass))).multiply(1.0 / newMass);

			// Whichever object has the greater mass will absorb the other object
			if(this.mass > other.mass)
			{
				this.position = combinedPosition;
				this.velocity = combinedVelocity;
				this.acceleration = combinedAcceleration;
				this.scale(this.mass, newMass);
				this.mass = newMass;
				returnCode = OTHER_ITEM_ABSORBED;
			}
			else if(this.mass == other.mass) // If they have equal masses
			{
				if(other.getClass() == Player.class) // If the other is a Player, don't let them be absorbed
				{
					other.position = combinedPosition;
					other.velocity = combinedVelocity;
					other.acceleration = combinedAcceleration;
					other.scale(other.mass, newMass);
					other.mass = newMass;
					returnCode = CALLING_ITEM_ABSORBED;
				}
				else
				{
					this.position = combinedPosition;
					this.velocity = combinedVelocity;
					this.acceleration = combinedAcceleration;
					this.scale(this.mass, newMass);
					this.mass = newMass;
					returnCode = OTHER_ITEM_ABSORBED;
				}
			}
			else
			{
				other.position = combinedPosition;
				other.velocity = combinedVelocity;
				other.acceleration = combinedAcceleration;
				other.scale(other.mass, newMass);
				other.mass = newMass;
				returnCode = CALLING_ITEM_ABSORBED;
			}
		}

		return returnCode;
	}

	/**
	 * Handles all collisions for the calling object
	 */
	protected void handleAllCollisions()
	{
		checkBounds();
		// Collisions with movable objects

		Globals.grid.handleBodyCollisions(this);
	}

	/**
	 * Keeps the calling object in the bounds of the world
	 */
	protected void checkBounds()
	{
		// Edge collisions
		if(position.x < 1)
		{
			position.x = 1;
			velocity.x = -velocity.x / 4;
			acceleration.x = 0;
		}
		if(position.x > Globals.levelSize - 1)
		{
			position.x = Globals.levelSize - 1;
			velocity.x = -velocity.x / 4;
			acceleration.x = 0;
		}
		if(position.y < 1)
		{
			position.y = 1;
			velocity.y = -velocity.y / 4;
			acceleration.y = 0;
		}
		if(position.y > Globals.levelSize - 1)
		{
			position.y = Globals.levelSize - 1;
			velocity.y = -velocity.y / 4;
			acceleration.y = 0;
		}
	}

	/**
	 * Keeps the calling object from going over terminal velocity
	 */
	protected void checkTerminalVelocity()
	{
		if(velocity.x > Globals.TERMINAL_VEL)
		{
			velocity.x = Globals.TERMINAL_VEL;
		}
		else if(velocity.x < -Globals.TERMINAL_VEL)
		{
			velocity.x = -Globals.TERMINAL_VEL;
		}
		if(velocity.y > Globals.TERMINAL_VEL)
		{
			velocity.y = Globals.TERMINAL_VEL;
		}
		else if(velocity.y < -Globals.TERMINAL_VEL)
		{
			velocity.y = -Globals.TERMINAL_VEL;
		}
	}

	/**
	 * Handles hard physical collisions
	 * 
	 * @param other The object to check collisions against
	 * @return 1 if there was a collision, -1 otherwise
	 */
	protected int handleCollisions(PhysObject other)
	{
		int code = -1;

		Vec2 newPosition = position.add(velocity.multiply(Globals.timeStep));

		// If the new coords cause a collision
		if(this.circularIntersects(other, newPosition))
		{
			code = 1;
			Vec2 fromOther = this.position.subtract(other.position);
			double massRatio = other.mass / this.mass;

			// If they were to our left and heading to the right
			// or if they were to our right and heading to the left
			if((fromOther.x > 0 && other.velocity.x > 0)
					|| (fromOther.x < 0 && other.velocity.x < 0))
			{
				// Add their x velocity to ours

				// If we are moving in the same direction, don't apply the ratio
				if((this.velocity.x > 0 && other.velocity.x > 0)
						|| (this.velocity.x < 0 && other.velocity.x < 0))
				{
					this.velocity.x += other.velocity.x;
				}
				else
					// If we are moving in different directions, apply the ratio
				{
					this.velocity.x += other.velocity.x * massRatio;
				}
			}
			// If they were to our left and heading to the left
			// or if they were to our right and heading to the right
			else
			{
				// Bounce
				this.velocity.x = -(this.velocity.x / 4);
			}

			// If they were above us and heading down
			// or if they were below us and heading up
			if((fromOther.y > 0 && other.velocity.y > 0)
					|| (fromOther.y < 0 && other.velocity.y < 0))
			{
				// Add their x velocity to ours

				// If we are moving in the same direction, don't apply the ratio
				if((this.velocity.y > 0 && other.velocity.y > 0)
						|| (this.velocity.y < 0 && other.velocity.y < 0))
				{
					this.velocity.y += other.velocity.y;
				}
				else
					// If we are moving in different directions, apply the ratio
				{
					this.velocity.y += other.velocity.y * massRatio;
				}
			}
			// If they were above us and heading up
			// or if they were below us and heading down
			else
			{
				// Bounce
				this.velocity.y = -(this.velocity.y / 4);
			}

			if(this.getClass() == Player.class)
			{
				((Player)this).takeDamage(other.mass);
			}
			else if(this.getClass() == Enemy.class)
			{
				((Enemy)this).takeDamage(other.mass);
			}
		}

		return code;
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

		affineTransform = new AffineTransform();
		affineTransform.translate(bounds.getX() - Globals.camera.bounds.getX(), bounds.getY() - Globals.camera.bounds.getY()); // Translate into position

		canvas2D.drawImage(texture, affineTransform, null);
	}

	/**
	 * Scales the calling object to the ratio of the old and new masses
	 * 
	 * @param oldMass The object's old mass
	 * @param newMass The object's new mass
	 */
	protected void scale(double oldMass, double newMass)
	{
		double scale = Math.log(newMass / oldMass) / 2;
		if(scale <= -1) // Prevent sizing issues
		{
			return;
		}

		int xScale = (int)((texture.getWidth() * scale) + texture.getWidth());
		int yScale = (int)((texture.getHeight() * scale) + texture.getHeight());

		BufferedImage bufImage = new BufferedImage(xScale, yScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bufImageGraphics = bufImage.createGraphics();
		bufImageGraphics.drawImage(texture.getScaledInstance(xScale, yScale, 0), 0, 0, null);

		int widthDifference = xScale - texture.getWidth();
		int heightDifferece = yScale - texture.getHeight();

		position.x -= widthDifference / 2;
		position.y -= heightDifferece / 2;

		bounds.setFrame(position.x - (xScale / 2), position.y - (yScale / 2), xScale, yScale);

		texture = bufImage;
	}

	/**
	 * Causes the calling object to take damage
	 * 
	 * @param value The base damage to take
	 * @return If the object was destroyed by the damage
	 */
	protected boolean takeDamage(double value)
	{
		double newMass = mass - value;
		if(newMass <= 0)
		{
			newMass = 0;
			delete();
			return true;
		}
		scale(mass, newMass);
		mass = newMass;
		return false;
	}

	/**
	 * Drwas the velocity vector of the calling object
	 * 
	 * @param canvas2D The canvas to draw on
	 */
	protected void drawVelocity(Graphics2D canvas2D)
	{
		canvas2D.setColor(Color.white);
		canvas2D.drawLine((int)(position.x - Globals.camera.bounds.getX()), (int)(position.y - Globals.camera.bounds.getY()), (int)(position.x - Globals.camera.bounds.getX() + velocity.x), (int)(position.y - Globals.camera.bounds.getY() + velocity.y));
	}

	/**
	 * To be called when an object is combined with another object
	 */
	protected void combine()
	{
		alive = false;
	}

	public void delete()
	{
		new Explosion(this, position.x, position.y, Explosion.LARGE);
		alive = false;
	}
}
