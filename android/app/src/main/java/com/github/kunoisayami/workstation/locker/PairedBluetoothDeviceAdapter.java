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

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class PairedBluetoothDeviceAdapter extends RecyclerView.Adapter<PairedBluetoothDeviceAdapter.ViewType> {

	private ArrayList<PairedBluetoothDevice> items;
	private OnRecyclerViewClickListener listener;

	public PairedBluetoothDeviceAdapter(Set<BluetoothDevice> items, @NonNull OnRecyclerViewClickListener listener) {
		this.items = new ArrayList<>();
		for (BluetoothDevice device: items)
			this.items.add(new PairedBluetoothDevice(device.getName(), device.getAddress()));
		this.listener = listener;
	}


	public PairedBluetoothDeviceAdapter(ArrayList<PairedBluetoothDevice> items, @NonNull OnRecyclerViewClickListener listener) {
		this.items = items;
		this.listener = listener;
	}

	public static class ViewType extends RecyclerView.ViewHolder {

		View rootView;

		ViewType(View v) {
			super(v);
			rootView = v;
		}

		void setProp(PairedBluetoothDevice device) {
			TextView textName, textAddress;

			textName = rootView.findViewById(R.id.textDeviceName);
			textAddress = rootView.findViewById(R.id.textDeviceAddress);

			textName.setText(device.getName());
			textAddress.setText(device.getAddress());
		}
	}

	@NonNull
	@Override
	public ViewType onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);

		v.setOnClickListener(l -> listener.onItemClickListener(v));
		return new ViewType(v);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewType holder, int position) {
		PairedBluetoothDevice device = this.items.get(position);
		holder.setProp(device);
	}

	@Override
	public int getItemCount() {
		return this.items.size();
	}

	public ArrayList<PairedBluetoothDevice> getList() {
		return this.items;
	}
}
