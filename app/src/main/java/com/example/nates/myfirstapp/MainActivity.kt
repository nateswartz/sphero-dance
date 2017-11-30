package com.example.nates.myfirstapp

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.orbotix.ConvenienceRobot
import com.orbotix.macro.MacroObject
import java.util.*


class MainActivity : Activity(), RobotServiceListener {

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
            mBoundService?.setCallbacks(this@MainActivity)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mBoundService = null
            var toast = Toast.makeText(this@MainActivity, "Disconnected!",
                    Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            mIsBound = false
            mBoundService?.removeCallbacks()
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

        val intent = Intent(this, RobotProviderService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.e("Activity", "onStop")
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection)
        }

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

    override fun handleRobotConnected(robot : ConvenienceRobot) {
        Log.e("Activity", "handleRobotConnected")
        mRobot = robot
        mRobotActions = RobotActions(mRobot!!)
        val runMacroButton = findViewById(R.id.run_macro) as Button
        val spinButton = findViewById(R.id.spin_button) as Button
        runMacroButton.isEnabled = true
        spinButton.isEnabled = true
    }

    override fun handleRobotDisconnected() {
        Log.e("Activity", "handleRobotDisconnected")
        mRobot = null
        val runMacroButton = findViewById(R.id.run_macro) as Button
        val spinButton = findViewById(R.id.spin_button) as Button
        runMacroButton.isEnabled = false
        spinButton.isEnabled = false
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