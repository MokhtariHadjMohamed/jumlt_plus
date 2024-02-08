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
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.HomePageActivity;
import com.hadjmohamed.oran_agro.LogInActivity;
import com.hadjmohamed.oran_agro.Order;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;
import com.hadjmohamed.oran_agro.User;

import java.util.ArrayList;
import java.util.List;

public class HomePageAdminActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private CardView sales, warehouse, newSale, expenses, employees, 
            deletedSales, clients, aboutProducts, orders;

    //firestor
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_delivery_boy);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        sales = findViewById(R.id.salesHomeActivity);
        warehouse = findViewById(R.id.warehouseHomeActivity);
        newSale = findViewById(R.id.newSaleHomeActivity);
        expenses = findViewById(R.id.expensesHomeActivity);
        employees = findViewById(R.id.employeesHomeActivity);
        deletedSales = findViewById(R.id.salesAreDeletedHomeActivity);
        clients = findViewById(R.id.clientHomeActivity);
        aboutProducts = findViewById(R.id.aboutProductsHomeActivity);
        orders = findViewById(R.id.ordersHomeActivity);
        
        sales.setOnClickListener(this);
        warehouse.setOnClickListener(this);
        newSale.setOnClickListener(this);
        expenses.setOnClickListener(this);
        clients.setOnClickListener(this);
        aboutProducts.setOnClickListener(this);

        getUserType();
    }

    private void getUserType(){
        firestore.collection("Users")
                .document(auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Failed Firestor", "Failed Users");
                        User user = task.getResult().toObject(User.class);
                        if (user.getType().equals("admin")) {
                            employees.setOnClickListener(HomePageAdminActivity.this);
                            deletedSales.setOnClickListener(HomePageAdminActivity.this);
                            orders.setOnClickListener(HomePageAdminActivity.this);
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == sales){
            startActivity(new Intent(this, SalesActivity.class));
        } else if (view == warehouse) {
            startActivity(new Intent(this, WarehouseActivity.class));
        } else if (view == newSale){
            startActivity(new Intent(this, NewSaleActivity.class));
        } else if (view == expenses) {
            startActivity(new Intent(this, ExpensesActivity.class));
        } else if (view == employees){
            startActivity(new Intent(this, EmployeesActivity.class));
        } else if (view == deletedSales) {
            startActivity(new Intent(this, DeletedSalesActivity.class));
        } else if (view == clients) {
            startActivity(new Intent(this, ClientsActivity.class));
        } else if (view == aboutProducts){
            startActivity(new Intent(this, AboutProductsActivity.class));
        } else if (view == orders) {
            startActivity(new Intent(this, OrdersActivity.class));
        }
    }
}