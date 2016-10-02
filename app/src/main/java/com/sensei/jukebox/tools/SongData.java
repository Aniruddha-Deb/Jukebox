package com.sensei.jukebox.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.sensei.jukebox.Constants;
import com.sensei.jukebox.R;

import java.nio.ByteBuffer;

public class SongData {

    private String title = null;
    private String artist = null;
    private String album = null;
    private String genre = null;
    private byte[] albumArt = null;

    private Uri songLocation = null;

    MediaMetadataRetriever retriever = null;

    public SongData( Uri songLocation ) {
        this.songLocation = songLocation;
        getSongData();
        validateData();
    }

    private void getSongData() {
        retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource( Constants.APPLICATION_CONTEXT, songLocation );
            retrieveData();
        } catch (Exception e) {
            // swallow exception
        }
    }

    private void retrieveData() throws Exception{
        albumArt = retriever.getEmbeddedPicture();
        album = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_ALBUM );
        genre = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_GENRE );
        artist = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_ARTIST );
        title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
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
        if( albumArt == null ) {
            setNoAlbumArtIcon();
        }
        if( title == null ) {
            title = "<no title data>";
        }
    }

    private void setNoAlbumArtIcon() {
        Bitmap bm = BitmapFactory.decodeResource( Constants.APPLICATION_CONTEXT.getResources(),
                R.drawable.no_album_art_icon );
        int bitmapSize = bm.getByteCount();
        ByteBuffer bf = ByteBuffer.allocate( bitmapSize );

        bm.copyPixelsToBuffer( bf );
        albumArt = null;
    }

    public String getTitle() {
        return title;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setAlbumArt(byte[] albumArt) {
        this.albumArt = albumArt;
    }
}
