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
import android.widget.TextView
import com.orbotix.ConvenienceRobot
import com.orbotix.async.AsyncMessage
import com.orbotix.async.DeviceSensorAsyncMessage
import com.orbotix.common.ResponseListener
import com.orbotix.common.Robot
import com.orbotix.common.RobotChangedStateListener
import com.orbotix.common.sensor.AccelerometerData
import com.orbotix.common.sensor.SensorFlag
import com.orbotix.macro.MacroObject
import com.orbotix.response.DeviceResponse
import com.orbotix.subsystem.SensorControl


class RobotMacrosActivity : AppCompatActivity(), RobotServiceListener, ResponseListener {

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

                val sensorFlag = SensorFlag(SensorFlag.SENSOR_FLAG_QUATERNION,
                                            SensorFlag.SENSOR_FLAG_ACCELEROMETER_NORMALIZED,
                                            SensorFlag.SENSOR_FLAG_GYRO_NORMALIZED,
                                            SensorFlag.SENSOR_FLAG_MOTOR_BACKEMF_NORMALIZED,
                                            SensorFlag.SENSOR_FLAG_ATTITUDE)
                mRobot!!.enableSensors(sensorFlag, SensorControl.StreamingRate.STREAMING_RATE1)
                mRobot!!.enableStabilization(false)
                mRobot!!.addResponseListener(this)
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Offline -> {
                Log.e("MacrosActivity", "handleRobotDisconnected")
                mRobot = null
                disableButtons()
            }
        }
    }

    override fun handleResponse(response: DeviceResponse?, robot: Robot?) {
    }

    override fun handleStringResponse(stringResponse: String?, robot: Robot?) {
    }

    override fun handleAsyncMessage(asyncMessage: AsyncMessage?, robot: Robot?) {
        if (asyncMessage == null)
            return

        //Check the asyncMessage type to see if it is a DeviceSensor message
        if (asyncMessage is DeviceSensorAsyncMessage) {
            val message = asyncMessage as DeviceSensorAsyncMessage

            if (message.sensorDataFrames == null
                    || message.sensorDataFrames.isEmpty()
                    || message.sensorDataFrames[0] == null)
                return

            //Retrieve DeviceSensorsData from the async message
            val data = message.sensorDataFrames[0]

            //Extract the accelerometer data from the sensor data
            displayAccelerometer(data.accelerometerData)

            //Extract attitude data (yaw, roll, pitch) from the sensor data
            //displayAttitude(data.attitudeData)

            //Extract quaternion data from the sensor data
            //displayQuaterions(data.quaternion)

            //Display back EMF data from left and right motors
            //displayBackEMF(data.backEMFData.emfFiltered)

            //Extract gyroscope data from the sensor data
            //displayGyroscope(data.gyroData)
        }
    }

    private fun displayAccelerometer(accelerometer: AccelerometerData?) {
        if (accelerometer == null || accelerometer.filteredAcceleration == null) {
            return
        }

        val mAccelX = findViewById(R.id.accel_x) as TextView
        val mAccelY = findViewById(R.id.accel_y) as TextView
        val mAccelZ = findViewById(R.id.accel_z) as TextView

        //Display the readings from the X, Y and Z components of the accelerometer
        mAccelX.setText(String.format("%.4f", accelerometer.filteredAcceleration.x))
        mAccelY.setText(String.format("%.4f", accelerometer.filteredAcceleration.y))
        mAccelZ.setText(String.format("%.4f", accelerometer.filteredAcceleration.z))
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
            mRobot!!.enableStabilization(true)
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