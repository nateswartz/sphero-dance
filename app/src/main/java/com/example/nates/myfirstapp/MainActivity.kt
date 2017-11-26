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


class MainActivity : Activity(), RobotChangedStateListener {

    private var mRobotActions: RobotActions? = null
    private var mRobotDances: RobotDances? = null
    private var mRobot: ConvenienceRobot? = null
    private var mDiscoveryAgent = DualStackDiscoveryAgent()
    private val clicks = HashMap<String, Int>()
    private var mp = MediaPlayer()
    private val clicksToStop = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupButtons()

        mDiscoveryAgent.addRobotStateListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_LOCATION_PERMISSION -> {
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
        val textViewToChange = findViewById(R.id.status_text) as TextView
        textViewToChange.text = "Connecting..."
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
                val textViewToChange = findViewById(R.id.status_text) as TextView
                textViewToChange.text = "Connected!"

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
        playCheckup.setOnClickListener { triggerTimeForYourCheckup() }

        val playDaniel = findViewById(R.id.play_daniel) as ImageButton
        playDaniel.setOnClickListener { triggerDanielTiger() }

        val playSesame = findViewById(R.id.play_sesame) as ImageButton
        playSesame.setOnClickListener { triggerSesameStreet() }

        val playElmo = findViewById(R.id.play_elmo) as ImageButton
        playElmo.setOnClickListener { triggerElmosSong() }

        val playSpider = findViewById(R.id.play_spider) as ImageButton
        playSpider.setOnClickListener { triggerItsyBitsySpider() }

        val playHead = findViewById(R.id.play_head) as ImageButton
        playHead.setOnClickListener { triggerHeadShouldersKneesToes() }
    }

    private fun recordClick(buttonName: String) {
        var keyFound = false
        for (key in clicks.keys) {
            if (key == buttonName) {
                clicks.put(key, clicks[key]!! + 1)
                keyFound = true
            } else {
                clicks.put(key, 0)
            }
        }

        if (!keyFound) {
            clicks.put(buttonName, 0)
        }
    }

    private fun triggerSong(song: String, resid: Int) {
        if (!mp.isPlaying) {
            mp = MediaPlayer.create(applicationContext, resid)
            mp.start()
            if (mRobot != null && mRobot!!.isConnected) {
                mRobotActions!!.setRobotToDefaultState()
                when (song) {
                    "doc" -> mRobotDances!!.timeForYourCheckupDance()
                    "daniel" -> mRobotDances!!.danielTigerDance()
                    "sesame" -> mRobotDances!!.sesameStreetDance()
                    "elmo" -> mRobotDances!!.elmosSongDance()
                    "spider" -> mRobotDances!!.itsyBitsySpiderDance()
                    "head" -> mRobotDances!!.headShouldersKneesToesDance()
                }
            }
        } else if (clicks.containsKey(song) && clicks[song]!! >= clicksToStop) {
            mRobotActions!!.setRobotToDefaultState()
            mp.stop()
            clicks.put(song, 0)
        } else {
            recordClick(song)
        }
    }

    private fun triggerDanielTiger() {
        triggerSong("daniel", R.raw.daniel_tiger_theme)
    }

    private fun triggerTimeForYourCheckup() {
        triggerSong("doc", R.raw.time_for_your_checkup)
    }

    private fun triggerSesameStreet() {
        triggerSong("sesame", R.raw.seasame_street_theme)
    }

    private fun triggerElmosSong() {
        triggerSong("elmo", R.raw.elmos_song)
    }

    private fun triggerItsyBitsySpider() {
        triggerSong("spider", R.raw.itsy_bitsy_spider)
    }

    private fun triggerHeadShouldersKneesToes() {
        triggerSong("head", R.raw.head_shoulders_knees_toes)
    }

    companion object {
        private val REQUEST_CODE_LOCATION_PERMISSION = 42
    }

}