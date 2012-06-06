package net.mcmiracom.inertia;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * Player class
 * 
 * @author Aaron Jacobs
 */
public class Player extends ControllableObject
{
	private double mouseX, mouseY;
	private BufferedImage thrustTexture, normalTexture;
	protected static int maxHealth = Globals.DEFAULT_HEALTH;

	public Player(double mass, int health)
	{
		super(mass, health);

		mouseX = 0.0;
		mouseY = 0.0;

		try
		{
			normalTexture = ImageIO.read(GamePanel.class.getClassLoader().getResource(Globals.TEXTURE_FOLDER + "ship.png"));
		}
		catch (IOException e)
		{
			normalTexture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); // Prevent null pointer
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Loading Player Texture", JOptionPane.ERROR_MESSAGE);
		}

		try
		{
			thrustTexture = ImageIO.read(GamePanel.class.getClassLoader().getResource(Globals.TEXTURE_FOLDER + "ship_thrust.png"));
		}
		catch (IOException e)
		{
			thrustTexture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); // Prevent null pointer
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Loading Player Texture", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void draw(Graphics2D canvas2D)
	{
		double adjustedMouseX = mouseX + Globals.camera.bounds.getX();
		double adjustedMouseY = mouseY + Globals.camera.bounds.getY();
		Vec2 mouse = new Vec2(adjustedMouseX, adjustedMouseY);
		Vec2 toMouse = mouse.subtract(position);
		toMouse.selfNormalize();

		Vec2 right = new Vec2(1.0, 0.0);
		angle = Math.acos(toMouse.dot(right));
		if(position.y > adjustedMouseY)
		{
			angle = -angle;
		}

		affineTransform = new AffineTransform();
		affineTransform.translate(bounds.getX() - Globals.camera.bounds.getX(), bounds.getY() - Globals.camera.bounds.getY()); // Translate into position
		affineTransform.translate(bounds.getWidth() / 2, bounds.getHeight() / 2); // Translate to the center of the Player
		affineTransform.rotate(angle); // Rotate the Player
		affineTransform.translate(-bounds.getWidth() / 2, -bounds.getHeight() / 2); // Translate back to the main position
		canvas2D.drawImage(texture, affineTransform, null);

		//drawVelocity(canvas2D);
	}

	@Override
	protected void scale(double oldMass, double newMass)
	{
		double scale = Math.log(newMass / oldMass) / 2;
		int xScale = (int)((texture.getWidth() * scale) + texture.getWidth());
		int yScale = (int)((texture.getHeight() * scale) + texture.getHeight());

		BufferedImage scaledImage = new BufferedImage(xScale, yScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bufImageGraphics = scaledImage.createGraphics();
		bufImageGraphics.drawImage(texture.getScaledInstance(xScale, yScale, 0), 0, 0, null);

		int widthDifference = xScale - texture.getWidth();
		int heightDifferece = yScale - texture.getHeight();

		position.x -= widthDifference / 2;
		position.y -= heightDifferece / 2;

		bounds.setFrame(position.x - (xScale / 2), position.y - (yScale / 2), xScale, yScale);

		texture = scaledImage;

		BufferedImage scaledThrust = new BufferedImage(xScale, yScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D scaledThrustGraphics = scaledThrust.createGraphics();
		scaledThrustGraphics.drawImage(thrustTexture.getScaledInstance(xScale, yScale, 0), 0, 0, null);
		thrustTexture = scaledThrust;

		BufferedImage scaledNormal = new BufferedImage(xScale, yScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D scaledNormalGraphics = scaledNormal.createGraphics();
		scaledNormalGraphics.drawImage(normalTexture.getScaledInstance(xScale, yScale, 0), 0, 0, null);
		normalTexture = scaledNormal;
	}

	@Override
	protected void handleThrust()
	{
		Vec2 mouse = new Vec2(mouseX + Globals.camera.bounds.getX(), mouseY + Globals.camera.bounds.getY());
		Vec2 toMouse = mouse.subtract(position);
		toMouse.selfNormalize();
		toMouse.multiplyOn(Globals.THRUST_FORCE);

		if((velocity.x > Globals.MAX_THRUST && toMouse.x > 0.0) || (velocity.x < -Globals.MAX_THRUST && toMouse.x < 0.0))
		{
			toMouse.x = 0.0;
		}
		if((velocity.y > Globals.MAX_THRUST && toMouse.y > 0.0) || (velocity.y < -Globals.MAX_THRUST && toMouse.y < 0.0))
		{
			toMouse.y = 0.0;
		}

		acceleration.addOn(toMouse);
	}

	@Override
	protected void handleAllCollisions()
	{
		checkBounds();

		Globals.grid.handlePhysicsCollisions(this);
	}

	/**
	 * Handles keyboard input for the player object
	 * 
	 * @param keyCode The key code detected by the listener
	 * @param keyDown If the event was a key down event
	 */
	public void handleKeyboardInput(int keyCode, boolean keyDown)
	{
		if(keyDown)
		{
			switch(keyCode)
			{
			case KeyEvent.VK_SPACE:
				if(!thrust)
				{
					setTexture(thrustTexture, false);
				}
				thrust = true;
			}
		}
		else
		{
			switch(keyCode)
			{
			case KeyEvent.VK_SPACE:
				setTexture(normalTexture, false);
				thrust = false;
				break;
			}
		}
	}

	/**
	 * Handles mouse motion input for the player object
	 * 
	 * @param mouseX The x coordinate of the mouse
	 * @param mouseY The y coordinate of the mouse
	 */
	public void handleMouseMotion(double mouseX, double mouseY)
	{
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	/**
	 * Handles mouse button input for the player object
	 * 
	 * @param buttonCode The button code of the pressed mouse button
	 */
	public void handleMouseInput(int buttonCode)
	{
		switch(buttonCode)
		{
		case MouseEvent.BUTTON1:
			if(alive)
			{
				shoot(mouseX + Globals.camera.bounds.getX(), mouseY + Globals.camera.bounds.getY());
			}
			break;
		}
	}

	@Override
	protected boolean takeDamage(double value)
	{
		int multiplier = 1;
		switch(Globals.difficulty)
		{
		case Globals.EASY:
			multiplier = 1;
			break;
		case Globals.MEDIUM:
			multiplier = 2;
			break;
		case Globals.HARD:
			multiplier = 3;
			break;
		}
		health -= (value * multiplier);
		if(health <= 0)
		{
			health = 0;
			delete();
			return true;
		}
		return false;
	}

	@Override
	public void delete()
	{
		if(alive)
		{
			alive = false;
			Globals.gameOver = true;
			new Explosion(this, position.x, position.y, Explosion.LARGE);
		}
	}
}
