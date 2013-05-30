package nl.jawsper.android.pebblemenu.receivers;

import nl.jawsper.android.pebblemenu.PebbleMenu;
import nl.jawsper.android.pebblemenu.menus.PebbleButton;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class MusicIntentReceiver extends BroadcastReceiver
{
	private static final PebbleMenu pebbleMenu = new PebbleMenu();

	private static final String TAG = "MusicIntentReceiver";

	@Override public void onReceive( final Context context, Intent intent )
	{
		// Log.d( TAG, "onReceive: " + intent.getAction() );
		if( "nl.jawsper.android.pebblemenu.PEBBLE_BUTTON".equals( intent.getAction() ) )
		{
			PebbleButton button = (PebbleButton)intent.getSerializableExtra( "PEBBLE_BUTTON" );
			KeyEvent originalEvent = (KeyEvent)intent.getParcelableExtra( Intent.EXTRA_KEY_EVENT );
			switch( button )
			{
				case BUTTON_SELECT_DOUBLECLICK:
					pebbleMenu.onDoubleClick( context );
					break;
				default:
					pebbleMenu.onButtonPressed( context, originalEvent, button );
					break;
			}
		}
		else if( intent.getAction().endsWith( ".metachanged" ) )
		{
			Log.d( TAG, intent.getAction() );
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
}
