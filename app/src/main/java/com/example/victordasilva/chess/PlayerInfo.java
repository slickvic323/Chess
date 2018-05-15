package com.example.victordasilva.chess;

/**
 * Created by victordasilva on 5/14/18.
 */

public class PlayerInfo {
    private boolean myTurn;
    //Color = "Dark" or "Light"
    private String color;
    private String name;

    PlayerInfo(String name, String color) {
        myTurn = false;
        this.name = name;
        this.color = color;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}