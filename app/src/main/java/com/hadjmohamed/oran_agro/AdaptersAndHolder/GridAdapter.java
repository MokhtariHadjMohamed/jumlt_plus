package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<Product> products;
    ProgressDialog progressDialog;

    public GridAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.grid_item, null);

        TextView nameProduct, productPrice;
        ImageView productImage;

        productImage = view.findViewById(R.id.productImage);
        nameProduct = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);

        nameProduct.setText(products.get(i).getNameProduct());
        productPrice.setText(String.valueOf(products.get(i).getPrixCarton()));

        if (products.get(i).getImageUrl().isEmpty())
            productImage.setImageResource(R.drawable.baseline_image_not_supported_24);
        else
            Picasso.get()
                    .load(products.get(i).getImageUrl())
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.baseline_image_not_supported_24)
                    .into(productImage);

        return view;
    }

}
