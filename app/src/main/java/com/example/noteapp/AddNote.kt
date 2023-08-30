package com.example.noteapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.noteapp.models.Note
import com.example.noteapp.databinding.ActivityAddNoteBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class AddNote : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var note : Note
    private lateinit var old_note : Note
    var isUpdate = false


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {

            old_note = intent.getSerializableExtra("current_note",Note::class.java) as Note
            binding.etTitle.setText(old_note.title)
            binding.etNote.setText(old_note.note)
            isUpdate = true

        }catch (e : Exception) {
        }

        binding.imgCheck.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val note_desc = binding.etNote.text.toString()

            if(title.isNotEmpty() || note_desc.isNotEmpty()) {
                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")
                if(isUpdate) {
                    note = Note(
                        old_note.id,title,note_desc,formatter.format(Date()),old_note.color
                    )
                } else {
                    note = Note (
                        null,title,note_desc,formatter.format(Date()) , ColorManager.randomColor()
                            )
                }

                val intent = Intent()
                intent.putExtra("note",note)
                setResult(RESULT_OK,intent)
                finish()
            } else {
                Toast.makeText(this@AddNote, "Please enter some data", Toast.LENGTH_SHORT).show()
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

    private suspend fun settings(){
        textSizeSetting()
        darkModeSetting()
    }

    private suspend fun textSizeSetting(){
        val title: TextView = findViewById(R.id.et_title)
        val note: TextView = findViewById(R.id.et_note)


        if (SettingsManager.read(SettingsManager.TEXT_SIZE) == null || SettingsManager.read(SettingsManager.TEXT_SIZE) == "false") {
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30.toFloat())
            note.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22.toFloat())
        } else {
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36.toFloat())
            note.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26.toFloat())
        }
    }

    private suspend fun darkModeSetting() {
        val layout: ConstraintLayout = findViewById(R.id.add_note_layout)
        val title: TextView = findViewById(R.id.et_title)
        val note: TextView = findViewById(R.id.et_note)

        if(SettingsManager.read(SettingsManager.DARK_MODE) == null || SettingsManager.read(SettingsManager.DARK_MODE) == "false") {
            layout.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            title.setBackgroundResource(R.drawable.corner8dp_light)
            note.setBackgroundResource(R.drawable.corner8dp_light)
        } else {
            layout.setBackgroundColor(ContextCompat.getColor(this,R.color.light_grey))
            title.setBackgroundResource(R.drawable.corner8dp_dark)
            note.setBackgroundResource(R.drawable.corner8dp_dark)
        }

    }

}