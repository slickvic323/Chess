package com.example.victordasilva.chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by victordasilva on 6/12/18.
 */

public class Clock {
    //Bitmap to get board from image
    private Bitmap bitmap;

    //Coordinates
    private int x;
    private int y;

    private static int width;
    private static int height;

    private static int screenSizeX;
    private static int screenSizeY;

    //Constructor
    public Clock(Context context, int screenSizeX, int screenSizeY) {
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;

        x = getScreenSizeX()/4;
        y = 0;

        width = getScreenSizeX()/2;
        height = getScreenSizeY()/8;

        //Getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.clock);
        bitmap = changeScale(bitmap);

    }

    private static Bitmap changeScale(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return newBitmap;
    }

    //Getters

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
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
}
