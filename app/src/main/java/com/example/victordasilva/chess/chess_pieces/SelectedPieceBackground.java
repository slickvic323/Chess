package com.example.victordasilva.chess.chess_pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.victordasilva.chess.R;

public class SelectedPieceBackground {

    private int x, y;
    private Bitmap bitmap;
    private static int squareSize;

    public SelectedPieceBackground(Context context,int squareSize, int screenSizeX, int screenSizeY) {
        x = screenSizeX*7/8 - (squareSize/2);
        y = 5;
        this.squareSize = squareSize;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.selected_border);
        bitmap = changeScale(bitmap);
    }

    private static Bitmap changeScale(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, squareSize, squareSize, false);
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
