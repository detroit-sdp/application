package com.example.sdp_assistiverobot

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_add_patient.*
import java.util.*


class AddPatientActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var state: String
    private lateinit var db : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

//        val months = this.resources.getStringArray(R.array.months)
//        birthMonth.adapter = SpinnerArrayAdapter<String>(this, android.R.layout.simple_spinner_item, months.toList())
//            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
//        birthMonth.onItemSelectedListener = this

        val states = this.resources.getStringArray(R.array.medicalStates)
        medicalState.adapter = SpinnerArrayAdapter<String>(this, android.R.layout.simple_spinner_item, states.toList())
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        medicalState.onItemSelectedListener = this

        button_save.setOnClickListener {

        }

        birthday.inputType = InputType.TYPE_NULL
        birthday.setOnClickListener{
            val calendar = Calendar.getInstance()
            val curDay = calendar.get(Calendar.DAY_OF_MONTH)
            val curMonth = calendar.get(Calendar.MONTH)
            val curYear = calendar.get(Calendar.YEAR)

            DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val month1 = month + 1
                    birthday.setText("$dayOfMonth/$month1/$year")
                }, curYear, curMonth, curDay).apply { show() }
        }
    }

    private fun validate(): Boolean {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)


        return true
    }

//    private fun createNewPatient() {
//
//    }

    private fun uploadToDatabase() {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        if (view == birthMonth) {
//            month = parent?.getItemAtPosition(position) as String
//            Toast.makeText(this, month, Toast.LENGTH_SHORT).show()
//        } else {
            state = parent?.getItemAtPosition(position) as String
            Toast.makeText(this, state, Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private class SpinnerArrayAdapter<String>(context: Context, resource: Int,
                                              objects : List<String>) : ArrayAdapter<String>(context, resource, objects) {
        override fun isEnabled(position: Int): Boolean {
            return position != 0
        }
        override fun getDropDownView(
            position: Int, convertView: View?,
            parent: ViewGroup?
        ): View? {
            val view = super.getDropDownView(position, convertView, parent)
            val tv = view as TextView
            if (position == 0) { // Set the hint text color gray
                tv.setTextColor(Color.GRAY)
            } else {
                tv.setTextColor(Color.BLACK)
            }
            return view
        }
    }
}
