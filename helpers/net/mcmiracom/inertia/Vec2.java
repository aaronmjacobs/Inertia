package net.mcmiracom.inertia;

/**
 * Two-dimensional vector class
 * 
 * @author Aaron Jacobs
 */
public class Vec2
{
	protected double x, y;

	/**
	 * Sets values of both variables to 0.0
	 */
	public Vec2()
	{
		x = 0.0;
		y = 0.0;
	}

	/**
	 * @param x The x value of the vector
	 * @param y The y value of the vector
	 */
	public Vec2(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Copy constructor
	 * 
	 * @param other The Vec2 to copy
	 */
	public Vec2(Vec2 other)
	{
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * @return The magnitude of the vector
	 */
	public double magnitude()
	{
		return Math.sqrt(x*x + y*y);
	}

	/**
	 * @return A normalized version of the vector
	 */
	public Vec2 normalize()
	{
		double magnitude = magnitude();
		if(magnitude == 0.0)
		{
			magnitude = 1.0; // Prevent division by 0
		}

		return new Vec2(x / magnitude, y / magnitude);
	}

	/**
	 * Normalizes the calling vector
	 */
	public void selfNormalize()
	{
		double magnitude = magnitude();
		if(magnitude == 0.0)
		{
			magnitude = 1.0; // Prevent division by 0
		}

		x /= magnitude;
		y /= magnitude;
	}

	/**
	 * @param other The vector to be used to calculate a dot product
	 * @return The dot product of the vectors
	 */
	public double dot(Vec2 other)
	{
		return (this.x * other.x) + (this.y * other.y);
	}

	/**
	 * @param value The vector to be used in the multiplication
	 * @return The result of the vector multiplication
	 */
	public Vec2 multiply(double value)
	{
		return new Vec2(this.x * value, this.y * value);
	}

	/**
	 * Multiplies the other vector by the calling vector, and stores the result in the calling vector
	 * 
	 * @param value The vector to be used in the multiplication
	 */
	public void multiplyOn(double value)
	{
		x *= value;
		y *= value;
	}

	/**
	 * @param other The vector to be used in the addition
	 * @return The result of the vector addition
	 */
	public Vec2 add(Vec2 other)
	{
		return new Vec2(this.x + other.x, this.y + other.y);
	}

	/**
	 * Adds the other vector to the calling vector, and stores the result in the calling vector
	 * 
	 * @param other The vector to be used in the addition
	 */
	public void addOn(Vec2 other)
	{
		this.x += other.x;
		this.y += other.y;
	}

	/**
	 * @param other The vector to be used in the subtraction
	 * @return The result of the vector subtraction
	 */
	public Vec2 subtract(Vec2 other)
	{
		return new Vec2(this.x - other.x, this.y - other.y);
	}

	/**
	 * Subtracts the other vector from the calling vector, and stores the result in the calling vector
	 * 
	 * @param other The vector to be used in the subtraction
	 */
	public void subtractOn(Vec2 other)
	{
		this.x -= other.x;
		this.y -= other.y;
	}

	/**
	 * @param other The vector to calculate the distance to
	 * @return The distance between the two vectors
	 */
	public double distance(Vec2 other)
	{
		return Math.sqrt(((this.x - other.x) * (this.x - other.x)) + ((this.y - other.y) * (this.y - other.y)));
	}

	/**
	 * Calculates the angle between two vectors
	 * 
	 * @param other The vector to check the angle against
	 * @return The angle between the calling vector, and the passed vector
	 */
	public double angle(Vec2 other)
	{
		return Math.acos(this.normalize().dot(other.normalize()));
	}

	/**
	 * @return The inverse of the calling vector
	 */
	public Vec2 inverse()
	{
		return new Vec2(-x, -y);
	}

	/**
	 * Flips the x and y values of the vector
	 */
	public Vec2 flip()
	{
		return new Vec2(y, x);
	}

	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
