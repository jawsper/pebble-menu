package nl.jawsper.android.pebbleagenda.receivers;

import static com.getpebble.android.kit.Constants.APP_UUID;
import static com.getpebble.android.kit.Constants.INTENT_APP_RECEIVE;
import static com.getpebble.android.kit.Constants.MSG_DATA;
import static com.getpebble.android.kit.Constants.TRANSACTION_ID;

import java.util.UUID;

import nl.jawsper.android.pebbleagenda.PebbleAgendaDisplay;

import org.json.JSONException;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PebbleReceiver extends BroadcastReceiver
{
	private static final String TAG = "PebbleReceiver";

	@Override public void onReceive( Context context, Intent intent )
	{
		Log.d( getClass().getName(), intent.toString() );
		if( intent.getAction().equals( INTENT_APP_RECEIVE ) )
		{
			Log.d( TAG, intent.toString() );
			final UUID receivedUuid = (UUID)intent.getSerializableExtra( APP_UUID );
			if( !PebbleAgendaDisplay.PEBBLE_APP_UUID.equals( receivedUuid ) )
			{
				return;
			}

			final int transactionId = intent.getIntExtra( TRANSACTION_ID, -1 );
			final String jsonData = intent.getStringExtra( MSG_DATA );
			if( jsonData == null || jsonData.isEmpty() )
			{
				return;
			}
			Log.d( TAG, jsonData );

			try
			{
				final PebbleDictionary data = PebbleDictionary.fromJson( jsonData );
				PebbleKit.sendAckToPebble( context, transactionId );
				PebbleAgendaDisplay.updateAgenda( context, data );
			}
			catch( JSONException e )
			{
				e.printStackTrace();
				return;
			}
		}
	}
}
