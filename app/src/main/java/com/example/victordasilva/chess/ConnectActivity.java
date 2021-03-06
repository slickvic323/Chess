package com.example.victordasilva.chess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.message;

public class ConnectActivity extends AppCompatActivity {

    IntentFilter mIntentFilter;
    Channel mChannel;
    WifiP2pManager mManager;
    BroadcastReceiver mReceiver;

    ProgressBar progressBar;
    Button searchButton, playButton, enterNameButton, backButton;
    EditText nameTextBox;
    TableLayout userInfoLayout;
    ImageView userImage, opponentImage;
    TextView userNameText, opponentNameText;


    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    static final int MESSAGE_READ=1;

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;

    PlayerInfo playerInfo;
    PlayerInfo opponentInfo;

    Intent startGameIntent;

    private Handler timerHandler;
    private int delay;
    private boolean searching;

    private boolean gameServerReady = false;

    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for(WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }
            }
            if(peers.size() == 0) {
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                // This is the host of the group
                playerInfo = new PlayerInfo(userNameText.getText().toString(), "Dark");
                serverClass = new ServerClass();
                serverClass.start();
            } else if(wifiP2pInfo.groupFormed) {
                // This is the client
                playerInfo = new PlayerInfo(userNameText.getText().toString(), "Light");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        searching = false;
        timerHandler = new Handler();
        delay = 3000; // milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(searching) {
                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("Discover Peers", "Reattempting to discover");
                        }

                        @Override
                        public void onFailure(int i) {
                            Log.i("Discover Peers", "Reattempting to discover failed: " + i);
                        }
                    });
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);


        setupUIPieces();
        setListeners();

        setup();
    }

    private void setupUIPieces() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setVisibility(View.GONE);

        playButton = (Button) findViewById(R.id.play_button);
        playButton.setVisibility(View.GONE);
        enterNameButton = (Button) findViewById(R.id.set_name_button);

        backButton = (Button) findViewById(R.id.back_button);

        userImage = (ImageView) findViewById(R.id.user_image);
        opponentImage = (ImageView) findViewById(R.id.opponent_image);

        nameTextBox = (EditText) findViewById(R.id.name_text_box);

        userNameText = (TextView) findViewById(R.id.username_text);
        opponentNameText = (TextView) findViewById(R.id.opponent_username_text);

        userInfoLayout = (TableLayout) findViewById(R.id.userInfoLayout);
        userInfoLayout.setVisibility(View.GONE);
    }

    private void setListeners() {
        enterNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempName = nameTextBox.getText().toString();
                if(!tempName.isEmpty() && tempName.length() <= 10) {
                    userNameText.setText(tempName);
                    userInfoLayout.setVisibility(View.VISIBLE);
                    searchButton.setVisibility(View.VISIBLE);

                    // Hide the Keyboard
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(serverClass!=null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message_type", 3);
                    jsonObject.put("server_ready", true);
                    String message = jsonObject.toString();
                    sendReceive.write(message.getBytes());
                    gameServerReady = true;
                }

                if(gameServerReady) {
                    // Destroy the previous socket threads
                    if(sendReceive!=null) {
                        sendReceive.setStopThreadVariable(true);
                        sendReceive.socket = null;
                        sendReceive = null;
                    }
                    if(serverClass!=null) {
                        serverClass.setStopThreadVariable(true);
                        serverClass.socket = null;
                        serverClass = null;
                    }
                    if(clientClass!=null) {
                        clientClass.setStopThreadVariable(true);
                        clientClass.socket = null;
                        clientClass = null;
                    }
                    if(handler != null) {
                        handler = null;
                    }

                    startGameIntent = new Intent(ConnectActivity.this, GamePlayActivity.class);
                    startGameIntent.putExtra("myName", playerInfo.getName());
                    startGameIntent.putExtra("myColor", playerInfo.getColor());
                    startGameIntent.putExtra("opponentName", opponentInfo.getName());
                    startGameIntent.putExtra("opponentColor", opponentInfo.getColor());
                    startActivity(startGameIntent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Wait for other player to be ready", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startTitleActivity = new Intent(ConnectActivity.this, TitleActivity.class);
                startActivity(startTitleActivity);
                finish();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        searching = false;

        if(startGameIntent == null) {
            // unregisterReceiver(mReceiver);
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
    }

    private void setup() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searching = true;
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.i("Discover Peers", "Discovery Started");
                        Toast.makeText(getApplicationContext(), "Searching for Opponent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Log.i("Discover Peers", "Discovery Failed to Start: " + i);
                        Toast.makeText(getApplicationContext(), "Discovery Failed to Start: " + i, Toast.LENGTH_SHORT).show();
                    }
                });

                // Show the progress bar
                if(progressBar.getVisibility()!=View.VISIBLE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

        });
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
                            searching = false;
                            // Setting opponent's name
                            String opponentName = (String) jsonObject.get("name");
                            opponentNameText.setText(opponentName);

                            // Setting opponent's picture
                            String opponentColor = (String) jsonObject.get("color");
                            if(opponentColor.equals("Dark")) {
                                userImage.setImageResource(R.drawable.light_pawn);
                                opponentImage.setImageResource(R.drawable.dark_pawn);
                                opponentInfo = new PlayerInfo(opponentName, "Dark");
                            } else if(opponentColor.equals("Light")) {
                                userImage.setImageResource(R.drawable.dark_pawn);
                                opponentImage.setImageResource(R.drawable.light_pawn);
                                opponentInfo = new PlayerInfo(opponentName, "Light");
                            }
                            playButton.setVisibility(View.VISIBLE);
                            searchButton.setVisibility(View.INVISIBLE);
                            // Set the progress Bar Visibility to Gone
                            progressBar.setVisibility(View.GONE);
                        } else if(messageType == 3) {
                            gameServerReady = (boolean) jsonObject.get("server_ready");
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return true;
        }
    });

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        private boolean stopThreadVariable = false;

        public void setStopThreadVariable(boolean stopThreadVariable) {
            this.stopThreadVariable = stopThreadVariable;
        }

        @Override
        public void run() {
            try {
                while(!stopThreadVariable) {
                    serverSocket = new ServerSocket(8888);
                    socket = serverSocket.accept();
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    sendOpponentName();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        private boolean stopThreadVariable = false;

        public void setStopThreadVariable(boolean stopThreadVariable) {
            this.stopThreadVariable = stopThreadVariable;
        }

        public SendReceive(Socket skt) {
            socket = skt;
            try {
                if(!stopThreadVariable) {
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (socket!=null && socket.isConnected()) {
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

        private boolean stopThreadVariable = false;

        public void setStopThreadVariable(boolean stopThreadVariable) {
            this.stopThreadVariable = stopThreadVariable;
        }

        public ClientClass(InetAddress hostAddress) {
            this.hostAddress = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                while(!stopThreadVariable && !socket.isConnected()) {
                    socket.connect(new InetSocketAddress(hostAddress, 8888), 10000);
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    sendOpponentName();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendOpponentName() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message_type", 1);
        jsonObject.put("name", userNameText.getText().toString());
        jsonObject.put("color", playerInfo.getColor());
        String message = jsonObject.toString();
        sendReceive.write(message.getBytes());
    }
}
