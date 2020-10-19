package com.github.kunoisayami.workstation.locker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private final static String DATABASE_NAME = "dev1ces.db";
	private final static String CREATE_OPTION = "CREATE TABLE `option` (`key` TEXT PRIMARY KEY, `value` TEXT)";
	private final static String TABLE_OPTION = "option";

	private final static String DROP_STATEMENT = "DROP TABLE IF EXISTS ";

	private final static String TAG = "log_Database";

	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null , DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_OPTION);
		for (String x: new String[]{"name", "address"}) {
			ContentValues cv = new ContentValues();
			cv.put("key", x);
			cv.put("value", "");
			db.insert("option", null, cv);
		}
	}

	// TODO: backup then drop next time
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_STATEMENT + TABLE_OPTION);
		onCreate(db);
	}

	public PairedBluetoothDevice getConfig() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_OPTION, new String[]{"value"}, "key = ?", new String[]{"name"}, null, null, null);
		cursor.moveToFirst();
		String name = cursor.getString(cursor.getColumnIndexOrThrow("value"));
		cursor.close();
		cursor = db.query(TABLE_OPTION, new String[]{"value"}, "key = ?", new String[]{"address"}, null, null, null);
		cursor.moveToFirst();
		String address = cursor.getString(cursor.getColumnIndexOrThrow("value"));
		cursor.close();
		return new PairedBluetoothDevice(name, address);
	}

	public DatabaseHelper updateConfig(String name, String address) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.update(TABLE_OPTION, generateContentValues("name", name), "key = ?", new String[]{"name"});
		db.update(TABLE_OPTION, generateContentValues("address", address), "key = ?", new String[]{"address"});
		db.close();
		return this;
	}

	public static ContentValues generateContentValues(String keyName, String keyValue) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("key", keyName);
		contentValues.put("value", keyValue);
		return contentValues;
	}

	@Override
	public synchronized void close() {
		super.close();
	}
}
