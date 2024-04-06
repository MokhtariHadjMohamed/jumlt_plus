package com.hadjmohamed.oran_agro.AdminAndDelivery;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecOrders;
import com.hadjmohamed.oran_agro.Order;
import com.hadjmohamed.oran_agro.ProductOrder;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

import java.util.ArrayList;
import java.util.List;

public class OrderInfoPageDeliveryBoy extends AppCompatActivity implements View.OnClickListener, RecViewInterface {

    private TextView orderId, totalOrder;
    private Button submit;
    private FirebaseFirestore firestore;
    private ImageView btnGoBack;
    private List<ProductOrder> productOrderList;
    private AdapterRecOrders adapterRecOrders;
    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    //dialog variable
    private MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info_page_delivery_boy);

        orderId = findViewById(R.id.IdOrderInfoActivity);
        orderId.setText(getIntent().getStringExtra("orderId"));
        totalOrder = findViewById(R.id.priceTotalOrderInfoActivity);

        //toolBar
        Toolbar toolbar = findViewById(R.id.toolbarBack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(this);

        submit = findViewById(R.id.takeOrderInfoDeliveryBoy);
        submit.setOnClickListener(this);

        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // Recycler View
        recyclerView = findViewById(R.id.recViewOrderInfoPage);
        firestore = FirebaseFirestore.getInstance();
        productOrderList = new ArrayList<>();
        adapterRecOrders = new AdapterRecOrders(getApplicationContext(),
                productOrderList, this);

        loadOrder();
    }

    @Override
    public void onClick(View view) {
        if (view == submit) {
            dialogBuilder = new MaterialAlertDialogBuilder(this);
            dialogBuilder.setMessage("هل تريد استلام هذا الطلب؟");
            dialogBuilder.setTitle("الطلب");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("take the order", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for(ProductOrder o: productOrderList)
                        o.setOrderSituation("تم الشحن");
                    firestore.collection("Orders")
                            .document(getIntent().getStringExtra("orderId")).update(
                                    "orderSituation", "تم شحن",
                                    "deliveryBoyId", FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    "productOrders", productOrderList
                            );
                    startActivity(new Intent(OrderInfoPageDeliveryBoy.this,
                            HomePageAdminActivity.class));
                    finish();
                }
            });

            dialogBuilder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            alertDialog = dialogBuilder.create();
            alertDialog.show();
        } else if (view == btnGoBack) {
            startActivity(new Intent(OrderInfoPageDeliveryBoy.this, HomePageAdminActivity.class));
            finish();
        }
    }

    private void loadOrder() {
        productOrderList.clear();
        adapterRecOrders.notifyDataSetChanged();
        firestore.collection("Orders")
                .document(getIntent().getStringExtra("orderId"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        int total = 0;
                        if (!task.isSuccessful()) {
                            Log.e("Task Failed", String.valueOf(task.getException()));
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        Order order = task.getResult().toObject(Order.class);
                        for (ProductOrder po :
                                order.getProductOrders()) {
                            Log.d("PRODUCTS", po.toString());
                            total += po.getQuantity() * po.getProductPrice();
                            productOrderList.add(po);
                        }
                        totalOrder.setText(String.valueOf(total));
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
        Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
    }
}