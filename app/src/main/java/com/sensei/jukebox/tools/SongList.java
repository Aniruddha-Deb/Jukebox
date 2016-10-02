package com.sensei.jukebox.tools;

import java.util.ArrayList;

public class SongList {

    private static ArrayList<Song> songs = new ArrayList<>();

    public static Song getSong( int position ) {
        return songs.get( position );
    }

    public static void addSong( Song song ) {
        songs.add( song );
    }

    public static int getSize() {
        return songs.size();
    }

    public static ArrayList<Song> getSongs() {
        return songs;
    }
}
