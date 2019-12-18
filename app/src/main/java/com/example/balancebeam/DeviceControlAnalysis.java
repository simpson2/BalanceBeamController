package com.example.balancebeam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class DeviceControlAnalysis extends AppCompatActivity {

    ConnectThread connectThread;
    ConnectedThread connectedThread;
    String MAC;
    TextView textView = findViewById(R.id.connected_to);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control);
        Intent intent = getIntent();
        MAC = intent.getStringExtra("ADDRESS");

        startConnectThread(MAC);
        startConnectedThread(connectThread.getSocket());

        textView.append(MAC);
    }

    public void startConnectThread(String MAC) {
        connectThread = new ConnectThread(MAC);
        connectThread.run();
    }

    public void startConnectedThread(BluetoothSocket bluetoothSocket) {
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.run();
    }
}

