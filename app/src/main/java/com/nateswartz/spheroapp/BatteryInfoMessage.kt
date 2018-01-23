package com.nateswartz.spheroapp

import com.orbotix.response.DeviceResponse

/**
 * Created by nates on 1/22/2018.
 */

val RESPONSE_CODE_BATTERY_INFO = 32

class BatteryInfoMessage(response: DeviceResponse) {
    val powerState = when (response.data[1].toInt()) {
        1 -> "Charging"
        2 -> "OK"
        3 -> "Low"
        4 -> "Critical"
        else -> "Unknown"
    }
    val batteryVoltage = bytesToInt(response.data[2], response.data[3])
    val lifetimeCharges = bytesToInt(response.data[4], response.data[5])
    val secondsAwake = bytesToInt(response.data[6], response.data[7])

    private fun bytesToInt(msb : Byte, lsb : Byte) : Int {
        return ((msb.toInt() and 0xff) shl 8) or (lsb.toInt() and 0xff)
    }
}