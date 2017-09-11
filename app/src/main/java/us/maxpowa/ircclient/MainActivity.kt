package us.maxpowa.ircclient

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.SupportActivity
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.ircclouds.irc.api.domain.messages.AbstractMessage
import net.engio.mbassy.listener.Handler
import us.maxpowa.ircclient.activity.AddConnection
import us.maxpowa.ircclient.components.DrawerServerListItem
import us.maxpowa.ircclient.fragment.ConnectingFragment
import us.maxpowa.ircclient.fragment.PlaceholderFragment
import us.maxpowa.ircclient.utils.ConnectionManager
import us.maxpowa.ircclient.utils.CredentialStore
import java.util.logging.Level
import java.util.logging.Logger


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var DRAWER_ALT_MODE = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        setupFab()
        setupDrawer()
        setupFragment()
    }

    private fun setupFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = PlaceholderFragment()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun setupDrawer() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = object : ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(view: View) {
                if(DRAWER_ALT_MODE) {
                    DRAWER_ALT_MODE = false
                    refreshDrawerContent()
                }
                super.onDrawerClosed(view)
            }
        }
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.drawer_navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        val header = navigationView.getHeaderView(0)

        header.findViewById<LinearLayout>(R.id.drawer_header_container).setOnClickListener {
            DRAWER_ALT_MODE = !DRAWER_ALT_MODE
            refreshDrawerContent()
        }
    }

    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view -> launchAddConnectionActivity(view.context) }

        // Feature discovery callout for FAB
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val fabDiscovered = sharedPref.getBoolean(getString(R.string.pref_fab_discovery), false)

        if (!fabDiscovered) {
            TapTargetView.showFor(this,
                TapTarget.forView(
                        findViewById(R.id.fab),
                        getText(R.string.fab_feature_discovery_title),
                        getText(R.string.fab_feature_discovery_description)
                ).tintTarget(false),
                object : TapTargetView.Listener() {
                    override fun onTargetClick(view: TapTargetView) {
                        launchAddConnectionActivity(view.context)
                        updateFabDiscovery()
                    }

                    override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                        if (userInitiated) updateFabDiscovery()
                    }
                }
            )
        }
    }

    private fun refreshDrawerContent() {
        val caret = findViewById<AppCompatImageView>(R.id.drawer_header_caret)

        if (DRAWER_ALT_MODE) {
            caret.setImageResource(R.drawable.ic_arrow_drop_up)
            val anim = RotateAnimation(180.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.duration = 250
            caret.animation = anim
            anim.start()

            val serverList = findViewById<LinearLayout>(R.id.drawer_server_list)

            CredentialStore.getAllCredentials(this).forEach { entry ->
                val item = DrawerServerListItem(this)
                item.setServerHost(entry.second.get("host") as String)
                item.setServerNick(entry.second.get("nick") as String)
                item.setServerStatus(3)
                item.setOnClickListener {
                    selectCurrentServer(entry.first!!)
                }
                serverList.addView(item)
            }
        } else {
            caret.setImageResource(R.drawable.ic_arrow_drop_down)
            val anim = RotateAnimation(180.0f, 0.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.duration = 250
            caret.animation = anim
            anim.start()

            val serverList = findViewById<LinearLayout>(R.id.drawer_server_list)
            serverList.removeAllViewsInLayout()
            serverList.invalidate()
            serverList.requestLayout()
        }
    }

    private fun selectCurrentServer(uuid: String) {
        findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawers()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ConnectingFragment()).commit()
        AsyncConnection().execute(ConnectionData(this, uuid))
    }

    data class ConnectionData(val context: SupportActivity, val uuid: String)

    class AsyncConnection : AsyncTask<ConnectionData, Void, Void>() {
        private val RAW_IRC = Logger.getLogger("riru-irc-raw")
        override fun doInBackground(vararg data: ConnectionData?): Void? {
            val connection = ConnectionManager.connectToServer(data[0]!!.context, data[0]!!.uuid)
            connection.register(object {
                @SuppressLint("SetTextI18n")
                @Handler
                fun onMessage(aMsg: AbstractMessage) {
                    val textView = data[0]!!.context.findViewById<TextView>(R.id.main_connecting_log_textview)
                    textView.text = "${textView.text}${aMsg.asRaw()}${System.getProperty("line.separator")}"
                }
            })
            return null
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.drawer_snooze_notifications) {
            val toast = Toast.makeText(this, "Snooze pressed", Toast.LENGTH_SHORT)
            toast.show()
        } else if (id == R.id.drawer_exit_app) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask()
            } else {
                finishAffinity()
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_launch_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun updateFabDiscovery() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(getString(R.string.pref_fab_discovery), true)
        editor.commit()
    }

    fun launchAddConnectionActivity(context: Context) {
        startActivity(Intent(context, AddConnection::class.java))
    }
}
