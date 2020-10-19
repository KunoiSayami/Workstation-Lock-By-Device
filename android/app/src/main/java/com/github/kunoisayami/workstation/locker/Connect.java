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

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

class ConnectException extends Exception {
	int status;
	ConnectException(int _status) {
		status = _status;
	}

	String getInfo() {
		return "Server returned: " + status;
	}
}

class ServerException extends ConnectException {
	ServerException(int _status) {
		super(_status);
	}
}


public class Connect extends AsyncTask<URL, Integer, Long> {
	private static final String TAG = "log_Connect";

	private String response = "";
	private String method;
	private int responseCode;


	private Callback listener;

	private HashMap<String, String> postParams;


	Connect(HashMap<String, String> postParams,
			Callback listener,
			boolean is_post) {
		this.postParams = postParams;
		this.listener = listener;
		this.method = is_post? "POST" : "GET";
	}


	private
	void doConnect() throws IOException, ConnectException {
		logRequest();
		StringBuilder stringBuilder = new StringBuilder();
		URL url = new URL(BuildConfig.serverAddress);
		HttpsURLConnection client = null;
		try {
			client = (HttpsURLConnection) url.openConnection();
			client.setRequestMethod(method);

			client.setRequestProperty("Accept-Charset", "utf8");
			client.setRequestProperty("Content-Type", "application/json");
			client.setRequestProperty("User-Agent", BuildConfig.userAgent);

			client.setDoInput(true);

			if (method.equals("POST")) {
				client.setDoOutput(true);
				String strParams = new JSONObject(postParams).toString();
				OutputStream os = client.getOutputStream();
				BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(os, StandardCharsets.UTF_8)
				);
				bufferedWriter.write(strParams);

				bufferedWriter.flush();
				bufferedWriter.close();
				os.close();
			}

			responseCode = client.getResponseCode();
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				String line;
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(client.getInputStream())
				);
				while ((line = bufferedReader.readLine()) != null){
					//Log.d(TAG, "postData: line => " + line);
					stringBuilder.append(line);
					//response += line;
				}
				response = stringBuilder.toString();
				//response = response.substring(1, response.length() - 1);
				bufferedReader.close();
			}
			else {
				response = "{}";
				throw new ServerException(responseCode);
			}
		}
		catch (MalformedURLException e){
			e.printStackTrace();
		}
		finally {
			if (client != null)
				client.disconnect();
			//Log.d(TAG, "postData: Finish");
		}
		Log.d(TAG, "postData: Finish Response => " + response);
	}

	@Override
	protected Long doInBackground(URL... params) {
		try {
			doConnect();
		} catch (IOException | ConnectException e) {
			e.printStackTrace();
			if (listener != null){
				listener.onFailure(this, e);
				listener.onFinish(this, e);
			}
		}

		final byte[] result = response.getBytes();
		return (long) result.length;
	}

	/**
	 *	Method that will execute on success
	 */
	protected void onPostExecute(Long reserved) {
		super.onPostExecute(reserved);
		if (listener != null) {
			listener.onSuccess(response);
			listener.onFinish(this, null);
		}
	}

	/**
	 * @return http response code which is returned by server
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * @return string that network raw response
	 */
	public String getResponse() {
		return response;
	}

	public void logRequest() {
		Log.v(TAG, "logRequest:  Method => " + method);
	}
}
