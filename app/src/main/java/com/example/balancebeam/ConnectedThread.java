package com.example.balancebeam;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {

    final String TAG = "ConnectedThread: ";
    BluetoothSocket socket;
    InputStream inputStream;
    OutputStream outputStream;
    byte[] buffer;

    public ConnectedThread(BluetoothSocket socket) {
        this.socket = socket;

        try {
            inputStream = socket.getInputStream();
        }
        catch(IOException e) {
            Log.e(TAG, "Error creating input stream", e);
        }

        try {
            outputStream = socket.getOutputStream();
        }
        catch(IOException e) {
            Log.e(TAG, "Error creating output stream", e);
        }
    }

    @Override
    public void run() {
        Log.i(TAG, "ConnectedThread thread initialised.");

        buffer = new byte[1024];
        int numBytes;

        while(true) {
            try {
                numBytes = inputStream.read(buffer);
            }
            catch(IOException e) {
                Log.e(TAG, "Input stream error: "+e+"\n\n"+"DISCONNECTED.");
            }
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

    public InputStream getInputStream() {
        return inputStream;
    }
    public OutputStream getOutputStream() {
        return outputStream;
    }
}
