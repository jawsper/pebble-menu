package nl.jawsper.android.pebblemenu.menus;

import java.util.Locale;

import android.content.Context;
import android.view.KeyEvent;

public class PebbleMenu implements IPebbleMenu
{
	private static final IPebbleMenu[] menus = { new VolumeMenu(), new AgendaMenu(), new PhoneFinderMenu() };
	private int selectedMenu = 0;
	private IPebbleMenu currentMenu = null;

	@Override public void onShow( Context context )
	{
		if( currentMenu != null ) currentMenu.onShow( context );
	}

	public boolean onDoubleClick()
	{
		if( currentMenu != null )
		{
			currentMenu = null;
			return false;
		}
		return true;
	}

	@Override public void onKeyEvent( Context context, int keyCode )
	{
		if( currentMenu != null )
		{
			currentMenu.onKeyEvent( context, keyCode );
		}
		switch( keyCode )
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

	@Override public String getTitle()
	{
		return getClass().getName();
	}

	@Override public String getTop()
	{
		if( currentMenu != null )
		{
			return currentMenu.getTop();
		}
		return "Main menu";
	}

	@Override public String getMiddle()
	{
		if( currentMenu != null )
		{
			return currentMenu.getMiddle();
		}
		return menus[selectedMenu].getTitle();
	}

	@Override public String getBottom()
	{
		if( currentMenu != null )
		{
			return currentMenu.getBottom();
		}
		return String.format( Locale.getDefault(), "Menu %02d/%02d", selectedMenu + 1, menus.length );
	}

}
