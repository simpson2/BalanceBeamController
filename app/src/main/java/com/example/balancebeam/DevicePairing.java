package com.example.balancebeam;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class DevicePairing extends AppCompatActivity {

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

    private static final int LOCATION_REQUEST = 1;
    private static final int REQUEST_ENABLE_BT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_pairing);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        pairedDevicesListView = findViewById(R.id.paired_devices_list);
        pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //Register onClick listener for each ListView
                String MAC = parent.getItemAtPosition(position).toString();

                Intent intent = new Intent(DevicePairing.this, DeviceControlAnalysis.class);
                intent.putExtra("ADDRESS", MAC);
                startActivity(intent);
            }
        });
        pairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.paired_devices_list, pairedDevicesArrayList);
        pairedDevicesListView.setAdapter(pairedDevicesArrayAdapter);

        discDevicesListView = findViewById(R.id.discovered_devices_list);
        discDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String MAC = parent.getItemAtPosition(position).toString();

                Intent intent = new Intent(DevicePairing.this, DeviceControlAnalysis.class);
                intent.putExtra("ADDRESS", MAC);
                startActivity(intent);
            }
        });
        discDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.discovered_devices_list, discDevicesArrayList);
        discDevicesListView.setAdapter(discDevicesArrayAdapter);

        /*Bluetooth API requires ACCESS_COARSE_LOCATION permission for discovery process
        * if SDK < 23 then permissions gotten at install else permissions gotten at run-time*/
        if(Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }
        else {
            startBluetooth();
        }
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

    public void startBluetooth() {

        if(bluetoothAdapter == null) {
            noBluetoothOrLocation();
        }
        else if(!bluetoothAdapter.isEnabled()) { //Check if device BT is on. If off, ask for permission to turn on without leaving app
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            pairedDevices = bluetoothAdapter.getBondedDevices();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //If permission refused, inform of incompatibility. If granted, turn BT on
        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == RESULT_CANCELED) {
                noBluetoothOrLocation();
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

        bluetoothAdapter.startDiscovery(); //Android's API function to start discovery - not the same function  as above
    }

    public void checkPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST);
        }
        else {
            startBluetooth();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == LOCATION_REQUEST) {
            if(grantResults.length > 0 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBluetooth();
            }
            else {
                noBluetoothOrLocation();
            }
        }
    }

    public void noBluetoothOrLocation() {
        Intent noBtIntent = new Intent(this, NoBluetoothOrLocation.class);
        startActivity(noBtIntent);
    }
}
