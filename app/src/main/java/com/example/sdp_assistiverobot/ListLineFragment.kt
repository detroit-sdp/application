//import android.os.AsyncTask
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseAdapter
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.cardview.widget.CardView
//import com.example.sdp_assistiverobot.R
//import com.example.sdp_assistiverobot.patients.Patient
//import com.google.firebase.firestore.FirebaseFirestore

package com.example.sdp_assistiverobot

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sdp_assistiverobot.patients.PatientViewActivity

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
        val line = v.findViewById<TextView>(R.id.line_view)
        val card = v.findViewById<TextView>(R.id.card_view)
        line.text = "$first $last"
        card.setOnClickListener {
            Intent(context, PatientViewActivity::class.java).apply {
                startActivity(this)
            }
        }
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

//class LineViewAdapter: BaseAdapter() {
//
//    override fun getView(position:Int, convertView: View?, parent: ViewGroup?): View {
//        val viewHolder: ViewHolder
//        var mConvertView = convertView
//
//        if (mConvertView == null) {
//            // If convert view is null then inflate a custom view and use it as convert view
//            mConvertView = LayoutInflater.from(parent?.context).
//                inflate(R.layout.fragment_list_line, parent, false)
//
//            // Create a new view holder instance using convert view
//            viewHolder = ViewHolder(mConvertView)
//            // Set the view holder as convert view tag
//            mConvertView.tag = viewHolder
//        } else {
//            /*
//                If convert view is not null then
//                initialize the view holder using convert view tag.
//            */
//            viewHolder = mConvertView.tag as ViewHolder
//        }
//        // Display the current color name and value on view holder
//
//
//        // Set a click listener for card view
//
//        return mConvertView!!
//    }
//
//    override fun getItem(position: Int): Any? {
//        return null
//    }
//
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//
//
//    override fun getCount(): Int {
//
//    }
//
//    private class ViewHolder(view:View){
//        var colorName: TextView = view.findViewById(R.id.line_view)
//        var imageView: ImageView = view.findViewById(R.id.image_view)
//        val card: CardView = view.findViewById(R.id.card_view)
//    }
//
////    private class retrieveData(mHolder: ViewHolder) : AsyncTask<Void, Void, String>() {
////
////        private lateinit var patientsNames: ArrayList<String>
////
////        override fun doInBackground(vararg params: Void?): String {
////            val db = FirebaseFirestore.getInstance()
////            db.collection("Patients").get()
////                .addOnSuccessListener{ results ->
////                    for (document in results) {
////                        patientsNames.add("${document.get("first").toString()} ${document.get("last").toString()}")
////                    }
////                }
////
////            return patientsNames
////        }
////
////        override fun onPreExecute() {
////            super.onPreExecute()
////        }
////
////        override fun onPostExecute(result: String?) {
////            super.onPostExecute(result)
////        }
////    }
//}
