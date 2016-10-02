package com.sensei.jukebox;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.sensei.jukebox.tools.Song;

import java.util.ArrayList;

public class Constants {

    public static final String BUNDLE = "song_bundle";

    public static final String SONG_URI = "song_uri";

    public static final String SONG_TITLE    = "title";
    public static final String SONG_ID       = "id";
    public static final String SONG_POSITION = "position";
    public static final String IS_RUNNING    = "is it running?";

    public static final String PAUSE = "pause";
    public static final String FAST_FORWARD = "fast forward";
    public static final String REWIND = "rewind";
    public static final String CLOSE = "close";
    public static final String NEXT_SONG = "next_song";
    public static final String PREVIOUS_SONG = "previous_song";

    public static Context APPLICATION_CONTEXT;

    public static void setApplicationContext( Context ctx ) {
        APPLICATION_CONTEXT = ctx;
    }

}
