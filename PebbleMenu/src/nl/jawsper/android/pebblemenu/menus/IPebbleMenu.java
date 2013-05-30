package nl.jawsper.android.pebblemenu.menus;

import android.content.Context;

public interface IPebbleMenu
{
	void onShow( Context context );
	void onButtonPressed( Context context, PebbleButton button );
	
	String getTitle();
	String getTop();
	String getMiddle();
	String getBottom();
}
