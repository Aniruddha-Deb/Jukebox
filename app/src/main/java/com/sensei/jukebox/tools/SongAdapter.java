package com.sensei.jukebox.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensei.jukebox.Constants;
import com.sensei.jukebox.R;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {

    private LayoutInflater songInf = null;

    public SongAdapter( Context ctx ) {
        songInf = LayoutInflater.from( ctx );
    }

    @Override
    public int getCount() {
        return SongList.getSize();
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
        // I don't know how to recycle views!
        LinearLayout songLayout = (LinearLayout)songInf.inflate( R.layout.song_layout, viewGroup, false );

        TextView songView = (TextView)songLayout.findViewById( R.id.list_song_title );
        TextView artistView = (TextView)songLayout.findViewById( R.id.list_song_artist );

        // To be replaced by database
        Song currentSong = SongList.getSong( i );
        songView.setText( currentSong.getSongData().getTitle() );
        artistView.setText( currentSong.getSongData().getArtist() );
        songLayout.setTag( i );

        return songLayout;
    }
}
