package com.nateswartz.spheroapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.orbotix.async.AsyncMessage
import com.orbotix.async.DeviceSensorAsyncMessage
import com.orbotix.common.ResponseListener
import com.orbotix.common.Robot
import com.orbotix.response.DeviceResponse
import com.orbotix.subsystem.SensorControl
import kotlinx.android.synthetic.main.activity_robot_stats.*
import com.orbotix.common.sensor.*
import android.widget.ArrayAdapter
import android.widget.Toolbar
import com.orbotix.command.GetPowerStateCommand
import com.orbotix.response.GetPowerStateResponse
import android.view.MenuItem


class RobotStatsActivity : BaseRobotActivity(), ResponseListener {

    private val dataBinding = Array(9) {_ -> ""}
    private lateinit var dataAdapter: ArrayAdapter<String>

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
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot_stats)
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setActionBar(myToolbar)

        // Enable the Up button
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        dataAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, dataBinding)
        list_view_data.adapter = dataAdapter

        setupButtons()
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()

        val btIntent = Intent(this, BluetoothControllerService::class.java)
        bindService(btIntent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        if (isRobotServiceBound) {
            unbindService(robotServiceConnection)
        }
        if (isBluetoothServiceBound) {
            unbindService(bluetoothServiceConnection)
        }
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun setupRobotItems()
    {
        toggle_stabilization.isEnabled = true
        val sensorFlag = SensorFlag(SensorFlag.SENSOR_FLAG_QUATERNION,
                SensorFlag.SENSOR_FLAG_ACCELEROMETER_NORMALIZED,
                SensorFlag.SENSOR_FLAG_GYRO_NORMALIZED,
                SensorFlag.SENSOR_FLAG_MOTOR_BACKEMF_NORMALIZED,
                SensorFlag.SENSOR_FLAG_ATTITUDE)
        robot!!.enableSensors(sensorFlag, SensorControl.StreamingRate.STREAMING_RATE1)
        robot!!.enableStabilization(false)
        robot!!.addResponseListener(this)
        robot!!.sendCommand(GetPowerStateCommand())
    }

    override fun disableRobotItems() {
        toggle_stabilization.isEnabled = false
    }

    private fun setupButtons() {
        toggle_stabilization.setOnCheckedChangeListener { _, isChecked ->
            robot?.enableStabilization(isChecked) }
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
            this.robot!!.sendCommand(GetPowerStateCommand())
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
        //mRightMotor.setText(sensor.rightMotorValue.toString())
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

    companion object {
        private const val TAG = "RobotStatsActivity"

        private val dataFormat = arrayListOf("X Accel: %.4f",
                "Y Accel: %.4f",
                "Z Accel: %.4f",
                "Roll: %3d°",
                "Pitch: %3d°",
                "Yaw: %3d°",
                "Charges: %s",
                "Battery: %s",
                "Seconds Since Charge: %s")
    }
}