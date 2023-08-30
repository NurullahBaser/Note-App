package com.example.noteapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.noteapp.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch

class Settings : AppCompatActivity() {

    private lateinit var textSizeSwitch: Switch
    private lateinit var darkModeSwitch: Switch
    private lateinit var radioGroupColumnView: RadioGroup
    private lateinit var radioGroupMaxLine: RadioGroup


    private val PREF_TEXT_SIZE = "text_size_preference"
    private val PREF_DARK_MODE = "dark_mode_preference"
    private val PREF_COLUMN_VIEW = "column_view_preference"
    private val PREF_MAX_LINE = "max_line_preference"


    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        textSizeSwitch = findViewById(R.id.switch1)
        darkModeSwitch = findViewById(R.id.switch2)
        radioGroupColumnView = findViewById(R.id.colum_view_radio_group)
        radioGroupMaxLine = findViewById(R.id.max_line_count_radio_group)

        val sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        textSizeSwitch.isChecked = sharedPref.getBoolean(PREF_TEXT_SIZE, false)
        darkModeSwitch.isChecked = sharedPref.getBoolean(PREF_DARK_MODE, true)
        val selectedColumnView = sharedPref.getInt(PREF_COLUMN_VIEW, R.id.multi_column_button)
        radioGroupColumnView.check(selectedColumnView)
        val selectedMaxLine = sharedPref.getInt(PREF_MAX_LINE,R.id.max_line_ten)
        radioGroupMaxLine.check(selectedMaxLine)



        radioGroupColumnView.setOnCheckedChangeListener { _, checkedId ->
            savePreferences(PREF_COLUMN_VIEW, checkedId)
            lifecycleScope.launch{
                when (checkedId) {
                    R.id.multi_column_button -> {
                        SettingsManager.save(SettingsManager.COLUMN_VIEW, "multi")
                    }
                    R.id.single_column_button -> {
                        SettingsManager.save(SettingsManager.COLUMN_VIEW, "single")
                    }
                }
            }
        }

        radioGroupMaxLine.setOnCheckedChangeListener { _, checkedId ->
            savePreferences(PREF_MAX_LINE,checkedId)
            lifecycleScope.launch{
                when (checkedId) {
                    R.id.max_line_five -> {
                        SettingsManager.save(SettingsManager.MAX_LINE,"5")
                    }
                    R.id.max_line_ten -> {
                        SettingsManager.save(SettingsManager.MAX_LINE,"10")
                    }
                    R.id.max_line_fifteen -> {
                        SettingsManager.save(SettingsManager.MAX_LINE,"15")
                    }
                }
            }
        }

        textSizeSwitch.setOnCheckedChangeListener { _, isChecked ->
            savePreferences(PREF_TEXT_SIZE, isChecked)
            lifecycleScope.launch {
                SettingsManager.save(SettingsManager.TEXT_SIZE,isChecked.toString())
                settings()
            }

        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            savePreferences(PREF_DARK_MODE, isChecked)
            lifecycleScope.launch {
                SettingsManager.save(SettingsManager.DARK_MODE,isChecked.toString())
                settings()
            }
        }

        binding.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            settings()
        }
    }


    private fun savePreferences(key: String, value: Boolean) {
        val sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    private fun savePreferences(key: String, value: Int) {
        val sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private suspend fun settings(){
        textSizeSetting()
        darkModeSetting()
    }

    private suspend fun textSizeSetting(){
        val textView2: TextView = findViewById(R.id.textView2)
        val textView3: TextView = findViewById(R.id.textView3)
        val textView4: TextView = findViewById(R.id.textView4)
        val textView5: TextView = findViewById(R.id.textView5)
        val switch1: Switch = findViewById(R.id.switch1)
        val switch2: Switch = findViewById(R.id.switch2)

        if (SettingsManager.read(SettingsManager.TEXT_SIZE) == null || SettingsManager.read(SettingsManager.TEXT_SIZE) == "false") {
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22.toFloat())
            textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22.toFloat())
            textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22.toFloat())
            textView5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22.toFloat())
            switch1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22.toFloat())
            switch2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22.toFloat())
        } else {
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.toFloat())
            textView3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.toFloat())
            textView4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.toFloat())
            textView5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.toFloat())
            switch1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.toFloat())
            switch2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.toFloat())
        }
    }

    private suspend fun darkModeSetting() {
        val layout: ConstraintLayout = findViewById(R.id.settings_layout)


        if(SettingsManager.read(SettingsManager.DARK_MODE) == null || SettingsManager.read(SettingsManager.DARK_MODE) == "false") {
            layout.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
        } else {
            layout.setBackgroundColor(ContextCompat.getColor(this,R.color.light_grey))
        }

    }

}