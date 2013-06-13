package nl.jawsper.android.pebblemenu.menus;

import android.content.Context;
import android.media.AudioManager;

public class VolumeMenu implements IPebbleMenu
{
	private int currentVolume = -1;

	@Override public void onShow( Context context )
	{
		AudioManager mgr = (AudioManager)context.getSystemService( Context.AUDIO_SERVICE );
		currentVolume = mgr.getStreamVolume( AudioManager.STREAM_MUSIC );
	}

	@Override public void onButtonPressed( Context context, PebbleButton button )
	{
		AudioManager mgr = (AudioManager)context.getSystemService( Context.AUDIO_SERVICE );
		switch( button )
		{
			case BUTTON_SELECT:
				mgr.setStreamMute( AudioManager.STREAM_MUSIC, currentVolume > 0 );
				break;
			case BUTTON_UP:
				mgr.adjustStreamVolume( AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI );
				break;
			case BUTTON_DOWN:
				mgr.adjustStreamVolume( AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI );
				break;
			default:
				break;
		}
		currentVolume = mgr.getStreamVolume( AudioManager.STREAM_MUSIC );
	}

	@Override public String getTitle()
	{
		return "Media volume";
	}

	@Override public String getTop()
	{
		return "Media volume";
	}

	@Override public String getMiddle()
	{
		return "Current: " + currentVolume;
	}

	@Override public String getBottom()
	{
		return "Up/down to change";
	}
}
