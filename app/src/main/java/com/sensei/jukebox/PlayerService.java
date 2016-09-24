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

        if( intent.getStringExtra(Constants.PAUSE) != null ) {
            pause();
        }
        else if ( intent.getStringExtra(Constants.FAST_FORWARD) != null ) {
            fastForward();
        }
        else if ( intent.getStringExtra(Constants.REWIND) != null ) {
            rewind();
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

        Intent pauseIntent = new Intent( this, PlayerService.class );
        pauseIntent.putExtra( Constants.PAUSE, Constants.PAUSE );
        PendingIntent pausePendingIntent = PendingIntent.getActivity( this, 1, pauseIntent, 0 );

        Intent fastForwardIntent = new Intent( this, PlayerService.class );
        pauseIntent.putExtra( Constants.FAST_FORWARD, Constants.FAST_FORWARD );
        PendingIntent fastForwardPendingIntent = PendingIntent.getActivity( this, 1, fastForwardIntent, 0 );

        Intent rewindIntent = new Intent( this, PlayerService.class );
        pauseIntent.putExtra( Constants.REWIND, Constants.REWIND );
        PendingIntent rewindPendingIntent = PendingIntent.getActivity( this, 1, rewindIntent, 0 );

        TaskStackBuilder builder = TaskStackBuilder.create( this );
        builder.addParentStack( PlayerActivity.class );

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        notificationIntent.putExtra( Constants.BUNDLE, Song.bundleSong( song, song.getPosition() ) );
        notificationIntent.putExtra( Constants.IS_RUNNING, true );
        builder.addNextIntent( notificationIntent );
        PendingIntent contentIntent = builder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );

        contentView.setOnClickPendingIntent( R.id.rewind, rewindPendingIntent );
        contentView.setOnClickPendingIntent( R.id.ff, pausePendingIntent );
        contentView.setOnClickPendingIntent( R.id.pp, fastForwardPendingIntent );

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
        player.pause();
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
