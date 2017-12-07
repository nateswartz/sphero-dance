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
import android.widget.Button
import com.orbotix.ConvenienceRobot
import com.orbotix.common.RobotChangedStateListener
import com.orbotix.macro.MacroObject


class RobotMacrosActivity : AppCompatActivity(), RobotServiceListener {

    private var mBoundService: RobotProviderService? = null
    private var mBoundBluetoothService: BluetoothControllerService? = null
    private var mRobotActions = RobotActions()
    private var mRobot: ConvenienceRobot? = null
    private var mp = MediaPlayer()
    private var mIsBound = false
    private var mIsBluetoothBound = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e("MacrosActivity","onServiceConnected")
            mBoundService = (service as RobotProviderService.RobotBinder).service
            mIsBound = true
            mBoundService?.addListener(this@RobotMacrosActivity)
            if (mBoundService?.hasActiveRobot() == true) {
                handleRobotChange(mBoundService!!.getRobot(), RobotChangedStateListener.RobotChangedStateNotificationType.Online)
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e("MacrosActivity","onServiceDisconnected")
            mBoundService = null
            mIsBound = false
            mBoundService?.removeListener(this@RobotMacrosActivity)
        }
    }

    private val mBluetoothConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e("Activity","onServiceConnected")
            mBoundBluetoothService = (service as BluetoothControllerService.BluetoothBinder).service
            mIsBluetoothBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e("Activity","onServiceDisconnected")
            mBoundBluetoothService = null
            mIsBluetoothBound = false
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

        val btIntent = Intent(this, BluetoothControllerService::class.java)
        bindService(btIntent, mBluetoothConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.e("MacrosActivity", "onStop")
        if (mIsBound) {
            unbindService(mConnection)
        }
        if (mIsBluetoothBound) {
            unbindService(mBluetoothConnection)
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

    override fun handleRobotChange(robot: ConvenienceRobot, type: RobotChangedStateListener.RobotChangedStateNotificationType) {
        when (type) {
            RobotChangedStateListener.RobotChangedStateNotificationType.Online -> {
                Log.e("MacrosActivity", "handleRobotConnected")
                mRobot = robot
                enableButtons()
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Offline -> {
                Log.e("MacrosActivity", "handleRobotDisconnected")
                mRobot = null
                disableButtons()
            }
        }
    }

    private fun setupButtons() {
        val runMacroButton = findViewById(R.id.shake_button) as Button
        runMacroButton.setOnClickListener { triggerMacro(mRobotActions::shake) }

        val spinButton = findViewById(R.id.spin_button) as Button
        spinButton.setOnClickListener { triggerMacro(mRobotActions::spin) }

        val changeColorsButton = findViewById(R.id.change_colors_button) as Button
        changeColorsButton.setOnClickListener { triggerMacro(mRobotActions::changeColors) }

        val figureEightButton = findViewById(R.id.figure_eight_button) as Button
        figureEightButton.setOnClickListener { triggerMacro(mRobotActions::figureEight) }

        if (mRobot?.isConnected == true) {
            enableButtons()
        } else {
            disableButtons()
        }
    }

    private fun triggerMacro(actionProvider: () -> MacroObject) {
        if (mRobot?.isConnected == true) {
            mRobotActions.setRobotToDefaultState(mRobot!!)
            val macro = actionProvider()
            macro.setRobot(mRobot!!.robot)
            macro.playMacro()
        }
    }

    private fun enableButtons() {
        val shakeButton = findViewById(R.id.shake_button) as Button
        val spinButton = findViewById(R.id.spin_button) as Button
        val changeColorsButton = findViewById(R.id.change_colors_button) as Button
        val figureEightButton = findViewById(R.id.figure_eight_button) as Button
        shakeButton.isEnabled = true
        spinButton.isEnabled = true
        changeColorsButton.isEnabled = true
        figureEightButton.isEnabled = true
    }

    private fun disableButtons() {
        val shakeButton = findViewById(R.id.shake_button) as Button
        val spinButton = findViewById(R.id.spin_button) as Button
        val changeColorsButton = findViewById(R.id.change_colors_button) as Button
        val figureEightButton = findViewById(R.id.figure_eight_button) as Button
        shakeButton.isEnabled = false
        spinButton.isEnabled = false
        changeColorsButton.isEnabled = false
        figureEightButton.isEnabled = false
    }
}