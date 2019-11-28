package com.example.balancebeam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothConnect extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> pairedDevices;

    /*Pairing screen split into two ListViews such that user can see previously
    * paired devices while also seeing discovered devices as they appear.
    * Thus it is necessary to have one ArrayList (to hold all the
    * paired/discovered devices which will then be added to the ListView)
    * and ArrayAdapter (to update ListView with data from ArrayList)
    * for each ListView.*/
    ListView pairedDevicesListView;
    ArrayList<String> pairedDevicesArrayList = new ArrayList<>();
    ArrayAdapter<String> pairedDevicesArrayAdapter;
    ListView discDevicesListView;
    ArrayList<String> discDevicesArrayList = new ArrayList<>();
    ArrayAdapter<String> discDevicesArrayAdapter;

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connect);

        /*Bluetooth API requires ACCESS_COARSE_LOCATION permission for discovery process
        * if sdk < 23 then permissions gotten at install else permissions gotten at run-time*/
        if(Build.VERSION.SDK_INT >= 23) {
            AppPermissions.checkPermission(this);
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        pairedDevicesListView = findViewById(R.id.paired_devices_list);
        pairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.paired_devices_list, pairedDevicesArrayList);
        pairedDevicesListView.setAdapter(pairedDevicesArrayAdapter);
        discDevicesListView = findViewById(R.id.discovered_devices_list);
        discDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.discovered_devices_list, discDevicesArrayList);
        discDevicesListView.setAdapter(discDevicesArrayAdapter);

        initBluetooth();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceAddress = device.getAddress();
                discDevicesArrayList.add(deviceAddress);
                discDevicesArrayAdapter.notifyDataSetChanged(); /*notifyDataSetChanged
                allows ListView to update without scroll being reset to the top each time.*/
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

                pairedDevicesArrayList.add(deviceAddress);
                pairedDevicesArrayAdapter.notifyDataSetChanged();
            }
        }

        bluetoothAdapter.startDiscovery();
    }

    public void noBt() {
        Intent noBtIntent = new Intent(this, NoBluetooth.class);
        startActivity(noBtIntent);
    }
}
