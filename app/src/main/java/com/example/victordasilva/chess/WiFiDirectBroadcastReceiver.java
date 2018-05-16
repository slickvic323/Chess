package com.example.victordasilva.chess;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by victordasilva on 5/16/18.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver{
    private WifiP2pManager manager;
    private Channel channel;
    private ConnectActivity activity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, ConnectActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.i("WiFi", "WiFi is ON");
            } else {
                Log.i("WiFi", "WiFi is OFF");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed! We should probably do something about
            // that.
            if(manager != null) {
                manager.requestPeers(channel, activity.peerListListener);

                // If there is a peer to connect to
                if(activity.deviceArray!= null && activity.deviceArray.length > 0) {
                    final WifiP2pDevice device = activity.deviceArray[0];
                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = device.deviceAddress;
                    Toast.makeText(activity.getApplicationContext(), "MADE IT!", Toast.LENGTH_SHORT).show();
                    manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(activity.getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(int i) {
                            Toast.makeText(activity.getApplicationContext(), "Error in connecting to " + device.deviceName, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed! We should probably do something about
            // that.
            if(manager==null) {
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected()) {
                manager.requestConnectionInfo(channel, activity.connectionInfoListener);
            } else {
                // Device disconnected
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
//            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }
    }

}
