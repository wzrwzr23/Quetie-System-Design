package io.catroll.iot.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

import io.catroll.iot.R;
import io.catroll.iot.task.Config;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Feature2Fragment extends Fragment {
    private Button chooseDateTimeButton;
    private TextView predictedNumberTextView;
    private TextView selectedStallTextView;
    private TextView selectedDateTimeTextView; // Added TextView for selected date and time
    private Spinner stallSpinner;
    private ProgressBar loadingProgressBar;

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
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

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
        String predictedNumber = calculatePredictedNumber(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", selectedDateTime).toString().substring(11));
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

    private String calculatePredictedNumber(String selectedDateTime) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client = new OkHttpClient();
        // Specify the URL you want to send the GET request to
        String url = "http://" + Config.IP_PORT + "/feature2?time_param="+selectedDateTime;
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String s = response.body().string();
            // Handle the response here
            Log.d("feat2_get_try", "onCreateView: " + s);
            return s;
        } catch (Exception e) {
            Log.e("feat2_error", "feature 2: cannot get data." );
            return "error";
        }
    }
}
