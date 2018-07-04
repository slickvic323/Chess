package com.example.victordasilva.chess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.victordasilva.chess.chess_pieces.TitleBackground;

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

    public TitleView(Context context, int screenSizeX, int screenSizeY) {
        super(context);

        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;

        titleBackground = new TitleBackground(context, screenSizeX, screenSizeY);
        //Initializing Drawing Objects
        surfaceHolder = getHolder();
        paint = new Paint();
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
}
