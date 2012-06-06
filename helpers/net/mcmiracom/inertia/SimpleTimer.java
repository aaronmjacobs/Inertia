package net.mcmiracom.inertia;

/**
 * Simple game timer
 * 
 * @author Aaron Jacobs
 */
public class SimpleTimer
{
	private long startTicks, pausedTicks;
	private boolean started, paused;

	/**
	 * Instantiates variables
	 */
	public SimpleTimer()
	{
		startTicks = 0L;
		pausedTicks = 0L;
		started = false;
		paused = false;
	}

	/**
	 * Starts the timer
	 */
	public void start()
	{
		started = true;
		paused = false;
		startTicks = System.currentTimeMillis(); // Grab the current time
	}

	/**
	 * Stops the timer
	 */
	public void stop()
	{
		started = false;
		paused = false;
	}

	/**
	 * Pauses the timer
	 */
	public void pause()
	{
		if(started && !paused)
		{
			paused = true;
			pausedTicks = System.currentTimeMillis() - startTicks; // Calculate the time the pause was initiated
		}
	}

	/**
	 * Unpauses the timer
	 */
	public void unPause()
	{
		if(paused)
		{
			paused = false;
			startTicks = System.currentTimeMillis() - pausedTicks; // Reset the starting time
			pausedTicks = 0L; // Reset the paused time
		}
	}

	/**
	 * @return The number of ticks since the timer started (or, if paused, the number of ticks since the timer was paused)
	 */
	public long getTicks()
	{
		if(started)
		{
			if(paused)
			{
				return pausedTicks; // Return the number of ticks when the timer was paused
			}
			else
			{
				return System.currentTimeMillis() - startTicks; // Return the total number of ticks since the start
			}
		}
		return 0L; // If the timer wasn't started, return 0
	}
}
