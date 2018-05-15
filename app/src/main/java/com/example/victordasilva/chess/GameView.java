package com.example.victordasilva.chess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.victordasilva.chess.chess_pieces.ChessPiece;
import com.example.victordasilva.chess.chess_pieces.DarkBishop;
import com.example.victordasilva.chess.chess_pieces.DarkKing;
import com.example.victordasilva.chess.chess_pieces.DarkKnight;
import com.example.victordasilva.chess.chess_pieces.DarkPawn;
import com.example.victordasilva.chess.chess_pieces.DarkQueen;
import com.example.victordasilva.chess.chess_pieces.DarkRook;
import com.example.victordasilva.chess.chess_pieces.GreenHighlightImage;
import com.example.victordasilva.chess.chess_pieces.LightBishop;
import com.example.victordasilva.chess.chess_pieces.LightKing;
import com.example.victordasilva.chess.chess_pieces.LightKnight;
import com.example.victordasilva.chess.chess_pieces.LightPawn;
import com.example.victordasilva.chess.chess_pieces.LightQueen;
import com.example.victordasilva.chess.chess_pieces.LightRook;
import com.example.victordasilva.chess.chess_pieces.PurpleHighlightImage;

import java.util.ArrayList;

/**
 * Created by victordasilva on 5/1/18.
 */

public class GameView extends SurfaceView implements Runnable {
    //boolean variable to track if the game is playing or not
    volatile boolean playing;

    private Context context;

    //the game thread
    private Thread gameThread = null;

    //Adding the GameBoard
    private GameBoard gameBoard;
    //Adding all pieces
    private DarkBishop darkBishop1, darkBishop2;
    private DarkKing darkKing;
    private DarkKnight darkKnight1, darkKnight2;
    private DarkPawn darkPawn1, darkPawn2, darkPawn3, darkPawn4, darkPawn5, darkPawn6, darkPawn7, darkPawn8;
    private DarkQueen darkQueen;
    private DarkRook darkRook1, darkRook2;

    private LightBishop lightBishop1, lightBishop2;
    private LightKing lightKing;
    private LightKnight lightKnight1, lightKnight2;
    private LightPawn lightPawn1, lightPawn2, lightPawn3, lightPawn4, lightPawn5, lightPawn6, lightPawn7, lightPawn8;
    private LightQueen lightQueen;
    private LightRook lightRook1, lightRook2;

    // y and x positions (pixels)
    private int[][][] squaresLayout;

    // y and x positions (relative to board)
    private ChessPiece[][] boardLayout;


    private static int screenSizeX;
    private static int screenSizeY;
    private static int boardSize;
    private static int squareSize;
    private static int leftBoard;
    private static int topBoard;

    private float touchedX;
    private float touchedY;
    private int[] touchedSquare; //height, width
    private ArrayList<ArrayList<Integer>> possibleMoves;
    private boolean movementTouchDetected;

    private GameInfo gameInfo;

    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    //Class constructor
    public GameView(Context context, int screenSizeX, int screenSizeY) {
        super(context);
        this.context = context;
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;
        this.squareSize = screenSizeX / 8;
        movementTouchDetected = false;
        //Initializing gameBoard object
        gameBoard = new GameBoard(context, screenSizeX, screenSizeY);
        leftBoard = gameBoard.getX();
        topBoard = gameBoard.getY();
        boardSize = gameBoard.getBoardSize();

        createLayout();

        boardLayout = new ChessPiece[8][8];
        setupPieces(context);

        gameInfo = new GameInfo(boardLayout, "Light", 120000);

        //Initializing Drawing Objects
        surfaceHolder = getHolder();
        paint = new Paint();
    }

