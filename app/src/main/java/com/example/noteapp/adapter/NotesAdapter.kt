package com.example.noteapp.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.R
import com.example.noteapp.models.Note
import androidx.core.content.ContextCompat
import com.example.noteapp.ColorManager
import com.example.noteapp.SettingsManager
import kotlinx.coroutines.runBlocking

class NotesAdapter(private val context : Context, val listener : NotesClickListener) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val notesList = ArrayList<Note>()
    private val fullList = ArrayList<Note>()
    private val holderList = ArrayList<NoteViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
       val holder = NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item,parent,false))
        holderList.add(holder)
        settings(holder)
        return holder
    }

    override fun getItemCount(): Int {
        return notesList.size
    }


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.title.text = currentNote.title
        holder.title.isSelected = true
        holder.note_tv.text = currentNote.note
        holder.date.text = currentNote.date
        holder.date.isSelected = true


        currentNote.color = currentNote.color ?: ColorManager.randomColor()
        holder.notes_layout.setCardBackgroundColor(ContextCompat.getColor(context, currentNote.color ?: R.color.default_color))

        holder.notes_layout.setOnLongClickListener{
            listener.onLongItemClicked(notesList[holder.absoluteAdapterPosition],holder.notes_layout)
            true
        }
        holder.notes_layout.setOnClickListener{
            listener.onItemClicked(notesList[holder.absoluteAdapterPosition])
        }
    }

    fun updateList(newList : List<Note>) {

        fullList.clear()
        fullList.addAll(newList)

        notesList.clear()
        notesList.addAll(fullList)

        notifyDataSetChanged()
    }

    fun filterList(search : String) {
        notesList.clear()

        for(item in fullList) {
            if (item.title?.lowercase()?.contains(search.lowercase()) == true
                || item.note?.lowercase()?.contains(search.lowercase()) == true) {
                notesList.add(item)
            }
        }
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val notes_layout = itemView.findViewById<CardView>(R.id.card_layout)
        val title = itemView.findViewById<TextView>(R.id.tv_title)
        val note_tv = itemView.findViewById<TextView>(R.id.tv_note)
        val date = itemView.findViewById<TextView>(R.id.tv_date)

    }

    interface NotesClickListener{

        fun onItemClicked(note: Note)
        fun onLongItemClicked(note: Note,cardView: CardView)

    }

    fun allHolderSettings() {
        runBlocking {
            for (holder in holderList) {
                textSizeSetting(holder)
                maxLineSetting(holder)
            }
        }
    }

    private fun settings(holder: NoteViewHolder) {
        runBlocking {
            textSizeSetting(holder)
            maxLineSetting(holder)
        }

    }

    private suspend fun textSizeSetting(holder: NoteViewHolder) {
        if (SettingsManager.read(SettingsManager.TEXT_SIZE) == null || SettingsManager.read(SettingsManager.TEXT_SIZE) == "false") {
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22.toFloat())
            holder.note_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.toFloat())
            holder.date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.toFloat())
            holder.note_tv.maxLines
        } else {
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28.toFloat())
            holder.note_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.toFloat())
            holder.date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.toFloat())
        }
    }

    private suspend fun maxLineSetting(holder: NoteViewHolder) {
        if (SettingsManager.read(SettingsManager.MAX_LINE) == null || SettingsManager.read(SettingsManager.MAX_LINE) == "10") {
            holder.note_tv.maxLines = 10
        } else if (SettingsManager.read(SettingsManager.MAX_LINE) == "5") {
            holder.note_tv.maxLines = 5
        } else if (SettingsManager.read(SettingsManager.MAX_LINE) == "15") {
            holder.note_tv.maxLines = 15
        }
    }


}