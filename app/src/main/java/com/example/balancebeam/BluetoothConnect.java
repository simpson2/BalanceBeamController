package com.example.balancebeam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothConnect extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ListView newDevicesListView = findViewById(R.id.device_list);
    ArrayAdapter<String> newDevicesArrayAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connect);

        newDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.bluetooth_connect_device_list);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        initBluetooth();
    }

    final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceAddress = device.getAddress();
                newDevicesArrayAdapter.add(deviceAddress);
                newDevicesListView.setAdapter(newDevicesArrayAdapter);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    public void initBluetooth() {

        if(bluetoothAdapter == null) {
            noBt();
        }
        else if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_CANCELED) {
                noBt();
            }
        }
    }

    public void onClickStartPairing(View view) {
        ViewGroup parentView = (ViewGroup) view.getParent();
        parentView.removeView(view);

        bluetoothAdapter.startDiscovery();
        Toast.makeText(this, "Searching for devices...", Toast.LENGTH_LONG).show();
    }

    public void noBt() {
        Intent noBtIntent = new Intent(this, NoBluetooth.class);
        startActivity(noBtIntent);
    }
}
