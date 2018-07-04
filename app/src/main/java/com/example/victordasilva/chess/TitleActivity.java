package com.example.victordasilva.chess;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;

import com.example.victordasilva.chess.chess_pieces.TitleBackground;

public class TitleActivity extends AppCompatActivity{

    private int screenSizeX;
    private int screenSizeY;
    private TitleBackground titleBackground;
    private TitleView titleView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting Display Object
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenSizeX = size.x;
        screenSizeY = size.y;

        //Initializing game view object
        titleView = new TitleView(this, screenSizeX, screenSizeY);

        //Adding it to contentView
        setContentView(titleView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        titleView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        titleView.resume();
    }

}
