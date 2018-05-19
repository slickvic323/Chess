package com.example.victordasilva.chess;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;


/**
 * Created by victordasilva on 5/16/18.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver{
    private WifiP2pManager manager;
    private Channel channel;
    private ConnectActivity activity;
    private boolean isConnected;
    private int numFails;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, ConnectActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        isConnected = false;
        numFails = 0;
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
                if(activity.deviceArray!= null && activity.deviceArray.length > 0 && !isConnected) {
                    for(WifiP2pDevice device : activity.deviceArray) {
                        final WifiP2pDevice currentDevice = device;
                        String deviceType = currentDevice.primaryDeviceType;
                        // If device is a phone
                        if(deviceType.substring(0, 2).equals("10")) {
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = currentDevice.deviceAddress;
                            manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    Log.i("DEVICE", currentDevice.primaryDeviceType);
                                    Toast.makeText(activity.getApplicationContext(), "Connected to " + currentDevice.deviceName, Toast.LENGTH_SHORT).show();
                                    numFails = 0;
                                }
                                @Override
                                public void onFailure(int i) {
                                    numFails++;
                                    if(numFails >= 3) {
                                        Toast.makeText(activity.getApplicationContext(), "Error in connecting to " + currentDevice.deviceName, Toast.LENGTH_SHORT).show();
                                        numFails=0;
                                    }
                                }
                            });
                            break;
                        }

                    }

                } else if(activity.deviceArray!=null && activity.deviceArray.length == 0) {
                    // Disconnected
                    Toast.makeText(activity.getApplicationContext(), "Device disconnected from network", Toast.LENGTH_SHORT).show();
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
                isConnected = true;
            } else {
                // Device disconnected
                isConnected = false;
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
//            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }
    }

}
