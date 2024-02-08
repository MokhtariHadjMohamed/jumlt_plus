package com.hadjmohamed.oran_agro;

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

        retrieveImage(holder.imageView, productList.get(position).getNameProduct());
        holder.productName.setText(productList.get(position).getNameProduct());
        holder.productPriceCarton.setText(productList.get(position).getPrixCarton() + "");
        holder.productPricePiece.setText(productList.get(position).getPrixUnitaire() + "");
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
                    Log.e("Image", e.getMessage());
                }
            });
        } catch (IOException e) {
            imageView.setImageResource(R.drawable.baseline_image_not_supported_24);
            throw new RuntimeException(e);
        }
    }
}
