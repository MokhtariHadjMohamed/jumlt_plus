package com.hadjmohamed.oran_agro.AdminAndDelivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdapterRecCategory;
import com.hadjmohamed.oran_agro.AdapterRecTableProduct;
import com.hadjmohamed.oran_agro.Category;
import com.hadjmohamed.oran_agro.Product;
import com.hadjmohamed.oran_agro.R;
import com.hadjmohamed.oran_agro.RecViewInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WarehouseActivity extends AppCompatActivity implements View.OnClickListener, RecViewInterface {

    // TODO Element
    private CardView addProduct, home, all;
    private TextView categoryTextWarehouse;

    // TODO Category Dialog var
    private RecyclerView categoryRecView;
    private AdapterRecCategory adapterRecCategory;
    private List<Category> categoryList;
    private Dialog dialogCategory;
    private Button cancelBtnCategory;

    // TODO RecycleView Product var
    private RecyclerView recyclerView;
    private AdapterRecTableProduct adapterRecTableProduct;
    private List<Product> productList;
    private ProgressDialog progressDialog;

    // TODO Search var
    private String searchVariable;
    private SearchView searchView;
    // TODO Firebase
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);

        home = findViewById(R.id.homeBtn);
        addProduct = findViewById(R.id.addProductWarehouse);
        all = findViewById(R.id.categoryBtn);
        categoryTextWarehouse = findViewById(R.id.categoryTextWarehouse);

        home.setOnClickListener(this);
        all.setOnClickListener(this);
        addProduct.setOnClickListener(this);

        // TODO Firebase
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // TODO Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        // TODO Search
        searchView = findViewById(R.id.searchViewWarehouse);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchVariable = s.substring(0,1).toUpperCase() + s.substring(1);
                getSubCategory(searchVariable);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // TODO Category Dialog
        dialogCategory = new Dialog(this);
        dialogCategory.setCancelable(false);
        dialogCategory.setContentView(R.layout.category_dialog);
        dialogCategory.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        categoryList = new ArrayList<>();
        categoryRecView = dialogCategory.findViewById(R.id.categoryRecView);
        adapterRecCategory = new AdapterRecCategory(getApplicationContext()
                , categoryList, this);
        adapterRecCategory.setLayout(R.layout.grid_item_category);
        cancelBtnCategory = dialogCategory.findViewById(R.id.cancelBtnCategory);
        cancelBtnCategory.setOnClickListener(this);

        // TODO Table
        recyclerView = findViewById(R.id.recyclerViewProduct);
        productList = new ArrayList<>();
        adapterRecTableProduct = new AdapterRecTableProduct(getApplicationContext(),
                productList, this);
        getProduct();
    }

    private void getSubCategory() {
        categoryList.clear();
        categoryList.add(new Category("all", 0, "all"));
        firestore.collection("SubCategory")
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
                            categoryList.add(ds.toObject(Category.class));
                        adapterRecCategory.notifyDataSetChanged();
                    }
                });
        categoryRecView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryRecView.setAdapter(adapterRecCategory);
        progressDialog.dismiss();
    }

    private void getSubCategory(String searchVariable) {
        firestore.collection("SubCategory")
                .whereEqualTo("Name", searchVariable)
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
                            getProductSearch(searchVariable, ds.toObject(Category.class).getIdSubCategory());
                    }
                });
    }

    private void getProductSearch(String s, int IDCategorie) {
        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        productList.clear();

        firestore.collection("Products")
                .where(Filter.or(
                        Filter.equalTo("IDCategorie", IDCategorie),
                        Filter.equalTo("NameProduct", s)
                ))
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
                        adapterRecTableProduct.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });

        if (progressDialog.isShowing())
            progressDialog.dismiss();

        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableProduct);
    }

    private void getProduct() {
        productList.clear();
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
                                productList.add(dc.getDocument().toObject(Product.class));
                            }
                            adapterRecTableProduct.notifyDataSetChanged();
                        }
                    }
                });
        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecTableProduct);

        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        if (view == home){
            startActivity(new Intent(WarehouseActivity.this, HomePageAdminActivity.class));
            finish();
        } else if (view == addProduct) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentWarehouse, AddProductFragment.class, new Bundle())
                    .commit();
        } else if (view == all){
            Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
            progressDialog.show();
            getSubCategory();
            dialogCategory.show();
        }else if (view == cancelBtnCategory){
            dialogCategory.dismiss();
        }
    }

    @Override
    public void onItemClick(String view, int position) {
        if (Objects.equals(view, "Table")){
            Bundle bundle = new Bundle();
            bundle.putString("uid", productList.get(position).getIdProduct());
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentWarehouse, UpdateProductFragment.class, bundle)
                    .commit();
            Toast.makeText(WarehouseActivity.this, productList.get(position).getNameProduct(), Toast.LENGTH_SHORT).show();
        } else if (Objects.equals(view, "category")) {
            if (position == 0){
                getProduct();
            }else {
                getSubCategory(categoryList.get(position).getName());
                categoryTextWarehouse.setText(categoryList.get(position).getName());
                dialogCategory.dismiss();
            }
        }
    }
}