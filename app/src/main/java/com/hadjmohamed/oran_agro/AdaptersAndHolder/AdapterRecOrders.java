package com.hadjmohamed.oran_agro.AdaptersAndHolder;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import com.hadjmohamed.oran_agro.Order;
import com.hadjmohamed.oran_agro.ProductOrder;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AdapterRecOrders extends RecyclerView.Adapter<HolderRecOrders> {

    private final RecViewInterface recViewInterface;
    private ImageView imageProduct;
    Context context;
    List<Order> orderList;

    public AdapterRecOrders(Context context, List<Order> orderList,
                            RecViewInterface recViewInterface) {
        this.context = context;
        this.orderList = orderList;
        this.recViewInterface = recViewInterface;
    }

    @NonNull
    @Override
    public HolderRecOrders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecOrders(LayoutInflater.from(context).
                inflate(R.layout.row_rec_item, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecOrders holder, int position) {
        holder.total.setText(orderList.get(position).getTotal() + "");
        holder.orderSituation.setText(orderList.get(position).getOrderSituation());
        getUser(orderList.get(position).getIdClient(),holder.name);
    }

    private void getUser(String userUid, TextView nameUser) {
        FirebaseFirestore.getInstance().collection("Users")
                .document(userUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e(TAG, "onComplete: error get user AdapterRecExpenses");
                        nameUser.setText(task.getResult().toObject(User.class).getName());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
