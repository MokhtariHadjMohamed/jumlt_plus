package com.hadjmohamed.oran_agro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePageDeliveryBoyActivity extends AppCompatActivity implements RecViewInterface{

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private List<Order> productOrderList;
    private AdapterRecOrdersDeliveryBoy adapterRecOrders;
    private ProgressDialog progressDialog;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_delivery_boy);

        // Navigation bar Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationPageDeliveryBoy);
        bottomNavigationView.setSelectedItemId(R.id.homeNavigationPageDeliveryBoy);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeNavigationPageDeliveryBoy) {
                return true;
            } else if (id == R.id.placeNavigationPageDeliveryBoy) {
                startActivity(new Intent(HomePageDeliveryBoyActivity.this, OrdersPageDeliveryBoyActivity.class));
                return true;
            } else if (id == R.id.accountNavigationPageDeliveryBoy) {
                startActivity(new Intent(HomePageDeliveryBoyActivity.this, AccountPageDeliveryBoyActivity.class));
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
        recyclerView = findViewById(R.id.recHomeDeliveryBoyActivity);
        firestore = FirebaseFirestore.getInstance();
        productOrderList = new ArrayList<>();
        adapterRecOrders = new AdapterRecOrdersDeliveryBoy(getApplicationContext(),
                productOrderList, this);

        loadOrder();

    }

    private void loadOrder(){
        productOrderList.clear();
        adapterRecOrders.notifyDataSetChanged();
        firestore.collection("Orders")
                .whereEqualTo("orderSituation","في انتظار شحن")
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
        Intent intent =
                new Intent(HomePageDeliveryBoyActivity.this, OrderInfoPageDeliveryBoy.class);
        intent.putExtra("orderId", productOrderList.get(position).getIdOrder());
        startActivity(intent);
    }

}