package nl.jawsper.android.pebblemenu;

import java.util.ArrayList;
import java.util.List;

import nl.jawsper.android.pebblemenu.receivers.MusicIntentReceiver;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
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

		final List<MediaPlayer> mediaPlayers = getMediaPlayers();
		final ListView listView = (ListView)findViewById( R.id.mediaPlayerList );
		final ArrayAdapter<MediaPlayer> arrayAdapter = new ArrayAdapter<MediaPlayer>( this, android.R.layout.simple_list_item_1, mediaPlayers );
		listView.setAdapter( arrayAdapter );

		listView.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{
			@Override public void onItemClick( AdapterView<?> parent, View view, int position, long id )
			{
				final MediaPlayer item = (MediaPlayer)parent.getItemAtPosition( position );
				Toast.makeText( MainActivity.this, "Clicked: " + item.getName() + "\nPackageName: " + item.getPackageName() + "\nClassName: " + item.getClassName(), Toast.LENGTH_LONG ).show();
				MusicIntentReceiver.setPlayer( item.getPackageName(), item.getClassName() );
			}
		} );
	}

	@Override public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	class MediaPlayer
	{
		private String m_Name;
		private String m_PackageName;
		private String m_ClassName;

		public MediaPlayer( String a_Name, String a_PackageName, String a_ClassName )
		{
			m_Name = a_Name;
			m_PackageName = a_PackageName;
			m_ClassName = a_ClassName;
		}

		public String getName()
		{
			return m_Name;
		}

		public void setName( String a_Name )
		{
			this.m_Name = a_Name;
		}

		public String getPackageName()
		{
			return m_PackageName;
		}

		public void setPackageName( String a_PackageName )
		{
			this.m_PackageName = a_PackageName;
		}

		public String getClassName()
		{
			return m_ClassName;
		}

		public void setClassName( String a_ClassName )
		{
			this.m_ClassName = a_ClassName;
		}

		@Override public String toString()
		{
			return m_Name;
		}
	}

	private List<MediaPlayer> getMediaPlayers()
	{
		List<MediaPlayer> players = new ArrayList<MainActivity.MediaPlayer>();

		PackageManager mgr = getPackageManager();

		final Intent mediaButtons = new Intent( "android.intent.action.MEDIA_BUTTON" );
		for( ResolveInfo nfo : mgr.queryBroadcastReceivers( mediaButtons, 0 ) )
		{
			ActivityInfo activity = nfo.activityInfo;
			if( activity != null && activity.applicationInfo != null )
			{
				if( activity.packageName.startsWith( "nl.jawsper.android.pebblemenu" ) ) continue;
				players.add( new MediaPlayer( activity.applicationInfo.loadLabel( getPackageManager() ).toString(), activity.packageName, activity.name ) );
			}
		}

		return players;
	}
}
