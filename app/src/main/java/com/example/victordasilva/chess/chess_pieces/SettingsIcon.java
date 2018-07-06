package com.example.victordasilva.chess.chess_pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.victordasilva.chess.R;

public class SettingsIcon {
    private Bitmap bitmap;

    private int x;
    private int y;

    public SettingsIcon(Context context) {
        // Settings Icon will be placed in the upper left hand corner of the screen
        x = 20;
        y = 40;

        //Getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.settings_icon2);
        bitmap = changeScale(bitmap);

    }

    private static Bitmap changeScale(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
        return newBitmap;
    }

    //Getters

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
