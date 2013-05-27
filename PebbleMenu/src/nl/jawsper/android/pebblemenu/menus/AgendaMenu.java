package nl.jawsper.android.pebblemenu.menus;

import java.util.List;

import nl.jawsper.android.agendatools.CalendarEventInfo;

import android.content.Context;
import android.view.KeyEvent;

public class AgendaMenu implements IPebbleMenu
{
	private List<CalendarEventInfo> events;
	int currentEvent = 0;

	@Override public void onShow( Context context )
	{
		events = CalendarEventInfo.readCalendarEvents( context, 5 );
	}

	@Override public void onKeyEvent( Context context, int keyCode )
	{
		switch( keyCode )
		{
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				break;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				currentEvent--;
				if( currentEvent < 0 ) currentEvent = events.size() - 1;
				break;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				currentEvent++;
				if( currentEvent >= events.size() ) currentEvent = 0;
				break;
		}
	}

	@Override public String getTitle()
	{
		return "Agenda";
	}

	@Override public String getTop()
	{
		return "Agenda";
	}

	@Override public String getMiddle()
	{
		if( events.size() == 0 ) return "No events soon";
		CalendarEventInfo event = events.get( currentEvent );
		return event.getLocation() + " @ " + event.getStartToEndFormatted();
	}

	@Override public String getBottom()
	{
		if( events.size() == 0 ) return "What";
		return events.get( currentEvent ).getTitle();
	}

}
