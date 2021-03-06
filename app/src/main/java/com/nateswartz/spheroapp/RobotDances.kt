package com.nateswartz.spheroapp

import com.orbotix.macro.MacroObject
import com.orbotix.macro.cmd.*

/**
 * Created by nates on 11/23/2017.
 */
class RobotDances {

    fun danielTigerDance() : MacroObject {
        val macro = MacroObject()

        val color1 = RGB(255, 152, 0, 0)
        val color2 = RGB(50, 23, 174, 0)
        val color3 = RGB(224, 10, 12, 0)

        // 1 Second
        // Shaking and flashing
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(LoopStart(50))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stabilization(true, 0))

        // 8 Seconds
        // Rolling around, changing color
        macro.addCommand(LoopStart(8))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 0, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(Roll(0.25f, 270, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color3)
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 90, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(LoopEnd())
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(Stop(0))

        // 24 Seconds
        // 8 x 3 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(LoopStart(7))
        macro.addCommand(color1)
        macro.addCommand(Delay(1000))
        macro.addCommand(color2)
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500))
        macro.addCommand(Delay(500))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        //1.2 Seconds
        // Color flashing, no movement
        macro.addCommand(LoopStart(8))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        //Send the macro to the robot and play
        macro.mode = MacroObject.MacroObjectMode.Normal
        return macro
    }


    fun timeForYourCheckupDance() : MacroObject {
        val macro = MacroObject()

        val color1 = RGB(255, 28, 101, 0)
        val color2 = RGB(50, 23, 174, 0)
        val color3 = RGB(21, 150, 43, 0)
        val color4 = RGB(100, 100, 100, 0)

        // Intro
        macro.addCommand(color1)
        macro.addCommand(Delay(400))
        macro.addCommand(color2)
        macro.addCommand(Delay(400))
        macro.addCommand(color3)
        macro.addCommand(Delay(400))

        // Main Loop
        macro.addCommand(LoopStart(6))
        // Move 1
        macro.addCommand(color1)
        macro.addCommand(Roll(0.3f, 0, 0))
        macro.addCommand(Delay(400))
        macro.addCommand(Roll(0.3f, 180, 0))
        macro.addCommand(Delay(400))
        // Move 2
        macro.addCommand(color2)
        macro.addCommand(RotateOverTime(720, 800))
        macro.addCommand(Delay(800))
        // Move 3
        macro.addCommand(color3)
        macro.addCommand(Delay(300))
        macro.addCommand(color1)
        macro.addCommand(Delay(200))
        macro.addCommand(color3)
        macro.addCommand(Delay(300))
        // Move 4
        macro.addCommand(color4)
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 200, RawMotor.DriveMode.REVERSE, 200, 200))
        macro.addCommand(Delay(200))
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.FORWARD, 200, 200))
        macro.addCommand(Delay(200))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 200, RawMotor.DriveMode.REVERSE, 200, 200))
        macro.addCommand(Delay(200))
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.FORWARD, 200, 200))
        macro.addCommand(Delay(200))
        macro.addCommand(Stabilization(true, 0))
        macro.addCommand(LoopEnd())

        // Ending
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(LoopStart(40))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 200, RawMotor.DriveMode.REVERSE, 200, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.FORWARD, 200, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stabilization(true, 0))
        macro.addCommand(Roll(0.0f, 0, 0))

        //Send the macro to the robot and play
        macro.mode = MacroObject.MacroObjectMode.Normal
        return macro
    }

    // TODO: Still a work in progress
    fun itsyBitsySpiderDance() : MacroObject {
        val macro = MacroObject()

        val color1 = RGB(200, 0, 0, 0)
        val color2 = RGB(0, 200, 0, 0)
        val color3 = RGB(0, 0, 200, 0)

        // 15 Seconds
        // 5 x 3 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(LoopStart(4))
        macro.addCommand(color1)
        macro.addCommand(Delay(1000))
        macro.addCommand(color2)
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500))
        macro.addCommand(Delay(500))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        // 1 Second
        // Shaking and flashing
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(LoopStart(50))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stabilization(true, 0))

        // 8 Seconds
        // Rolling around, changing color
        macro.addCommand(LoopStart(8))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 0, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(Roll(0.25f, 270, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color3)
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 90, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(LoopEnd())
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(Stop(0))

        //1.2 Seconds
        // Color flashing, no movement
        macro.addCommand(LoopStart(8))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        //Send the macro to the robot and play
        macro.mode = MacroObject.MacroObjectMode.Normal
        return macro
    }

    // TODO: Still a work in progress
    fun headShouldersKneesToesDance() : MacroObject {
        val macro = MacroObject()

        val color1 = RGB(255, 255, 0, 0)
        val color2 = RGB(0, 255, 255, 0)
        val color3 = RGB(255, 0, 255, 0)

        // 7 Seconds
        // Rolling around, changing color
        macro.addCommand(LoopStart(7))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 0, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(Roll(0.25f, 270, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color3)
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 90, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(LoopEnd())
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(Stop(0))

        // 2 Seconds
        // Shaking and flashing
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(LoopStart(100))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stabilization(true, 0))

        // 9 Seconds
        // 3 x 3 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(LoopStart(2))
        macro.addCommand(color1)
        macro.addCommand(Delay(1000))
        macro.addCommand(color2)
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500))
        macro.addCommand(Delay(500))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        //1.2 Seconds
        // Color flashing, no movement
        macro.addCommand(LoopStart(8))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        //Send the macro to the robot and play
        macro.mode = MacroObject.MacroObjectMode.Normal
        return macro
    }

    // TODO: Still a work in progress
    fun sesameStreetDance() : MacroObject {
        val macro = MacroObject()

        val color1 = RGB(255, 0, 255, 0)
        val color2 = RGB(100, 50, 240, 0)
        val color3 = RGB(145, 33, 255, 0)

        // 15 Seconds
        // Rolling around, changing color
        macro.addCommand(LoopStart(15))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 0, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(Roll(0.25f, 270, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color3)
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 90, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(LoopEnd())
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(Stop(0))

        // 22 Seconds
        // 11 x 2 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(LoopStart(11))
        macro.addCommand(color1)
        macro.addCommand(Delay(1000))
        macro.addCommand(color2)
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500))
        macro.addCommand(Delay(500))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        // 2 Seconds
        // Shaking and flashing
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(LoopStart(100))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stabilization(true, 0))

        //1.6 Seconds
        // Color flashing, no movement
        macro.addCommand(LoopStart(10))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        //Send the macro to the robot and play
        macro.mode = MacroObject.MacroObjectMode.Normal
        return macro
    }

    // TODO: Still a work in progress
    fun elmosSongDance() : MacroObject {
        val macro = MacroObject()

        val color1 = RGB(255, 0, 0, 0)
        val color2 = RGB(175, 0, 0, 0)
        val color3 = RGB(50, 20, 20, 0)

        // 22 Seconds
        // 11 x 2 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(LoopStart(11))
        macro.addCommand(color1)
        macro.addCommand(Delay(1000))
        macro.addCommand(color2)
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500))
        macro.addCommand(Delay(500))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        //1.6 Seconds
        // Color flashing, no movement
        macro.addCommand(LoopStart(10))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        // 15 Seconds
        // Rolling around, changing color
        macro.addCommand(LoopStart(15))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 0, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(Roll(0.25f, 270, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color3)
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 90, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(LoopEnd())
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(Stop(0))

        // 2 Seconds
        // Shaking and flashing
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(LoopStart(100))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stabilization(true, 0))

        //Send the macro to the robot and play
        macro.mode = MacroObject.MacroObjectMode.Normal
        return macro
    }

    // TODO: Still a work in progress
    fun cookieDance() : MacroObject {
        val macro = MacroObject()

        val color1 = RGB(255, 0, 0, 0)
        val color2 = RGB(175, 0, 0, 0)
        val color3 = RGB(50, 20, 20, 0)

        // 10 Seconds
        // 5 x 2 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(LoopStart(5))
        macro.addCommand(color1)
        macro.addCommand(Delay(1000))
        macro.addCommand(color2)
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500))
        macro.addCommand(Delay(500))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        //1.6 Seconds
        // Color flashing, no movement
        macro.addCommand(LoopStart(10))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        // 12 Seconds
        // Rolling around, changing color
        macro.addCommand(LoopStart(12))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 0, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(Roll(0.25f, 270, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color3)
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 90, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(LoopEnd())
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(Stop(0))

        // 2 Seconds
        // Shaking and flashing
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(LoopStart(100))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stabilization(true, 0))

        //Send the macro to the robot and play
        macro.mode = MacroObject.MacroObjectMode.Normal
        return macro
    }

    // TODO: Still a work in progress
    fun rubberDuckieDance() : MacroObject {
        val macro = MacroObject()

        val color1 = RGB(255, 0, 0, 0)
        val color2 = RGB(175, 0, 0, 0)
        val color3 = RGB(50, 20, 20, 0)

        // 10 Seconds
        // 5 x 2 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(LoopStart(5))
        macro.addCommand(color1)
        macro.addCommand(Delay(1000))
        macro.addCommand(color2)
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225))
        macro.addCommand(Delay(225))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500))
        macro.addCommand(Delay(500))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        //1.6 Seconds
        // Color flashing, no movement
        macro.addCommand(LoopStart(10))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(color3)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100))
        macro.addCommand(Delay(100))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stop(0))

        // 15 Seconds
        // Rolling around, changing color
        macro.addCommand(LoopStart(15))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 0, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(Roll(0.25f, 270, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color3)
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color1)
        macro.addCommand(Roll(0.25f, 90, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(color2)
        macro.addCommand(LoopEnd())
        macro.addCommand(Roll(0.25f, 180, 0))
        macro.addCommand(Delay(250))
        macro.addCommand(Stop(0))

        // 2 Seconds
        // Shaking and flashing
        macro.addCommand(Stabilization(false, 0))
        macro.addCommand(LoopStart(100))
        macro.addCommand(color1)
        macro.addCommand(RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(color2)
        macro.addCommand(RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10))
        macro.addCommand(Delay(10))
        macro.addCommand(LoopEnd())
        macro.addCommand(Stabilization(true, 0))

        //Send the macro to the robot and play
        macro.mode = MacroObject.MacroObjectMode.Normal
        return macro
    }
}