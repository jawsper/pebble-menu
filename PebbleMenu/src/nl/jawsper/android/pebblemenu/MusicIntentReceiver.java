package nl.jawsper.android.pebblemenu;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import static com.getpebble.android.kit.Constants.APP_UUID;
import static com.getpebble.android.kit.Constants.INTENT_APP_RECEIVE;
import static com.getpebble.android.kit.Constants.MSG_DATA;
import static com.getpebble.android.kit.Constants.TRANSACTION_ID;

import nl.jawsper.android.pebblemenu.menus.PebbleMenu;
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
		// Log.d( TAG, "onReceive: " + intent.toString() );
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
		else if( intent.getAction().equals( INTENT_APP_RECEIVE ) )
		{
			Log.d( TAG, intent.toString() );
			final UUID receivedUuid = (UUID)intent.getSerializableExtra( APP_UUID );
			if( !PebbleAgendaDisplay.PEBBLE_APP_UUID.equals( receivedUuid ) )
			{
				return;
			}

			final int transactionId = intent.getIntExtra( TRANSACTION_ID, -1 );
			final String jsonData = intent.getStringExtra( MSG_DATA );
			if( jsonData == null || jsonData.isEmpty() )
			{
				return;
			}
			Log.d( TAG, jsonData );

			try
			{
				final PebbleDictionary data = PebbleDictionary.fromJson( jsonData );
				PebbleKit.sendAckToPebble( context, transactionId );
				PebbleAgendaDisplay.updateAgenda( context, data );
			}
			catch( JSONException e )
			{
				e.printStackTrace();
				return;
			}
		}
		else if( intent.getAction().startsWith( "com.android.music" ) )
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

	private static final String player_package = "com.google.android.music";
	private static final String player_class = "com.google.android.music.playback.MediaButtonIntentReceiver";

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
	}
}
