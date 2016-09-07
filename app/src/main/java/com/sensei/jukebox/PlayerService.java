package com.sensei.jukebox;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.sensei.jukebox.tools.Song;

import java.io.IOException;

public class PlayerService extends Service {

    private MediaPlayer player;
    private Song song;

    public PlayerService() {
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        song = Constants.songs.get( intent.getExtras().getInt( Constants.SONG_POSITION ) );
        player = MediaPlayer.create( getApplicationContext(), song.getUri() );

        player.setAudioStreamType( AudioManager.STREAM_MUSIC );
        player.start();

        return 1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
