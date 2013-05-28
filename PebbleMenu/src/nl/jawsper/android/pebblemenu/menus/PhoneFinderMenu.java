package nl.jawsper.android.pebblemenu.menus;

import android.content.Context;
import android.os.Vibrator;
import android.view.KeyEvent;

public class PhoneFinderMenu implements IPebbleMenu
{
	private boolean vibrating = false;

	@Override public void onShow( Context context )
	{
		// TODO Auto-generated method stub

	}

	@Override public void onKeyEvent( Context context, KeyEvent keyEvent )
	{
		if( keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE )
		{
			Vibrator vib = (Vibrator)context.getSystemService( Context.VIBRATOR_SERVICE );
			if( vibrating )
			{
				vib.cancel();
			}
			else
			{
				vib.vibrate( new long[] { 0, 200, 500 }, 0 );
			}
			vibrating = !vibrating;
		}
	}

	@Override public String getTitle()
	{
		return "Phone finder";
	}

	@Override public String getTop()
	{
		return "Phone finder";
	}

	@Override public String getMiddle()
	{
		return vibrating ? "Press to stop noise" : "Press to start noise";
	}

	@Override public String getBottom()
	{
		return vibrating ? "Vibrating" : "";
	}

}
