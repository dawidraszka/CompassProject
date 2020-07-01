package pl.dawidraszka.compassproject.view

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_set_destination.*
import kotlinx.android.synthetic.main.dialog_set_destination.view.*
import pl.dawidraszka.compassproject.R
import pl.dawidraszka.compassproject.model.SimplePosition

class PositionDialogFragment : DialogFragment() {

    internal lateinit var listener: PositionDialogListener

    interface PositionDialogListener {
        fun onDialogPositiveClick(destination: SimplePosition)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        listener = activity as PositionDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val layout = inflater.inflate(R.layout.dialog_set_destination, null)

            builder.setView(layout)
                .setMessage(R.string.set_destination)
                .setPositiveButton(
                    R.string.ok
                ) { dialog, _ ->
                    listener.onDialogPositiveClick(SimplePosition(layout.latitude_et.text.toString().toDouble(), layout.longitude_et.text.toString().toDouble()))
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}