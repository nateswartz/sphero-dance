package com.nateswartz.spheroapp

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
    private var listeners: MutableList<RobotServiceListener> = mutableListOf()
    private val discoveryAgent = DualStackDiscoveryAgent()
    private var robot: ConvenienceRobot? = null
    private val binder = RobotBinder()

    inner class RobotBinder : Binder() {
        internal val service: RobotProviderService
            get() = this@RobotProviderService
    }

    override fun onCreate() {
        Log.d("Service", "onCreate")
        discoveryAgent.addRobotStateListener(this)
    }

    override fun onDestroy() {
        Log.d("Service", "onDestroy")
        //If the DiscoveryAgent is in discovery mode, stop it.
        if (discoveryAgent.isDiscovering) {
            discoveryAgent.stopDiscovery()
        }

        //If a robot is connected to the device, disconnect it
        robot?.disconnect()
        robot = null
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("Service", "onBind")
        if (!discoveryAgent.isDiscovering) {
            try {
                Log.e("Service", "Discovering...")
                discoveryAgent.startDiscovery(applicationContext)
            } catch (e: DiscoveryException) {
                Log.e("Service", "DiscoveryException: " + e.message)
            }
        }
        return binder
    }

    override fun handleRobotChangedState(robot: Robot, type: RobotChangedStateListener.RobotChangedStateNotificationType) {
        Log.d("Service", "handleRobotChangedState $type")
        when (type)
        {
            RobotChangedStateListener.RobotChangedStateNotificationType.Online -> this.robot = ConvenienceRobot(robot)
        }
        for (listener in listeners) {
            listener.handleRobotChange(ConvenienceRobot(robot), type)
        }
    }

    fun hasActiveRobot() : Boolean {
        return robot?.isConnected == true
    }

    fun getRobot() : ConvenienceRobot {
        return robot!!
    }

    fun addListener(listener: RobotServiceListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: RobotServiceListener) {
        listeners.remove(listener)
    }
}