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

public class AdapterRecProduct extends RecyclerView.Adapter<HolderRecProduct> {

    private final RecViewInterface recViewInterface;
    Context context;
    List<Product> productList;

    public AdapterRecProduct(Context context, List<Product> productList, RecViewInterface recViewInterface) {
        this.recViewInterface = recViewInterface;
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public HolderRecProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecProduct(LayoutInflater.from(context).
                inflate(R.layout.grid_item_salles, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecProduct holder, int position) {


        if (productList.get(position) == null || productList.get(position).getImageUrl() == null || productList.get(position).getImageUrl().isEmpty())
            holder.imageView.setImageResource(R.drawable.baseline_image_not_supported_24);
        else
            Picasso.get()
                    .load(productList.get(position).getImageUrl())
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.baseline_image_not_supported_24)
                    .into(holder.imageView);

        holder.productName.setText(productList.get(position).getNameProduct());
        holder.productPriceCarton.setText(productList.get(position).getPrixCarton() + "");
        holder.productPricePiece.setText(productList.get(position).getPrixUnitaire() + "");
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
