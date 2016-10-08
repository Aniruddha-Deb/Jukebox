package com.sensei.jukebox;

import android.content.Context;

public class Constants {

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
