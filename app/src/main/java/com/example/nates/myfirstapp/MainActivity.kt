package com.example.nates.myfirstapp

import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import com.orbotix.ConvenienceRobot
import com.orbotix.DualStackDiscoveryAgent
import com.orbotix.common.DiscoveryException
import com.orbotix.common.Robot
import com.orbotix.common.RobotChangedStateListener
import com.orbotix.le.RobotLE
import java.util.*
import android.widget.Toast
import android.view.Gravity
import com.orbotix.macro.MacroObject
import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import android.content.ServiceConnection
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.renderscript.ScriptGroup


class MainActivity : Activity() {
    private var mBoundService: RobotProviderService? = null
    private var mRobotActions: RobotActions? = null
    private var mRobotDances = RobotDances()
    private var mRobot: ConvenienceRobot? = null
    private val clicks = HashMap<Int, Int>()
    private var mp = MediaPlayer()
    private val clicksToStop = 2
    private var mIsBound = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mBoundService = (service as RobotProviderService.LocalBinder).service
            var toast = Toast.makeText(this@MainActivity, "Connected!",
                    Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            mIsBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mBoundService = null
            var toast = Toast.makeText(this@MainActivity, "Disconnected!",
                    Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            mIsBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("Activity", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupButtons()
    }

    override fun onStart() {
        Log.e("Activity", "onStart")
        super.onStart()

        doBindService()
    }

    fun doBindService() {
        Log.e("Activity", "doBindService")
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        var toast = Toast.makeText(this@MainActivity, "doBindService!",
                Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
        val intent = Intent(this, RobotProviderService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    fun doUnbindService() {
        var toast = Toast.makeText(this@MainActivity, "doUnbindService!",
                Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection)
        }
    }

    override fun onStop() {
        doUnbindService()

        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRobotActions!!.setRobotToDefaultState()
        if (mp.isPlaying) {
            mp.stop()
            mp.release()
        }
    }

    fun handleRobotChangedState(robot: Robot, type: RobotChangedStateListener.RobotChangedStateNotificationType) {
        when (type) {
            RobotChangedStateListener.RobotChangedStateNotificationType.Online -> {
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

        mapButton(R.id.play_checkup, mRobotDances::timeForYourCheckupDance, R.raw.time_for_your_checkup)
        mapButton(R.id.play_daniel, mRobotDances::danielTigerDance, R.raw.daniel_tiger_theme)
        mapButton(R.id.play_sesame, mRobotDances::sesameStreetDance, R.raw.seasame_street_theme)
        mapButton(R.id.play_elmo, mRobotDances::elmosSongDance, R.raw.elmos_song)
        mapButton(R.id.play_spider, mRobotDances::itsyBitsySpiderDance, R.raw.itsy_bitsy_spider)
        mapButton(R.id.play_head, mRobotDances::headShouldersKneesToesDance, R.raw.head_shoulders_knees_toes)
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

    private fun triggerSong(song: () -> MacroObject, resid: Int) {
        if (!mp.isPlaying) {
            mp = MediaPlayer.create(applicationContext, resid)
            mp.start()
            if (mRobot != null && mRobot!!.isConnected) {
                mRobotActions!!.setRobotToDefaultState()
                val macro = song()
                macro.setRobot(mRobot!!.robot)
                macro.playMacro()
            }
        } else if (clicks.containsKey(resid) && clicks[resid]!! >= clicksToStop) {
            mRobotActions!!.setRobotToDefaultState()
            mp.stop()
            clicks.put(resid, 0)
        } else {
            recordClick(resid)
        }
    }

    private fun mapButton(button: Int, dance: () -> MacroObject, song: Int)
    {
        val playCheckup = findViewById(button) as ImageButton
        playCheckup.setOnClickListener { triggerSong(dance, song) }
    }
}