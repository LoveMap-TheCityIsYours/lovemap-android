package com.lovemap.lovemapandroid.ui.main.love

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.lovemap.lovemapandroid.R
import com.noowenz.customdatetimepicker.CustomDateTimePicker
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class RecordLoveFragment : Fragment(), CustomDateTimePicker.ICustomDateTimeListener {

    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

    private lateinit var datePicker: CustomDateTimePicker
    private lateinit var recordLoveHappenedAt: TextView
    private lateinit var recordLoveChangeDateTime: Button

    private var selectedTime: Instant = Instant.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_record_love, container, false)
        datePicker = CustomDateTimePicker(requireActivity(), this)
        datePicker.set24HourFormat(true)

        recordLoveChangeDateTime = view.findViewById(R.id.recordLoveChangeDateTime)
        recordLoveHappenedAt = view.findViewById(R.id.recordLoveHappenedAt)

        recordLoveHappenedAt.text = ZonedDateTime.now().format(dateTimeFormatter)
        recordLoveChangeDateTime.setOnClickListener {
            datePicker.setDate(Calendar.getInstance())
            datePicker.showDialog()
        }
        return view
    }

    override fun onCancel() {

    }

    override fun onSet(
        dialog: Dialog,
        calendarSelected: Calendar,
        dateSelected: Date,
        year: Int,
        monthFullName: String,
        monthShortName: String,
        monthNumber: Int,
        day: Int,
        weekDayFullName: String,
        weekDayShortName: String,
        hour24: Int,
        hour12: Int,
        min: Int,
        sec: Int,
        AM_PM: String
    ) {
        val timeZone = TimeZone.getDefault()
        val zonedDateTime = ZonedDateTime.of(year, monthNumber, day, hour24, min, sec, 0, timeZone.toZoneId())
        selectedTime = zonedDateTime.toInstant()
        recordLoveHappenedAt.text = zonedDateTime.format(dateTimeFormatter)
    }
}