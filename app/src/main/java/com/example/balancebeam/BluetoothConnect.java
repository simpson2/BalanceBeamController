package com.example.balancebeam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothConnect extends AppCompatActivity {

    //TODO DELETE AFTER TESTING
    public static final String TAG = "BluetoothConnect";

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> pairedDevices;
    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connect);

        TextView textView = new TextView(this);
        textView.append("Search for bluetooth devices");
        listView.addHeaderView(textView);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        listView = findViewById(R.id.device_list);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.device_list, arrayList);

        initBluetooth();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceAddress = device.getAddress();
                arrayList.add(deviceAddress);
                listView.setAdapter(arrayAdapter);
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
        else {
            pairedDevices = bluetoothAdapter.getBondedDevices();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_CANCELED) {
                noBt();
            }
            else {
                pairedDevices = bluetoothAdapter.getBondedDevices();
            }
        }
    }

    public void startDiscovery(View view) {
        ViewGroup parentView = (ViewGroup) view.getParent();
        parentView.removeView(view);

        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                String deviceAddress = device.getAddress();

                arrayList.add(deviceAddress);
                listView.setAdapter(arrayAdapter);
            }
        }

        //TODO DELETE AFTER TESTING
        for (int i = 0; i < 10; i++) {
            arrayList.add("Test data "+i);
            listView.setAdapter(arrayAdapter);
        }

        //TODO MODIFY AFTER TESTING
        boolean discoverySuccess = bluetoothAdapter.startDiscovery();
        Log.d(TAG, "startDiscoverySTATUS: "+discoverySuccess);
        Toast.makeText(this, "Searching for devices...", Toast.LENGTH_LONG).show();
    }

    public void noBt() {
        Intent noBtIntent = new Intent(this, NoBluetooth.class);
        startActivity(noBtIntent);
    }
}
