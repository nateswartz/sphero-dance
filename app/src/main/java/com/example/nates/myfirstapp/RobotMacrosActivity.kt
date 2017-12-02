package com.example.nates.myfirstapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import com.orbotix.ConvenienceRobot


class RobotMacrosActivity : AppCompatActivity(), RobotServiceListener {

    private var mBoundService: RobotProviderService? = null
    private var mRobotActions: RobotActions? = null
    private var mRobot: ConvenienceRobot? = null
    private var mp = MediaPlayer()
    private var mIsBound = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e("MacrosActivity","onServiceConnected")
            mBoundService = (service as RobotProviderService.RobotBinder).service
            mIsBound = true
            mBoundService?.addListener(this@RobotMacrosActivity)
            if (mBoundService?.hasActiveRobot() == true) {
                handleRobotConnected(mBoundService!!.getRobot())
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e("MacrosActivity","onServiceDisconnected")
            mBoundService = null
            mIsBound = false
            mBoundService?.removeListener(this@RobotMacrosActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("MacrosActivity", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_macros)
        setupButtons()
    }

    override fun onStart() {
        Log.e("MacrosActivity", "onStart")
        super.onStart()

        val intent = Intent(this, RobotProviderService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.e("MacrosActivity", "onStop")
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection)
        }
        super.onStop()
    }

    override fun onDestroy() {
        Log.e("MacrosActivity", "onDestroy")
        super.onDestroy()
        if (mp.isPlaying) {
            mp.stop()
            mp.release()
        }
    }

    override fun handleRobotConnected(robot : ConvenienceRobot) {
        Log.e("MacrosActivity", "handleRobotConnected")
        var toast = Toast.makeText(this@RobotMacrosActivity, "Connected!",
                Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
        mRobot = robot
        mRobotActions = RobotActions(mRobot!!)
        val runMacroButton = findViewById(R.id.run_macro) as Button
        val spinButton = findViewById(R.id.spin_button) as Button
        runMacroButton.isEnabled = true
        spinButton.isEnabled = true
    }

    override fun handleRobotDisconnected() {
        Log.e("MacrosActivity", "handleRobotDisconnected")
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

        if (mRobot?.isConnected == true) {
            runMacroButton.isEnabled = true
            spinButton.isEnabled = true
        } else {
            runMacroButton.isEnabled = false
            spinButton.isEnabled = false
        }
    }

}