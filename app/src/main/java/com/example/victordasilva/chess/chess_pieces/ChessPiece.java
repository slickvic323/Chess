package com.example.victordasilva.chess.chess_pieces;

import android.content.Context;

/**
 * Created by victordasilva on 5/1/18.
 */

public abstract class ChessPiece {
    //Coordinates
    private int x;
    private int y;

    private int xSquare;
    private int ySquare;

    private int leftBoard, topBoard;

    public static int squareSize;

    private boolean inPlay;
    public String pieceName;
    public boolean isDarkPiece;
    private boolean madeFirstMove;

    //Constructor
    public ChessPiece(Context context, int squareSize, int leftBoard, int topBoard) {
        this.squareSize = squareSize;
        this.leftBoard = leftBoard;
        this.topBoard = topBoard;
        inPlay = true;
        madeFirstMove = false;
        x = (getxSquare() * getSquareSize()) + leftBoard;
        y = (getySquare() * getSquareSize()) + topBoard;
    }

    //Getters
    public boolean getInPlay() {
        return inPlay;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getxSquare() {
        return xSquare;
    }

    public int getySquare() {
        return ySquare;
    }

    public String getPieceName() {
        return pieceName;
    }

    public static int getSquareSize() {
        return squareSize;
    }

    public int getLeftBoard() {
        return leftBoard;
    }

    public int getTopBoard() {
        return topBoard;
    }

    public void updatePosition(int xSquare, int ySquare) {
        this.xSquare = xSquare;
        this.ySquare = ySquare;
        x = (getxSquare() * getSquareSize()) + leftBoard;
        y = (getySquare() * getSquareSize()) + topBoard;
    }

    public boolean getMadeFirstMove() {
        return madeFirstMove;
    }

    public void setMadeFirstMove(boolean madeFirstMove) {
        this.madeFirstMove = madeFirstMove;
    }
}
