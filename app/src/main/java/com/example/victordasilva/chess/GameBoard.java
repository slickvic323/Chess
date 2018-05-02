package com.example.victordasilva.chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static android.R.attr.screenSize;

/**
 * Created by victordasilva on 5/1/18.
 */

public class GameBoard {
    //Bitmap to get board from image
    private Bitmap bitmap;

    //Coordinates
    private int x;
    private int y;

    private static int screenSizeX;
    private static int screenSizeY;
    private static int boardSize;

    //Constructor
    public GameBoard(Context context, int screenSizeX, int screenSizeY) {
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;

        x = 0;
        y = (screenSizeY/2)-(screenSizeX/2);

        //Getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.chess_board);
        bitmap = changeScale(bitmap);
    }

    private static Bitmap changeScale(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, getScreenSizeX(), getScreenSizeX(), false);
        boardSize = getScreenSizeX();
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

    public static int getScreenSizeX() {
        return screenSizeX;
    }

    public static int getScreenSizeY() {
        return screenSizeY;
    }

    public static int getBoardSize() {
        return boardSize;
    }
}
