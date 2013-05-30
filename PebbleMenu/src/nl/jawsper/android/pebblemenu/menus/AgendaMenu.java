package nl.jawsper.android.pebblemenu.menus;

import java.util.List;

import nl.jawsper.android.agendatools.CalendarEventInfo;

import android.content.Context;

public class AgendaMenu implements IPebbleMenu
{
	private List<CalendarEventInfo> events;
	int currentEvent = 0;

	@Override public void onShow( Context context )
	{
		events = CalendarEventInfo.readCalendarEvents( context, 5 );
	}

	@Override public void onButtonPressed( Context context, PebbleButton button )
	{
		switch( button )
		{
			case BUTTON_SELECT:
				break;
			case BUTTON_UP:
				currentEvent--;
				if( currentEvent < 0 ) currentEvent = events.size() - 1;
				break;
			case BUTTON_DOWN:
				currentEvent++;
				if( currentEvent >= events.size() ) currentEvent = 0;
				break;
			default:
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
