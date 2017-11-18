package com.example.nates.myfirstapp;

import com.orbotix.ConvenienceRobot;
import com.orbotix.command.AbortMacroCommand;
import com.orbotix.macro.MacroObject;
import com.orbotix.macro.cmd.Delay;
import com.orbotix.macro.cmd.LoopEnd;
import com.orbotix.macro.cmd.LoopStart;
import com.orbotix.macro.cmd.RGB;
import com.orbotix.macro.cmd.RawMotor;
import com.orbotix.macro.cmd.Roll;
import com.orbotix.macro.cmd.RotateOverTime;
import com.orbotix.macro.cmd.Stabilization;
import com.orbotix.macro.cmd.Stop;

import javax.crypto.Mac;

/**
 * Created by nates on 11/18/2017.
 */

public final class Dances {

    private Dances()
    {
    }

    public static void danielTigerDance(ConvenienceRobot robot) {
        if (robot == null)
            return;

        setRobotToDefaultState(robot);

        MacroObject macro = new MacroObject();

        RGB color1 = new RGB(255, 152, 0, 0);
        RGB color2 = new RGB(50, 23, 174, 0);
        RGB color3 = new RGB(224, 10, 12, 0);


        // 1 Second
        // Shaking and flashing
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new LoopStart(50));
        macro.addCommand(color1);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(color2);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stabilization(true, 0));

        // 8 Seconds
        // Rolling around, changing color
        macro.addCommand(new LoopStart(8));
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.25f, 0, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color2);
        macro.addCommand(new Roll(0.25f, 270, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color3);
        macro.addCommand(new Roll(0.25f, 180, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.25f, 90, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color2);
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Roll(0.25f, 180, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(new Stop(0));

        // 24 Seconds
        // 8 x 3 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(new LoopStart(7));
        macro.addCommand(color1);
        macro.addCommand(new Delay(1000));
        macro.addCommand(color2);
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225));
        macro.addCommand(new Delay(225));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225));
        macro.addCommand(new Delay(225));
        macro.addCommand(color3);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500));
        macro.addCommand(new Delay(500));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stop(0));

        //1.2 Seconds
        // Color flashing, no movement
        macro.addCommand(new LoopStart(8));
        macro.addCommand(color1);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(color2);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(color3);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stop(0));

        //Send the macro to the robot and play
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(robot.getRobot());
        macro.playMacro();
    }


    public static void timeForYourCheckupDance(ConvenienceRobot robot) {
        if (robot == null)
            return;

        setRobotToDefaultState(robot);

        MacroObject macro = new MacroObject();

        RGB color1 = new RGB(255, 28, 101, 0);
        RGB color2 = new RGB(50, 23, 174, 0);
        RGB color3 = new RGB(21, 150, 43, 0);
        RGB color4 = new RGB(100, 100, 100, 0);

        // Intro
        macro.addCommand(color1);
        macro.addCommand(new Delay(400));
        macro.addCommand(color2);
        macro.addCommand(new Delay(400));
        macro.addCommand(color3);
        macro.addCommand(new Delay(400));

        // Main Loop
        macro.addCommand(new LoopStart(6));
        // Move 1
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.3f, 0, 0));
        macro.addCommand(new Delay(400));
        macro.addCommand(new Roll(0.3f, 180, 0));
        macro.addCommand(new Delay(400));
        // Move 2
        macro.addCommand(color2);
        macro.addCommand(new RotateOverTime(720,800));
        macro.addCommand(new Delay(800));
        // Move 3
        macro.addCommand(color3);
        macro.addCommand(new Delay(300));
        macro.addCommand(color1);
        macro.addCommand(new Delay(200));
        macro.addCommand(color3);
        macro.addCommand(new Delay(300));
        // Move 4
        macro.addCommand(color4);
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 200, RawMotor.DriveMode.REVERSE, 200, 200));
        macro.addCommand(new Delay(200));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.FORWARD, 200, 200));
        macro.addCommand(new Delay(200));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 200, RawMotor.DriveMode.REVERSE, 200, 200));
        macro.addCommand(new Delay(200));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.FORWARD, 200, 200));
        macro.addCommand(new Delay(200));
        macro.addCommand(new Stabilization(true, 0));
        macro.addCommand(new LoopEnd());

        // Ending
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new LoopStart(40));
        macro.addCommand(color1);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 200, RawMotor.DriveMode.REVERSE, 200, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(color2);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.FORWARD, 200, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stabilization(true, 0));
        macro.addCommand(new Roll(0.0f, 0, 0));

        //Send the macro to the robot and play
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(robot.getRobot());
        macro.playMacro();
    }

    // TODO: Still a work in progress
    public static void itsyBitsySpiderDance(ConvenienceRobot robot) {
        if (robot == null)
            return;

        setRobotToDefaultState(robot);

        MacroObject macro = new MacroObject();

        RGB color1 = new RGB(200, 0, 0, 0);
        RGB color2 = new RGB(0, 200, 0, 0);
        RGB color3 = new RGB(0, 0, 200, 0);

        // 15 Seconds
        // 5 x 3 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(new LoopStart(4));
        macro.addCommand(color1);
        macro.addCommand(new Delay(1000));
        macro.addCommand(color2);
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225));
        macro.addCommand(new Delay(225));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225));
        macro.addCommand(new Delay(225));
        macro.addCommand(color3);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500));
        macro.addCommand(new Delay(500));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stop(0));

        // 1 Second
        // Shaking and flashing
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new LoopStart(50));
        macro.addCommand(color1);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(color2);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stabilization(true, 0));

        // 8 Seconds
        // Rolling around, changing color
        macro.addCommand(new LoopStart(8));
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.25f, 0, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color2);
        macro.addCommand(new Roll(0.25f, 270, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color3);
        macro.addCommand(new Roll(0.25f, 180, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.25f, 90, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color2);
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Roll(0.25f, 180, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(new Stop(0));

        //1.2 Seconds
        // Color flashing, no movement
        macro.addCommand(new LoopStart(8));
        macro.addCommand(color1);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(color2);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(color3);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stop(0));

        //Send the macro to the robot and play
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(robot.getRobot());
        macro.playMacro();
    }

    // TODO: Still a work in progress
    public static void headShouldersKneesToesDance(ConvenienceRobot robot) {
        if (robot == null)
            return;

        setRobotToDefaultState(robot);

        MacroObject macro = new MacroObject();

        RGB color1 = new RGB(255, 255, 0, 0);
        RGB color2 = new RGB(0, 255, 255, 0);
        RGB color3 = new RGB(255, 0, 255, 0);

        // 7 Seconds
        // Rolling around, changing color
        macro.addCommand(new LoopStart(7));
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.25f, 0, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color2);
        macro.addCommand(new Roll(0.25f, 270, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color3);
        macro.addCommand(new Roll(0.25f, 180, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.25f, 90, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color2);
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Roll(0.25f, 180, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(new Stop(0));

        // 2 Seconds
        // Shaking and flashing
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new LoopStart(100));
        macro.addCommand(color1);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(color2);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stabilization(true, 0));

        // 9 Seconds
        // 3 x 3 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(new LoopStart(2));
        macro.addCommand(color1);
        macro.addCommand(new Delay(1000));
        macro.addCommand(color2);
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225));
        macro.addCommand(new Delay(225));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225));
        macro.addCommand(new Delay(225));
        macro.addCommand(color3);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500));
        macro.addCommand(new Delay(500));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stop(0));

        //1.2 Seconds
        // Color flashing, no movement
        macro.addCommand(new LoopStart(8));
        macro.addCommand(color1);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(color2);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(color3);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stop(0));

        //Send the macro to the robot and play
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(robot.getRobot());
        macro.playMacro();
    }

    // TODO: Still a work in progress
    public static void sesameStreetDance(ConvenienceRobot robot) {
        if (robot == null)
            return;

        setRobotToDefaultState(robot);

        MacroObject macro = new MacroObject();

        RGB color1 = new RGB(255, 0, 255, 0);
        RGB color2 = new RGB(100, 50, 240, 0);
        RGB color3 = new RGB(145, 33, 255, 0);

        // 9 Seconds
        // Rolling around, changing color
        macro.addCommand(new LoopStart(9));
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.25f, 0, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color2);
        macro.addCommand(new Roll(0.25f, 270, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color3);
        macro.addCommand(new Roll(0.25f, 180, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.25f, 90, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(color2);
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Roll(0.25f, 180, 0));
        macro.addCommand(new Delay(250));
        macro.addCommand(new Stop(0));

        // 21 Seconds
        // 7 x 3 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(new LoopStart(6));
        macro.addCommand(color1);
        macro.addCommand(new Delay(1000));
        macro.addCommand(color2);
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225));
        macro.addCommand(new Delay(225));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225));
        macro.addCommand(new Delay(225));
        macro.addCommand(color3);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 500));
        macro.addCommand(new Delay(500));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stop(0));

        // 2 Seconds
        // Shaking and flashing
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new LoopStart(100));
        macro.addCommand(color1);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 255, RawMotor.DriveMode.REVERSE, 255, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(color2);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 255, RawMotor.DriveMode.FORWARD, 255, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stabilization(true, 0));

        //1.6 Seconds
        // Color flashing, no movement
        macro.addCommand(new LoopStart(10));
        macro.addCommand(color1);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 100, RawMotor.DriveMode.REVERSE, 100, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(color2);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.REVERSE, 150, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(color3);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.REVERSE, 200, 100));
        macro.addCommand(new Delay(100));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stop(0));

        //Send the macro to the robot and play
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(robot.getRobot());
        macro.playMacro();
    }

    //Set the robot to a default 'clean' state between running macros
    private static void setRobotToDefaultState(ConvenienceRobot robot) {
        if (robot == null)
            return;

        robot.sendCommand(new AbortMacroCommand());
        robot.setLed(1.0f, 1.0f, 1.0f);
        robot.enableStabilization(true);
        robot.setBackLedBrightness(0.0f);
        robot.stop();
    }
}
