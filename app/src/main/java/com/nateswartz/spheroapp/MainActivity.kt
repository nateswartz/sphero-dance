package com.nateswartz.spheroapp

import android.app.Activity
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import com.orbotix.common.RobotChangedStateListener
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView

class MainActivity : Activity(), RobotServiceListener, BluetoothServiceListener {

    private var robotActions = RobotActions()
    private var robotDances = RobotDances()
    private var mRobot: ConvenienceRobot? = null
    private val clicks = HashMap<Int, Int>()
    private var mp = MediaPlayer()
    private val clicksToStop = 0
    private var isRobotServiceBound = false
    private var isBluetoothServiceBound = false
    private var robotAlreadyConnected = false

    private val bluetoothServiceConnection = object : ServiceConnection {
        private var boundBluetoothService: BluetoothControllerService? = null

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e("Activity","onServiceConnected")
            boundBluetoothService = (service as BluetoothControllerService.BluetoothBinder).service
            isBluetoothServiceBound = true
            boundBluetoothService?.addListener(this@MainActivity)
            if (boundBluetoothService?.hasActiveBluetooth() == true) {
                handleBluetoothChange(1)
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e("Activity","onServiceDisconnected")
            boundBluetoothService = null
            isBluetoothServiceBound = false
            boundBluetoothService?.removeListener(this@MainActivity)
        }
    }

    private val robotServiceConnection = object : ServiceConnection {
        private var boundService: RobotProviderService? = null

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e("Activity","onServiceConnected")
            boundService = (service as RobotProviderService.RobotBinder).service
            isRobotServiceBound = true
            boundService?.addListener(this@MainActivity)
            if (boundService?.hasActiveRobot() == true) {
                robotAlreadyConnected = true
                handleRobotChange(boundService!!.getRobot(), RobotChangedStateListener.RobotChangedStateNotificationType.Online)
            } else {
                val toast = Toast.makeText(this@MainActivity, "Discovering...",
                        Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 10)
                toast.show()
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e("Activity","onServiceDisconnected")
            boundService = null
            isRobotServiceBound = false
            boundService?.removeListener(this@MainActivity)
        }
    }

/*    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.action_settings)
        item.isVisible = mRobot != null
        item.isEnabled = mRobot != null
        val secondItem = menu.findItem(R.id.action_macros)
        secondItem.isVisible = mRobot != null
        secondItem.isEnabled = mRobot != null
        return true
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // User chose the "Settings" item, show the app settings UI...
                Log.e("Activity", "Menu clicked")
                val intent = Intent(this, RobotStatsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_macros -> {
                // User chose the "Settings" item, show the app settings UI...
                Log.e("Activity", "Menu clicked")
                val intent = Intent(this, RobotMacrosActivity::class.java)
                startActivity(intent)
                true
            }
            else ->
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("Activity", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActivity()
    }

    private fun setupActivity()
    {
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setActionBar(myToolbar)

        val gridview = findViewById<View>(R.id.gridview) as GridView
        gridview.adapter = ImageAdapter(this)

        gridview.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
            when ((parent.adapter as ImageAdapter).imgIds[position]) {
                R.drawable.grid_docmcstuffins -> triggerSong(robotDances::timeForYourCheckupDance, R.raw.time_for_your_checkup)
                R.drawable.grid_daniel_tiger -> triggerSong(robotDances::danielTigerDance, R.raw.daniel_tiger_theme)
                R.drawable.grid_sesame_street -> triggerSong(robotDances::sesameStreetDance, R.raw.seasame_street_theme)
                R.drawable.grid_elmos_song -> triggerSong(robotDances::elmosSongDance, R.raw.elmos_song)
                R.drawable.grid_itsybitsyspider -> triggerSong(robotDances::itsyBitsySpiderDance, R.raw.itsy_bitsy_spider)
                R.drawable.grid_head_shoulders_knees_toes -> triggerSong(robotDances::headShouldersKneesToesDance, R.raw.head_shoulders_knees_toes)
                R.drawable.grid_cookie_monster -> triggerSong(robotDances::cookieDance, R.raw.c_is_for_cookie)
                R.drawable.grid_rubber_ducky -> triggerSong(robotDances::rubberDuckieDance, R.raw.rubber_duckie)
            }
        }
    }

    override fun onStart() {
        Log.e("Activity", "onStart")
        val intent = Intent(this@MainActivity, BluetoothControllerService::class.java)
        bindService(intent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE)
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        Log.e("Activity", "onStop")
        if (isRobotServiceBound) {
            unbindService(robotServiceConnection)
        }
        if (isBluetoothServiceBound) {
            unbindService(bluetoothServiceConnection)
        }
    }

    override fun onDestroy() {
        Log.e("Activity", "onDestroy")
        super.onDestroy()
        if (mp.isPlaying) {
            mp.stop()
            mp.release()
        }
    }

    override fun handleBluetoothChange(type: Int) {
        val intent = Intent(this@MainActivity, RobotProviderService::class.java)
        bindService(intent, robotServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun handleRobotChange(robot: ConvenienceRobot, type: RobotChangedStateListener.RobotChangedStateNotificationType) {
        when (type) {
            RobotChangedStateListener.RobotChangedStateNotificationType.Online -> {
                if (!robotAlreadyConnected) {
                    Log.e("Activity", "handleRobotConnected")
                    val toast = Toast.makeText(this@MainActivity, "Connected!",
                            Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.BOTTOM, 0, 10)
                    toast.show()
                }
                robotAlreadyConnected = false
                isRobotServiceBound = true
                mRobot = robot
                val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                val savedRedValue = sharedPref.getInt(getString(R.string.saved_red_value), -1)
                val savedGreenValue = sharedPref.getInt(getString(R.string.saved_green_value), -1)
                val savedBlueValue = sharedPref.getInt(getString(R.string.saved_blue_value), -1)

                if (savedRedValue != 1) {
                    mRobot?.setLed(savedRedValue.toFloat() / 255, savedGreenValue.toFloat() / 255, savedBlueValue.toFloat() / 255 )
                }
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Offline -> {
                Log.e("Activity", "handleRobotDisconnected")
                mRobot = null
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Connecting -> {
                Log.e("Activity", "handleRobotConnecting")
                val toast = Toast.makeText(this@MainActivity, "Connecting..",
                        Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 10)
                toast.show()
            }
            else -> {
            }
        }
    }

    private fun recordClick(buttonId: Int) {
        var keyFound = false
        for (key in clicks.keys) {
            if (key == buttonId) {
                clicks[key] = clicks[key]!! + 1
                keyFound = true
            } else {
                clicks[key] = 0
            }
        }

        if (!keyFound) {
            clicks[buttonId] = 0
        }
    }

    private fun triggerSong(song: () -> MacroObject, resid: Int) {
        if (!mp.isPlaying) {
            mp = MediaPlayer.create(applicationContext, resid)
            mp.start()
            if (mRobot?.isConnected == true) {
                robotActions.setRobotToDefaultState(mRobot!!, this)
                val macro = song()
                macro.setRobot(mRobot!!.robot)
                macro.playMacro()
            }
        } else if (clicks.containsKey(resid) && clicks[resid]!! >= clicksToStop) {
            if (mRobot?.isConnected == true) robotActions.setRobotToDefaultState(mRobot!!, this)
            mp.stop()
            clicks[resid] = 0
        } else {
            recordClick(resid)
        }
    }
}