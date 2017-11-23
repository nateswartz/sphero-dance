package com.example.nates.myfirstapp

import com.orbotix.ConvenienceRobot
import com.orbotix.command.AbortMacroCommand
import com.orbotix.macro.MacroObject
import com.orbotix.macro.cmd.*

/**
 * Created by nates on 11/22/2017.
 */

class RobotActions(robot: ConvenienceRobot) {

    private var mRobot = robot;
    private var isSpinning = false
    private var isRunningMacro = false

    fun spin() {

        if (mRobot == null)
            return

        setRobotToDefaultState()

        if (!isSpinning) {
            mRobot.setRawMotors(RawMotor.DriveMode.REVERSE.ordinal, 255, RawMotor.DriveMode.FORWARD.ordinal, 255)
            isSpinning = true
        } else {
            isSpinning = false
        }
    }

    fun runMacro() {
        if (mRobot == null)
            return

        setRobotToDefaultState()

        if (!isRunningMacro) {
            isRunningMacro = true
            var macro = MacroObject()

            macro.addCommand(Stabilization(false, 0))
            macro.addCommand(LoopStart(500))
            macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 200, RawMotor.DriveMode.REVERSE, 200, 10))
            macro.addCommand(Delay(10))
            macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.FORWARD, 200, 10))
            macro.addCommand(Delay(10))
            macro.addCommand(LoopEnd())
            macro.addCommand(Stabilization(true, 0))

            //Send the macro to the robot and play
            macro.mode = MacroObject.MacroObjectMode.Normal
            macro.setRobot(mRobot.robot)
            macro.playMacro()
        } else {
            isRunningMacro = false
        }
    }

    fun setRobotToDefaultState() {
        if (mRobot == null)
            return

        mRobot.sendCommand(AbortMacroCommand())
        mRobot.setLed(0.5f, 0.5f, 0.5f)
        mRobot.enableStabilization(true)
        mRobot.setBackLedBrightness(0.0f)
        mRobot.stop()
    }
}