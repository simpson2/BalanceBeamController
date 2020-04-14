package com.example.balancebeam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Handler;

public class BluetoothControlService {
    //Debugging
    private static final String TAG = "BluetoothControlService";

    //Default UUID for this app and the expected device
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Member variables
    private final BluetoothAdapter mAdapter;
    private String mMAC;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    //Constants indicating current connection state
    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    //Constructor to prepare BluetoothConnect session
    public BluetoothControlService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        mState = STATE_NONE;
    }

    public synchronized  void reconnect() {
        Log.i(TAG, "Connection lost. Reconnecting...");

        if(mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mConnectThread == null) {
            connect(mMAC);
        }
    }

    public synchronized void connect(String MAC) {
        mMAC = MAC;
        BluetoothDevice device = mAdapter.getRemoteDevice(MAC);

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();;
}

    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e) {
                Log.e(TAG, "Failed to initialise socket.", e);
            }
            mmSocket = tmp;
            mState = STATE_CONNECTING;
        }

        public void run() {
            Log.i(TAG, "ConnectThread thread init.");
            mAdapter.cancelDiscovery();

            try{
                mmSocket.connect();
                Log.i(TAG, "SOCKET CONNECTED");
            }
            catch(IOException e) {
                Log.e(TAG, "Failed to connect to device.", e);
                try {
                    mmSocket.close();
                }
                catch(IOException ec) {
                    Log.e(TAG, "Failed to close socket.", e);
                }
            }
        }

        public class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;

            public ConnectedThread(BluetoothSocket socket) {
                mmSocket = socket;
                InputStream tmpIn = null;
                OutputStream tmpOut = null;

                try {
                    tmpIn = mmSocket.getInputStream();
                    tmpOut = mmSocket.getOutputStream();
                }
                catch(IOException e) {
                    Log.e(TAG, "Failed to connect comm streams.",e);
                }
                mmInStream = tmpIn;
                mmOutStream = tmpOut;
                mState = STATE_CONNECTED;
            }

            public void run() {
                Log.i(TAG, "ConnectedThread thread init.");

                byte[] buffer = new byte[1024];
                int bytes;

                while(mState == STATE_CONNECTED) {
                    try {
                        bytes = mmInStream.read(buffer);
                    }
                    catch(IOException e) {
                        Log.e(TAG, "SOCKET DISCONNECTED");
                        reconnect();
                        break;
                    }
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            }
            catch(IOException e) {
                Log.e(TAG, "Failed to close socket.",e);
            }
        }
    }
}
