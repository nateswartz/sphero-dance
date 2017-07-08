package com.example.nates.myfirstapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.bluetooth.BluetoothAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.media.MediaPlayer;

import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.le.RobotLE;
import com.orbotix.macro.cmd.GoTo;
import com.orbotix.macro.cmd.RawMotor;
import com.orbotix.command.AbortMacroCommand;

import com.orbotix.macro.MacroObject;
import com.orbotix.macro.MacroObject.OnMacroUploadedListener;
import com.orbotix.macro.cmd.BackLED;
import com.orbotix.macro.cmd.Delay;
import com.orbotix.macro.cmd.Fade;
import com.orbotix.macro.cmd.LoopEnd;
import com.orbotix.macro.cmd.LoopStart;
import com.orbotix.macro.cmd.RGB;
import com.orbotix.macro.cmd.RawMotor;
import com.orbotix.macro.cmd.Roll;
import com.orbotix.macro.cmd.RotateOverTime;
import com.orbotix.macro.cmd.Stabilization;
import com.orbotix.macro.cmd.Stop;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Hello World Sample
 * Connect either a Bluetooth Classic or Bluetooth LE robot to an Android Device, then
 * blink the robot's LED on or off every two seconds.
 *
 * This example also covers turning on Developer Mode for LE robots.
 */

public class MainActivity extends Activity implements RobotChangedStateListener {

    private ConvenienceRobot mRobot;
    private DualStackDiscoveryAgent mDiscoveryAgent;
    private String mColor = "red";
    //BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean isBackLedOn = false;
    private boolean isMainLedCycling = false;
    private boolean hasPlayed = false;
    private int danielClicks = 0;
    private int docClicks = 0;
    private int headClicks = 0;
    private int spiderClicks = 0;
    private MediaPlayer mp;
    private final int CLICKSTOSTOP = 2;
    private boolean isAligning = false;
    MacroObject macro;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupButtons();

       /* if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }*/

       /* try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {}*/

