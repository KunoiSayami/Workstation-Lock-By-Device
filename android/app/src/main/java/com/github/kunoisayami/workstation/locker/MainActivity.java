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

import android.content.Intent;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "log_MainActivity";

	TextView textDeviceName, textDeviceAddress;
	Button buttonConfirm, buttonReset, buttonStartService, buttonStopService, buttonRefresh, buttonTest;

	ArrayList<PairedBluetoothDevice> devices = new ArrayList<>();
	PairedBluetoothDeviceAdapter deviceAdapter;
	RecyclerView devicesView;
	private String bindName, bindAddress;

	private void initView() {
		textDeviceName = findViewById(R.id.textDeviceName);
		textDeviceAddress = findViewById(R.id.textDeviceAddress);
		buttonConfirm = findViewById(R.id.buttonConfirm);
		buttonReset = findViewById(R.id.buttonReset);
		buttonStartService = findViewById(R.id.buttonStartService);
		buttonStopService = findViewById(R.id.buttonStopService);
		buttonRefresh = findViewById(R.id.buttonRefresh);
		devicesView = findViewById(R.id.recyclerDevicesView);

		this.recheckService();
		buttonStartService.setOnClickListener(v -> {
			Log.i(TAG, "initView: OnClick start");
			if (recheckService())
				return;
			startService(new Intent(this, WatchingService.class));
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
		devicesView.setAdapter(new PairedBluetoothDeviceAdapter(Unit.getPairedBluetoothDevices(), o -> {
			PairedBluetoothDevice device = (PairedBluetoothDevice) o;
			bindName = device.getName();
			bindAddress = device.getAddress();
			textDeviceName.setText(device.getName());
			textDeviceAddress.setText(device.getAddress());
			buttonConfirm.setEnabled(true);
		}));
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
		super.onDestroy();
	}
}