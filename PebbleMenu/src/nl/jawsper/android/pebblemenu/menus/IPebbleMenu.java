package nl.jawsper.android.pebblemenu.menus;

import android.content.Context;

public interface IPebbleMenu
{
	void onShow( Context context );
	void onKeyEvent( Context context, int keyCode );
	
	String getTitle();
	String getTop();
	String getMiddle();
	String getBottom();
}
