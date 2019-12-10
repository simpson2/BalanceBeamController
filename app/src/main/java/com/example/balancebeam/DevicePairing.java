package com.example.balancebeam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class DevicePairing extends Thread {

    final String TAG = "DevicePairing: ";
    final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    final BluetoothDevice device;
    final BluetoothSocket socket;

    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public DevicePairing(String MAC) {
        BluetoothSocket temp = null;
        device = adapter.getRemoteDevice(MAC);

        try {
            temp = device.createRfcommSocketToServiceRecord(MY_UUID);
            Log.i(TAG, "Successfully paired to device.");
        }
        catch(IOException e) {
            Log.e(TAG, "Failed to initialise socket.", e);
        }
        socket = temp;
    }

    public void run() {

        Log.i(TAG, "DevicePairing Thread Initialised.");
        adapter.cancelDiscovery();

        try {
            socket.connect();
        }
        catch(IOException connectException) {
            Log.e(TAG, "Failed to connect to device.", connectException);
            try {
                socket.close();
            }
            catch(IOException closeException) {
                Log.e(TAG, "Failed to close socket.", closeException);
            }
            return;
        }
    }

    public void cancel() {
        try {
            socket.close();
        }
        catch(IOException e) {
            Log.e(TAG, "Failed to close socket.", e);
        }
    }
}
