package com.sensei.jukebox;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sensei.jukebox.tools.Song;
import com.sensei.jukebox.tools.SongList;

import java.util.Collections;
import java.util.Comparator;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setUpUI();
        Constants.setApplicationContext( this );
        buildSongDatabase();
        startListSongsActivity();
        finish();
    }

    private void setUpUI() {
        setContentView( R.layout.activity_startup );
    }

    private void buildSongDatabase() {
        getSongs();
        sortSongs();
    }

    private void getSongs() {
        ContentResolver resolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = resolver.query( musicUri, null, null, null, null );

        if( cursor != null && cursor.moveToFirst() ) {
            int idColumn = cursor.getColumnIndex( MediaStore.Audio.Media._ID );
            int titleColumn = cursor.getColumnIndex( MediaStore.Audio.Media.TITLE );

            while( cursor.moveToNext() ) {
                long id = cursor.getLong( idColumn );
                String title = cursor.getString( titleColumn );
                Uri location = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id );

                Song song = new Song( location );
                song.getSongData().setTitle( title );
                // To soon be replaced by a database
                SongList.addSong( song );
            }
        }

        if ( cursor != null ) {
            cursor.close();
        }
    }

    private void sortSongs() {
        Collections.sort( SongList.getSongs(), new Comparator<Song>() {
            @Override
            public int compare(Song a, Song b) {
                return a.toString().compareTo(b.toString());
            }
        });
        for( int i=0; i<SongList.getSize(); i++ ) {
            SongList.getSong(i).setPosition( i );
        }
    }

    private void startListSongsActivity() {
        Intent intent = new Intent( this, ListSongsActivity.class );
        startActivity( intent );
    }

}
