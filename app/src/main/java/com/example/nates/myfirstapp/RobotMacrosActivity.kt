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
import com.orbotix.ConvenienceRobot
import com.orbotix.async.AsyncMessage
import com.orbotix.async.DeviceSensorAsyncMessage
import com.orbotix.common.ResponseListener
import com.orbotix.common.Robot
import com.orbotix.common.RobotChangedStateListener
import com.orbotix.macro.MacroObject
import com.orbotix.response.DeviceResponse
import com.orbotix.subsystem.SensorControl
import kotlinx.android.synthetic.main.activity_robot_macros.*
import android.widget.CompoundButton
import android.R.id.toggle
import com.orbotix.common.sensor.*


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
            displayAttitude(data.attitudeData)

            //Extract quaternion data from the sensor data
            displayQuaterions(data.quaternion)

            //Display back EMF data from left and right motors
            displayBackEMF(data.backEMFData.emfFiltered)

            //Extract gyroscope data from the sensor data
            displayGyroscope(data.gyroData)
        }
    }

    private fun displayBackEMF(sensor: BackEMFSensor?) {
        if (sensor == null)
            return

        //mLeftMotor.setText(sensor.leftMotorValue.toString())
       // mRightMotor.setText(sensor.rightMotorValue.toString())
    }

    private fun displayGyroscope(data: GyroData) {
        //mGyroX.setText(data.rotationRateFiltered.x.toString())
        //mGyroY.setText(data.rotationRateFiltered.y.toString())
        //mGyroZ.setText(data.rotationRateFiltered.z.toString())
    }

    private fun displayAccelerometer(accelerometer: AccelerometerData?) {
        if (accelerometer == null || accelerometer.filteredAcceleration == null) {
            return
        }

        //Display the readings from the X, Y and Z components of the accelerometer
        text_accel_x.setText(String.format("X Accel: %.4f", accelerometer.filteredAcceleration.x))
        text_accel_y.setText(String.format("Y Accel: %.4f", accelerometer.filteredAcceleration.y))
        text_accel_z.setText(String.format("Z Accel: %.4f", accelerometer.filteredAcceleration.z))
    }

    private fun displayAttitude(attitude: AttitudeSensor?) {
        if (attitude == null)
            return

        //Display the pitch, roll and yaw from the attitude sensor
        text_roll.setText(String.format("Roll: %3d", attitude.roll) + "°")
        text_pitch.setText(String.format("Pitch: %3d", attitude.pitch) + "°")
        text_yaw.setText(String.format("Yaw: %3d", attitude.yaw) + "°")
    }

    private fun displayQuaterions(quaternion: QuaternionSensor?) {
        if (quaternion == null)
            return

        //Display the four quaterions data
        //mQ0Value.setText(String.format("%.5f", quaternion.getQ0()))
        //mQ1Value.setText(String.format("%.5f", quaternion.getQ1()))
        //mQ2Value.setText(String.format("%.5f", quaternion.getQ2()))
        //mQ3Value.setText(String.format("%.5f", quaternion.getQ3()))

    }

    private fun setupButtons() {
        button_shake.setOnClickListener { triggerMacro(mRobotActions::shake) }
        button_spin.setOnClickListener { triggerMacro(mRobotActions::spin) }
        button_change_colors.setOnClickListener { triggerMacro(mRobotActions::changeColors) }
        button_figure_eight.setOnClickListener { triggerMacro(mRobotActions::figureEight) }
        toggle_stabilization.setOnCheckedChangeListener({ _, isChecked ->
            mRobot?.enableStabilization(isChecked) })

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