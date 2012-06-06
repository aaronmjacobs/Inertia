package net.mcmiracom.inertia;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Grid class used to limit the number of physics calculations performed
 * 
 * @author Aaron Jacobs
 */
public class Grid
{
	protected GridNode[] gridNodes;
	private int gridSize, numXGridNodes, numYGridNodes;

	private static final int NONE = 0, LEFT = 1, RIGHT = 2, TOP = 4, BOTTOM = 8;

	/**
	 * @param worldWidth The width of the world
	 * @param worldHeight The height of the world
	 * @param gridSize The length of the side of a GridNode
	 */
	public Grid(int worldWidth, int worldHeight, int gridSize)
	{
		this.gridSize = gridSize;

		numXGridNodes = worldWidth / gridSize;
		// If there is some world spillover, we need one more GridNode
		if(worldWidth % gridSize > 0)
		{
			++numXGridNodes;
		}

		numYGridNodes = worldHeight / gridSize;
		// If there is some world spillover, we need one more GridNode
		if(worldWidth % gridSize > 0)
		{
			++numYGridNodes;
		}

		gridNodes = new GridNode[numXGridNodes * numYGridNodes];
		for(int i = 0; i < gridNodes.length; ++i)
		{
			gridNodes[i] = new GridNode();
		}
	}

	/**
	 * Places the item into the correct GridNode
	 * 
	 * @param item The item to be placed
	 */
	public void place(MovableObject item)
	{
		// Determine which GrideNode the MovableObject's position is in
		int newGridNodeNum = findGridNode(item.position.x, item.position.y);

		// If the MovableObject is already in that GridNode, return
		if(newGridNodeNum == item.gridNodeNum)
		{
			return;
		}
		else // Otherwise remove the MovableObject from the previous GridNode, and place it in the correct one
		{
			gridNodes[item.gridNodeNum].items.remove(item);

			gridNodes[newGridNodeNum].items.add(item);
			item.gridNodeNum = newGridNodeNum;
		}
	}

	/**
	 * Removes the item from its associated GridNode
	 * 
	 * @param item MovableObjecthe item to be removed
	 */
	public void remove(MovableObject item)
	{	
		// Remove its reference
		gridNodes[item.gridNodeNum].items.remove(item);
	}

	/**
	 * Finds the index of the GridNode the item should be located in
	 * 
	 * @param x The x coordinate of the item
	 * @param y The y coordinate of the item
	 * @return The index of the correct GridNode
	 */
	protected int findGridNode(double x, double y)
	{
		int xGridNodeNum = (int)(x + 0.5) / gridSize;
		int yGridNodeNum = (int)(y + 0.5) / gridSize;

		return xGridNodeNum + (yGridNodeNum * numXGridNodes);
	}

	/**
	 * Calculates all attraction vectors for the supplied object (relative to the grid-space)
	 * 
	 * @param item The item who's attraction vectors are to be calculated
	 * @return The overall attraction vector
	 */
	public Vec2 calculateAttraction(PhysObject item)
	{
		Vec2 attraction = new Vec2();
		MovableObject otherItem;

		ArrayList<Integer> nodes = findNearbyGridNodes(item.position.x, item.position.y);
		for(int i = 0; i < nodes.size(); ++i)
		{
			Iterator<MovableObject> itemIterator = gridNodes[nodes.get(i)].items.iterator();
			while(itemIterator.hasNext())
			{
				otherItem = itemIterator.next();
				if(otherItem != item && otherItem instanceof PhysObject)
				{
					attraction.addOn(item.calculateAttraction((PhysObject) otherItem));
				}
			}
		}

		return attraction;
	}

