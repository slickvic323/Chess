package com.example.victordasilva.chess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import com.example.victordasilva.chess.chess_pieces.LightBishop;
import com.example.victordasilva.chess.chess_pieces.LightKing;
import com.example.victordasilva.chess.chess_pieces.LightKnight;
import com.example.victordasilva.chess.chess_pieces.LightPawn;
import com.example.victordasilva.chess.chess_pieces.LightQueen;
import com.example.victordasilva.chess.chess_pieces.LightRook;
import com.example.victordasilva.chess.chess_pieces.TitleBackground;

import java.util.Random;

public class TitleView extends SurfaceView implements Runnable {
    //boolean variable to track if the game is playing or not
    volatile boolean running;

    //the game thread
    private Thread titleThread = null;

    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private TitleBackground titleBackground;
    private int screenSizeX, screenSizeY;
    private Context context;
    private int squareSize, leftBoard, topBoard;


    // Floating Chess Pieces
    private DarkBishop darkBishop;
    private DarkKing darkKing;
    private DarkKnight darkKnight;
    private DarkPawn darkPawn;
    private DarkQueen darkQueen;
    private DarkRook darkRook;
    private LightBishop lightBishop;
    private LightKing lightKing;
    private LightKnight lightKnight;
    private LightPawn lightPawn;
    private LightQueen lightQueen;
    private LightRook lightRook;

    private int[] xSpeeds = new int[12];
    private int[] ySpeeds = new int[12];

    private boolean properPlayDown;


    public TitleView(Context context, int screenSizeX, int screenSizeY) {
        super(context);
        this.context = context;
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;

        squareSize = screenSizeX / 8;
        leftBoard = 0;
        topBoard = (screenSizeY/2)-(screenSizeX/2);

        properPlayDown = false;

        titleBackground = new TitleBackground(context, screenSizeX, screenSizeY);
        //Initializing Drawing Objects
        surfaceHolder = getHolder();
        paint = new Paint();

        initializePieces();
    }

