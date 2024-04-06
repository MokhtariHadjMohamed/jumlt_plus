package com.hadjmohamed.oran_agro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecSearch;

import java.util.ArrayList;
import java.util.List;

public class SearchPageActivity extends AppCompatActivity implements RecViewInterface {

    private SearchView searchView;
    private RecyclerView recyclerViewSearch;
    private AdapterRecSearch adapterRecSearch;
    private List<Product> productList;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    private String searchVariable = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        searchView = findViewById(R.id.searchSearchPage);

        // Recycle view search
        recyclerViewSearch = findViewById(R.id.recSearchPage);
        firestore = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();
        adapterRecSearch = new AdapterRecSearch(getApplicationContext(),
                productList, this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchVariable = s;
                getUser();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // Get Search Other Activity
        Intent intent = getIntent();
        if (intent.getStringExtra("search") != null) {
            searchVariable = intent.getStringExtra("search");
            searchView.setQuery(searchVariable, false);
            getUser();
        }

        // Navigation bar Bottom
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar_home);
        bottomNavigationView.setSelectedItemId(R.id.searchNavigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeNavigation) {
                startActivity(new Intent(SearchPageActivity.this, HomePageActivity.class));
                return true;
            } else if (id == R.id.searchNavigation) {
                return true;
            } else if (id == R.id.accountNavigation) {
                startActivity(new Intent(SearchPageActivity.this, UserAccountActivity.class));
                return true;
            } else if (id == R.id.categoryNavigation) {
                startActivity(new Intent(SearchPageActivity.this, CategoryPageActivity.class));
                return true;
            } else if (id == R.id.shoppingCartNavigation) {
                startActivity(new Intent(SearchPageActivity.this, ShoppingCartActivity.class));
                return true;
            } else {
                return true;
            }
        });
    }

    private void getSubCategory(int idCategory) {
        firestore.collection("SubCategory")
                .whereEqualTo("idCategory", idCategory)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", "Failed");
                            return;
                        }
                        for (DocumentSnapshot ds : task.getResult().getDocuments())
                            getProduct(searchVariable, ds.toObject(Category.class).getIdSubCategory());
                    }
                });
    }

    private void categorySeparation(User user) {
        firestore.collection("Category")
                .whereEqualTo("code", user.getInvitation())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot ds : task.getResult().getDocuments())
                            getSubCategory(ds.toObject(Category.class).getIdCategory());
                    }
                });
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerViewSearch.setAdapter(adapterRecSearch);
    }


    private void getUser() {
        firestore.collection("Users")
                .document(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful())
                            Log.e("Failed", "Failed");
                        categorySeparation(task.getResult().toObject(User.class));
                    }
                });
    }


    private void getProduct(String s, int IDCategorie) {
        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        productList.clear();

        firestore.collection("Products")
                .whereEqualTo("IDCategorie", IDCategorie)
                .orderBy("idProduct", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful() || s.isEmpty()) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Task Failed", String.valueOf(task.getException()));
                            return;
                        }
                        for (DocumentSnapshot dc :
                                task.getResult().getDocuments()) {
                            if (dc.toObject(Product.class).getNameProduct().contains(s.toUpperCase()))
                                productList.add(dc.toObject(Product.class));
                        }
                        adapterRecSearch.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });

        if (progressDialog.isShowing())
            progressDialog.dismiss();

        recyclerViewSearch.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerViewSearch.setAdapter(adapterRecSearch);
    }

    @Override
    public void onItemClick(String view, int position) {
        Intent intent = new Intent(SearchPageActivity.this, ProductActivity.class);
        intent.putExtra("IdProduct", productList.get(position).getIdProduct());
        startActivity(intent);
    }
}