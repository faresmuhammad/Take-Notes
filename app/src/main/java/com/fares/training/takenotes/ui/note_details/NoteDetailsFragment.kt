package com.fares.training.takenotes.ui.note_details

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fares.training.takenotes.R
import com.fares.training.takenotes.data.local.Note
import com.fares.training.takenotes.ui.BaseFragment
import com.fares.training.takenotes.ui.dialogs.AddOwnerToNoteDialog
import com.fares.training.takenotes.util.Constants.Dialog.ADD_OWNER_DIALOG_TAG
import com.fares.training.takenotes.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_note_details.*

@AndroidEntryPoint
class NoteDetailsFragment : BaseFragment(R.layout.fragment_note_details) {

    private val vm: NoteDetailsViewModel by viewModels()

    private val args: NoteDetailsFragmentArgs by navArgs()

    private var note: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        fabEditNote.setOnClickListener {
            findNavController().navigate(
                NoteDetailsFragmentDirections.actionNoteDetailsFragmentToAddEditNote(args.id)
            )
        }

        savedInstanceState?.let {
            val addOwnerDialog =
                parentFragmentManager.findFragmentByTag(ADD_OWNER_DIALOG_TAG) as AddOwnerToNoteDialog?
            addOwnerDialog?.setPositiveListener {
                addOwnerToCurrentNote(it)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_owner, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_owner -> {
                showAddOwnerDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMarkdownText(text: String) {
        val markwon = Markwon.create(requireContext())
        val markdown = markwon.toMarkdown(text)
        markwon.setParsedMarkdown(tvNoteContent, markdown)
    }

    private fun subscribeToObservers() {
        vm.observeNoteById(args.id).observe(viewLifecycleOwner) {
            it?.let { note ->
                tvNoteTitle.text = note.title
                setMarkdownText(note.content)
                this.note = note
            } ?: showSnackBar("Note not found")
        }
        vm.addOwnerStatus.observe(viewLifecycleOwner) {event->
            event?.getContentIfNotHandled()?.let { resource ->
                when(resource){
                    is Resource.Success -> {
                        addOwnerProgressBar.visibility = GONE
                        showSnackBar(resource.data ?: "Successfully Owner Added")
                    }
                    is Resource.Error -> {
                        addOwnerProgressBar.visibility = GONE
                        showSnackBar(resource.message ?: "An unknown error occurred")
                    }
                    is Resource.Loading -> {
                        addOwnerProgressBar.visibility = VISIBLE
                    }
                }
            }
        }
    }

    private fun showAddOwnerDialog() {
        AddOwnerToNoteDialog().apply {
            setPositiveListener {
                addOwnerToCurrentNote(it)
            }
        }.show(parentFragmentManager, ADD_OWNER_DIALOG_TAG)
    }

    private fun addOwnerToCurrentNote(email: String) {
        this.note?.let { note ->
            vm.addOwnerToNote(email, note.id)
        }
    }
}