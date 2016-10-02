package com.sensei.jukebox;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sensei.jukebox.tools.Song;
import com.sensei.jukebox.tools.SongList;

import java.util.Locale;

public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private Song song;
    private MediaPlayer player;

    private SeekBar seekBar;
    private TextView timeLeft;
    private TextView timePassed;

    private boolean wasRunning = false;

    private PlayerService service;
    private ServiceConnection connection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpServiceConnection();
        checkForPreviousInstance( savedInstanceState );
        connectToService( song.getPosition() );
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbindService( connection );
        super.onDestroy();
    }

    //-------------------------------- Connection helper methods -----------------------------------

    private void setUpServiceConnection() {
        this.connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) iBinder;
                service = binder.getService();
                player = service.getPlayer();

                setUpUI();
                updateUIComponents();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                // nothing as of now
            }

        };
    }

    private void checkForPreviousInstance( Bundle savedInstanceState ) {
        if( savedInstanceState == null ) {
            retrieveIntent();
        }
        else {
            song = SongList.getSong( savedInstanceState.getInt( Constants.SONG_POSITION ) );
            wasRunning = false;
        }
    }

    private void retrieveIntent() {
        Intent intent = getIntent();
        song = SongList.getSong( intent.getExtras().getInt( Constants.SONG_POSITION ) );
        wasRunning = intent.getBooleanExtra( Constants.IS_RUNNING, false );
    }

    private void connectToService( int songPosition ) {
        Intent intent = new Intent( this, PlayerService.class );
        intent.putExtra( Constants.SONG_POSITION, songPosition );

        if( wasRunning ) {
            bindService( intent, connection, Context.BIND_AUTO_CREATE );
        }
        else {
            startService(intent);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    //-------------------------------- UI setup methods --------------------------------------------

    public void setUpUI() {

        setContentView(R.layout.activity_player);

        ImageView artwork = (ImageView)findViewById( R.id.albumArt );
        TextView album = (TextView)findViewById( R.id.album );
        TextView title = (TextView)findViewById( R.id.title );
        TextView artist = (TextView)findViewById( R.id.artist );
        TextView genre = (TextView)findViewById( R.id.genre );

        album.setText ( song.getSongData().getAlbum() );
        title.setText ( song.getSongData().getTitle() );
        artist.setText( song.getSongData().getArtist() );
        genre.setText( song.getSongData().getGenre() );

        if( song.getSongData().getAlbumArt() == null ) {
            artwork.setImageResource( R.drawable.no_album_art_icon );
        }
        else {
            Bitmap songImage = BitmapFactory.decodeByteArray(song.getSongData().getAlbumArt(), 0,
                song.getSongData().getAlbumArt().length);
            artwork.setImageBitmap(songImage);
        }

        ImageButton playPlause = (ImageButton)findViewById(R.id.playPause);

        if( player.isPlaying() ) {
            playPlause.setImageResource( R.drawable.pause );
        }
        else {
            playPlause.setImageResource( R.drawable.play );
        }

        seekBar = (SeekBar)findViewById( R.id.seekBar );
        timeLeft = (TextView)findViewById( R.id.time_left );
        timePassed = (TextView)findViewById( R.id.time_passed );
    }

    private void updateUIComponents() {
        player = service.getPlayer();
        if( player != null ) {

            seekBar.setMax(player.getDuration() / 1000);
            seekBar.setOnSeekBarChangeListener(this);

            final Handler handler = new Handler();
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        updateUI();
                    } catch (IllegalStateException e) {
                        // swallow exception
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private void updateUI() {

        int timePassedInSeconds = player.getCurrentPosition() / 1000;
        int timeLeftInSeconds = player.getDuration() / 1000 - player.getCurrentPosition() / 1000;

        seekBar.setProgress(timePassedInSeconds);

        int minutes = (timePassedInSeconds % 3600) / 60;
        int seconds = timePassedInSeconds % 60;

        String $timePassed = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
        timePassed.setText($timePassed);

        minutes = (timeLeftInSeconds % 3600) / 60;
        seconds = timeLeftInSeconds % 60;

        String $timeLeft = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
        timeLeft.setText($timeLeft);
    }

    //-------------------------------- Listener methods --------------------------------------------

    public void pause(View view) {
        ImageButton b = (ImageButton)view;

        if( b.getTag().equals( "playerIsPlaying" ) ) {
            service.pause();
            b.setTag( "playerIsPaused" );
            b.setImageResource( R.drawable.play );
        }
        else {
            service.play();
            b.setTag( "playerIsPlaying" );
            b.setImageResource( R.drawable.pause );
        }
    }

    public void fastForward(View view) {
        service.fastForward();
        updateUI();
    }

    public void rewind(View view) {
        service.rewind();
        updateUI();
    }

    public void nextSong(View view) {

        if( song.getPosition() + 1 >= SongList.getSize() ) {
            Toast.makeText( this, "No next song available", Toast.LENGTH_SHORT ).show();
        }
        else {
            service.onDestroy();
            this.onStop();
            this.onDestroy();

            Bundle b = new Bundle();
            b.putInt(Constants.SONG_POSITION, song.getPosition() + 1);
            Log.d( "PlayerActivity", "Found next song, recreating activity" );
            this.onCreate(b);
        }
    }

    public void previousSong(View view) {

        if( song.getPosition() - 1 < 0 ) {
            Toast.makeText( this, "No previous song available", Toast.LENGTH_SHORT ).show();
        }
        else {
            service.onDestroy();
            this.onStop();
            this.onDestroy();

            Bundle b = new Bundle();
            b.putInt(Constants.SONG_POSITION, song.getPosition() - 1);
            Log.d( "PlayerActivity", "Found previous song, recreating activity" );
            this.onCreate(b);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if( b ) {
            service.seekTo(i * 1000);
        }
        updateUI();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
