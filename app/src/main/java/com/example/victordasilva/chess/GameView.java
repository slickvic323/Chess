package com.example.victordasilva.chess;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
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
import com.example.victordasilva.chess.chess_pieces.SettingsIcon;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.screenSize;
import static com.example.victordasilva.chess.ConnectActivity.MESSAGE_READ;

/**
 * Created by victordasilva on 5/1/18.
 */

public class GameView extends SurfaceView implements Runnable {
    //boolean variable to track if the game is playing or not
    volatile boolean playing;

    private Context context;

    private boolean connectionEstablished;

    //the game thread
    private Thread gameThread = null;

    //Adding the GameBackground
    private GameBackground gameBackground;
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

    // Adding GameClock Picture
    private Clock clockPic;

    // Adding the Settings button
    private SettingsIcon settingsIcon;

    private ArrayList<ChessPiece> chessPieces;

    // y and x positions (pixels)
    private int[][][] squaresLayout;

    // y and x positions (relative to board)
    private ChessPiece[][] boardLayout;

    PlayerInfo myInfo;
    PlayerInfo opponentInfo;

    private static int screenSizeX;
    private static int screenSizeY;
    private static int boardSize;
    private static int squareSize;
    private static int leftBoard;
    private static int topBoard;

    private static Timer myTimer;
    private String timerString = "";

    private float touchedX;
    private float touchedY;
    private int[] touchedSquare; //height, width
    private ArrayList<ArrayList<Integer>> possibleMoves;
    private boolean movementTouchDetected;

    private float clickStartX, clickStartY, clickEndX, clickEndY;

