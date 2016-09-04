package com.sensei.jukebox;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensei.jukebox.tools.Song;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {

    private Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        retrieveIntent();
        setUpUI();

        try {
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpUI() {
        ImageView artwork = (ImageView)findViewById( R.id.albumArt );
        TextView album = (TextView)findViewById( R.id.album );
        TextView title = (TextView)findViewById( R.id.title );
        TextView artist = (TextView)findViewById( R.id.artist );

        album.setText ( song.getAlbum() );
        title.setText ( song.toString() );
        artist.setText( song.getArtist() );

        if( song.getAlbumArt() == null ) {
            artwork.setImageResource( R.drawable.no_album_art_icon );
        }
        else {
            Bitmap bm = BitmapFactory.decodeByteArray(song.getAlbumArt(), 0, song.getAlbumArt().length);
            artwork.setImageBitmap(bm);
        }
    }

    private void retrieveIntent() {
        Intent intent = getIntent();
        song = Song.unwrapSong( intent.getBundleExtra(Constants.BUNDLE), this );
    }

    private void playMusic() throws IOException {
        Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.getId() );
        MediaPlayer player = MediaPlayer.create( this, songUri );
        player.setAudioStreamType( AudioManager.STREAM_MUSIC );
        player.start();
    }


}
