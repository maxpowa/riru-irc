package us.maxpowa.ircclient.activity

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView

import java.net.URI
import java.net.URISyntaxException

import us.maxpowa.ircclient.R
import us.maxpowa.ircclient.utils.TextValidator
import android.widget.EditText
import us.maxpowa.ircclient.connection.Host


class AddConnection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_connection)

        val hostField = findViewById(R.id.server) as AutoCompleteTextView
        hostField.addTextChangedListener(object : TextValidator(hostField) {
            override fun validate(textView: TextView, text: String) {
                try {
                    val uri = URI("irc://" + text) // may throw URISyntaxException

                    if (uri.host == null) {
                        val colonCount = text.length - text.replace(":", "").length

                        if (colonCount > 1) {
                            textView.error = getString(R.string.add_connection_ipv6_hint)
                        } else {
                            textView.error = getText(R.string.add_connection_invalid_host)
                        }
                    }

                    if (uri.port > 25565 || uri.port == 0) {
                        textView.error = getText(R.string.add_connection_invalid_port)
                    }

                } catch (ex: URISyntaxException) {
                    // validation failed
                    textView.error = getText(R.string.add_connection_invalid_host)
                }
            }
        })

        val hostAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("chat.freenode.net", "chat.freenode.net:6697", "open.ircnet.net", "irc.quakenet.org", "irc.efnet.org", "irc.ipv6.efnet.org", "irc.undernet.org", "irc.rizon.net", "irc.oftc.net", "irc.oftc.net:6697", "irc.dal.net", "irc.esper.net", "na.irc.esper.net", "eu.irc.esper.net"))
        hostField.setAdapter(hostAdapter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_add_connection, menu)

        val menuItem = menu.findItem(R.id.add_connection_save)
        val drawable = menuItem.icon
        if (drawable != null) {
            drawable.mutate()
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorForegroundIcon), PorterDuff.Mode.SRC_ATOP)
        }

        return true
    }

    fun saveCredentials() {
        val hostField = findViewById(R.id.server) as AutoCompleteTextView
        val nickField = findViewById(R.id.nickname) as EditText
        val passwordField = findViewById(R.id.password) as EditText
        val prefs = getSharedPreferences("connections", Context.MODE_PRIVATE)

        //prefs.edit().putString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.add_connection_save) {

            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
