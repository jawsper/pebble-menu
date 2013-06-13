package nl.jawsper.android.pebblemenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.jawsper.android.pebblemenu.menus.AgendaMenu;
import nl.jawsper.android.pebblemenu.menus.IPebbleMenu;
import nl.jawsper.android.pebblemenu.menus.PebbleButton;
import nl.jawsper.android.pebblemenu.menus.PhoneFinderMenu;
import nl.jawsper.android.pebblemenu.menus.VolumeMenu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.KeyEvent;

public class PebbleMenu
{
	private static PebbleMenu sInstance = new PebbleMenu();

	public static PebbleMenu getInstance()
	{
		return sInstance;
	}

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
		// Log.d( TAG, "onDoubleClick" );
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
		// Log.d( TAG, "updateDisplay" );
		final Intent i = new Intent( "com.getpebble.action.NOW_PLAYING" );
		i.putExtra( "artist", artist );
		i.putExtra( "track", track );
		i.putExtra( "album", album );
		context.sendBroadcast( i );
	}

	private void sendMediaKeyEvent( Context context, int action, int code )
	{
		KeyEvent keyEvent = new KeyEvent( action, code );
		Intent intent = new Intent( Intent.ACTION_MEDIA_BUTTON );
		intent.setClassName( m_MediaPlayer.getPackageName(), m_MediaPlayer.getClassName() );
		intent.putExtra( Intent.EXTRA_KEY_EVENT, keyEvent );
		context.sendBroadcast( intent );
	}

	public void onButtonPressed( Context context, KeyEvent originalEvent, PebbleButton button )
	{
		// Log.d( TAG, "onButtonPressed" );
		if( inMediaMode )
		{
			sendMediaKeyEvent( context, KeyEvent.ACTION_DOWN, originalEvent.getKeyCode() );
			sendMediaKeyEvent( context, KeyEvent.ACTION_UP, originalEvent.getKeyCode() );
		}
		else
		{
			if( currentMenu != null )
			{
				currentMenu.onButtonPressed( context, button );
			}
			else
			{
				switch( button )
				{
					case BUTTON_SELECT:
						currentMenu = menus[selectedMenu];
						currentMenu.onShow( context );
						break;
					case BUTTON_UP:
						selectedMenu--;
						if( selectedMenu < 0 ) selectedMenu = menus.length - 1;
						break;
					case BUTTON_DOWN:
						selectedMenu++;
						if( selectedMenu >= menus.length ) selectedMenu = 0;
						break;
					default:
						break;
				}
			}
		}
		updateDisplay( context );
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

	// media player stuff
	private MediaPlayer m_MediaPlayer;

	public void setMediaPlayer( MediaPlayer a_MediaPlayer )
	{
		Log.d( TAG, "Setting MediaPlayer to " + a_MediaPlayer.toString() );
		m_MediaPlayer = a_MediaPlayer;
	}

	public MediaPlayer getMediaPlayer()
	{
		return m_MediaPlayer;
	}

	public static List<MediaPlayer> getMediaPlayers( final Context context )
	{
		List<MediaPlayer> players = new ArrayList<MediaPlayer>();

		PackageManager mgr = context.getPackageManager();

		final Intent mediaButtons = new Intent( "android.intent.action.MEDIA_BUTTON" );
		for( ResolveInfo nfo : mgr.queryBroadcastReceivers( mediaButtons, 0 ) )
		{
			ActivityInfo activity = nfo.activityInfo;
			if( activity != null && activity.applicationInfo != null )
			{
				if( activity.packageName.startsWith( "nl.jawsper.android.pebblemenu" ) ) continue;
				players.add( new MediaPlayer( activity.applicationInfo.loadLabel( mgr ).toString(), activity.packageName, activity.name ) );
			}
		}

		return players;
	}
}
