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
    private var mListeners: MutableList<BluetoothServiceListener> = mutableListOf()
    private val mBinder = BluetoothBinder()
    private val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var mBluetoothChanged = false
    private var mHasActiveBluetooth = false

    inner class BluetoothBinder : Binder() {
        internal val service: BluetoothControllerService
            get() = this@BluetoothControllerService
    }

    private val mReceiver = object : BroadcastReceiver() {
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
                        mBluetoothChanged = true
                        mHasActiveBluetooth = true
                        for (listener in mListeners) {
                            listener.handleBluetoothChange(state)
                        }
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> Log.e("Activity","Turning Bluetooth on...")
                }
            }
        }
    }

    override fun onCreate() {
        Log.e("Service", "onCreate")
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        Log.e("Service", "onDestroy")
        //If the DiscoveryAgent is in discovery mode, stop it.
        if (mBluetoothAdapter.isEnabled && mBluetoothChanged) {
            Log.e("Activity", "disable Bluetooth")
            mBluetoothAdapter.disable()
        }
        unregisterReceiver(mReceiver)
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e("Service", "onBind")
        if (!mBluetoothAdapter.isEnabled) {
            mBluetoothAdapter.enable()
        } else {
            mHasActiveBluetooth = true
        }
        return mBinder
    }

    fun hasActiveBluetooth() : Boolean {
        return mHasActiveBluetooth;
    }

    fun addListener(listener: BluetoothServiceListener) {
        mListeners.add(listener)
    }

    fun removeListener(listener: BluetoothServiceListener) {
        mListeners.remove(listener)
    }
}