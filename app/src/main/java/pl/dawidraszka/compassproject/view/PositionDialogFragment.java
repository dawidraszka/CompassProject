package pl.dawidraszka.compassproject.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import pl.dawidraszka.compassproject.R;
import pl.dawidraszka.compassproject.model.data.SimplePosition;

public class PositionDialogFragment extends DialogFragment {

    private PositionDialogListener listener;
    private Button okButton;
    private EditText latitudeEditText;
    private EditText longitudeEditText;

    private SimplePosition destination;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (PositionDialogListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_set_destination, null);

        okButton = view.findViewById(R.id.ok_button);

        view.findViewById(R.id.cancel_button).setOnClickListener(v -> getDialog().cancel());

        TextWatcher textChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textUpdated();
            }
        };

        latitudeEditText = view.findViewById(R.id.latitude_et);
        longitudeEditText = view.findViewById(R.id.longitude_et);

        latitudeEditText.addTextChangedListener(textChangedListener);
        longitudeEditText.addTextChangedListener(textChangedListener);

        okButton.setOnClickListener(v -> {
            listener.onDialogPositiveClick(destination);
            this.dismiss();
        });

        builder.setView(view)
                .setTitle(R.string.set_destination);
        return builder.create();
    }

    private void textUpdated() {
        double latitude;
        double longitude;

        try {
            latitude = Double.parseDouble(latitudeEditText.getText().toString());
            longitude = Double.parseDouble(longitudeEditText.getText().toString());

            if (latitudeCorrect(latitude) && longitudeCorrect(longitude)) {
                correctDestination(new SimplePosition(latitude, longitude));
            } else {
                incorrectDestination();
            }

        } catch (NumberFormatException e) {
            incorrectDestination();
        }
    }

    private boolean latitudeCorrect(double latitude) {
        return latitude >= -90 && latitude <= 90;
    }

    private boolean longitudeCorrect(double longitude) {
        return longitude >= -180 && longitude <= 180;
    }

    private void correctDestination(SimplePosition destination) {
        this.destination = destination;
        okButton.setEnabled(true);
    }

    private void incorrectDestination() {
        destination = null;
        okButton.setEnabled(false);
    }

    interface PositionDialogListener {
        void onDialogPositiveClick(SimplePosition destination);
    }
}
