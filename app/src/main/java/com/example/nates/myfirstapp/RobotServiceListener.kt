package com.example.nates.myfirstapp

import com.orbotix.ConvenienceRobot
import com.orbotix.common.RobotChangedStateListener

/**
 * Created by nates on 11/29/2017.
 */
interface RobotServiceListener {
    fun handleRobotChange(robot: ConvenienceRobot, type: RobotChangedStateListener.RobotChangedStateNotificationType)
}