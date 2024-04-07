package com.hadjmohamed.oran_agro.AdminAndDelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecTableSales;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SalesActivity extends AppCompatActivity implements View.OnClickListener, RecViewInterface {

    // TODO Firebase var
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private User user;
    // TODO Recycle View var
    private RecyclerView recyclerView;
    private AdapterRecTableSales adapterRecTableSales;
    private ProgressDialog progressDialog;
    private List<Sale> saleList;
    // TODO Btn var
    private CardView home, add, category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        // TODO Firebase Declaration
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // TODO Recycle View Declaration
        recyclerView = findViewById(R.id.recyclerViewSale);
        saleList = new ArrayList<>();
        adapterRecTableSales = new AdapterRecTableSales(this,
                saleList, this);
        getUser().thenAccept(deliveryBoy -> {
            if (user.getType().equals("admin") || user.getType().equals("employee"))
                getSales();
            else
                getSalesDeliveryBoy();
        }).exceptionally(exception -> {
            // Handle exception here
            return null;
        });


        // TODO btn Declaration
        home = findViewById(R.id.homeBtnSalesActivity);
        add = findViewById(R.id.NewSalesActivity);
        category = findViewById(R.id.categorySalesActivity);

        home.setOnClickListener(this);
        add.setOnClickListener(this);
        category.setOnClickListener(this);

        // TODO Progress Declaration
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();
    }

    private CompletableFuture<String> getUser() {
        CompletableFuture<String> future = new CompletableFuture<>();
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Error", "get user logged in Sales activity");
                        }
                        user = task.getResult().toObject(User.class);
                        future.complete(user.getType());
                    }
                });
        return future;
    }

    private void getSales() {
        saleList.clear();
        firestore.collection("Sales")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Error", "get sales function in Sales activity");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            return;
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            saleList.add(d.toObject(Sale.class));
                        adapterRecTableSales.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(SalesActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableSales);
    }

    private void getSalesDeliveryBoy() {
        saleList.clear();
        firestore.collection("Sales")
                .whereEqualTo("uidEmployee", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Error", "get sales function in Sales activity");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            return;
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            saleList.add(d.toObject(Sale.class));
                        adapterRecTableSales.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(SalesActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableSales);
    }

    private void getSalesNotPayed() {
        saleList.clear();
        firestore.collection("Sales")
                .whereEqualTo("totalPayed", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Error", "get sales function in Sales activity");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            saleList.add(d.toObject(Sale.class));
                        adapterRecTableSales.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(SalesActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableSales);
    }

    private void getSalesALLPayed() {
        saleList.clear();
        firestore.collection("Sales")
                .whereGreaterThan("totalPayed", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Error", "get sales function in Sales activity");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            saleList.add(d.toObject(Sale.class));
                        adapterRecTableSales.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(SalesActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableSales);
    }
    private void getSalesNotPayedDeliveryBoy() {
        saleList.clear();
        firestore.collection("Sales")
                .whereEqualTo("totalPayed", 0)
                .whereEqualTo("uidEmployee", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Error", "get sales function in Sales activity");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            saleList.add(d.toObject(Sale.class));
                        adapterRecTableSales.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(SalesActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableSales);
    }

    private void getSalesALLPayedDeliveryBoy() {
        saleList.clear();
        firestore.collection("Sales")
                .whereGreaterThan("totalPayed", 0)
                .whereEqualTo("uidEmployee", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Error", "get sales function in Sales activity");
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            saleList.add(d.toObject(Sale.class));
                        adapterRecTableSales.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(SalesActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableSales);
    }

    @Override
    public void onClick(View view) {
        if (home == view) {
            startActivity(new Intent(SalesActivity.this,
                    HomePageAdminActivity.class));
            finish();
        } else if (add == view) {
            startActivity(new Intent(SalesActivity.this,
                    NewSaleActivity.class));
        } else if (category == view) {
            TextView textView = findViewById(R.id.categoryTextSales);
            switch (textView.getText().toString()) {
                case "كل":
                    progressDialog.show();
                    textView.setText("غير مدفوع");
                    if (user.getType().equals("admin") || user.getType().equals("employee"))
                        getSalesNotPayed();
                    else
                        getSalesNotPayedDeliveryBoy();
                    break;
                case "غير مدفوع":
                    progressDialog.show();
                    textView.setText("مدفوع");
                    if (user.getType().equals("admin") || user.getType().equals("employee"))
                        getSalesALLPayed();
                    else
                        getSalesALLPayedDeliveryBoy();
                    break;
                case "مدفوع":
                    progressDialog.show();
                    textView.setText("كل");
                    if (user.getType().equals("admin") || user.getType().equals("employee"))
                        getSales();
                    else
                        getSalesDeliveryBoy();
                    break;
            }
        }
    }

    @Override
    public void onItemClick(String view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("uid", saleList.get(position).getUid());
        bundle.putString("collection", "Sales");
        FrameLayout frameLayout = findViewById(R.id.Sale);
        frameLayout.setElevation(500);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.Sale, SaleEditFragment.class, bundle)
                .commit();
    }
}