    private void draw() {
        if(surfaceHolder.getSurface().isValid()) {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //Drawing a background color for the canvas
            canvas.drawColor(Color.BLACK);

            // Draw the Title Background Image
            canvas.drawBitmap(
                    titleBackground.getBitmap(),
                    titleBackground.getX(),
                    titleBackground.getY(),
                    paint
            );

            drawPieces();

            Paint titlePaint = new Paint();
            titlePaint.setColor(Color.parseColor("#4700B3"));
            titlePaint.setStyle(Paint.Style.FILL);
            titlePaint.setTextSize(screenSizeY/8);
            titlePaint.setTextAlign(Paint.Align.CENTER);
            Typeface tf = Typeface.createFromAsset(getResources().getAssets(), "fonts/garineldo.otf");
            titlePaint.setTypeface(tf);

            canvas.drawText(
                    "Chess Royale",
                    canvas.getWidth()/2,
                    screenSizeY/ 3,
                    titlePaint
            );

            Paint playPaint = new Paint();
            playPaint.setColor(Color.parseColor("#4D4DFF"));
            playPaint.setStyle(Paint.Style.FILL);
            playPaint.setTextSize(screenSizeY/14);
            playPaint.setTextAlign(Paint.Align.CENTER);
            Typeface playTF = Typeface.createFromAsset(getResources().getAssets(), "fonts/josefinsans_bold.ttf");
            playPaint.setTypeface(playTF);

            canvas.drawText(
                    "PLAY",
                    canvas.getWidth()/2,
                    screenSizeY / 2,
                    playPaint
            );

            //Unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void run() {
        while(running) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        // Update position of chess pieces
        darkBishop.setX(darkBishop.getX() + xSpeeds[0]);
        darkBishop.setY(darkBishop.getY() + ySpeeds[0]);
        changeDirection(darkBishop, 0);
        darkKing.setX(darkKing.getX() + xSpeeds[1]);
        darkKing.setY(darkKing.getY() + ySpeeds[1]);
        changeDirection(darkKing, 1);
        darkKnight.setX(darkKnight.getX() + xSpeeds[2]);
        darkKnight.setY(darkKnight.getY() + ySpeeds[2]);
        changeDirection(darkKnight, 2);
        darkPawn.setX(darkPawn.getX() + xSpeeds[3]);
        darkPawn.setY(darkPawn.getY() + ySpeeds[3]);
        changeDirection(darkPawn, 3);
        darkQueen.setX(darkQueen.getX() + xSpeeds[4]);
        darkQueen.setY(darkQueen.getY() + ySpeeds[4]);
        changeDirection(darkQueen, 4);
        darkRook.setX(darkRook.getX() + xSpeeds[5]);
        darkRook.setY(darkRook.getY() + ySpeeds[5]);
        changeDirection(darkRook, 5);
        lightBishop.setX(lightBishop.getX() + xSpeeds[6]);
        lightBishop.setY(lightBishop.getY() + ySpeeds[6]);
        changeDirection(lightBishop, 6);
        lightKing.setX(lightKing.getX() + xSpeeds[7]);
        lightKing.setY(lightKing.getY() + ySpeeds[7]);
        changeDirection(lightKing, 7);
        lightKnight.setX(lightKnight.getX() + xSpeeds[8]);
        lightKnight.setY(lightKnight.getY() + ySpeeds[8]);
        changeDirection(lightKnight, 8);
        lightPawn.setX(lightPawn.getX() + xSpeeds[9]);
        lightPawn.setY(lightPawn.getY() + ySpeeds[9]);
        changeDirection(lightPawn, 9);
        lightQueen.setX(lightQueen.getX() + xSpeeds[10]);
        lightQueen.setY(lightQueen.getY() + ySpeeds[10]);
        changeDirection(lightQueen, 10);
        lightRook.setX(lightRook.getX() + xSpeeds[11]);
        lightRook.setY(lightRook.getY() + ySpeeds[11]);
        changeDirection(lightRook, 11);
    }

    private void control() {
        try {
            titleThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void pause() {
        running = false;
        try {
            //stopping the thread
            titleThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        running = true;
        titleThread = new Thread(this);
        titleThread.start();
    }

    private void initializePieces() {
        darkBishop = new DarkBishop(context, squareSize, leftBoard, topBoard);
        darkBishop.updatePosition(0, 2);
        darkKing = new DarkKing(context, squareSize, leftBoard, topBoard);
        darkKing.updatePosition(0, 3);
        darkKnight = new DarkKnight(context, squareSize, leftBoard, topBoard);
        darkKnight.updatePosition(0, 4);
        darkPawn = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn.updatePosition(0, 5);
        darkQueen = new DarkQueen(context, squareSize, leftBoard, topBoard);
        darkQueen.updatePosition(0, 6);
        darkRook = new DarkRook(context, squareSize, leftBoard, topBoard);
        darkRook.updatePosition(0, 7);
        lightBishop = new LightBishop(context, squareSize, leftBoard, topBoard);
        lightBishop.updatePosition(7, 2);
        lightKing = new LightKing(context, squareSize, leftBoard, topBoard);
        lightKing.updatePosition(7, 3);
        lightKnight = new LightKnight(context, squareSize, leftBoard, topBoard);
        lightKnight.updatePosition(7, 4);
        lightPawn = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn.updatePosition(7, 5);
        lightQueen = new LightQueen(context, squareSize, leftBoard, topBoard);
        lightQueen.updatePosition(7, 6);
        lightRook = new LightRook(context, squareSize, leftBoard, topBoard);
        lightRook.updatePosition(7, 7);

        Random rand = new Random();
        for(int i=0;i<12;i++) {
            xSpeeds[i] = rand.nextInt(20) - 10;
            ySpeeds[i] = rand.nextInt(20) - 10;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        float touchedX, touchedY;
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                touchedX = motionEvent.getX();
                touchedY = motionEvent.getY();
                if(touchedX >= screenSizeX/4 && touchedX <= screenSizeX *3/4 && touchedY>=(screenSizeY/2)-(screenSizeY/14) && touchedY<=(screenSizeY/2)+(screenSizeY/14)) {
                    properPlayDown = true;
                } else {
                    properPlayDown = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchedX = motionEvent.getX();
                touchedY = motionEvent.getY();
                if(properPlayDown && touchedX >= screenSizeX/4 && touchedX <= screenSizeX *3/4 && touchedY>=(screenSizeY/2)-(screenSizeY/14) && touchedY<=(screenSizeY/2)+(screenSizeY/14)) {
                    Log.i("Click", "You clicked Play!");
                    // Go to the Connect Activity
                    Intent startConnectActivity = new Intent(context, ConnectActivity.class);
                    context.startActivity(startConnectActivity);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void drawPieces() {
        canvas.drawBitmap(
                darkBishop.getBitmap(),
                darkBishop.getX(),
                darkBishop.getY(),
                paint
        );
        canvas.drawBitmap(
                darkKing.getBitmap(),
                darkKing.getX(),
                darkKing.getY(),
                paint
        );
        canvas.drawBitmap(
                darkKnight.getBitmap(),
                darkKnight.getX(),
                darkKnight.getY(),
                paint
        );
        canvas.drawBitmap(
                darkPawn.getBitmap(),
                darkPawn.getX(),
                darkPawn.getY(),
                paint
        );
        canvas.drawBitmap(
                darkQueen.getBitmap(),
                darkQueen.getX(),
                darkQueen.getY(),
                paint
        );
        canvas.drawBitmap(
                darkRook.getBitmap(),
                darkRook.getX(),
                darkRook.getY(),
                paint
        );
        canvas.drawBitmap(
                lightBishop.getBitmap(),
                lightBishop.getX(),
                lightBishop.getY(),
                paint
        );
        canvas.drawBitmap(
                lightKing.getBitmap(),
                lightKing.getX(),
                lightKing.getY(),
                paint
        );
        canvas.drawBitmap(
                lightKnight.getBitmap(),
                lightKnight.getX(),
                lightKnight.getY(),
                paint
        );
        canvas.drawBitmap(
                lightPawn.getBitmap(),
                lightPawn.getX(),
                lightPawn.getY(),
                paint
        );
        canvas.drawBitmap(
                lightQueen.getBitmap(),
                lightQueen.getX(),
                lightQueen.getY(),
                paint
        );
        canvas.drawBitmap(
                lightRook.getBitmap(),
                lightRook.getX(),
                lightRook.getY(),
                paint
        );
    }

    private void changeDirection(ChessPiece piece, int num) {
        if(piece.getX() <= 0) {
            xSpeeds[num] = Math.abs(xSpeeds[num]);
        } else if(piece.getX() + squareSize >= screenSizeX) {
            xSpeeds[num] = Math.abs(xSpeeds[num]) * -1;
        }
        if(piece.getY() <= 0 ) {
            ySpeeds[num] = Math.abs(ySpeeds[num]);
        } else if(piece.getY() + squareSize >= screenSizeY) {
            ySpeeds[num] = Math.abs(ySpeeds[num]) * -1;
        }
    }
}
