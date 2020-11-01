package com.fares.training.takenotes.ui.note_details

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
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
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NoteDetailsFragment : BaseFragment(R.layout.fragment_note_details) {

    private val vm: NoteDetailsViewModel by viewModels()

    private val args: NoteDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var pref: SharedPreferences

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
                pickMultipleImages.launch(
                    Intent.createChooser(
                        multipleImagesIntent,
                        "Select Picture"
                    )
                )
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
                vm.getNotePicture(note.id)
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
                        showSnackBar(resource.message)
                    }
                    is Resource.Loading -> {
                        addOwnerProgressBar.visibility = VISIBLE
                    }
                }
            }
        }

        vm.addPictureStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success<String> -> {
                    Timber.d("Success Picture")
                    vm.getNotePicture(note?.id!!)
                }
                is Resource.Error<String> -> {

                }
                is Resource.Loading -> {

                }
            }
        }
        vm.notePicture.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    imagePicked.setImageBitmap(resource.data?.get(1))
                }
                is Resource.Error -> Timber.d("Error Loading")
                is Resource.Loading -> {
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


    /*private val pickMultipleImages =
        registerForActivityResult(PickMultipleImagesContract()) { selectedImages ->
            val images = mutableListOf<ByteArray>()
            for (image in selectedImages){
                images.add(image.toByteArray(requireContext())!!)
            }
            vm.addPictureToNote(note?.id!!,images)
        }*/
    private val pickMultipleImages =
        registerForActivityResult(StartActivityForResult()) { result ->
            Timber.d("Selected Images Count: ${getSelectedImages(result).size}")
            for (image in getSelectedImages(result)) {
                val encodedImage = image.toByteArray(requireContext())!!.toBase64String()
                Timber.d("Encoded Image: $encodedImage")
                vm.addPictureToNote(
                    note?.id!!,
                    encodedImage
                )
            }

        }

    private fun getSelectedImages(activityResult: ActivityResult): List<Uri> {
        val imagesSelected = mutableListOf<Uri>()
        val intent = activityResult.data
        val clipData = intent?.clipData

        clipData?.let { clip ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                for (i in 0 until clip.itemCount) {
                    val imageUri = clip.getItemAt(i).uri
                    Timber.d("Image Uri: $imageUri")
                    imagesSelected.add(imageUri)
                }
            }
        }
        if (clipData == null && activityResult.resultCode == Activity.RESULT_OK) {
            val imageUri = intent?.data
            imagesSelected.add(imageUri!!)
        }


        return imagesSelected
    }

    private val multipleImagesIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        type = "image/*"
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