package com.example.nouned;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.nouned.R.id.container;
import static com.example.nouned.R.id.coordinator;
import static com.example.nouned.R.id.root;

public class WifiBroadcastReciever extends BroadcastReceiver {
    private WifiP2pManager.PeerListListener mPeerlistener;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Fragment currentFragment;
    private FragmentManager FragManager;




    public WifiBroadcastReciever(WifiP2pManager.Channel channel, WifiP2pManager manager, Fragment currentFrag,FragmentManager Fmanager){
        this.mChannel=channel;
        this.mManager = manager;
        this.currentFragment = currentFrag;
        this.FragManager = Fmanager;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            //Broadcast when Wi-Fi P2P is enabled or disabled on the device.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

                //Wifi is enabled
                MainActivity.wifiIsOn = true;
            } else {

                //wifi isnt enabled
                MainActivity.wifiIsOn = false;
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            //Broadcast when a device's details have changed, such as the device's name.
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                   mManager.requestConnectionInfo(mChannel,MainActivity.connectionInfoListener);
                //Group info listener to listen  to the group state and such
                mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        Collection<WifiP2pDevice> devices =  group.getClientList();
                        if(devices.size()>0){


                           MainActivity.MyFragment = new connected();
                            FragManager.beginTransaction().replace(R.id.container,MainActivity.MyFragment).commit();
                           // Snackbar.make(findViewById(coordinator),"Device connected",Snackbar.LENGTH_SHORT;

                        }else{
                            //IsConnected =false;
                        }

                    }
                });
                // we are connected with the other device, request connection
                // info to find group owner IP
             //   MainActivity.MyFragment = new Connect();
              // FragManager.beginTransaction().replace(R.id.container,MainActivity.MyFragment).commit();



            } else {
                // It's a disconnect

            }

            //Broadcast when the state of the device's Wi-Fi connection changes.
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                //Broadcast when you call discoverPeers(). You usually want to call requestPeers() to get an updated list of peers if you handle this intent in your application.
                if (mManager != null & MainActivity.isGroupOwner==false) {
                    mManager.requestPeers(mChannel,MainActivity.myPeerListListener);
                }


        }

    }



}
