package us.maxpowa.ircclient.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ViewSwitcher
import us.maxpowa.ircclient.R

/**
 * A simple [Fragment] subclass.
 */
class ConnectingFragment : Fragment(), View.OnClickListener {

    private lateinit var fragment: View

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.fragment_connecting, container, false)

        val button = view.findViewById<Button>(R.id.main_connecting_log_btn)
        button.setOnClickListener(this)

        this.fragment = view

        return view
    }

    override fun onClick(view: View) {
        when (view.getId()) {
            R.id.main_connecting_log_btn -> {
                fragment.findViewById<ViewSwitcher>(R.id.main_connecting_switcher).showNext()
            }
        }
    }

}// Required empty public constructor