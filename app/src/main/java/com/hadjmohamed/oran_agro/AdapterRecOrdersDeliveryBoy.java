package com.hadjmohamed.oran_agro;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdapterRecOrdersDeliveryBoy extends RecyclerView.Adapter<HolderRecOrdersDeliveryBoy> {

    private final RecViewInterface recViewInterface;
    private ImageView imageProduct;
    Context context;
    List<Order> productOrderList;

    public AdapterRecOrdersDeliveryBoy(Context context, List<Order> productOrderList, RecViewInterface recViewInterface) {
        this.context = context;
        this.productOrderList = productOrderList;
        this.recViewInterface = recViewInterface;
    }

    @NonNull
    @Override
    public HolderRecOrdersDeliveryBoy onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecOrdersDeliveryBoy(LayoutInflater.from(context).
                inflate(R.layout.order_item_delivery, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecOrdersDeliveryBoy holder, int position) {
        holder.orderId.setText(productOrderList.get(position).getIdOrder());
        getUser(holder.nameId, productOrderList.get(position).getIdClient());
    }

    @Override
    public int getItemCount() {
        return productOrderList.size();
    }
    private void getUser(TextView name, String idUser) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users")
                .document(idUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Failed", "Failed");
                        name.setText(task.getResult().toObject(User.class).getName());
                    }
                });
    }

}
