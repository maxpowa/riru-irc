package us.maxpowa.ircclient.components

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import us.maxpowa.ircclient.R

class DrawerServerListItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    init {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.component_server_menu_item, this, true)

        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.DrawerServerListItem, 0, 0)
            var serverHost = ""
            var serverNick = ""
            var serverConnectionStatus = 3
            try {
                serverHost = attributes.getString(R.styleable.DrawerServerListItem_host)
                serverNick = attributes.getString(R.styleable.DrawerServerListItem_nick)
                serverConnectionStatus = attributes.getInt(R.styleable.DrawerServerListItem_status, serverConnectionStatus)
            } finally {
                attributes.recycle()
            }

            setServerHost(serverHost)
            setServerNick(serverNick)
            setServerStatus(serverConnectionStatus)
        }
    }

    private fun getConnectionStatusDrawable(status: Int): Int{
        return when (status) {
            0 -> R.drawable.server_network_off
            1 -> R.drawable.server_network
            2 -> R.drawable.server
            else -> R.drawable.server_network_help
        }
    }

    fun setServerHost(host: String) {
        this.findViewById<TextView>(R.id.server_menu_item_host).text = host
    }

    fun setServerNick(nick: String) {
        this.findViewById<TextView>(R.id.server_menu_item_nick).text = nick
    }

    /**
     * 0 means disconnected,
     * 1 means connecting,
     * 2 means connected,
     * 3+ means error
     */
    fun setServerStatus(serverConnectionStatus: Int) {
        this.findViewById<AppCompatImageView>(R.id.server_menu_item_status_icon).setImageResource(getConnectionStatusDrawable(serverConnectionStatus))
    }

}