    private GameInfo gameInfo;

    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    public ServerClass serverClass;
    public ClientClass clientClass;
    public SendReceive sendReceive;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    //Class constructor
    public GameView(Context context, int screenSizeX, int screenSizeY, WifiP2pManager mManager, WifiP2pManager.Channel mChannel, PlayerInfo myInfo, PlayerInfo opponentInfo) {
        super(context);

        // Fixes the NetworkOnMainThread Exception
        enableStrictMode();

        this.mManager = mManager;
        this.mChannel = mChannel;
        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

                if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                    // This is the host of the group
                    serverClass = new ServerClass();
                    serverClass.start();
                    Log.i("ServerClassAlive", String.valueOf(serverClass.isAlive()));
                } else if(wifiP2pInfo.groupFormed) {
                    // This is the client
                    clientClass = new ClientClass(groupOwnerAddress);
                    clientClass.start();
                    Log.i("ClientClassAlive", String.valueOf(clientClass.isAlive()));
                }
            }
        });



        this.context = context;
        this.myInfo = myInfo;
        this.opponentInfo = opponentInfo;
        this.screenSizeX = screenSizeX;
        this.screenSizeY = screenSizeY;
        this.squareSize = screenSizeX / 8;
        chessPieces = new ArrayList<ChessPiece>();
        movementTouchDetected = false;

        clickStartX = -1.0f;
        clickStartY = -1.0f;
        clickEndX = -1.0f;
        clickEndY = -1.0f;

        //Initializing the GameBackground object
        gameBackground = new GameBackground(context, screenSizeX, screenSizeY);
        //Initializing gameBoard object
        gameBoard = new GameBoard(context, screenSizeX, screenSizeY);
        //Initializing Clock Picture object
        clockPic = new Clock(context, screenSizeX, screenSizeY);
        leftBoard = gameBoard.getX();
        topBoard = gameBoard.getY();
        boardSize = gameBoard.getBoardSize();

        // Initializing the settings icon
        settingsIcon = new SettingsIcon(context);

        createLayout();

        boardLayout = new ChessPiece[8][8];
        setupPieces(context);

        myTimer = new Timer();
        if(myInfo.getColor().equals("Light")) {
            gameInfo = new GameInfo(boardLayout, "Light", 121000);
        } else if(myInfo.getColor().equals("Dark")) {
            flipBoard();
            gameInfo = new GameInfo(boardLayout, "Dark", 121000);
        }

        connectionEstablished = false;

        //Initializing Drawing Objects
        surfaceHolder = getHolder();
        paint = new Paint();

    }

    private void startTimer() {
        if(gameInfo.getWhoseTurn().equals(myInfo.getColor())) {
            myTimer = new Timer();
            gameInfo.setTimeRemaining(gameInfo.getTimeForTurns());
            myTimer.schedule(new TimerTask() {
                                 @Override
                                 public void run() {
                                     gameInfo.setTimeRemaining(gameInfo.getTimeRemaining()-1000);
                                     timerString = getTimerString(gameInfo.getTimeRemaining());
                                 }
                             },
                    1000, 1000);
            sendTimer();
        }
    }

    private void sendTimer() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message_type", 4);
        String message = jsonObject.toString();
        sendReceive.write(message.getBytes());
    }

    private String getTimerString(long ms) {
        String time = "";
        int totalSeconds = (int) ms/1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        if(seconds < 10) {
            time = minutes + ":0" + seconds;
        } else {
            time = minutes + ":" + seconds;
        }
        return time;
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
                // Set the click down spot if this is the beginning of the click
                if(clickStartX == -1.0f && clickStartY == -1.0f) {
                    clickStartX = motionEvent.getX();
                    clickStartY = motionEvent.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!movementTouchDetected){
                    touchedX = motionEvent.getX();
                    touchedY = motionEvent.getY();
                    updateTouchHandler();
                }
                clickEndX = motionEvent.getX();
                clickEndY = motionEvent.getY();
                // Check if the click started in the settings icon
                if(clickStartX >= 20 && clickStartX <= 170 && clickStartY >= 40 && clickStartY <= 190) {
                    // Check if the click ended in the settings icon
                    if(clickEndX >= 20 && clickEndX <= 170 && clickEndY >= 40 && clickEndY <= 190) {
                        // They clicked the settings button
                        Log.i("Settings", "Settings Button Clicked");
                        // Play the click_sound.mp3 sound for settings button clicked
                        MediaPlayer mp = MediaPlayer.create(context, R.raw.click_sound);
                        mp.start();
                    }
                }
                // Clear the variables
                clickStartX = -1.0f;
                clickStartY = -1.0f;
                clickEndX = -1.0f;
                clickEndY = -1.0f;


                break;
            case MotionEvent.ACTION_MOVE:
                //movementTouchDetected = true;
                break;
            default:
                break;
        }
        return true;
    }


    private void update() {
        if(!connectionEstablished) {
            if(sendReceive!=null) {
                startTimer();
                connectionEstablished = true;
            }
        }
    }

    private void draw() {
        //Checking if surface is valid
        if (surfaceHolder.getSurface().isValid()) {
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //Drawing a background color for the canvas
            canvas.drawColor(Color.BLACK);
            //Drawing the Background Image
            canvas.drawBitmap(
                    gameBackground.getBitmap(),
                    gameBackground.getX(),
                    gameBackground.getY(),
                    paint
            );

            // Drawing the Clock Pic
            canvas.drawBitmap(
                    clockPic.getBitmap(),
                    clockPic.getX(),
                    clockPic.getY(),
                    paint
            );

            // Drawing the timer time
            Paint timerTextPaint = new Paint();
            timerTextPaint.setColor(Color.RED);
            timerTextPaint.setStyle(Paint.Style.FILL);
            timerTextPaint.setTextSize(5*clockPic.getHeight()/6);
            Typeface tf = Typeface.createFromAsset(getResources().getAssets(), "fonts/simply_square.ttf");
            timerTextPaint.setTypeface(tf);
            if(timerString.length()>0) {
                canvas.drawText(
                        timerString.substring(0, 1),
                        screenSizeX/4 + clockPic.getWidth()/14,
                        clockPic.getHeight() - clockPic.getHeight()/8,
                        timerTextPaint
                );
                canvas.drawText(
                        timerString.substring(2, 3),
                        screenSizeX/4 + (47*clockPic.getWidth()/100),
                        clockPic.getHeight() - clockPic.getHeight()/8,
                        timerTextPaint
                );
                canvas.drawText(
                        timerString.substring(3, 4),
                        screenSizeX/4 + (3*clockPic.getWidth()/4),
                        clockPic.getHeight() - clockPic.getHeight()/8,
                        timerTextPaint
                );
            }

            // Draw the Turn Signifier
            Paint turnPaint = new Paint();
            turnPaint.setColor(Color.BLACK);
            turnPaint.setStyle(Paint.Style.FILL);
            turnPaint.setTextSize(screenSizeY/18);
            Typeface turnTF = Typeface.createFromAsset(getResources().getAssets(), "fonts/josefinsans-regular.ttf");
            turnPaint.setTypeface(turnTF);
            turnPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(
                    gameInfo.getWhoseTurnText(),
                    screenSizeX/2,
                    screenSizeY/8+screenSizeY/18,
                    turnPaint
            );

            // Draw the Settings Icon
            canvas.drawBitmap(
                    settingsIcon.getBitmap(),
                    settingsIcon.getX(),
                    settingsIcon.getY(),
                    paint
            );

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

            for(int i=0;i<chessPieces.size();i++) {
                ChessPiece currentPiece = chessPieces.get(i);
                if(currentPiece.isInPlay()) {
                    canvas.drawBitmap(
                            currentPiece.getBitmap(),
                            currentPiece.getX(),
                            currentPiece.getY(),
                            paint
                    );
                }
            }

            // Drawing the names of the players
            Paint namePaint = new Paint();
            namePaint.setColor(Color.rgb(0, 102, 102));
            namePaint.setStyle(Paint.Style.FILL);
            namePaint.setTextSize(screenSizeY/18);
            Typeface nameTF = Typeface.createFromAsset(getResources().getAssets(), "fonts/josefinsans_bold.ttf");
            namePaint.setTypeface(nameTF);
            namePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(
                    myInfo.getName() + " VS " + opponentInfo.getName(),
                    canvas.getWidth()/2,
                    gameBoard.getY() + screenSizeX + screenSizeY/18 + 5,
                    namePaint
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
            boolean madeMove = false;

            if(gameInfo.getWhoseTurn().equals(gameInfo.getUserColor())) {
                if(possibleMoves != null) {
                    // Check if the player's touch was to make one of the possible moves
                    int[] tempTouchedSquare = checkSquareTouch(xVal, yVal);
                    for(int i=0;i<possibleMoves.size();i++) {
                        int possibleMoveY = possibleMoves.get(i).get(0);
                        int possibleMoveX = possibleMoves.get(i).get(1);
                        // Player Clicked on a possible move square
                        if(tempTouchedSquare[0] == possibleMoveY && tempTouchedSquare[1] == possibleMoveX) {
                            Log.i("Move", "Player wants to make move!!!");
                            // Move the selected piece to the empty space that was selected
                            if(boardLayout[tempTouchedSquare[0]][tempTouchedSquare[1]] == null) {
                                ChessPiece movingPiece = boardLayout[touchedSquare[0]][touchedSquare[1]];
                                boardLayout[tempTouchedSquare[0]][tempTouchedSquare[1]] = movingPiece;
                                boardLayout[touchedSquare[0]][touchedSquare[1]] = null;
                                movingPiece.updatePosition(tempTouchedSquare[1], tempTouchedSquare[0]);
                                if(!movingPiece.hasMadeFirstMove()) {
                                    movingPiece.setMadeFirstMove(true);
                                }
                                madeMove = true;
                            } else {
                                // Check that the piece is of the opposite color
                                ChessPiece movingPiece = boardLayout[touchedSquare[0]][touchedSquare[1]];
                                ChessPiece enemyPiece = boardLayout[tempTouchedSquare[0]][tempTouchedSquare[1]];
                                if(areOppositeColors(enemyPiece.isDarkPiece(), movingPiece.isDarkPiece())) {
                                    boardLayout[tempTouchedSquare[0]][tempTouchedSquare[1]] = movingPiece;
                                    boardLayout[touchedSquare[0]][touchedSquare[1]] = null;
                                    movingPiece.updatePosition(tempTouchedSquare[1], tempTouchedSquare[0]);
                                    // Enemy Piece is taken down
                                    enemyPiece.setInPlay(false);
                                    enemyPiece.updatePosition(10, 10);

                                    // Checkmate has been made
                                    if(enemyPiece.getPieceName().equals("King")) {
                                        if(gameInfo.getWhoseTurn().equals("Dark")) {
                                            gameInfo.setWinner("Dark");
                                        } else {
                                            gameInfo.setWinner("Light");
                                        }
                                        gameInfo.setInProgress(false);
                                    }
                                    if(!movingPiece.hasMadeFirstMove()) {
                                        movingPiece.setMadeFirstMove(true);
                                    }
                                    madeMove = true;
                                }
                            }
                            sendMoveToOpponent(touchedSquare[1], touchedSquare[0], tempTouchedSquare[1], tempTouchedSquare[0]);
                            myTimer.cancel();
                            myTimer.purge();
                            myTimer = null;
                            break;
                        }
                    }
                }
            }
            if(madeMove) {
                if(gameInfo.getUserColor().equals("Dark")) {
                    // Change the turn to Light user
                    gameInfo.setWhoseTurn("Light");
                } else if(gameInfo.getUserColor().equals("Light")) {
                    // Change the turn to the Dark user
                    gameInfo.setWhoseTurn("Dark");
                }
                touchedSquare = null;
                // Play the click_sound_2.mp3 sound for a click that resulted in a made move
                MediaPlayer mp = MediaPlayer.create(context, R.raw.click_sound_2);
                mp.start();

                // Check if the move has made the current user get a check on the opponent
                gameInfo.checkForCheck();
                if(gameInfo.checkOnOpponent) {
                    // TODO: Send a check notification over to opponent
                }
            } else {
                touchedSquare = checkSquareTouch(xVal, yVal);
                Log.i("touchedSquare", "Touched Square - Vertical: " + touchedSquare[0] + " Horizontal: " + touchedSquare[1]);
                // Play the click_sound.mp3 sound for a click that did NOT result in a made move
                MediaPlayer mp = MediaPlayer.create(context, R.raw.click_sound);
                mp.start();
            }
            gameInfo.setBoardLayout(boardLayout);
            possibleMoves = gameInfo.getPossibleMoves(touchedSquare);
        }
    }

    private void sendMoveToOpponent(int beginX, int beginY, int endX, int endY) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message_type", 2);
        // jsonObject.put("move", "Player just sent you their move");
        jsonObject.put("beginX", beginX);
        jsonObject.put("beginY", beginY);
        jsonObject.put("endX", endX);
        jsonObject.put("endY", endY);
        jsonObject.put("colorMoved", myInfo.getColor());
        String message = jsonObject.toString();
        sendReceive.write(message.getBytes());
    }


    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                Log.i("ServerClass", "In the run method");
                serverSocket = new ServerSocket(8000);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt) {
            socket = skt;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            Log.i("SendReceive", "In the Run Method");
            byte[] buffer = new byte[1024];
            int bytes;

            while (socket!=null) {
                try {
                    bytes = inputStream.read(buffer);
                    if(bytes > 0) {
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread {
        Socket socket;
        String hostAddress;

        public ClientClass(InetAddress hostAddress) {
            this.hostAddress = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            Log.i("ClientClass", "In the Run Method");
            try {
                while(!socket.isConnected()) {
                    socket.connect(new InetSocketAddress(hostAddress, 8000), 10000);
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //TODO
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    Log.i("TempMessage", tempMsg);
                    JSONParser parser = new JSONParser();
                    try {
                        JSONObject jsonObject = (JSONObject) parser.parse(tempMsg);
                        long messageType = (long) jsonObject.get("message_type");
                        Log.i("Message type", String.valueOf(messageType));
                        if(messageType == 1) {

                        } else if(messageType == 2) {
                            // String note = (String) jsonObject.get("move");
                            // Log.i("Note", note);
                            int beginX = (int) ((long) jsonObject.get("beginX"));
                            int beginY = (int) ((long) jsonObject.get("beginY"));
                            int endX = (int) ((long) jsonObject.get("endX"));
                            int endY = (int) ((long) jsonObject.get("endY"));

                            // Flip the movement values!
                            beginX = 7-beginX;
                            beginY = 7-beginY;
                            endX = 7-endX;
                            endY = 7-endY;

                            // Piece that moved
                            ChessPiece movingPiece = boardLayout[beginY][beginX];

                            // Did not destroy another piece
                            if(boardLayout[endY][endX]==null) {
                                boardLayout[endY][endX] = movingPiece;
                                movingPiece.updatePosition(endX, endY);
                            } else {
                                // Piece destroyed another piece
                                ChessPiece destroyedPiece = boardLayout[endY][endX];
                                destroyedPiece.setInPlay(false);
                                boardLayout[endY][endX] = movingPiece;
                                movingPiece.updatePosition(endX, endY);
                            }

                            String colorMoved = (String) jsonObject.get("colorMoved");
                            if(colorMoved.equals("Dark")) {
                                // Change the turn to Light user
                                gameInfo.setWhoseTurn("Light");
                            } else if(colorMoved.equals("Light")) {
                                // Change the turn to the Dark user
                                gameInfo.setWhoseTurn("Dark");
                            }
                            myTimer.cancel();
                            myTimer.purge();
                            myTimer = null;
                            startTimer();
                        } else if(messageType == 4) {
                            myTimer = new Timer();
                            gameInfo.setTimeRemaining(gameInfo.getTimeForTurns());
                            myTimer.schedule(new TimerTask() {
                                                 @Override
                                                 public void run() {
                                                     gameInfo.setTimeRemaining(gameInfo.getTimeRemaining()-1000);
                                                     timerString = getTimerString(gameInfo.getTimeRemaining());
                                                     if(gameInfo.getTimeRemaining() <= 0) {
                                                         gameInfo.setNumFaults(gameInfo.getNumFaults()+1);
                                                         if(gameInfo.getNumFaults() >= 2) {
                                                             // Whoever didn't make move in time loses
                                                             if(gameInfo.getWhoseTurn().equals("Dark")) {
                                                                 gameInfo.setWinner("Light");
                                                             } else {
                                                                 gameInfo.setWinner("Dark");
                                                             }
                                                             // Game is over
                                                             gameInfo.setInProgress(false);
                                                         }
                                                     }
                                                 }
                                             },
                                    1000, 1000);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    });




    private boolean areOppositeColors(boolean isDark1, boolean isDark2) {
        if(isDark1 && !isDark2){
            return true;
        } else if(!isDark1 && isDark2) {
            return true;
        }
        return false;
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
        chessPieces.add(darkBishop1);
        boardLayout[0][2] = darkBishop1;
        darkBishop2 = new DarkBishop(context, squareSize, leftBoard, topBoard);
        darkBishop2.updatePosition(5, 0);
        chessPieces.add(darkBishop2);
        boardLayout[0][5] = darkBishop2;


        darkKing = new DarkKing(context, squareSize, leftBoard, topBoard);
        darkKing.updatePosition(4, 0);
        chessPieces.add(darkKing);
        boardLayout[0][4] = darkKing;


        darkKnight1 = new DarkKnight(context, squareSize, leftBoard, topBoard);
        darkKnight1.updatePosition(1, 0);
        chessPieces.add(darkKnight1);
        boardLayout[0][1] = darkKnight1;
        darkKnight2 = new DarkKnight(context, squareSize, leftBoard, topBoard);
        darkKnight2.updatePosition(6, 0);
        chessPieces.add(darkKnight2);
        boardLayout[0][6] = darkKnight2;

        darkPawn1 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn1.updatePosition(0, 1);
        chessPieces.add(darkPawn1);
        boardLayout[1][0] = darkPawn1;
        darkPawn2 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn2.updatePosition(1, 1);
        chessPieces.add(darkPawn2);
        boardLayout[1][1] = darkPawn2;
        darkPawn3 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn3.updatePosition(2, 1);
        chessPieces.add(darkPawn3);
        boardLayout[1][2] = darkPawn3;
        darkPawn4 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn4.updatePosition(3, 1);
        chessPieces.add(darkPawn4);
        boardLayout[1][3] = darkPawn4;
        darkPawn5 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn5.updatePosition(4, 1);
        chessPieces.add(darkPawn5);
        boardLayout[1][4] = darkPawn5;
        darkPawn6 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn6.updatePosition(5, 1);
        chessPieces.add(darkPawn6);
        boardLayout[1][5] = darkPawn6;
        darkPawn7 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn7.updatePosition(6, 1);
        chessPieces.add(darkPawn7);
        boardLayout[1][6] = darkPawn7;
        darkPawn8 = new DarkPawn(context, squareSize, leftBoard, topBoard);
        darkPawn8.updatePosition(7, 1);
        chessPieces.add(darkPawn8);
        boardLayout[1][7] = darkPawn8;

        darkQueen = new DarkQueen(context, squareSize, leftBoard, topBoard);
        darkQueen.updatePosition(3, 0);
        chessPieces.add(darkQueen);
        boardLayout[0][3] = darkQueen;

        darkRook1 = new DarkRook(context, squareSize, leftBoard, topBoard);
        darkRook1.updatePosition(0, 0);
        chessPieces.add(darkRook1);
        boardLayout[0][0] = darkRook1;
        darkRook2 = new DarkRook(context, squareSize, leftBoard, topBoard);
        darkRook2.updatePosition(7, 0);
        chessPieces.add(darkRook2);
        boardLayout[0][7] = darkRook2;


        //Light Pieces
        lightBishop1 = new LightBishop(context, squareSize, leftBoard, topBoard);
        lightBishop1.updatePosition(2, 7);
        chessPieces.add(lightBishop1);
        boardLayout[7][2] = lightBishop1;
        lightBishop2 = new LightBishop(context, squareSize, leftBoard, topBoard);
        lightBishop2.updatePosition(5, 7);
        chessPieces.add(lightBishop2);
        boardLayout[7][5] = lightBishop2;

        lightKing = new LightKing(context, squareSize, leftBoard, topBoard);
        lightKing.updatePosition(4, 7);
        chessPieces.add(lightKing);
        boardLayout[7][4] = lightKing;

        lightKnight1 = new LightKnight(context, squareSize, leftBoard, topBoard);
        lightKnight1.updatePosition(1, 7);
        chessPieces.add(lightKnight1);
        boardLayout[7][1] = lightKnight1;
        lightKnight2 = new LightKnight(context, squareSize, leftBoard, topBoard);
        lightKnight2.updatePosition(6, 7);
        chessPieces.add(lightKnight2);
        boardLayout[7][6] = lightKnight2;

        lightPawn1 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn1.updatePosition(0, 6);
        chessPieces.add(lightPawn1);
        boardLayout[6][0] = lightPawn1;
        lightPawn2 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn2.updatePosition(1, 6);
        chessPieces.add(lightPawn2);
        boardLayout[6][1] = lightPawn2;
        lightPawn3 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn3.updatePosition(2, 6);
        chessPieces.add(lightPawn3);
        boardLayout[6][2] = lightPawn3;
        lightPawn4 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn4.updatePosition(3, 6);
        chessPieces.add(lightPawn4);
        boardLayout[6][3] = lightPawn4;
        lightPawn5 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn5.updatePosition(4, 6);
        chessPieces.add(lightPawn5);
        boardLayout[6][4] = lightPawn5;
        lightPawn6 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn6.updatePosition(5, 6);
        chessPieces.add(lightPawn6);
        boardLayout[6][5] = lightPawn6;
        lightPawn7 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn7.updatePosition(6, 6);
        chessPieces.add(lightPawn7);
        boardLayout[6][6] = lightPawn7;
        lightPawn8 = new LightPawn(context, squareSize, leftBoard, topBoard);
        lightPawn8.updatePosition(7, 6);
        chessPieces.add(lightPawn8);
        boardLayout[6][7] = lightPawn8;

        lightQueen = new LightQueen(context, squareSize, leftBoard, topBoard);
        lightQueen.updatePosition(3, 7);
        chessPieces.add(lightQueen);
        boardLayout[7][3] = lightQueen;

        lightRook1 = new LightRook(context, squareSize, leftBoard, topBoard);
        lightRook1.updatePosition(0, 7);
        chessPieces.add(lightRook1);
        boardLayout[7][0] = lightRook1;
        lightRook2 = new LightRook(context, squareSize, leftBoard, topBoard);
        lightRook2.updatePosition(7, 7);
        chessPieces.add(lightRook2);
        boardLayout[7][7] = lightRook2;
    }

    public static int getLeftBoard() {
        return leftBoard;
    }

    public static int getTopBoard() {
        return topBoard;
    }

    // General Information
    // Message Type: 3
    public void sendGeneralInfo() {
        // TODO
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message_type", 3);

        String message = jsonObject.toString();
        sendReceive.write(message.getBytes());
    }

    public void flipBoard() {
        for(ChessPiece piece : chessPieces) {
            int newX = 7-piece.getxSquare();
            int newY = 7-piece.getySquare();
            piece.updatePosition(newX, newY);
            boardLayout[newY][newX] = piece;
        }
    }





    public void enableStrictMode()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

}
