package com.hadjmohamed.oran_agro.AdminAndDelivery;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecOrders;
import com.hadjmohamed.oran_agro.Order;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OrdersActivity extends AppCompatActivity implements RecViewInterface, View.OnClickListener {

    // TODO Firebase Declaration
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private User user;
    // TODO RecyclerView Declaration
    private RecyclerView recyclerViewTable;
    private AdapterRecOrders adapterRecOrders;
    private List<Order> orderList;
    // TODO ProgressDialog Declaration
    private ProgressDialog progressDialog;
    // TODO Elements Declaration
    private CardView home, category;
    private TextView textCategory;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        // TODO Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // TODO ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // TODO RecyclerView
        recyclerViewTable = findViewById(R.id.recOrderActivity);
        orderList = new ArrayList<>();
        adapterRecOrders = new AdapterRecOrders(this,
                orderList, this);
        getUser().thenAccept(type -> {
            if (type.equals("deliveryBoy"))
                getOrderDeliveryBoy();
            else
                getOrder();
        }).exceptionally(throwable -> {
            Log.e(TAG, "onCreate: " + throwable);
            return null;
        });


        // TODO Element
        home = findViewById(R.id.homeOrdersActivity);
        category = findViewById(R.id.allOrdersActivity);
        textCategory = findViewById(R.id.textCategoryOrder);
        searchView = findViewById(R.id.searchView);

        home.setOnClickListener(this);
        category.setOnClickListener(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getExpensesByNameUser(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
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
                            Log.e(TAG, "onComplete: error get user orders");
                            return;
                        }
                        user = task.getResult().toObject(User.class);
                        future.complete(user.getType());
                    }
                });
        return future;
    }

    private void getOrder() {
        orderList.clear();
        firestore.collection("Orders")
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
                            orderList.add(dc.toObject(Order.class));
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        adapterRecOrders.notifyDataSetChanged();
                    }
                });
        recyclerViewTable.setLayoutManager(new
                LinearLayoutManager(OrdersActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerViewTable.setAdapter(adapterRecOrders);
    }

    private void getOrderDeliveryBoy() {
        orderList.clear();
        firestore.collection("Orders")
                .orderBy("idOrder", Query.Direction.ASCENDING)
                .whereEqualTo("deliveryBoyId", auth.getCurrentUser().getUid())
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
                            orderList.add(dc.toObject(Order.class));
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        adapterRecOrders.notifyDataSetChanged();
                    }
                });
        recyclerViewTable.setLayoutManager(new
                LinearLayoutManager(OrdersActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerViewTable.setAdapter(adapterRecOrders);
    }

    private void getOrder(String s) {
        orderList.clear();
        firestore.collection("Orders")
                .orderBy("idOrder", Query.Direction.ASCENDING)
                .whereEqualTo("orderSituation", s)
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
                            orderList.add(dc.toObject(Order.class));
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        adapterRecOrders.notifyDataSetChanged();
                    }
                });
        recyclerViewTable.setLayoutManager(new
                LinearLayoutManager(OrdersActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerViewTable.setAdapter(adapterRecOrders);
    }

    private void getOrderDeliveryBoy(String s) {
        orderList.clear();
        firestore.collection("Orders")
                .orderBy("idOrder", Query.Direction.ASCENDING)
                .whereEqualTo("orderSituation", s)
                .whereEqualTo("deliveryBoyId", auth.getCurrentUser().getUid())
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
                            orderList.add(dc.toObject(Order.class));
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        adapterRecOrders.notifyDataSetChanged();
                    }
                });
        recyclerViewTable.setLayoutManager(new
                LinearLayoutManager(OrdersActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerViewTable.setAdapter(adapterRecOrders);
    }

    private void getExpensesByNameUser(String s) {
        firestore.collection("Users")
                .where(Filter.and(
                        Filter.or(Filter.equalTo("name", s),
                                Filter.equalTo("phone", s),
                                Filter.equalTo("email", s),
                                Filter.equalTo("familyName", s)),
                        Filter.or(Filter.equalTo("type", "user")
                        )))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Error on get on expenses");
                        }
                        for (QueryDocumentSnapshot d : task.getResult())
                            if (user.getType().equals("deliveryBoy"))
                                getOrderDeliveryBoySearch(d.toObject(User.class).getIdUser());
                            else
                                getOrderSearch(d.toObject(User.class).getIdUser());
                    }
                });
    }

    private void getOrderSearch(String uid) {
        orderList.clear();
        firestore.collection("Orders")
                .orderBy("idOrder", Query.Direction.ASCENDING)
                .whereEqualTo("idClient", uid)
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
                            orderList.add(dc.toObject(Order.class));
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        adapterRecOrders.notifyDataSetChanged();
                    }
                });
        recyclerViewTable.setLayoutManager(new
                LinearLayoutManager(OrdersActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerViewTable.setAdapter(adapterRecOrders);
    }

    private void getOrderDeliveryBoySearch(String uid) {
        orderList.clear();
        firestore.collection("Orders")
                .orderBy("idOrder", Query.Direction.ASCENDING)
                .whereEqualTo("idClient", uid)
                .whereEqualTo("deliveryBoyId", auth.getCurrentUser().getUid())
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
                            orderList.add(dc.toObject(Order.class));
                        }
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        adapterRecOrders.notifyDataSetChanged();
                    }
                });
        recyclerViewTable.setLayoutManager(new
                LinearLayoutManager(OrdersActivity.this,
                LinearLayoutManager.VERTICAL, false));
        recyclerViewTable.setAdapter(adapterRecOrders);
    }

    @Override
    public void onItemClick(String view, int position) {
        if (orderList.get(position).getOrderSituation().equals("في انتظار الشحن")
                || orderList.get(position).getOrderSituation().equals("تم إلغاء الشحنة")
                || orderList.get(position).getOrderSituation().equals("تم توصيل")) {
            Intent intent = new Intent(OrdersActivity.this, OrderInfoActivity.class);
            intent.putExtra("orderId", orderList.get(position).getIdOrder());
            intent.putExtra("clientId", orderList.get(position).getIdClient());
            intent.putExtra("orderSituation", orderList.get(position).getOrderSituation());
            startActivity(intent);
        } else {
            Intent intent = new Intent(OrdersActivity.this, takenOrderActivity.class);
            intent.putExtra("orderId", orderList.get(position).getIdOrder());
            intent.putExtra("clientId", orderList.get(position).getIdClient());
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == home) {
            startActivity(new Intent(OrdersActivity.this, HomePageAdminActivity.class));
            finish();
        } else if (view == category) {
            switch (textCategory.getText().toString()) {
                case "كل":
                    if (!user.getType().equals("deliveryBoy")) {
                        getOrder("في انتظار الشحن");
                        textCategory.setText("في انتظار الشحن");
                        break;
                    }
                case "في انتظار الشحن":
                    if (user.getType().equals("deliveryBoy"))
                        getOrderDeliveryBoy("تم شحن");
                    else
                        getOrder("تم شحن");
                    textCategory.setText("تم شحن");
                    break;
                case "تم شحن":
                    if (user.getType().equals("deliveryBoy"))
                        getOrderDeliveryBoy("تم توصيل");
                    else
                        getOrder("تم توصيل");
                    textCategory.setText("تم توصيل");
                    break;
                case "تم توصيل":
                    if (!user.getType().equals("deliveryBoy")) {
                        getOrder("تم إلغاء الشحنة");
                        textCategory.setText("تم إلغاء الشحنة");
                        break;
                    }
                case "تم إلغاء الشحنة":
                    if (user.getType().equals("deliveryBoy"))
                        getOrderDeliveryBoy();
                    else
                        getOrder();
                    textCategory.setText("كل");
                    break;
            }
        }
    }
}