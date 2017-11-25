package com.example.nates.myfirstapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.le.RobotLE;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity implements RobotChangedStateListener {

    private RobotActions mRobotActions;
    private RobotDances mRobotDances;
    private ConvenienceRobot mRobot;
    private DualStackDiscoveryAgent mDiscoveryAgent;
    private Map<String, Integer> clicks = new HashMap<>();
    private MediaPlayer mp;
    private final int CLICKSTOSTOP = 2;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupButtons();

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
        mRobotActions.setRobotToDefaultState();
        mDiscoveryAgent.addRobotStateListener(null);
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
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
                mRobotActions = new RobotActions(mRobot);
                mRobotDances = new RobotDances(mRobot);

                Button runMacroButton = (Button) findViewById(R.id.run_macro);
                Button spinButton = (Button) findViewById(R.id.spin_button);
                runMacroButton.setEnabled(true);
                spinButton.setEnabled(true);

                break;
            }
            case Offline: {
                Button runMacroButton = (Button) findViewById(R.id.run_macro);
                Button spinButton = (Button) findViewById(R.id.spin_button);
                runMacroButton.setEnabled(false);
                spinButton.setEnabled(false);
            }
        }
    }

    private void setupButtons() {
        Button runMacroButton = (Button) findViewById(R.id.run_macro);
        runMacroButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRobotActions.runMacro();
            }
        });

        Button spinButton = (Button) findViewById(R.id.spin_button);
        spinButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRobotActions.spin();
            }
        });

        runMacroButton.setEnabled(false);
        spinButton.setEnabled(false);

        ImageButton playCheckup = (ImageButton) findViewById(R.id.play_checkup);
        playCheckup.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerTimeForYourCheckup();
            }
        });

        ImageButton playDaniel = (ImageButton) findViewById(R.id.play_daniel);
        playDaniel.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerDanielTiger();
            }
        });

        ImageButton playSesame = (ImageButton) findViewById(R.id.play_sesame);
        playSesame.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerSesameStreet();
            }
        });

        ImageButton playElmo = (ImageButton) findViewById(R.id.play_elmo);
        playSesame.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerElmosSong();
            }
        });

        ImageButton playSpider = (ImageButton) findViewById(R.id.play_spider);
        playSpider.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerItsyBitsySpider();
            }
        });

        ImageButton playHead = (ImageButton) findViewById(R.id.play_head);
        playHead.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerHeadShouldersKneesToes();
            }
        });
    }

    private void recordClick(String buttonName) {
        boolean keyFound = false;
        for (String key : clicks.keySet()) {
            if (key.equals(buttonName))
            {
                clicks.put(key, clicks.get(key) + 1);
                keyFound = true;
            }
            else
            {
                clicks.put(key, 0);
            }
        }

        if (!keyFound)
        {
            clicks.put(buttonName, 0);
        }
    }

    private void triggerSong(String song, int resid) {
        if (mp == null || !mp.isPlaying()) {
            mp = MediaPlayer.create(getApplicationContext(), resid);
            mp.start();
            if (mRobot != null && mRobot.isConnected()) {
                mRobotActions.setRobotToDefaultState();
                switch (song) {
                    case "doc":
                        mRobotDances.timeForYourCheckupDance();
                        break;
                    case "daniel":
                        mRobotDances.danielTigerDance();
                        break;
                    case "sesame":
                        mRobotDances.sesameStreetDance();
                        break;
                    case "elmo":
                        mRobotDances.elmosSongDance();
                        break;
                    case "spider":
                        mRobotDances.itsyBitsySpiderDance();
                        break;
                    case "head":
                        mRobotDances.headShouldersKneesToesDance();
                        break;
                }
            }
        } else if (clicks.containsKey(song) && clicks.get(song) >= CLICKSTOSTOP){
            mRobotActions.setRobotToDefaultState();
            mp.stop();
            clicks.put(song, 0);
        } else {
            recordClick(song);
        }
    }

    private void triggerDanielTiger() {
        triggerSong("daniel", R.raw.daniel_tiger_theme);
    }

    private void triggerTimeForYourCheckup() {
        triggerSong("doc", R.raw.time_for_your_checkup);
    }

    private void triggerSesameStreet() {
        triggerSong("sesame", R.raw.seasame_street_theme);
    }

    private void triggerElmosSong() {
        triggerSong("elmo", R.raw.elmos_song);
    }

    private void triggerItsyBitsySpider() {
        triggerSong("spider", R.raw.itsy_bitsy_spider);
    }

    private void triggerHeadShouldersKneesToes() {
        triggerSong("head", R.raw.head_shoulders_knees_toes);
    }

}