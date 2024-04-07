package com.hadjmohamed.oran_agro;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecOrders;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecProductOrders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoppingCartActivity extends AppCompatActivity implements RecViewInterface, View.OnClickListener {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private List<ProductOrder> productOrderList;
    private AdapterRecProductOrders adapterRecOrders;
    private ProgressDialog progressDialog;
    private Button submit;
    //dialog variable
    private MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        submit = findViewById(R.id.submitOrdersShooppingCartActivity);
        submit.setOnClickListener(this);

        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // Recycler View
        recyclerView = findViewById(R.id.orderProdectShoppingCartActivity);
        firestore = FirebaseFirestore.getInstance();
        productOrderList = new ArrayList<>();
        adapterRecOrders = new AdapterRecProductOrders(getApplicationContext(),
                productOrderList, this);

        uploadProductsOrder();
        loadOrder();

        // Navigation bar Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar_home);
        bottomNavigationView.setSelectedItemId(R.id.shoppingCartNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeNavigation) {
                startActivity(new Intent(ShoppingCartActivity.this, HomePageActivity.class));
                return true;
            } else if (id == R.id.searchNavigation) {
                startActivity(new Intent(ShoppingCartActivity.this, SearchPageActivity.class));
                return true;
            } else if (id == R.id.accountNavigation) {
                startActivity(new Intent(ShoppingCartActivity.this, UserAccountActivity.class));
                return true;
            } else if (id == R.id.categoryNavigation) {
                startActivity(new Intent(ShoppingCartActivity.this, CategoryPageActivity.class));
                return true;
            } else if (id == R.id.shoppingCartNavigation) {
                return true;
            } else {
                return true;
            }
        });
    }

    private void submitOrder(List<ProductOrder> productOrders, String orderSituation) {
        Toast.makeText(this, orderSituation, Toast.LENGTH_SHORT).show();
        List<ProductOrder> po = new ArrayList<ProductOrder>();
        float total = 0;
        for (ProductOrder o : productOrders){
            if (Objects.equals(o.getOrderSituation(), "في انتظار شحن"))
                po.add(o);
            total =+ o.getProductPrice();
        }
        DocumentReference order = firestore.collection("Orders").document();
        order.set(new Order(order.getId(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        po,
                        total,
                        orderSituation,
                        null)).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Orders", "Order Create");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Orders", e.getMessage());
                    }
                });
    }

    private void uploadProductsOrder() {
        productOrderList.clear();
        adapterRecOrders.notifyDataSetChanged();
        firestore.collection("ProductsOrder")
                .whereEqualTo("idClient",
                        FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                                task.getResult().getDocuments())
                            productOrderList.add(dc.toObject(ProductOrder.class));

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

    private void loadOrder() {
        productOrderList.clear();
        adapterRecOrders.notifyDataSetChanged();
        firestore.collection("Orders")
                .whereEqualTo("idClient",
                        FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                                task.getResult().getDocuments())
                            if (!dc.toObject(Order.class).getOrderSituation().equals("تم توصيل"))
                                for (ProductOrder o :
                                        dc.toObject(Order.class).getProductOrders())
                                    productOrderList.add(o);

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
        Toast.makeText(this, productOrderList.get(position).getOrderSituation(), Toast.LENGTH_SHORT).show();
        if (!Objects.equals(productOrderList.get(position).getOrderSituation(), "في انتظار شحن")
                && !Objects.equals(productOrderList.get(position).getOrderSituation(), "تم الشحن")) {

            dialogBuilder = new MaterialAlertDialogBuilder(ShoppingCartActivity.this);
            dialogBuilder.setMessage("Do you want delete this?");
            dialogBuilder.setTitle("Delete");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(ShoppingCartActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                }
            });

            dialogBuilder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == submit) {
            int total = 0;
            for (ProductOrder o :
                    productOrderList) {
                if (Objects.equals(o.getOrderSituation(), "في انتظار تاكيد"))
                    total += (o.getQuantity() * o.getProductPrice());

            }
            dialogBuilder = new MaterialAlertDialogBuilder(ShoppingCartActivity.this);
            dialogBuilder.setMessage("هل أنت متأكد تريد شراء هذه المنتجات؟، سعر: " + total + "دج");
            dialogBuilder.setTitle("Confirmation");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (ProductOrder o :
                            productOrderList) {
                        if (Objects.equals(o.getOrderSituation(), "في انتظار تاكيد")) {
                            firestore.collection("ProductsOrder").document(
                                    o.getIdOrder()).delete();
                            o.setOrderSituation("في انتظار شحن");
                        }
                    }
                    submitOrder(productOrderList, "في انتظار شحن");
                    loadOrder();
                }
            });

            dialogBuilder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }
}