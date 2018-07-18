package com.example.victordasilva.chess.chess_pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.victordasilva.chess.R;

public class OneFaultImage {

    private int x, y;
    private Bitmap bitmap;

    public OneFaultImage(Context context, int screenSizeY) {
        x = 5;
        y = screenSizeY - 90;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.one_fault);
        bitmap = changeScale(bitmap);
    }

    private static Bitmap changeScale(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 160, 80, false);
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
