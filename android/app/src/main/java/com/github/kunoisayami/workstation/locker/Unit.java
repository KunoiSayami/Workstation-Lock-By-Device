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
package com.github.kunoisayami.workstation.locker;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;
import java.util.Set;

public class Unit {
	private static final String TAG = "log_Unit";

	public static File filesDir = null;

	@NonNull
	public static List<BluetoothDevice> getConnectBluetoothDevices(@NonNull Context context) {
		final BluetoothManager bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
		List<BluetoothDevice> gattServerConnectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
		for (BluetoothDevice device : gattServerConnectedDevices) {
			Log.d(TAG, "Found connected device: " + device.getAddress() + " " + device.getName());
		}
		return gattServerConnectedDevices;
	}

	@NonNull
	public static Set<BluetoothDevice> getPairedBluetoothDevices() {
		Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice d: pairedDevices) {
				String deviceName = d.getName();
				String macAddress = d.getAddress();
				Log.i("TAB", "paired device: " + deviceName + " at " + macAddress);
			}
		}
		return pairedDevices;
	}

	@NonNull
	public static String getBluetoothNameAndAddress(@NonNull BluetoothDevice device) {
		return device.getName() + " " + device.getAddress();
	}

	public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
