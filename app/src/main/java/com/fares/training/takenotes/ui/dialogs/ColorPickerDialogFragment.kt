package com.fares.training.takenotes.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class ColorPickerDialogFragment : DialogFragment() {

    private var positiveListener: ((String) -> Unit)? = null

    fun setPositiveListener(listener: (String) -> Unit) {
        positiveListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return ColorPickerDialog.Builder(requireContext()).apply {
            setTitle("Choose a Color")
            setPositiveButton("Ok", object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    positiveListener?.let { click ->
                        envelope?.let {
                            click(it.hexCode)
                        }
                    }
                }
            })
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()

            }
            setBottomSpace(10)
            attachAlphaSlideBar(true)
            attachBrightnessSlideBar(true)
        }.create()
    }
}