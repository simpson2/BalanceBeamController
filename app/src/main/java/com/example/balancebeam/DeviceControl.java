package com.example.balancebeam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class DeviceControl extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control);

        Intent intent = getIntent();
        String MAC = intent.getStringExtra("ADDRESS");

        DevicePairingThread devicePairingThread = new DevicePairingThread(MAC);
        devicePairingThread.run();
    }


    private class DevicePairingThread extends Thread {

        final String TAG = "DevicePairingThread: ";
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        final BluetoothDevice device;
        final BluetoothSocket socket;

        public DevicePairingThread(String MAC) {
            BluetoothSocket temp = null;
            device = adapter.getRemoteDevice(MAC);

            try {
                UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                temp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Failed to initialise socket.", e);
            }
            socket = temp;
        }

        public void run() {

            Log.i(TAG, "DevicePairingThread Thread Initialised.");
            adapter.cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException connectException) {
                Log.e(TAG, "Failed to connect to device.", connectException);
                try {
                    socket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Failed to close socket.", closeException);
                }
            }
            Toast.makeText(DeviceControl.this, "Successfully connected to device." ,Toast.LENGTH_LONG).show();
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close socket.", e);
            }
        }

    }
}
