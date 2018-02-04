package com.nateswartz.spheroapp

import android.content.Context
import android.content.Intent
import android.graphics.Color.rgb
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toolbar
import com.orbotix.macro.MacroObject
import kotlinx.android.synthetic.main.activity_robot_macros.*

class RobotMacrosActivity : BaseRobotActivity() {

    private var TAG = "RobotMacrosActivity"

    private var mRobotActions = RobotActions()

    private var redValue = 100
    private var greenValue = 100
    private var blueValue = 100

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
            val sharedPref = this@RobotMacrosActivity.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putInt(getString(R.string.saved_red_value), redValue)
                putInt(getString(R.string.saved_green_value), greenValue)
                putInt(getString(R.string.saved_blue_value), blueValue)
                commit()
            }
            mRobot?.setLed(redValue.toFloat() / 255, greenValue.toFloat() / 255, blueValue.toFloat() / 255)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            // called when the user first touches the SeekBar
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            // called after the user finishes moving the SeekBar
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_macros)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setActionBar(myToolbar)

        // Get a support ActionBar corresponding to this toolbar
        val ab = actionBar

        // Enable the Up button
        ab!!.setDisplayHomeAsUpEnabled(true)

        setupButtons()
    }

    override fun onStart() {
        Log.e(TAG, "onStart")
        super.onStart()

        val btIntent = Intent(this, BluetoothControllerService::class.java)
        bindService(btIntent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.e(TAG, "onStop")
        if (isRobotServiceBound) {
            unbindService(robotServiceConnection)
        }
        if (isBluetoothServiceBound) {
            unbindService(bluetoothServiceConnection)
        }
        super.onStop()
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun setupRobotItems() {
        button_shake.isEnabled = true
        button_spin.isEnabled = true
        button_change_colors.isEnabled = true
        button_figure_eight.isEnabled = true
        seekBar_Red.isEnabled = true
        seekBar_Green.isEnabled = true
        seekBar_Blue.isEnabled = true
    }

    override fun disableRobotItems() {
        button_shake.isEnabled = false
        button_spin.isEnabled = false
        button_change_colors.isEnabled = false
        button_figure_eight.isEnabled = false
        seekBar_Red.isEnabled = false
        seekBar_Green.isEnabled = false
        seekBar_Blue.isEnabled = false
    }

    private fun setupButtons() {
        // set a change listener on the SeekBar
        val redSeekBar = findViewById<SeekBar>(R.id.seekBar_Red)
        val greenSeekBar = findViewById<SeekBar>(R.id.seekBar_Green)
        val blueSeekBar = findViewById<SeekBar>(R.id.seekBar_Blue)
        redSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        greenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        blueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)

        button_shake.setOnClickListener { triggerMacro(mRobotActions::shake) }
        button_spin.setOnClickListener { triggerMacro(mRobotActions::spin) }
        button_change_colors.setOnClickListener { triggerMacro(mRobotActions::changeColors) }
        button_figure_eight.setOnClickListener { triggerMacro(mRobotActions::figureEight) }

        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val savedRedValue = sharedPref.getInt(getString(R.string.saved_red_value), -1)
        val savedGreenValue = sharedPref.getInt(getString(R.string.saved_green_value), -1)
        val savedBlueValue = sharedPref.getInt(getString(R.string.saved_blue_value), -1)

        if (savedRedValue != -1) {
            val redBar = findViewById<SeekBar>(R.id.seekBar_Red)
            val greenBar = findViewById<SeekBar>(R.id.seekBar_Green)
            val blueBar = findViewById<SeekBar>(R.id.seekBar_Blue)
            redBar.progress = savedRedValue
            greenBar.progress = savedGreenValue
            blueBar.progress = savedBlueValue
            redBar.refreshDrawableState()
            greenBar.refreshDrawableState()
            blueBar.refreshDrawableState()
        }

    }

    private fun triggerMacro(actionProvider: () -> MacroObject) {
        if (mRobot?.isConnected == true) {
            mRobotActions.setRobotToDefaultState(mRobot!!, this)
            val macro = actionProvider()
            macro.setRobot(mRobot!!.robot)
            macro.playMacro()
        }
    }

}