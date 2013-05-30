package nl.jawsper.android.pebblemenu.receivers;

import java.util.Date;

import nl.jawsper.android.pebblemenu.PebbleMenu;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

public class MusicIntentReceiver extends BroadcastReceiver
{
	private static final PebbleMenu pebbleMenu = new PebbleMenu();

	private static final String TAG = "MusicIntentReceiver";
	private static final int modeSwitchTime = 500;

	private static long lastButtonPressed = 0;
	private static Runnable doubleClickTimeout = null;

	private static Handler handler = new Handler()
	{
	};

	@Override public void onReceive( final Context context, Intent intent )
	{
		//Log.d( TAG, "onReceive: " + intent.getAction() );
		if( intent.getAction() == Intent.ACTION_MEDIA_BUTTON )
		{
			KeyEvent keyEvent = (KeyEvent)intent.getExtras().get( Intent.EXTRA_KEY_EVENT );
			// Log.d( TAG, "KeyEvent: " + Integer.toString( keyEvent.getAction() ) + "; " + keyEvent.getKeyCode() );

			if( keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE )
			{
				if( keyEvent.getAction() == KeyEvent.ACTION_DOWN )
				{
					if( lastButtonPressed > 0 )
					{
						long now = new Date().getTime();
						if( now - lastButtonPressed < modeSwitchTime )
						{
							pebbleMenu.onDoubleClick( context );
							handler.removeCallbacks( doubleClickTimeout );
							doubleClickTimeout = null;
						}
						lastButtonPressed = 0;
					}
					else
					{
						lastButtonPressed = new Date().getTime();
						doubleClickTimeout = new Runnable()
						{
							@Override public void run()
							{
								// Log.d( TAG, "DoubleClick timeout!" );
								lastButtonPressed = 0;
								sendPlayPauseIntent( context );
							}
						};
						handler.postAtTime( doubleClickTimeout, SystemClock.uptimeMillis() + modeSwitchTime );
					}
				}
			}
			else
			{
				sendKeyEventIntent( context, keyEvent );
			}
		}
		else if( intent.getAction().endsWith( ".metachanged" ) )
		{
			String artist = intent.getStringExtra( "artist" );
			String track = intent.getStringExtra( "track" );
			String album = intent.getStringExtra( "album" );
			Log.d( TAG, "NowPlaying = " + artist + ":" + album + ":" + track );

			pebbleMenu.setNowPlaying( artist, track, album );
			pebbleMenu.updateDisplay( context );
		}
		else
		{
			Log.d( TAG, "Unknown intent: " + intent.toString() );
		}

	}

	private void sendPlayPauseIntent( Context context )
	{
		KeyEvent down = new KeyEvent( KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE );
		KeyEvent up = new KeyEvent( KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE );
		sendKeyEventIntent( context, down );
		sendKeyEventIntent( context, up );
	}

	private void sendKeyEventIntent( Context context, KeyEvent keyEvent )
	{
		pebbleMenu.onKeyEvent( context, keyEvent );

		if( pebbleMenu.isMediaMode() )
		{
			Intent intent = new Intent( Intent.ACTION_MEDIA_BUTTON );
			intent.setClassName( player_package, player_class );
			intent.putExtra( Intent.EXTRA_KEY_EVENT, keyEvent );
			context.sendBroadcast( intent );
		}
		else
		{

		}
	}

	private static String player_package = "com.google.android.music";
	private static String player_class = "com.google.android.music.playback.MediaButtonIntentReceiver";

	public static void setPlayer( String packageName, String className )
	{
		Log.d( TAG, "Setting player to " + packageName );
		player_package = packageName;
		player_class = className;
	}
}
