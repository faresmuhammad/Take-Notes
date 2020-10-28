package com.fares.training.takenotes.ui.notes

import android.content.SharedPreferences
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fares.training.takenotes.R
import com.fares.training.takenotes.data.local.Note
import com.fares.training.takenotes.ui.BaseFragment
import com.fares.training.takenotes.ui.adapters.NoteAdapter
import com.fares.training.takenotes.utils.Constants.Preferences.KEY_LOGGED_EMAIL
import com.fares.training.takenotes.utils.Constants.Preferences.KEY_LOGGED_PASSWORD
import com.fares.training.takenotes.utils.Constants.Preferences.NO_EMAIL
import com.fares.training.takenotes.utils.Constants.Preferences.NO_PASSWORD
import com.fares.training.takenotes.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_notes.*
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment : BaseFragment(R.layout.fragment_notes) {

    private val vm: NotesViewModel by viewModels()

    @Inject
    lateinit var pref: SharedPreferences

    private lateinit var noteAdapter: NoteAdapter

    private val swipingItem = MutableLiveData(false)

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
        requireActivity().requestedOrientation = SCREEN_ORIENTATION_USER
        setupSwipeRefreshLayout()
        setupRecyclerView()
        fabAddNote.setOnClickListener {
            findNavController().navigate(NotesFragmentDirections.actionNotesFragmentToAddEditNote(""))
        }

        subscribeToObservers()

        noteAdapter.setOnItemClickListener { note, _ ->
            findNavController().navigate(
                NotesFragmentDirections.actionNotesFragmentToNoteDetailsFragment(note.id)
            )
        }
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            vm.syncAllNotes()
        }
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
    ) {

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                swipingItem.postValue(isCurrentlyActive)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.notes[position]
            vm.deleteNote(note.id)
            Snackbar.make(requireView(), "Note was successfully deleted", Snackbar.LENGTH_LONG)
                .apply {
                    setAction("Undo") {
                        vm.insertNote(note)
                        vm.deleteLocallyDeletedNoteId(note.id)
                    }
                    show()
                }
        }
    }

    private fun setupRecyclerView() = rvNotes.apply {
        noteAdapter = NoteAdapter()
        adapter = noteAdapter
        layoutManager = LinearLayoutManager(requireContext())
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
    }

    private fun subscribeToObservers() {
        vm.allNotes.observe(viewLifecycleOwner) {
            it?.let { event ->
                when (val result = event.peekContent()) {
                    is Resource.Success<List<Note>> -> {
                        noteAdapter.notes = result.data!!
                        swipeRefreshLayout.isRefreshing = false
                    }
                    is Resource.Error<List<Note>> -> {
                        event.getContentIfNotHandled()?.let { error ->
                            showSnackBar(error.message)
                        }
                        result.data?.let { notes ->
                            noteAdapter.notes = notes
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
                    is Resource.Loading<List<Note>> -> {
                        result.data?.let { notes ->
                            noteAdapter.notes = notes
                        }
                        swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        }

        swipingItem.observe(viewLifecycleOwner) {
            swipeRefreshLayout.isEnabled = !it
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_notes, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        pref.edit {
            putString(KEY_LOGGED_EMAIL, NO_EMAIL)
            putString(KEY_LOGGED_PASSWORD, NO_PASSWORD)
        }
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.notesFragment, true).build()
        findNavController().navigate(
            NotesFragmentDirections.actionNotesFragmentToAuthFragment(),
            navOptions
        )
    }


}