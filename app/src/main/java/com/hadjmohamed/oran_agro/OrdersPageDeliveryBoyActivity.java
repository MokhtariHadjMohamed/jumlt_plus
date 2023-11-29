package com.hadjmohamed.oran_agro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdersPageDeliveryBoyActivity extends AppCompatActivity implements RecViewInterface {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private List<Order> productOrderList;
    private AdapterRecOrdersDeliveryBoy adapterRecOrders;
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
                startActivity(new Intent(OrdersPageDeliveryBoyActivity.this, HomePageDeliveryBoyActivity.class));
                return true;
            } else if (id == R.id.placeNavigationPageDeliveryBoy) {
                return true;
            } else if (id == R.id.accountNavigationPageDeliveryBoy) {
                startActivity(new Intent(OrdersPageDeliveryBoyActivity.this, AccountPageDeliveryBoyActivity.class));
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


        // Recycler View
        recyclerView = findViewById(R.id.recOrderPageDeliveryBoy);
        firestore = FirebaseFirestore.getInstance();
        productOrderList = new ArrayList<>();
        adapterRecOrders = new AdapterRecOrdersDeliveryBoy(getApplicationContext(),
                productOrderList, this);

        uploadOrder();
    }

    private void uploadOrder() {
        productOrderList.clear();
        adapterRecOrders.notifyDataSetChanged();
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
                        adapterRecOrders.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });

        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecOrders);
    }

    @Override
    public void onItemClick(String view, int position) {
        Intent intent = new Intent(OrdersPageDeliveryBoyActivity.this, takenOrderDeliveryBoyActivity.class);
        intent.putExtra("orderId", productOrderList.get(position).getIdOrder());
        intent.putExtra("clientId", productOrderList.get(position).getIdClient());
        startActivity(intent);

    }
}