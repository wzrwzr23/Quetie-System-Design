package io.catroll.iot.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Random;

import io.catroll.iot.R;

public class Feature2Fragment extends Fragment {
    private Button chooseDateTimeButton;
    private TextView predictedNumberTextView;
    private TextView selectedStallTextView;
    private TextView selectedDateTimeTextView; // Added TextView for selected date and time
    private Spinner stallSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feature_2, container, false);

        chooseDateTimeButton = view.findViewById(R.id.chooseDateTimeButton);
        stallSpinner = view.findViewById(R.id.stallSpinner);
        predictedNumberTextView = view.findViewById(R.id.predictedNumberTextView);
        selectedStallTextView = view.findViewById(R.id.selectedStallTextView);
        selectedDateTimeTextView = view.findViewById(R.id.selectedDateTimeTextView); // Initialize the TextView for selected date and time

        // Set up the Spinner with your list of stalls
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.stall_list,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stallSpinner.setAdapter(adapter);

        chooseDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
        return view;
    }

    private void handleStallSelection() {
        String selectedStall = "Selected Stall: " + stallSpinner.getSelectedItem().toString();
        selectedStallTextView.setText(selectedStall);
        selectedStallTextView.setVisibility(View.VISIBLE);
    }

    private void handleDateTimeSelection(Calendar selectedDateTime) {
        String formattedDateTime = "Selected Date and Time: " + android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", selectedDateTime).toString();
//        Toast.makeText(requireContext(), formattedDateTime, Toast.LENGTH_LONG).show();

        // Display the selected date and time in the TextView
        selectedDateTimeTextView.setText(formattedDateTime);
        selectedDateTimeTextView.setVisibility(View.VISIBLE);

        // Assuming a method to calculate the predicted number based on the selectedDateTime
        int predictedNumber = calculatePredictedNumber(selectedDateTime);

        // Display the predicted number in the TextView
        String predictedNumString = "Predicted Number: " + predictedNumber;
        predictedNumberTextView.setText(predictedNumString);
        predictedNumberTextView.setVisibility(View.VISIBLE);
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                requireContext(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Round the minute to the nearest 15-minute interval
                                        minute = (minute / 15) * 15;

                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);
                                        handleDateTimeSelection(calendar);
                                    }
                                },
                                hour,
                                minute,
                                true

                        );

                        timePickerDialog.show();
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
        handleStallSelection();
    }

    // A placeholder method for calculating the predicted number (replace with logic)
    private int calculatePredictedNumber(Calendar selectedDateTime) {
        int min = 20;
        int max = 100;

        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}
