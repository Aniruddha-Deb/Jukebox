package com.sensei.jukebox;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.sensei.jukebox.tools.Song;

import java.io.IOException;

public class PlayerService extends Service {

    private MediaPlayer player = new MediaPlayer();
    private Song song;

    private final IBinder playerBinder = new PlayerBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        song = Constants.songs.get( intent.getExtras().getInt( Constants.SONG_POSITION ) );
        try {
            player.setDataSource( getApplicationContext(), song.getUri() );
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setAudioStreamType( AudioManager.STREAM_MUSIC );
        player.start();

        return 1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        onStartCommand( intent, 0, 0 );
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

    public class PlayerBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }
}
