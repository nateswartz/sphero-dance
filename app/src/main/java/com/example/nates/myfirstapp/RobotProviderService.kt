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

    private val mDiscoveryAgent = DualStackDiscoveryAgent()
    var robot: ConvenienceRobot? = null
    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder() {
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
        if (robot != null) {
            robot!!.disconnect()
            robot = null
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e("Service", "onBind")
        if (!mDiscoveryAgent.isDiscovering) {
            try {
                mDiscoveryAgent.startDiscovery(applicationContext)
            } catch (e: DiscoveryException) {
                Log.e("Sphero", "DiscoveryException: " + e.message)
            }
        }
        return mBinder
    }

    override fun handleRobotChangedState(robot: Robot, type: RobotChangedStateListener.RobotChangedStateNotificationType) {
        Log.e("Service", "handleRobotChangedState")
        when (type) {
            RobotChangedStateListener.RobotChangedStateNotificationType.Online -> {
                //Save the robot as a ConvenienceRobot for additional utility methods
                this.robot = ConvenienceRobot(robot)
                // Notify listeners
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Offline -> {
                // Notify listeners
            }
        }
    }
}