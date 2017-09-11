package us.maxpowa.ircclient.activity

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import us.maxpowa.ircclient.R
import us.maxpowa.ircclient.utils.CredentialStore
import us.maxpowa.ircclient.utils.TextValidator
import java.net.URI
import java.net.URISyntaxException


class AddConnection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_connection)

        val hostField = findViewById<AutoCompleteTextView>(R.id.server)
        val sslToggle = findViewById<CheckBox>(R.id.ssltoggle)
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
                    } else if (uri.port === 994 || uri.port === 6697) {
                        sslToggle.isChecked = true
                    }

                } catch (ex: URISyntaxException) {
                    // validation failed
                    textView.error = getText(R.string.add_connection_invalid_host)
                }
            }
        })

        val nickField = findViewById<EditText>(R.id.nickname)
        nickField.addTextChangedListener(object : TextValidator(nickField) {
            override fun validate(textView: TextView, text: String) {
                var isValidNick = text.matches("""^[a-z_\-\[\]\\^{}|`][a-z0-9_\-\[\]\\^{}|`]{0,15}$""".toRegex(RegexOption.IGNORE_CASE))
                if (!isValidNick) {
                    textView.error = "Nick does not conform to RFC2812"
                }
            }
        })

        val altNicksField = findViewById<EditText>(R.id.altNicks)
        altNicksField.addTextChangedListener(object : TextValidator(altNicksField) {
            override fun validate(textView: TextView, text: String) {
                text.split(',').forEach { nick ->
                    var isValidNick = nick.matches("""^[a-z_\-\[\]\\^{}|`][a-z0-9_\-\[\]\\^{}|`]{0,15}$""".toRegex(RegexOption.IGNORE_CASE))
                    if (!isValidNick && nick.length > 0) {
                        textView.error = "Alt. nick does not conform to RFC2812"
                        return@forEach
                    }
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

    fun saveCredentials(): Boolean {
        val hostField = findViewById<AutoCompleteTextView>(R.id.server)
        if (hostField.error != null) {
            hostField.requestFocus()
            return false
        }
        val host = hostField.text.toString()

        val nickField = findViewById<EditText>(R.id.nickname)
        if (nickField.error != null) {
            nickField.requestFocus()
            return false
        }
        val nick = nickField.text.toString()

        val passwordField = findViewById<EditText>(R.id.password)
        if (passwordField.error != null) {
            passwordField.requestFocus()
            return false
        }
        val password = passwordField.text.toString()

        val altNicksField = findViewById<EditText>(R.id.altNicks)
        if (altNicksField.error != null) {
            altNicksField.requestFocus()
            return false
        }
        val altNicks = altNicksField.text.split(',').mapNotNull { nick ->
            var isValidNick = nick.matches("""^[a-z_\-\[\]\\^{}|`][a-z0-9_\-\[\]\\^{}|`]{0,15}$""".toRegex(RegexOption.IGNORE_CASE))
            if (!isValidNick && nick.length > 0) {
                return@mapNotNull nick
            }
            return@mapNotNull null
        }

        CredentialStore.pushCredentials(this, host, nick, altNicks, password)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.add_connection_save) {
            val result = saveCredentials()
            if (result) {
                onBackPressed()
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
