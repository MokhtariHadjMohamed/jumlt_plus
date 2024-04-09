package com.hadjmohamed.oran_agro.AdminAndDelivery;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecProductOrders;
import com.hadjmohamed.oran_agro.Order;
import com.hadjmohamed.oran_agro.ProductOrder;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import java.util.ArrayList;
import java.util.List;

public class OrderInfoActivity extends AppCompatActivity implements View.OnClickListener, RecViewInterface {

    // TODO Firebase Declaration
    private FirebaseFirestore firestore;
    // TODO Element Declaration
    private TextView orderId, totalOrder;
    private Button submit, cancel;
    private ImageView btnGoBack;
    private String orderSituation;

    // TODO DeliveryBoy Declaration
    private Spinner deliveryBoy;
    private ArrayAdapter arrayAdapter;
    private List<User> userList;
    private List<String> nameList;

    // TODO RecyclerView Declaration
    private RecyclerView recyclerView;
    private List<ProductOrder> productOrderList;
    private AdapterRecProductOrders adapterRecOrders;

    // TODO Progress Dialog Declaration
    private ProgressDialog progressDialog;

    // TODO Dialog variable Declaration
    private MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        // TODO Firebase
        firestore = FirebaseFirestore.getInstance();

        // TODO Element
        orderSituation = getIntent().getStringExtra("orderSituation");
        orderId = findViewById(R.id.IdOrderInfoActivity);
        totalOrder = findViewById(R.id.priceTotalOrderInfoActivity);
        submit = findViewById(R.id.takeOrderInfoDeliveryBoy);
        cancel = findViewById(R.id.cancelOrderInfoDeliveryBoy);

        orderId.setText(getIntent().getStringExtra("orderId"));

        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);

        // TODO DeliveryBoy Declaration
        deliveryBoy = findViewById(R.id.spinnerDeliveryBoyOrderInfo);
        userList = new ArrayList<>();
        nameList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                nameList);
        getUser();

        if (orderSituation.equals("تم إلغاء الشحنة")) {
            submit.setText("تم إلغاء الشحنة");
            submit.setEnabled(false);
            cancel.setVisibility(View.GONE);
            cancel.setEnabled(false);
            deliveryBoy.setVisibility(View.GONE);
        } else if (orderSituation.equals("تم توصيل")) {
            submit.setText("تم توصيل");
            submit.setEnabled(false);
            cancel.setVisibility(View.GONE);
            cancel.setEnabled(false);
            deliveryBoy.setVisibility(View.GONE);
        }

        // TODO toolBar
        Toolbar toolbar = findViewById(R.id.toolbarBack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(this);

        // TODO Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // TODO Recycler View
        recyclerView = findViewById(R.id.recViewOrderInfoPage);
        firestore = FirebaseFirestore.getInstance();
        productOrderList = new ArrayList<>();
        adapterRecOrders = new AdapterRecProductOrders(getApplicationContext(),
                productOrderList, this);
        loadOrder();
    }

    private void getUser() {
        firestore.collection("Users")
                .whereEqualTo("type", "deliveryBoy")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "onComplete: error get deliveryBoy order info");
                            return;
                        }
                        for (QueryDocumentSnapshot d : task.getResult()){
                            userList.add(d.toObject(User.class));
                            nameList.add(d.toObject(User.class).getName());
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                });
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deliveryBoy.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view == submit) {
            dialogBuilder = new MaterialAlertDialogBuilder(this);
            dialogBuilder.setMessage("هل تريد شحن هذا الطلب؟");
            dialogBuilder.setTitle("الطلب");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (ProductOrder o : productOrderList)
                        o.setOrderSituation("تم الشحن");
                    firestore.collection("Orders")
                            .document(getIntent().getStringExtra("orderId"))
                            .update(
                                    "orderSituation", "تم شحن",
                                    "deliveryBoyId", FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    "productOrders", productOrderList,
                                    "deliveryBoyId", userList.get(deliveryBoy.getSelectedItemPosition()).getIdUser()
                            );
                    startActivity(new Intent(OrderInfoActivity.this,
                            HomePageAdminActivity.class));
                    finish();
                }
            });

            dialogBuilder.setNegativeButton("لا", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            alertDialog = dialogBuilder.create();
            alertDialog.show();
        } else if (view == btnGoBack) {
            startActivity(new Intent(OrderInfoActivity.this, HomePageAdminActivity.class));
            finish();
        } else if (view == cancel) {
            dialogBuilder = new MaterialAlertDialogBuilder(this);
            dialogBuilder.setMessage("هل تريد إلغاء هذا الطلب؟");
            dialogBuilder.setTitle("الطلب");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (ProductOrder o : productOrderList)
                        o.setOrderSituation("تم إلغاء الشحنة");
                    firestore.collection("Orders")
                            .document(getIntent().getStringExtra("orderId")).update(
                                    "orderSituation", "تم إلغاء الشحنة",
                                    "deliveryBoyId", FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    "productOrders", productOrderList
                            );
                    startActivity(new Intent(OrderInfoActivity.this,
                            HomePageAdminActivity.class));
                    finish();
                }
            });

            dialogBuilder.setNegativeButton("لا", (DialogInterface.OnClickListener) (dialog, which) -> {
                // If user click no then dialog box is canceled.
                dialog.cancel();
            });

            alertDialog = dialogBuilder.create();
            alertDialog.show();
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
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setMessage("هل تريد حذف هذا المنتج؟");
        dialogBuilder.setTitle("حذف");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                productOrderList.remove(position);
                adapterRecOrders.notifyDataSetChanged();
            }
        });

        dialogBuilder.setNegativeButton("لا", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}