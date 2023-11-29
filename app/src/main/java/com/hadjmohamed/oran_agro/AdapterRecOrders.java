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

public class AdapterRecOrders extends RecyclerView.Adapter<HolderRecOrders> {

    private final RecViewInterface recViewInterface;
    private ImageView imageProduct;
    Context context;
    List<ProductOrder> productList;

    public AdapterRecOrders(Context context, List<ProductOrder> productList, RecViewInterface recViewInterface) {
        this.context = context;
        this.productList = productList;
        this.recViewInterface = recViewInterface;
    }

    @NonNull
    @Override
    public HolderRecOrders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecOrders(LayoutInflater.from(context).
                inflate(R.layout.order_rec_item, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecOrders holder, int position) {
        holder.productName.setText(productList.get(position).getProductName());
        holder.priceProduct.setText(productList.get(position).getProductPrice() + "");
        holder.orderSituation.setText(productList.get(position).getOrderSituation());
        holder.qnt.setText(String.valueOf(productList.get(position).getQuantity()));
        retrieveImage(holder.imageView, productList.get(position).getProductName());
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
