package com.sensei.jukebox.tools;

import android.content.ContentUris;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.sensei.jukebox.Constants;

public class Song {

    private Context context = null;

    private long id = 0;
    private Uri location = null;

    private String title = null;
    private String artist = null;
    private String album = null;
    private String genre = null;
    private byte[] albumArt = null;

    MediaMetadataRetriever retriever = null;

    private int position = 0;

    public Song( long id, Context context, String title ) {
        this.id = id;
        this.context = context;
        this.title = title;

        getSongData();
    }

    public Song( long id, Context context, String title, int position ) {
        this.id = id;
        this.context = context;
        this.title = title;
        this.position = position;

        getSongData();
    }

    private void getSongData() {
        initFileLocation();

        retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, location);
        } catch (Exception e) {
            // swallow exception
        }

        try {
            retrieveData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if( title != null ) {
            System.out.println( title + " is null");
        }
        System.out.println( "now validating data" );
        validateData();
        System.out.println( title + " after validating data" );
    }

    private void initFileLocation() {
        location = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id );
    }

    private void retrieveData() throws Exception{
        albumArt = retriever.getEmbeddedPicture();
        album = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_ALBUM );
        genre = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_GENRE );
        artist = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_ARTIST );

        if( title == null ) {
            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }
    }

    private void validateData() {
        if( album == null ) {
            album = "<no album data>";
        }
        if( artist == null ) {
            artist = "<no artist data>";
        }
        if( genre == null ) {
            genre = "<no genre data>";
        }
    }

    public static Song unwrapSong( Bundle bundle, Context context ) {
        return new Song( bundle.getLong( Constants.SONG_ID ),
                         context,
                         bundle.getString( Constants.SONG_TITLE ),
                         bundle.getInt( Constants.SONG_POSITION ) );
    }

    public static Bundle bundleSong( Song song, int position ) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SONG_TITLE,    song.toString());
        bundle.putInt   (Constants.SONG_POSITION, position);
        bundle.putLong  (Constants.SONG_ID,       song.getId());

        return bundle;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public byte[] getAlbumArt() {
        return albumArt;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition( int position ) {
        this.position = position;
    }

    @Override
    public String toString() {
        return title;
    }
}
