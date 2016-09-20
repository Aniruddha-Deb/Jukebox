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
import android.view.View;
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

        song = Constants.songs.get( intent.getExtras().getInt( Constants.SONG_POSITION ) );

        player = MediaPlayer.create( getApplicationContext(), song.getUri() );
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.start();
        } catch ( NullPointerException e ) {
            player = new MediaPlayer();  // to avoid future null pointer exceptions
            Toast.makeText( getApplicationContext(), "File format not supported", Toast.LENGTH_SHORT ).show();
        }

        showNotification();

        return 1;
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

        TaskStackBuilder builder = TaskStackBuilder.create( this );
        builder.addParentStack( PlayerActivity.class );

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        notificationIntent.putExtra( Constants.BUNDLE, Song.bundleSong( song, song.getPosition() ) );
        notificationIntent.putExtra( Constants.IS_RUNNING, true );
        builder.addNextIntent( notificationIntent );
        PendingIntent contentIntent = builder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );

        Notification notification = new Notification.Builder( this )
                .setSmallIcon( R.mipmap.ic_launcher )
                .setContent( contentView )
                .setContentIntent(contentIntent)
                .build();

        notification.bigContentView = contentView;

        startForeground( 5252, notification );
    }

    public void pause( View view ) {
        player.pause();
    }

    public void play( View view ) {
        player.start();
    }

    public void fastForward( View view ) {
        player.seekTo( player.getCurrentPosition() + 5000 );
    }

    public void rewind( View view ) {
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
