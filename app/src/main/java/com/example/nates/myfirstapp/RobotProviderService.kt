package com.example.nates.myfirstapp

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.orbotix.ConvenienceRobot
import com.orbotix.DualStackDiscoveryAgent
import com.orbotix.common.DiscoveryException
import com.orbotix.common.Robot
import com.orbotix.common.RobotChangedStateListener


class RobotProviderService : Service(), RobotChangedStateListener {
    private var mListeners: MutableList<RobotServiceListener> = mutableListOf()
    private val mDiscoveryAgent = DualStackDiscoveryAgent()
    private var robot: ConvenienceRobot? = null
    private val mBinder = RobotBinder()

    inner class RobotBinder : Binder() {
        internal val service: RobotProviderService
            get() = this@RobotProviderService
    }

    override fun onCreate() {
        Log.e("Service", "onCreate")
        mDiscoveryAgent.addRobotStateListener(this)
    }

    override fun onDestroy() {
        Log.e("Service", "onDestroy")
        //If the DiscoveryAgent is in discovery mode, stop it.
        if (mDiscoveryAgent.isDiscovering) {
            mDiscoveryAgent.stopDiscovery()
        }

        //If a robot is connected to the device, disconnect it
        robot?.disconnect();
        robot = null;
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e("Service", "onBind")
        if (!mDiscoveryAgent.isDiscovering) {
            try {
                mDiscoveryAgent.startDiscovery(applicationContext)
            } catch (e: DiscoveryException) {
                Log.e("Service", "DiscoveryException: " + e.message)
            }
        }
        return mBinder
    }

    override fun handleRobotChangedState(robot: Robot, type: RobotChangedStateListener.RobotChangedStateNotificationType) {
        Log.e("Service", "handleRobotChangedState " + type)
        when (type) {
            RobotChangedStateListener.RobotChangedStateNotificationType.Online -> {
                Log.e("Service", "handleRobotChangedState - Online")
                this.robot = ConvenienceRobot(robot)
                for (listener in mListeners) {
                    listener.handleRobotConnected(this.robot!!)
                }
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Offline -> {
                Log.e("Service", "handleRobotChangedState - Offline")
                for (listener in mListeners) {
                    listener.handleRobotDisconnected()
                }
            }
        }
    }

    fun addListener(listener: RobotServiceListener) {
        mListeners.add(listener)
    }

    fun removeListener(listener: RobotServiceListener) {
        mListeners.remove(listener)
    }
}