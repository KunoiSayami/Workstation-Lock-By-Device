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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
	private static final String TAG = "log_MainActivity";

	TextView textDeviceName, textDeviceAddress;
	Button buttonConfirm, buttonReset, buttonStartService, buttonStopService, buttonRefresh,
			buttonLock;

	ArrayList<PairedBluetoothDevice> devices;
	PairedBluetoothDeviceAdapter deviceAdapter;
	RecyclerView devicesView;
	private static String bindName, bindAddress, preBindName, preBindAddress;
	private BroadcastReceiver receiverLockFinish;

	private void initView() {
		textDeviceName = findViewById(R.id.textDeviceName);
		textDeviceAddress = findViewById(R.id.textDeviceAddress);
		buttonConfirm = findViewById(R.id.buttonConfirm);
		buttonReset = findViewById(R.id.buttonReset);
		buttonStartService = findViewById(R.id.buttonStartService);
		buttonStopService = findViewById(R.id.buttonStopService);
		buttonRefresh = findViewById(R.id.buttonRefresh);
		devicesView = findViewById(R.id.recyclerDevicesView);
		buttonLock = findViewById(R.id.buttonLock);

		DatabaseHelper helper = new DatabaseHelper(this);
		PairedBluetoothDevice _device = helper.getConfig();

		preBindName = bindName = _device.getName();
		preBindAddress = bindAddress = _device.getAddress();
		Log.d(TAG, "initView: bindName => " + bindName + " bindAddress => " + bindAddress);

		helper.close();

		textDeviceName.setText(bindName);
		textDeviceAddress.setText(bindAddress);

		buttonLock.setOnClickListener( v-> {
			buttonLock.setEnabled(false);
			Thread thread = new Thread(() -> {
				HashMap<String, String> params = new HashMap<>();
				params.put("auth", BuildConfig.accessKey);
				new Connect(params, new Callback() {
					@Override
					public void onSuccess(Object o) {

					}

					@Override
					public void onFailure(Object o, Throwable e) {

					}

					@Override
					public void onFinish(Object o, @Nullable Throwable e) {
						Log.i(TAG, "onFinish: Lock request sent");
						sendBroadcast(new Intent("lock.workstation.finish"));
					}
				}, true).doInBackground();
			});
			thread.start();
		});
		receiverLockFinish = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "onReceive: Release button");
				Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show();
				buttonLock.setEnabled(true);
			}
		};
		IntentFilter filter = new IntentFilter("lock.workstation.finish");
		registerReceiver(receiverLockFinish, filter);

		buttonConfirm.setOnClickListener( v -> {
			DatabaseHelper _help = new DatabaseHelper(this);
			_help.updateConfig(bindName, bindAddress).close();
			preBindName = bindName;
			preBindAddress = bindAddress;
			buttonConfirm.setEnabled(false);
			Toast.makeText(this, "Set address to " + bindAddress, Toast.LENGTH_SHORT).show();
		});

		this.recheckService();
		buttonStartService.setOnClickListener(v -> {
			Log.i(TAG, "initView: OnClick start");
			if (recheckService())
				return;
			Intent intent = new Intent(this, WatchingService.class);
			intent.putExtra("address", bindAddress);
			startService(intent);
			recheckService();
		});
		buttonStopService.setOnClickListener(v -> {
			if (!recheckService())
				return;
			stopService(new Intent(this, WatchingService.class));
			recheckService();
		});

		buttonRefresh.setOnClickListener(v ->
				recheckService());

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
		devicesView.setLayoutManager(layoutManager);
		devicesView.addItemDecoration(new VerticalSpaceItemDecoration(30));
		deviceAdapter = new PairedBluetoothDeviceAdapter(Unit.getPairedBluetoothDevices(), v -> {
			int position = devicesView.getChildAdapterPosition(v);
			PairedBluetoothDevice device = devices.get(position);
			updateCurrentDeviceStatus(device.getName(), device.getAddress());
			buttonConfirm.setEnabled(true);
		});
		devices = deviceAdapter.getList();
		devicesView.setAdapter(deviceAdapter);

		buttonReset.setOnClickListener(v -> {
			updateCurrentDeviceStatus(preBindName, preBindAddress);
			buttonConfirm.setEnabled(false);
		});
	}

	private void updateCurrentDeviceStatus(String name, String address) {
		bindName = name;
		bindAddress = address;
		textDeviceName.setText(bindName);
		textDeviceAddress.setText(bindAddress);
	}

	private boolean recheckService() {
		boolean b = Unit.isServiceRunning(this, WatchingService.class);
		buttonStartService.setEnabled(!b);
		buttonStopService.setEnabled(b);
		return b;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	@Override
	protected void onDestroy() {
		if (Unit.isServiceRunning(this, WatchingService.class))
			stopService(new Intent(this, WatchingService.class));
		unregisterReceiver(receiverLockFinish);
		super.onDestroy();
	}
}