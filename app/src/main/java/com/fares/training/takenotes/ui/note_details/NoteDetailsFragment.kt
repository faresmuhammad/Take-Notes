package com.fares.training.takenotes.ui.note_details

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.result.launch
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fares.training.takenotes.R
import com.fares.training.takenotes.data.local.Note
import com.fares.training.takenotes.ui.BaseFragment
import com.fares.training.takenotes.ui.dialogs.AddOwnerToNoteDialog
import com.fares.training.takenotes.utils.*
import com.fares.training.takenotes.utils.Constants.Dialog.ADD_OWNER_DIALOG_TAG
import com.fares.training.takenotes.utils.Constants.Permission.EXTERNAL_STORAGE_REQUEST_CODE
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_note_details.*
import javax.inject.Inject

@AndroidEntryPoint
class NoteDetailsFragment : BaseFragment(R.layout.fragment_note_details) {

    private val vm: NoteDetailsViewModel by viewModels()

    private val args: NoteDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var pref:SharedPreferences

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
            R.id.action_add_image -> {
                if (!checkExternalStoragePermission()) {
                    handleRequestPermissionsCases(pref) {
                        toast("This Permission needed to pick an image.")
                    }
                }
                pickAnImageContract.launch()
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
        vm.addOwnerStatus.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        addOwnerProgressBar.visibility = GONE
                        showSnackBar(resource.data ?: "Successfully Owner Added")
                    }
                    is Resource.Error -> {
                        addOwnerProgressBar.visibility = GONE
                        showSnackBar(resource.message )
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

    private val pickAnImageContract = registerForActivityResult(PickAnImageContract()) { uri ->
        imagePicked.setImageURI(uri)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toast("Permission Granted")
            } else {
                requestExternalStoragePermission()
            }
        }
    }
}