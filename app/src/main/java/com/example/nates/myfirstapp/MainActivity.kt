package com.example.nates.myfirstapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.orbotix.ConvenienceRobot
import com.orbotix.DualStackDiscoveryAgent
import com.orbotix.common.DiscoveryException
import com.orbotix.common.Robot
import com.orbotix.common.RobotChangedStateListener
import com.orbotix.le.RobotLE
import java.util.*
import android.widget.Toast
import android.R.attr.data
import android.view.Gravity


class MainActivity : Activity(), RobotChangedStateListener {

    private var mRobotActions: RobotActions? = null
    private var mRobotDances: RobotDances? = null
    private var mRobot: ConvenienceRobot? = null
    private var mDiscoveryAgent = DualStackDiscoveryAgent()
    private val clicks = HashMap<Int, Int>()
    private var mp = MediaPlayer()
    private val clicksToStop = 2
    private val requestCodeLocationPermission = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupButtons()

        mDiscoveryAgent.addRobotStateListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            requestCodeLocationPermission -> {
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startDiscovery()
                        Log.d("Permissions", "Permission Granted: " + permissions[i])
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d("Permissions", "Permission Denied: " + permissions[i])
                    }
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }


    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }
        startDiscovery()
    }

    private fun startDiscovery() {
        var toast = Toast.makeText(this, "Connecting...",
                Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
        //If the DiscoveryAgent is not already looking for robots, start discovery.
        if (!mDiscoveryAgent.isDiscovering) {
            try {
                mDiscoveryAgent.startDiscovery(applicationContext)
            } catch (e: DiscoveryException) {
                Log.e("Sphero", "DiscoveryException: " + e.message)
            }

        }
    }

    override fun onStop() {
        //If the DiscoveryAgent is in discovery mode, stop it.
        if (mDiscoveryAgent.isDiscovering) {
            mDiscoveryAgent.stopDiscovery()
        }

        //If a robot is connected to the device, disconnect it
        if (mRobot != null) {
            mRobot!!.disconnect()
            mRobot = null
        }

        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRobotActions!!.setRobotToDefaultState()
        mDiscoveryAgent.addRobotStateListener(null)
        if (mp.isPlaying) {
            mp.stop()
            mp.release()
        }
    }

    override fun handleRobotChangedState(robot: Robot, type: RobotChangedStateListener.RobotChangedStateNotificationType) {
        when (type) {
            RobotChangedStateListener.RobotChangedStateNotificationType.Online -> {
                var toast = Toast.makeText(this, "Connected!",
                        Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()

                //If robot uses Bluetooth LE, Developer Mode can be turned on.
                //This turns off DOS protection. This generally isn't required.
                (robot as? RobotLE)?.setDeveloperMode(true)

                //Save the robot as a ConvenienceRobot for additional utility methods
                mRobot = ConvenienceRobot(robot)
                mRobotActions = RobotActions(mRobot!!)
                mRobotDances = RobotDances(mRobot!!)

                val runMacroButton = findViewById(R.id.run_macro) as Button
                val spinButton = findViewById(R.id.spin_button) as Button
                runMacroButton.isEnabled = true
                spinButton.isEnabled = true
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Offline -> {
                val runMacroButton = findViewById(R.id.run_macro) as Button
                val spinButton = findViewById(R.id.spin_button) as Button
                runMacroButton.isEnabled = false
                spinButton.isEnabled = false
            }
        }
    }

    private fun setupButtons() {
        val runMacroButton = findViewById(R.id.run_macro) as Button
        runMacroButton.setOnClickListener { mRobotActions!!.runMacro() }

        val spinButton = findViewById(R.id.spin_button) as Button
        spinButton.setOnClickListener { mRobotActions!!.spin() }

        runMacroButton.isEnabled = false
        spinButton.isEnabled = false

        val playCheckup = findViewById(R.id.play_checkup) as ImageButton
        playCheckup.setOnClickListener { triggerSong(mRobotDances!!::timeForYourCheckupDance, R.raw.time_for_your_checkup) }

        val playDaniel = findViewById(R.id.play_daniel) as ImageButton
        playDaniel.setOnClickListener { triggerSong(mRobotDances!!::danielTigerDance, R.raw.daniel_tiger_theme) }

        val playSesame = findViewById(R.id.play_sesame) as ImageButton
        playSesame.setOnClickListener { triggerSong(mRobotDances!!::sesameStreetDance, R.raw.seasame_street_theme) }

        val playElmo = findViewById(R.id.play_elmo) as ImageButton
        playElmo.setOnClickListener { triggerSong(mRobotDances!!::elmosSongDance, R.raw.elmos_song) }

        val playSpider = findViewById(R.id.play_spider) as ImageButton
        playSpider.setOnClickListener { triggerSong(mRobotDances!!::itsyBitsySpiderDance, R.raw.itsy_bitsy_spider) }

        val playHead = findViewById(R.id.play_head) as ImageButton
        playHead.setOnClickListener { triggerSong(mRobotDances!!::headShouldersKneesToesDance, R.raw.head_shoulders_knees_toes) }
    }

    private fun recordClick(buttonId: Int) {
        var keyFound = false
        for (key in clicks.keys) {
            if (key == buttonId) {
                clicks.put(key, clicks[key]!! + 1)
                keyFound = true
            } else {
                clicks.put(key, 0)
            }
        }

        if (!keyFound) {
            clicks.put(buttonId, 0)
        }
    }

    private fun triggerSong(song: () -> Unit, resid: Int) {
        if (!mp.isPlaying) {
            mp = MediaPlayer.create(applicationContext, resid)
            mp.start()
            if (mRobot != null && mRobot!!.isConnected) {
                mRobotActions!!.setRobotToDefaultState()
                song()
            }
        } else if (clicks.containsKey(resid) && clicks[resid]!! >= clicksToStop) {
            mRobotActions!!.setRobotToDefaultState()
            mp.stop()
            clicks.put(resid, 0)
        } else {
            recordClick(resid)
        }
    }
}