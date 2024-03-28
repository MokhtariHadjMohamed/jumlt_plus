package com.hadjmohamed.oran_agro;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hadjmohamed.oran_agro.AdminAndDelivery.Sale;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdapterRecTableSales extends RecyclerView.Adapter<HolderRecTableSale> {

    private final RecViewInterface recViewInterface;
    Context context;
    List<Sale> saleList;

    public AdapterRecTableSales(Context context, List<Sale> saleList, RecViewInterface recViewInterface) {
        this.recViewInterface = recViewInterface;
        this.context = context;
        this.saleList = saleList;
    }

    @NonNull
    @Override
    public HolderRecTableSale onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderRecTableSale(LayoutInflater.from(context).
                inflate(R.layout.row_sales_rec_item, parent, false), recViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecTableSale holder, int position) {
        getDeliveryBoy(saleList.get(position).getUidClient())
                .thenAccept(deliveryBoyName -> {
                    holder.name.setText(deliveryBoyName);
                }).exceptionally(exception -> {
                    // Handle exception here
                    return null;
                });

        holder.totalPrice.setText(String.valueOf(saleList.get(position).getTotal()));
        holder.paidPrice.setText(String.valueOf(saleList.get(position).getTotalPayed()));
        holder.remaining.setText(String.valueOf(
                saleList.get(position).getTotal() -
                        saleList.get(position).getTotalPayed()
        ));
    }

    private CompletableFuture<String> getDeliveryBoy(String uid) {
        CompletableFuture<String> future = new CompletableFuture<>();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final String[] name = new String[1];
        firestore.collection("Users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.d("error", "get Delivery boy table sales adapter");
                        User user = task.getResult().toObject(User.class);
                        future.complete(user.getName());
                    }
                });
        return future;
    }

    @Override
    public int getItemCount() {
        return saleList.size();
    }

}
