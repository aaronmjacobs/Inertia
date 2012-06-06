package net.mcmiracom.inertia;

import java.awt.geom.Rectangle2D;

/**
 * Camera who's position determines what is rendered on screen
 * 
 * @author Aaron Jacobs
 */
public class Camera
{
	protected Rectangle2D bounds;

	/**
	 * @param x The x coordinate of the (upper left of the) camera
	 * @param y They y coordinate of the (upper left of the) camera
	 * @param width The width of the camera's viewing area
	 * @param height The height of the camera's viewing area
	 */
	public Camera(double x, double y, double width, double height)
	{
		bounds = new Rectangle2D.Double(x, y, width, height);
	}

	/**
	 * Centers the camera on the targetBounds, keeping it inside of the worldBounds
	 * 
	 * @param targetBounds The target to center on
	 * @param worldBounds The bounds to stay inside of
	 */
	public void centerOn(Rectangle2D targetBounds, Rectangle2D worldBounds)
	{
		// Set our position to be centered on the target
		Vec2 position = new Vec2();
		position.x = targetBounds.getCenterX() - (bounds.getWidth() / 2);
		position.y = targetBounds.getCenterY() - (bounds.getHeight() / 2);

		// Stay within the bounds of the world
		if(position.x < 0)
		{
			position.x = 0;
		}
		if(position.x + bounds.getWidth() > worldBounds.getX() + worldBounds.getWidth())
		{
			position.x = worldBounds.getX() + worldBounds.getWidth() - bounds.getWidth();
		}
		if(position.y < 0)
		{
			position.y = 0;
		}
		if(position.y + bounds.getHeight() > worldBounds.getY() + worldBounds.getHeight())
		{
			position.y = worldBounds.getY() + worldBounds.getHeight() - bounds.getHeight();
		}

		// Set the bounds
		bounds.setFrame(position.x, position.y, bounds.getWidth(), bounds.getHeight());
	}
}
