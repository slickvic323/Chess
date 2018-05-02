package com.example.victordasilva.chess;

import com.example.victordasilva.chess.chess_pieces.ChessPiece;

import java.util.ArrayList;

/**
 * Created by victordasilva on 5/2/18.
 */

public class GameInfo {
    private static long timeForTurns;
    private boolean inProgress;
    private ChessPiece[][] boardLayout;
    private String userColor;
    private String whoseTurn;
    private long timeRemaining;
    private long startTimeOfNewTurn;
    private int[] chosenTile; //0=y 1=x


    public GameInfo(ChessPiece[][] boardLayout, String userColor, long timeForTurns) {
        inProgress = true;
        this.boardLayout = boardLayout;
        this.userColor = userColor;
        this.timeForTurns = timeForTurns;
        whoseTurn = "Dark";
    }

    // Returns an array list of all of the possible tiles that the user can move to
    public ArrayList<ArrayList<Integer>> getPossibleMoves(int[] chosenTile){
        this.chosenTile = chosenTile;
        ArrayList<ArrayList<Integer>> possibleMoves = new ArrayList<ArrayList<Integer>>();
        if(boardLayout[chosenTile[0]][chosenTile[1]] == null) {
            return possibleMoves;
        }
        ChessPiece userPiece = boardLayout[chosenTile[0]][chosenTile[1]];
        String pieceName = userPiece.getPieceName();
        boolean isDarkPiece = userPiece.isDarkPiece;
        if((userColor.equals("Dark") && isDarkPiece) || (userColor.equals("Light") && !isDarkPiece)) {
            switch (pieceName) {
                case "Bishop":
                    possibleMoves = getBishopMoves(isDarkPiece);
                    break;
                case "King":
                    possibleMoves = getKingMoves(isDarkPiece);
                    break;
                case "Knight":
                    possibleMoves = getKnightMoves(isDarkPiece);
                    break;
                case "Pawn":
                    possibleMoves = getPawnMoves(isDarkPiece);
                    break;
                case "Queen":
                    possibleMoves = getQueenMoves(isDarkPiece);
                    break;
                case "Rook":
                    possibleMoves = getRookMoves(isDarkPiece);
                    break;
                default:
                    break;
            }
        }
        return possibleMoves;
    }

    private ArrayList<ArrayList<Integer>> getBishopMoves(boolean userIsDark) {
        return null;
    }

    private ArrayList<ArrayList<Integer>> getKingMoves(boolean userIsDark) {
        int y = chosenTile[0];
        int x = chosenTile[1];

        return null;
    }

    private ArrayList<ArrayList<Integer>> getKnightMoves(boolean userIsDark) {
        return null;
    }

    private ArrayList<ArrayList<Integer>> getPawnMoves(boolean userIsDark) {
        int y = chosenTile[0];
        int x = chosenTile[1];

        ArrayList<ArrayList<Integer>> possibleMoves = new ArrayList<ArrayList<Integer>>();
        if((chosenTile[0] == 6) && !userIsDark) {
            // Tile 1 above
            if(boardLayout[y-1][x] == null) {
                ArrayList<Integer> newMove = new ArrayList<Integer>();
                newMove.add(y-1);
                newMove.add(x);
                possibleMoves.add(newMove);
            }
            // Tile 2 above
            if(boardLayout[y-2][x] == null) {
                ArrayList<Integer> newMove = new ArrayList<Integer>();
                newMove.add(y-2);
                newMove.add(x);
                possibleMoves.add(newMove);
            }

        }
        return possibleMoves;
    }

    private ArrayList<ArrayList<Integer>> getQueenMoves(boolean userIsDark) {
        return null;
    }

    private ArrayList<ArrayList<Integer>> getRookMoves(boolean userIsDark) {
        return null;
    }

    //Getters
    public static long getTimeForTurns() {
        return timeForTurns;
    }

    public boolean getInProgress() {
        return inProgress;
    }

    public ChessPiece[][] getBoardLayout() {
        return boardLayout;
    }

    public String getUserColor() {
        return userColor;
    }

    public String getWhoseTurn() {
        return whoseTurn;
    }

    public long getTimeRemaining() {
        //TODO
        //timeRemaining =
        return timeRemaining;
    }

    public long getStartTimeOfNewTurn() {
        return startTimeOfNewTurn;
    }

    public int[] getChosenTile() {
        return chosenTile;
    }

    //Setters
    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public void setBoardLayout(ChessPiece[][] boardLayout) {
        this.boardLayout = boardLayout;
    }

    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }

    public void setWhoseTurn(String whoseTurn) {
        this.whoseTurn = whoseTurn;
    }

    public void setStartTimeOfNewTurn(long startTimeOfNewTurn) {
        this.startTimeOfNewTurn = startTimeOfNewTurn;
    }

    public void setChosenTile(int[] chosenTile) {
        this.chosenTile = chosenTile;
    }
}
