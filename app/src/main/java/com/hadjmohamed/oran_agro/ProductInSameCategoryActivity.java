package com.hadjmohamed.oran_agro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hadjmohamed.oran_agro.AdaptersAndHolder.AdapterRecSearch;

import java.util.ArrayList;
import java.util.List;

public class ProductInSameCategoryActivity extends AppCompatActivity implements RecViewInterface, View.OnClickListener {

    private RecyclerView recyclerView;
    private AdapterRecSearch adapterRecSearch;
    private List<Product> productList;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private ImageView btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in_same_category);

        // ToolBar
        toolbar = findViewById(R.id.toolbarBack);
        toolbar.setTitle(getIntent().getStringExtra("subCategoryName"));
        setSupportActionBar(toolbar);
        btnGoBack = findViewById(R.id.btnGoBack);

        btnGoBack.setOnClickListener(this);


        // Recycle view search
        recyclerView = findViewById(R.id.productRecProductInSameCategoryActivity);
        firestore = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();
        adapterRecSearch = new AdapterRecSearch(getApplicationContext(),
                productList, this);

        int s = getIntent().getIntExtra("idSubCategory", 0);
        uploadDataFirestore(s);
    }

    void uploadDataFirestore(int s) {
        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        productList.clear();

        firestore.collection("Products")
                .whereEqualTo("IDCategorie", s)
                .orderBy("idProduct", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Task Failed", String.valueOf(task.getException()));

                        }
                        for (DocumentSnapshot dc :
                                task.getResult().getDocuments()) {
                            productList.add(dc.toObject(Product.class));
                        }
                        adapterRecSearch.notifyDataSetChanged();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                    }
                });

        recyclerView.setLayoutManager(new
                LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapterRecSearch);
    }


    @Override
    public void onItemClick(String view, int position) {
        Intent intent = new Intent(ProductInSameCategoryActivity.this, ProductActivity.class);
        intent.putExtra("IdProduct", productList.get(position).getIdProduct());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view == btnGoBack)
            startActivity(new Intent(ProductInSameCategoryActivity.this, CategoryPageActivity.class));
    }
}