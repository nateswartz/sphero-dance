package com.nateswartz.spheroapp

import android.app.Activity
import android.app.ActivityOptions
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
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Toolbar
import com.orbotix.common.RobotChangedStateListener
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView




class MainActivity : Activity(), RobotServiceListener, BluetoothServiceListener {

    private var mBoundService: RobotProviderService? = null
    private var mBoundBluetoothService: BluetoothControllerService? = null
    private var mRobotActions = RobotActions()
    private var mRobotDances = RobotDances()
    private var mRobot: ConvenienceRobot? = null
    private val clicks = HashMap<Int, Int>()
    private var mp = MediaPlayer()
    private val clicksToStop = 0
    private var mIsBound = false
    private var mIsBluetoothBound = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e("Activity","onServiceConnected")
            mBoundService = (service as RobotProviderService.RobotBinder).service
            mIsBound = true
            mBoundService?.addListener(this@MainActivity)
            if (mBoundService?.hasActiveRobot() == true) {
                handleRobotAlreadyConnected(mBoundService!!.getRobot())
            } else {
                val toast = Toast.makeText(this@MainActivity, "Discovering...",
                        Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 10)
                toast.show()
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e("Activity","onServiceDisconnected")
            mBoundService = null
            mIsBound = false
            mBoundService?.removeListener(this@MainActivity)
        }
    }

    private val mBluetoothConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.e("Activity","onServiceConnected")
            mBoundBluetoothService = (service as BluetoothControllerService.BluetoothBinder).service
            mIsBluetoothBound = true
            mBoundBluetoothService?.addListener(this@MainActivity)
            if (mBoundBluetoothService?.hasActiveBluetooth() == true) {
                handleBluetoothChange(1)
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e("Activity","onServiceDisconnected")
            mBoundBluetoothService = null
            mIsBluetoothBound = false
            mBoundBluetoothService?.removeListener(this@MainActivity)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.action_settings)
        item.isVisible = mRobot != null
        item.isEnabled = mRobot != null
        val secondItem = menu.findItem(R.id.action_macros)
        secondItem.isVisible = mRobot != null
        secondItem.isEnabled = mRobot != null
        return true
    }

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
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setActionBar(myToolbar)


        val gridview = findViewById<View>(R.id.gridview) as GridView
        gridview.adapter = ImageAdapter(this)

        gridview.onItemClickListener = OnItemClickListener { parent, _, position, _ ->
            when ((parent.adapter as ImageAdapter).imgIds[position]) {
                R.drawable.grid_docmcstuffins -> triggerSong(mRobotDances::timeForYourCheckupDance, R.raw.time_for_your_checkup)
                R.drawable.grid_daniel_tiger -> triggerSong(mRobotDances::danielTigerDance, R.raw.daniel_tiger_theme)
                R.drawable.grid_sesame_street -> triggerSong(mRobotDances::sesameStreetDance, R.raw.seasame_street_theme)
                R.drawable.grid_elmos_song -> triggerSong(mRobotDances::elmosSongDance, R.raw.elmos_song)
                R.drawable.grid_itsybitsyspider -> triggerSong(mRobotDances::itsyBitsySpiderDance, R.raw.itsy_bitsy_spider)
                R.drawable.grid_head_shoulders_knees_toes -> triggerSong(mRobotDances::headShouldersKneesToesDance, R.raw.head_shoulders_knees_toes)
                R.drawable.grid_cookie_monster -> triggerSong(mRobotDances::cookieDance, R.raw.c_is_for_cookie)
                R.drawable.grid_rubber_ducky -> triggerSong(mRobotDances::rubberDuckieDance, R.raw.rubber_duckie)
            }
        }

        setupButtons()
    }

    override fun onStart() {
        Log.e("Activity", "onStart")
        val intent = Intent(this@MainActivity, BluetoothControllerService::class.java)
        bindService(intent, mBluetoothConnection, Context.BIND_AUTO_CREATE)
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        Log.e("Activity", "onStop")
        if (mIsBound) {
            unbindService(mConnection)
        }
        if (mIsBluetoothBound) {
            unbindService(mBluetoothConnection)
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
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    fun handleRobotAlreadyConnected(robot: ConvenienceRobot) {
        mRobot = robot
        invalidateOptionsMenu()
    }

    override fun handleRobotChange(robot: ConvenienceRobot, type: RobotChangedStateListener.RobotChangedStateNotificationType) {
        when (type) {
            RobotChangedStateListener.RobotChangedStateNotificationType.Online -> {
                Log.e("Activity", "handleRobotConnected")
                val toast = Toast.makeText(this@MainActivity, "Connected!",
                        Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 10)
                toast.show()
                handleRobotAlreadyConnected(robot)
            }
            RobotChangedStateListener.RobotChangedStateNotificationType.Offline -> {
                Log.e("Activity", "handleRobotDisconnected")
                mRobot = null
                invalidateOptionsMenu()
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
        }    }

    private fun setupButtons() {
        invalidateOptionsMenu()

        mapButton(R.id.play_checkup, mRobotDances::timeForYourCheckupDance, R.raw.time_for_your_checkup)
        mapButton(R.id.play_daniel, mRobotDances::danielTigerDance, R.raw.daniel_tiger_theme)
        mapButton(R.id.play_sesame, mRobotDances::sesameStreetDance, R.raw.seasame_street_theme)
        mapButton(R.id.play_elmo, mRobotDances::elmosSongDance, R.raw.elmos_song)
        mapButton(R.id.play_spider, mRobotDances::itsyBitsySpiderDance, R.raw.itsy_bitsy_spider)
        mapButton(R.id.play_head, mRobotDances::headShouldersKneesToesDance, R.raw.head_shoulders_knees_toes)
        mapButton(R.id.play_cookie, mRobotDances::cookieDance, R.raw.c_is_for_cookie)
        mapButton(R.id.play_rubber_ducky, mRobotDances::rubberDuckieDance, R.raw.rubber_duckie)
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
                mRobotActions.setRobotToDefaultState(mRobot!!)
                val macro = song()
                macro.setRobot(mRobot!!.robot)
                macro.playMacro()
            }
        } else if (clicks.containsKey(resid) && clicks[resid]!! >= clicksToStop) {
            if (mRobot?.isConnected == true) mRobotActions.setRobotToDefaultState(mRobot!!)
            mp.stop()
            clicks[resid] = 0
        } else {
            recordClick(resid)
        }
    }

    private fun mapButton(button: Int, dance: () -> MacroObject, song: Int)
    {
        val playSong = findViewById<ImageButton>(button)
        playSong.setOnClickListener { triggerSong(dance, song) }
    }
}