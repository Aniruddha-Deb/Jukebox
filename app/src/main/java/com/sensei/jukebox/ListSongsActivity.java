package com.sensei.jukebox;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.sensei.jukebox.tools.Song;
import com.sensei.jukebox.tools.SongAdapter;
import com.sensei.jukebox.tools.SongList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        // To be replaced by database
        Song song = SongList.getSong( songPosition );

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
