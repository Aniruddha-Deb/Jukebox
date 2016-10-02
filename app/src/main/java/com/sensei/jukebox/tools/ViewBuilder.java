package com.sensei.jukebox.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.sensei.jukebox.R;

public class ViewBuilder {

    static Song song;
    static Context ctx;
    public static boolean textColorIsBlack;

    public static RemoteViews buildView(String packageName, Resources res, Song song, Context ctx ) {

        ViewBuilder.song = song;
        ViewBuilder.ctx = ctx;
        Bitmap baseBitmap = parseBitmap( res );

        Bitmap backgroundImage = processBitmap( baseBitmap );
        textColorIsBlack = false;
        String songTitle = song.getSongData().getTitle();
        String songArtist = song.getSongData().getArtist();

        RemoteViews rv = new RemoteViews( packageName, R.layout.notification_layout );

        rv.setTextViewText(R.id.notif_title, songTitle);
        rv.setTextViewText(R.id.notif_artist, songArtist);
        rv.setProgressBar( R.id.notif_seekBar, 1000, 10, false );

        if( textColorIsBlack ) {
            rv.setInt( R.id.notif_title, "setTextColor", Color.BLACK );
            rv.setInt( R.id.notif_artist, "setTextColor", Color.BLACK );

            rv.setInt( R.id.previous, "setImageResource", R.drawable.previous );
            rv.setInt( R.id.rewind, "setImageResource", R.drawable.rewind );
            rv.setInt( R.id.pp, "setImageResource", R.drawable.pause );
            rv.setInt( R.id.ff, "setImageResource", R.drawable.fast_forward );
            rv.setInt( R.id.next, "setImageResource", R.drawable.next );
        }

        rv.setImageViewBitmap( R.id.imageView, backgroundImage );

        return rv;
    }

    private static Bitmap parseBitmap( Resources res ) {
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

        backgroundBitmap = ImageBlur.blur( ctx, backgroundBitmap );
        return backgroundBitmap;
    }

    private static boolean checkTextColor( Bitmap bm ) {
        Bitmap colourTestBitmap = Bitmap.createScaledBitmap( bm, 1, 1, false );
        int pixel = colourTestBitmap.getPixel( 0, 0 );
        int redValue = Color.red( pixel );
        int blueValue = Color.blue( pixel );
        int greenValue = Color.green( pixel );

        return redValue > 128 && greenValue > 128 && blueValue > 128;
    }
}
