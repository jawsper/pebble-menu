package nl.jawsper.android.pebblemenu.receivers;

import nl.jawsper.android.pebblemenu.PebbleMenu;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MusicNowPlayingReceiver extends BroadcastReceiver
{
	private static String TAG = "MusicNowPlayingReceiver";

	@Override public void onReceive( Context context, Intent intent )
	{
		Log.d( TAG, intent.getAction() );

		String artist = intent.getStringExtra( "artist" );
		String track = intent.getStringExtra( "track" );
		String album = intent.getStringExtra( "album" );
		Log.d( TAG, "NowPlaying = " + artist + ":" + album + ":" + track );

		PebbleMenu.getInstance().setNowPlaying( artist, track, album );
		PebbleMenu.getInstance().updateDisplay( context );
	}
}
