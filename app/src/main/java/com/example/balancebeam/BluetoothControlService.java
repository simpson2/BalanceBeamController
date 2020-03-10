package com.example.balancebeam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Handler;

public class BluetoothControlService {
    //Debugging
    private static final String TAG = "BluetoothControlService";

    //Default UUID for this app and the expected device
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Member variables
    private final BluetoothAdapter mAdapter;
    //private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    //Constants indicating current connection state
    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    //Constructor to prepare BluetoothConnect session
    public BluetoothControlService() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //mHandler = handler;
        mState = STATE_NONE;
    }

    public synchronized void connect(String MAC) {
        BluetoothDevice device = mAdapter.getRemoteDevice(MAC);

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();;
}

    private class ConnectThread extends Thread {
        private final BluetoothDevice mDevice;
        private final BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice device) {
            mDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e) {
                Log.e(TAG, "Failed to initialise socket.", e);
            }
            mSocket = tmp;
            mState = STATE_CONNECTING;
        }

        public void run() {
            Log.i(TAG, "ConnectThread thread init.");
            mAdapter.cancelDiscovery();

            try{
                mSocket.connect();
                Log.i(TAG, "SOCKET CONNECTED");
            }
            catch(IOException e) {
                Log.e(TAG, "Failed to connect to device.", e);
                try {
                    mSocket.close();
                }
                catch(IOException ec) {
                    Log.e(TAG, "Failed to close socket.", e);
                }
            }
        }

        public void cancel() {
            try {
                mSocket.close();
            }
            catch(IOException e) {
                Log.e(TAG, "Failed to close socket.",e);
            }
        }
    }
}
