package com.example.victordasilva.chess;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    Button searchButton, playButton, enterNameButton;
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

        setupUIPieces();
        setListeners();

        setup();
    }

    private void setupUIPieces() {
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setVisibility(View.GONE);

        playButton = (Button) findViewById(R.id.play_button);
        enterNameButton = (Button) findViewById(R.id.set_name_button);

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
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOpponentName();
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
        mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Successfully disconnected
            }

            @Override
            public void onFailure(int i) {
                // Unsuccessfully disconnected
            }
        });
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    private void setup() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.i("Discover Peers", "Discovery Started");
                        Toast.makeText(getApplicationContext(), "Discovery Started", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Log.i("Discover Peers", "Discovery Failed to Start: " + i);
                        Toast.makeText(getApplicationContext(), "Discovery Failed to Start: " + i, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
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
                            // Setting opponent's name
                            String opponentName = (String) jsonObject.get("name");
                            opponentNameText.setText(opponentName);

                            // Setting opponent's picture
                            String opponentColor = (String) jsonObject.get("color");
                            if(opponentColor.equals("Dark")) {
                                userImage.setImageResource(R.drawable.light_pawn);
                                opponentImage.setImageResource(R.drawable.dark_pawn);
                            } else if(opponentColor.equals("Light")) {
                                userImage.setImageResource(R.drawable.dark_pawn);
                                opponentImage.setImageResource(R.drawable.light_pawn);
                            }
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

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                sendOpponentName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread {
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
            try {
                socket.connect(new InetSocketAddress(hostAddress, 8888), 10000);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                sendOpponentName();
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
