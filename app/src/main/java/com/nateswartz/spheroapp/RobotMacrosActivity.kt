package com.nateswartz.spheroapp

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color.rgb
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.*
import com.orbotix.ConvenienceRobot
import com.orbotix.common.RobotChangedStateListener
import com.orbotix.macro.MacroObject
import kotlinx.android.synthetic.main.activity_robot_macros.*

class RobotMacrosActivity : Activity(), RobotServiceListener {

    private var mBoundService: RobotProviderService? = null
    private var mBoundBluetoothService: BluetoothControllerService? = null
    private var mRobotActions = RobotActions()
    private var mRobot: ConvenienceRobot? = null
    private var mIsBound = false
    private var mIsBluetoothBound = false

    private var redValue = 100
    private var greenValue = 100
    private var blueValue = 100

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
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setActionBar(myToolbar)

        // Get a support ActionBar corresponding to this toolbar
        val ab = actionBar

        // Enable the Up button
        ab!!.setDisplayHomeAsUpEnabled(true)

        // set a change listener on the SeekBar
        val redSeekBar = findViewById<SeekBar>(R.id.seekBar_Red)
        val greenSeekBar = findViewById<SeekBar>(R.id.seekBar_Green)
        val blueSeekBar = findViewById<SeekBar>(R.id.seekBar_Blue)
        redSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        greenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        blueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)

        setupButtons()
    }

    private var seekBarChangeListener: SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            // updated continuously as the user slides the thumb
            when (seekBar.id) {
                R.id.seekBar_Red -> redValue = progress
                R.id.seekBar_Green -> greenValue = progress
                R.id.seekBar_Blue -> blueValue = progress
            }
            val color = ColorDrawable(rgb(redValue, greenValue, blueValue))
            findViewById<ImageView>(R.id.imageView_ColorPreview).setImageDrawable(color)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            // called when the user first touches the SeekBar
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            // called after the user finishes moving the SeekBar
        }
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
        button_shake.setOnClickListener { triggerMacro(mRobotActions::shake) }
        button_spin.setOnClickListener { triggerMacro(mRobotActions::spin) }
        button_change_colors.setOnClickListener { triggerMacro(mRobotActions::changeColors) }
        button_figure_eight.setOnClickListener { triggerMacro(mRobotActions::figureEight) }

        toggle_stabilization.setOnCheckedChangeListener({ _, isChecked ->
            mRobot?.enableStabilization(isChecked) })

        button_setColor.setOnClickListener {
            mRobot?.setLed(redValue.toFloat() / 255, greenValue.toFloat() / 255, blueValue.toFloat() / 255) }

        if (mRobot?.isConnected == true) {
            enableButtons()
        } else {
            disableButtons()
        }
    }

    private fun triggerMacro(actionProvider: () -> MacroObject) {
        if (mRobot?.isConnected == true) {
            mRobotActions.setRobotToDefaultState(mRobot!!)
            (toggle_stabilization as CompoundButton).isChecked = true
            val macro = actionProvider()
            macro.setRobot(mRobot!!.robot)
            macro.playMacro()
        }
    }

    private fun enableButtons() {
        button_shake.isEnabled = true
        button_spin.isEnabled = true
        button_change_colors.isEnabled = true
        button_figure_eight.isEnabled = true
        toggle_stabilization.isEnabled = true
    }

    private fun disableButtons() {
        button_shake.isEnabled = false
        button_spin.isEnabled = false
        button_change_colors.isEnabled = false
        button_figure_eight.isEnabled = false
        toggle_stabilization.isEnabled = false
    }
}