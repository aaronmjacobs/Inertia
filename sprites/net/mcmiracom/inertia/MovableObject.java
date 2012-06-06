package net.mcmiracom.inertia;

/**
 * Abstract movable object (contains velocity, grid node number, move method)
 * 
 * @author Aaron Jacobs
 */
public abstract class MovableObject extends GameObject
{
	protected Vec2 velocity;
	protected int gridNodeNum;

	public abstract void move();
}