    @Override
    public void run() {
        while(playing) {
            update();
            draw();
            control();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        Log.i("action", String.valueOf(action));
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                movementTouchDetected = false;
                break;
            case MotionEvent.ACTION_UP:
                if(!movementTouchDetected){
                    touchedX = motionEvent.getX();
                    touchedY = motionEvent.getY();
                    updateTouchHandler();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                movementTouchDetected = true;
                break;
            default:
                break;
        }
        return true;
    }


    private void update() {

    }

    private void draw() {
        //Checking if surface is valid
        if (surfaceHolder.getSurface().isValid()) {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //Drawing a background color for the canvas
            canvas.drawColor(Color.BLACK);
            //Drawing the game board
            canvas.drawBitmap(
                    gameBoard.getBitmap(),
                    gameBoard.getX(),
                    gameBoard.getY(),
                    paint
            );

            if(touchedSquare != null){
                GreenHighlightImage ghi = new GreenHighlightImage(context, leftBoard, topBoard, squareSize, touchedSquare);
                canvas.drawBitmap(
                        ghi.getBitmap(),
                        ghi.getX(),
                        ghi.getY(),
                        paint);
            }

            if(possibleMoves != null) {
                for(ArrayList<Integer> square : possibleMoves) {
                    PurpleHighlightImage phi = new PurpleHighlightImage(context, leftBoard, topBoard, squareSize, square);
                    canvas.drawBitmap(
                            phi.getBitmap(),
                            phi.getX(),
                            phi.getY(),
                            paint
                    );
                }
            }

            // Drawing all the pieces

            //Dark Bishop1
            canvas.drawBitmap(
                    darkBishop1.getBitmap(),
                    darkBishop1.getX(),
                    darkBishop1.getY(),
                    paint
            );
            //Dark Bishop2
            canvas.drawBitmap(
                    darkBishop2.getBitmap(),
                    darkBishop2.getX(),
                    darkBishop2.getY(),
                    paint
            );
            //Dark King
            canvas.drawBitmap(
                    darkKing.getBitmap(),
                    darkKing.getX(),
                    darkKing.getY(),
                    paint
            );
            //Dark Knight 1
            canvas.drawBitmap(
                    darkKnight1.getBitmap(),
                    darkKnight1.getX(),
                    darkKnight1.getY(),
                    paint
            );
            //Dark Knight 2
            canvas.drawBitmap(
                    darkKnight2.getBitmap(),
                    darkKnight2.getX(),
                    darkKnight2.getY(),
                    paint
            );

            drawDarkPawns();

            //Dark Queen
            canvas.drawBitmap(
                    darkQueen.getBitmap(),
                    darkQueen.getX(),
                    darkQueen.getY(),
                    paint
            );
            //Dark Rook1
            canvas.drawBitmap(
                    darkRook1.getBitmap(),
                    darkRook1.getX(),
                    darkRook1.getY(),
                    paint
            );
            //Dark Rook2
            canvas.drawBitmap(
                    darkRook2.getBitmap(),
                    darkRook2.getX(),
                    darkRook2.getY(),
                    paint
            );
            //Light Bishop1
            canvas.drawBitmap(
                    lightBishop1.getBitmap(),
                    lightBishop1.getX(),
                    lightBishop1.getY(),
                    paint
            );
            //Light Bishop2
            canvas.drawBitmap(
                    lightBishop2.getBitmap(),
                    lightBishop2.getX(),
                    lightBishop2.getY(),
                    paint
            );
            //Light King
            canvas.drawBitmap(
                    lightKing.getBitmap(),
                    lightKing.getX(),
                    lightKing.getY(),
                    paint
            );
            //Light Knight1
            canvas.drawBitmap(
                    lightKnight1.getBitmap(),
                    lightKnight1.getX(),
                    lightKnight1.getY(),
                    paint
            );
            //Light Knight2
            canvas.drawBitmap(
                    lightKnight2.getBitmap(),
                    lightKnight2.getX(),
                    lightKnight2.getY(),
                    paint
            );

            drawLightPawns();

            //Light Queen
            canvas.drawBitmap(
                    lightQueen.getBitmap(),
                    lightQueen.getX(),
                    lightQueen.getY(),
                    paint
            );
            //Light Rook1
            canvas.drawBitmap(
                    lightRook1.getBitmap(),
                    lightRook1.getX(),
                    lightRook1.getY(),
                    paint
            );
            //Light Rook2
            canvas.drawBitmap(
                    lightRook2.getBitmap(),
                    lightRook2.getX(),
                    lightRook2.getY(),
                    paint
            );

            //Unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        //when the game is paused
        //setting te variable to false
        playing = false;
        try {
            //stopping the thread
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        //when the game is resumed
        //starting the thread again
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void updateTouchHandler() {
        Log.i("touchHandler", "Solid Tap - X: " + touchedX + " Y: " + touchedY);
        //player clicked on the board
        if(touchedX >= leftBoard && touchedX <= leftBoard + boardSize && touchedY >= topBoard && touchedY <= topBoard + boardSize){
            int xVal = Math.round(touchedX);
            int yVal = Math.round(touchedY);
            if(possibleMoves != null) {
                // Check if the player's touch was to make one of the possible moves
                int[] tempTouchedSquare = checkSquareTouch(xVal, yVal);
                for(int i=0;i<possibleMoves.size();i++) {
                    int possibleMoveY = possibleMoves.get(i).get(0);
                    int possibleMoveX = possibleMoves.get(i).get(1);
                    // Player Clicked on a possible move square
                    if(tempTouchedSquare[0] == possibleMoveY && tempTouchedSquare[1] == possibleMoveX) {
                        Log.i("Move", "Player wants to make move!!!");

                        break;
                    }
                }
            }

            touchedSquare = checkSquareTouch(xVal, yVal);
            Log.i("touchedSquare", "Touched Square - Vertical: " + touchedSquare[0] + " Horizontal: " + touchedSquare[1]);
            possibleMoves = gameInfo.getPossibleMoves(touchedSquare);
        }
    }

    private int[] checkSquareTouch(int xVal, int yVal) {
        int[] touchedSquare = new int[2];
        // Iterate through height
        for(int i=0;i<8;i++) {
            //Iterate through width
            for(int j=0;j<8;j++) {
                //Check if tapped in this square
                int leftPoint = squaresLayout[i][j][0];
                int topPoint = squaresLayout[i][j][1];
                if(xVal >= leftPoint && xVal <= leftPoint + squareSize) {
                    if(yVal >= topPoint && yVal <= topPoint + squareSize) {
                        touchedSquare[0] = i;
                        touchedSquare[1] = j;
                        return touchedSquare;
                    }
                }
            }
        }
        return null;
    }

    private void createLayout() {
        //[height][width][upper left x and y coordinates]
        squaresLayout = new int[8][8][2];
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                //x val
                squaresLayout[i][j][0] = (j * squareSize) + leftBoard;
                //y val
                squaresLayout[i][j][1] = (i * squareSize) + topBoard;
            }
        }
    }

    private void setupPieces(Context context) {
        //Dark Pieces
        darkBishop1 = new DarkBishop(context, squareSize, leftBoard, topBoard);
        darkBishop1.updatePosition(2, 0);
        boardLayout[0][2] = darkBishop1;
        darkBishop2 = new DarkBishop(context, squareSize, leftBoard, topBoard);
        darkBishop2.updatePosition(5, 0);
        boardLayout[0][5] = darkBishop2;


        darkKing = new DarkKing(context, squareSize, leftBoard, topBoard);
        darkKing.updatePosition(4, 0);
        boardLayout[0][4] = darkKing;


        darkKnight1 = new DarkKnight(context, squareSize, leftBoard, topBoard);
        darkKnight1.updatePosition(1, 0);
        boardLayout[0][1] = darkKnight1;
        darkKnight2 = new DarkKnight(context, squareSize, leftBoard, topBoard);
        darkKnight2.updatePosition(6, 0);
        boardLayout[0][6] = darkKnight2;

        darkPawn1 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn1.updatePosition(0, 1);
        boardLayout[1][0] = darkPawn1;
        darkPawn2 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn2.updatePosition(1, 1);
        boardLayout[1][1] = darkPawn2;
        darkPawn3 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn3.updatePosition(2, 1);
        boardLayout[1][2] = darkPawn3;
        darkPawn4 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn4.updatePosition(3, 1);
        boardLayout[1][3] = darkPawn4;
        darkPawn5 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn5.updatePosition(4, 1);
        boardLayout[1][4] = darkPawn5;
        darkPawn6 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn6.updatePosition(5, 1);
        boardLayout[1][5] = darkPawn6;
        darkPawn7 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn7.updatePosition(6, 1);
        boardLayout[1][6] = darkPawn7;
        darkPawn8 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn8.updatePosition(7, 1);
        boardLayout[1][7] = darkPawn8;

        darkQueen = new DarkQueen(context, squareSize, leftBoard, topBoard);
        darkQueen.updatePosition(3, 0);
        boardLayout[0][3] = darkQueen;

        darkRook1 = new DarkRook(context, squareSize, leftBoard, topBoard);
        darkRook1.updatePosition(0, 0);
        boardLayout[0][0] = darkRook1;
        darkRook2 = new DarkRook(context, squareSize, leftBoard, topBoard);
        darkRook2.updatePosition(7, 0);
        boardLayout[0][7] = darkRook2;


        //Light Pieces
        lightBishop1 = new LightBishop(context, squareSize, leftBoard, topBoard);
        lightBishop1.updatePosition(2, 7);
        boardLayout[7][2] = lightBishop1;
        lightBishop2 = new LightBishop(context, squareSize, leftBoard, topBoard);
        lightBishop2.updatePosition(5, 7);
        boardLayout[7][5] = lightBishop2;

        lightKing = new LightKing(context, squareSize, leftBoard, topBoard);
        lightKing.updatePosition(4, 7);
        boardLayout[7][4] = lightKing;

        lightKnight1 = new LightKnight(context, squareSize, leftBoard, topBoard);
        lightKnight1.updatePosition(1, 7);
        boardLayout[7][1] = lightKnight1;
        lightKnight2 = new LightKnight(context, squareSize, leftBoard, topBoard);
        lightKnight2.updatePosition(6, 7);
        boardLayout[7][6] = lightKnight2;

        lightPawn1 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn1.updatePosition(0, 6);
        boardLayout[6][0] = lightPawn1;
        lightPawn2 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn2.updatePosition(1, 6);
        boardLayout[6][1] = lightPawn2;
        lightPawn3 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn3.updatePosition(2, 6);
        boardLayout[6][2] = lightPawn3;
        lightPawn4 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn4.updatePosition(3, 6);
        boardLayout[6][3] = lightPawn4;
        lightPawn5 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn5.updatePosition(4, 6);
        boardLayout[6][4] = lightPawn5;
        lightPawn6 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn6.updatePosition(5, 6);
        boardLayout[6][5] = lightPawn6;
        lightPawn7 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn7.updatePosition(6, 6);
        boardLayout[6][6] = lightPawn7;
        lightPawn8 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn8.updatePosition(7, 6);
        boardLayout[6][7] = lightPawn8;

        lightQueen = new LightQueen(context, squareSize, leftBoard, topBoard);
        lightQueen.updatePosition(3, 7);
        boardLayout[7][3] = lightQueen;

        lightRook1 = new LightRook(context, squareSize, leftBoard, topBoard);
        lightRook1.updatePosition(0, 7);
        boardLayout[7][0] = lightRook1;
        lightRook2 = new LightRook(context, squareSize, leftBoard, topBoard);
        lightRook2.updatePosition(7, 7);
        boardLayout[7][7] = lightRook2;
    }

    public static int getLeftBoard() {
        return leftBoard;
    }

    public static int getTopBoard() {
        return topBoard;
    }

    public void drawDarkPawns() {
        //Dark pawn 1
        canvas.drawBitmap(
                darkPawn1.getBitmap(),
                darkPawn1.getX(),
                darkPawn1.getY(),
                paint
        );
        //Dark pawn 2
        canvas.drawBitmap(
                darkPawn2.getBitmap(),
                darkPawn2.getX(),
                darkPawn2.getY(),
                paint
        );
        //Dark pawn 3
        canvas.drawBitmap(
                darkPawn3.getBitmap(),
                darkPawn3.getX(),
                darkPawn3.getY(),
                paint
        );
        //Dark pawn 4
        canvas.drawBitmap(
                darkPawn4.getBitmap(),
                darkPawn4.getX(),
                darkPawn4.getY(),
                paint
        );
        //Dark pawn 5
        canvas.drawBitmap(
                darkPawn5.getBitmap(),
                darkPawn5.getX(),
                darkPawn5.getY(),
                paint
        );
        //Dark pawn 6
        canvas.drawBitmap(
                darkPawn6.getBitmap(),
                darkPawn6.getX(),
                darkPawn6.getY(),
                paint
        );
        //Dark pawn 7
        canvas.drawBitmap(
                darkPawn7.getBitmap(),
                darkPawn7.getX(),
                darkPawn7.getY(),
                paint
        );
        //Dark pawn 8
        canvas.drawBitmap(
                darkPawn8.getBitmap(),
                darkPawn8.getX(),
                darkPawn8.getY(),
                paint
        );
    }

    public void drawLightPawns() {
        //Light pawn 1
        canvas.drawBitmap(
                lightPawn1.getBitmap(),
                lightPawn1.getX(),
                lightPawn1.getY(),
                paint
        );
        //Light pawn 2
        canvas.drawBitmap(
                lightPawn2.getBitmap(),
                lightPawn2.getX(),
                lightPawn2.getY(),
                paint
        );
        //Light pawn 3
        canvas.drawBitmap(
                lightPawn3.getBitmap(),
                lightPawn3.getX(),
                lightPawn3.getY(),
                paint
        );
        //Light pawn 4
        canvas.drawBitmap(
                lightPawn4.getBitmap(),
                lightPawn4.getX(),
                lightPawn4.getY(),
                paint
        );
        //Light pawn 5
        canvas.drawBitmap(
                lightPawn5.getBitmap(),
                lightPawn5.getX(),
                lightPawn5.getY(),
                paint
        );
        //Light pawn 6
        canvas.drawBitmap(
                lightPawn6.getBitmap(),
                lightPawn6.getX(),
                lightPawn6.getY(),
                paint
        );
        //Light pawn 7
        canvas.drawBitmap(
                lightPawn7.getBitmap(),
                lightPawn7.getX(),
                lightPawn7.getY(),
                paint
        );
        //Light pawn 8
        canvas.drawBitmap(
                lightPawn8.getBitmap(),
                lightPawn8.getX(),
                lightPawn8.getY(),
                paint
        );
    }
}
