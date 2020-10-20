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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class WatchingService extends Service {

	private static final String TAG = "log_WatchingService";
	private DeviceDisconnectReceiver receiver;

	private static String bindAddress;


	@Override
	public void onCreate() {
		Log.d(TAG, "onStartCommand: Service started");
		startMyOwnForeground();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		this.receiver = new DeviceDisconnectReceiver(device -> {
			Log.i(TAG, "onCreate: " + Unit.getBluetoothNameAndAddress((BluetoothDevice) device));
			if (((BluetoothDevice) device).getAddress().equals(bindAddress)) {
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
							Log.i(TAG, "onFinish: Finished");
						}
					}, true).doInBackground();
				});
				thread.start();
			}
		});
		this.registerReceiver(this.receiver, filter);
	}

	private Timer timer;

	public void startTimer() {
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			public void run() {
			}
		};
		timer.schedule(timerTask, 10000, 10000);
	}

	public void stopTimerTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	// Reference: https://stackoverflow.com/a/52258125
	@RequiresApi(Build.VERSION_CODES.O)
	private void startMyOwnForeground() {
		String NOTIFICATION_CHANNEL_ID = "workstation.locker.watchdog";
		String channelName = "Watchdog Service";
		NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
		chan.setLightColor(Color.BLUE);
		chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		assert manager != null;
		manager.createNotificationChannel(chan);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
		Notification notification = notificationBuilder.setOngoing(true)
				.setContentTitle("Watchdog service is running in background")
				.setPriority(NotificationManager.IMPORTANCE_MIN)
				.setCategory(Notification.CATEGORY_SERVICE)
				.build();
		startForeground(2, notification);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		bindAddress = intent.getStringExtra("address");
		if (bindAddress == null)
			Log.w(TAG, "onStartCommand: Set Address Failure");
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		startTimer();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
		super.onDestroy();
		this.unregisterReceiver(this.receiver);
		stopTimerTask();
	}
}