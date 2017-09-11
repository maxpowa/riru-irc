package us.maxpowa.ircclient.components

import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import us.maxpowa.ircclient.R
import java.text.SimpleDateFormat

class ChatMessage @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private var message : String = ""
    private var sender : String = "unknown"
    private var timestamp : String = "1970-01-01T00:00:00.000Z"
    private var timestampHidden: Boolean = true

    init {

        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.component_chat_message, this, true)

        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.ChatMessage, 0, 0)
            var chatMessageText = ""
            var chatMessageSender = "unknown"
            var chatMessageTimestamp = "1970-01-01T00:00:00.000Z"
            try {
                chatMessageText = attributes.getString(R.styleable.ChatMessage_message)
                chatMessageTimestamp = attributes.getString(R.styleable.ChatMessage_timestamp)
                chatMessageSender = attributes.getString(R.styleable.ChatMessage_sender)
            } finally {
                attributes.recycle()
            }

            this.setMessage(chatMessageText)
            this.setSender(chatMessageSender)
            this.setTimestamp(chatMessageTimestamp)
            this.setTimestampHidden(true)
        }
    }


    fun getMessage(): String {
        return message
    }

    fun setMessage(message: String) {
        this.message = message

        val messageView = this.findViewById<TextView>(R.id.chat_message_text)
        messageView.text = this.message

        invalidate()
        requestLayout()
    }

    fun getSender(): String {
        return sender
    }

    fun setSender(sender: String) {
        this.sender = sender

        val senderView = this.findViewById<TextView>(R.id.chat_message_sender)
        senderView.text = this.sender

        invalidate()
        requestLayout()
    }

    fun getTimestamp(): String {
        return timestamp
    }

    fun setTimestamp(timestamp: String) {
        this.timestamp = timestamp

        val timestampView = this.findViewById<TextView>(R.id.chat_message_timestamp)

        val parsedTime = SimpleDateFormat("YYYY-MM-DD'T'hh:mm:ss.sss'Z'").parse(timestamp)
        val formattedTime = DateFormat.getTimeFormat(context).format(parsedTime)
        timestampView.text = formattedTime

        invalidate()
        requestLayout()
    }

    fun isTimestampHidden(): Boolean {
        return timestampHidden
    }

    fun setTimestampHidden(hidden: Boolean) {
        this.timestampHidden = hidden

        val timestampView = this.findViewById<TextView>(R.id.chat_message_timestamp)
        if (timestampHidden) {
            timestampView.visibility = View.GONE
        } else {
            timestampView.visibility = View.VISIBLE
        }

        invalidate()
        requestLayout()
    }

}
