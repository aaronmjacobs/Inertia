package net.mcmiracom.inertia;

import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a single explosion
 * 
 * @author Aaron Jacobs
 */
public class Explosion extends GameObject
{
	public static final int SMALL = 0, LARGE = 1;

	protected GameObject host;
	private Vec2 positionDifference;
	private int frame, type;

	/**
	 * Creates an explosion effect
	 * 
	 * @param host The object that the explosion is occuring on
	 * @param initialX The inital x position of the explosion
	 * @param initialY The initial y position of the explosion
	 * @param eType The type of the explosion (either SMALL or LARGE)
	 */
	public Explosion(GameObject host, double initialX, double initialY, int eType)
	{
		super();
		allGameObjects.add(this);
		this.host = host;
		//TODO
		frame = 0;
		this.type = eType;
		switch(type)
		{
		case SMALL:
			setTexture(Globals.laserExplosion[frame++], true);
			break;
		case LARGE:
			setTexture(Globals.explosion[frame++], true);
			break;
		}

		positionDifference = new Vec2(initialX - host.position.x, initialY - host.position.y);

		final Timer explosionTimer = new Timer();
		explosionTimer.schedule(new TimerTask()
		{
			public void run()
			{
				switch(type)
				{
				case SMALL:
					if(frame >= Globals.LASER_EXPLOSION_FRAMECOUNT)
					{
						delete();
						explosionTimer.cancel();
						return;
					}
					setTexture(Globals.laserExplosion[frame++], true);
					break;
				case LARGE:
					if(frame >= Globals.EXPLOSION_FRAMECOUNT)
					{
						delete();
						explosionTimer.cancel();
						return;
					}
					setTexture(Globals.explosion[frame++], true);
					break;
				}
			}
		}, 0, 45); // 60 fps
	}

	@Override
	public void draw(Graphics2D canvas2D)
	{
		setPosition(host.position.x + positionDifference.x, host.position.y + positionDifference.y);

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
}
