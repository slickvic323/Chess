package com.example.victordasilva.chess.chess_pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.victordasilva.chess.R;

import static com.example.victordasilva.chess.chess_pieces.ChessPiece.squareSize;

/**
 * Created by victordasilva on 5/1/18.
 */

public class LightPawn extends ChessPiece{
    //Bitmap to get board from image
    private Bitmap bitmap;

    //Constructor
    public LightPawn(Context context, int squareSize, int leftBoard, int topBoard) {
        super(context, squareSize, leftBoard, topBoard);
        pieceName = "Pawn";
        isDarkPiece = false;
        //Getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.light_pawn);
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
