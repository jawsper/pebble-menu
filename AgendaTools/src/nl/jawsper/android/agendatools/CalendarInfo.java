package nl.jawsper.android.agendatools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.ArrayAdapter;

public class CalendarInfo
{
	private int id;
	private String name;
	private boolean visible;

	public CalendarInfo( int id, String name, boolean visible )
	{
		this.id = id;
		this.name = name;
		this.visible = visible;
	}

	public int getID()
	{
		return this.id;
	}

	public String getName()
	{
		return this.name;
	}

	public boolean isVisible()
	{
		return this.visible;
	}

	public void setVisible( boolean visible )
	{
		this.visible = visible;
	}

	public String toString()
	{
		return Integer.toString( id ) + " " + name;
	}

	public static List<CalendarInfo> readCalendarData( Context context )
	{
		List<CalendarInfo> list = new ArrayList<CalendarInfo>();

		Uri uri = CalendarContract.Calendars.CONTENT_URI;
		String[] projection = new String[] { CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.VISIBLE };

		CursorLoader cl = new CursorLoader( context, uri, projection, null, null, null );
		Cursor cursor = cl.loadInBackground();
		while( cursor.moveToNext() )
		{
			list.add( new CalendarInfo( cursor.getInt( 0 ), cursor.getString( 1 ), ( cursor.getInt( 2 ) == 1 ) ? true : false ) );
		}
		cursor.close();

		return list;
	}

	public static int[] getVisibleCalendars( Context context )
	{
		List<CalendarInfo> calendars = readCalendarData( context );
		int count = 0;
		for( CalendarInfo calendar : calendars )
		{
			if( calendar.isVisible() ) count++;
		}
		int[] list = new int[count];
		count = 0;
		for( CalendarInfo calendar : calendars )
		{
			if( calendar.isVisible() ) list[count++] = calendar.getID();
		}

		return list;
	}

	public static void readCalendarData( Context context, ArrayAdapter<CalendarInfo> adapter )
	{
		Uri uri = CalendarContract.Calendars.CONTENT_URI;
		String[] projection = new String[] { CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.VISIBLE };

		adapter.clear();

		CursorLoader cl = new CursorLoader( context, uri, projection, null, null, null );
		Cursor cursor = cl.loadInBackground();

		while( cursor.moveToNext() )
		{
			adapter.add( new CalendarInfo( cursor.getInt( 0 ), cursor.getString( 1 ), ( cursor.getInt( 2 ) == 1 ) ? true : false ) );
		}
		cursor.close();

		adapter.notifyDataSetChanged();
	}
}
