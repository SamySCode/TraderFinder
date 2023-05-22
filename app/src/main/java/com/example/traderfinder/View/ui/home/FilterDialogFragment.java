package com.example.traderfinder.View.ui.home;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.traderfinder.R;

public class FilterDialogFragment extends DialogFragment {

    private Spinner tradeSpinner;
    private EditText locationEditText;

    private FilterDialogListener listener;

    public FilterDialogFragment(FilterDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_filter_dialog, null);

        tradeSpinner = view.findViewById(R.id.spinner_trade);
        locationEditText = view.findViewById(R.id.edittext_location);

        String[] trades = getResources().getStringArray(R.array.trades_array);
        ArrayAdapter<String> tradeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, trades);
        tradeSpinner.setAdapter(tradeAdapter);

        Button applyButton = view.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(v -> {
            String selectedTrade = (String) tradeSpinner.getSelectedItem();
            String selectedLocation = locationEditText.getText().toString();
            listener.onFilterApplied(selectedTrade, selectedLocation);
            dismiss();
        });

        Button clearButton = view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> {
            listener.onFilterApplied("", "");
            dismiss();
        });

        return new Dialog(requireContext(), getTheme()) {{
            setContentView(view);
        }};
    }

    public interface FilterDialogListener {
        void onFilterApplied(String trade, String location);
    }
}
