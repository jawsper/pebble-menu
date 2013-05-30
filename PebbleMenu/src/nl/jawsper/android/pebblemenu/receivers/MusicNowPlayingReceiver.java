package nl.jawsper.android.pebblemenu.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MusicNowPlayingReceiver extends BroadcastReceiver
{
	@Override public void onReceive( Context context, Intent intent )
	{
		Log.d( getClass().getName(), intent.getAction() );
		intent.setAction( "nl.jawsper.android.MEDIA_UPDATE" );
		intent.setClass( context, MusicIntentReceiver.class );
		context.sendBroadcast( intent );
	}
}
