package com.sensei.jukebox.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.sensei.jukebox.R;

public class ViewBuilder {

    static Song song;

    public static RemoteViews buildView( String packageName, Song song ) {

        ViewBuilder.song = song;
        Bitmap baseBitmap = parseBitmap();

        Bitmap backgroundImage = processBitmap( baseBitmap );
        String songTitle = song.getSongData().getTitle();
        String songArtist = song.getSongData().getArtist();

        RemoteViews rv = new RemoteViews( packageName, R.layout.notification_layout );

        rv.setTextViewText(R.id.notif_title, songTitle);
        rv.setTextViewText(R.id.notif_artist, songArtist);
        rv.setProgressBar( R.id.notif_seekBar, 1000, 10, false );
        rv.setImageViewBitmap( R.id.imageView, backgroundImage );

        return rv;
    }

    private static Bitmap parseBitmap() {
        Bitmap bm;
        if( song.getSongData().getAlbumArt() == null ) {
            bm = Bitmap.createBitmap( 150, 150, Bitmap.Config.ARGB_8888 );
            bm.eraseColor( Color.BLACK );
            Log.d( "ViewBuilder", "decoded from system image" );
        }
        else {
            bm = BitmapFactory.decodeByteArray( song.getSongData().getAlbumArt(), 0, song.getSongData().getAlbumArt().length );
            Log.d( "ViewBuilder", "decoded from byte array" );
        }
        return bm;
    }

    private static Bitmap processBitmap( Bitmap bm ) {
        Bitmap backgroundBitmap = Bitmap.createScaledBitmap(bm, 1500, 1500, false);
        backgroundBitmap = Bitmap.createBitmap( backgroundBitmap, 0, 450, 1500, 450 );

        backgroundBitmap = ImageBlur.blur( backgroundBitmap );
        return backgroundBitmap;
    }
}
