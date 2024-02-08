package com.hadjmohamed.oran_agro.AdminAndDelivery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.Order;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity implements RecViewInterface {

    private FirebaseFirestore firestore;
    private List<Order> productOrderList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_page_delivery_boy);

        // Navigation bar Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationPageDeliveryBoy);
        bottomNavigationView.setSelectedItemId(R.id.placeNavigationPageDeliveryBoy);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeNavigationPageDeliveryBoy) {
                startActivity(new Intent(OrdersActivity.this, HomePageAdminActivity.class));
                return true;
            } else if (id == R.id.placeNavigationPageDeliveryBoy) {
                return true;
            } else if (id == R.id.accountNavigationPageDeliveryBoy) {
                return true;
            } else {
                return true;
            }
        });

        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        uploadOrder();
    }

    private void uploadOrder() {
        productOrderList.clear();
        firestore.collection("Orders")
                .whereEqualTo("orderSituation", "تم شحن")
                .orderBy("idOrder", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Task Failed", String.valueOf(task.getException()));
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (DocumentSnapshot dc :
                                task.getResult().getDocuments()) {
                            Log.d("PRODUCTS", dc.toString());
                            productOrderList.add(dc.toObject(Order.class));
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onItemClick(String view, int position) {
        Intent intent = new Intent(OrdersActivity.this, takenOrderDeliveryBoyActivity.class);
        intent.putExtra("orderId", productOrderList.get(position).getIdOrder());
        intent.putExtra("clientId", productOrderList.get(position).getIdClient());
        startActivity(intent);

    }
}