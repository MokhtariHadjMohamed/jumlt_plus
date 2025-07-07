package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hadjmohamed.oran_agro.models.Product;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdapterRecSearch extends RecyclerView.Adapter<HolderRecSearch> {

    private final RecViewInterface recViewInterface;
    Context context;
    List<Product> productList;

    public AdapterRecSearch(Context context, List<Product> productList, RecViewInterface recViewInterface) {
        this.context = context;
        this.productList = productList;
        this.recViewInterface = recViewInterface;
    }

    @NonNull
    @Override
    public HolderRecSearch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecSearch(LayoutInflater.from(context).
                inflate(R.layout.search_rec_item, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecSearch holder, int position) {
        holder.productName.setText(productList.get(position).getNameProduct());
        holder.priceProduct.setText(productList.get(position).getPrixCarton() + "");

        if (productList.get(position) == null || productList.get(position).getImageUrl() == null || productList.get(position).getImageUrl().isEmpty())
            holder.imageView.setImageResource(R.drawable.baseline_image_not_supported_24);
        else
            Picasso.get()
                    .load(productList.get(position).getImageUrl())
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.baseline_image_not_supported_24)
                    .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
