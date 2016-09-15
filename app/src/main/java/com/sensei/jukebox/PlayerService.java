package com.sensei.jukebox;

import android.app.Notification;
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
        Bitmap bm;
        if( song.getAlbumArt() == null ) {
            bm = BitmapFactory.decodeResource( getResources(), R.drawable.no_album_art_icon );
        }
        else {
            bm = BitmapFactory.decodeByteArray( song.getAlbumArt(), 0, song.getAlbumArt().length );
        }

        TaskStackBuilder builder = TaskStackBuilder.create( this );
        builder.addParentStack( PlayerActivity.class );
        RemoteViews notifView = new RemoteViews( getPackageName(), R.layout.notification_layout );

        notifView.setTextViewText( R.id.notif_title, song.toString() );
        notifView.setTextViewText( R.id.notif_artist, song.getArtist() );

        Intent notifIntent = new Intent( this, PlayerActivity.class );
        notifIntent.putExtra( Constants.BUNDLE, Song.bundleSong( song, song.getPosition() ) );
        notifIntent.putExtra( Constants.IS_RUNNING, true );

        builder.addNextIntent( notifIntent );
        PendingIntent pi = builder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );

        Notification notif = new Notification.Builder(getApplicationContext())
                .setSmallIcon( R.drawable.play )
                //.setLargeIcon( Bitmap.createScaledBitmap( bm, 64, 64, false ) )
                .setAutoCancel( true )
                .setPriority( Notification.PRIORITY_MAX )
                .setContent( notifView )
                .setContentIntent( pi )
                .build();

        startForeground( 5252, notif );

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