	/**
	 * Draws all attraction vectors for the supplied object (relative to the grid-space)
	 * 
	 * @param item The item who's attraction vectors are to be drawn
	 * @param canvas2D The canvas to draw on
	 */
	public void drawVisualAttraction(MovableObject item, Graphics2D canvas2D)
	{
		MovableObject otherItem;

		ArrayList<Integer> nodes = findNearbyGridNodes(item.position.x, item.position.y);
		for(int i = 0; i < nodes.size(); ++i)
		{
			Iterator<MovableObject> itemIterator = gridNodes[nodes.get(i)].items.iterator();
			while(itemIterator.hasNext())
			{
				otherItem = itemIterator.next();
				if(otherItem != item)
				{
					double dist = item.position.distance(otherItem.position);
					if(dist < 300)
					{
						canvas2D.setColor(Color.red);
					}
					else if(dist < 700)
					{
						canvas2D.setColor(Color.yellow);
					}
					else
					{
						canvas2D.setColor(Color.green);
					}
					canvas2D.drawLine((int)(item.position.x - Globals.camera.bounds.getX()), (int)(item.position.y - Globals.camera.bounds.getY()), (int)(otherItem.position.x - Globals.camera.bounds.getX()), (int)(otherItem.position.y - Globals.camera.bounds.getY()));
				}
			}
		}
	}

	/**
	 * Handles all body collisions for the supplied object (relative to the grid-space)
	 * 
	 * @param item The item who's collisions are to be handled
	 */
	public void handleBodyCollisions(PhysObject item)
	{
		MovableObject otherItem;
		int code;

		ArrayList<Integer> nodes = findNearbyGridNodes(item.position.x, item.position.y);
		for(int i = 0; i < nodes.size(); ++i)
		{
			Iterator<MovableObject> itemIterator = gridNodes[nodes.get(i)].items.iterator();
			while(itemIterator.hasNext())
			{
				otherItem = itemIterator.next();

				// Don't absorb players
				if(otherItem.getClass() == Player.class) // TODO
				{
					continue;
				}


				if(otherItem != item && otherItem.getClass() == PhysObject.class)
				{
					code = item.handleBodyCollision((PhysObject) otherItem);
					if(code == PhysObject.CALLING_ITEM_ABSORBED) // The calling item was absorbed
					{
						// Remove the object, stop iterating
						item.combine();
						remove(item);
						return;
					}
					else if(code == PhysObject.OTHER_ITEM_ABSORBED) // The otherItem was absorbed
					{
						// Remove the object
						((PhysObject)otherItem).combine();
						itemIterator.remove();
					}
				}
			}
		}
	}

