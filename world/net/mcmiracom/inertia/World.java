package net.mcmiracom.inertia;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Represents the world (bounds and background)
 * 
 * @author Aaron Jacobs
 */
public class World
{
	protected Rectangle2D bounds;

	/**
	 * Sets the bounds of the world to the defaults set in the Globals class, and loads the background images
	 */
	public World()
	{
		bounds = new Rectangle2D.Double(0.0, 0.0, Globals.levelSize, Globals.levelSize);
	}

	/**
	 * Draws the 3 background layers to the screen, relative to the camera
	 * 
	 * @param canvas2D The Graphics2D object to which the world will be drawn
	 * @param camera The camera that the world will be drawn relative to
	 */
	public void draw(Graphics2D canvas2D)
	{
		// Draw the background (only what fits on the screen)
		canvas2D.drawImage(Globals.background, 0, 0, Globals.panelWidth, Globals.panelHeight, 0, 0, Globals.panelWidth, Globals.panelHeight, null);

		// Calculate location of the midground texture
		int midgroundX = (int)((((Globals.levelSize / 2) - (Globals.midground.getWidth() / 2)) - Globals.camera.bounds.getX()) * 0.04) + (Globals.panelWidth / 2) - (Globals.midground.getWidth() / 2);
		int midgroundY = (int)((((Globals.levelSize / 2) - (Globals.midground.getHeight() / 2)) - Globals.camera.bounds.getY()) * 0.04) + (Globals.panelHeight / 2) - (Globals.midground.getHeight() / 2);
		int midgroundX1Clip = 0, midgroundX2Clip = 0;
		int midgroundY1Clip = 0, midgroundY2Clip = 0;

		// Generate clipping regions
		if(midgroundX < 0)
		{
			midgroundX1Clip = -midgroundX;
		}
		if(midgroundX + Globals.midground.getWidth() > Globals.panelWidth)
		{
			midgroundX2Clip = (midgroundX + Globals.midground.getWidth()) - Globals.panelWidth;
		}
		if(midgroundY < 0)
		{
			midgroundY1Clip = -midgroundY;
		}
		if(midgroundY + Globals.midground.getHeight() > Globals.panelHeight)
		{
			midgroundY2Clip = (midgroundY + Globals.midground.getHeight()) - Globals.panelHeight;
		}

		// Draw the midground (only what's on screen)
		canvas2D.drawImage(Globals.midground, midgroundX + midgroundX1Clip, midgroundY + midgroundY1Clip, (midgroundX + Globals.midground.getWidth()) - midgroundX2Clip, (midgroundY + Globals.midground.getHeight()) - midgroundY2Clip, midgroundX1Clip, midgroundY1Clip, Globals.midground.getWidth() - midgroundX2Clip, Globals.midground.getHeight() - midgroundY2Clip, null);

		// Set up variables for the tiling foreground
		int tileWidth = Globals.foreground.getWidth();
		int tileHeight = Globals.foreground.getHeight();
		int xTile = ((int)Globals.camera.bounds.getX()) / tileWidth; // Which tile number (across the screen) we are on
		int yTile = ((int)Globals.camera.bounds.getY()) / tileHeight; // Which tile number (down the screen) we are on
		int foregroundXPos = -(int)(Globals.camera.bounds.getX() + 0.5);
		int foregroundYPos = -(int)(Globals.camera.bounds.getY() + 0.5);
		int numXTiles = (Globals.panelWidth / Globals.foreground.getWidth()) + 2; // The number of times the image fits on screen, plus on extra on the left and one extra on the right
		int numYTiles = (Globals.panelHeight / Globals.foreground.getHeight()) + 2; // The number of times the image fits on screen, plus on extra on the top and one extra on the bottom

		for(int i = 0; i < numXTiles; ++i) // For each column
		{
			for(int j = 0; j < numYTiles; ++j) // For each row
			{
				// Calculate the coordinates of the tile
				int x = foregroundXPos + (tileWidth * (xTile + i)); // Our upper left x (relative to the screen)
				int y = foregroundYPos + (tileHeight * (yTile + j)); // Our upper left y (relative to the screen)
				int x1Clip = 0, x2Clip = 0; // The amount to be clipped off from the left, right
				int y1Clip = 0, y2Clip = 0; // The amount to be clipped off from the top, bottom

				// Generate the clipping region of the tile
				if(x < 0)
				{
					x1Clip = -x;
				}
				if(x + tileWidth > Globals.panelWidth)
				{
					x2Clip = (x + tileWidth) - Globals.panelWidth;
				}
				if(y < 0)
				{
					y1Clip = -y;
				}
				if(y + tileHeight > Globals.panelHeight)
				{
					y2Clip = (y + tileHeight) - Globals.panelHeight;
				}

				// Don't draw if off screen
				if(x > Globals.panelWidth
						|| x + x1Clip < 0
						|| y > Globals.panelHeight
						|| y + y1Clip < 0)
				{
					continue;
				}

				// Draw the tile (only what's on screen)
				canvas2D.drawImage(Globals.foreground, x + x1Clip, y + y1Clip, (x + tileWidth) - x2Clip, (y + tileHeight) - y2Clip, x1Clip, y1Clip, tileWidth - x2Clip, tileHeight - y2Clip, null);
			}
		}
	}
}
