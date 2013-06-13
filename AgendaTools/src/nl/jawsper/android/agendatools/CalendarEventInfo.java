package nl.jawsper.android.agendatools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

public class CalendarEventInfo
{
	// private static final String[] projection = new String[] { CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.EVENT_LOCATION };
	long id;
	String title;
	Date dtstart;
	Date dtend;
	String location;

	public CalendarEventInfo( Cursor cursor )
	{
		this.id = cursor.getLong( cursor.getColumnIndex( CalendarContract.Events._ID ) );
		this.title = cursor.getString( cursor.getColumnIndex( CalendarContract.Events.TITLE ) );
		this.dtstart = new Date( cursor.getLong( cursor.getColumnIndex( CalendarContract.Events.DTSTART ) ) );
		this.dtend = new Date( cursor.getLong( cursor.getColumnIndex( CalendarContract.Events.DTEND ) ) );
		this.location = cursor.getString( cursor.getColumnIndex( CalendarContract.Events.EVENT_LOCATION ) );
	}

	public long getID()
	{
		return this.id;
	}

	public String getTitle()
	{
		return this.title;
	}

	public Date getDtstart()
	{
		return this.dtstart;
	}

	public String getDtstartFormatted()
	{
		return getDateFormatted( this.dtstart, "EEE HH:mm" );
	}

	public String getDtendFormatted()
	{
		return getDateFormatted( this.dtend, "HH:mm" );
	}

	private String getDateFormatted( Date date, String format )
	{
		return new SimpleDateFormat( format, Locale.getDefault() ).format( date );
	}

	@SuppressWarnings( "deprecation" ) public String getStartToEndFormatted()
	{
		String fmt_start = "EEE HH:mm";
		String fmt_end = "HH:mm";
		if( this.dtstart.getDate() != this.dtend.getDate() )
		{
			fmt_end = fmt_start;
		}
		return String.format( Locale.getDefault(), "%s - %s", getDateFormatted( this.dtstart, fmt_start ), getDateFormatted( this.dtend, fmt_end ) );
	}

	public byte[] getPebbleCalendarTime( int which )
	{
		Calendar cal = Calendar.getInstance( Locale.getDefault() );
		cal.setTimeInMillis( ( which == 0 ? this.dtstart : this.dtend ).getTime() );
		byte[] data = new byte[6];

		data[0] = (byte)( cal.get( Calendar.YEAR ) >> 8 );
		data[1] = (byte)( cal.get( Calendar.YEAR ) & 0xFF );
		data[2] = (byte)( cal.get( Calendar.MONTH ) & 0xFF );
		data[3] = (byte)( cal.get( Calendar.DAY_OF_MONTH ) & 0xFF );
		data[4] = (byte)( cal.get( Calendar.HOUR ) & 0xFF );
		data[5] = (byte)( cal.get( Calendar.MINUTE ) & 0xFF );

		return data;
	}

	public String getLocation()
	{
		return this.location;
	}

	@Override public boolean equals( Object o )
	{
		if( o instanceof CalendarEventInfo )
		{
			if( ( (CalendarEventInfo)o ).id == this.id ) return true;
		}
		return false;
	}

	@Override public String toString()
	{
		return String.format( Locale.getDefault(), "%d; %s; %s; %s", this.id, this.title, this.getStartToEndFormatted(), this.location );
	}

	public static CalendarEventInfo readNextCalendarEvent( Context context )
	{
		return readNextCalendarEvent( context, CalendarInfo.getVisibleCalendars( context ) );
	}

	public static CalendarEventInfo readNextCalendarEvent( Context context, int[] calendars )
	{
		List<CalendarEventInfo> events = readCalendarEvents( context, calendars, 1 );
		if( events.size() > 0 ) return events.get( 0 );
		return null;
	}

	public static List<CalendarEventInfo> readCalendarEvents( Context context, int count )
	{
		return readCalendarEvents( context, CalendarInfo.getVisibleCalendars( context ), count );
	}

	private static String makePlaceholders( int len )
	{
		if( len < 1 )
		{
			// It will lead to an invalid query anyway ..
			throw new RuntimeException( "No placeholders" );
		}
		else
		{
			StringBuilder sb = new StringBuilder( len * 2 - 1 );
			sb.append( "?" );
			for( int i = 1; i < len; i++ )
			{
				sb.append( ",?" );
			}
			return sb.toString();
		}
	}

	static class QueryBuilder
	{
		private StringBuilder sb = new StringBuilder();
		private List<String> values = new ArrayList<String>();

		QueryBuilder()
		{

		}

		private void append( String glue, String key, String comparer, String value )
		{
			append( glue, key, comparer, value, "?" );
		}

		private void append( String glue, String key, String comparer, String value, String placeholders )
		{
			if( sb.length() > 0 ) sb.append( glue );
			sb.append( "( " );
			sb.append( key );
			sb.append( " " );
			sb.append( comparer );
			sb.append( " " );
			sb.append( placeholders );
			sb.append( " )" );

			values.add( value );
		}

		public void andString( String key, String comparer, String value )
		{
			append( " AND ", key, comparer, value );
		}

		public void andInIntList( String key, int[] values )
		{
			if( sb.length() > 0 ) sb.append( " AND " );
			sb.append( "( " );
			sb.append( key );
			sb.append( " in ( " );
			sb.append( makePlaceholders( values.length ) );
			sb.append( " ) )" );
			for( int value : values )
			{
				this.values.add( Integer.toString( value ) );
			}
		}

		public void andLong( String key, String comparer, Long value )
		{
			append( " AND ", key, comparer, Long.toString( value ) );
		}

		public String getSelection()
		{
			return sb.toString();
		}

		public String[] getSelectionArgs()
		{
			String[] args = new String[this.values.size()];
			for( int i = 0; i < this.values.size(); i++ )
			{
				args[i] = this.values.get( i );
				Log.d( "Arrrg", args[i] );
			}
			return args;
		}
	}

	public static List<CalendarEventInfo> readCalendarEvents( Context context, int[] calendars, int count )
	{
		List<CalendarEventInfo> events = new ArrayList<CalendarEventInfo>();

		if( context != null )
		{
			Uri uri = CalendarContract.Events.CONTENT_URI;

			QueryBuilder builder = new QueryBuilder();

			// Calendar cal = Calendar.getInstance();
			// cal.add( Calendar.DAY_OF_MONTH, -7 );

			// builder.andLong( CalendarContract.Events.DTSTART, ">", Calendar.getInstance().getTime().getTime() );
			// builder.andLong( CalendarContract.Events.DTEND, ">=", cal.getTime().getTime() );

			builder.andInIntList( CalendarContract.Events.CALENDAR_ID, calendars );

			String sortOrder = CalendarContract.Events.DTSTART + " ASC";

			CursorLoader cl = new CursorLoader( context, uri, null, builder.getSelection(), builder.getSelectionArgs(), sortOrder );
			Cursor cursor = cl.loadInBackground();
			if( cursor.moveToFirst() )
			{
				long now = Calendar.getInstance().getTimeInMillis();
				while( cursor.moveToNext() && events.size() < count )
				{
					// long dtstart = cursor.getLong( cursor.getColumnIndex( CalendarContract.Events.DTSTART ) );
					long dtend = cursor.getLong( cursor.getColumnIndex( CalendarContract.Events.DTEND ) );
					if( dtend >= now )
					{
						events.add( new CalendarEventInfo( cursor ) );
					}
				}

			}
			cursor.close();
		}

		return events;
	}
}
