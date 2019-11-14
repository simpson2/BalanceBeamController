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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothConnect extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private ListView deviceList;
    private ArrayAdapter<String> deviceListArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connect);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        deviceList = findViewById(R.id.device_list);
        deviceListArrayAdapter = new ArrayAdapter<String>(this, R.layout.bluetooth_connect);
        deviceList.setAdapter(deviceListArrayAdapter);

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

                String deviceName = device.getName();
                String deviceAddress = device.getAddress();

                deviceListArrayAdapter.add(deviceName+"\n"+deviceAddress);
                deviceListArrayAdapter.notifyDataSetChanged();
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

    public void onClickSearchDevices(View view) {
        bluetoothAdapter.startDiscovery();
        Toast.makeText(this, "Searching for devices...", Toast.LENGTH_LONG).show();
    }

    public void noBt() {
        Intent noBtIntent = new Intent(this, NoBluetooth.class);
        startActivity(noBtIntent);
    }
}
