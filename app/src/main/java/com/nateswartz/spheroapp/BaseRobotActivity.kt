package com.nateswartz.spheroapp

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.orbotix.ConvenienceRobot
import com.orbotix.common.RobotChangedStateListener

abstract class BaseRobotActivity : Activity(), RobotServiceListener, BluetoothServiceListener{

    var isRobotServiceBound = false
    var isBluetoothServiceBound = false
    var robotAlreadyConnected = false
    var robot: ConvenienceRobot? = null

    val bluetoothServiceConnection = object : ServiceConnection {
        private var boundBluetoothService: BluetoothControllerService? = null

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e(TAG,"onServiceConnected")
            boundBluetoothService = (service as BluetoothControllerService.BluetoothBinder).service
            isBluetoothServiceBound = true
            boundBluetoothService?.addListener(this@BaseRobotActivity)
            if (boundBluetoothService?.hasActiveBluetooth() == true) {
                handleBluetoothChange(1)
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG,"onServiceDisconnected")
            boundBluetoothService = null
            isBluetoothServiceBound = false
            boundBluetoothService?.removeListener(this@BaseRobotActivity)
        }
    }

    val robotServiceConnection = object : ServiceConnection {
        private var boundService: RobotProviderService? = null

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e(TAG,"onServiceConnected")
            boundService = (service as RobotProviderService.RobotBinder).service
            isRobotServiceBound = true
            boundService?.addListener(this@BaseRobotActivity)
            if (boundService?.hasActiveRobot() == true) {
                robotAlreadyConnected = true
                handleRobotChange(boundService!!.getRobot(), RobotChangedStateListener.RobotChangedStateNotificationType.Online)
            } else {
                val toast = Toast.makeText(this@BaseRobotActivity, "Discovering...",
                        Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 10)
                toast.show()
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG,"onServiceDisconnected")
            boundService = null
            isRobotServiceBound = false
            boundService?.removeListener(this@BaseRobotActivity)
        }
    }

    override fun handleBluetoothChange(type: Int) {
        Log.e(TAG, "Bluetooth Connected")
        val intent = Intent(this@BaseRobotActivity, RobotProviderService::class.java)
        bindService(intent, robotServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun handleRobotChange(robot: ConvenienceRobot, type: RobotChangedStateListener.RobotChangedStateNotificationType) {
        when (type) {
            RobotChangedStateListener.RobotChangedStateNotificationType.Online -> {
                if (!robotAlreadyConnected) {
                    Log.e(TAG, "handleRobotConnected")
                    val toast = Toast.makeText(this@BaseRobotActivity, "Connected!",
                            Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.BOTTOM, 0, 10)
                    toast.show()
                }
                robotAlreadyConnected = false
                isRobotServiceBound = true
                this.robot = robot
                val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                val savedRedValue = sharedPref.getInt(getString(R.string.saved_red_value), -1)
                val savedGreenValue = sharedPref.getInt(getString(R.string.saved_green_value), -1)
                val savedBlueValue = sharedPref.getInt(getString(R.string.saved_blue_value), -1)

                if (savedRedValue != 1) {
                    this.robot?.setLed(savedRedValue.toFloat() / 255, savedGreenValue.toFloat() / 255, savedBlueValue.toFloat() / 255 )
                }

                setupRobotItems()
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Offline -> {
                Log.e(TAG, "handleRobotDisconnected")
                this.robot = null

                disableRobotItems()
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Connecting -> {
                Log.e(TAG, "handleRobotConnecting")
                val toast = Toast.makeText(this@BaseRobotActivity, "Connecting..",
                        Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 10)
                toast.show()
            }
            else -> {
            }
        }
    }

    abstract fun setupRobotItems()
    abstract fun disableRobotItems()

    companion object {
        private const val TAG = "BaseRobotActivity"
    }
}