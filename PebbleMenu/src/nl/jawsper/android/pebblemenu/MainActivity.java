package nl.jawsper.android.pebblemenu;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private static final String TAG = "MainActivity";

	@Override protected void onCreate( Bundle savedInstanceState )
	{
		Log.v( TAG, "onCreate" );

		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );

		final List<MediaPlayer> mediaPlayers = PebbleMenu.getMediaPlayers( this );
		final ListView listView = (ListView)findViewById( R.id.mediaPlayerList );
		final ArrayAdapter<MediaPlayer> arrayAdapter = new ArrayAdapter<MediaPlayer>( this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, mediaPlayers );
		listView.setAdapter( arrayAdapter );
		listView.setChoiceMode( ListView.CHOICE_MODE_SINGLE );
		for( int i = 0; i < listView.getCount(); i++ )
		{
			if( ( (MediaPlayer)listView.getItemAtPosition( i ) ).getPackageName().equals( preferences.getString( "playerPackageName", "" ) ) )
			{
				PebbleMenu.getInstance().setMediaPlayer( (MediaPlayer)listView.getItemAtPosition( i ) );
				listView.setItemChecked( i, true );
				break;
			}
		}

		listView.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{
			@Override public void onItemClick( AdapterView<?> parent, View view, int position, long id )
			{
				final MediaPlayer item = (MediaPlayer)parent.getItemAtPosition( position );
				Toast.makeText( MainActivity.this, "Clicked: " + item.getName() + "\nPackageName: " + item.getPackageName() + "\nClassName: " + item.getClassName(), Toast.LENGTH_LONG ).show();
				PebbleMenu.getInstance().setMediaPlayer( item );
				Editor edit = preferences.edit();
				edit.putString( "playerPackageName", item.getPackageName() );
				edit.putString( "playerClassName", item.getClassName() );
				edit.commit();
			}
		} );

	}

	@Override public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}
}
