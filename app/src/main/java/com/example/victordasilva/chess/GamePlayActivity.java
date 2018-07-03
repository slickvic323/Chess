package com.example.victordasilva.chess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.example.victordasilva.chess.ConnectActivity.MESSAGE_READ;

public class GamePlayActivity extends AppCompatActivity {

    private GameView gameView;
    private PlayerInfo myInfo;
    private PlayerInfo opponentInfo;

    IntentFilter mIntentFilter;
    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        // Getting Player Info from previoius Activity
        myInfo = new PlayerInfo(getIntent().getStringExtra("myName"), getIntent().getStringExtra("myColor"));
        opponentInfo = new PlayerInfo(getIntent().getStringExtra("opponentName"), getIntent().getStringExtra("opponentColor"));

        //Getting Display Object
        Display display = getWindowManager().getDefaultDisplay();

        //Getting the screen resolution into point object
        Point size = new Point();
        display.getSize(size);

        //Initializing game view object
        gameView = new GameView(this, size.x, size.y, mManager, mChannel, myInfo, opponentInfo);

        //Adding it to contentView
        setContentView(gameView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();

        if(mReceiver!=null) {
            unregisterReceiver(mReceiver);
        }
        mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Successfully disconnected
                Log.i("connectionInfo", "Cancel Connect Successful");
            }

            @Override
            public void onFailure(int i) {
                // Unsuccessfully disconnected
                Log.i("connectionInfo", "Cancel Connect Failed");
            }
        });
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i("connectionInfo", "Removed Group Successfully");
            }

            @Override
            public void onFailure(int i) {
                Log.i("connectionInfo", "Remove Group Failed");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
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
                            String note = (String) jsonObject.get("move");
                            Log.i("Note", note);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    });


}
