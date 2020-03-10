package com.example.balancebeam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {

    final String TAG = "ConnectThread: ";
    final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice device;
    BluetoothSocket socket;

    public ConnectThread(String MAC) {
        device = adapter.getRemoteDevice(MAC);

        try {
            UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Failed to initialise socket.", e);
        }
    }

    public void run() {

        Log.i(TAG, "ConnectThread thread initialised.");
        adapter.cancelDiscovery();

        try {
            socket.connect();
            Log.i(TAG, "SOCKET CONNECTED.");
            //ConnectedThread connectedThread = new ConnectedThread(socket);
            //connectedThread.start();
        } catch (IOException connectException) {
            Log.e(TAG, "Failed to connect to device.", connectException);
            try {
                socket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Failed to close socket.", closeException);
            }
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to close socket.", e);
        }
    }
}