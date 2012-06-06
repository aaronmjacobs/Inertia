package net.mcmiracom.inertia;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

/**
 * Loads, plays, and loops music
 * 
 * @author Aaron Jacobs
 */
public class MusicPlayer
{
	public static final int ANTON = 0;
	public static final int LOOP_INDEFINTELY = -1;

	private static volatile boolean stop = true;

	/**
	 * Plays the selected song
	 * 
	 * @param songID The ID of the song to be played
	 */
	public static void play(int songID)
	{
		if(!stop)
		{
			return;
		}

		String songName = "";
		switch(songID)
		{
		case ANTON:
			songName = "sounds/music.wav";
			break;
		}

		final String path = songName;

		new Thread(new Runnable(){
			public void run()
			{
				playClip(path, LOOP_INDEFINTELY);
			}
		}).start();
	}

	/**
	 * Stops any playing music
	 */
	public static void stop()
	{
		stop = true;
	}

	/**
	 * Plays the audio file located at fileName, looping numLoops times
	 * 
	 * @param fileName The name (and path) of the audio file
	 * @param numLoops The number of times the audio should loop
	 */
	private static void playClip(String fileName, int numLoops)
	{
		final int BUFFER_SIZE = 65536; // 64 KB
		SourceDataLine soundLine = null;
		stop = false;
		int loopCounter = 0;

		try
		{
			BufferedInputStream in = new BufferedInputStream(MusicPlayer.class.getClassLoader().getResourceAsStream((fileName)), 65536);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(in);
			AudioFormat audioFormat = audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			soundLine = (SourceDataLine)AudioSystem.getLine(info);
			soundLine.open(audioFormat);
			soundLine.start();
			int nBytesRead = 0;
			byte[] sampledData = new byte[BUFFER_SIZE];
			audioInputStream.mark(Integer.MAX_VALUE);
			while(!stop && (nBytesRead = audioInputStream.read(sampledData, 0, sampledData.length)) != -1)
			{
				soundLine.write(sampledData, 0, nBytesRead);

				// If we're at the end of the stream
				if(audioInputStream.available() == 0 && (numLoops == LOOP_INDEFINTELY || loopCounter < numLoops))
				{
					// Reset if so that it loops
					audioInputStream.reset();

					++loopCounter;
				}
			}
		}
		catch(UnsupportedAudioFileException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Playing Audio", JOptionPane.ERROR_MESSAGE);
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Playing Audio", JOptionPane.ERROR_MESSAGE);
		}
		catch(LineUnavailableException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error Playing Audio", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			soundLine.drain();
			soundLine.close();
		}
	}
}
