package com.sensei.jukebox.tools;

import android.net.Uri;

public class Song {

    private Uri location = null;
    private int position = 0;
    private SongData songData = null;

    public Song( Uri uri ) {
        this.location = uri;
        songData = new SongData( location );
    }

    public int getPosition() {
        return position;
    }

    public void setPosition( int position ) {
        this.position = position;
    }

    public Uri getUri() {
        return location;
    }

    public SongData getSongData() {
        return songData;
    }

    @Override
    public String toString() {
        return songData.getTitle();
    }
}
