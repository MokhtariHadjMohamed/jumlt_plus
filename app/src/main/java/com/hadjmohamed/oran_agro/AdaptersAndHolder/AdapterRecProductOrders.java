package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hadjmohamed.oran_agro.models.Product;
import com.hadjmohamed.oran_agro.models.ProductOrder;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdapterRecProductOrders extends RecyclerView.Adapter<HolderRecProductOrders> {

    private final RecViewInterface recViewInterface;
    private ImageView imageProduct;
    Context context;
    List<ProductOrder> productList;

    public AdapterRecProductOrders(Context context, List<ProductOrder> productList,
                                   RecViewInterface recViewInterface) {
        this.context = context;
        this.productList = productList;
        this.recViewInterface = recViewInterface;
    }

    @NonNull
    @Override
    public HolderRecProductOrders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecProductOrders(LayoutInflater.from(context).
                inflate(R.layout.order_rec_item, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecProductOrders holder, int position) {
        holder.productName.setText(productList.get(position).getProductName());
        holder.priceProduct.setText(productList.get(position).getProductPrice() + "");
        holder.orderSituation.setText(productList.get(position).getOrderSituation());
        holder.qnt.setText(String.valueOf(productList.get(position).getQuantity()));


        getProduct(productList.get(position).getIdProduct(), holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void getProduct(String uid, ImageView imageView) {
        FirebaseFirestore.getInstance().collection("Products")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        Log.e("Firestore", "Failed to get product");
                        imageView.setImageResource(R.drawable.baseline_image_not_supported_24);
                        return;
                    }

                    Product product = task.getResult().toObject(Product.class);
                    if (product == null || product.getImageUrl() == null || product.getImageUrl().isEmpty()) {
                        imageView.setImageResource(R.drawable.baseline_image_not_supported_24);
                    } else {
                        Picasso.get()
                                .load(product.getImageUrl())
                                .placeholder(R.drawable.loading_image)
                                .error(R.drawable.baseline_image_not_supported_24)
                                .into(imageView);
                    }
                });
    }

}
