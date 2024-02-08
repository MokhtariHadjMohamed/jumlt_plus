package com.hadjmohamed.oran_agro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private GridView gridView;
    private GridAdapter gridAdapter;
    private SearchView searchView;
    private List<Product> products;
    private FirebaseFirestore firestore;
    private Button callNumber;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        callNumber = findViewById(R.id.callUsePhone);
        callNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0662222300"));
                startActivity(intent);
            }
        });

        // Search View
        searchView = findViewById(R.id.searchHomePage);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(HomePageActivity.this,
                         SearchPageActivity.class);
                intent.putExtra("search", s);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        // Navigation bar Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar_home);
        bottomNavigationView.setSelectedItemId(R.id.homeNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeNavigation) {
                return true;
            } else if (id == R.id.searchNavigation) {
                startActivity(new Intent(HomePageActivity.this,
                        SearchPageActivity.class));
                return true;
            } else if (id == R.id.accountNavigation) {
                startActivity(new Intent(HomePageActivity.this,
                        UserAccountActivity.class));
                return true;
            } else if (id == R.id.categoryNavigation) {
                startActivity(new Intent(HomePageActivity.this,
                        CategoryPageActivity.class));
                return true;
            } else if (id == R.id.shoppingCartNavigation) {
                startActivity(new Intent(HomePageActivity.this,
                        ShoppingCartActivity.class));
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

        // GridView
        gridView = findViewById(R.id.gridItem);
        products = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        gridAdapter = new GridAdapter(HomePageActivity.this, products);

        getProduct();
    }

    private void getProduct() {
        firestore.collection("Products")
                .orderBy("idProduct", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }
                        for (DocumentChange dc :
                                value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                products.add(dc.getDocument().toObject(Product.class));
                            }
                            gridAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    }
                });
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(HomePageActivity.this, ProductActivity.class);
        intent.putExtra("IdProduct", products.get(i).getIdProduct());
        Toast.makeText(this, products.get(i).getIdProduct() + "", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}