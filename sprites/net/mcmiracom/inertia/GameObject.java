package net.mcmiracom.inertia;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Generic GameObject (position and texture)
 * 
 * @author Aaron Jacobs
 */
public class GameObject
{
	protected static ArrayList<GameObject> allGameObjects = new ArrayList<GameObject>();

	protected RectangularShape bounds;
	protected Vec2 position;
	protected BufferedImage texture;

	public GameObject()
	{
		bounds = new Rectangle2D.Double();
		position = new Vec2();
		texture = null;

		if(this.getClass() == GameObject.class)
		{
			allGameObjects.add(this);
		}
	}

	/**
	 * Sets the texture of the object
	 * 
	 * @param texture The texture
	 * @param changeBounds If the bounds of the object should change to match the new texture
	 */
	public void setTexture(BufferedImage texture, boolean changeBounds)
	{
		this.texture = texture;
		if(changeBounds)
		{
			bounds.setFrame(bounds.getX(), bounds.getY(), texture.getWidth(), texture.getHeight());
		}
		position.x = bounds.getCenterX();
		position.y = bounds.getCenterY();
	}

	/**
	 * Sets the position of the object, and updates the bounds
	 * 
	 * @param x The new x position
	 * @param y The new y position
	 */
	public void setPosition(double x, double y)
	{
		position.x = x;
		position.y = y;
		bounds.setFrame(position.x - (bounds.getWidth() / 2), position.y - (bounds.getHeight() / 2), bounds.getWidth(), bounds.getHeight());
	}

	/**
	 * Draws the image, relative to the camera (if on screen)
	 * 
	 * @param canvas2D The canvas to draw onto
	 */
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

		canvas2D.drawImage(texture, (int)((bounds.getX() - Globals.camera.bounds.getX()) + 0.5), (int)((bounds.getY() - Globals.camera.bounds.getY()) + 0.5), null);
	}

	/**
	 * @param other The object to check for intersection
	 * @return If the other objects intersects this one
	 */
	public boolean circularIntersects(GameObject other)
	{
		return this.position.distance(other.position) < ((((this.bounds.getWidth() / 2) + (this.bounds.getHeight() / 2)) / 2) + ((other.bounds.getWidth() / 2) + (other.bounds.getHeight() / 2)) / 2);
	}

	/**
	 * @param other The object to check for intersection
	 * @param newPosition The new position of the calling object
	 * @return If the other objects intersects this one
	 */
	public boolean circularIntersects(GameObject other, Vec2 newPosition)
	{
		return newPosition.distance(other.position) < ((((this.bounds.getWidth() / 2) + (this.bounds.getHeight() / 2)) / 2) + ((other.bounds.getWidth() / 2) + (other.bounds.getHeight() / 2)) / 2);
	}

	/**
	 * Draws the bounds of this object
	 * 
	 * @param canvas2D The canvas to draw onto
	 */
	public void drawBounds(Graphics2D canvas2D)
	{
		canvas2D.setColor(Color.cyan);
		canvas2D.drawRect((int)(bounds.getX() - Globals.camera.bounds.getX()), (int)(bounds.getY() - Globals.camera.bounds.getY()), (int)bounds.getWidth(), (int)bounds.getHeight());
	}

	/**
	 * Deletes the calling object
	 */
	public void delete()
	{
		allGameObjects.remove(this);
	}
}
