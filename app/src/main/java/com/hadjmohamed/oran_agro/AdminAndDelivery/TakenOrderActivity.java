package com.hadjmohamed.oran_agro.AdminAndDelivery;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecProductOrders;
import com.hadjmohamed.oran_agro.Order;
import com.hadjmohamed.oran_agro.Product;
import com.hadjmohamed.oran_agro.ProductOrder;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class TakenOrderActivity extends AppCompatActivity implements View.OnClickListener, RecViewInterface {

    // TODO Firebase Declaration
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    // TODO Dialog Product Var
    private Dialog dialogSale;
    private Spinner spinner;
    private TextView totalSubmitSale;
    private EditText payment;
    private Button submitPayment, cancelPayment;
    private List<String> namesList;
    private List<User> userList;

    // TODO Element Declaration
    private TextView orderId, totalOrder, userName, userPhoneNumber;
    private Button submit, goBtn;
    private ImageView btnGoBack;
    // TODO RecyclerView
    private List<ProductOrder> productOrderList;
    private Order order;
    private float total = 0;
    private AdapterRecProductOrders adapterRecOrders;
    private RecyclerView recyclerView;
    // TODO ProgressDialog Declaration
    private ProgressDialog progressDialog;
    // TODO dialog variable
    private MaterialAlertDialogBuilder dialogBuilder;
    private AlertDialog alertDialog;
    // TODO Location
    private final int FINE_PERMISSION_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taken_order);

        // TODO Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        orderId = findViewById(R.id.IdTakenOrderDeliveryBoy);
        orderId.setText(getIntent().getStringExtra("orderId"));
        totalOrder = findViewById(R.id.priceTotalTakenOrderDeliveryBoy);

        userName = findViewById(R.id.userNameTakenOrderDeliveryBoy);
        userPhoneNumber = findViewById(R.id.userPhoneNumberTakenOrderDeliveryBoy);
        userPhoneNumber.setOnClickListener(this);

        getUser(getIntent().getStringExtra("clientId"), "get");

        // TODO toolBar
        Toolbar toolbar = findViewById(R.id.toolbarBack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(this);

        // TODO Element
        goBtn = findViewById(R.id.takeTakenOrderDeliveryBoy);
        goBtn.setOnClickListener(this);
        submit = findViewById(R.id.submitTakenOrderDeliveryBoy);
        submit.setOnClickListener(this);

        // TODO Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // TODO dialog submit sale
        dialogSale = new Dialog(this);
        dialogSale.setCancelable(false);
        dialogSale.setContentView(R.layout.submit_order_sale);
        dialogSale.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        spinner = dialogSale.findViewById(R.id.buyerNeme);
        totalSubmitSale = dialogSale.findViewById(R.id.prudectPriceTotalPay);
        payment = dialogSale.findViewById(R.id.productPaymentSale);
        submitPayment = dialogSale.findViewById(R.id.submitPay);
        cancelPayment = dialogSale.findViewById(R.id.CancelPay);

        // TODO Recycler View
        recyclerView = findViewById(R.id.recViewTakenOrderDeliveryBoy);
        firestore = FirebaseFirestore.getInstance();
        productOrderList = new ArrayList<>();
        adapterRecOrders = new AdapterRecProductOrders(getApplicationContext(),
                productOrderList, this);

        loadOrder();

        // TODO Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
                        if (!task.isSuccessful()) {
                            Log.e("Task Failed", String.valueOf(task.getException()));
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        order = task.getResult().toObject(Order.class);
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
                    Geocoder geocoder = new Geocoder(TakenOrderActivity.this,
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
        firestore.collection("Users")
                .document(idUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Failed", "Failed");
                            return;
                        }
                        User user = task.getResult().toObject(User.class);
                        assert user != null;
                        if (typeBtn.equals("Click"))
                            getLastLocation(user.getLatitude(), user.getLongitude());
                        else {
                            userName.setText(String.valueOf(user.getName() + " " + user.getFamilyName()));
                            userPhoneNumber.setText(String.valueOf("+213" + user.getPhone()));
                        }
                    }
                });
    }

    private void getUser(String idUser) {
        firestore.collection("Users")
                .document(idUser)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Failed", "Failed");
                            return;
                        }
                        User user = task.getResult().toObject(User.class);
                        namesList.add(user.getName());
                        userList.add(user);

                        String[] names = new String[namesList.size()];
                        namesList.toArray(names);
                        ArrayAdapter arrayAdapter = new ArrayAdapter(TakenOrderActivity.this,
                                android.R.layout.simple_spinner_item, names);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(arrayAdapter);
                    }
                });
    }

    public CompletableFuture<List<Product>> convertFromProductToProductList(List<ProductOrder> productOrderList) {
        CompletableFuture<List<Product>> future = new CompletableFuture<>();
        List<Product> productList = new ArrayList<>();
        for (ProductOrder productOrder : productOrderList) {
            firestore.collection("Products")
                    .document(productOrder.getIdProduct())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e(TAG, "onComplete: convertFromProductToProductList function");
                                return;
                            }
                            Product product = task.getResult().toObject(Product.class);
                            product.setQuantite(productOrder.getQuantity());
                            productList.add(product);
                            future.complete(productList);
                        }
                    });
        }
        return future;
    }


    public void uploadSale() {
        convertFromProductToProductList(productOrderList)
                .thenAccept(productList -> {
                    Sale sale = new Sale();
                    sale.setUid(order.getIdOrder());
                    sale.setUidEmployee(auth.getCurrentUser().getUid());
                    sale.setUidClient(userList.get(spinner.getSelectedItemPosition()).getIdUser());
                    sale.setProducts(productList);
                    sale.setTotal(total);
                    sale.setTotalPayed(Float.parseFloat(payment.getText().toString()));
                    firestore.collection("Sales")
                            .document(order.getIdOrder())
                            .set(sale)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    startActivity(new Intent(TakenOrderActivity.this, HomePageAdminActivity.class));
                                    finish();
                                    dialogSale.dismiss();
                                }
                            });
                }).exceptionally(throwable -> {
                    Log.e(TAG, "onCreate: " + throwable);
                    return null;
                });
    }

    @Override
    public void onClick(View view) {
        if (view == submit) {
            namesList = new ArrayList<>();
            userList = new ArrayList<>();
            getUser(order.getDeliveryBoyId());
            totalOrder.setText(String.valueOf(total));
            payment.setText(String.valueOf(total));
            totalSubmitSale.setText(String.valueOf(total));
            submitPayment.setOnClickListener(this);
            cancelPayment.setOnClickListener(this);
            dialogSale.show();
        } else if (view == btnGoBack) {
            startActivity(new Intent(TakenOrderActivity.this,
                    OrdersActivity.class));
            finish();
        } else if (view == goBtn) {
            getUser(getIntent().getStringExtra("clientId"), "Click");
        } else if (view == userPhoneNumber) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + userPhoneNumber.getText().toString()));
            startActivity(intent);
        } else if (view == submitPayment) {
            dialogBuilder = new MaterialAlertDialogBuilder(this);
            dialogBuilder.setMessage("هل تم تسليم الطلبية؟");
            dialogBuilder.setTitle("الطلب");
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    uploadSale();
                    for (ProductOrder o : productOrderList)
                        o.setOrderSituation("تم توصيل");
                    firestore.collection("Orders")
                            .document(getIntent().getStringExtra("orderId")).update(
                                    "orderSituation", "تم توصيل",
                                    "deliveryBoyId",
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    "productOrders", productOrderList
                            );
                    startActivity(new Intent(TakenOrderActivity.this,
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
        } else if (view == cancelPayment) {
            dialogSale.dismiss();
        }
    }

    @Override
    public void onItemClick(String view, int position) {
        Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
    }

}