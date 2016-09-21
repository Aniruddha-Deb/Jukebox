package com.sensei.jukebox;

import com.sensei.jukebox.tools.Song;

import java.util.ArrayList;

public class Constants {

    public static final String BUNDLE = "song_bundle";

    public static final String SONG_TITLE    = "title";
    public static final String SONG_ID       = "id";
    public static final String SONG_POSITION = "position";
    public static final String IS_RUNNING    = "is it running?";

    public static final String PAUSE = "pause";
    public static final String FAST_FORWARD = "fast forward";
    public static final String REWIND = "rewind";

    public static ArrayList<Song> songs = new ArrayList<>();

}
