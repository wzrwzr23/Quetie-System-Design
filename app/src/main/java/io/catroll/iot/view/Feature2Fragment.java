package io.catroll.iot.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import io.catroll.iot.R;
import io.catroll.iot.task.Config;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Feature2Fragment extends Fragment {
    private TextView predictedNumberTextView;
    private TextView selectedDateTimeTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feature_2, container, false);

        Button chooseDateTimeButton = view.findViewById(R.id.chooseDateTimeButton);
        predictedNumberTextView = view.findViewById(R.id.predictedNumberTextView);
        selectedDateTimeTextView = view.findViewById(R.id.selectedDateTimeTextView);
        chooseDateTimeButton.setOnClickListener(v -> showDateTimePicker());
        return view;
    }


    private void handleDateTimeSelection(Calendar selectedDateTime) {
        String dayOfWeek = Integer.toString(selectedDateTime.get(Calendar.DAY_OF_WEEK));

        if (dayOfWeek.equals("1")){
            dayOfWeek = "7";
        }else{
            dayOfWeek = Integer.toString(selectedDateTime.get(Calendar.DAY_OF_WEEK)-1);
        }
        Log.d("dayofweek", dayOfWeek);
        String formattedDateTime = "Selected Date and Time: " + android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", selectedDateTime).toString();

        // Display the selected date and time in the TextView
        selectedDateTimeTextView.setText(formattedDateTime);
        selectedDateTimeTextView.setVisibility(View.VISIBLE);

        // Assuming a method to calculate the predicted number based on the selectedDateTime
        String predictedNumber = calculatePredictedNumber(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", selectedDateTime).toString().substring(11), dayOfWeek);
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
                (view, year1, month1, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            requireContext(),
                            (view1, hourOfDay, minute1) -> {
                                // Round the minute to the nearest 15-minute interval
                                minute1 = (minute1 / 15) * 15;

                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute1);
                                handleDateTimeSelection(calendar);
                            },
                            hour,
                            minute,
                            true

                    );

                    timePickerDialog.show();
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private String calculatePredictedNumber(String selectedDateTime, String weekday) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client = new OkHttpClient();
        // Specify the URL you want to send the GET request to
        String url = "http://" + Config.IP_PORT + "/feature2?time_param="+selectedDateTime+"&weekday="+weekday;
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
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
