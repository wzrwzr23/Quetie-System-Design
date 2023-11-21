package io.catroll.iot.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.catroll.iot.R;

public class Feature1Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feature_1, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LineChart lineChart = getView().findViewById(R.id.line_chart);
        try {

            String[] dates = {"2023-11-21 13:00", "2023-11-22 15:00", "2023-11-23 17:00", "2023-11-24 14:00", "2023-11-25 16:00"};
            int[] people = {50, 60, 70, 55, 65};

            List<Entry> entries = new ArrayList<>();
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (int i = 0; i < dates.length; i++) {
                try {
                    long timestamp = dateTimeFormat.parse(dates[i]).getTime();
                    entries.add(new Entry(timestamp, people[i]));
                } catch (Exception e) {
                    // Handle parse exception
                }
            }

            LineDataSet dataSet = new LineDataSet(entries, "Number of People");
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);

            lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                private final SimpleDateFormat mFormat = new SimpleDateFormat("E HH:mm");

                @Override
                public String getFormattedValue(float value) {
                    return mFormat.format(new Date((long) value));
                }
            });

            lineChart.invalidate(); // Refresh the chart
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }

    public static final String TAG = "Feature1Fragment";
}
