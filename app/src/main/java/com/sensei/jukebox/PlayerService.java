package com.sensei.jukebox;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sensei.jukebox.tools.Song;

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
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        contentView.setTextViewText(R.id.notif_title, song.toString() );
        contentView.setTextViewText(R.id.notif_artist, song.getArtist() );

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder( this )
                .setSmallIcon( R.mipmap.ic_launcher )
                .setContentIntent(contentIntent)
                .setContent( contentView )
                .setDefaults( Notification.FLAG_AUTO_CANCEL )
                .setDefaults( Notification.DEFAULT_VIBRATE )
                .setDefaults( Notification.DEFAULT_SOUND )
                .build();

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
