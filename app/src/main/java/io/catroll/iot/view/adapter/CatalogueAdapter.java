package io.catroll.iot.view.adapter;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

import io.catroll.iot.R;
import io.catroll.iot.data.Product;

public class CatalogueAdapter extends
        RecyclerView.Adapter<CatalogueAdapter.ViewHolder> {

    private final List<Product> mProductList;

    public CatalogueAdapter(List<Product> productList) {
        this.mProductList = productList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView1);
            imageView = itemView.findViewById(R.id.itemImage1);
            textView = itemView.findViewById(R.id.itemName1);
        }
    }

    @NonNull
    @Override
    public CatalogueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueAdapter.ViewHolder holder, int position) {
        Product product = mProductList.get(position);

        ImageView imageView = holder.imageView;
        Glide.with(holder.itemView.getContext()).load(
                product.isAvail() ? product.getImageSrc() :
                convertToBlackAndWhite(product.getImageSrc(), holder.itemView.getResources())
        ).into(imageView);

        holder.textView.setText(product.getName());


        holder.cardView.setOnClickListener(view -> {
            Dialog dialog = new Dialog(holder.itemView.getContext());
            dialog.setContentView(R.layout.dialog_dishes);
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            TextView textView = dialog.findViewById(R.id.text_dishes);
            textView.setText(product.getDishes());

            TextView wView = dialog.findViewById(R.id.text_waiting_time);
            wView.setText(product.getW());

            TextView cView = dialog.findViewById(R.id.text_estimated_customer);
            cView.setText(product.getC());
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }


    public static Drawable convertToBlackAndWhite(Drawable drawable, Resources resources) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); // Set saturation to 0 to create the black and white effect

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        Paint paint = new Paint();
        paint.setColorFilter(filter);

        Bitmap coloredBitmap;
        if (drawable instanceof BitmapDrawable) {
            coloredBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            coloredBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(coloredBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        Bitmap bwBitmap = Bitmap.createBitmap(coloredBitmap.getWidth(), coloredBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bwBitmap);
        canvas.drawBitmap(coloredBitmap, 0, 0, paint);

        return new BitmapDrawable(resources, bwBitmap);
    }
}

