package com.sensei.jukebox;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.sensei.jukebox.tools.Song;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {

    private Song song;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState == null ) {
            retrieveIntent();
        }
        else {
            song = Constants.songs.get( savedInstanceState.getInt( Constants.SONG_POSITION ) );
        }
        setUpUI();

        try {
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
        player.release();
    }

    public void setUpUI() {

        if( getResources().getDisplayMetrics().density <= 1.5 ) {
            setContentView(R.layout.activity_player);
        }
        else {
            setContentView(R.layout.activity_player_xhdpi);
        }

        ImageView artwork = (ImageView)findViewById( R.id.albumArt );
        TextView album = (TextView)findViewById( R.id.album );
        TextView title = (TextView)findViewById( R.id.title );
        TextView artist = (TextView)findViewById( R.id.artist );
        TextView genre = (TextView)findViewById( R.id.genre );

        album.setText ( song.getAlbum() );
        title.setText ( song.toString() );
        artist.setText( song.getArtist() );
        genre.setText( song.getGenre() );

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
        player = MediaPlayer.create( this, songUri );
        player.setAudioStreamType( AudioManager.STREAM_MUSIC );

        player.start();
    }

    public void pause(View view) {
        ImageButton b = (ImageButton)view;

        if( b.getTag().equals( "false" ) ) {
            player.pause();
            b.setTag( "true" );
            b.setImageResource( R.drawable.play );
            b.setScaleX( 0.75f );
            b.setScaleY( 0.75f );
        }
        else {
            player.start();
            b.setTag( "false" );
            b.setImageResource( R.drawable.pause );
            b.setScaleX( 0.75f );
            b.setScaleY( 0.75f );
        }
    }

    public void fastForward(View view) {
        player.seekTo( player.getCurrentPosition() + 5000 );
    }

    public void rewind(View view) {
        player.seekTo( player.getCurrentPosition() - 5000 );
    }

    public void nextSong(View view) {
        this.onStop();
        this.onDestroy();

        Bundle b = new Bundle();
        b.putInt( Constants.SONG_POSITION, song.getPosition() + 1 );
        this.onCreate( b );
    }

    public void previousSong(View view) {
        this.onStop();
        this.onDestroy();

        Bundle b = new Bundle();
        b.putInt( Constants.SONG_POSITION, song.getPosition() - 1 );
        this.onCreate( b );
    }
}
