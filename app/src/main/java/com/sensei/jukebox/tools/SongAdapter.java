package com.sensei.jukebox.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensei.jukebox.R;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs = null;
    private LayoutInflater songInf = null;

    public SongAdapter(Context c, ArrayList<Song> songs) {
        this.songs = songs;
        songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LinearLayout songLayout = (LinearLayout)songInf.inflate( R.layout.song_layout, viewGroup, false );

        TextView songView = (TextView)songLayout.findViewById( R.id.list_song_title );
        TextView artistView = (TextView)songLayout.findViewById( R.id.list_song_artist );

        Song currentSong = songs.get( i );
        songView.setText( currentSong.toString() );
        artistView.setText( currentSong.getArtist() );
        songLayout.setTag( i );

        return songLayout;
    }
}
