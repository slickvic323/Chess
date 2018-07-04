package com.example.victordasilva.chess.chess_pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.example.victordasilva.chess.R;

public class TitleBackground {
    private int x, y;
    private Bitmap bitmap;

    private static int screenSizeX, screenSizeY;


    //Constructor
    public TitleBackground(Context context, int screenSizeX, int screenSizeY) {
        x = 0;
        y = 0;
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;
        //Getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.wooden_background);
        bitmap = changeScale(bitmap);
    }

    public static Bitmap changeScale(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, screenSizeX, screenSizeY, false);
        return newBitmap;
    }

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
