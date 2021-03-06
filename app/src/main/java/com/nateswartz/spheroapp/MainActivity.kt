package com.nateswartz.spheroapp

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import com.orbotix.macro.MacroObject
import java.util.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView

class MainActivity : BaseRobotActivity() {

    private var robotActions = RobotActions()
    private var robotDances = RobotDances()
    private val clicks = HashMap<Int, Int>()
    private var mp = MediaPlayer()
    private val clicksToStop = 0

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // User chose the "Settings" item, show the app settings UI...
                Log.i(TAG, "Menu clicked")
                val intent = Intent(this, RobotStatsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_macros -> {
                // User chose the "Settings" item, show the app settings UI...
                Log.i(TAG, "Menu clicked")
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
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActivity()
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        val intent = Intent(this@MainActivity, BluetoothControllerService::class.java)
        bindService(intent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE)
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        if (isRobotServiceBound) {
            unbindService(robotServiceConnection)
        }
        if (isBluetoothServiceBound) {
            unbindService(bluetoothServiceConnection)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        if (mp.isPlaying) {
            mp.stop()
            mp.release()
        }
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

    override fun setupRobotItems() {
    }

    override fun disableRobotItems() {
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
            if (robot?.isConnected == true) {
                robotActions.setRobotToDefaultState(robot!!, this)
                val macro = song()
                macro.setRobot(robot!!.robot)
                macro.playMacro()
            }
        } else if (clicks.containsKey(resid) && clicks[resid]!! >= clicksToStop) {
            if (robot?.isConnected == true) robotActions.setRobotToDefaultState(robot!!, this)
            mp.stop()
            clicks[resid] = 0
        } else {
            recordClick(resid)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}