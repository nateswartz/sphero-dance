package com.nateswartz.spheroapp

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log


class BluetoothControllerService : Service() {
    private var listeners: MutableList<BluetoothServiceListener> = mutableListOf()
    private val binder = BluetoothBinder()
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothChanged = false
    private var hasActiveBluetooth = false

    inner class BluetoothBinder : Binder() {
        internal val service: BluetoothControllerService
            get() = this@BluetoothControllerService
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> Log.e("Activity","Bluetooth off");
                    BluetoothAdapter.STATE_TURNING_OFF -> Log.e("Activity","Turning Bluetooth off...")
                    BluetoothAdapter.STATE_ON -> {
                        Log.e("Activity","Bluetooth on")
                        bluetoothChanged = true
                        hasActiveBluetooth = true
                        for (listener in listeners) {
                            listener.handleBluetoothChange(state)
                        }
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> Log.e("Activity","Turning Bluetooth on...")
                }
            }
        }
    }

    override fun onCreate() {
        Log.d("Service", "onCreate")
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        Log.d("Service", "onDestroy")
        //If the DiscoveryAgent is in discovery mode, stop it.
        if (bluetoothAdapter.isEnabled && bluetoothChanged) {
            Log.e("Activity", "disable Bluetooth")
            bluetoothAdapter.disable()
        }
        unregisterReceiver(receiver)
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("Service", "onBind")
        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        } else {
            hasActiveBluetooth = true
        }
        return binder
    }

    fun hasActiveBluetooth() : Boolean {
        return hasActiveBluetooth
    }

    fun addListener(listener: BluetoothServiceListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: BluetoothServiceListener) {
        listeners.remove(listener)
    }
}