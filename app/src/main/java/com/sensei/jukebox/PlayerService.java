package com.sensei.jukebox;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sensei.jukebox.tools.Song;
import com.sensei.jukebox.tools.ViewBuilder;

public class PlayerService extends Service {

    private MediaPlayer player;
    private final IBinder playerBinder = new PlayerBinder();

    private Song song;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d( "PlayerService", "Got an intent." ) ;

        String command = intent.getAction();
        if( command == null ) {
            command = "";
        }

        if( command.equals( Constants.PAUSE ) ) {
            pause();
        }
        else if ( command.equals( Constants.FAST_FORWARD ) ) {
            fastForward();
        }
        else if ( command.equals( Constants.REWIND ) ) {
            rewind();
        }
        else if( command.equals( Constants.CLOSE ) ) {
            this.onDestroy();
        }
        else if( command.equals( Constants.NEXT_SONG ) ) {
            nextSong();
        }
        else if( command.equals( Constants.PREVIOUS_SONG ) ) {
            previousSong();
        }
        else {
            song = Constants.songs.get(intent.getExtras().getInt(Constants.SONG_POSITION));
            player = MediaPlayer.create(getApplicationContext(), song.getUri());

            try {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.start();
            }
            catch (NullPointerException e) {
                player = new MediaPlayer();  // to avoid future null pointer exceptions
                Toast.makeText(getApplicationContext(), "File format not supported", Toast.LENGTH_SHORT).show();
            }
            showNotification();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground( true );
        player.stop();
        player.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return playerBinder;
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    private void showNotification() {

        RemoteViews contentView = ViewBuilder.buildView( getPackageName(), getResources(), song, this );

        Intent pauseIntent = new Intent( this, PlayerService.class ).setAction( Constants.PAUSE );
        PendingIntent pausePendingIntent = PendingIntent.getService( this, 0, pauseIntent, PendingIntent.FLAG_CANCEL_CURRENT );

        Intent fastForwardIntent = new Intent( this, PlayerService.class ).setAction( Constants.FAST_FORWARD );
        PendingIntent fastForwardPendingIntent = PendingIntent.getService( this, 0, fastForwardIntent, PendingIntent.FLAG_CANCEL_CURRENT );

        Intent rewindIntent = new Intent( this, PlayerService.class ).setAction( Constants.REWIND );
        PendingIntent rewindPendingIntent = PendingIntent.getService( this, 0, rewindIntent, PendingIntent.FLAG_CANCEL_CURRENT );

        Intent closeIntent = new Intent( this, PlayerService.class ).setAction( Constants.CLOSE );
        PendingIntent closePendingIntent = PendingIntent.getService( this, 0, closeIntent, PendingIntent.FLAG_CANCEL_CURRENT );

        Intent nextSongIntent = new Intent( this, PlayerService.class ).setAction( Constants.NEXT_SONG );
        PendingIntent nextSongPendingIntent = PendingIntent.getService( this, 0, nextSongIntent, PendingIntent.FLAG_CANCEL_CURRENT );

        Intent previousSongIntent = new Intent( this, PlayerService.class ).setAction( Constants.PREVIOUS_SONG );
        PendingIntent previousSongPendingIntent = PendingIntent.getService( this, 0, previousSongIntent, PendingIntent.FLAG_CANCEL_CURRENT );

        TaskStackBuilder builder = TaskStackBuilder.create( this );
        builder.addParentStack( PlayerActivity.class );

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        notificationIntent.putExtra( Constants.BUNDLE, Song.bundleSong( song, song.getPosition() ) );
        notificationIntent.putExtra( Constants.IS_RUNNING, true );
        builder.addNextIntent( notificationIntent );
        PendingIntent contentIntent = builder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );

        contentView.setOnClickPendingIntent( R.id.rewind, rewindPendingIntent );
        contentView.setOnClickPendingIntent( R.id.ff, fastForwardPendingIntent );
        contentView.setOnClickPendingIntent( R.id.pp, pausePendingIntent );
        contentView.setOnClickPendingIntent( R.id.close, closePendingIntent );
        contentView.setOnClickPendingIntent( R.id.next, nextSongPendingIntent );
        contentView.setOnClickPendingIntent( R.id.previous, previousSongPendingIntent );

        //noinspection deprecation
        Notification notification = new Notification.Builder( this )
                .setSmallIcon( R.mipmap.ic_launcher )
                .setContent( contentView )
                .setContentIntent(contentIntent)
                .build();

        //noinspection deprecation
        notification.bigContentView = contentView;

        startForeground( 5252, notification );
    }

    public void pause() {
        if( player.isPlaying() ) {
            player.pause();
        }
        else {
            player.start();
        }
    }

    public void play() {
        player.start();
    }

    public void fastForward() {
        player.seekTo( player.getCurrentPosition() + 5000 );
    }

    public void rewind() {
        player.seekTo( player.getCurrentPosition() - 5000 );
    }

    public void nextSong() {
        if( song.getPosition() + 1 >= Constants.songs.size() ) {
            Toast.makeText( this, "No next song available", Toast.LENGTH_SHORT ).show();
        }
        else {
            Intent intent = new Intent( this, PlayerService.class );
            intent.putExtra( Constants.SONG_POSITION, song.getPosition() + 1 );

            this.onDestroy();
            this.onCreate();
            startService( intent );
        }

    }

    public void previousSong() {
        if( song.getPosition() - 1 < 0 ) {
            Toast.makeText( this, "No previous song available", Toast.LENGTH_SHORT ).show();
        }
        else {
            Intent intent = new Intent( this, PlayerService.class );
            intent.putExtra( Constants.SONG_POSITION, song.getPosition() - 1 );

            this.onDestroy();
            this.onCreate();
            startService( intent );
        }

    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void seekTo( int position ) {
        player.seekTo( position );
    }

    public class PlayerBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }
}