        mDiscoveryAgent = new DualStackDiscoveryAgent();
        mDiscoveryAgent.addRobotStateListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startDiscovery();
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        startDiscovery();
    }

    private void startDiscovery() {
        final TextView textViewToChange = (TextView) findViewById(R.id.status_text);
        textViewToChange.setText("Connecting...");
        //If the DiscoveryAgent is not already looking for robots, start discovery.
        if (!mDiscoveryAgent.isDiscovering()) {
            try {
                mDiscoveryAgent.startDiscovery(getApplicationContext());
            } catch (DiscoveryException e) {
                Log.e("Sphero", "DiscoveryException: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onStop() {
        //If the DiscoveryAgent is in discovery mode, stop it.
        if (mDiscoveryAgent.isDiscovering()) {
            mDiscoveryAgent.stopDiscovery();
        }

        //If a robot is connected to the device, disconnect it
        if (mRobot != null) {
            mRobot.disconnect();
            mRobot = null;
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setRobotToDefaultState();
        mDiscoveryAgent.addRobotStateListener(null);
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
        /*if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }*/
    }

    @Override
    public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
        switch(type) {
            case Online: {
                final TextView textViewToChange = (TextView) findViewById(R.id.status_text);
                textViewToChange.setText("Connected!");

                //If robot uses Bluetooth LE, Developer Mode can be turned on.
                //This turns off DOS protection. This generally isn't required.
                if (robot instanceof RobotLE) {
                    ((RobotLE) robot).setDeveloperMode(true);
                }

                //Save the robot as a ConvenienceRobot for additional utility methods
                mRobot = new ConvenienceRobot(robot);

                Button runMacroButton = (Button) findViewById(R.id.run_macro);
                Button ledToggleButton = (Button) findViewById(R.id.back_led_toggle);
                Button alignButton = (Button) findViewById(R.id.align_button);
                runMacroButton.setEnabled(true);
                ledToggleButton.setEnabled(true);
                alignButton.setEnabled(true);

                break;
            }
            case Offline: {
                Button runMacroButton = (Button) findViewById(R.id.run_macro);
                Button ledToggleButton = (Button) findViewById(R.id.back_led_toggle);
                Button alignButton = (Button) findViewById(R.id.align_button);
                runMacroButton.setEnabled(false);
                ledToggleButton.setEnabled(false);
                alignButton.setEnabled(false);
            }
        }
    }

    private void runMacro() {
        if (mRobot == null)
            return;

        setRobotToDefaultState();

        macro = new MacroObject();


        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new LoopStart(500));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 200, RawMotor.DriveMode.REVERSE, 200, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.FORWARD, 200, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stabilization(true, 0));

        //Send the macro to the robot and play
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(mRobot.getRobot());
        macro.playMacro();
    }

    private void danielTigerDance() {
        if (mRobot == null)
            return;

        setRobotToDefaultState();

        macro = new MacroObject();

        RGB color1 = new RGB(255, 28, 101, 0);
        RGB color2 = new RGB(50, 23, 174, 0);
        RGB color3 = new RGB(21, 150, 43, 0);


        // 1 Second
        // Shaking and flashing
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new LoopStart(50));
        macro.addCommand(color1);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 200, RawMotor.DriveMode.REVERSE, 200, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(color2);
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 200, RawMotor.DriveMode.FORWARD, 200, 10));
        macro.addCommand(new Delay(10));
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stabilization(true, 0));

        // 8 Seconds
        // Rolling around, changing color
        macro.addCommand(new LoopStart(4));
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.3f, 0, 0));
        macro.addCommand(new Delay(500));
        macro.addCommand(color2);
        macro.addCommand(new Roll(0.3f, 270, 0));
        macro.addCommand(new Delay(500));
        macro.addCommand(color3);
        macro.addCommand(new Roll(0.3f, 180, 0));
        macro.addCommand(new Delay(500));
        macro.addCommand(color1);
        macro.addCommand(new Roll(0.3f, 90, 0));
        macro.addCommand(new Delay(500));
        macro.addCommand(color2);
        macro.addCommand(new LoopEnd());
        macro.addCommand(new Stop(0));

        // 24 Seconds
        // 8 x 3 Second Loops
        // Color change and spin and go crazy
        macro.addCommand(new LoopStart(8));
        macro.addCommand(color1);
        macro.addCommand(new Delay(1000));
        macro.addCommand(color2);
        macro.addCommand(new Stabilization(false, 0));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.REVERSE, 150, RawMotor.DriveMode.REVERSE, 150, 225));
        macro.addCommand(new Delay(225));
        macro.addCommand(new RawMotor(RawMotor.DriveMode.FORWARD, 150, RawMotor.DriveMode.FORWARD, 150, 225));
        macro.addCommand(new Delay(225));
        macro.addCommand(new Stabilization(true, 0));
        macro.addCommand(color3);
        macro.addCommand(new RotateOverTime(540,500));
        macro.addCommand(new Delay(500));
        macro.addCommand(new LoopEnd());

        //1.2 Seconds
        // Color flashing, no movement
        macro.addCommand(new LoopStart(4));
        macro.addCommand(color1);
        macro.addCommand(new Delay(100));
        macro.addCommand(color2);
        macro.addCommand(new Delay(100));
        macro.addCommand(color3);
        macro.addCommand(new Delay(100));
        macro.addCommand(new LoopEnd());

        //Send the macro to the robot and play
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(mRobot.getRobot());
        macro.playMacro();
    }

    private void timeForYourCheckupDance() {
        if (mRobot == null)
            return;

        setRobotToDefaultState();

        macro = new MacroObject();

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
        macro.setRobot(mRobot.getRobot());
        macro.playMacro();
    }

    private void itsyBitsySpiderDance() {
        if (mRobot == null)
            return;

        setRobotToDefaultState();

        macro = new MacroObject();

        RGB color1 = new RGB(255, 28, 101, 0);
        RGB color2 = new RGB(50, 23, 174, 0);
        RGB color3 = new RGB(21, 150, 43, 0);


        macro.addCommand(new LoopStart(4));
        macro.addCommand(color1);
        macro.addCommand(new Delay(1000));
        macro.addCommand(color2);
        macro.addCommand(new Delay(1000));
        macro.addCommand(new LoopEnd());

        //Send the macro to the robot and play
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(mRobot.getRobot());
        macro.playMacro();
    }


    private void headShouldersKneesToesDance() {
        if (mRobot == null)
            return;

        setRobotToDefaultState();

        macro = new MacroObject();

        RGB color1 = new RGB(255, 28, 101, 0);
        RGB color2 = new RGB(50, 23, 174, 0);
        RGB color3 = new RGB(21, 150, 43, 0);


        macro.addCommand(new LoopStart(4));
        macro.addCommand(color1);
        macro.addCommand(new Delay(1000));
        macro.addCommand(color2);
        macro.addCommand(new Delay(1000));
        macro.addCommand(new LoopEnd());

        //Send the macro to the robot and play
        macro.setMode(MacroObject.MacroObjectMode.Normal);
        macro.setRobot(mRobot.getRobot());
        macro.playMacro();
    }


    //Set the robot to a default 'clean' state between running macros
    private void setRobotToDefaultState() {
        if (mRobot == null)
            return;

        mRobot.sendCommand(new AbortMacroCommand());
        mRobot.setLed(1.0f, 1.0f, 1.0f);
        mRobot.enableStabilization(true);
        mRobot.setBackLedBrightness(0.0f);
        mRobot.stop();
    }

    private void setupButtons() {
        Button runMacroButton = (Button) findViewById(R.id.run_macro);
        runMacroButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                runMacro();
            }
        });

        Button alignButton = (Button) findViewById(R.id.align_button);
        alignButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAlignMode();
            }
        });

        Button ledToggleButton = (Button) findViewById(R.id.back_led_toggle);
        ledToggleButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBackLED();
            }
        });

        runMacroButton.setEnabled(false);
        ledToggleButton.setEnabled(false);
        alignButton.setEnabled(false);

        ImageButton playCheckup = (ImageButton) findViewById(R.id.play_checkup);
        playCheckup.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                playTimeForYourCheckup();
            }
        });

        ImageButton playDaniel = (ImageButton) findViewById(R.id.play_daniel);
        playDaniel.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                playDanielTiger();
            }
        });

        ImageButton playSpider = (ImageButton) findViewById(R.id.play_spider);
        playSpider.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                playItsyBitsySpider();
            }
        });

        ImageButton playHead = (ImageButton) findViewById(R.id.play_head);
        playHead.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                playHeadShouldersKneesToes();
            }
        });
    }

    private void recordClick(String buttonName) {
        switch (buttonName) {
            case "daniel":
                danielClicks++;
                docClicks = 0;
                headClicks = 0;
                spiderClicks = 0;
                break;
            case "doc":
                docClicks++;
                danielClicks = 0;
                headClicks = 0;
                spiderClicks = 0;
                break;
            case "head":
                headClicks++;
                docClicks = 0;
                danielClicks = 0;
                spiderClicks = 0;
                break;
            case "spider":
                spiderClicks++;
                danielClicks = 0;
                docClicks = 0;
                headClicks = 0;
        }
    }

    private void toggleBackLED() {
        if (isBackLedOn) {
            mRobot.setBackLedBrightness(0.0f);
            isBackLedOn = false;
        } else {
            mRobot.setBackLedBrightness(1.0f);
            isBackLedOn = true;
        }
    }

    private void toggleAlignMode() {
        if (mRobot == null)
            return;

        if (isAligning) {
            isAligning = false;
            mRobot.setZeroHeading();
            mRobot.setBackLedBrightness(0.0f);
            mRobot.enableStabilization(true);
        }
        else {
            isAligning = true;
            mRobot.setBackLedBrightness(1.0f);
            mRobot.enableStabilization(false);
        }
    }

    private void playDanielTiger() {
        if (mp == null || !mp.isPlaying()) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.daniel_tiger_theme);
            mp.start();
            if (mRobot != null && mRobot.isConnected()) {
                danielTigerDance();
            }
            hasPlayed = true;
        } else if (danielClicks >= CLICKSTOSTOP){
            setRobotToDefaultState();
            mp.stop();
            danielClicks = 0;
        } else {
            recordClick("daniel");
        }

    }

    private void playTimeForYourCheckup() {
        if (mp == null || !mp.isPlaying()) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.time_for_your_checkup);
            mp.start();
            hasPlayed = true;
            if (mRobot != null && mRobot.isConnected()) {
                //danielTigerDance();
                timeForYourCheckupDance();
            }
        } else if (docClicks >= CLICKSTOSTOP){
            setRobotToDefaultState();
            mp.stop();
            docClicks = 0;
        } else {
            recordClick("doc");
        }
    }

    private void playItsyBitsySpider() {
        if (mp == null || !mp.isPlaying()) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.itsy_bitsy_spider);
            mp.start();
            hasPlayed = true;
            if (mRobot != null && mRobot.isConnected()) {
                danielTigerDance();
                //itsyBitsySpiderDance();
            }
        } else if (spiderClicks >= CLICKSTOSTOP){
            setRobotToDefaultState();
            mp.stop();
            spiderClicks = 0;
        } else {
            recordClick("spider");
        }
    }

    private void playHeadShouldersKneesToes() {
        if (mp == null || !mp.isPlaying()) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.head_shoulders_knees_toes);
            mp.start();
            hasPlayed = true;
            if (mRobot != null && mRobot.isConnected()) {
                danielTigerDance();
                //headShouldersKneesToesDance();
            }
        } else if (headClicks >= CLICKSTOSTOP){
            setRobotToDefaultState();
            mp.stop();
            headClicks = 0;
        } else {
            recordClick("head");
        }
    }

}