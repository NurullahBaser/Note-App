package com.example.noteapp

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.adapter.NotesAdapter
import com.example.noteapp.database.NoteDatabase
import com.example.noteapp.databinding.ActivityMainBinding
import com.example.noteapp.models.Note
import com.example.noteapp.models.NoteViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity(), NotesAdapter.NotesClickListener, PopupMenu.OnMenuItemClickListener,
    LifecycleObserver {

    private lateinit var binding : ActivityMainBinding
    private lateinit var database : NoteDatabase
    lateinit var viewModel : NoteViewModel
    lateinit var adapter : NotesAdapter
    lateinit var selectedNote : Note
    private val layoutManager = StaggeredGridLayoutManager(3, LinearLayout.VERTICAL)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {

            val note = result.data?.getSerializableExtra("note",Note::class.java)

            if (note != null) {
                viewModel.insertNote(note)

            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SettingsManager.dataStore = createDataStore(name = "settings")

        initUI()
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        viewModel.allNotes.observe(this) { list ->

            list?.let {
                adapter.updateList(list)
            }
        }

        database = NoteDatabase.getDatabase(this)

        lifecycleScope.launch{
            settings()
        }

    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            settings()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initUI() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = layoutManager
        adapter = NotesAdapter(this,this)
        binding.recyclerView.adapter = adapter


        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if(result.resultCode == Activity.RESULT_OK) {

                val note = result.data?.getSerializableExtra("note",Note::class.java)

                if (note != null) {
                    viewModel.insertNote(note)
                }
            }
        }


        binding.fbAddNote.setOnClickListener {
            val intent = Intent(this, AddNote::class.java)
            getContent.launch(intent)
        }
        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            getContent.launch(intent)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener  {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null) adapter.filterList(newText)
                return true
            }

        })

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onItemClicked(note: Note) {
        val intent = Intent(this@MainActivity, AddNote::class.java)
        intent.putExtra("current_note",note)
        updateNote.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onLongItemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun popUpDisplay(cardView: CardView) {
        val popup = PopupMenu(this,cardView)
        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate(R.menu.pop_up_menu)
        popup.setForceShowIcon(true)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_note){
            viewModel.deleteNote(selectedNote)
            return true
        }
        return false
    }

    private suspend fun settings(){
        textSizeSetting()
        darkModeSetting()
        columnViewSetting()
    }

    private fun textSizeSetting() {
        adapter.allHolderSettings()
    }

    private suspend fun columnViewSetting() {
        if(SettingsManager.read(SettingsManager.COLUMN_VIEW) == null || SettingsManager.read(SettingsManager.COLUMN_VIEW) == "multi") {
            layoutManager.spanCount = 2
        } else if(SettingsManager.read(SettingsManager.COLUMN_VIEW) == "single") {
            layoutManager.spanCount = 1
        }
    }


    private suspend fun darkModeSetting() {
        val layout: ConstraintLayout = findViewById(R.id.main_layout)
        val search : SearchView = findViewById(R.id.search_view)
        val settingsButton : ImageButton = findViewById(R.id.btn_settings)
        val addNoteButton : ImageButton = findViewById(R.id.fbAddNote)

        if(SettingsManager.read(SettingsManager.DARK_MODE) == null || SettingsManager.read(SettingsManager.DARK_MODE) == "false") {
            layout.setBackgroundColor(ContextCompat.getColor(this,R.color.white))
            search.setBackgroundResource(R.drawable.search_view_background_light)
            settingsButton.setBackgroundResource(R.drawable.circle_shape_light)
            addNoteButton.setBackgroundResource(R.drawable.circle_shape_light)
        } else {
            layout.setBackgroundColor(ContextCompat.getColor(this,R.color.light_grey))
            search.setBackgroundResource(R.drawable.search_view_background_dark)
            settingsButton.setBackgroundResource(R.drawable.circle_shape_dark)
            addNoteButton.setBackgroundResource(R.drawable.circle_shape_dark)
        }
    }
}