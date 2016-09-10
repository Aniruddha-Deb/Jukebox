package com.sensei.jukebox;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.sensei.jukebox.tools.Song;

public class PlayerService extends Service {

    private MediaPlayer player;
    private final IBinder playerBinder = new PlayerBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Song song = Constants.songs.get( intent.getExtras().getInt( Constants.SONG_POSITION ) );

        player = MediaPlayer.create( getApplicationContext(), song.getUri() );
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.start();
        } catch ( NullPointerException e ) {
            Toast.makeText( getApplicationContext(), "File format not supported", Toast.LENGTH_SHORT ).show();
        }

        return 1;
    }

    @Override
    public void onDestroy() {
        if( player != null ) {
            player.stop();
            player.release();
        }
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
