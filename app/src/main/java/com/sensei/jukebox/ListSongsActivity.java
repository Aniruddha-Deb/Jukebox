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
import android.view.View;
import android.widget.ListView;

import com.sensei.jukebox.tools.Song;
import com.sensei.jukebox.tools.SongAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListSongsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.songs = getSongs();
        sortSongs();
        SongAdapter adapter = new SongAdapter( this, Constants.songs );
        getListView().setAdapter( adapter );
    }

    @Override
    public void onListItemClick(ListView listView,
                                View itemView,
                                int position,
                                long id) {
        Song song = Constants.songs.get( position );
        if( serviceIsRunning() ) {
            stopService( new Intent( this, PlayerService.class ) );
        }

        Intent intent = new Intent( this, PlayerActivity.class );
        intent.putExtra( Constants.BUNDLE, Song.bundleSong( song, position ) );
        startActivity( intent );
    }

    private ArrayList<Song> getSongs() {
        ArrayList<Song> songs = new ArrayList<>();

        ContentResolver resolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query( musicUri, null, null, null, null );

        if( cursor != null && cursor.moveToFirst() ) {
            int idColumn = cursor.getColumnIndex( MediaStore.Audio.Media._ID );
            int titleColumn = cursor.getColumnIndex( MediaStore.Audio.Media.TITLE );

            while( cursor.moveToNext() ) {
                long id = cursor.getLong( idColumn );
                String title = cursor.getString( titleColumn );

                songs.add( new Song( id, this, title ) );
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return songs;
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

    private void sortSongs() {
        Collections.sort(Constants.songs, new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.toString().compareTo(b.toString());
            }
        });
        for( int i=0; i<Constants.songs.size(); i++ ) {
            Constants.songs.get(i).setPosition( i );
        }
    }
}
