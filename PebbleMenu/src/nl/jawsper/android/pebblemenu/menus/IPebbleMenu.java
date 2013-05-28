package nl.jawsper.android.pebblemenu.menus;

import android.content.Context;
import android.view.KeyEvent;

public interface IPebbleMenu
{
	void onShow( Context context );
	void onKeyEvent( Context context, KeyEvent keyEvent );
	
	String getTitle();
	String getTop();
	String getMiddle();
	String getBottom();
}
