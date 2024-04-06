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
import com.hadjmohamed.oran_agro.Product;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

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
        retrieveImage(holder.imageView, productList.get(position).getNameProduct());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void retrieveImage(ImageView imageView, String image) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("Image")
                .child(image + ".png");

        final File file;
        try {
            file = File.createTempFile("img", ".png");

            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    imageView.setImageResource(R.drawable.baseline_image_not_supported_24);
                    Log.e("Image: " + image , e.getMessage());
                }
            });
        } catch (IOException e) {
            imageView.setImageResource(R.drawable.baseline_image_not_supported_24);
            throw new RuntimeException(e);
        }

    }

}
