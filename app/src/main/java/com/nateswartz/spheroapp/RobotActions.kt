package com.nateswartz.spheroapp

import android.app.Activity
import android.content.Context
import android.provider.Settings.Global.getString
import com.orbotix.ConvenienceRobot
import com.orbotix.command.AbortMacroCommand
import com.orbotix.macro.MacroObject
import com.orbotix.macro.cmd.*

/**
 * Created by nates on 11/22/2017.
 */

class RobotActions {

    fun spin(): MacroObject {
        val macro = MacroObject()

        macro.addCommand(LoopStart(500))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.FORWARD, 255, 10))
        macro.addCommand(Delay(10))

        return macro
    }

    fun shake(): MacroObject {
        val macro = MacroObject()

        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(LoopStart(500))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 200, RawMotor.DriveMode.REVERSE, 200, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.FORWARD, 200, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stabilization(true, 0))

        return macro
    }

    fun changeColors(): MacroObject {
        val macro = MacroObject()

        macro.addCommand(LoopStart(15))
        macro.addCommand(Fade(0, 255, 255, 100))
        macro.addCommand(Delay(50))
        macro.addCommand(Fade(255, 0, 255, 100))
        macro.addCommand(Delay(50))
        macro.addCommand(Fade(255, 255, 0, 100))
        macro.addCommand(Delay(50))
        macro.addCommand(LoopEnd())

        return macro
    }

    fun figureEight(): MacroObject {
        val macro = MacroObject()

        macro.addCommand(Roll(1f, 0, 500))
        macro.addCommand(LoopStart(10))
        macro.addCommand(RotateOverTime(360, 500))
        macro.addCommand(Delay(500))
        macro.addCommand(RotateOverTime(-360, 500))
        macro.addCommand(Delay(500))
        macro.addCommand(LoopEnd())
        macro.addCommand(Roll(1f, 0, 500))

        return macro
    }

    fun setRobotToDefaultState(robot: ConvenienceRobot, context: Context) {
        val activity = context as Activity
        val sharedPref = activity.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val savedRedValue = sharedPref.getInt(context.getString(R.string.saved_red_value), -1)
        val savedGreenValue = sharedPref.getInt(context.getString(R.string.saved_green_value), -1)
        val savedBlueValue = sharedPref.getInt(context.getString(R.string.saved_blue_value), -1)

        robot.sendCommand(AbortMacroCommand())
        if (savedRedValue != -1) {
            robot.setLed(savedRedValue.toFloat() / 255, savedGreenValue.toFloat() / 255, savedBlueValue.toFloat() / 255 )
        }
        else {
            robot.setLed(0.5f, 0.5f, 0.5f)
        }
        robot.enableStabilization(true)
        robot.setBackLedBrightness(0.0f)
        robot.stop()
    }
}