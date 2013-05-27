package nl.jawsper.android.pebblemenu.menus;

import android.content.Context;
import android.media.AudioManager;
import android.view.KeyEvent;

public class VolumeMenu implements IPebbleMenu
{
	private int currentVolume = -1;

	@Override public void onShow( Context context )
	{
		AudioManager mgr = (AudioManager)context.getSystemService( Context.AUDIO_SERVICE );
		currentVolume = mgr.getStreamVolume( AudioManager.STREAM_MUSIC );
	}

	@Override public void onKeyEvent( Context context, int keyCode )
	{
		AudioManager mgr = (AudioManager)context.getSystemService( Context.AUDIO_SERVICE );
		switch( keyCode )
		{
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				mgr.setStreamMute( AudioManager.STREAM_MUSIC, currentVolume > 0 );
				break;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				mgr.adjustStreamVolume( AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI );
				break;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				mgr.adjustStreamVolume( AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI );
				break;
		}
		currentVolume = mgr.getStreamVolume( AudioManager.STREAM_MUSIC );
	}

	@Override public String getTitle()
	{
		return "Volume";
	}

	@Override public String getTop()
	{
		return "Volume";
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
