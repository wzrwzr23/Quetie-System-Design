package io.catroll.iot.view;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.catroll.iot.R;
import io.catroll.iot.task.Config;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client = new OkHttpClient();
        String url = "http://" + Config.IP_PORT + "/feature1?date=2020-1-19";
        Request request = new Request.Builder()
                .url(url)
                .build();
        String[] dates = null;
        int[] people = null;
        try (Response response = client.newCall(request).execute()) {
            String s = response.body().string();
            // Handle the response here
            Log.d(TAG, "onCreateView: " + s);
            JSONObject jsonObject = new JSONObject(s);
            JSONArray datesJson = jsonObject.getJSONArray("dates");
            JSONArray ppl = jsonObject.getJSONArray("people");
            dates = new String[datesJson.length()];
            for (int i = 0; i < datesJson.length(); i++) {
                dates[i] = datesJson.getString(i);
            }
            people = new int[ppl.length()];
            for (int i = 0; i < ppl.length(); i++) {
                people[i] = ppl.getInt(i);
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: ", e);
        }

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
            xAxis.setLabelCount(4, true);
        }

        YAxis yAxis;
        {
            yAxis = chart.getAxisLeft();
            chart.getAxisRight().setEnabled(false);
            yAxis.setAxisMinimum(0);
            yAxis.setAxisMaximum(100);
        }

        chart.invalidate(); // Refresh the chart

        TextView nop = view.findViewById(R.id.nop);
        new Thread(() -> {
            while (true) {
                String nopUrl = "http://" + Config.IP_PORT + "/nop";
                Request nopRequest = new Request.Builder()
                        .url(nopUrl)
                        .build();

                try (Response response = client.newCall(nopRequest).execute()) {
                    String s = response.body().string();
                    // Handle the response here
                    getActivity().runOnUiThread(() -> {
                        nop.setText(s);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "onCreateView: ", e);
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "onCreateView: ", e);
                }
            }
        }).start();
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
