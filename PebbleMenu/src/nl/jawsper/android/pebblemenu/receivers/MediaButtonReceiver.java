package nl.jawsper.android.pebblemenu.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaButtonReceiver extends BroadcastReceiver
{
	@Override public void onReceive( Context context, Intent intent )
	{
		// send to Menu
		intent.setClass( context, MusicIntentReceiver.class );
		context.sendBroadcast( intent );
	}
}