	/**
	 * Handles all hard collisions for the supplied object (relative to the grid-space)
	 * 
	 * @param item The object who's hard collisions are to be handled
	 */
	public void handlePhysicsCollisions(PhysObject item)
	{
		MovableObject otherItem;
		int code;

		ArrayList<Integer> nodes = findNearbyGridNodes(item.position.x, item.position.y);
		for(int i = 0; i < nodes.size(); ++i)
		{
			Iterator<MovableObject> itemIterator = gridNodes[nodes.get(i)].items.iterator();
			while(itemIterator.hasNext())
			{
				otherItem = itemIterator.next();
				if(otherItem != item && otherItem instanceof PhysObject)
				{
					code = item.handleCollisions((PhysObject) otherItem);
					if(code == 1) // If a collision occurred
					{
						if(otherItem.getClass() == PhysObject.class)
						{
							otherItem.delete();
							itemIterator.remove();
						}
						else
						{
							((PhysObject)otherItem).handleCollisions((PhysObject) item);

							if(!((PhysObject)otherItem).alive)
							{
								otherItem.delete();
								itemIterator.remove();
							}
						}

						if(!item.alive)
						{
							remove(item);
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * Handles all collisions for the supplied laser (relative to the grid-space)
	 * 
	 * @param laser The laser who's collisions are to be handled
	 */
	public void handleLaserCollisions(Laser laser)
	{
		MovableObject otherItem;

		ArrayList<Integer> nodes = findNearbyGridNodes(laser.position.x, laser.position.y);
		for(int i = 0; i < nodes.size(); ++i)
		{
			Iterator<MovableObject> itemIterator = gridNodes[nodes.get(i)].items.iterator();
			while(itemIterator.hasNext())
			{
				otherItem = itemIterator.next();
				if(otherItem != laser && otherItem instanceof PhysObject)
				{
					if(laser.checkHit(otherItem)) // If a hit occurred
					{
						PhysObject otherPhysItem = (PhysObject)otherItem;
						if(otherPhysItem.takeDamage(Globals.LASER_DAMAGE))
						{
							itemIterator.remove();
						}
						else
						{
							// Explode at the impact site
							new Explosion(otherItem, laser.position.x, laser.position.y, Explosion.SMALL);
						}
						laser.delete();
					}
				}
			}
		}
	}

	/**
	 * Determines the nearby GridNodes for the given point
	 * 
	 * @param x The x coordinate for the point to check
	 * @param y The y coordinate for the point to check
	 * @return An ArrayList of the GridNode numbers for the nearby nodes
	 */
	private ArrayList<Integer> findNearbyGridNodes(double x, double y)
	{
		ArrayList<Integer> nodeList = new ArrayList<Integer>();
		int fourthPosition = NONE;

		// Determine which GrideNode the position is in
		int newGridNodeNum = findGridNode(x, y);
		nodeList.add(newGridNodeNum);

		if(leftGridNodeExists(newGridNodeNum) && rightGridNodeExists(newGridNodeNum))
		{
			// If the object is closer to the next-left GridNode than the right one
			if(Math.abs(x - ((newGridNodeNum % numXGridNodes) * gridSize)) < Math.abs(x - (((newGridNodeNum + 1) % numXGridNodes) * gridSize)))
			{
				nodeList.add(newGridNodeNum - 1);
				fourthPosition |= LEFT;
			}
			else // If the object is closer to the next-right GridNode than the left one
			{
				nodeList.add(newGridNodeNum + 1);
				fourthPosition |= RIGHT;
			}
		}
		else if(leftGridNodeExists(newGridNodeNum))
		{
			nodeList.add(newGridNodeNum - 1);
			fourthPosition |= LEFT;
		}
		else if(rightGridNodeExists(newGridNodeNum))
		{
			nodeList.add(newGridNodeNum + 1);
			fourthPosition |= RIGHT;
		}

		if(topGridNodeExists(newGridNodeNum) && bottomGridNodeExists(newGridNodeNum))
		{
			// If the object is closer to the next-top GridNode than the bottom one
			if(Math.abs(y - ((newGridNodeNum / numXGridNodes) * gridSize)) < Math.abs(y - (((newGridNodeNum + numXGridNodes) / numXGridNodes) * gridSize)))
			{
				nodeList.add(newGridNodeNum - numXGridNodes);
				fourthPosition |= TOP;
			}
			else // If the object is closer to the next-bottom GridNode than the top one
			{
				nodeList.add(newGridNodeNum + numXGridNodes);
				fourthPosition |= BOTTOM;
			}
		}
		else if(topGridNodeExists(newGridNodeNum))
		{
			nodeList.add(newGridNodeNum - numXGridNodes);
			fourthPosition |= TOP;
		}
		else if(bottomGridNodeExists(newGridNodeNum))
		{
			nodeList.add(newGridNodeNum + numXGridNodes);
			fourthPosition |= BOTTOM;
		}

		if(fourthPosition == (LEFT | TOP))
		{
			nodeList.add(newGridNodeNum - 1 - numXGridNodes);
		}
		else if(fourthPosition == (RIGHT | TOP))
		{
			nodeList.add(newGridNodeNum + 1 - numXGridNodes);
		}
		else if(fourthPosition == (LEFT | BOTTOM))
		{
			nodeList.add(newGridNodeNum - 1 + numXGridNodes);
		}
		else if(fourthPosition == (RIGHT | BOTTOM))
		{
			nodeList.add(newGridNodeNum + 1 + numXGridNodes);
		}

		return nodeList;
	}

	/**
	 * @param gridNodeNum The number of the GridNode to check
	 * @return If a left GridNode exists
	 */
	private boolean leftGridNodeExists(int gridNodeNum)
	{
		// Return true if the previous GridNode exists and is on the same row
		return gridNodeExists(gridNodeNum - 1) && ((gridNodeNum / numXGridNodes) == ((gridNodeNum - 1) / numXGridNodes));
	}

	/**
	 * @param gridNodeNum The number of the GridNode to check
	 * @return If a right GridNode exists
	 */
	private boolean rightGridNodeExists(int gridNodeNum)
	{
		// Return true if the next GridNode exists and is on the same row
		return gridNodeExists(gridNodeNum + 1) && ((gridNodeNum / numXGridNodes) == ((gridNodeNum + 1) / numXGridNodes));
	}

	/**
	 * @param gridNodeNum The number of the GridNode to check
	 * @return If a top GridNode exists
	 */
	private boolean topGridNodeExists(int gridNodeNum)
	{
		// Return true if the GridNode above the current node exists (should always be in the same columb)
		return gridNodeExists(gridNodeNum - numXGridNodes);
	}

	/**
	 * @param gridNodeNum The number of the GridNode to check
	 * @return If a bottom GridNode exists
	 */
	private boolean bottomGridNodeExists(int gridNodeNum)
	{
		// Return true if the GridNode above the current node exists (should always be in the same columb)
		return gridNodeExists(gridNodeNum + numXGridNodes);
	}

	/**
	 * @param gridNodeNum The number of the GridNode to check
	 * @return If the GridNode exists
	 */
	private boolean gridNodeExists(int gridNodeNum)
	{
		return (gridNodeNum >= 0) && (gridNodeNum < gridNodes.length);
	}

	/**
	 * Draws a visual representation of the grid
	 * 
	 * @param canvas2D The canvas to draw on
	 */
	protected void drawGrid(Graphics2D canvas2D)
	{
		canvas2D.setColor(Color.lightGray);
		for(int i = 0; i < numXGridNodes; ++i)
		{
			for(int j = 0; j < numYGridNodes; ++j)
			{
				canvas2D.drawRect(i * gridSize - (int)(Globals.camera.bounds.getX() + 0.5), j * gridSize - (int)(Globals.camera.bounds.getY() + 0.5), gridSize, gridSize);
			}
		}
	}

	/**
	 * Draws the closest enemy to the player
	 * 
	 * @param ship The player
	 * @param canvas2D The canvas to draw on
	 */
	protected void drawClosestEnemy(Player ship, Graphics2D canvas2D)
	{
		MovableObject otherItem = null;
		Enemy closestEnemy = null;
		double closestDistance = 999999;

		ArrayList<Integer> nodes = findNearbyGridNodes(ship.position.x, ship.position.y);
		for(int i = 0; i < nodes.size(); ++i)
		{
			Iterator<MovableObject> itemIterator = gridNodes[nodes.get(i)].items.iterator();
			while(itemIterator.hasNext())
			{
				otherItem = itemIterator.next();
				if(otherItem != ship && otherItem instanceof Enemy)
				{
					double dist = ship.position.distance(otherItem.position);
					if(dist < closestDistance)
					{
						closestDistance = dist;
						closestEnemy = (Enemy)otherItem;
					}
				}
			}
		}

		if(closestEnemy != null)
		{
			canvas2D.setColor(Color.magenta);
			canvas2D.drawLine((int)(ship.position.x - Globals.camera.bounds.getX()), (int)(ship.position.y - Globals.camera.bounds.getY()), (int)(closestEnemy.position.x - Globals.camera.bounds.getX()), (int)(closestEnemy.position.y - Globals.camera.bounds.getY()));
		}
	}

	/**
	 * GridNode class used in the grid
	 * 
	 * @author Aaron Jacobs
	 */
	protected static class GridNode
	{
		protected LinkedList<MovableObject> items;

		public GridNode()
		{
			items = new LinkedList<MovableObject>();
		}
	}
}
