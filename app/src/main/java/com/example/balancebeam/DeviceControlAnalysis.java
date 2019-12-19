package com.example.balancebeam;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DeviceControlAnalysis extends AppCompatActivity {

    ConnectThread connectThread;
    ConnectedThread connectedThread;
    String MAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control);
        Intent intent = getIntent();
        MAC = intent.getStringExtra("ADDRESS");

        startConnectThread(MAC);
        startConnectedThread(connectThread.getSocket());

        TextView textView = findViewById(R.id.connected_to);
        textView.append(MAC);
    }

    public void startConnectThread(String MAC) {
        connectThread = new ConnectThread(MAC);
        connectThread.start();
    }

    public void startConnectedThread(BluetoothSocket bluetoothSocket) {
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();
    }
}

