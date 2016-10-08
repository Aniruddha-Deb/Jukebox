package com.sensei.jukebox;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.sensei.jukebox.tools.SongAdapter;

public class ListSongsActivity extends ListActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        Log.d( "l", "started ListSongsActivity" );
        setListViewAdapter();
    }

    private void setListViewAdapter() {
        SongAdapter adapter = new SongAdapter( this );
        getListView().setAdapter( adapter );
    }

    @Override
    public void onListItemClick(ListView listView, View itemView,
                                int position, long id) {
        startPlayerActivity( position );
    }

    private void startPlayerActivity( int songPosition ) {
        if( serviceIsRunning() ) {
            stopService( new Intent( this, PlayerService.class ) );
        }

        Intent intent = new Intent( this, PlayerActivity.class );
        intent.putExtra( Constants.SONG_POSITION, songPosition );
        startActivity( intent );
    }

    private boolean serviceIsRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (PlayerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
