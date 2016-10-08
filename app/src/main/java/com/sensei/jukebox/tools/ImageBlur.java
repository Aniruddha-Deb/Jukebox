package com.sensei.jukebox.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.sensei.jukebox.Constants;

public class ImageBlur {

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.0f;

    public static Bitmap blur( Bitmap image ) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create( Constants.APPLICATION_CONTEXT );
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        for( int i=0; i<outputBitmap.getHeight(); i++ ) {
            for( int j=0; j<outputBitmap.getWidth(); j++ ) {
                int pixel = outputBitmap.getPixel( j, i );
                int A = Color.alpha( pixel );
                int B = Color.blue( pixel ) - 50;
                if( B < 0 ) {
                    B = 0;
                }
                int G = Color.green( pixel ) - 50;
                if( G < 0 ) {
                    G = 0;
                }
                int R = Color.red( pixel ) - 50;
                if( R < 0 ) {
                    R = 0;
                }
                outputBitmap.setPixel( j, i, Color.argb( A, R, G, B ) );
            }
        }

        return outputBitmap;
    }
}