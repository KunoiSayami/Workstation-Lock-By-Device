/*
 ** Copyright (C) 2020 KunoiSayami
 **
 ** This file is part of Workstation-Lock-By-Device and is released under
 ** the AGPL v3 License: https://www.gnu.org/licenses/agpl-3.0.txt
 **
 ** This program is free software: you can redistribute it and/or modify
 ** it under the terms of the GNU Affero General Public License as published by
 ** the Free Software Foundation, either version 3 of the License, or
 ** any later version.
 **
 ** This program is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 ** GNU Affero General Public License for more details.
 **
 ** You should have received a copy of the GNU Affero General Public License
 ** along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.kunoisayami.workstaion.locker;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "log_MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		this.registerReceiver(broadcastReceiver, filter);
		/*Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice d: pairedDevices) {
				String deviceName = d.getName();
				String macAddress = d.getAddress();
				Log.i("TAB", "paired device: " + deviceName + " at " + macAddress);

			}
		}*/
		final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		List<BluetoothDevice> gattServerConnectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
		for (BluetoothDevice device : gattServerConnectedDevices) {
			Log.d(TAG, "Found connected device: " + device.getAddress() + " " + device.getName());
		}
	}
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		BluetoothDevice device;
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
				Toast.makeText(getApplicationContext(), "Device is now connected",    Toast.LENGTH_SHORT).show();
				Log.i(TAG, "Device "+ getBluetoothNameAndAddress(device) +" is now connected");
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				Toast.makeText(getApplicationContext(), "Device is disconnected",       Toast.LENGTH_SHORT).show();
				Log.i(TAG, "Device "+ getBluetoothNameAndAddress(device) +" is now disconnected");
			}
		}
	};

	public static String getBluetoothNameAndAddress(BluetoothDevice device) {
		return device.getName() + " " + device.getAddress();
	}
}