package com.example.victordasilva.chess.chess_pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.victordasilva.chess.R;

import static android.R.attr.y;

/**
 * Created by victordasilva on 5/1/18.
 */

public class DarkBishop implements ChessPiece{
    private int x, y;
    private Bitmap bitmap;
    private String pieceName;
    private int xSquare, ySquare;
    private boolean darkPiece, inPlay, madeFirstMove;
    private static int squareSize;
    private int leftBoard, topBoard;

    //Constructor
    public DarkBishop(Context context, int squareSize, int leftBoard, int topBoard) {
        this.squareSize = squareSize;
        this.leftBoard = leftBoard;
        this.topBoard = topBoard;
        pieceName = "Bishop";
        darkPiece = true;
        inPlay = true;
        madeFirstMove = false;
        x = (getxSquare() * squareSize) + leftBoard;
        y = (getySquare() * squareSize) + topBoard;

        //Getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dark_bishop);
        bitmap = changeScale(bitmap);
    }

    public static Bitmap changeScale(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, squareSize, squareSize, false);
        return newBitmap;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public int getxSquare() {
        return xSquare;
    }

    @Override
    public int getySquare() {
        return ySquare;
    }

    @Override
    public String getPieceName() {
        return pieceName;
    }

    @Override
    public void updatePosition(int xSquare, int ySquare) {
        this.xSquare = xSquare;
        this.ySquare = ySquare;
        x = (getxSquare() * squareSize) + leftBoard;
        y = (getySquare() * squareSize) + topBoard;
    }

    @Override
    public boolean hasMadeFirstMove() {
        return madeFirstMove;
    }

    @Override
    public boolean isDarkPiece() {
        return darkPiece;
    }

    @Override
    public boolean isInPlay() {
        return inPlay;
    }

    @Override
    public void setInPlay(boolean inPlay) {
        this.inPlay = inPlay;
    }

    @Override
    public void setMadeFirstMove(boolean madeFirstMove) {
        this.madeFirstMove = madeFirstMove;
    }
}
