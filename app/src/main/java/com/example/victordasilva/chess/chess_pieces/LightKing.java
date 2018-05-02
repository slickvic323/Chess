package com.example.victordasilva.chess.chess_pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.victordasilva.chess.R;

/**
 * Created by victordasilva on 5/1/18.
 */

public class LightKing extends ChessPiece{
    //Bitmap to get board from image
    private Bitmap bitmap;

    //Constructor
    public LightKing(Context context, int squareSize, int leftBoard, int topBoard) {
        super(context, squareSize, leftBoard, topBoard);
        pieceName = "King";
        isDarkPiece = false;
        //Getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.light_king);
        bitmap = changeScale(bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public static Bitmap changeScale(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, squareSize, squareSize, false);
        return newBitmap;
    }
}
