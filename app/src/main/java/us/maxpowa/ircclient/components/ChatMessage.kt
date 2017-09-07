package us.maxpowa.ircclient.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import us.maxpowa.ircclient.R

class ChatMessage @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    init {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.chat_message_attrs, 0, 0)
        val chatMessageText = a.getString(R.styleable.chat_message_attrs_message)
        a.recycle()

        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.chat_message_component, this, true)

        val frame = getChildAt(0) as LinearLayout

        val message = frame.findViewById(R.id.chat_message_text) as TextView
        message.text = chatMessageText
    }

}
