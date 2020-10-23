package com.fares.training.takenotes.ui.add_edit_note

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fares.training.takenotes.R
import com.fares.training.takenotes.data.local.Note
import com.fares.training.takenotes.ui.BaseFragment
import com.fares.training.takenotes.ui.dialogs.ColorPickerDialogFragment
import com.fares.training.takenotes.util.Constants.Dialog.COLOR_FRAGMENT_TAG
import com.fares.training.takenotes.util.Constants.Note.DEFAULT_NOTE_COLOR
import com.fares.training.takenotes.util.Constants.Preferences.KEY_LOGGED_EMAIL
import com.fares.training.takenotes.util.Constants.Preferences.NO_EMAIL
import com.fares.training.takenotes.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_edit_note.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddEditNoteFragment : BaseFragment(R.layout.fragment_add_edit_note) {


    private val vm: AddEditNoteViewModel by viewModels()

    private val args: AddEditNoteFragmentArgs by navArgs()

    private var note: Note? = null
    private var noteColor: String = DEFAULT_NOTE_COLOR

    @Inject
    lateinit var pref: SharedPreferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.id.isNotEmpty()) {
            vm.getNoteById(args.id)
        }

        savedInstanceState?.let {
            val colorPickerFragment =
                parentFragmentManager.findFragmentByTag(COLOR_FRAGMENT_TAG) as ColorPickerDialogFragment?
            colorPickerFragment?.setPositiveListener {
                changeViewNoteColor(it)
            }
        }

        viewNoteColor.setOnClickListener {
            ColorPickerDialogFragment().apply {
                setPositiveListener {
                    changeViewNoteColor(it)
                }
            }.show(parentFragmentManager, COLOR_FRAGMENT_TAG)
        }

        subscribeToObservers()
    }

    override fun onPause() {
        super.onPause()
        saveNote()
    }

    private fun subscribeToObservers() {
        vm.note.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is Resource.Success<Note> -> {
                        val noteData = result.data!!
                        this.note = noteData
                        etNoteTitle.setText(noteData.title)
                        etNoteContent.setText(noteData.content)
                        changeViewNoteColor(noteData.color)
                    }
                    is Resource.Error<Note> -> {
                        showSnackBar(result.message ?: "Note not found")
                    }
                    is Resource.Loading<Note> -> {
                        /* No Operatipn */
                    }
                }
            }
        }

    }

    private fun saveNote() {
        val authEmail = pref.getString(KEY_LOGGED_EMAIL, NO_EMAIL) ?: NO_EMAIL
        val title = etNoteTitle.text.toString()
        val content = etNoteContent.text.toString()
        if (title.isEmpty() || content.isEmpty()) {
            return
        }
        val date = System.currentTimeMillis()
        val color = noteColor
        val id = note?.id ?: UUID.randomUUID().toString()
        val owners = note?.owners ?: listOf(authEmail)
        val note = Note(title, content, date, owners, color, id = id)
        vm.insertNote(note)
    }

    private fun changeViewNoteColor(colorString: String) {
        noteColor = colorString
        ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            val color = Color.parseColor("#$colorString")
            DrawableCompat.setTint(wrappedDrawable, color)
            viewNoteColor.background = wrappedDrawable
        }

    }
}