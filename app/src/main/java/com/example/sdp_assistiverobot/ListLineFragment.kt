package com.example.sdp_assistiverobot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

private const val ARG_PARAM1 = "name"
private const val ARG_PARAM2 = "state"

class ListLineFragment : Fragment() {

    private var first: String? = null
    private var last: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            first = it.getString(ARG_PARAM1)
            last = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_list_line, container, false)
        val line = v.findViewById<TextView>(R.id.listLine)
        line.text = "$first $last"
        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListLineFragment2.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListLineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
