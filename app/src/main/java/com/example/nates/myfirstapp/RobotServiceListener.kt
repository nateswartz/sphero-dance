package com.example.nates.myfirstapp

import com.orbotix.ConvenienceRobot

/**
 * Created by nates on 11/29/2017.
 */
interface RobotServiceListener {
    fun handleRobotConnected(robot: ConvenienceRobot)
    fun handleRobotDisconnected()
}