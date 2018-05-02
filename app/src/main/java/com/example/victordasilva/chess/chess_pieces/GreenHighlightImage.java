package com.example.victordasilva.chess.chess_pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.victordasilva.chess.R;

import static com.example.victordasilva.chess.GameBoard.getScreenSizeX;

/**
 * Created by victordasilva on 5/2/18.
 */

public class GreenHighlightImage {
    private Bitmap bitmap;

    //Coordinates
    private int x;
    private int y;

    private static int squareSize;
    private int[] whichSquare;
    private static int leftBoard;
    private static int topBoard;

    //Constructor
    public GreenHighlightImage(Context context, int leftBoard, int topBoard, int squareSize, int[] whichSquare) {
        this.leftBoard = leftBoard;
        this.topBoard = topBoard;
        this.squareSize = squareSize;
        this.whichSquare = whichSquare;

        x = leftBoard + (squareSize * whichSquare[1]);
        y = topBoard + (squareSize * whichSquare[0]);

        //Getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_highlight);
        bitmap = changeScale(bitmap);
    }

    private static Bitmap changeScale(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, squareSize, squareSize, false);
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
