package com.example.balancebeam;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BluetoothConnect extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 1;
    Intent noBtIntent = new Intent(this, NoBluetooth.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connect);
        InitBluetooth();
    }

    public void InitBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        if(bluetoothAdapter == null) {
            startActivity(noBtIntent);
        }
        else if(!bluetoothAdapter.isEnabled()) {
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 0) {
            startActivity(noBtIntent);
        }
    }
}
