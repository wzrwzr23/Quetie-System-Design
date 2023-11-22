package io.catroll.iot.view;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.catroll.iot.R;

public class Feature1Fragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feature_1, container, false);
        LineChart chart = view.findViewById(R.id.line_chart);

        chart.getDescription().setEnabled(false);

        String[] dates = {"2023-11-21 13:00", "2023-11-22 15:00", "2023-11-23 17:00", "2023-11-24 14:00", "2023-11-25 16:00"};
        int[] people = {50, 60, 70, 55, 65};

        String[] formattedDates = convertDateTimeArray(dates);

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < formattedDates.length; i++) {
            entries.add(new Entry(i, people[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Number of People");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < formattedDates.length) {
                    return formattedDates[index];
                } else {
                    return "";
                }
            }
        });

        XAxis xAxis;
        {
            xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setAxisMinimum(0);
            xAxis.setAxisMaximum(dates.length - 1);
            xAxis.setGranularity(1);
        }

        YAxis yAxis;
        {
            yAxis = chart.getAxisLeft();
            chart.getAxisRight().setEnabled(false);
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(100);
        }

        chart.invalidate(); // Refresh the chart

        return view;
    }

    public static String[] convertDateTimeArray(String[] inputArray) {
        // Define the input and output date time formats
        DateTimeFormatter inputFormatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        }
        DateTimeFormatter outputFormatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            outputFormatter = DateTimeFormatter.ofPattern("E HH:mm");
        }

        // Create an array to hold the converted dates
        String[] outputArray = new String[inputArray.length];

        // Convert each date string in the input array
        for (int i = 0; i < inputArray.length; i++) {
            LocalDateTime dateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateTime = LocalDateTime.parse(inputArray[i], inputFormatter);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                outputArray[i] = dateTime.format(outputFormatter);
            }
        }

        return outputArray;
    }

    public static final String TAG = "Feature1Fragment";
}
