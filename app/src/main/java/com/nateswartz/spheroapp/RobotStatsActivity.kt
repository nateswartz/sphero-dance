package com.nateswartz.spheroapp

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.orbotix.ConvenienceRobot
import com.orbotix.async.AsyncMessage
import com.orbotix.async.DeviceSensorAsyncMessage
import com.orbotix.common.ResponseListener
import com.orbotix.common.Robot
import com.orbotix.common.RobotChangedStateListener
import com.orbotix.response.DeviceResponse
import com.orbotix.subsystem.SensorControl
import kotlinx.android.synthetic.main.activity_robot_stats.*
import com.orbotix.common.sensor.*
import android.widget.ArrayAdapter
import android.widget.Toolbar
import com.orbotix.command.GetPowerStateCommand
import com.orbotix.response.GetPowerStateResponse
import android.view.MenuItem


class RobotStatsActivity : Activity(), RobotServiceListener, ResponseListener {

    private var mBoundService: RobotProviderService? = null
    private var mBoundBluetoothService: BluetoothControllerService? = null
    private var mRobot: ConvenienceRobot? = null
    private var mIsBound = false
    private var mIsBluetoothBound = false

    private val dataFormat = arrayListOf("X Accel: %.4f",
                                         "Y Accel: %.4f",
                                         "Z Accel: %.4f",
                                         "Roll: %3d°",
                                         "Pitch: %3d°",
                                         "Yaw: %3d°",
                                         "Charges: %s",
                                         "Battery: %s",
                                         "Seconds Since Charge: %s")

    private val dataBinding = Array(9) {_ -> ""}
    private lateinit var dataAdapter: ArrayAdapter<String>

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e("StatsActivity","onServiceConnected")
            mBoundService = (service as RobotProviderService.RobotBinder).service
            mIsBound = true
            mBoundService?.addListener(this@RobotStatsActivity)
            if (mBoundService?.hasActiveRobot() == true) {
                handleRobotChange(mBoundService!!.getRobot(), RobotChangedStateListener.RobotChangedStateNotificationType.Online)
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e("StatsActivity","onServiceDisconnected")
            mBoundService = null
            mIsBound = false
            mBoundService?.removeListener(this@RobotStatsActivity)
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
        Log.e("StatsActivity", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_stats)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setActionBar(myToolbar)

        // Get an ActionBar corresponding to this toolbar
        val ab = actionBar

        // Enable the Up button
        ab!!.setDisplayHomeAsUpEnabled(true)

        dataAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, dataBinding)
        list_view_data.adapter = dataAdapter
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

    override fun onStart() {
        Log.e("StatsActivity", "onStart")
        super.onStart()

        val intent = Intent(this, RobotProviderService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)

        val btIntent = Intent(this, BluetoothControllerService::class.java)
        bindService(btIntent, mBluetoothConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.e("StatsActivity", "onStop")
        if (mIsBound) {
            unbindService(mConnection)
        }
        if (mIsBluetoothBound) {
            unbindService(mBluetoothConnection)
        }
        super.onStop()
    }

    override fun onDestroy() {
        Log.e("StatsActivity", "onDestroy")
        super.onDestroy()
    }

    override fun handleRobotChange(robot: ConvenienceRobot, type: RobotChangedStateListener.RobotChangedStateNotificationType) {
        when (type) {
            RobotChangedStateListener.RobotChangedStateNotificationType.Online -> {
                Log.e("StatsActivity", "handleRobotConnected")
                mRobot = robot
                val sensorFlag = SensorFlag(SensorFlag.SENSOR_FLAG_QUATERNION,
                                            SensorFlag.SENSOR_FLAG_ACCELEROMETER_NORMALIZED,
                                            SensorFlag.SENSOR_FLAG_GYRO_NORMALIZED,
                                            SensorFlag.SENSOR_FLAG_MOTOR_BACKEMF_NORMALIZED,
                                            SensorFlag.SENSOR_FLAG_ATTITUDE)
                mRobot!!.enableSensors(sensorFlag, SensorControl.StreamingRate.STREAMING_RATE1)
                mRobot!!.enableStabilization(false)
                mRobot!!.addResponseListener(this)
                mRobot!!.sendCommand(GetPowerStateCommand())
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Offline -> {
                Log.e("StatsActivity", "handleRobotDisconnected")
                mRobot = null
            }
        }
    }

    override fun handleResponse(response: DeviceResponse?, robot: Robot?) {
        if (response is GetPowerStateResponse)
        {
            val powerStateText = when (response.powerState) {
                1 -> "Charging"
                2 -> "OK"
                3 -> "Low"
                4 -> "Critical"
                else -> "Unknown"
            }
            dataBinding[6] = String.format(dataFormat[6], response.numberOfCharges)
            dataBinding[7] = String.format(dataFormat[7], powerStateText)
            dataBinding[8] = String.format(dataFormat[8], response.timeSinceLastCharge)
            dataAdapter.notifyDataSetChanged()
        }
    }

    override fun handleStringResponse(stringResponse: String?, robot: Robot?) {
    }

    override fun handleAsyncMessage(asyncMessage: AsyncMessage?, robot: Robot?) {
        if (asyncMessage == null)
            return

        //Check the asyncMessage type to see if it is a DeviceSensor message
        if (asyncMessage is DeviceSensorAsyncMessage) {

            if (asyncMessage.sensorDataFrames == null
                    || asyncMessage.sensorDataFrames.isEmpty()
                    || asyncMessage.sensorDataFrames[0] == null)
                return

            //Retrieve DeviceSensorsData from the async message
            val data = asyncMessage.sensorDataFrames[0]

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

        dataBinding[0] = String.format(dataFormat[0], accelerometer.filteredAcceleration.x)
        dataBinding[1] = String.format(dataFormat[1], accelerometer.filteredAcceleration.y)
        dataBinding[2] = String.format(dataFormat[2], accelerometer.filteredAcceleration.z)
        dataAdapter.notifyDataSetChanged()
    }

    private fun displayAttitude(attitude: AttitudeSensor?) {
        if (attitude == null)
            return

        dataBinding[3] = String.format(dataFormat[3], attitude.roll)
        dataBinding[4] = String.format(dataFormat[4], attitude.pitch)
        dataBinding[5] = String.format(dataFormat[5], attitude.yaw)
        dataAdapter.notifyDataSetChanged()
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
}