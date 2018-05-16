package com.example.victordasilva.chess.chess_pieces;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by victordasilva on 5/1/18.
 */

public interface ChessPiece {

    int getX();
    int getY();
    Bitmap getBitmap();
    int getxSquare();
    int getySquare();
    String getPieceName();
    void updatePosition(int xSquare, int ySquare);
    boolean hasMadeFirstMove();
    boolean isDarkPiece();
    boolean isInPlay();

    void setInPlay(boolean inPlay);
    void setMadeFirstMove(boolean madeFirstMove);
}
