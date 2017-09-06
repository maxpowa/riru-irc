package us.maxpowa.ircclient

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

import com.getkeepsafe.taptargetview.TapTargetView
import com.getkeepsafe.taptargetview.TapTarget

import us.maxpowa.ircclient.activity.AddConnection
import android.widget.Toast
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.widget.LinearLayout


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var DRAWER_ALT_MODE = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        setupFab()
        setupDrawer()
    }

    private fun setupDrawer() {
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.drawer_navigation_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val header = navigationView.getHeaderView(0)

        header.findViewById(R.id.drawer_header_container).setOnClickListener {
            DRAWER_ALT_MODE = !DRAWER_ALT_MODE
            refreshDrawerContent()
        }
    }

    private fun setupFab() {
        val fab = findViewById(R.id.fab) as FloatingActionButton
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

    fun refreshDrawerContent() {
        val caret = findViewById(R.id.drawer_header_caret) as AppCompatImageView

        if (DRAWER_ALT_MODE) {
            caret.setImageResource(R.drawable.ic_arrow_drop_up)
        } else {
            caret.setImageResource(R.drawable.ic_arrow_drop_down)
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

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
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
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
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
        //        overridePendingTransition(R.anim.bottom_up, R.anim.no_animation);
    }
}
