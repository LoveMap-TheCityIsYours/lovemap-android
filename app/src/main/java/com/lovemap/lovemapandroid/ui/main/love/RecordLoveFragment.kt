package com.lovemap.lovemapandroid.ui.main.love

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lover.LoverViewDto
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.ui.utils.partnersFromRelations
import com.noowenz.customdatetimepicker.CustomDateTimePicker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RecordLoveFragment : Fragment(), CustomDateTimePicker.ICustomDateTimeListener {

    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
    private val appContext = AppContext.INSTANCE
    private val loverService = AppContext.INSTANCE.loverService

    lateinit var datePicker: CustomDateTimePicker
    lateinit var recordLoveHappenedAt: TextView
    lateinit var recordLoveChangeDateTime: Button
    var selectedTime: Instant = Instant.now()

    lateinit var recordLoveSelectPartnerDropdown: Spinner
    val partners: MutableList<String> = ArrayList()
    val partnerIds: MutableMap<String, Long?> = HashMap()

    lateinit var addPrivateNote: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_record_love, container, false)
        initHappenedAtViews(view)
        initPartnerDropdown(view)
        addPrivateNote = view.findViewById(R.id.addPrivateNote)
        return view
    }

    private fun initPartnerDropdown(view: View) {
        recordLoveSelectPartnerDropdown = view.findViewById(R.id.recordLoveSelectPartnerDropdown)
        partners.add(getString(R.string.not_app_user))
        partnerIds[getString(R.string.not_app_user)] = null
        recordLoveSelectPartnerDropdown.setSelection(0)
        MainScope().launch {
            loverService.getById()?.let { lover ->
                partners.addAll(partnersFromRelations(lover.relations).map {
                    partnerIds[it.userName] = it.id
                    it.userName
                })
                recordLoveSelectPartnerDropdown.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    partners.toTypedArray()
                )
            }
        }
    }

    private fun initHappenedAtViews(view: View) {
        datePicker = CustomDateTimePicker(requireActivity(), this)
        datePicker.set24HourFormat(true)
        recordLoveChangeDateTime = view.findViewById(R.id.recordLoveChangeDateTime)
        recordLoveHappenedAt = view.findViewById(R.id.recordLoveHappenedAt)
        recordLoveHappenedAt.text = ZonedDateTime.now().format(dateTimeFormatter)
        recordLoveChangeDateTime.setOnClickListener {
            datePicker.setDate(Calendar.getInstance())
            datePicker.showDialog()
        }
    }

    fun selectedPartner(): Long? {
        val selectedPartnerName = partners[recordLoveSelectPartnerDropdown.selectedItemPosition]
        return partnerIds[selectedPartnerName]
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
        val zonedDateTime =
            ZonedDateTime.of(year, monthNumber, day, hour24, min, sec, 0, timeZone.toZoneId())
        selectedTime = zonedDateTime.toInstant()
        recordLoveHappenedAt.text = zonedDateTime.format(dateTimeFormatter)
    }
}