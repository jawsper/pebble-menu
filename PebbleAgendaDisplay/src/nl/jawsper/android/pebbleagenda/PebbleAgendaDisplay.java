package nl.jawsper.android.pebbleagenda;


import java.util.UUID;

import nl.jawsper.android.agendatools.CalendarEventInfo;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import android.content.Context;
import android.util.Log;

public class PebbleAgendaDisplay
{
	public static final UUID PEBBLE_APP_UUID = UUID.fromString( "dfe47513-555c-4dd2-8721-85a0bc4c710c" );
	private static final int[] LINES = { 40, 10, 25 };

	public static void updateAgenda( Context context, PebbleDictionary data )
	{
		CalendarEventInfo evt = CalendarEventInfo.readNextCalendarEvent( context );
		String[] lines = { "", "", "" };
		if( evt != null )
		{
			lines = new String[] { evt.getTitle(), evt.getLocation(), evt.getStartToEndFormatted() };
		}

		PebbleDictionary outData = new PebbleDictionary();
		for( int i = 0; i < lines.length; i++ )
		{
			if( i >= LINES.length ) break;
			String text = lines[i];
			if( text.length() > LINES[i] ) text = text.substring( 0, LINES[i] );
			outData.addString( i, text );
		}
		Log.d( "PebbleAgendaDisplay", outData.toJsonString() );
		PebbleKit.sendDataToPebble( context, PEBBLE_APP_UUID, outData );
	}
}
