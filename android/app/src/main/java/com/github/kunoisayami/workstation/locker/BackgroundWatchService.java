package com.github.kunoisayami.workstation.locker;

import android.app.IntentService;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class BackgroundWatchService extends IntentService {

	private static final String TAG = "log_BackgroundWatchService";
	private static final String HANDLER_THREAD_NAME = "BackgroundWatchServiceThread";

	private final static int SEND_COMMAND = 1;

	private boolean is_run = true;
	private SleepThread sleepThread;
	private Looper serviceLooper;

	private Handler handler;

	public static String address;

	static class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(@NonNull Message msg) {
			if (msg.what == SEND_COMMAND) {
				BluetoothDevice device = (BluetoothDevice) msg.obj;
				if (device.getAddress().equals(address)) {

				}
			}
		}
	}

	DeviceDisconnectReceiver receiver;

	static class SleepThread implements Runnable {

		private boolean flag = true;

		public void stop() {
			this.flag = false;
		}

		@Override
		public void run() {
			try {
				while (flag) {
					Thread.sleep(10000);
				}
			} catch (InterruptedException ignored) {}
		}
	}

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public BackgroundWatchService(String name) {
		super(name);
	}

	public BackgroundWatchService() {
		super("BackgroundWatchService");
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate: Create service");
		super.onCreate();
		HandlerThread thread = new HandlerThread(HANDLER_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		serviceLooper = thread.getLooper();
		handler = new ServiceHandler(serviceLooper);
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		this.receiver = new DeviceDisconnectReceiver(device -> {
			Log.i(TAG, "handleMessage: " + Unit.getBluetoothNameAndAddress((BluetoothDevice) device));
		});
		this.registerReceiver(this.receiver, filter);
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		Log.d(TAG, "onHandleIntent: calling");
		sleepThread = new SleepThread();
		sleepThread.run();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.receiver);
		sleepThread.stop();
	}
}
