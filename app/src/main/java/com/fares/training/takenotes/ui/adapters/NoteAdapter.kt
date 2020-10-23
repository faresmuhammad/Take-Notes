package com.fares.training.takenotes.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fares.training.takenotes.R
import com.fares.training.takenotes.data.local.Note
import kotlinx.android.synthetic.main.item_note.view.*
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter() : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {


    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    })

    var notes: List<Note>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(notes[position], position)
        }
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(note: Note) = with(itemView) {
            tvTitle.text = note.title
            if (note.isSynced) {
                ivSynced.setImageResource(R.drawable.ic_check)
                tvSynced.text = "Synced"
            } else {
                ivSynced.setImageResource(R.drawable.ic_cross)
                tvSynced.text = "Not Synced"
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy, HH.mm", Locale.getDefault())
            tvDate.text = dateFormat.format(note.date)

            ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)?.let {
                val wrappedDrawable = DrawableCompat.wrap(it)
                val color = Color.parseColor("#${note.color}")
                DrawableCompat.setTint(wrappedDrawable, color)
                viewNoteColor.background = wrappedDrawable
            }
            
        }
    }

    private var onItemClickListener: OnItemClickListener? = null


    fun setOnItemClickListener(click: (Note, Int) -> Unit): OnItemClickListener {
        onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(note: Note, position: Int) {
                click(note, position)
            }
        }
        return onItemClickListener!!
    }

    interface OnItemClickListener {
        fun onItemClick(note: Note, position: Int)
    }
}