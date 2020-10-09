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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import static com.github.kunoisayami.workstaion.locker.Unit.getBluetoothNameAndAddress;

class DeviceDisconnectReceiver extends BroadcastReceiver {
	private static final String TAG = "log_DisconnectReceiver";
	BluetoothDevice device;

	@Override
	public void onReceive(Context context, @NonNull Intent intent) {
		String action = intent.getAction();
		device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		if (device == null) {
			Log.w(TAG, "onReceive: Got null device");
			return ;
		}
		if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			Log.i(TAG, "Device "+ getBluetoothNameAndAddress(device) +" is now disconnected");
		}

	}

}
