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
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.sensei.jukebox.tools.Song;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl{

    private Song song;
    private MediaPlayer player;
    private MediaController controller;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        retrieveIntent();
        setUpUI();

        handler = new Handler();
        try {
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        controller.hide();
        player.stop();
        player.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
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
        player = MediaPlayer.create( this, songUri );
        player.setAudioStreamType( AudioManager.STREAM_MUSIC );
        player.setOnPreparedListener( this );

        controller = new MediaController( this );
        player.start();
    }

    //---------------- MediaPlayer Control methods -------------------------------------------------
    @Override
    public void start() {
        player.start();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        controller.setMediaPlayer( this );
        controller.setAnchorView( findViewById( R.id.media_controller_view ) );

        handler.post(new Runnable() {
            @Override
            public void run() {
                controller.setEnabled( true );
                controller.show();
            }
        });
    }
}
