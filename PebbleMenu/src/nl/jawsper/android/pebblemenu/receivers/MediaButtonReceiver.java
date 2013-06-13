package nl.jawsper.android.pebblemenu.receivers;

import java.util.Date;

import nl.jawsper.android.pebblemenu.PebbleMenu;
import nl.jawsper.android.pebblemenu.menus.PebbleButton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;

public class MediaButtonReceiver extends BroadcastReceiver
{

	private static final int doubleClickSpeed = 500;

	private static long lastButtonPressed = 0;
	private static Runnable doubleClickTimeout = null;

	private static Handler handler = new Handler();

	@Override public void onReceive( final Context context, final Intent intent )
	{
		if( intent.getAction() == Intent.ACTION_MEDIA_BUTTON )
		{
			final KeyEvent keyEvent = (KeyEvent)intent.getExtras().get( Intent.EXTRA_KEY_EVENT );
			// Log.d( getClass().getName(), "KeyEvent: " + Integer.toString( keyEvent.getAction() ) + "; " + keyEvent.getKeyCode() );

			if( keyEvent.getAction() == KeyEvent.ACTION_DOWN )
			{
				if( keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE )
				{
					if( lastButtonPressed > 0 )
					{
						long now = new Date().getTime();
						if( now - lastButtonPressed < doubleClickSpeed )
						{
							sendButton( context, null, PebbleButton.BUTTON_SELECT_DOUBLECLICK );
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
								doubleClickTimeout = null;
								lastButtonPressed = 0;

								sendButton( context, keyEvent, PebbleButton.BUTTON_SELECT );
							}
						};
						handler.postAtTime( doubleClickTimeout, SystemClock.uptimeMillis() + doubleClickSpeed );
					}
				}
				else
				{
					switch( keyEvent.getKeyCode() )
					{
						case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
							sendButton( context, keyEvent, PebbleButton.BUTTON_UP );
							break;
						case KeyEvent.KEYCODE_MEDIA_NEXT:
							sendButton( context, keyEvent, PebbleButton.BUTTON_DOWN );
							break;
					}
				}
			}
		}
	}

	private void sendButton( final Context context, KeyEvent originalEvent, PebbleButton button )
	{
		// Log.d( getClass().getName(), "Sending button: " + button.toString() );
		switch( button )
		{
			case BUTTON_SELECT_DOUBLECLICK:
				PebbleMenu.getInstance().onDoubleClick( context );
				break;
			default:
				PebbleMenu.getInstance().onButtonPressed( context, originalEvent, button );
				break;
		}
		PebbleMenu.getInstance().updateDisplay( context );
	}
}
