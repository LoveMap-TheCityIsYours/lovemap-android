package com.lovemap.lovemapandroid.ui.main.love

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.utils.partnersFromRelations
import com.lovemap.lovemapandroid.utils.toFormattedDateTime
import com.lovemap.lovemapandroid.utils.toInstant
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class RecordLoveFragment : Fragment(),
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private val appContext = AppContext.INSTANCE
    private val loverService = appContext.loverService
    private val partnershipService = appContext.partnershipService

    lateinit var datePicker: DatePickerDialog
    lateinit var timePicker: TimePickerDialog
    lateinit var recordLoveHappenedAt: TextView
    lateinit var recordLoveChangeDateTime: Button
    var selectedDateTime: LocalDateTime = LocalDateTime.now()

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
            try {
                loverService.getMyself()?.let { lover ->
                    partners.addAll(partnersFromRelations(lover.relations).map {
                        partnerIds[it.displayName] = it.id
                        it.displayName
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
            } catch (e: Exception) {
                Log.e(tag, "initPartnerDropdown shitted itself", e)
            }

        }
    }

    private fun initHappenedAtViews(view: View) {
        recordLoveChangeDateTime = view.findViewById(R.id.recordLoveChangeDateTime)
        recordLoveHappenedAt = view.findViewById(R.id.recordLoveHappenedAt)
        recordLoveHappenedAt.text = selectedDateTime.toInstant().toFormattedDateTime()
        datePicker = DatePickerDialog(
            requireActivity(),
            this,
            selectedDateTime.year,
            selectedDateTime.monthValue - 1,
            selectedDateTime.dayOfMonth
        )
        timePicker = TimePickerDialog(
            requireActivity(),
            this,
            selectedDateTime.hour,
            selectedDateTime.minute,
            true
        )

        recordLoveChangeDateTime.setOnClickListener {
            datePicker.updateDate(
                selectedDateTime.year,
                selectedDateTime.monthValue - 1,
                selectedDateTime.dayOfMonth
            )
            datePicker.show()
        }
    }

    fun selectedPartner(): Long? = runCatching {
        val selectedPartnerName = partners[recordLoveSelectPartnerDropdown.selectedItemPosition]
        partnerIds[selectedPartnerName]
    }.getOrNull()

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedDateTime = LocalDateTime.of(
            year,
            month + 1,
            dayOfMonth,
            selectedDateTime.hour,
            selectedDateTime.minute
        )
        recordLoveHappenedAt.text = selectedDateTime.toInstant().toFormattedDateTime()
        timePicker.updateTime(selectedDateTime.hour, selectedDateTime.minute)
        timePicker.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        selectedDateTime = LocalDateTime.of(
            selectedDateTime.year,
            selectedDateTime.monthValue,
            selectedDateTime.dayOfMonth,
            hourOfDay,
            minute
        )
        recordLoveHappenedAt.text = selectedDateTime.toInstant().toFormattedDateTime()
    }
}