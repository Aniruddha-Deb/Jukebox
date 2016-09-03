package com.sensei.jukebox.tools;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaDataSource;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.sensei.jukebox.Constants;
import com.sensei.jukebox.ListSongsActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;

public class Song {

    private Context context = null;

    private long id = 0;
    private Uri location = null;
    private File file = null;
    private String fileLocation = null;

    private String title = null;
    private String artist = null;
    private String album = null;
    private String genre = null;
    private byte[] albumArt = null;

    MediaMetadataRetriever retriever = null;

    public Song(long id, Context context) {
        this.id = id;
        this.context = context;

        getSongData();
    }

    public Song( long id, Context context, String title ) {
        this.id = id;
        this.context = context;
        this.title = title;

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
        file = new File( location.toString() );
        fileLocation = file.getAbsolutePath();
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
        return new Song( bundle.getLong( Constants.SONG_ID ), context, bundle.getString( Constants.SONG_TITLE ) );
    }

    public static Bundle bundleSong( Song song ) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SONG_TITLE,  song.toString());
        bundle.putString(Constants.SONG_ALBUM,  song.getAlbum());
        bundle.putString(Constants.SONG_ARTIST, song.getArtist());
        bundle.putString(Constants.SONG_GENRE,  song.getGenre());
        bundle.putLong  (Constants.SONG_ID,     song.getId());

        bundle.putByteArray(Constants.SONG_ARTWORK, song.getAlbumArt());

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

    @Override
    public String toString() {
        return title;
    }
}
