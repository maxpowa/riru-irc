package us.maxpowa.ircclient.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import us.maxpowa.ircclient.R


/**
 * A simple [Fragment] subclass.
 */
class PlaceholderFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_placeholder, container, false)
    }

}// Required empty public constructor
