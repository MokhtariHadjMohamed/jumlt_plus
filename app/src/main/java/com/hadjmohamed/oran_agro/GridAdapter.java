package com.hadjmohamed.oran_agro;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
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

        // Progress
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        productImage = view.findViewById(R.id.productImage);
        nameProduct = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);

        nameProduct.setText(products.get(i).getNameProduct());
        productPrice.setText(String.valueOf(products.get(i).getPrixCarton()));
        retrieveImage(productImage, products.get(i).getNameProduct());

        return view;
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
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    imageView.setImageResource(R.drawable.baseline_image_not_supported_24);
                    Log.e("Image: " + image , e.getMessage());
                }
            });
        } catch (IOException e) {
            imageView.setImageResource(R.drawable.baseline_image_not_supported_24);
            throw new RuntimeException(e);
        }
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
