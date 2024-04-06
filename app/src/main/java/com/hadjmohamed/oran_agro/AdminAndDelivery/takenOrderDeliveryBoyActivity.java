package com.hadjmohamed.oran_agro.AdminAndDelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.hadjmohamed.oran_agro.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class takenOrderDeliveryBoyActivity extends AppCompatActivity implements View.OnClickListener, RecViewInterface {

    private TextView orderId, totalOrder, userName, userPhoneNumber;
    private Button submit, goBtn;
    private FirebaseFirestore firestore;
    private ImageView btnGoBack;
    private List<ProductOrder> productOrderList;
    private AdapterRecOrders adapterRecOrders;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    //dialog variable
    private MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog alertDialog;
    // Location
    private final int FINE_PERMISSION_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taken_order_delivery_boy);

        orderId = findViewById(R.id.IdTakenOrderDeliveryBoy);
        orderId.setText(getIntent().getStringExtra("orderId"));
        totalOrder = findViewById(R.id.priceTotalTakenOrderDeliveryBoy);

        userName= findViewById(R.id.userNameTakenOrderDeliveryBoy);
        userPhoneNumber = findViewById(R.id.userPhoneNumberTakenOrderDeliveryBoy);
        userPhoneNumber.setOnClickListener(this);

        getUser(getIntent().getStringExtra("clientId"), "get");

        //toolBar
        Toolbar toolbar = findViewById(R.id.toolbarBack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(this);

        goBtn = findViewById(R.id.takeTakenOrderDeliveryBoy);
        goBtn.setOnClickListener(this);
        submit = findViewById(R.id.submitTakenOrderDeliveryBoy);
        submit.setOnClickListener(this);

        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // Recycler View
        recyclerView = findViewById(R.id.recViewTakenOrderDeliveryBoy);
        firestore = FirebaseFirestore.getInstance();
        productOrderList = new ArrayList<>();
        adapterRecOrders = new AdapterRecOrders(getApplicationContext(),
                productOrderList, this);

        loadOrder();

        // Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @Override
    public void onClick(View view) {
        if (view == submit) {
            dialogBuilder = new MaterialAlertDialogBuilder(this);
            dialogBuilder.setMessage("هل تم تسليم الطلبية؟");
            dialogBuilder.setTitle("الطلب");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for(ProductOrder o: productOrderList)
                        o.setOrderSituation("تم توصيل");
                    firestore.collection("Orders")
                            .document(getIntent().getStringExtra("orderId")).update(
                                    "orderSituation", "تم توصيل",
                                    "deliveryBoyId",
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    "productOrders", productOrderList
                            );
                    startActivity(new Intent(takenOrderDeliveryBoyActivity.this,
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
            startActivity(new Intent(takenOrderDeliveryBoyActivity.this,
                    OrdersActivity.class));
            finish();
        }else if (view == goBtn){
            getUser(getIntent().getStringExtra("clientId"), "Click");
        }else if (view == userPhoneNumber){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + userPhoneNumber.getText().toString()));
            startActivity(intent);
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

    private void getLastLocation(double latitude, double longitude) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(takenOrderDeliveryBoyActivity.this,
                            Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), FINE_PERMISSION_CODE);
                        Uri uri = Uri.parse("https://www.google.com/maps/dir/" +
                                (addressList.get(0).getLatitude()) + ","
                                + (addressList.get(0).getLongitude())
                                + "/" + latitude + "," + longitude);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Location", e.getMessage());
            }
        });
    }

    private void getUser(String idUser, String typeBtn) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users")
                .document(idUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()){
                            Log.e("Failed", "Failed");
                            return;
                        }
                        User user = task.getResult().toObject(User.class);
                        assert user != null;
                        if (typeBtn.equals("Click"))
                            getLastLocation(user.getLatitude(), user.getLongitude());
                        else{
                            userName.setText(String.valueOf(user.getName() + " " + user.getFamilyName()));
                            userPhoneNumber.setText(String.valueOf("+213" + user.getPhone()));
                        }
                    }
                });
    }
}