package io.catroll.iot.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.catroll.iot.R;
import io.catroll.iot.data.Product;
import io.catroll.iot.view.adapter.CatalogueAdapter;

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
        List<String> dishes = new ArrayList<>();
        dishes.add("babi"); dishes.add("anjing");

        pList.add(new Product("1", loadDrawableFromAssets(view.getContext(), "1.jpg"), dishes, true));
        pList.add(new Product("2", loadDrawableFromAssets(view.getContext(), "2.jpg"), dishes, false));
        pList.add(new Product("3", loadDrawableFromAssets(view.getContext(), "3.png"), dishes, true));
        pList.add(new Product("4", loadDrawableFromAssets(view.getContext(), "4.png"), dishes, false));
        pList.add(new Product("5", loadDrawableFromAssets(view.getContext(), "5.png"), dishes, true));
        pList.add(new Product("6", loadDrawableFromAssets(view.getContext(), "6.png"), dishes, false));
        pList.add(new Product("7", loadDrawableFromAssets(view.getContext(), "7.png"), dishes, true));
        pList.add(new Product("8", loadDrawableFromAssets(view.getContext(), "8.png"), dishes, false));
        pList.add(new Product("9", loadDrawableFromAssets(view.getContext(), "9.png"), dishes, true));
        pList.add(new Product("10", loadDrawableFromAssets(view.getContext(), "10.png"), dishes, false));

        catalogueRecyclerView = view.findViewById(R.id.rv_catalogue);
        catalogueRecyclerView.setAdapter(new CatalogueAdapter(pList));
        catalogueRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3, GridLayoutManager.VERTICAL, false));

        return view;
    }

    public static Drawable loadDrawableFromAssets(Context context, String fileName) {
        try (InputStream is = context.getAssets().open(fileName)) {
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
