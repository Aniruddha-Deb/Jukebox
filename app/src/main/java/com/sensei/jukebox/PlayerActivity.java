package com.sensei.jukebox;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
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

import java.util.Locale;

public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private Song song;
    private MediaPlayer player;

    private SeekBar seekBar;
    private TextView timeLeft;
    private TextView timePassed;

    private boolean wasRunning = false;

    private PlayerService service;
    private ServiceConnection connection = null ;

    public PlayerActivity() {
        Log.d( "PlayerActivity", "New instance of Activity created" );
    }

    private void connectToService( int songPosition ) {
        Intent intent = new Intent( this, PlayerService.class );
        intent.putExtra( Constants.SONG_POSITION, songPosition );

        if( wasRunning ) {
            Log.d( "PlayerActivity", "service already running, binding to it" );
            bindService( intent, connection, Context.BIND_AUTO_CREATE );
            Log.d( "PlayerActivity", "connectToService bound to service" );
        }
        else {
            startService(intent);
            Log.d("PlayerActivity", "connectToService started service");
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
            Log.d("PlayerActivity", "connectToService bound to service");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d( "PlayerActivity", "onCreate called" );
        super.onCreate(savedInstanceState);

        this.connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) iBinder;
                service = binder.getService();
                Log.d( "PlayerActivity", "srvConn got service" );
                player = service.getPlayer();

                setUpUI();
                Log.d( "PlayerActivity", "srvConn set up UI" );
                updateUIComponents();
                Log.d( "PlayerActivity", "srvConn updated UI components" );
                //displayNotification();
                Log.d( "PlayerActivity", "srvConn created notification" );
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };

        if( savedInstanceState == null ) {
            Log.d( "PlayerActivity", "onCreate found no song passed on, playing selected song" );
            retrieveIntent();
        }
        else {
            Log.d( "PlayerActivity", "onCreate retrieved song" );
            song = Constants.songs.get( savedInstanceState.getInt( Constants.SONG_POSITION ) );
            wasRunning = false;
        }
        Log.d( "PlayerActivity", "onCreate connecting to service" );
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

    public void setUpUI() {

        setContentView(R.layout.activity_player);

        ImageView artwork = (ImageView)findViewById( R.id.albumArt );
        TextView album = (TextView)findViewById( R.id.album );
        TextView title = (TextView)findViewById( R.id.title );
        TextView artist = (TextView)findViewById( R.id.artist );
        TextView genre = (TextView)findViewById( R.id.genre );

        album.setText ( song.getAlbum() );
        title.setText ( song.toString() );
        artist.setText( song.getArtist() );
        genre.setText( song.getGenre() );

        Bitmap songImage;
        if( song.getAlbumArt() == null ) {
            songImage = BitmapFactory.decodeResource( this.getResources(), R.drawable.no_album_art_icon );
        }
        else {
            songImage = BitmapFactory.decodeByteArray(song.getAlbumArt(), 0, song.getAlbumArt().length);
        }
        artwork.setImageBitmap(songImage);

        seekBar = (SeekBar)findViewById( R.id.seekBar );
        timeLeft = (TextView)findViewById( R.id.time_left );
        timePassed = (TextView)findViewById( R.id.time_passed );
    }

    private void retrieveIntent() {
        Intent intent = getIntent();
        song = Song.unwrapSong( intent.getBundleExtra(Constants.BUNDLE), this );
        wasRunning = intent.getBooleanExtra( Constants.IS_RUNNING, false );
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

    public void pause(View view) {
        ImageButton b = (ImageButton)view;

        if( b.getTag().equals( "false" ) ) {
            service.pause( null );
            b.setTag( "true" );
            b.setImageResource( R.drawable.play );
            b.setScaleX( 0.75f );
            b.setScaleY( 0.75f );
        }
        else {
            service.play( null );
            b.setTag( "false" );
            b.setImageResource( R.drawable.pause );
            b.setScaleX( 0.75f );
            b.setScaleY( 0.75f );
        }
    }

    public void fastForward(View view) {
        service.fastForward( null );
        updateUI();
    }

    public void rewind(View view) {
        service.rewind( null );
        updateUI();
    }

    public void nextSong(View view) {

        if( song.getPosition() + 1 >= Constants.songs.size() ) {
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
