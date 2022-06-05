package com.lovemap.lovemapandroid.ui.main.love

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.utils.partnersFromRelations
import com.lovemap.lovemapandroid.utils.timeZone
import com.lovemap.lovemapandroid.utils.toFormattedString
import com.noowenz.customdatetimepicker.CustomDateTimePicker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

class RecordLoveFragment : Fragment(), CustomDateTimePicker.ICustomDateTimeListener {

    private val appContext = AppContext.INSTANCE
    private val loverService = appContext.loverService

    lateinit var datePicker: CustomDateTimePicker
    lateinit var recordLoveHappenedAt: TextView
    lateinit var recordLoveChangeDateTime: Button
    var selectedTime: Instant = Instant.now()

    lateinit var recordLoveSelectPartnerDropdown: Spinner
    private val partners: MutableList<String> = ArrayList()
    private val partnerIds: MutableMap<String, Long?> = HashMap()

    lateinit var addPrivateNote: TextView

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
            loverService.getMyself()?.let { lover ->
                partners.addAll(partnersFromRelations(lover.relations).map {
                    partnerIds[it.userName] = it.id
                    it.userName
                })
                recordLoveSelectPartnerDropdown.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    partners.toTypedArray()
                )
                if (partners.size > 1) {
                    recordLoveSelectPartnerDropdown.setSelection(1)
                }
            }
        }
    }

    private fun initHappenedAtViews(view: View) {
        datePicker = CustomDateTimePicker(requireActivity(), this)
        datePicker.set24HourFormat(true)
        recordLoveChangeDateTime = view.findViewById(R.id.recordLoveChangeDateTime)
        recordLoveHappenedAt = view.findViewById(R.id.recordLoveHappenedAt)
        recordLoveHappenedAt.text = Instant.now().toFormattedString()
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
        val zonedDateTime =
            ZonedDateTime.of(year, monthNumber + 1, day, hour24, min, sec, 0, timeZone.toZoneId())
        selectedTime = zonedDateTime.toInstant()
        recordLoveHappenedAt.text = selectedTime.toFormattedString()
    }
}