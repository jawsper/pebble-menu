package nl.jawsper.android.pebblemenu.menus;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class PebbleMenu
{
	private static final String TAG = "PebbleMenu";
	private static final IPebbleMenu[] menus = { new VolumeMenu(), new AgendaMenu(), new PhoneFinderMenu() };
	private boolean inMediaMode = false;
	private int selectedMenu = 0;
	private IPebbleMenu currentMenu = null;

	public void onShow( Context context )
	{
		if( currentMenu != null ) currentMenu.onShow( context );
	}

	public boolean isMediaMode()
	{
		return inMediaMode;
	}

	public void onDoubleClick( Context context )
	{
		Log.d( TAG, "onDoubleClick" );
		if( inMediaMode )
		{
			inMediaMode = false;
		}
		else
		{
			if( currentMenu != null )
			{
				currentMenu = null;
			}
			else
			{
				inMediaMode = true;
			}
		}
		updateDisplay( context );
	}

	private String nowPlaying[] = new String[3];

	public void setNowPlaying( String artist, String track, String album )
	{
		nowPlaying[0] = artist;
		nowPlaying[1] = track;
		nowPlaying[2] = album;
	}

	public void updateDisplay( Context context )
	{
		if( inMediaMode )
		{
			if( nowPlaying[0] == null )
			{
				updateDisplay( context, "", "", "" );
			}
			else
			{
				updateDisplay( context, nowPlaying[0], nowPlaying[1], nowPlaying[2] );
			}
		}
		else
		{
			updateDisplay( context, getTop(), getMiddle(), getBottom() );
		}
	}

	private void updateDisplay( Context context, String artist, String track, String album )
	{
		Log.d( TAG, "updateDisplay" );
		final Intent i = new Intent( "com.getpebble.action.NOW_PLAYING" );
		i.putExtra( "artist", artist );
		i.putExtra( "track", track );
		i.putExtra( "album", album );
		context.sendBroadcast( i );
	}

	public void onKeyEvent( Context context, KeyEvent keyEvent )
	{
		Log.d( TAG, "onKeyEvent: " + keyEvent.getKeyCode() );
		if( inMediaMode )
		{

		}
		else
		{
			if( keyEvent.getAction() == KeyEvent.ACTION_DOWN )
			{
				if( currentMenu != null )
				{
					currentMenu.onKeyEvent( context, keyEvent.getKeyCode() );
				}
				else
				{
					switch( keyEvent.getKeyCode() )
					{
						case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
							currentMenu = menus[selectedMenu];
							currentMenu.onShow( context );
							break;
						case KeyEvent.KEYCODE_MEDIA_NEXT:
							selectedMenu--;
							if( selectedMenu < 0 ) selectedMenu = menus.length - 1;
							break;
						case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
							selectedMenu++;
							if( selectedMenu >= menus.length ) selectedMenu = 0;
							break;
					}
				}
			}
		}
		updateDisplay( context );
	}

	public String getTitle()
	{
		return getClass().getName();
	}

	public String getTop()
	{
		if( currentMenu != null )
		{
			return currentMenu.getTop();
		}
		return "Main menu";
	}

	public String getMiddle()
	{
		if( currentMenu != null )
		{
			return currentMenu.getMiddle();
		}
		return menus[selectedMenu].getTitle();
	}

	public String getBottom()
	{
		if( currentMenu != null )
		{
			return currentMenu.getBottom();
		}
		return String.format( Locale.getDefault(), "Menu %02d/%02d", selectedMenu + 1, menus.length );
	}

}
