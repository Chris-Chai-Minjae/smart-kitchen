package com.smartkitchen.assistant.ui.adapter

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smartkitchen.assistant.R

/**
 * 블루투스 장치 목록 어댑터
 * 발견된 블루투스 장치 목록을 표시합니다.
 */
class BluetoothDeviceAdapter(
    private val onDeviceClick: (BluetoothDevice) -> Unit
) : ListAdapter<BluetoothDevice, BluetoothDeviceAdapter.DeviceViewHolder>(DeviceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bluetooth_device, parent, false)
        return DeviceViewHolder(view, onDeviceClick)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DeviceViewHolder(
        itemView: View,
        private val onDeviceClick: (BluetoothDevice) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val textViewDeviceName: TextView = itemView.findViewById(R.id.textViewDeviceName)
        private val textViewDeviceAddress: TextView = itemView.findViewById(R.id.textViewDeviceAddress)
        
        fun bind(device: BluetoothDevice) {
            textViewDeviceName.text = device.name ?: "알 수 없는 장치"
            textViewDeviceAddress.text = device.address
            
            itemView.setOnClickListener {
                onDeviceClick(device)
            }
        }
    }

    class DeviceDiffCallback : DiffUtil.ItemCallback<BluetoothDevice>() {
        override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
            return oldItem.address == newItem.address
        }

        override fun areContentsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
            return oldItem.name == newItem.name && oldItem.address == newItem.address
        }
    }
}
