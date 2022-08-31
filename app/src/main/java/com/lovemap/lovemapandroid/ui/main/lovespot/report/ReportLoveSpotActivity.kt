package com.lovemap.lovemapandroid.ui.main.lovespot.report

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.api.lovespot.report.LoveSpotReportRequest
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.databinding.ActivityReportLoveSpotBinding
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ReportLoveSpotActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val loveSpotService = appContext.loveSpotService
    private val loveSpotReportService = appContext.loveSpotReportService

    private lateinit var binding: ActivityReportLoveSpotBinding

    private lateinit var addReportText: EditText
    private lateinit var reportLoveSpotName: TextView
    private lateinit var reportSpotSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportLoveSpotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addReportText = binding.addReportText
        reportLoveSpotName = binding.reportLoveSpotName
        reportSpotSubmit = binding.reportSpotSubmit

        appContext.selectedLoveSpotId?.let {
            MainScope().launch {
                appContext.selectedLoveSpot = loveSpotService.findLocally(it)
                reportLoveSpotName.text = appContext.selectedLoveSpot?.name
            }
        }

        addReportText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (addReportText.text.length > 5) {
                    addReportText.error = null
                    reportSpotSubmit.isEnabled = true
                } else {
                    addReportText.error = getString(R.string.invalid_report_text)
                    reportSpotSubmit.isEnabled = false
                }
            }
        })

        reportSpotSubmit.setOnClickListener {
            MainScope().launch {
                val loadingBarShower = LoadingBarShower(this@ReportLoveSpotActivity).show()
                appContext.selectedLoveSpot?.let {
                    loveSpotReportService.submitReport(
                        LoveSpotReportRequest(
                            appContext.userId,
                            appContext.selectedLoveSpotId!!,
                            addReportText.text.toString()
                        )
                    )
                }
                loadingBarShower.onResponse()
            }
            onBackPressed()
        }

        binding.reportSpotCancel.setOnClickListener {
            onBackPressed()
        }
    }
}