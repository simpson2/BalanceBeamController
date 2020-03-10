package com.example.balancebeam;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DeviceControlAnalysis extends AppCompatActivity {

    //Member variables
    private String MAC;
    private BluetoothControlService mBCService = new BluetoothControlService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(receiver, filter);

        Intent intent = getIntent();
        MAC = intent.getStringExtra("ADDRESS");

        mBCService.connect(MAC);

        TextView textView = findViewById(R.id.connected_to);
        textView.append(MAC);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            TextView textView = findViewById(R.id.connected_to);

            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                textView.setBackgroundColor(Color.argb(255, 0, 87, 75));
            }
            else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                textView.setBackgroundColor(Color.argb(255, 255, 0, 0));
            }
        }
    };
}

