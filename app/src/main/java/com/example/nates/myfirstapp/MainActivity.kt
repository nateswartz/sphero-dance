package com.example.nates.myfirstapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import com.orbotix.ConvenienceRobot
import com.orbotix.macro.MacroObject
import java.util.*
import android.support.v7.app.AppCompatActivity
import android.widget.*


class MainActivity : AppCompatActivity(), RobotServiceListener {

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
            Log.e("Activity","onServiceConnected")
            var toast = Toast.makeText(this@MainActivity, "Connecting...",
                    Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            mBoundService = (service as RobotProviderService.RobotBinder).service
            mIsBound = true
            mBoundService?.addListener(this@MainActivity)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e("Activity","onServiceDisconnected")
            mBoundService = null
            mIsBound = false
            mBoundService?.removeListener(this@MainActivity)
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
        Log.e("Activity", "onDestroy")
        super.onDestroy()
        if (mp.isPlaying) {
            mp.stop()
            mp.release()
        }
    }

    override fun handleRobotConnected(robot : ConvenienceRobot) {
        Log.e("Activity", "handleRobotConnected")
        var toast = Toast.makeText(this@MainActivity, "Connected!",
                Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
        mRobot = robot
        mRobotActions = RobotActions(mRobot!!)
        val macrosActivityButton = findViewById(R.id.robot_macros) as Button
        macrosActivityButton.isEnabled = true
    }

    override fun handleRobotDisconnected() {
        Log.e("Activity", "handleRobotDisconnected")
        mRobot = null
        val macrosActivityButton = findViewById(R.id.robot_macros) as Button
        macrosActivityButton.isEnabled = false
    }

    private fun setupButtons() {
        val macrosActivityButton = findViewById(R.id.robot_macros) as Button
        macrosActivityButton.setOnClickListener {
            val intent = Intent(this, RobotMacrosActivity::class.java)
            startActivity(intent)
        }

        macrosActivityButton.isEnabled = false

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
            if (mRobot?.isConnected == true) {
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
        val playSong = findViewById(button) as ImageButton
        playSong.setOnClickListener { triggerSong(dance, song) }
    }
}