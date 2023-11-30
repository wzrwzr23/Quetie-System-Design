package io.catroll.iot.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.catroll.iot.R;
import io.catroll.iot.data.Product;
import io.catroll.iot.task.Config;
import io.catroll.iot.view.adapter.CatalogueAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Feature4Fragment extends Fragment {

    RecyclerView catalogueRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feature_4, container, false);

        List<Product> pList = new ArrayList<>();
        catalogueRecyclerView = view.findViewById(R.id.rv_catalogue);
        catalogueRecyclerView.setVisibility(View.GONE);
        CatalogueAdapter adapter = new CatalogueAdapter(pList);
        catalogueRecyclerView.setAdapter(adapter);
        catalogueRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2, GridLayoutManager.VERTICAL, false));

        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            String url = "http://" + Config.IP_PORT + "/feature4";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            String[] v = new String[]{"sampleV"};
            String[] m = new String[]{"sampleM"};
            boolean[] a = new boolean[]{false};
            String[] w = new String[]{"sampleW"};
            String [] c = new String[]{"sampleC"};
            try (Response response = client.newCall(request).execute()) {
                String s = response.body().string();
                // Handle the response here
                Log.d(TAG, "feature 4: " + s);
                JSONObject jsonObject = new JSONObject(s);
                JSONArray vJson = jsonObject.getJSONArray("vendors");
                JSONArray mJson = jsonObject.getJSONArray("menu");
                JSONArray aJson = jsonObject.getJSONArray("availability");
                JSONArray wJson = jsonObject.getJSONArray("avg_waiting_time");
                JSONArray cJson = jsonObject.getJSONArray("estimated_total_customers");
                v = new String[vJson.length()];
                for (int i = 0; i < vJson.length(); i++) {
                    v[i] = vJson.getString(i);
                }
                m = new String[mJson.length()];
                for (int i = 0; i < mJson.length(); i++) {
                    m[i] = mJson.getString(i);
                }
                a = new boolean[aJson.length()];
                for (int i = 0; i < aJson.length(); i++) {
                    a[i] = aJson.getBoolean(i);
                }
                w = new String[wJson.length()];
                for (int i = 0; i < wJson.length(); i++) {
                    w[i] = wJson.getString(i);
                }
                c = new String[cJson.length()];
                for (int i = 0; i < cJson.length(); i++) {
                    c[i] = cJson.getString(i);
                }
            } catch (Exception e) {
                Log.e(TAG, "feature 4: cannot get data." );
            }

            for (int i=0; i<v.length; i++) {
                pList.add(new Product(v[i], loadDrawableFromAssets(view.getContext(), (i+1)+".jpg"), m[i], a[i], w[i], c[i]));
            }

            getActivity().runOnUiThread(() -> {
                adapter.notifyItemRangeInserted(0, pList.size());
                catalogueRecyclerView.setVisibility(View.VISIBLE);
            });
        }).start();

        return view;
    }

    public static Drawable loadDrawableFromAssets(Context context, String fileName) {
        try (InputStream is = context.getAssets().open(fileName)) {
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            Log.e(TAG, "loadDrawableFromAssets: ");
            return null;
        }
    }

    public static final String TAG = "Feature4Fragment";
}
